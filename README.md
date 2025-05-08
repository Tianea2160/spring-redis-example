# Redis High Availability Solutions

This repository contains implementations of two Redis high availability solutions using Docker and a distributed lock implementation:

1. **Redis Cluster** - A distributed implementation with sharding across multiple nodes
2. **Redis Sentinel** - A monitoring and automatic failover solution
3. **Redis Distributed Lock** - A distributed lock implementation using Redis and Redisson

All solutions are configured for easy deployment using Docker Compose and provide examples for integration with Java/Kotlin applications.

## Repository Structure

```
.
├── redis-cluster/          # Redis Cluster implementation
│   ├── docker-compose.yml  # Cluster Docker configuration
│   ├── redis-node-*.conf   # Configuration files for cluster nodes
│   └── README.md           # Detailed instructions for Redis Cluster
│
├── redis-sentinel/         # Redis Sentinel implementation
│   ├── docker-compose.yml  # Sentinel Docker configuration
│   ├── redis-master.conf   # Master configuration
│   ├── redis-slave.conf    # Slave configuration
│   ├── sentinel.conf       # Sentinel configuration
│   └── README.md           # Detailed instructions for Redis Sentinel
│
├── redis-distributed-lock/ # Redis Distributed Lock implementation
│   ├── src/                # Source code
│   ├── build.gradle.kts    # Gradle build file
│   └── README.md           # Detailed instructions for Redis Distributed Lock
│ 
└── docker-compose.yml      # Docker Compose for standalone Redis (for distributed lock)
```

## Overview of Solutions

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

| Feature | Redis Cluster | Redis Sentinel | Redis Distributed Lock |
|---------|---------------|----------------|------------------------|
| Primary purpose | Data sharding + HA | High availability | Distributed synchronization |
| Complexity | Higher | Lower | Medium |
| Data distribution | Automatic sharding | No sharding (full replicas) | N/A |
| Typical use case | Large datasets, high throughput | Smaller datasets, HA priority | Distributed application coordination |
| Node minimum | 6 (3 masters + 3 slaves) | 3 (1 master + 2 slaves) + sentinels | 1 (standalone Redis) |

## Getting Started with Distributed Lock

To use the Redis Distributed Lock module:

1. Start Redis using the provided Docker Compose file:

```bash
docker-compose up -d
```

This will start a Redis server on port 6379 and Redis Commander UI on port 8081.

2. Run the Spring Boot application with the distributed lock module:

```bash
cd redis-distributed-lock
./gradlew bootRun
```

3. Test the distributed lock API endpoints:

```bash
# Example 1: Basic distributed lock (THROW_EXCEPTION strategy)
curl "http://localhost:8080/api/users/123/process?data=testData"

# Example 2: Retry strategy
curl "http://localhost:8080/api/orders?userId=user1&itemId=item1&quantity=5"

# Example 3: Fallback strategy
curl "http://localhost:8080/api/reports/report1?userId=user1"

# Example 4: Skip strategy (for read-only operations)
curl "http://localhost:8080/api/statistics/stat1"
```

4. You can view Redis data using Redis Commander at http://localhost:8081

## Prerequisites

- Docker and Docker Compose installed
- JDK 21 or higher
- Gradle 8.x or higher

## License

This project is licensed under the MIT License - see the LICENSE file for details.
