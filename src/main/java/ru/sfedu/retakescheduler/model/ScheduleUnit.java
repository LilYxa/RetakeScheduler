package ru.sfedu.retakescheduler.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class ScheduleUnit {
	private LocalDateTime dateTime;
	private Subject subject;
	private String location;
	private Person person;

	public ScheduleUnit() {
	}

	public ScheduleUnit(LocalDateTime dateTime, Subject subject, String location, Person person) {
		this.dateTime = dateTime;
		this.subject = subject;
		this.location = location;
		this.person = person;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ScheduleUnit that = (ScheduleUnit) o;
		return Objects.equals(dateTime, that.dateTime) && Objects.equals(subject, that.subject) && Objects.equals(location, that.location) && Objects.equals(person, that.person);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateTime, subject, location, person);
	}

	@Override
	public String toString() {
		return "ScheduleUnit{" +
				"dateTime=" + dateTime +
				", subject=" + subject +
				", location='" + location + '\'' +
				", person=" + person +
				'}';
	}
}
