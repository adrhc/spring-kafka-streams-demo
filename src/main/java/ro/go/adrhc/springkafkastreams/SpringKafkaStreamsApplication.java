package ro.go.adrhc.springkafkastreams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafka
@EnableKafkaStreams
@EnableConfigurationProperties
@SpringBootApplication
public class SpringKafkaStreamsApplication {
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpringKafkaStreamsApplication.class, args);
		Thread.sleep(Long.MAX_VALUE);
	}
}
