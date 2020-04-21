package ro.go.adrhc.springkafkastreams.appevents;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.streams.PaymentsUtils;

@Component
public class AppEvents {
	@EventListener
	public void handleContextRefreshEvent(ContextRefreshedEvent ev) {
		AppProperties app = ev.getApplicationContext().getBean(AppProperties.class);
		PaymentsUtils.CURRENCY = app.getCurrency();
	}
}
