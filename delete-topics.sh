#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

# KAFKA_HOME
cd /home/adr/tools/kafka/kafka_2.12-2.4.0

kdelete client-profiles.v2
kdelete daily-exceeds.v2
kdelete daily-total-spent.v2
kdelete kstreams1-dailyTotalSpentByClientId-changelog
kdelete kstreams1-dailyTotalSpentJoinClientProfile-repartition
kdelete transactions.v2
