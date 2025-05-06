package org.tianea.springredisexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisSentinelApplication

fun main(args: Array<String>) {
    runApplication<RedisSentinelApplication>(*args)
}
