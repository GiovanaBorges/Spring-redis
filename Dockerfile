FROM maven:3.9.8-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ---------- STAGE 2: runtime ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia apenas o jar do stage anterior
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
