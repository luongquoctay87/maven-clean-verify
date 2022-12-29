FROM openjdk:11

ARG JAR_FILE=target/inventory-service.jar

ADD ${JAR_FILE} inventory-service.jar

EXPOSE 2633

ENTRYPOINT ["java","-jar","inventory-service.jar"]
