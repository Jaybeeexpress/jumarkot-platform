FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S jumarkot && adduser -S jumarkot -G jumarkot
WORKDIR /app
COPY build/libs/*.jar app.jar
USER jumarkot
EXPOSE 8082
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
