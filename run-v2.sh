#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

LOG_ARG="--logging.file.name=app.log"
ARGS="$LOG_ARG $1"
PROFILES=${2:-v2}

echo "ARGS: $ARGS"
echo "PROFILES: $PROFILES"

# tailf app.log | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN"
rm -fv *.log
./mvnw -Dspring-boot.run.profiles="$PROFILES" -Dspring-boot.run.arguments="$ARGS" spring-boot:run
