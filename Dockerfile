FROM openjdk:latest
COPY ./target/Lab1-1.0-SNAPSHOT-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "Lab1-1.0-SNAPSHOT-jar-with-dependencies.jar"]
