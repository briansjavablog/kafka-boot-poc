#!/bin/sh

cd kafka/
docker-compose down
docker-compose rm -f
cd ..
mvn clean install -f pub-service/
docker rmi $(docker images 'briansjavablog/pub-service' -a -q)
cd pub-service/
docker build --no-cache -t briansjavablog/pub-service .
cd ..
mvn clean install -f sub-service/
docker rmi $(docker images 'briansjavablog/sub-service' -a -q)
cd sub-service/
docker build --no-cache -t briansjavablog/sub-service .
cd ..
docker-compose -f kafka/docker-compose.yaml up --force-recreate