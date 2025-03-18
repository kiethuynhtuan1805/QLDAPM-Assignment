FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar
ENV AWS_JAVA_V1_DISABLE_DEPRECATION_ANNOUNCEMENT=true
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]