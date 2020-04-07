package ro.go.adrhc.springkafkastreams.send;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"v1", "v1test"})
@SpringBootTest
public class SendV1IT extends AbstractSend {}
