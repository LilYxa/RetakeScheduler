package ru.sfedu.retakescheduler.utils.mappers;

import ru.sfedu.retakescheduler.model.EntityMapper;
import ru.sfedu.retakescheduler.model.Group;

public class GroupEntityMapper implements EntityMapper<Group> {
	@Override
	public Object[] mapEntity(Group group) {
		return new Object[]{
				group.getGroupNumber(),
				group.getCourse(),
				group.getLevelOfTraining(),
				group.getBusyDay().toString()
		};
	}
}