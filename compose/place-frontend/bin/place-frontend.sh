#!/bin/sh

export PLACE_FRONTEND_HOME=`dirname $0`/..
CLASSPATH=$PLACE_FRONTEND_HOME/conf`find $PLACE_FRONTEND_HOME/lib/ -name place-*.jar -exec echo -n ':'{} \;`

java -Djava.security.egd=file:/dev/urandom -classpath $CLASSPATH org.springframework.boot.loader.JarLauncher --spring.config.location=$PLACE_FRONTEND_HOME/conf/application.yml --logging.config=$PLACE_FRONTEND_HOME/conf/logback.xml
