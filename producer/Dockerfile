FROM amazoncorretto:17
EXPOSE 8080
WORKDIR /app
COPY build/libs/producer.jar /app/producer.jar
ENTRYPOINT ["java", "-jar", "/app/producer.jar"]