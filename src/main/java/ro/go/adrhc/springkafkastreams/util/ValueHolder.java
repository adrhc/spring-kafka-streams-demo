package ro.go.adrhc.springkafkastreams.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ValueHolder<T> {
	private T value;
}
