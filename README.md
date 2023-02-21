# IVVQ Project : Library management in Spring Boot

Web service REST of a library providing search and book loaning.
## Requirements

For building and running the application you need:

- [JDK 17](https://www.oracle.com/fr/java/technologies/javase/jdk11-archive-downloads.html)
- [Maven 3](https://maven.apache.org)

## [DEV] Running the application in standelone mode (H2 embedded)

```shell
mvn spring-boot:run -P dev
```

## [PROD] Building and running the application with docker-compose (PostgreSQL db)

First, build docker image using :
```shell
docker-compose -f docker-compose.yml -f docker-compose-develop.yml build
```

Then, launch the application :
```shell
docker-compose -f docker-compose.yml -f docker-compose-develop.yml up -d
```

## Deploy app locally with docker-compose (PostgreSQL db)
```shell
./run.sh
```

## Jacoco
```
mvn clean test jacoco:report
```

## Deployments :
* VM [sonar](http://sonar.rn20-21.master-sdl.ovh:9000) by @RN20-21
* VM [pre-prod](http://app.rayhan-el.master-sdl.ovh/swagger-ui.html) by @Rayhan-El
* VM [prod](http://app.jonathanarsac.master-sdl.ovh/swagger-ui.html) by @JonathanArsac
