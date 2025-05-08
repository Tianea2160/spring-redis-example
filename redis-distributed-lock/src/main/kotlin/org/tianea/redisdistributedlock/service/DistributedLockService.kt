package org.tianea.redisdistributedlock.service

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class DistributedLockService(
    private val redissonClient: RedissonClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val threadLocal = ThreadLocal<RLock>()

    /**
     * 분산락 획득을 시도
     *
     * @param key 락을 획득할 키
     * @param waitTime 락 획득 시도 대기 시간
     * @param leaseTime 락 유지 시간
     * @param timeUnit 시간 단위
     * @return 락 획득 성공 여부
     */
    fun tryLock(key: String, waitTime: Long, leaseTime: Long, timeUnit: TimeUnit): Boolean {
        try {
            logger.debug("Get lock key: {}", key)
            val lock = redissonClient.getLock(key)
            threadLocal.set(lock)

            val isLocked = lock.tryLock(waitTime, leaseTime, timeUnit)

            if (isLocked) {
                logger.debug("Acquired lock for key: {}", key)
            } else {
                logger.debug("Failed to acquire lock for key: {}", key)
            }

            return isLocked
        } catch (e: Exception) {
            logger.error("Error acquiring lock for key: {}", key, e)
            return false
        }
    }

    /**
     * 분산락 해제
     *
     * @param key 락을 해제할 키
     * @return 락 해제 성공 여부
     */
    fun unlock(key: String): Boolean {
        try {
            val lock = threadLocal.get()

            if (lock == null) {
                logger.warn("No lock found in ThreadLocal for key: {}", key)
                return false
            }

            if (lock.isLocked && lock.isHeldByCurrentThread) {
                lock.unlock()
                threadLocal.remove()
                logger.debug("Released lock for key: {}", key)
                return true
            } else {
                logger.warn("Cannot unlock key: {}, lock not held by current thread", key)
                return false
            }
        } catch (e: Exception) {
            logger.error("Error releasing lock for key: {}", key, e)
            return false
        }
    }
}
