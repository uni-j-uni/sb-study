FROM eclipse-temurin:21-jdk

COPY build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=dev", "-jar", "app.jar"]