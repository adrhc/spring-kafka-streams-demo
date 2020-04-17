#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

# tailf app.log | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN"
if [[ "$1" == "log" ]]; then
	./mvnw spring-boot:run -Dspring-boot.run.profiles=v2 -Dspring-boot.run.arguments=--logging.file.name=app.log
else
	./mvnw spring-boot:run -Dspring-boot.run.profiles=v2
fi
