FROM openjdk:11

ARG JAR_FILE=target/service-registry.jar

ADD ${JAR_FILE} service-registry.jar

EXPOSE 8761

ENTRYPOINT ["java","-jar","service-registry.jar"]
