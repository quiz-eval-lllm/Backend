# Gunakan base image resmi Java 21
FROM openjdk:21-jdk-slim

# Set working directory dalam container
WORKDIR /app

# Copy file JAR Spring Boot ke dalam container
COPY ./build/libs/llmbackend-0.0.1-SNAPSHOT.jar app.jar

# Expose port yang digunakan oleh aplikasi Spring Boot
EXPOSE 8080

# Jalankan aplikasi
ENTRYPOINT ["java", "-jar", "app.jar"]
