package org.tianea.springredisexample.controller

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/redis")
class RedisController(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    @GetMapping("/ping")
    fun ping(): String {
        return "Connected to Redis: ${redisTemplate.connectionFactory?.connection?.ping() ?: "Not connected"}"
    }

    @PostMapping("/set")
    fun setValue(@RequestParam key: String, @RequestParam value: String): String {
        redisTemplate.opsForValue().set(key, value)
        return "Value set successfully"
    }

    @GetMapping("/get")
    fun getValue(@RequestParam key: String): String {
        return redisTemplate.opsForValue().get(key)?.toString() ?: "Key not found"
    }
}