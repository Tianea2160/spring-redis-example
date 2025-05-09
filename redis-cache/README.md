# Redis Cache 모듈

이 모듈은 Spring Boot 애플리케이션에서 Redis를 사용하여 효율적인 데이터 캐싱을 구현하는 예제입니다.

## 개요

Redis Cache 모듈은 Article 엔티티에 대한 CRUD 작업에 캐싱 레이어를 적용하여 데이터베이스 조회를 최소화하고 애플리케이션 성능을 향상시키는 방법을 보여줍니다.

## 기술 스택

- Kotlin
- Spring Boot 3.4.5
- Spring Data Redis
- Spring Data JPA
- PostgreSQL
- Redis
- Docker Compose

## 주요 기능

1. **Redis 기반 캐싱**: 데이터 조회 결과를 Redis에 캐싱하여 반복적인 데이터베이스 접근을 방지합니다.
2. **엔티티 CRUD 작업**: Article 엔티티에 대한 생성, 조회, 수정, 삭제 기능을 제공합니다.
3. **캐시 관리**: `@Cacheable`, `@CachePut`, `@CacheEvict` 애노테이션을 활용한 효율적인 캐시 관리를 구현했습니다.
4. **캐시 TTL 설정**: 캐시 항목에 대한 만료 시간(TTL) 설정을 통해 데이터 신선도를 보장합니다.

## 프로젝트 구조

```
redis-cache/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── org/tianea/rediscache/
│   │   │       ├── config/
│   │   │       │   └── RedisConfig.kt          # Redis 설정
│   │   │       ├── controller/
│   │   │       │   └── ArticleController.kt    # API 엔드포인트
│   │   │       ├── entity/
│   │   │       │   └── Article.kt              # Article 엔티티
│   │   │       ├── repository/
│   │   │       │   └── ArticleRepository.kt    # 데이터 액세스 레이어
│   │   │       ├── service/
│   │   │       │   └── ArticleService.kt       # 비즈니스 로직 및 캐싱
│   │   │       └── RedisCacheApplication.kt    # 애플리케이션 진입점
│   │   └── resources/
│   │       └── application.yml                 # 애플리케이션 설정
│   └── test/
└── docker-compose.yml                          # Docker 설정
```

## Redis 캐싱 전략

이 모듈에서는 다음과 같은 캐싱 전략을 구현했습니다:

1. **읽기 캐싱**: `@Cacheable` 애노테이션을 사용하여 ID 기반 Article 조회 결과를 캐싱합니다.
2. **쓰기 캐싱**: `@CachePut` 애노테이션을 사용하여 Article 생성/수정 시 캐시를 갱신합니다.
3. **캐시 무효화**: `@CacheEvict` 애노테이션을 사용하여 Article 삭제 시 캐시를 무효화합니다.
4. **전역 캐시 비우기**: 모든 Article 캐시를 비우는 API 엔드포인트를 제공합니다.

## API 엔드포인트

| 메소드 | 경로 | 설명 |
|-------|------|------|
| GET | /api/articles | 모든 Article 조회 |
| GET | /api/articles/{id} | ID로 Article 조회 (캐싱 적용) |
| GET | /api/articles/published | 게시된 Article만 조회 |
| GET | /api/articles/author/{author} | 작성자별 Article 조회 |
| GET | /api/articles/search?title={title} | 제목 검색 |
| POST | /api/articles | 새 Article 생성 |
| PUT | /api/articles/{id} | Article 수정 |
| DELETE | /api/articles/{id} | Article 삭제 (캐시 무효화) |
| POST | /api/articles/clear-cache | 모든 Article 캐시 비우기 |

## 설치 및 실행

1. 프로젝트 클론:
   ```bash
   git clone https://github.com/yourusername/spring-redis-example.git
   cd spring-redis-example/redis-cache
   ```

2. Docker Compose로 Redis와 PostgreSQL 실행:
   ```bash
   docker-compose up -d
   ```

3. 애플리케이션 빌드 및 실행:
   ```bash
   ./gradlew bootRun
   ```

4. 애플리케이션 접속:
   - API 엔드포인트: http://localhost:8080/api/articles

## 캐시 설정

기본 캐시 설정은 다음과 같습니다:
- 기본 TTL: 10분
- 직렬화: JSON
- Null 값 캐싱: 비활성화

캐시 설정은 `RedisConfig.kt` 파일에서 수정할 수 있습니다.

## 성능 개선 사례

Article을 ID로 조회하는 경우:
- 첫 번째 요청: 데이터베이스 쿼리 실행 (느림)
- 이후 요청: Redis 캐시에서 결과 반환 (빠름)

콘솔 로그에서 "Fetching article from database" 메시지가 표시되지 않는 경우, 캐시에서 데이터를 가져오고 있음을 의미합니다.
