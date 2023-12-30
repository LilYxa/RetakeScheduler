package ru.sfedu.retakescheduler.utils.mappers;

import ru.sfedu.retakescheduler.model.EntityMapper;
import ru.sfedu.retakescheduler.model.ScheduleUnit;
import ru.sfedu.retakescheduler.model.Teacher;

public class ScheduleUnitEntityMapper implements EntityMapper<ScheduleUnit> {
	@Override
	public Object[] mapEntity(ScheduleUnit scheduleUnit) {
		Teacher teacher = (Teacher) scheduleUnit.getPerson();
		return new Object[]{
				scheduleUnit.getScheduleUnitId(),
				scheduleUnit.getDateTime().toString(),
				scheduleUnit.getLocation(),
				scheduleUnit.getSubject().getSubjectId(),
				teacher.getTeacherId(),
				scheduleUnit.getGroup().getGroupNumber()
		};
	}
}
