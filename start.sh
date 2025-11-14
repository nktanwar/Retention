#!/bin/bash

set -e

echo "Moving into backend directory..."
cd Retention/backend

echo "Building Spring Boot app..."
./gradlew build -x test

echo "Starting Spring Boot app..."
java -jar build/libs/*.jar






