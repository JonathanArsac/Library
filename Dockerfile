# Build app using maven
FROM maven:3.8-jdk-11-slim as maven
WORKDIR /app
COPY pom.xml pom.xml
RUN mvn dependency:go-offline -B
COPY src src
RUN mvn package && cp target/ivvq-library-*.jar app.jar

# package app in smaller jre only image
FROM openjdk:11-jre-slim as java
WORKDIR /app
COPY --from=maven /app/app.jar app.jar
EXPOSE 8080
CMD [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar" ]