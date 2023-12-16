package ru.sfedu.retakescheduler.model;

import jakarta.xml.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlWrapper<T> {

	@XmlElements({
			@XmlElement(name = "student", type = Student.class),
			@XmlElement(name = "teacher", type = Teacher.class),
			@XmlElement(name = "group", type = Group.class),
			@XmlElement(name = "scheduleUnit", type = ScheduleUnit.class),
			@XmlElement(name = "subject", type = Subject.class)
	})
	private List<T> items;

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		XmlWrapper<?> that = (XmlWrapper<?>) o;
		return Objects.equals(items, that.items);
	}

	@Override
	public int hashCode() {
		return Objects.hash(items);
	}

	@Override
	public String toString() {
		return "XmlWrapper{" +
				"items=" + items +
				'}';
	}
}
