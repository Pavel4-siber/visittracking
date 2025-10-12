# Spring Boot REST API Application

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1.0-green.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![swagger](https://img.shields.io/badge/-Swagger-85EA2D?style=flat&logo=swagger&logoColor=white)](https://swagger.io/)

## Описание

REST API приложение на Spring Boot для управления визитами пациентов. Приложение предоставляет возможность создавать, получать и управлять данными о визитах с проверкой на пересечение времени.

## Функциональность

- Создание новых визитов с проверкой на пересечение времени
- Получение списка визитов с пагинацией
- Фильтрация по пациентам и врачам
- Поддержка работы с разными временными зонами

## Требования

- Java 17+
- Maven 3.6+
- Docker и Docker Compose (для запуска через контейнер)

## Запуск приложения

### Локальный запуск

1. Склонируйте репозиторий:
```bash
git clone <repository-url>
cd <project-name>
```

### Запуск через Docker Compose (рекомендуется)
1. Убедитесь, что у вас установлен Docker и Docker Compose
2. Запустите приложение:
docker-compose up -d
3. Приложение будет доступно по адресу:
http://localhost:8080

### Конфигурация базы данных
Приложение поддерживает следующие переменные окружения для конфигурации MySQL:

|Переменная	| Описание	| По умолчанию
| --- | --- | --- |
|SPRING_DATASOURCE_URL	|URL подключения к MySQL	|jdbc:mysql://localhost:3306/appdb
|SPRING_DATASOURCE_USERNAME	|Имя пользователя БД	|appuser
|SPRING_DATASOURCE_PASSWORD	|Пароль БД	|apppassword
|SPRING_JPA_HIBERNATE_DDL_AUTO	|Режим автоматической генерации схемы	|update

### API Endpoints
#### Создание визита
POST /api/visits
Content-Type: application/json

{
    "patientId": 1,
    "doctorId": 1,
    "start": "2023-12-01T10:00:00+03:00",
    "end": "2023-12-01T11:00:00+03:00"
}
#### Получение списка визитов с пагинацией
GET /api/patients?page=0&size=10&sort=patient.lastName,asc

#### Параметры запроса:
```bash
page - номер страницы (по умолчанию 0)
size - размер страницы (по умолчанию 20)
sort - сортировка (например: patient.lastName,asc)
```
###  SWAGGER
```bash
OpenAPI JSON: http://localhost:8080/v3/api-docs
Swagger UI: http://localhost:8080/swagger-ui/index.html
```


### Структура проекта
```bash
src/
├── main/
│   ├── java/com/example/visittracking/
│   │   ├── config/         # Конфигурационные классы
│   │   ├── controller/     # REST контроллеры
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # Сущности базы данных
│   │   ├── exception/      # Обработка исключений
│   │   ├── repository/     # Репозитории JPA
│   │   ├── service/        # Бизнес-логика
│   └── resources/
│       ├── application.yml  # Конфигурация приложения
└── test/
    └── java/com/example/visittracking/       # Тесты
    └── resources/                            # Тест конфигурация приложения

```
