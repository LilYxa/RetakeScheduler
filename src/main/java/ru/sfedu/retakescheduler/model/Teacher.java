package ru.sfedu.retakescheduler.model;

import com.opencsv.bean.CsvBindByPosition;

import java.util.Date;
import java.util.Objects;

public class Teacher extends Person{
	@CsvBindByPosition(position = 4)
	private String teacherId;
	@CsvBindByPosition(position = 5)
	private Date busyDay;

	public Teacher() {
	}

	public Teacher(String lastName, String firstName, String patronymic, String email, String teacherId, Date busyDay) {
		super(lastName, firstName, patronymic, email);
		this.teacherId = teacherId;
		this.busyDay = busyDay;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public Date getBusyDay() {
		return busyDay;
	}

	public void setBusyDay(Date busyDay) {
		this.busyDay = busyDay;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Teacher teacher = (Teacher) o;
		return Objects.equals(teacherId, teacher.teacherId) && Objects.equals(busyDay, teacher.busyDay);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), teacherId, busyDay);
	}

	@Override
	public String toString() {
		return "Teacher{" +
				"teacherId='" + teacherId + '\'' +
				", busyDay=" + busyDay +
				'}';
	}
}
