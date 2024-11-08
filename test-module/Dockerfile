FROM openjdk:17-jdk-slim

COPY test-module/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
