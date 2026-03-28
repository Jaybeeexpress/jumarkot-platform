FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY build/libs/tenant-service.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
