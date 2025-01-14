FROM amazoncorretto:17
EXPOSE 8081
WORKDIR /app
COPY build/libs/consumer.jar /app/consumer.jar
COPY .env /app/.env
ENTRYPOINT ["java", "-jar", "/app/consumer.jar"]