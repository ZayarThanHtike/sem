FROM openjdk:24-jdk
COPY ./target/Lab1-0.1.0.3-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "Lab1-0.1.0.3-jar-with-dependencies.jar"]
