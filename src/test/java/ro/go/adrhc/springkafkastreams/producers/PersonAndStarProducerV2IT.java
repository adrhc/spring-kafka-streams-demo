package ro.go.adrhc.springkafkastreams.producers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"v2", "test"})
@SpringBootTest
public class PersonAndStarProducerV2IT extends AbstractPersonAndStarProducer {}