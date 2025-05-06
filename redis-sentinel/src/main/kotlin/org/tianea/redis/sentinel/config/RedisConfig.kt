package org.tianea.redis.sentinel.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfig {

    @Value("\${spring.redis.sentinel.master}")
    private lateinit var master: String

    @Value("\${spring.redis.sentinel.nodes}")
    private lateinit var sentinelNodes: String

    @Value("\${spring.redis.password}")
    private lateinit var password: String

    @Value("\${spring.redis.timeout}")
    private var timeout: Long = 5000

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val sentinelConfig = RedisSentinelConfiguration()

        sentinelConfig.master(master)
        sentinelConfig.setPassword(password)
        sentinelConfig
            .apply {
                sentinelNodes.split(",").forEach { node ->
                    val parts = node.split(":")
                    val host = parts[0]
                    val port = if (parts.size > 1) parts[1].toInt() else 26379
                    sentinel(host, port)
                }
            }

        val clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(timeout))
            .build()

        return LettuceConnectionFactory(sentinelConfig, clientConfig)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory()
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = StringRedisSerializer()
        return template
    }
}