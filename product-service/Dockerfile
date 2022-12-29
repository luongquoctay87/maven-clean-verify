FROM openjdk:11

ARG JAR_FILE=target/product-service.jar

ADD ${JAR_FILE} product-service.jar

EXPOSE 2632

ENTRYPOINT ["java","-jar","product-service.jar"]
