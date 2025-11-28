#FROM eclipse-temurin:21-jdk
#COPY target/*.jar /app.jar
#ENTRYPOINT ["java", "-jar", "app.jar"]
#ENV SPRING_PROFILES_ACTIVE=prod

FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
