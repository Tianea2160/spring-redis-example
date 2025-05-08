package org.tianea.redisdistributedlock.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Redisson 추가 구성을 위한 설정 클래스
 * application.yml의 redisson 설정을 기반으로 함
 */
@Configuration
class RedissonConfig {

    /**
     * Redisson 자동 구성을 커스터마이징
     * 이미 RedissonAutoConfiguration이 application.yml 설정을 로드함
     */
    @Bean
    fun redissonCustomizer(): RedissonAutoConfigurationCustomizer {
        return RedissonAutoConfigurationCustomizer { config ->
            // watchdog 설정 활성화 (락 자동 갱신)
            config.lockWatchdogTimeout = 30000 // 30초
            
            // 필요한 경우 추가 커스터마이징 가능
        }
    }
}
