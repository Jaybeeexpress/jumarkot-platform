FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY build/libs/identity-access-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
