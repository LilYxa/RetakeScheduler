package ru.sfedu.retakescheduler.model;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.sfedu.retakescheduler.utils.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ScheduleUnit {
	@XmlElement(name = "scheduleUnitId")
	@CsvBindByPosition(position = 0)
	private String scheduleUnitId;
	@XmlElement(name = "dateTime")
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	@CsvBindByPosition(position = 1)
	@CsvDate(value = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime dateTime;
	@XmlElement(name = "subject")
	@CsvBindByPosition(position = 2)
	private Subject subject;
	@XmlElement(name = "location")
	@CsvBindByPosition(position = 3)
	private String location;
	@XmlElement(name = "person")
	@CsvBindByPosition(position = 4)
	private Person person;
	@XmlElement(name = "group")
	@CsvBindByPosition(position = 5)
	private Group group;

	public ScheduleUnit() {
		this.scheduleUnitId = UUID.randomUUID().toString();
	}

	public ScheduleUnit(String id, LocalDateTime dateTime, Subject subject, String location, Person person, Group group) {
		this.scheduleUnitId = id;
		this.dateTime = dateTime;
		this.subject = subject;
		this.location = location;
		this.person = person;
		this.group = group;
	}

	public ScheduleUnit(LocalDateTime dateTime, Subject subject, String location, Person person, Group group) {
		this.scheduleUnitId = UUID.randomUUID().toString();
		this.dateTime = dateTime;
		this.subject = subject;
		this.location = location;
		this.person = person;
		this.group = group;
	}

	public String getScheduleUnitId() {
		return scheduleUnitId;
	}

	public void setScheduleUnitId(String scheduleUnitId) {
		this.scheduleUnitId = scheduleUnitId;
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

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ScheduleUnit that = (ScheduleUnit) o;
		return Objects.equals(scheduleUnitId, that.scheduleUnitId) && Objects.equals(dateTime, that.dateTime) && Objects.equals(subject, that.subject) && Objects.equals(location, that.location) && Objects.equals(person, that.person) && Objects.equals(group, that.group);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scheduleUnitId, dateTime, subject, location, person, group);
	}

	@Override
	public String toString() {
		return "ScheduleUnit{" +
				"scheduleUnitId='" + scheduleUnitId + '\'' +
				", dateTime=" + dateTime +
				", subject=" + subject +
				", location='" + location + '\'' +
				", person=" + person +
				", group=" + group +
				'}';
	}
}
