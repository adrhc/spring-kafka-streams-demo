# Context
It's about a person having a bank account with 2 cards attached: his own and one for his wife. The client wants to be notified when a daily-expenses threshold is exceeded or one related to a period (e.g. 3 days, 1 month, etc).
# Features
- using Confluent, AVRO schema/protocol (see ksd1.avpr), Spring Boot, Kafka Streams, java 11
- using a custom timestamp extractor to fully control payment transactions time (see CustomTimestampExtractor)
- using joins, grouping, time windows (Tumbling and Hopping), aggregates, transformers
- *kafka extensions* (kafka sources are not modified):
    - extended Kafka DSL with a new operator for having *dynamic-grouping-windows* (see PeriodExceedsWithEnhancer)
    - extended Kafka DSL with a new operator equivalent to peek() and named tap() which also have access to headers and topic metadata  
- querying KTable stores from within the kafka streams topology without using a REST endpoints or other similar external approach (see PaymentsReport)
# Setup
```bash
export CONFLUENT_HOME="/home/adr/tools/confluent/confluent-5.5.0"
export PATH="$CONFLUENT_HOME/bin:$PATH"
confluent local start
```
see http://localhost:9021/clusters  
(disable browser cache)
# Scenario 1: 3 DAYS, kafka enhancer OFF
```bash
./delete-topics.sh
./run-v2.sh "--app.window-size=3 --app.window-unit=DAYS" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|Configuration:|spring profiles|app version|windowSize|windowUnit|enhancements"  
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh config,profiles | grep parameters
./create-report-command.sh daily,period | grep parameters
```
# Scenario 2: 1 MONTH, kafka enhancer OFF (not supported by Kafka)
```bash
./run-v2.sh "--app.window-size=1 --app.window-unit=MONTHS" | egrep -i "error|UnsupportedTemporalTypeException|Unit must not have an estimated duration"
```
# Scenario 3: 1 MONTH, kafka enhancer ON (using extended kafka DSL)
```bash
./delete-topics.sh
./run-v2.sh "--app.window-size=1 --app.window-unit=MONTHS --app.kafka-enhanced=true" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|Configuration:|spring profiles|app version|windowSize|windowUnit|enhancements" 
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh daily,period | grep parameters
```
# Scenario 4: 3 DAYS, kafka enhancer ON (using extended kafka DSL)
```bash
./delete-topics.sh
./run-v2.sh "--app.window-size=3 --app.window-unit=DAYS --app.kafka-enhanced=true" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|Configuration:|spring profiles|app version|windowSize|windowUnit|enhancements"  
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh daily,period | grep parameters
```
