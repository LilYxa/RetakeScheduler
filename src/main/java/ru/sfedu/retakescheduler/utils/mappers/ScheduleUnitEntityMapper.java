package ru.sfedu.retakescheduler.utils.mappers;

import ru.sfedu.retakescheduler.model.EntityMapper;
import ru.sfedu.retakescheduler.model.ScheduleUnit;

public class ScheduleUnitEntityMapper implements EntityMapper<ScheduleUnit> {
	@Override
	public Object[] mapEntity(ScheduleUnit scheduleUnit) {
		return new Object[]{
				scheduleUnit.getScheduleUnitId(),
				scheduleUnit.getDateTime().toString(),
				scheduleUnit.getLocation(),
				scheduleUnit.getSubjectId(),
				scheduleUnit.getPersonId(),
				scheduleUnit.getGroupNumber()
		};
	}
}
