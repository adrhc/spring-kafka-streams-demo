#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

./mvnw -DenableIT=true -DtransactionsCount=${1:-1} -Dtest=TransactionsProducerV2IT test
