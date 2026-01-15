#Build the application using Maven
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

#Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

#Install fonts and font-management tools in the runtime image
USER root
RUN apt-get update && apt-get install -y \
    fontconfig \
    libfreetype6 \
    xfonts-utils \
    fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*
# ---------------------------------

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]