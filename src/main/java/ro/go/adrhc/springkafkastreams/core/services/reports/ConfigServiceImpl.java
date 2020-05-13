package ro.go.adrhc.springkafkastreams.core.services.reports;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ro.go.adrhc.springkafkastreams.config.AppProperties;

@Service
@Slf4j
public class ConfigServiceImpl implements ConfigService {
	private final Environment env;
	private final AppProperties appProperties;

	public ConfigServiceImpl(Environment env, AppProperties appProperties) {
		this.env = env;
		this.appProperties = appProperties;
	}

	@Override
	public void report() {
		log.debug("\n\tConfiguration:\n\tspring profiles = {}\n\tapp version = {}" +
						"\n\twindowSize = {}\n\twindowUnit = {}\n\tKafka enhancements = {}",
				env.getActiveProfiles(), appProperties.getVersion(), appProperties.getWindowSize(),
				appProperties.getWindowUnit(), appProperties.isKafkaEnhanced());
	}
}
