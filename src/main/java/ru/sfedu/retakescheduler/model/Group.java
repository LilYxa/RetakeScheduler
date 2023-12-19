package ru.sfedu.retakescheduler.model;

import com.opencsv.bean.CsvBindByPosition;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.sfedu.retakescheduler.utils.LocalDateAdapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Group implements EntityInterface {

	@XmlElement(name = "groupNumber")
	@CsvBindByPosition(position = 0)
	private String groupNumber;
	@XmlElement(name = "course")
	@CsvBindByPosition(position = 1)
	private int course;
	@XmlElement(name = "levelOfTraining")
	@CsvBindByPosition(position = 2)
	private String levelOfTraining;
	@XmlElement(name = "busyDay")
	@XmlJavaTypeAdapter(LocalDateAdapter.class)
	@CsvBindByPosition(position = 3)
	private LocalDate busyDay;
//	@CsvBindByPosition(position = 4)
//	@CsvBindAndSplitByPosition(position = 4, required = true, elementType = Student.class, splitOn = ",")
	@XmlElementWrapper(name = "students")
	@XmlElement(name = "student")
	private List<Student> students;

	public Group() {
	}

	public Group(String groupNumber, int course, String levelOfTraining, LocalDate busyDay, List<Student> students) {
		this.groupNumber = groupNumber;
		this.course = course;
		this.levelOfTraining = levelOfTraining;
		this.busyDay = busyDay;
		this.students = students;
	}

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}

	public int getCourse() {
		return course;
	}

	public void setCourse(int course) {
		this.course = course;
	}

	public String getLevelOfTraining() {
		return levelOfTraining;
	}

	public void setLevelOfTraining(String levelOfTraining) {
		this.levelOfTraining = levelOfTraining;
	}

	public LocalDate getBusyDay() {
		return busyDay;
	}

	public void setBusyDay(LocalDate busyDay) {
		this.busyDay = busyDay;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	@Override
	public TypeOfEntity getType() {
		return TypeOfEntity.GROUP;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Group group = (Group) o;
		return course == group.course && Objects.equals(groupNumber, group.groupNumber) && Objects.equals(levelOfTraining, group.levelOfTraining) && Objects.equals(busyDay, group.busyDay) && Objects.equals(students, group.students);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupNumber, course, levelOfTraining, busyDay, students);
	}

	@Override
	public String toString() {
		return "Group{" +
				"groupNumber='" + groupNumber + '\'' +
				", course=" + course +
				", levelOfTraining='" + levelOfTraining + '\'' +
				", busyDay=" + busyDay +
				", students=" + students +
				'}';
	}
}
