package ru.sfedu.retakescheduler.model;

public class ExcelRow {
	private String level;
	private int course;
	private String group;
	private String controlType;
	private String discipline;
	private String studentName;
	private String teacherName;
	private int finalRating;

	public ExcelRow() {
	}

	public ExcelRow(String level, int course, String group, String controlType, String discipline, String studentName, String teacherName, int finalRating) {
		this.level = level;
		this.course = course;
		this.group = group;
		this.controlType = controlType;
		this.discipline = discipline;
		this.studentName = studentName;
		this.teacherName = teacherName;
		this.finalRating = finalRating;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getCourse() {
		return course;
	}

	public void setCourse(int course) {
		this.course = course;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getControlType() {
		return controlType;
	}

	public void setControlType(String controlType) {
		this.controlType = controlType;
	}

	public String getDiscipline() {
		return discipline;
	}

	public void setDiscipline(String discipline) {
		this.discipline = discipline;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public int getFinalRating() {
		return finalRating;
	}

	public void setFinalRating(int finalRating) {
		this.finalRating = finalRating;
	}

	@Override
	public String toString() {
		return "ExcelRow{" +
				"level='" + level + '\'' +
				", course=" + course +
				", group='" + group + '\'' +
				", controlType='" + controlType + '\'' +
				", discipline='" + discipline + '\'' +
				", studentName='" + studentName + '\'' +
				", teacherName='" + teacherName + '\'' +
				", finalRating=" + finalRating +
				'}';
	}
}
