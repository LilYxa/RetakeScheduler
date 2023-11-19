package ru.sfedu.retakescheduler.model;

import com.opencsv.bean.CsvBindByPosition;

import java.util.Objects;

public class Subject {
	@CsvBindByPosition(position = 0)
	private String subjectId;
	@CsvBindByPosition(position = 1)
	private String subjectName;
	@CsvBindByPosition(position = 2)
	private String controlType;

	public Subject() {
	}

	public Subject(String subjectId, String subjectName, String controlType) {
		this.subjectId = subjectId;
		this.subjectName = subjectName;
		this.controlType = controlType;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getControlType() {
		return controlType;
	}

	public void setControlType(String controlType) {
		this.controlType = controlType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Subject subject = (Subject) o;
		return Objects.equals(subjectId, subject.subjectId) && Objects.equals(subjectName, subject.subjectName) && Objects.equals(controlType, subject.controlType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subjectId, subjectName, controlType);
	}

	@Override
	public String toString() {
		return "Subject{" +
				"subjectId='" + subjectId + '\'' +
				", subjectName='" + subjectName + '\'' +
				", controlType='" + controlType + '\'' +
				'}';
	}
}
