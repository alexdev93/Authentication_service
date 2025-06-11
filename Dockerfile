# ---------- Build Stage ----------
    FROM eclipse-temurin:17-jdk-jammy AS build

    # Set working directory
    WORKDIR /app
    
    # Copy project files
    COPY . .
    
    # Package the application
    RUN ./mvnw clean package -DskipTests
    
    # ---------- Runtime Stage ----------
    FROM eclipse-temurin:17-jre-jammy
    
    # Create app directory
    WORKDIR /app
    
    # Copy only the JAR from build stage
    COPY --from=build /app/target/myapp-0.0.1-SNAPSHOT.jar app.jar
    
    # Expose application port
    EXPOSE 8085
    
    # Run the app
    ENTRYPOINT ["java", "-jar", "app.jar"]
    