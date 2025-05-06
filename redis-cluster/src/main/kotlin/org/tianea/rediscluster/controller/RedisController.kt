package org.tianea.rediscluster.controller

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/redis")
class RedisController(private val redisTemplate: RedisTemplate<String, Any>) {

    @GetMapping("/get/{key}")
    fun getValue(@PathVariable key: String): String? {
        return redisTemplate.opsForValue().get(key)?.toString()
    }

    @PostMapping("/set")
    fun setValue(@RequestParam key: String, @RequestParam value: String): String {
        redisTemplate.opsForValue().set(key, value)
        return "Value set successfully"
    }

    @GetMapping("/test")
    fun testConnection(): String {
        return try {
            val testKey = "test-connection"
            val testValue = "Connection Successful at ${java.time.LocalDateTime.now()}"
            redisTemplate.opsForValue().set(testKey, testValue)
            "Redis Cluster Connection Test: SUCCESS - Value set: $testValue"
        } catch (e: Exception) {
            "Redis Cluster Connection Test: FAILED - ${e.message}"
        }
    }
}
