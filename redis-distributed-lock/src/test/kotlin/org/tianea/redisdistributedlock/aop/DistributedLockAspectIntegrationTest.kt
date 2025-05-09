package org.tianea.redisdistributedlock.aop

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.tianea.redisdistributedlock.exception.DistributedLockException
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

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
}


@Component
class TestService {
    private val logger = LoggerFactory.getLogger(TestService::class.java)

    @DistributedLock(key = "#param1", leaseTime = 1L, timeUnit = TimeUnit.SECONDS)
    fun test(param1: String) {
        logger.info("param1: $param1")
    }

    @DistributedLock(key = "#param1", leaseTime = 3L, waitTime = 0L, timeUnit = TimeUnit.SECONDS)
    fun testWithSleep(param1: String) {
        logger.info("test with sleep, param1: $param1")
        sleep(1000)
    }
}