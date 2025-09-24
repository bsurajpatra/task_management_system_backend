# Multi-stage build: build with Maven, run on a lightweight JRE image
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# copy Maven files and download dependencies
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN mvn -B -q -DskipTests dependency:go-offline

# copy source and package
COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

# copy the built artifact (jar or war). The project packages as a WAR; copy any SNAPSHOT artifact.
COPY --from=build /workspace/target/*-SNAPSHOT.* /app/app.war

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.war"]
