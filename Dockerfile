FROM eclipse-temurin:21-jdk

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

COPY sb-study-config sb-study-config

ENTRYPOINT ["java", "-jar", "/app.jar"]