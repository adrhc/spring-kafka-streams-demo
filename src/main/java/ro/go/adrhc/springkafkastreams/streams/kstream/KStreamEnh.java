package ro.go.adrhc.springkafkastreams.streams.kstream;

import lombok.AllArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import ro.go.adrhc.springkafkastreams.streams.kstream.operators.aggregators.WindowByEnh;
import ro.go.adrhc.springkafkastreams.streams.kstream.operators.peek.KPeek;
import ro.go.adrhc.springkafkastreams.streams.kstream.operators.peek.KPeekParams;

import java.time.temporal.TemporalUnit;
import java.util.function.Consumer;

@AllArgsConstructor
public class KStreamEnh<K, V> implements KStream<K, V> {
	private final KStream<K, V> delegate;
	private final StreamsBuilder streamsBuilder;

	public WindowByEnh<K, V> windowedBy(int windowSize, TemporalUnit unit) {
		return new WindowByEnh<>(windowSize, unit, delegate, streamsBuilder);
	}

	/**
	 * similar to KStream.peek() but also allow partially access to ProcessorContext
	 */
	public KStreamEnh<K, V> peek(Consumer<KPeekParams<K, V>> consumer) {
		return new KStreamEnh<>(delegate.transformValues(new KPeek<>(consumer)), streamsBuilder);
	}

	@Override
	public KStream<K, V> filter(Predicate<? super K, ? super V> predicate) {return delegate.filter(predicate);}

	@Override
	public KStream<K, V> filterNot(Predicate<? super K, ? super V> predicate) {return delegate.filterNot(predicate);}

	@Override
	public <KR> KStream<KR, V> selectKey(KeyValueMapper<? super K, ? super V, ? extends KR> mapper) {return delegate.selectKey(mapper);}

	@Override
	public <KR, VR> KStream<KR, VR> map(KeyValueMapper<? super K, ? super V, ? extends KeyValue<? extends KR, ? extends VR>> mapper) {return delegate.map(mapper);}

	@Override
	public <VR> KStream<K, VR> mapValues(ValueMapper<? super V, ? extends VR> mapper) {return delegate.mapValues(mapper);}

	@Override
	public <VR> KStream<K, VR> mapValues(ValueMapperWithKey<? super K, ? super V, ? extends VR> mapper) {return delegate.mapValues(mapper);}

	@Override
	public <KR, VR> KStream<KR, VR> flatMap(KeyValueMapper<? super K, ? super V, ? extends Iterable<? extends KeyValue<? extends KR, ? extends VR>>> mapper) {return delegate.flatMap(mapper);}

	@Override
	public <VR> KStream<K, VR> flatMapValues(ValueMapper<? super V, ? extends Iterable<? extends VR>> mapper) {return delegate.flatMapValues(mapper);}

	@Override
	public <VR> KStream<K, VR> flatMapValues(ValueMapperWithKey<? super K, ? super V, ? extends Iterable<? extends VR>> mapper) {return delegate.flatMapValues(mapper);}

	@Override
	public void print(Printed<K, V> printed) {delegate.print(printed);}

	@Override
	public void foreach(ForeachAction<? super K, ? super V> action) {delegate.foreach(action);}

	@Override
	public KStreamEnh<K, V> peek(ForeachAction<? super K, ? super V> action) {
		return new KStreamEnh<>(delegate.peek(action), streamsBuilder);
	}

	@Override
	public KStream<K, V>[] branch(Predicate<? super K, ? super V>... predicates) {return delegate.branch(predicates);}

	@Override
	public KStream<K, V> merge(KStream<K, V> stream) {return delegate.merge(stream);}

	@Override
	public KStream<K, V> through(String topic) {return delegate.through(topic);}

	@Override
	public KStream<K, V> through(String topic, Produced<K, V> produced) {return delegate.through(topic, produced);}

	@Override
	public void to(String topic) {delegate.to(topic);}

	@Override
	public void to(String topic, Produced<K, V> produced) {delegate.to(topic, produced);}

	@Override
	public void to(TopicNameExtractor<K, V> topicExtractor) {delegate.to(topicExtractor);}

	@Override
	public void to(TopicNameExtractor<K, V> topicExtractor, Produced<K, V> produced) {delegate.to(topicExtractor, produced);}

	@Override
	public <K1, V1> KStream<K1, V1> transform(TransformerSupplier<? super K, ? super V, KeyValue<K1, V1>> transformerSupplier, String... stateStoreNames) {return delegate.transform(transformerSupplier, stateStoreNames);}

	@Override
	public <K1, V1> KStream<K1, V1> flatTransform(TransformerSupplier<? super K, ? super V, Iterable<KeyValue<K1, V1>>> transformerSupplier, String... stateStoreNames) {return delegate.flatTransform(transformerSupplier, stateStoreNames);}

