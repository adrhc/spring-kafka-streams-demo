#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

# KAFKA_HOME
cd /home/adr/tools/kafka/kafka_2.12-2.4.0

kdelete client-profiles.v2
kdelete daily-exceeds.v2
kdelete daily-total-spent.v2
kdelete kstreams1-dailyTotalSpentByClientId-1day-changelog
kdelete kstreams1-dailyTotalSpentByClientId-30days-changelog
kdelete kstreams1-dailyTotalSpentJoinClientProfile-repartition
kdelete kstreams1-periodTotalSpentJoinClientProfile-repartition
kdelete kstreams1-periodTotalSpentStore-changelog
kdelete period-exceeds.v2
kdelete period-total-spent.v2
kdelete transactions.v2
