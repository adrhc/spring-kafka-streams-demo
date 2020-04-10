package ro.go.adrhc.springkafkastreams.model;

public class PersonStars {
	private Person person;
	private Integer stars;

	public PersonStars() {}

	public PersonStars(Person person, Integer stars) {
		this.person = person;
		this.stars = stars;
	}

	public Integer getStars() {
		return stars;
	}

	public void setStars(Integer stars) {
		this.stars = stars;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
