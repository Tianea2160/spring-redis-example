# Spring Redis Example with Redis Sentinel

A demonstration project showcasing high-availability Redis setup with Spring Boot and Kotlin, using Redis Sentinel for automatic failover.

## Overview

This project implements a Spring Boot application that connects to a Redis cluster configured with Redis Sentinel for high availability. The setup includes one Redis master, two Redis slave nodes, and three Sentinel instances to monitor the Redis cluster and perform automatic failover in case of master failure.

## Technologies

- Spring Boot 3.4.5
- Kotlin 1.9.25
- Spring Data Redis
- Redis 7.4 with Sentinel
- Lettuce Redis client
- Docker & Docker Compose
- Java 21

## Architecture

The project is structured as follows:

1. **Redis Setup**:
    - 1 Master node
    - 2 Slave (replica) nodes
    - 3 Sentinel nodes for monitoring and automatic failover

2. **Spring Boot Application**:
    - Connects to Redis using Spring Data Redis
    - Uses Lettuce as the Redis client
    - Configured to communicate with Redis through Sentinel

## Prerequisites

- JDK 21
- Docker and Docker Compose
- Kotlin

## Project Structure

```
spring-redis-example/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── org/tianea/springredisexample/
│   │   │       ├── config/
│   │   │       │   └── RedisConfig.kt
│   │   │       └── SpringRedisExampleApplication.kt
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── redis-server.conf
├── redis-sentinel.conf
├── docker-compose.yml
├── Dockerfile
└── build.gradle.kts
```

## Configuration

### Redis Server Configuration

The Redis server is configured with password authentication:

```
port 6379
requirepass test1234
masterauth test1234
```

### Redis Sentinel Configuration

The Sentinel configuration monitors the master node and is set up for automatic failover:

```
port 26379
dir "/tmp"
sentinel resolve-hostnames yes
sentinel monitor mymaster redis-master 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 60000
sentinel auth-pass mymaster test1234
sentinel known-replica mymaster redis-slave-1 6379
sentinel known-replica mymaster redis-slave-2 6379
```

### Spring Redis Connection Configuration

The Spring application is configured to connect to Redis through Sentinel:

```kotlin
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
                setPassword("test1234")
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
```

## Running the Application

1. **Start Redis containers**:
   ```bash
   docker-compose up -d
   ```
   This will start one master Redis node, two slave nodes, and three Sentinel nodes.

2. **Run the Spring Boot application**:
   ```bash
   ./gradlew bootRun
   ```

## Docker Compose Details

The Docker Compose setup creates a bridge network for all Redis-related containers with the following services:

- **redis-master**: The primary Redis node (port 6379)
- **redis-slave-1**: First replica node (port 6380)
- **redis-slave-2**: Second replica node (port 6381)
- **redis-sentinel-1**: First Sentinel node (port 5001)
- **redis-sentinel-2**: Second Sentinel node (port 5002)
- **redis-sentinel-3**: Third Sentinel node (port 5003)

## Testing Failover

To test the failover mechanism:

1. Connect to the Redis master:
   ```bash
   redis-cli -h 127.0.0.1 -p 6379 -a test1234
   ```

2. Shut down the master to simulate a failure:
   ```bash
   docker stop redis-master
   ```

3. Observe Sentinel logs to see the automatic failover:
   ```bash
   docker logs redis-sentinel-1
   ```

4. Verify that one of the slaves has been promoted to master.

## Troubleshooting

### Common Issues

1. **Connection Errors**:
    - Ensure all containers are running (`docker-compose ps`)
    - Verify that the network settings are correct
    - Check that the password is correctly set in all configurations

2. **Sentinel Authentication Issues**:
    - Verify that the `sentinel auth-pass` setting matches the Redis password
    - Ensure Sentinel can resolve hostnames with `sentinel resolve-hostnames yes`

3. **Network Issues**:
    - When using bridge network mode, services need to communicate by service name
    - Use `docker network inspect redis-net` to verify connectivity

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

tianea