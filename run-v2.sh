#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

LOG_FILE=$1
PROFILES=${2:-v2}
# tailf app.log | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN"
if [[ "$LOG_FILE" == "" || "$LOG_FILE" == "-" ]]; then
	./mvnw spring-boot:run -Dspring-boot.run.profiles=$PROFILES
else
	./mvnw spring-boot:run -Dspring-boot.run.profiles=$PROFILES -Dspring-boot.run.arguments=--logging.file.name=$LOG_FILE
fi
