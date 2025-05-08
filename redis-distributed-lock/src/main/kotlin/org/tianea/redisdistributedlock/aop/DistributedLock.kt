package org.tianea.redisdistributedlock.aop

import java.util.concurrent.TimeUnit

/**
 * 분산락 획득 실패 시 처리 전략
 */
enum class LockFailureStrategy {
    /**
     * 예외를 발생시킵니다 (기본값)
     */
    THROW_EXCEPTION,
    
    /**
     * 지정된 횟수만큼 재시도합니다
     */
    RETRY,
    
    /**
     * 대체 메서드를 실행합니다
     */
    EXECUTE_FALLBACK,
    
    /**
     * 락 획득 실패를 무시하고 메서드를 실행합니다
     * (데이터 일관성 문제가 없는 경우에만 사용)
     */
    SKIP
}

/**
 * 분산락을 적용하기 위한 어노테이션
 * 
 * @param key 락에 사용될 키 (SpEL 표현식 지원)
 * @param prefix 키 접두사
 * @param waitTime 락 획득을 위한 대기 시간
 * @param leaseTime 락 유지 시간
 * @param timeUnit 시간 단위
 * @param failureStrategy 락 획득 실패 시 처리 전략
 * @param maxRetries 재시도 최대 횟수 (RETRY 전략일 때만 사용)
 * @param fallbackMethod 대체 메서드 이름 (EXECUTE_FALLBACK 전략일 때만 사용)
 * @param reason 실패 전략 선택 이유 (문서화 목적)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val key: String,
    val prefix: String = "distributed-lock:",
    val waitTime: Long = 5L,
    val leaseTime: Long = 5L,
    val timeUnit: TimeUnit = TimeUnit.SECONDS,
    val failureStrategy: LockFailureStrategy = LockFailureStrategy.THROW_EXCEPTION,
    val maxRetries: Int = 3,
    val fallbackMethod: String = "",
    val reason: String = ""
)
