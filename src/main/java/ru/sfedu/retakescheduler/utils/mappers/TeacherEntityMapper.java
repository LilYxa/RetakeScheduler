package ru.sfedu.retakescheduler.utils.mappers;

import ru.sfedu.retakescheduler.model.EntityMapper;
import ru.sfedu.retakescheduler.model.Teacher;

public class TeacherEntityMapper implements EntityMapper<Teacher> {
	@Override
	public Object[] mapEntity(Teacher teacher) {
		return new Object[]{
				teacher.getTeacherId(),
				teacher.getLastName(),
				teacher.getFirstName(),
				teacher.getPatronymic(),
				teacher.getEmail(),
				teacher.getBusyDay().toString()
		};
	}
}
