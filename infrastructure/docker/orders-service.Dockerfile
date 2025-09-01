# === Етап 1: Збірка ===
# Використовуємо офіційний образ Maven, який містить JDK, для збірки проекту.
# Ми використовуємо Java 17 (eclipse-temurin - це якісна збірка OpenJDK).
FROM maven:3.9-eclipse-temurin-17 AS builder

# Встановлюємо робочу директорію
WORKDIR /app

# Копіюємо наш pom.xml для завантаження залежностей
COPY pom.xml .
RUN mvn dependency:go-offline

# Копіюємо решту вихідного коду
COPY src ./src

# Збираємо проект. mvn package створює готовий jar-файл.
# -DskipTests=true пропускає виконання тестів під час збірки образу.
RUN mvn package -DskipTests=true


# === Етап 2: Запуск ===
# Використовуємо офіційний образ Java 17 для запуску.
FROM eclipse-temurin:17-jre

# Встановлюємо робочу директорію
WORKDIR /app

# Копіюємо ТІЛЬКИ зібраний .jar файл зі стадії "builder".
# Шлях до файлу в образах Maven зазвичай такий.
COPY --from=builder /app/target/*.jar app.jar

# Відкриваємо порт 8081
EXPOSE 8081

# Команда для запуску Java-додатку.
ENTRYPOINT ["java", "-jar", "app.jar"]
