#!/usr/bin/env sh

#echo "### Maven building ..."
#./mvnw clean install
#

echo "### Docker downloading & building ..."
docker-compose build

echo "### docker-compose down --remove-orphans"
docker-compose down --remove-orphans
echo "### docker-compose up -d"
docker-compose up -d