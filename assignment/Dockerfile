# Use official OpenJDK 17 runtime (or 21 depending on your project)
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Give execution rights to mvnw
RUN chmod +x mvnw

# Resolve dependencies (layer caching)
# Note: running verify or dependency:go-offline might fail if tests depend on DB. 
# We'll skip it or do a simple clean package later.
# RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the application
# Expose port 8080
EXPOSE 8080

# Command to run the jar
# Find the created jar in target/ (usually *-SNAPSHOT.jar)
CMD ["java", "-jar", "target/assignment-0.0.1-SNAPSHOT.jar"]
