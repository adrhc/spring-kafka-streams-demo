package ro.go.adrhc.springkafkastreams.send;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"v2", "v2test"})
@SpringBootTest
public class SendV2IT extends AbstractSend {}
