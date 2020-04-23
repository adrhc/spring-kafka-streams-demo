# context
It's about a person having a bank account with 2 cards attached: his own and one for his wife. The client wants to be notified when a daily-expenses threshold is exceed or one set for a period (e.g. 3 days, 1 month, etc).
# features
- using Confluent, AVRO schema/protocol (see ksd1.avpr), Spring Boot, Kafka Streams
- using a custom timestamp extractor to fully control payment transactions time (see CustomTimestampExtractor)
- using grouping, time windows (Tumbling and Hopping), aggregates
- *kafka extensions*:
    - extended Kafka DSL with a new operator for having *dynamic-grouping-windows* (see PeriodExceedsWithEnhancer)
    - extended Kafka DSL with a new operator equivalent to peek() and named tap() which also have access to headers and topic metadata  
- querying KTable stores from within the kafka streams topology without using a REST endpoints or other similar external approach (see PaymentsReport)
# confluent
confluent local start
http://localhost:9021/clusters
# scenario 1: 3 DAYS, kafka enhancer OFF
```bash
./delete-topics.sh
./run-v2.sh "--app.window-size=3 --app.window-unit=DAYS" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|Configuration:|spring profiles|app version|windowSize|windowUnit|enhancements"  
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh config | grep parameters
./create-report-command.sh daily,period | grep parameters
```
# scenario 2: 1 MONTH, kafka enhancer OFF (not supported by Kafka)
```bash
./run-v2.sh "--app.window-size=1 --app.window-unit=MONTHS" | egrep -i "error|UnsupportedTemporalTypeException|Unit must not have an estimated duration"
```
# scenario 3: 1 MONTH, kafka enhancer ON
```bash
./delete-topics.sh
./run-v2.sh "--app.window-size=1 --app.window-unit=MONTHS --app.kafka-enhanced=true" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|Configuration:|spring profiles|app version|windowSize|windowUnit|enhancements" 
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh daily,period | grep parameters
```
# scenario 4: 3 DAYS, kafka enhancer ON
```bash
./delete-topics.sh
./run-v2.sh "--app.window-size=3 --app.window-unit=DAYS --app.kafka-enhanced=true" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|Configuration:|spring profiles|app version|windowSize|windowUnit|enhancements"  
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh daily,period | grep parameters
```
