#!/bin/bash

if [ "$1" = '' ]; then
    while ! nc -vz place-kafka1 9092; do
        echo Waiting for Kafka to be available
        sleep 1
    done

    sleep 30

    /usr/lib/place-frontend/bin/place-frontend.sh
fi

exec "$@"
