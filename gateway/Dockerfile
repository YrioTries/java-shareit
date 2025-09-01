# Stage 1: Сборка проекта
FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /app
# Копируем только необходимые файлы для сборки
COPY gateway/pom.xml ./pom.xml
COPY gateway/src ./src
# Собираем проект
RUN mvn clean package -DskipTests

# Stage 2: Запуск приложения
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Копируем только собранный .jar файл (замените gateway-0.0.1-SNAPSHOT.jar на актуальное имя вашего .jar)
COPY --from=builder /app/target/gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]