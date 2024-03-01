# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Compile and package the application to an executable JAR, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Setup the run environment
FROM openjdk:17-slim
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application's port
EXPOSE 8083

# Command to run the application
CMD ["java", "-jar", "app.jar"]
