package ro.go.adrhc.springkafkastreams.producers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"v1", "test"})
@SpringBootTest
public class PersonProducerV1IT extends AbstractPersonProducer {}
