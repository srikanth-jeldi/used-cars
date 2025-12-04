FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/car-listing-service-1.0.0.jar app.jar

EXPOSE 7002

ENTRYPOINT ["java", "-jar", "app.jar"]
