#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

./mvnw -DenableIT=true -Dtest=ClientProfileProducerV2IT test
