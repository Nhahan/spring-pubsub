FROM openjdk:17-jdk-slim

COPY build/libs/webflux-module-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app.jar"]
