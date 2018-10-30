FROM openjdk:8-jre-alpine

COPY app.jar /app.jar


# Define default command
CMD ["/usr/bin/java", "-jar", "-Dspring.profiles.active=dev", "/app.jar"]
