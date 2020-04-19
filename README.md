# demo run order:
```bash
./delete-topics.sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=v2
./mvnw -Dtest=ClientProfileProducerV2IT test
./mvnw -Dtest=TransactionsProducerV2IT test
```
or
```bash
./delete-topics.sh

./run-v2.sh | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN|totals:"
./run-v2.sh "app.log" "v2,monthWindow" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN|totals:"
./run-v2.sh "app.log" "v2" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN|totals:"
./run-v2.sh - "v2,monthWindow" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN|totals:"
./run-v2.sh - "v2" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN|totals:"
tailf app4.log | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN|totals:"

./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'

./create-transactions.sh | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-transactions.sh 2 | tee -a transactions.log | egrep 'Transaction\(|client1'

./create-report-command.sh daily | grep parameters
./create-report-command.sh period | grep parameters
./create-report-command.sh config | grep parameters
./create-report-command.sh daily,period | grep parameters
```
# scenario 1
```bash
./delete-topics.sh
# egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN|totals:|spring profiles|app version|windowSize|windowUnit"
./run-v2.sh "--app.window-size=3 --app.window-unit=DAYS" 
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh daily,period | grep parameters
```
# scenario 2
```bash
./run-v2.sh "--app.window-size=1 --app.window-unit=MONTHS" | egrep -i "error|warn|Unit must not have an estimated duration"
```
# scenario 1
```bash
./delete-topics.sh
# egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN|totals:|spring profiles|app version|windowSize|windowUnit"
./run-v2.sh "--app.window-size=1 --app.window-unit=MONTHS --app.kafka.enhanced=true" 
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh daily,period | grep parameters
```
