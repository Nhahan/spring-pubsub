FROM openjdk:17-jdk-slim

COPY mvc-module/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]
