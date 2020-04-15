#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

./mvnw spring-boot:run -Dspring-boot.run.profiles=v2
