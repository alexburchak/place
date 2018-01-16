#!/bin/sh

export PLACE_BACKEND_HOME=`dirname $0`/..
CLASSPATH=$PLACE_BACKEND_HOME/conf`find $PLACE_BACKEND_HOME/lib/ -name place-*.jar -exec echo -n ':'{} \;`

java -Djava.security.egd=file:/dev/urandom -classpath $CLASSPATH org.springframework.boot.loader.JarLauncher --spring.config.location=$PLACE_BACKEND_HOME/conf/application.yml --logging.config=$PLACE_BACKEND_HOME/conf/logback.xml
