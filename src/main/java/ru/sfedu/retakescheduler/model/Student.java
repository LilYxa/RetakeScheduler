package ru.sfedu.retakescheduler.model;

import java.util.Objects;

public class Student extends Person{
	private String studentId;
	private int finalRating;

	public Student() {
	}

	public Student(String lastName, String firstName, String patronymic, String email, String studentId, int finalRating) {
		super(lastName, firstName, patronymic, email);
		this.studentId = studentId;
		this.finalRating = finalRating;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public int getFinalRating() {
		return finalRating;
	}

	public void setFinalRating(int finalRating) {
		this.finalRating = finalRating;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Student student = (Student) o;
		return finalRating == student.finalRating && Objects.equals(studentId, student.studentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), studentId, finalRating);
	}

	@Override
	public String toString() {
		return "Student{" +
				"studentId='" + studentId + '\'' +
				", finalRating=" + finalRating +
				'}';
	}
}
