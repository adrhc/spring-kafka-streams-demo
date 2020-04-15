#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

# tailf app.log | egrep "client1|Notification:|Overdue:|Limit:"
./mvnw spring-boot:run -Dspring-boot.run.profiles=v2 -Dspring-boot.run.arguments=--logging.file.name=app.log
