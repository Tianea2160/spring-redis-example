package org.tianea.springredisexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringRedisExampleApplication

fun main(args: Array<String>) {
    runApplication<SpringRedisExampleApplication>(*args)
}
