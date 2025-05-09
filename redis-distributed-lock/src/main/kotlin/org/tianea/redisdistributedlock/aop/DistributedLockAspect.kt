package org.tianea.redisdistributedlock.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.tianea.redisdistributedlock.exception.DistributedLockException
import org.tianea.redisdistributedlock.service.DistributedLockService
import java.lang.reflect.Method

@Aspect
@Component
class DistributedLockAspect(
    private val distributedLockService: DistributedLockService,
    private val spelParser: SpelParser
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Around("@annotation(org.tianea.redisdistributedlock.aop.DistributedLock)")
    fun distributedLock(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val distributedLock = method.getAnnotation(DistributedLock::class.java)
            ?: throw IllegalArgumentException("No DistributedLock annotation found")

        // SpEL 을 사용하여 락 키 생성
        val lockKey = spelParser.parseKey(
            distributedLock.key,
            method,
            joinPoint.args,
            joinPoint.target,
            distributedLock.prefix
        )

        // 락 획득 실패 시 전략에 따라 처리
        return when (distributedLock.failureStrategy) {
            LockFailureStrategy.THROW_EXCEPTION -> executeWithLockOrThrow(joinPoint, lockKey, distributedLock)
            LockFailureStrategy.RETRY -> executeWithRetry(joinPoint, lockKey, distributedLock)
            LockFailureStrategy.EXECUTE_FALLBACK -> executeWithFallback(joinPoint, lockKey, distributedLock)
            LockFailureStrategy.SKIP -> executeWithSkip(joinPoint, lockKey, distributedLock)
        }
    }

    /**
     * 락 획득에 실패하면 예외를 발생시키는 전략
     */
    private fun executeWithLockOrThrow(
        joinPoint: ProceedingJoinPoint,
        lockKey: String,
        distributedLock: DistributedLock
    ): Any? {
        val isLockAcquired = distributedLockService.tryLock(
            lockKey,
            distributedLock.waitTime,
            distributedLock.leaseTime,
            distributedLock.timeUnit
        )

        if (!isLockAcquired) {
            throw DistributedLockException("Failed to acquire distributed lock for key: $lockKey")
        }

        logger.info("Acquired distributed lock for key: {}", lockKey)

        try {
            return joinPoint.proceed()
        } finally {
            unlockSafely(lockKey)
        }
    }

    /**
     * 락 획득에 실패하면 재시도하는 전략
     */
    private fun executeWithRetry(
        joinPoint: ProceedingJoinPoint,
        lockKey: String,
        distributedLock: DistributedLock
    ): Any? {
        var retryCount = 0
        var lastException: Exception? = null

        // 설정된 횟수만큼 재시도
        while (retryCount < distributedLock.maxRetries) {
            try {
                val isLockAcquired = distributedLockService.tryLock(
                    lockKey,
                    distributedLock.waitTime,
                    distributedLock.leaseTime,
                    distributedLock.timeUnit
                )

                if (isLockAcquired) {
                    logger.info("Acquired distributed lock for key: {} (retry: {})", lockKey, retryCount)
                    try {
                        return joinPoint.proceed()
                    } finally {
                        unlockSafely(lockKey)
                    }
                }
            } catch (e: Exception) {
                lastException = e
                logger.warn("Retry {} failed for key: {}", retryCount + 1, lockKey, e)
            }

            retryCount++
            // 재시도 간격을 지수적으로 증가 (지수 백오프)
            val sleepTime = (1000L * (1L shl retryCount)).coerceAtMost(10000L)
            Thread.sleep(sleepTime)
        }

        throw DistributedLockException(
            "Failed to acquire lock after ${distributedLock.maxRetries} retries for key: $lockKey",
            lastException
        )
    }

    /**
     * 락 획득에 실패하면 대체 메서드를 실행하는 전략
     */
    private fun executeWithFallback(
        joinPoint: ProceedingJoinPoint,
        lockKey: String,
        distributedLock: DistributedLock
    ): Any? {
        val isLockAcquired = distributedLockService.tryLock(
            lockKey,
            distributedLock.waitTime,
            distributedLock.leaseTime,
            distributedLock.timeUnit
        )

        if (isLockAcquired) {
            logger.info("Acquired distributed lock for key: {}", lockKey)
            try {
                return joinPoint.proceed()
            } finally {
                unlockSafely(lockKey)
            }
        }

        // 대체 메서드 실행
        val fallbackMethod = distributedLock.fallbackMethod
        if (fallbackMethod.isBlank()) {
            throw DistributedLockException(
                "Lock acquisition failed and no fallback method specified for key: $lockKey"
            )
        }

        logger.info("Executing fallback method: {} for key: {}", fallbackMethod, lockKey)
        return executeFallbackMethod(joinPoint, fallbackMethod)
    }

    /**
     * 락 획득에 실패해도 무시하고 진행하는 전략
     */
    private fun executeWithSkip(
        joinPoint: ProceedingJoinPoint,
        lockKey: String,
        distributedLock: DistributedLock
    ): Any? {
        val isLockAcquired = distributedLockService.tryLock(
            lockKey,
            distributedLock.waitTime,
            distributedLock.leaseTime,
            distributedLock.timeUnit
        )

        if (isLockAcquired) {
            logger.info("Acquired distributed lock for key: {}", lockKey)
            try {
                return joinPoint.proceed()
            } finally {
                unlockSafely(lockKey)
            }
        }

        // 락 획득 실패를 로그만 남기고 무시
        logger.warn(
            "Failed to acquire lock for key: {}, but proceeding anyway (reason: {})",
            lockKey, distributedLock.reason
        )
        return joinPoint.proceed()
    }

    /**
     * 대체 메서드 실행 로직
     */
    private fun executeFallbackMethod(joinPoint: ProceedingJoinPoint, fallbackMethodName: String): Any? {
        val target = joinPoint.target
        val args = joinPoint.args
        val methodSignature = joinPoint.signature as MethodSignature
        val returnType = methodSignature.returnType

        try {
            // 대체 메서드 찾기
            val fallbackMethod = findFallbackMethod(target, fallbackMethodName, args, returnType)
            return fallbackMethod.invoke(target, *args)
        } catch (e: Exception) {
            throw DistributedLockException("Failed to execute fallback method: $fallbackMethodName", e)
        }
    }

    /**
     * 대체 메서드 찾기
     */
    private fun findFallbackMethod(
        target: Any,
        fallbackMethodName: String,
        args: Array<Any>,
        returnType: Class<*>
    ): Method {
        val targetClass = target.javaClass
        val methods = targetClass.methods

        return methods.firstOrNull { method ->
            method.name == fallbackMethodName &&
                    method.parameterCount == args.size &&
                    method.returnType.isAssignableFrom(returnType) &&
                    areParametersCompatible(method.parameterTypes, args)
        } ?: throw NoSuchMethodException(
            "No suitable fallback method found: $fallbackMethodName with matching parameters"
        )
    }

    /**
     * 메서드 파라미터 타입 호환성 체크
     */
    private fun areParametersCompatible(paramTypes: Array<Class<*>>, args: Array<Any>): Boolean {
        if (paramTypes.size != args.size) {
            return false
        }

        for (i in paramTypes.indices) {
            val paramType = paramTypes[i]
            val arg = args[i]

            if (!paramType.isAssignableFrom(arg.javaClass)) {
                return false
            }
        }

        return true
    }

    /**
     * 안전하게 락 해제
     */
    private fun unlockSafely(lockKey: String) {
        try {
            val isUnlockSuccessful = distributedLockService.unlock(lockKey)
            if (isUnlockSuccessful) {
                logger.info("Released distributed lock for key: {}", lockKey)
            } else {
                logger.warn("Failed to release distributed lock for key: {}", lockKey)
            }
        } catch (e: Exception) {
            logger.error("Error while releasing distributed lock for key: {}", lockKey, e)
        }
    }
}
