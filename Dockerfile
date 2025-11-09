FROM eclipse-temurin:21-jdk
COPY target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
ENV SPRING_PROFILES_ACTIVE=prod
