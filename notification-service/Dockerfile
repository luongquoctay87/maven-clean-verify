FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/notification-service.jar

RUN mkdir /opt/notification-service

COPY ${JAR_FILE} /opt/notification-service/notification-service.jar

ENTRYPOINT ["java","-jar","/opt/notification-service/notification-service.jar"]
