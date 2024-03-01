# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app

# Copy the Maven project files and source code
COPY pom.xml .
COPY src ./src

# Compile and package the application, skipping tests to speed up the build
RUN mvn clean package -DskipTests

# Stage 2: Setup the runtime environment
FROM openjdk:17-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the service's port, adjust if your service uses a different port
EXPOSE 8084

# Command to run the application
CMD ["java", "-jar", "app.jar"]
