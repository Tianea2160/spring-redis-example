package org.tianea.redis.sentinel.config


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate

@SpringBootTest
class RedisTemplateTest {

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Test
    fun `RedisTemplate set and get test`() {
        // given
        val key = "test:key"
        val value = "Hello, Redis!"

        // when
        redisTemplate.opsForValue().set(key, value)
        val result = redisTemplate.opsForValue().get(key)

        // then
        assertEquals(value, result)
    }
}