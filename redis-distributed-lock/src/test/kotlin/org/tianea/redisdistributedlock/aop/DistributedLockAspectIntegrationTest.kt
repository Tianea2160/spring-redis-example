package org.tianea.redisdistributedlock.aop

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.tianea.redisdistributedlock.aop.LockFailureStrategy.EXECUTE_FALLBACK
import org.tianea.redisdistributedlock.exception.DistributedLockException
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.measureTime

@SpringBootTest
@ExtendWith(SpringExtension::class)
class DistributedLockAspectIntegrationTest
@Autowired constructor(
    private val testService: TestService,
) {

    @Test
    fun `executed distribute try lock`() {
        testService.test("hello world")
    }

    @Test
    fun `when other thread call current method, this thread can not call same method`() {
        thread {
            testService.testWithSleep("hello world")
        }

        sleep(100)

        assertThrows<DistributedLockException> {
            testService.testWithSleep("hello world")
        }
    }

    @Test
    fun `waiting time test`() {
        val measureTime = measureTime {
            thread { testService.testWithWaitingTime("hello world") }
            thread { testService.testWithWaitingTime("hello world") }
            thread { testService.testWithWaitingTime("hello world") }
            thread { testService.testWithWaitingTime("hello world") }
            thread { testService.testWithWaitingTime("hello world") }

            testService.testWithWaitingTime("hello world")
        }

        assertThat(measureTime > Duration.parse("PT5S")) //  5s
    }

    @Test
    fun `fallback method test`() {
        thread {
            testService.testWithFallBack("hello world")
        }
        sleep(100)

        testService.testWithFallBack("hello world")
    }

    @Test
    fun `exception method test`() {
        assertThrows<IllegalStateException> {
            testService.testWithException("hello world")
        }
    }
}


@Component
class TestService {
    private val logger = LoggerFactory.getLogger(TestService::class.java)

    @DistributedLock(key = "#param1", leaseTime = 1L, timeUnit = SECONDS)
    fun test(param1: String) {
        logger.info("param1: $param1")
    }

    @DistributedLock(key = "#param1", leaseTime = 3L, waitTime = 0L, timeUnit = SECONDS)
    fun testWithSleep(param1: String) {
        logger.info("test with sleep, param1: $param1")
        sleep(1000)
    }

    @DistributedLock(key = "#param1", leaseTime = 1L, waitTime = 1L, timeUnit = SECONDS)
    fun testWithWaitingTime(param1: String) {
        logger.info("test with waiting param1: $param1")
    }

    @DistributedLock(
        key = "#param1",
        waitTime = 0L,
        leaseTime = 2L,
        timeUnit = SECONDS,
        failureStrategy = EXECUTE_FALLBACK,
        fallbackMethod = "fallback"
    )
    fun testWithFallBack(param1: String) {
        logger.info("test with fallback param1: $param1")
        sleep(2000)
    }

    fun fallback(param1: String) {
        logger.info("fallback method: $param1")
    }

    @DistributedLock(key = "#param1")
    fun testWithException(param1: String) {
        logger.info("test with exception method: $param1")
        throw IllegalStateException("test with exception method: $param1")
    }
}