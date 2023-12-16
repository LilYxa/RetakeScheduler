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
	@XmlElement(name = "subjectId")
	@CsvBindByPosition(position = 2)
//	private Subject subject;
	private String subjectId;
	@XmlElement(name = "location")
	@CsvBindByPosition(position = 3)
	private String location;
	@XmlElement(name = "personId")
	@CsvBindByPosition(position = 4)
//	private Person person;
	private String personId;
	@XmlElement(name = "groupNumber")
	@CsvBindByPosition(position = 5)
//	private Group group;
	private String groupNumber;

	public ScheduleUnit() {
		this.scheduleUnitId = UUID.randomUUID().toString();
	}

	public ScheduleUnit(String id, LocalDateTime dateTime, String subjectId, String location, String personId, String groupId) {
		this.scheduleUnitId = id;
		this.dateTime = dateTime;
//		this.subject = subject;
		this.subjectId = subjectId;
		this.location = location;
//		this.person = person;
		this.personId = personId;
//		this.group = group;
		this.groupNumber = groupId;
	}

//	public ScheduleUnit(LocalDateTime dateTime, Subject subject, String location, Person person, Group group) {
//		this.scheduleUnitId = UUID.randomUUID().toString();
//		this.dateTime = dateTime;
//		this.subject = subject;
//		this.location = location;
//		this.person = person;
//		this.group = group;
//	}

	public ScheduleUnit(LocalDateTime dateTime, String subjectId, String location, String personId, String groupId) {
		this.scheduleUnitId = UUID.randomUUID().toString();
		this.dateTime = dateTime;
//		this.subject = subject;
		this.subjectId = subjectId;
		this.location = location;
//		this.person = person;
		this.personId = personId;
//		this.group = group;
		this.groupNumber = groupId;
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

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ScheduleUnit that = (ScheduleUnit) o;
		return Objects.equals(scheduleUnitId, that.scheduleUnitId) && Objects.equals(dateTime, that.dateTime) && Objects.equals(subjectId, that.subjectId) && Objects.equals(location, that.location) && Objects.equals(personId, that.personId) && Objects.equals(groupNumber, that.groupNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scheduleUnitId, dateTime, subjectId, location, personId, groupNumber);
	}

	@Override
	public String toString() {
		return "ScheduleUnit{" +
				"scheduleUnitId='" + scheduleUnitId + '\'' +
				", dateTime=" + dateTime +
				", subjectId='" + subjectId + '\'' +
				", location='" + location + '\'' +
				", personId='" + personId + '\'' +
				", groupNumber='" + groupNumber + '\'' +
				'}';
	}
}
