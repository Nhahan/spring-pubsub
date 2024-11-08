#!/bin/bash

set -e

echo "Building project..."
./gradlew clean :mvc-module:bootJar :webflux-module:bootJar :test-module:bootJar

echo "Starting Docker Compose..."
docker-compose down
docker-compose up --build -d

echo "Docker containers are up and running:"
docker-compose ps
