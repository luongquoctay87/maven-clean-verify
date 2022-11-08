FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/inventory-service.jar

RUN mkdir /opt/inventory-service

COPY ${JAR_FILE} /opt/inventory-service/inventory-service.jar

ENTRYPOINT ["java","-jar","/opt/inventory-service/inventory-service.jar"]
