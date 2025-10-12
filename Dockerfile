# Используем официальный образ OpenJDK 17
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем JAR-файл приложения
COPY target/*.jar app.jar

# Экспонируем порт, на котором будет работать приложение
EXPOSE 8080

# Создаем пользователя для запуска приложения (без root)
RUN addgroup --system spring && \
    adduser --system --group spring && \
    chown -R spring:spring /app

# Переключаемся на пользователя spring
USER spring:spring

# Команда запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

