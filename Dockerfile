FROM openjdk:17-jdk-alpine
COPY ./target/usersystem-0.0.1-SNAPSHOT.jar usersystem-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/usersystem-0.0.1-SNAPSHOT.jar"]
