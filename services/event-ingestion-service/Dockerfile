FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --gid 1001 appuser

COPY build/libs/*.jar app.jar

USER appuser

EXPOSE 8083

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
