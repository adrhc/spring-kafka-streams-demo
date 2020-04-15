#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

./mvnw -DtransactionsCount=${1:-1} -Dtest=TransactionsProducerV2IT test
