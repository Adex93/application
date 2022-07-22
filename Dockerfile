FROM openjdk:17.0.1
ADD /target/application-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]