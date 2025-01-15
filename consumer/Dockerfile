FROM openjdk:17-jdk
EXPOSE 8081
WORKDIR /app
COPY build/libs/consumer.jar /app/consumer.jar
ENTRYPOINT ["java", "-jar", "/app/consumer.jar"]