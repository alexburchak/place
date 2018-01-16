FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk-headless iproute2 netcat

COPY bin/place-frontend.sh /usr/lib/place-frontend/bin/place-frontend.sh
RUN chmod a+rx /usr/lib/place-frontend/bin/place-frontend.sh

RUN mkdir /usr/lib/place-frontend/logs

COPY conf/application.yml /usr/lib/place-frontend/conf/application.yml
COPY conf/logback.xml /usr/lib/place-frontend/conf/logback.xml

COPY place-common.jar /usr/lib/place-backend/lib/place-common.jar
COPY place-frontend.jar /usr/lib/place-frontend/lib/place-frontend.jar

COPY docker-entrypoint.sh /

ENTRYPOINT /docker-entrypoint.sh

EXPOSE 8080
