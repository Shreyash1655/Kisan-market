# 1. Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Run stage (Fixed: Using eclipse-temurin instead of openjdk)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copies the jar from the build stage /app/target folder
COPY --from=build /app/target/*.jar app.jar

# Matches your server.port=8082 in application.properties
EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]