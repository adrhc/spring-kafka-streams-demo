package ro.go.adrhc.springkafkastreams.kextensions.kstream.operators.peek;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.TaskId;

@AllArgsConstructor
public class KPeekContext {
	private final ProcessorContext context;

	public String applicationId() {return context.applicationId();}

	public TaskId taskId() {return context.taskId();}

	public String topic() {return context.topic();}

	public int partition() {return context.partition();}

	public long offset() {return context.offset();}

	public Headers headers() {return context.headers();}

	public long timestamp() {return context.timestamp();}
}
