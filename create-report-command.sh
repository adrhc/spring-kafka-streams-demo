#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

./mvnw -DenableIT=true -DreportType=${1:-daily} -Dtest=ReportCmdProducerV2IT test
