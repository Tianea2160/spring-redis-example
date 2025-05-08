package org.tianea.redisdistributedlock.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.tianea.redisdistributedlock.aop.DistributedLock
import org.tianea.redisdistributedlock.aop.LockFailureStrategy
import java.util.concurrent.TimeUnit

@Service
class SampleService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 분산락 테스트를 위한 메서드
     * 락 획득 실패 시 예외 발생 (기본 전략)
     */
    @DistributedLock(key = "#userId", prefix = "user-lock:")
    fun processUserData(userId: String, data: String): String {
        logger.info("Processing data for user: {}", userId)
        Thread.sleep(2000)
        logger.info("Finished processing data for user: {}", userId)
        return "Processed $data for user $userId"
    }

    /**
     * 락 획득 실패 시 재시도하는 전략
     */
    @DistributedLock(
        key = "#userId + '-' + #itemId", 
        prefix = "order-lock:", 
        failureStrategy = LockFailureStrategy.RETRY,
        maxRetries = 3
    )
    fun processOrder(userId: String, itemId: String, quantity: Int): String {
        logger.info("Processing order: user={}, item={}, quantity={}", userId, itemId, quantity)
        Thread.sleep(1500)
        logger.info("Finished processing order for user: {}, item: {}", userId, itemId)
        return "Processed order for user $userId, item $itemId, quantity $quantity"
    }

    /**
     * 락 획득 실패 시 대체 메서드를 실행하는 전략
     */
    @DistributedLock(
        key = "#reportId", 
        prefix = "report-lock:", 
        failureStrategy = LockFailureStrategy.EXECUTE_FALLBACK,
        fallbackMethod = "generateReportFallback"
    )
    fun generateReport(reportId: String, userId: String): String {
        logger.info("Generating report: {} for user: {}", reportId, userId)
        Thread.sleep(3000)
        logger.info("Finished generating report: {} for user: {}", reportId, userId)
        return "Generated full report $reportId for user $userId"
    }

    /**
     * generateReport의 대체 메서드
     */
    fun generateReportFallback(reportId: String, userId: String): String {
        logger.info("Generating simplified report: {} for user: {}", reportId, userId)
        Thread.sleep(1000)
        logger.info("Finished generating simplified report: {} for user: {}", reportId, userId)
        return "Generated simplified report $reportId for user $userId (fallback)"
    }

    /**
     * 락 획득 실패 시 무시하고 진행하는 전략 (읽기 전용 작업)
     */
    @DistributedLock(
        key = "#statId", 
        prefix = "stat-lock:", 
        waitTime = 1L,
        failureStrategy = LockFailureStrategy.SKIP,
        reason = "Read-only operation, safe to proceed without lock"
    )
    fun viewStatistics(statId: String): String {
        logger.info("Viewing statistics: {}", statId)
        Thread.sleep(1000)
        logger.info("Finished viewing statistics: {}", statId)
        return "Statistics for $statId"
    }
}
