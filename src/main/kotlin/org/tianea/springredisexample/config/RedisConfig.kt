package org.tianea.springredisexample.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfig {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {

        val config = RedisSentinelConfiguration()
            .apply {
                master("mymaster")
                sentinel("127.0.0.1", 5001)
                sentinel("127.0.0.1", 5002)
                sentinel("127.0.0.1", 5003)
                setPassword("test1234") // Redis 비밀번호 설정
            }
        return LettuceConnectionFactory(config)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = redisConnectionFactory()
        return template
    }
}