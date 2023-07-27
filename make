#!/bin/bash
./compile

docker build -t account-stream-img:1.0 .
docker build -t account-stream-img:latest .

./gradlew clean
rm *.log

