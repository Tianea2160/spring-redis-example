package org.tianea.rediscache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisCacheApplication

fun main(args: Array<String>) {
    runApplication<RedisCacheApplication>(*args)
}
