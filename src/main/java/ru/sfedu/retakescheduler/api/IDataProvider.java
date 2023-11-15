package ru.sfedu.retakescheduler.api;

import ru.sfedu.retakescheduler.model.Group;
import ru.sfedu.retakescheduler.model.Student;
import ru.sfedu.retakescheduler.model.Teacher;

public interface IDataProvider {
	void saveStudent(Student student);
	void saveTeacher(Teacher teacher);
	void saveGroup(Group group);
}
