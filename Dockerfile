FROM eclipse-temurin:17-jdk-alpine
COPY target/pipeline-0.0.1-SNAPSHOT.jar app.jar
COPY config.yml config.yml
ENTRYPOINT ["java","-jar","/app.jar"]
