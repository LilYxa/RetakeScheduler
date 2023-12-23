package ru.sfedu.retakescheduler.utils.mappers;

import ru.sfedu.retakescheduler.model.EntityMapper;
import ru.sfedu.retakescheduler.model.Subject;

public class SubjectEntityMapper implements EntityMapper<Subject> {
	@Override
	public Object[] mapEntity(Subject subject) {
		return new Object[]{
				subject.getSubjectId(),
				subject.getSubjectName(),
				subject.getControlType()
		};
	}
}
