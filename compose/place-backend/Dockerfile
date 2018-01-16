FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk-headless iproute2 netcat

COPY bin/place-backend.sh /usr/lib/place-backend/bin/place-backend.sh
RUN chmod a+rx /usr/lib/place-backend/bin/place-backend.sh

RUN mkdir /usr/lib/place-backend/logs

COPY conf/application.yml /usr/lib/place-backend/conf/application.yml
COPY conf/logback.xml /usr/lib/place-backend/conf/logback.xml

COPY place-common.jar /usr/lib/place-backend/lib/place-common.jar
COPY place-backend.jar /usr/lib/place-backend/lib/place-backend.jar

COPY docker-entrypoint.sh /

ENTRYPOINT /docker-entrypoint.sh
