FROM adoptopenjdk/openjdk11
RUN mkdir -p /opt/app
COPY target/PubService-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-jar", "/opt/app/PubService-0.0.1-SNAPSHOT.jar"]