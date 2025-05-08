package org.tianea.redisdistributedlock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisDistributedLockApplication

fun main(args: Array<String>) {
    runApplication<RedisDistributedLockApplication>(*args)
}
