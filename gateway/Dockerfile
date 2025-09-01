FROM eclipse-temurin:21-jdk-jammy as builder

WORKDIR /java-shareit
COPY gateway/pom.xml .
COPY gateway/src ./src
COPY pom.xml .

# Собираем JAR
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -pl shareit-gateway -am -DskipTests

# Финальный образ
FROM eclipse-temurin:21-jre-jammy

WORKDIR /java-shareit
# Копируем собранный JAR из стадии builder
COPY --from=builder /java-shareit/gateway/target/*.jar app.jar

# Настройки
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]