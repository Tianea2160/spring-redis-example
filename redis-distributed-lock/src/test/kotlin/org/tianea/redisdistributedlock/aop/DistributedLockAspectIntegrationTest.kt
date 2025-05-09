package org.tianea.redisdistributedlock.aop

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.TimeUnit

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

}


@Component
class TestService {
    private val logger = LoggerFactory.getLogger(TestService::class.java)

    @DistributedLock(key = "#param1", leaseTime = 1L, timeUnit = TimeUnit.SECONDS)
    fun test(param1: String) {
        logger.info("param1: $param1")
    }
}