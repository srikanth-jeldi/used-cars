FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/notification-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 7003

ENTRYPOINT ["java", "-jar", "app.jar"]
