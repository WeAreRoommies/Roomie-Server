FROM amazoncorretto:17
EXPOSE 8080
WORKDIR /app
COPY build/libs/producer.jar /app/producer.jar
COPY .env /app/.env
ENTRYPOINT ["java", "-jar", "/app/producer.jar"]