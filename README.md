# Redis High Availability Solutions

This repository contains implementations of four Redis high availability, caching, and distributed patterns using Spring Boot, Kotlin, and Docker:

1. **Redis Cache** - A caching solution for improved application performance
2. **Redis Cluster** - A distributed implementation with sharding across multiple nodes
3. **Redis Sentinel** - A monitoring and automatic failover solution
4. **Redis Distributed Lock** - A distributed lock implementation using Redis and Redisson

All solutions are configured for easy deployment using Docker Compose and provide examples for integration with Spring Boot applications in Kotlin.

## Repository Structure

```
.
├── redis-cache/            # Redis Caching implementation
│   ├── docker-compose.yml  # Redis & PostgreSQL Docker configuration
│   ├── src/                # Source code with caching examples
│   └── README.md           # Detailed instructions for Redis Cache
│
├── redis-cluster/          # Redis Cluster implementation
│   ├── docker-compose.yml  # Cluster Docker configuration
│   ├── redis-node-*.conf   # Configuration files for cluster nodes
│   └── README.md           # Detailed instructions for Redis Cluster
│
├── redis-sentinel/         # Redis Sentinel implementation
│   ├── docker-compose.yml  # Sentinel Docker configuration
│   ├── redis-server.conf   # Master configuration
│   ├── redis-sentinel.conf # Sentinel configuration
│   └── README.md           # Detailed instructions for Redis Sentinel
│
├── redis-distributed-lock/ # Redis Distributed Lock implementation
│   ├── src/                # Source code
│   ├── build.gradle.kts    # Gradle build file
│   └── README.md           # Detailed instructions for Redis Distributed Lock
│ 
└── README.md               # This file
```

## Overview of Solutions

### Redis Cache

Redis Cache provides a simple yet powerful way to implement caching in Spring Boot applications. It offers:

- **Performance Improvement**: Reduce database load by caching frequently accessed data
- **Annotation-based Caching**: Easy-to-use Spring Cache annotations (@Cacheable, @CachePut, @CacheEvict)
- **Time-To-Live (TTL)**: Configurable expiration for cached data
- **Serialization Options**: JSON-based serialization for complex objects

Our implementation includes:
- Complete Article entity CRUD example with caching
- Integration with PostgreSQL for persistent storage
- Redis configuration with TTL settings
- Docker Compose setup for both Redis and PostgreSQL

[Visit Redis Cache directory](./redis-cache) for detailed instructions.

### Redis Cluster

Redis Cluster provides a way to run a Redis installation where data is **automatically sharded across multiple Redis nodes**. It offers:

- **Data Sharding**: Automatically distributes data across multiple nodes
- **High Availability**: Continues operations when some nodes fail
- **Horizontal Scalability**: Add more nodes to handle increased load

Our implementation includes:
- 3 master nodes + 3 slave nodes configuration
- Automatic cluster formation
- Configuration files and Docker Compose setup

[Visit Redis Cluster directory](./redis-cluster) for detailed instructions.

### Redis Sentinel

Redis Sentinel provides **high availability** through monitoring, notification, and automatic failover. It offers:

- **Monitoring**: Checks if master and slave instances are working correctly
- **Automatic Failover**: Promotes a slave to master when the master fails
- **Client Notifications**: Notifies clients about changes in the Redis topology

Our implementation includes:
- 1 master + 2 slave configuration
- 3 sentinel nodes for monitoring and failover
- Configuration files and Docker Compose setup

[Visit Redis Sentinel directory](./redis-sentinel) for detailed instructions.

### Redis Distributed Lock

The Redis Distributed Lock module provides a robust implementation of distributed locks using Redis and Redisson library, featuring:

- **AOP-based implementation**: Easy to use with annotations
- **Redisson integration**: Efficient distributed lock using pub/sub mechanism
- **SpEL support**: Dynamic lock keys using Spring Expression Language
- **Multiple failure strategies**: Configurable handling of lock acquisition failures
- **Thread safety**: Thread-local based lock tracking

Our implementation includes:
- Spring Boot AOP implementation
- Redisson-based lock service
- Various failure strategies for lock acquisition

[Visit Redis Distributed Lock directory](./redis-distributed-lock) for detailed instructions.

## Choosing Between Solutions

| Feature | Redis Cache | Redis Cluster | Redis Sentinel | Redis Distributed Lock |
|---------|-------------|---------------|----------------|------------------------|
| Primary purpose | Performance improvement | Data sharding + HA | High availability | Distributed synchronization |
| Complexity | Low | Higher | Lower | Medium |
| Data distribution | No sharding (single instance) | Automatic sharding | No sharding (full replicas) | N/A |
| Typical use case | Reducing DB load | Large datasets, high throughput | Smaller datasets, HA priority | Distributed application coordination |
| Node minimum | 1 (standalone Redis) | 6 (3 masters + 3 slaves) | 3 (1 master + 2 slaves) + sentinels | 1 (standalone Redis) |

## Prerequisites

- Docker and Docker Compose installed
- JDK 21 or higher
- Gradle 8.x or higher

## Technology Stack

- **Kotlin 1.9.25**
- **Spring Boot 3.4.5**
- **Spring Data Redis**
- **Spring Data JPA**
- **PostgreSQL**
- **Redisson 3.46.0**
- **Redis 7.x**
- **Docker & Docker Compose**

## License

This project is licensed under the MIT License - see the LICENSE file for details.