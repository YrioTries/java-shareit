# Stage 1: Сборка проекта с использованием Maven
FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /app

# Копируем родительский POM и POM модуля
COPY ../pom.xml ./parent-pom.xml
COPY pom.xml ./pom.xml
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests -f pom.xml

# Stage 2: Запуск приложения
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Копируем только собранный .jar файл
COPY --from=builder /app/target/gateway-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
