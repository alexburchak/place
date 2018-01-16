#!/bin/bash

if [ "$1" = '' ]; then
    while ! nc -vz place-kafka1 9092; do
        echo Waiting for Kafka to be available
        sleep 1
    done

    sleep 30

    /usr/lib/place-backend/bin/place-backend.sh
fi

exec "$@"
