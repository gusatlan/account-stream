FROM ubuntu:latest
RUN apt update;apt -y install openjdk-17-jre && apt -y upgrade;apt -y autoclean;apt -y autoremove

EXPOSE 8080
WORKDIR /tmp
ADD /build/libs/account-stream-*.jar /tmp/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
