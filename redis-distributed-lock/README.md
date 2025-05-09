# Redis Distributed Lock

This module provides a Spring Boot-based implementation of distributed locks using Redis and the Redisson library. It implements an efficient distributed lock mechanism through AOP (Aspect-Oriented Programming) for easy application in your services.

## Key Features

- **AOP-based distributed locks**: Apply locks to methods with simple annotations
- **Redisson integration**: Efficient lock implementation using pub/sub mechanism
- **SpEL support**: Dynamic lock keys using Spring Expression Language
- **Multiple failure strategies**: Configurable handling of lock acquisition failures
- **Thread safety**: ThreadLocal-based lock tracking

## Usage

### 1. Dependencies Configuration

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson:3.46.0")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    // Other dependencies...
}
```

### 2. Redis Configuration

```yaml
# application.yml
spring:
  application:
    name: redis-distributed-lock
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 3000
```

### 3. Applying Distributed Locks

```kotlin
// Basic usage (throws exception on failure)
@DistributedLock(key = "#userId", prefix = "user-lock:")
fun processUserData(userId: String, data: String): String {
    // Business logic
}

// Retry strategy
@DistributedLock(
    key = "#userId + '-' + #itemId", 
    prefix = "order-lock:", 
    failureStrategy = LockFailureStrategy.RETRY,
    maxRetries = 3
)
fun processOrder(userId: String, itemId: String, quantity: Int): String {
    // Business logic
}

// Fallback method strategy
@DistributedLock(
    key = "#reportId", 
    failureStrategy = LockFailureStrategy.EXECUTE_FALLBACK,
    fallbackMethod = "generateReportFallback"
)
fun generateReport(reportId: String, userId: String): String {
    // Business logic
}

// Fallback method
fun generateReportFallback(reportId: String, userId: String): String {
    // Simplified logic
}

// Skip strategy (for read-only operations)
@DistributedLock(
    key = "#statId", 
    failureStrategy = LockFailureStrategy.SKIP,
    reason = "Read-only operation, safe to proceed without lock"
)
fun viewStatistics(statId: String): String {
    // Read-only logic
}
```

## Annotation Options

The `@DistributedLock` annotation provides the following options:

| Option | Description | Default Value |
|--------|-------------|---------------|
| key | Lock key (supports SpEL expression) | (required) |
| prefix | Key prefix | "distributed-lock:" |
| waitTime | Lock acquisition wait time | 5 seconds |
| leaseTime | Lock lease time | 5 seconds |
| timeUnit | Time unit | TimeUnit.SECONDS |
| failureStrategy | Lock acquisition failure strategy | THROW_EXCEPTION |
| maxRetries | Maximum retry attempts | 3 |
| fallbackMethod | Fallback method name | "" |
| reason | Strategy selection reason (documentation) | "" |

## Failure Strategies

When a distributed lock cannot be acquired, you can choose one of the following strategies:

1. **THROW_EXCEPTION** (default): Throws an exception.
2. **RETRY**: Retries the lock acquisition a specified number of times (with exponential backoff).
3. **EXECUTE_FALLBACK**: Executes a specified fallback method.
4. **SKIP**: Ignores the lock acquisition failure and proceeds with the method execution (only for operations that don't require strict data consistency).

## Running with Docker

Run a Redis instance using the provided Docker Compose file:

```bash
cd redis-distributed-lock
docker-compose up -d
```

This will start a Redis server on port 6379.

## Testing the API Endpoints

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

## Important Notes

- **SKIP strategy caution**: Do not use the SKIP strategy for operations requiring data consistency.
- **Fallback method signature**: When using the EXECUTE_FALLBACK strategy, ensure the fallback method has the same parameters and return type as the original method.
- **Testing**: Thoroughly test in a distributed environment to validate lock behavior.

## Implementation Details

### Lock Service Implementation

The core lock functionality is implemented in `DistributedLockService`:

```kotlin
@Service
class DistributedLockService(
    private val redissonClient: RedissonClient
) {
    private val threadLocal = ThreadLocal<RLock>()

    fun tryLock(key: String, waitTime: Long, leaseTime: Long, timeUnit: TimeUnit): Boolean {
        // Implementation details...
    }

    fun unlock(key: String): Boolean {
        // Implementation details...
    }
}
```

### AOP Aspect Implementation

The AOP aspect that handles the `@DistributedLock` annotation is implemented in `DistributedLockAspect`:

```kotlin
@Aspect
@Component
class DistributedLockAspect(
    private val distributedLockService: DistributedLockService,
    private val spelParser: SpelParser
) {
    @Around("@annotation(org.tianea.redisdistributedlock.aop.DistributedLock)")
    fun distributedLock(joinPoint: ProceedingJoinPoint): Any? {
        // Implementation details...
    }
    
    // Helper methods...
}
```

## License

This project is licensed under the MIT License.