#!/bin/sh

docker-compose -f kafka/docker-compose.yaml down
mvn clean install -f pub-service/
docker build -t briansjavablog/pub-service .
mvn clean install -f sub-service/
docker build -t briansjavablog/sub-service .
docker-compose -f kafka/docker-compose.yaml up