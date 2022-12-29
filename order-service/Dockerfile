FROM openjdk:11

ARG JAR_FILE=target/order-service.jar

ADD ${JAR_FILE} order-service.jar

EXPOSE 2634

ENTRYPOINT ["java","-jar","order-service.jar"]
