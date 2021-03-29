# Getting Started
This service exposes a simple REST endpoint that takes messages and 
publishes them to a Kafka topic.

Build and run
 - ```mvn clean install```
 - ```mvn spring-boot:run```

Publish a message to Kafka topic
- ``` curl localhost:8080/api/publish/xxx```