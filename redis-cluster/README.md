# Redis Cluster Setup Guide

This project provides an easy way to set up a Redis Cluster with 6 nodes (3 masters + 3 slaves) using Docker Compose and Spring Boot integration.

## Components

- **Redis Nodes**: 6 Redis server instances (ports 6379-6384)
  - 3 master nodes (redis-node-1, redis-node-2, redis-node-3)
  - 3 slave nodes (redis-node-4, redis-node-5, redis-node-6)
- **Cluster Initialization**: Automates Redis cluster creation and slave node attachment
- **Spring Boot Application**: Provides a REST API to interact with the Redis Cluster

## Getting Started

### Prerequisites

- Docker
- Docker Compose
- JDK 21
- Gradle 8.x

### Installation and Setup

1. Start the Redis cluster using Docker Compose.
```bash
cd redis-cluster
docker-compose up -d
```

2. Check the cluster initialization progress.
```bash
docker logs -f redis-cluster-init
```

3. Once the cluster is successfully configured, you can check its status with:
```bash
docker exec -it redis-node-1 redis-cli -a redisauth cluster nodes
docker exec -it redis-node-1 redis-cli -a redisauth cluster info
```

4. Build and run the Spring Boot application.
```bash
./gradlew build
java -jar build/libs/redis-cluster-0.0.1-SNAPSHOT.jar
```

## Cluster Configuration Details

### Node Structure

| Container Name | Role | Port |
|--------------|------|------|
| redis-node-1 | Master | 6379 |
| redis-node-2 | Master | 6380 |
| redis-node-3 | Master | 6381 |
| redis-node-4 | Slave of redis-node-1 | 6382 |
| redis-node-5 | Slave of redis-node-2 | 6383 |
| redis-node-6 | Slave of redis-node-3 | 6384 |

### Cluster Initialization Process

1. Creates a cluster with 3 master nodes
2. Adds slave nodes to each master node

### Security

- All Redis nodes are protected with the password `redisauth`
- You must use the `-a redisauth` option when executing Redis cluster commands

## Testing the Application

You can use the following endpoints to test the Redis Cluster integration:

```bash
# Test connection to Redis Cluster
curl http://localhost:8080/api/redis/test

# Set a value
curl -X POST "http://localhost:8080/api/redis/set?key=mykey&value=myvalue"

# Get a value
curl http://localhost:8080/api/redis/get/mykey
```

## Spring Boot Integration Details

### Configuration Class

The application uses the following configuration to connect to the Redis Cluster:

```kotlin
@Configuration
class RedisConfig {

    @Value("\${spring.redis.cluster.nodes}")
    private lateinit var clusterNodes: List<String>

    @Value("\${spring.redis.password}")
    private lateinit var password: String

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val clusterConfig = RedisClusterConfiguration(clusterNodes)
        clusterConfig.setPassword(password)
        return LettuceConnectionFactory(clusterConfig)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory()
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        return template
    }
}
```

### Application Properties

The application connects to the Redis Cluster using the following properties:

```yaml
spring:
  application:
    name: redis-cluster
  redis:
    cluster:
      nodes: redis-node-1:6379,redis-node-2:6380,redis-node-3:6381,redis-node-4:6382,redis-node-5:6383,redis-node-6:6384
    password: redisauth
    timeout: 60000

server:
  port: 8080
```

## Cluster Management

### Checking Cluster Information
```bash
docker exec -it redis-node-1 redis-cli -a redisauth cluster info
```

### Listing Cluster Nodes
```bash
docker exec -it redis-node-1 redis-cli -a redisauth cluster nodes
```

### Testing Key Storage and Retrieval
```bash
docker exec -it redis-node-1 redis-cli -a redisauth -c set mykey "Hello Redis Cluster"
docker exec -it redis-node-1 redis-cli -a redisauth -c get mykey
```
(Note: The `-c` option enables key redirection in cluster mode)

### Stopping and Removing the Cluster
```bash
docker-compose down
```

To remove volumes as well:
```bash
docker-compose down -v
```

## Configuration Files Explained

### redis-node-x.conf
Configuration files for each Redis node, including:
- Port number
- Cluster mode activation
- Authentication settings
- Data persistence settings

### docker-compose.yml
- Defines 6 Redis nodes
- Sets up volumes and networking for each node
- Automates the cluster initialization process

## Troubleshooting

### Cluster Initialization Failure
Check the redis-cluster-init container logs:
```bash
docker logs redis-cluster-init
```

To retry initialization:
```bash
docker-compose down -v
docker-compose up -d
```

### Node Connection Issues
Verify that all nodes are on the same network:
```bash
docker network inspect redis-cluster_redis-net
```

## Limitations

- The default configuration is for development and testing environments; additional security settings are needed for production
- Redis clusters require a minimum of 3 master nodes
- Each master node should have at least one slave for availability

## License

This project is distributed under the MIT License.