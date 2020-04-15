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
./create-client-profile.sh | grep 'ClientProfile('
./create-transactions.sh | grep 'Transaction('
./create-transactions.sh 2 | grep 'Transaction('
```
