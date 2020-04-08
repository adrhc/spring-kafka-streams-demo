package ro.go.adrhc.springkafkastreams.transformers.debug;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import ro.go.adrhc.springkafkastreams.persons.Person;

@Slf4j
public class DebugValueTransformer implements ValueTransformer<Person, Person> {
	private ProcessorContext context;

	@Override
	public void init(ProcessorContext context) {
		this.context = context;
	}

	@Override
	public Person transform(Person value) {
		this.context.headers().forEach(h -> log.debug(h.toString()));
		return value;
	}

	@Override
	public void close() {}
}
