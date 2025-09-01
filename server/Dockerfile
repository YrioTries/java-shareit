FROM eclipse-temurin:21-jdk-jammy as builder

WORKDIR /app
COPY shareit-server/pom.xml .
COPY shareit-server/src ./src
COPY pom.xml .

# Собираем JAR
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -pl shareit-server -am -DskipTests

# Финальный образ
FROM eclipse-temurin:21-jre-jammy

WORKDIR /java-shareit
# Копируем собранный JAR из стадии builder
COPY --from=builder /java-shareit/server/target/*.jar app.jar

# Настройки
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]