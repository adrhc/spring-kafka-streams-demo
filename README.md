# demo run order:
```bash
./delete-topics.sh
./mvnw spring-boot:run
./mvnw -Dtest=ClientProfileProducerV2IT test
./mvnw -Dtest=TransactionsProducerV2IT test
```
