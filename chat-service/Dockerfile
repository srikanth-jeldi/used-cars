FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/chat-service-1.0.0.jar app.jar

EXPOSE 7004

ENTRYPOINT ["java", "-jar", "app.jar"]
