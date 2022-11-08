FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/authentication-service.jar

RUN mkdir /opt/authentication-service

COPY ${JAR_FILE} /opt/authentication-service/authentication-service.jar

ENTRYPOINT ["java","-jar","/opt/authentication-service/authentication-service.jar"]
