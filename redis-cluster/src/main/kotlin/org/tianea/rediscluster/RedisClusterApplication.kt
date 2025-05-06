package org.tianea.rediscluster

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisClusterApplication

fun main(args: Array<String>) {
    runApplication<RedisClusterApplication>(*args)
}
