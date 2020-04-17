#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

./mvnw -Dtest=ReportCmdProducerV2IT test
