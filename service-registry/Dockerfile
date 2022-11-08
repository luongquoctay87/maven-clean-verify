FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/service-registry.jar

RUN mkdir /opt/service-registry

COPY ${JAR_FILE} /opt/service-registry/service-registry.jar

ENTRYPOINT ["java","-jar","/opt/service-registry/service-registry.jar"]
