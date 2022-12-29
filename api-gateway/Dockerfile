FROM openjdk:11

ARG JAR_FILE=target/api-gateway.jar

ADD ${JAR_FILE} api-gateway.jar

EXPOSE 2630

ENTRYPOINT ["java","-jar","api-gateway.jar"]
