package ru.sfedu.retakescheduler.model;

import java.util.List;
import java.util.Objects;

public class Schedule {
	private TypeOfSchedule typeOfSchedule;
	private List<ScheduleUnit> units;

	public Schedule() {
	}

	public Schedule(TypeOfSchedule type) {
		this.typeOfSchedule = type;
	}

	public Schedule(TypeOfSchedule typeOfSchedule, List<ScheduleUnit> units) {
		this.typeOfSchedule = typeOfSchedule;
		this.units = units;
	}

	public TypeOfSchedule getTypeOfSchedule() {
		return typeOfSchedule;
	}

	public void setTypeOfSchedule(TypeOfSchedule typeOfSchedule) {
		this.typeOfSchedule = typeOfSchedule;
	}

	public List<ScheduleUnit> getUnits() {
		return units;
	}

	public void setUnits(List<ScheduleUnit> units) {
		this.units = units;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Schedule schedule = (Schedule) o;
		return typeOfSchedule == schedule.typeOfSchedule && Objects.equals(units, schedule.units);
	}

	@Override
	public int hashCode() {
		return Objects.hash(typeOfSchedule, units);
	}

	@Override
	public String toString() {
		return "Schedule{" +
				"typeOfSchedule=" + typeOfSchedule +
				", units=" + units +
				'}';
	}
}
