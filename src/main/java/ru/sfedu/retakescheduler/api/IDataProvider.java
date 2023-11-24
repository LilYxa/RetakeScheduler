package ru.sfedu.retakescheduler.api;

import ru.sfedu.retakescheduler.model.*;

import java.util.List;

public interface IDataProvider {
	void savePerson(Person person);
	void saveStudent(Student student);
	void saveTeacher(Teacher teacher);
	void saveGroup(Group group);
	void saveScheduleUnit(ScheduleUnit scheduleUnit);
	void saveSubject(Subject subject);

	void deletePerson(Person person);
	void deleteStudent(Student student);
	void deleteTeacher(Teacher teacher);
	void deleteGroup(Group group);
	void deleteScheduleUnit(ScheduleUnit scheduleUnit);
	void deleteSubject(Subject subject);

	Person getPersonById(String id);
	Student getStudentById(String id);
	Teacher getTeacherById(String id);
	Group getGroupById(String id);
	ScheduleUnit getScheduleUnitById(String id);
	Subject getSubjectById(String id);

	List<Person> getAllPeople();
	List<Student> getAllStudents();
	List<Teacher> getAllTeachers();
	List<Group> getAllGroups();
	List<ScheduleUnit> getAllScheduleUnits();
	List<Subject> getAllSubjects();

	void dataTransform(String sourceFilePath);

}
