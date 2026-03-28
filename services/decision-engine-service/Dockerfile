FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S jumarkot && adduser -S -G jumarkot jumarkot

WORKDIR /app
COPY build/libs/*-SNAPSHOT.jar app.jar

RUN chown jumarkot:jumarkot app.jar
USER jumarkot

EXPOSE 8085
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
