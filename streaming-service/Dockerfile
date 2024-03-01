# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app

# Copy your Maven project files
COPY src src
COPY pom.xml .

# Package your application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-slim
WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Command to start the Java application
CMD ["java", "-jar", "app.jar"]
