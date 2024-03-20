
FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} jira-1.0.jar
COPY resources ./resources
ENTRYPOINT ["java","-jar","/jira-1.0.jar"]

