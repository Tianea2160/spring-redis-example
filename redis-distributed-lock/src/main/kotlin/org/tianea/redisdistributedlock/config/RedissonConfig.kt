package org.tianea.redisdistributedlock.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Redisson 구성을 위한 설정 클래스
 */
@Configuration
class RedissonConfig {

    @Value("\${spring.data.redis.host:localhost}")
    private lateinit var redisHost: String

    @Value("\${spring.data.redis.port:6379}")
    private var redisPort: Int = 0

    /**
     * RedissonClient 빈 생성
     */
    @Bean
    @Primary
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://$redisHost:$redisPort")
            .setConnectionMinimumIdleSize(5)
            .setConnectionPoolSize(10)
            .setConnectTimeout(3000)

        // 락 Watchdog 타임아웃 설정 (30초)
        // 락을 획득한 스레드가 살아있으면 자동으로 락을 갱신
        config.lockWatchdogTimeout = 30000

        return Redisson.create(config)
    }
}
