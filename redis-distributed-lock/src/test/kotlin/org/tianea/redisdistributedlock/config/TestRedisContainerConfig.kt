package org.tianea.redisdistributedlock.config

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName


class TestRedisContainerConfig : BeforeAllCallback {
    companion object {
        const val REDIS_IMAGE: String = "redis:7.0.8-alpine"
        const val REDIS_PORT: Int = 6379
        private lateinit var redis: GenericContainer<*>
    }

    override fun beforeAll(context: ExtensionContext?) {
        redis = GenericContainer(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT);
        redis.start();
        System.setProperty("spring.data.redis.host", redis.host)
        System.setProperty("spring.data.redis.port", redis.getMappedPort(REDIS_PORT).toString())
    }
}