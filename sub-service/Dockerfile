FROM adoptopenjdk/openjdk11
RUN mkdir -p /opt/app
RUN apt-get update
RUN apt-get install curl
COPY target/SubService-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-jar", "/opt/app/SubService-0.0.1-SNAPSHOT.jar"]
