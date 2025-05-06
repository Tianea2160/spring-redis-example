# Redis High Availability Solutions

This repository contains implementations of two Redis high availability solutions using Docker:

1. **Redis Cluster** - A distributed implementation with sharding across multiple nodes
2. **Redis Sentinel** - A monitoring and automatic failover solution

Both solutions are configured for easy deployment using Docker Compose and provide examples for integration with Java/Kotlin applications.

## Repository Structure

```
.
├── redis-cluster/          # Redis Cluster implementation
│   ├── docker-compose.yml  # Cluster Docker configuration
│   ├── redis-node-*.conf   # Configuration files for cluster nodes
│   └── README.md           # Detailed instructions for Redis Cluster
│
└── redis-sentinel/         # Redis Sentinel implementation
    ├── docker-compose.yml  # Sentinel Docker configuration
    ├── redis-master.conf   # Master configuration
    ├── redis-slave.conf    # Slave configuration
    ├── sentinel.conf       # Sentinel configuration
    └── README.md           # Detailed instructions for Redis Sentinel
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

## Choosing Between Cluster and Sentinel

| Feature | Redis Cluster | Redis Sentinel |
|---------|---------------|----------------|
| Primary purpose | Data sharding + HA | High availability |
| Complexity | Higher | Lower |
| Data distribution | Automatic sharding | No sharding (full replicas) |
| Typical use case | Large datasets, high throughput | Smaller datasets, HA priority |
| Node minimum | 6 (3 masters + 3 slaves) | 3 (1 master + 2 slaves) + sentinels |

- **Use Redis Cluster when**: You need to scale beyond the memory limits of a single Redis instance or need high throughput with data sharding.
- **Use Redis Sentinel when**: Your primary concern is high availability with automatic failover, and your dataset fits in a single Redis instance.

## Getting Started

Each solution has its own directory with detailed instructions. To get started:

1. Choose either Redis Cluster or Redis Sentinel based on your needs
2. Navigate to the respective directory
3. Follow the setup instructions in the README file

## Prerequisites

- Docker and Docker Compose installed
- Basic understanding of Redis concepts
- Familiarity with container orchestration

## License

This project is licensed under the MIT License - see the LICENSE file for details.
