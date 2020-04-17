#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

# KAFKA_HOME
cd /home/adr/tools/kafka/kafka_2.12-2.4.0

kdelete ksd1.client-profiles.v2
kdelete ksd1.daily-exceeds.v2
kdelete ksd1.daily-total-spent.v2
kdelete ksd1-dailyTotalSpentByClientId-1day-changelog
kdelete ksd1-dailyTotalSpentByClientId-3days-changelog
kdelete ksd1-dailyTotalSpentJoinClientProfile-repartition
kdelete ksd1-periodTotalSpentJoinClientProfile-repartition
kdelete ksd1-periodTotalSpentStore-changelog
kdelete ksd1.period-exceeds.v2
kdelete ksd1.period-total-spent.v2
kdelete ksd1.transactions.v2
