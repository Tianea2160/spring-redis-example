# Redis Cluster Setup Guide

This project provides an easy way to set up a Redis Cluster with 6 nodes (3 masters + 3 slaves) using Docker Compose.

## Components

- **Redis Nodes**: 6 Redis server instances (ports 6379-6384)
  - 3 master nodes (redis-node-1, redis-node-2, redis-node-3)
  - 3 slave nodes (redis-node-4, redis-node-5, redis-node-6)
- **Cluster Initialization**: Automates Redis cluster creation and slave node attachment

## Getting Started

### Prerequisites

- Docker
- Docker Compose

### Installation and Setup

1. Clone the repository.
```bash
git clone <repository-url>
cd redis-cluster
```

2. Start the Redis cluster.
```bash
docker-compose up -d
```

3. Check the cluster initialization progress.
```bash
docker logs -f redis-cluster-init
```

4. Once the cluster is successfully configured, you can check its status with:
```bash
docker exec -it redis-node-1 redis-cli -a redisauth cluster nodes
docker exec -it redis-node-1 redis-cli -a redisauth cluster info
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

## Using with Applications

### Spring Boot (Kotlin)

application.properties:
```properties
spring.redis.cluster.nodes=redis-node-1:6379,redis-node-2:6380,redis-node-3:6381
spring.redis.password=redisauth
spring.redis.timeout=60000
```

### Configuration Class:
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

## Limitations

- The default configuration is for development and testing environments; additional security settings are needed for production
- Redis clusters require a minimum of 3 master nodes
- Each master node should have at least one slave for availability

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

## License

This project is distributed under the MIT License.
