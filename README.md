# scenario 1: 3 DAYS, kafka enhancer OFF
```bash
./delete-topics.sh
./run-v2.sh "--app.window-size=3 --app.window-unit=DAYS" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|spring profiles|app version|windowSize|windowUnit|enhancements"  
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh config | grep parameters
./create-report-command.sh daily,period | grep parameters
```
# scenario 2: 1 MONTH, kafka enhancer OFF
```bash
./run-v2.sh "--app.window-size=1 --app.window-unit=MONTHS" | egrep -i "error|warn|Unit must not have an estimated duration"
```
# scenario 3: 1 MONTH, kafka enhancer ON
```bash
./delete-topics.sh
./run-v2.sh "--app.window-size=1 --app.window-unit=MONTHS --app.kafka-enhanced=true" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|spring profiles|app version|windowSize|windowUnit|enhancements" 
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh daily,period | grep parameters
```
# scenario 4: 3 DAYS, kafka enhancer ON
```bash
./delete-topics.sh
./run-v2.sh "--app.window-size=3 --app.window-unit=DAYS --app.kafka-enhanced=true" | egrep -i "client1|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|spring profiles|app version|windowSize|windowUnit|enhancements"  
./create-client-profile.sh | tee -a profile.log | egrep 'ClientProfile\(|client1'
./create-transactions.sh 1 | tee -a transactions.log | egrep 'Transaction\(|client1'
./create-report-command.sh daily,period | grep parameters
```
