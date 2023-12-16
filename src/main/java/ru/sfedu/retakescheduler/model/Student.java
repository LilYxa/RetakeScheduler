package ru.sfedu.retakescheduler.model;

import com.opencsv.bean.CsvBindByPosition;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Objects;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Student extends Person{
	@XmlElement(name = "studentId")
	@CsvBindByPosition(position = 4)
	private String studentId;
	@XmlElement(name = "averageScore")
	@CsvBindByPosition(position = 5)
	private double averageScore;

	public Student() {
		this.studentId = UUID.randomUUID().toString();
	}

	public Student(String lastName, String firstName, String patronymic, String email, String studentId, double averageScore) {
		super(lastName, firstName, patronymic, email);
		this.studentId = studentId;
		this.averageScore = averageScore;
	}

	public Student(String lastName, String firstName, String patronymic, String email, double averageScore) {
		super(lastName, firstName, patronymic, email);
		this.studentId = UUID.randomUUID().toString();
		this.averageScore = averageScore;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public double getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(double averageScore) {
		this.averageScore = averageScore;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Student student = (Student) o;
		return Double.compare(student.averageScore, averageScore) == 0 && Objects.equals(studentId, student.studentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), studentId, averageScore);
	}

	@Override
	public String toString() {
		return "Student{" +
				"studentId='" + studentId + '\'' +
				", averageScore=" + averageScore +
				'}';
	}
}
