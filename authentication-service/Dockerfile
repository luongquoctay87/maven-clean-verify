FROM openjdk:11

ARG JAR_FILE=target/authentication-service.jar

ADD ${JAR_FILE} authentication-service.jar

EXPOSE 2631

ENTRYPOINT ["java","-jar","authentication-service.jar"]
