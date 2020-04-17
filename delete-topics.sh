#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

# KAFKA_HOME
cd /home/adr/tools/kafka/kafka_2.12-2.4.0

kdelete ks1.client-profiles.v2
kdelete ks1.daily-exceeds.v2
kdelete ks1.daily-total-spent.v2
kdelete ks1-dailyTotalSpentByClientId-1day-changelog
kdelete ks1-dailyTotalSpentByClientId-3days-changelog
kdelete ks1-dailyTotalSpentJoinClientProfile-repartition
kdelete ks1-periodTotalSpentJoinClientProfile-repartition
kdelete ks1-periodTotalSpentStore-changelog
kdelete ks1.period-exceeds.v2
kdelete ks1.period-total-spent.v2
kdelete ks1.transactions.v2
