FROM openjdk:17-jdk-slim

COPY webflux-module/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app.jar"]
