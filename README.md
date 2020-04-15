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
./run-v2.sh | grep -P "client1|Notification:|Overdue:|Limit:"
tailf app.log | egrep "client1|Notification:|Overdue:|Limit:"./create-client-profile.sh | grep 'ClientProfile(' | tee -a profile.log
./create-transactions.sh | grep 'Transaction(' | tee -a transactions.log
./create-transactions.sh 2 | grep 'Transaction(' | tee -a transactions.log
```
