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

## Getting Started

1. **Start Redis containers**:
   ```bash
   cd redis-sentinel
   docker-compose up -d
   ```
   This will start one master Redis node, two slave nodes, three Sentinel nodes, and the Spring Boot application.

2. **Verify that all services are running**:
   ```bash
   docker-compose ps
   ```

3. **Test the application**:
   ```bash
   # Check Redis connection
   curl http://localhost:8080/redis/ping
   
   # Set a key-value pair
   curl -X POST "http://localhost:8080/redis/set?key=test&value=hello"
   
   # Get the value for a key
   curl "http://localhost:8080/redis/get?key=test"
   ```

## Configuration

### Redis Server Configuration (redis-server.conf)

The Redis server is configured with password authentication:

```
port 6379
requirepass test1234
masterauth test1234
```

### Redis Sentinel Configuration (redis-sentinel.conf)

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
protected-mode no
```

### Application Configuration (application.yml)

The Spring Boot application configuration includes Redis Sentinel settings:

```yaml
spring:
  application:
    name: spring-redis-example
  redis:
    timeout: 5000
    password: test1234
    sentinel:
      master: mymaster
      nodes: redis-sentinel-1:26379,redis-sentinel-2:26379,redis-sentinel-3:26379
    
server:
  port: 8080
  
logging:
  level:
    org.springframework.data.redis: DEBUG
    io.lettuce.core: DEBUG
```

## Testing Failover

To test the Redis Sentinel failover mechanism:

1. Connect to the Redis master:
   ```bash
   redis-cli -h 127.0.0.1 -p 6379 -a test1234
   ```

2. Set some data:
   ```bash
   SET testkey "testvalue"
   ```

3. Shut down the master to simulate a failure:
   ```bash
   docker stop redis-master
   ```

4. Observe Sentinel logs to see the automatic failover:
   ```bash
   docker logs redis-sentinel-1
   ```

5. Check if one of the slaves has been promoted to master:
   ```bash
   redis-cli -h 127.0.0.1 -p 6380 -a test1234 INFO replication
   redis-cli -h 127.0.0.1 -p 6381 -a test1234 INFO replication
   ```

6. Verify that your data is still accessible through the application:
   ```bash
   curl "http://localhost:8080/redis/get?key=testkey"
   ```

## Key Implementation Notes

1. **Bridge Network**: The project uses Docker's bridge network type to isolate containers while allowing them to communicate using service names.

2. **Sentinel Configuration**: The `resolve-hostnames` option is enabled to allow Sentinel to resolve container names to IP addresses.

3. **Health Checks**: Docker health checks ensure that dependencies are only started once the required services are healthy.

4. **Application Configuration**: Redis connection settings are externalized in the `application.yml` file, making it easy to modify without changing code.

5. **Timeout Settings**: Connection timeout is configured to handle network latency or temporary connectivity issues.

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

4. **Application Failed to Connect**:
   - Check application logs: `docker logs app-server`
   - Verify that Sentinel nodes are correctly specified in application.yml

## License

This project is licensed under the MIT License - see the LICENSE file for details.