FROM openjdk:11

ARG JAR_FILE=target/notification-service.jar

ADD ${JAR_FILE} notification-service.jar

EXPOSE 2635

ENTRYPOINT ["java","-jar","notification-service.jar"]
