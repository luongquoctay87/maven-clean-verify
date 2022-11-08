FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/api-gateway.jar

RUN mkdir /opt/api-gateway

COPY ${JAR_FILE} /opt/api-gateway/api-gateway.jar

ENTRYPOINT ["java","-jar","/opt/api-gateway/api-gateway.jar"]
