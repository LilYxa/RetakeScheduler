package ru.sfedu.retakescheduler.model;

import com.opencsv.bean.CsvBindByPosition;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Group {

	@CsvBindByPosition(position = 0)
	private String groupNumber;
	@CsvBindByPosition(position = 1)
	private int course;
	@CsvBindByPosition(position = 2)
	private String levelOfTraining;
	@CsvBindByPosition(position = 3)
	private Date busyDay;
	@CsvBindByPosition(position = 4)
	private List<Student> students;

	public Group() {
	}

	public Group(String groupNumber, int course, String levelOfTraining, Date busyDay, List<Student> students) {
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

	public Date getBusyDay() {
		return busyDay;
	}

	public void setBusyDay(Date busyDay) {
		this.busyDay = busyDay;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
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
