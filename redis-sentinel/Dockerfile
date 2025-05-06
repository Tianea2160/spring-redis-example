FROM openjdk:21-jdk-slim AS build

WORKDIR /build

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew dependencies
RUN ./gradlew build -x test

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /build/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]