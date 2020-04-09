package ro.go.adrhc.springkafkastreams.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("topic")
public class TopicsProperties {
	private String persons;
	private String personsUpper;
	private String stars;
	private String starsMultiplied;

	public String getPersons() {
		return persons;
	}

	public String getPersonsUpper() {
		return personsUpper;
	}

	public String getStars() {
		return stars;
	}

	public String getStarsMultiplied() {
		return starsMultiplied;
	}

	public void setPersons(String persons) {
		this.persons = persons;
	}

	public void setPersonsUpper(String personsUpper) {
		this.personsUpper = personsUpper;
	}

	public void setStars(String stars) {
		this.stars = stars;
	}

	public void setStarsMultiplied(String starsMultiplied) {
		this.starsMultiplied = starsMultiplied;
	}
}
