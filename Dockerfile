FROM openjdk:8-jdk-alpine
LABEL maintainer = "ie-denisov"
EXPOSE 8080
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} booking_system.jar
ENTRYPOINT ["java", "-jar", "booking_system.jar"]