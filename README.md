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
tailf app.log | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN|totals:"

./create-client-profile.sh | egrep 'ClientProfile\(|client1' | tee -a profile.log

./create-transactions.sh | egrep 'Transaction\(|client1' | tee -a transactions.log
./create-transactions.sh 2 | egrep 'Transaction\(|client1' | tee -a transactions.log

./create-report-command.sh | egrep "parameters"
./create-report-command.sh period | egrep "parameters"
./create-report-command.sh daily,period | egrep "parameters"
```
