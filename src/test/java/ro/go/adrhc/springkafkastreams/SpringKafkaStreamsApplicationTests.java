package ro.go.adrhc.springkafkastreams;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import ro.go.adrhc.springkafkastreams.persons.Person;

@SpringBootTest(properties = {"spring.kafka.streams.auto-startup=false"})
class SpringKafkaStreamsApplicationTests {
	@Autowired
	private KafkaTemplate<String, Person> template;
	@Value("${topic.persons}")
	private String personsTopic;

	@Test
	void contextLoads() {
		template.send(personsTopic, "adr", new Person("adr", 34));
	}
}
