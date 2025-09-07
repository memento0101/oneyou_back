# 최적화된 Multi-stage Dockerfile for Spring Boot
# Dev 환경 배포용

###################
# Build Stage
###################
FROM amazoncorretto:21 as build

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 캐싱을 위해 빌드 파일들을 먼저 복사
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle

# gradlew에 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (이 레이어는 build.gradle이 변경되지 않는 한 캐시됨)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 및 이미 생성된 JOOQ 코드 복사
COPY src ./src
COPY build/generated-sources ./build/generated-sources

# 애플리케이션 빌드 (테스트 및 JOOQ 생성 제외)
RUN ./gradlew build -x test -x generateJooq --no-daemon

###################
# Runtime Stage
###################
FROM amazoncorretto:21

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출 (Spring Boot 기본 포트)
EXPOSE 8080

# dev 환경으로 기본 프로필 설정
ENV SPRING_PROFILES_ACTIVE=dev

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]