	@Override
	public <VR> KStream<K, VR> transformValues(ValueTransformerSupplier<? super V, ? extends VR> valueTransformerSupplier, String... stateStoreNames) {return delegate.transformValues(valueTransformerSupplier, stateStoreNames);}

	@Override
	public <VR> KStream<K, VR> transformValues(ValueTransformerWithKeySupplier<? super K, ? super V, ? extends VR> valueTransformerSupplier, String... stateStoreNames) {return delegate.transformValues(valueTransformerSupplier, stateStoreNames);}

	@Override
	public <VR> KStream<K, VR> flatTransformValues(ValueTransformerSupplier<? super V, Iterable<VR>> valueTransformerSupplier, String... stateStoreNames) {return delegate.flatTransformValues(valueTransformerSupplier, stateStoreNames);}

	@Override
	public <VR> KStream<K, VR> flatTransformValues(ValueTransformerWithKeySupplier<? super K, ? super V, Iterable<VR>> valueTransformerSupplier, String... stateStoreNames) {return delegate.flatTransformValues(valueTransformerSupplier, stateStoreNames);}

	@Override
	public void process(ProcessorSupplier<? super K, ? super V> processorSupplier, String... stateStoreNames) {delegate.process(processorSupplier, stateStoreNames);}

	@Override
	public KGroupedStream<K, V> groupByKey() {return delegate.groupByKey();}

	@Override
	@Deprecated
	public KGroupedStream<K, V> groupByKey(Serialized<K, V> serialized) {return delegate.groupByKey(serialized);}

	@Override
	public KGroupedStream<K, V> groupByKey(Grouped<K, V> grouped) {return delegate.groupByKey(grouped);}

	@Override
	public <KR> KGroupedStream<KR, V> groupBy(KeyValueMapper<? super K, ? super V, KR> selector) {return delegate.groupBy(selector);}

	@Override
	@Deprecated
	public <KR> KGroupedStream<KR, V> groupBy(KeyValueMapper<? super K, ? super V, KR> selector, Serialized<KR, V> serialized) {return delegate.groupBy(selector, serialized);}

	@Override
	public <KR> KGroupedStream<KR, V> groupBy(KeyValueMapper<? super K, ? super V, KR> selector, Grouped<KR, V> grouped) {return delegate.groupBy(selector, grouped);}

	@Override
	public <VO, VR> KStream<K, VR> join(KStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows) {return delegate.join(otherStream, joiner, windows);}

	@Override
	public <VO, VR> KStream<K, VR> join(KStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows, Joined<K, V, VO> joined) {return delegate.join(otherStream, joiner, windows, joined);}

	@Override
	public <VO, VR> KStream<K, VR> leftJoin(KStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows) {return delegate.leftJoin(otherStream, joiner, windows);}

	@Override
	public <VO, VR> KStream<K, VR> leftJoin(KStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows, Joined<K, V, VO> joined) {return delegate.leftJoin(otherStream, joiner, windows, joined);}

	@Override
	public <VO, VR> KStream<K, VR> outerJoin(KStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows) {return delegate.outerJoin(otherStream, joiner, windows);}

	@Override
	public <VO, VR> KStream<K, VR> outerJoin(KStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows, Joined<K, V, VO> joined) {return delegate.outerJoin(otherStream, joiner, windows, joined);}

	@Override
	public <VT, VR> KStream<K, VR> join(KTable<K, VT> table, ValueJoiner<? super V, ? super VT, ? extends VR> joiner) {return delegate.join(table, joiner);}

	@Override
	public <VT, VR> KStream<K, VR> join(KTable<K, VT> table, ValueJoiner<? super V, ? super VT, ? extends VR> joiner, Joined<K, V, VT> joined) {return delegate.join(table, joiner, joined);}

	@Override
	public <VT, VR> KStream<K, VR> leftJoin(KTable<K, VT> table, ValueJoiner<? super V, ? super VT, ? extends VR> joiner) {return delegate.leftJoin(table, joiner);}

	@Override
	public <VT, VR> KStream<K, VR> leftJoin(KTable<K, VT> table, ValueJoiner<? super V, ? super VT, ? extends VR> joiner, Joined<K, V, VT> joined) {return delegate.leftJoin(table, joiner, joined);}

	@Override
	public <GK, GV, RV> KStream<K, RV> join(GlobalKTable<GK, GV> globalKTable, KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper, ValueJoiner<? super V, ? super GV, ? extends RV> joiner) {return delegate.join(globalKTable, keyValueMapper, joiner);}

	@Override
	public <GK, GV, RV> KStream<K, RV> leftJoin(GlobalKTable<GK, GV> globalKTable, KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper, ValueJoiner<? super V, ? super GV, ? extends RV> valueJoiner) {return delegate.leftJoin(globalKTable, keyValueMapper, valueJoiner);}
}
