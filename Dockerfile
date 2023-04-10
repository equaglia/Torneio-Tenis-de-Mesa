FROM openjdk:17-jdk-alpine

COPY target/torneio-tm-api-0.0.1-SNAPSHOT.jar torneio-tm-1.0.0.jar

ENTRYPOINT [ "java", "-jar", "torneio-tm-1.0.0.jar" ]
