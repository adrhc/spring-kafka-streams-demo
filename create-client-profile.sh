#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

./mvnw -Dtest=ClientProfileProducerV2IT test
