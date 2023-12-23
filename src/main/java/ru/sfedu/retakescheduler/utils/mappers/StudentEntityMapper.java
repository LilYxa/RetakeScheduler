package ru.sfedu.retakescheduler.utils.mappers;

import ru.sfedu.retakescheduler.model.EntityMapper;
import ru.sfedu.retakescheduler.model.Student;

public class StudentEntityMapper implements EntityMapper<Student> {
	@Override
	public Object[] mapEntity(Student student) {
		return new Object[]{
				student.getStudentId(),
				student.getLastName(),
				student.getFirstName(),
				student.getPatronymic(),
				student.getEmail(),
				student.getAverageScore()
		};
	}
}
