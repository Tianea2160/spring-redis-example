# Redis Distributed Lock

이 모듈은 Spring Boot 기반의 Redis 분산락(Distributed Lock) 구현을 제공합니다. Redisson 라이브러리를 활용하여 효율적인 분산락 메커니즘을 구현했으며, AOP(Aspect-Oriented Programming)를 통해 쉽게 사용할 수 있습니다.

## 주요 기능

- **AOP 기반 분산락**: 메서드에 어노테이션만 추가하여 분산락 적용
- **Redisson 통합**: pub/sub 메커니즘을 활용한 효율적인 락 관리
- **SpEL 지원**: Spring Expression Language를 사용한 동적 락 키 생성
- **다양한 실패 처리 전략**: 락 획득 실패 시 다양한 처리 방법 제공
- **스레드 안전성**: ThreadLocal 기반의 락 추적

## 사용 방법

### 1. 의존성 설정

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.26.2")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    // 기타 의존성...
}
```

### 2. Redis 설정

```yaml
# application.yml
spring:
  data:
    redis:
      host: localhost
      port: 6379

redisson:
  singleServerConfig:
    address: redis://${spring.data.redis.host}:${spring.data.redis.port}
    connectionMinimumIdleSize: 5
    connectionPoolSize: 10
```

### 3. 분산락 적용

```kotlin
// 기본 사용법 (실패 시 예외 발생)
@DistributedLock(key = "#userId", prefix = "user-lock:")
fun processUserData(userId: String, data: String): String {
    // 비즈니스 로직
}

// 실패 시 재시도 전략
@DistributedLock(
    key = "#userId + '-' + #itemId", 
    prefix = "order-lock:", 
    failureStrategy = LockFailureStrategy.RETRY,
    maxRetries = 3
)
fun processOrder(userId: String, itemId: String, quantity: Int): String {
    // 비즈니스 로직
}

// 실패 시 대체 메서드 실행 전략
@DistributedLock(
    key = "#reportId", 
    failureStrategy = LockFailureStrategy.EXECUTE_FALLBACK,
    fallbackMethod = "generateReportFallback"
)
fun generateReport(reportId: String, userId: String): String {
    // 비즈니스 로직
}

// 대체 메서드
fun generateReportFallback(reportId: String, userId: String): String {
    // 간소화된 로직
}

// 실패 시 무시하고 진행 전략 (읽기 전용 작업)
@DistributedLock(
    key = "#statId", 
    failureStrategy = LockFailureStrategy.SKIP,
    reason = "Read-only operation, safe to proceed without lock"
)
fun viewStatistics(statId: String): String {
    // 읽기 전용 로직
}
```

## 어노테이션 옵션

`@DistributedLock` 어노테이션은 다음과 같은 옵션을 제공합니다:

| 옵션 | 설명 | 기본값 |
|------|------|--------|
| key | 락 키 (SpEL 표현식 지원) | (필수) |
| prefix | 키 접두사 | "distributed-lock:" |
| waitTime | 락 획득 대기 시간 | 5초 |
| leaseTime | 락 유지 시간 | 5초 |
| timeUnit | 시간 단위 | TimeUnit.SECONDS |
| failureStrategy | 락 획득 실패 처리 전략 | THROW_EXCEPTION |
| maxRetries | 재시도 최대 횟수 | 3 |
| fallbackMethod | 대체 메서드 이름 | "" |
| reason | 전략 선택 이유 (문서화) | "" |

## 실패 처리 전략

분산락 획득에 실패했을 때 다음 전략 중 하나를 선택할 수 있습니다:

1. **THROW_EXCEPTION** (기본값): 예외를 발생시킵니다.
2. **RETRY**: 지정된 횟수만큼 재시도합니다 (지수 백오프 적용).
3. **EXECUTE_FALLBACK**: 지정된 대체 메서드를 실행합니다.
4. **SKIP**: 락 획득 실패를 무시하고 메서드를 실행합니다 (데이터 일관성 문제가 없는 경우에만 사용).

## 주의사항

- **SKIP 전략 사용 시 주의**: 데이터 일관성이 중요한 작업에는 SKIP 전략을 사용하지 마십시오.
- **대체 메서드 시그니처**: `EXECUTE_FALLBACK` 전략 사용 시, 대체 메서드는 원본 메서드와 동일한 파라미터와 반환 타입을 가져야 합니다.
- **테스트 환경**: 분산 환경에서 충분히 테스트하여 락 동작을 검증하십시오.

## Redis 컨테이너 실행

프로젝트 루트 디렉토리의 `docker-compose.yml`을 사용하여 Redis 컨테이너를 실행할 수 있습니다:

```bash
docker-compose up -d
```

이 명령은 Redis 서버(포트 6379)와 Redis Commander UI(포트 8081)를 시작합니다.

## API 엔드포인트 테스트

```bash
# 예제 1: 기본 분산락 (THROW_EXCEPTION 전략)
curl "http://localhost:8080/api/users/123/process?data=testData"

# 예제 2: 재시도 전략
curl "http://localhost:8080/api/orders?userId=user1&itemId=item1&quantity=5"

# 예제 3: 대체 메서드 전략
curl "http://localhost:8080/api/reports/report1?userId=user1"

# 예제 4: 무시 전략 (읽기 전용 작업)
curl "http://localhost:8080/api/statistics/stat1"
```

## 심화 학습

- Redisson: https://github.com/redisson/redisson
- Spring AOP: https://docs.spring.io/spring-framework/reference/core/aop.html
- Redis 분산락 관련 논문: http://redis.io/topics/distlock
