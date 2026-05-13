# ========================= BUILD STAGE =========================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ========================= RUNTIME STAGE =========================
FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache curl

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copier le JAR généré
COPY --from=builder /app/target/*.jar app.jar

RUN chown -R spring:spring /app

USER spring:spring

EXPOSE 8087

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
    CMD curl -f http://localhost:8087/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]