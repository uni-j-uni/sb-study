FROM openjdk:21-jdk-alpine

WORKDIR /app

COPY sb-study-config/application-dev.properties sb-study-config/application-dev.properties

COPY build/libs/app.jar app.jar

ENTRYPOINT ["java", "-Dspring.config.import=optional:file:sb-study-config/application-dev.properties", "-jar", "app.jar"]