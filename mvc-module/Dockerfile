FROM openjdk:17-jdk-slim

COPY build/libs/mvc-module.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]
