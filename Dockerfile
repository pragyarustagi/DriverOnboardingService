FROM openjdk:8-jdk-alpine

WORKDIR /app

COPY target/springboot-mongo-docker.jar /app/
EXPOSE 8080

CMD ["java", "-jar", "/app/springboot-mongo-docker.jar"]
