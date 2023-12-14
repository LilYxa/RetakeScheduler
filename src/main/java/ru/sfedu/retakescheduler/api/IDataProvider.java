package ru.sfedu.retakescheduler.api;

import ru.sfedu.retakescheduler.model.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public interface IDataProvider {
	void saveStudent(Student student) throws Exception;
	void saveTeacher(Teacher teacher) throws Exception;
	void saveGroup(Group group) throws Exception;
	void saveScheduleUnit(ScheduleUnit scheduleUnit, TypeOfSchedule type) throws Exception;
	void saveSubject(Subject subject) throws Exception;

	void deleteStudent(Student student);
	void deleteTeacher(Teacher teacher);
	void deleteGroup(Group group);
	void deleteScheduleUnit(ScheduleUnit scheduleUnit, TypeOfSchedule type);
	void deleteSubject(Subject subject);

	Student getStudentById(String id);
	Teacher getTeacherById(String id);
	Group getGroupById(String id);
	ScheduleUnit getScheduleUnitById(String id, TypeOfSchedule type);
	Subject getSubjectById(String id);

	List<Student> getAllStudents();
	List<Teacher> getAllTeachers();
	List<Group> getAllGroups();
	List<ScheduleUnit> getAllScheduleUnits(TypeOfSchedule type);
	List<Subject> getAllSubjects();

	void dataTransform(String sourceFilePath);

	Schedule createSchedule(Schedule mainSchedule, LocalDate startDate, LocalDate endDate, boolean exportToExcel, boolean sendEmail);

}
