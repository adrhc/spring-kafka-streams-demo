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
./run-v2.sh | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN"
tailf app.log | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR|WARN"
./create-client-profile.sh | egrep 'ClientProfile\(|client1' | tee -a profile.log
./create-transactions.sh | grep 'Transaction(' | tee -a transactions.log
./create-transactions.sh 2 | grep 'Transaction(' | tee -a transactions.log
```
