#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

# KAFKA_HOME
cd /home/adr/tools/kafka/kafka_2.12-2.4.0

kdelete client-profiles.v2
kdelete daily-exceeds.v2
kdelete daily-total-spent.v2
kdelete adrks1-dailyTotalSpentByClientId-1day-changelog
kdelete adrks1-dailyTotalSpentByClientId-3days-changelog
kdelete adrks1-dailyTotalSpentJoinClientProfile-repartition
kdelete adrks1-periodTotalSpentJoinClientProfile-repartition
kdelete adrks1-periodTotalSpentStore-changelog
kdelete period-exceeds.v2
kdelete period-total-spent.v2
kdelete transactions.v2
