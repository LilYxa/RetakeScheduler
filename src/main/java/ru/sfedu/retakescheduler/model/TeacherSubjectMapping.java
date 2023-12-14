package ru.sfedu.retakescheduler.model;

import java.util.HashMap;
import java.util.Map;

public class TeacherSubjectMapping {
	private Map<Subject, Teacher> teacherSubjectMap;

	public TeacherSubjectMapping() {
		this.teacherSubjectMap = new HashMap<>();
	}

	public void addTeacherSubject(Teacher teacher, Subject subject) {
		teacherSubjectMap.put(subject, teacher);
	}

	public Teacher getTeacherBySubject(Subject subject) {
		return teacherSubjectMap.get(subject);
	}
}
