package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;
import ru.sfedu.retakescheduler.utils.FileUtil;
import ru.sfedu.retakescheduler.utils.mappers.SubjectEntityMapper;
import ru.sfedu.retakescheduler.utils.mappers.TeacherEntityMapper;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.sfedu.retakescheduler.utils.PropertiesConfigUtil.getProperty;
import static ru.sfedu.retakescheduler.utils.XmlUtil.saveRecords;

public class DataProviderPostgresTest extends BaseTest {
	Student student1 = new Student("Ivanov", "Ivan", "Ivanovich", "ivanov@mail.ru", "53b51af6-04df-4af5-8bdb-499436bc575a", 77.5);
	Student student2 = new Student("Petrov", "Petr", "Petrovich", "petrov@mail.ru", "8bccaa52-ef8c-4b1a-879f-10c6dfea861d", 94.6);
	final Student student3 = new Student("Sidorov", "Sidor", "Sidorovich","sidorov@mail.ru", "1e1d663e-29d4-4599-a2a2-18723e47f560", 88.8);
	Teacher teacher = new Teacher("Васильев", "Иван", "Николаевич", "vasiliev@mail.ru", "teach1", LocalDate.now());
	Teacher teacher2 = new Teacher("Васильев", "Вася", "Николаевич", "vasi@mail.ru", "teach2", LocalDate.now());
	Teacher teacher3 = new Teacher("Васил", "Иван", "Николаевич", "va@mail.ru", "teach3", LocalDate.now());
	Teacher teacherForSchedule = new Teacher("Doe", "John", "Johnovich", "john@mail.ru", "4e61290b-c004-491e-8c7a-ee194711ee47", LocalDate.now());
	Group group = new Group("22ВТ-12.03.01.01-о1", 1, "Бакалавриат", LocalDate.now().with(DayOfWeek.TUESDAY), new ArrayList<>(Arrays.asList(student3, student1, student2)));
	Group group2 = new Group("22ВТ-12.03.01.01-о2", 1, "Бакалавриат", LocalDate.now().with(DayOfWeek.TUESDAY), new ArrayList<>(Arrays.asList(student3, student1, student2)));
	Group group3 = new Group("22ВТ-12.03.01.01-о3", 1, "Бакалавриат", LocalDate.now().with(DayOfWeek.TUESDAY), new ArrayList<>(Arrays.asList(student3, student1, student2)));
	Subject subject = new Subject("q2dw1", "Математика", "Экзамен");
	Subject subject2 = new Subject("q2dw1fddxfd", "Физика", "Экзамен");
	Subject subject3 = new Subject("q2dw1kjb", "История", "Экзамен");
	Subject testSubjectForSchedule = new Subject("1wfwef-hbehdh-qwwqq-dw1sqs","Test Subject", "Test Type");

	ScheduleUnit scheduleUnit = new ScheduleUnit("jknkjwndkcjnwkdjcn", LocalDateTime.of(2023, 12, 12, 12, 12), "q2dw1", "location", "teach1", "22ВТ-12.03.01.01-о1");
	ScheduleUnit scheduleUnit2 = new ScheduleUnit("jknkjwndk", LocalDateTime.of(2023, 12, 14, 12, 12), "q2dw1fddxfd", "location", "teach2", "22ВТ-12.03.01.01-о2");
	ScheduleUnit scheduleUnit3 = new ScheduleUnit("okwokdmwok", LocalDateTime.of(2023, 12, 11, 12, 12), "q2dw1kjb", "location", "teach3", "22ВТ-12.03.01.01-о3");

	private static final Logger log = LogManager.getLogger(DataProviderPostgresTest.class);
	private static DataProviderPostgres dataProviderPostgres = new DataProviderPostgres();

	@BeforeEach
	public void beforeEach() {
		try {
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			List<String> tableNames = List.of(
					Constants.STUDENT_TABLE_NAME,
					Constants.TEACHER_TABLE_NAME,
					Constants.GROUP_TABLE_NAME,
					Constants.SUBJECT_TABLE_NAME,
					Constants.MAIN_SCHEDULE_UNITS_TABLE_NAME,
					Constants.RETAKE_SCHEDULE_UNITS_TABLE_NAME,
					Constants.GROUP_STUDENT_TABLE_NAME
			);
			tableNames.forEach(table -> {
				try {
					truncateTable(statement, table);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (SQLException e) {
			log.error("beforeEach[]: error: {}", e.getMessage());
		}
	}

	private void truncateTable(Statement statement, String tableName) throws SQLException {
		statement.executeUpdate(String.format(Constants.SQL_TRUNCATE_TABLE, tableName));
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
				getProperty(Constants.POSTGRES_JDBC_URL),
				getProperty(Constants.POSTGRES_DB_USER),
				getProperty(Constants.POSTGRES_DB_PASSWORD));
	}


	@Test
	public void testSaveStudentPositive() throws Exception {
		log.debug("testSaveStudentPositive[1]: test start");
		log.debug("testSaveStudentPositive[2]: student1: {}", student1);
		log.debug("testSaveStudentPositive[3]: student2: {}", student2);
		log.debug("testSaveStudentPositive[4]: student3: {}", student3);
		dataProviderPostgres.saveStudent(student1);
		dataProviderPostgres.saveStudent(student2);
		dataProviderPostgres.saveStudent(student3);
		assertNotNull(dataProviderPostgres.getStudentById(student1.getStudentId()));
		assertNotNull(dataProviderPostgres.getStudentById(student2.getStudentId()));
		assertNotNull(dataProviderPostgres.getStudentById(student3.getStudentId()));
	}

	@Test
	public void testSaveStudentNegative() throws Exception {
		log.debug("testSaveStudentNegative[1]: test start");
		log.debug("testSaveStudentNegative[2]: student1: {}", student1);
		dataProviderPostgres.saveStudent(student1);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.saveStudent(student1);
		});
		assertEquals("student with this id already exists", exception.getMessage());
	}

	@Test
	public void testSaveTeacherPositive() throws Exception {
		log.debug("testSaveTeacherPositive[1]: test start");
		log.debug("testSaveTeacherPositive[2]: teacher: {}", teacher);
		dataProviderPostgres.saveTeacher(teacher);
		dataProviderPostgres.saveTeacher(teacher2);
		dataProviderPostgres.saveTeacher(teacher3);
		assertNotNull(dataProviderPostgres.getTeacherById(teacher.getTeacherId()));
		assertNotNull(dataProviderPostgres.getTeacherById(teacher2.getTeacherId()));
		assertNotNull(dataProviderPostgres.getTeacherById(teacher3.getTeacherId()));
	}

	@Test
	public void testSaveTeacherNegative() throws Exception {
		log.debug("testSaveTeacherNegative[1]: test start");
		log.debug("testSaveTeacherNegative[2]: teacher: {}", teacher);
		dataProviderPostgres.saveTeacher(teacher);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.saveTeacher(teacher);
		});
		assertEquals("teacher with this id already exists", exception.getMessage());
	}

	@Test
	public void testSaveGroupPositive() throws Exception {
		log.debug("testSaveGroupPositive[1]: test start");
		log.debug("testSaveGroupPositive[2]: group1: {}", group);

		dataProviderPostgres.saveStudent(student1);
		dataProviderPostgres.saveStudent(student2);
		dataProviderPostgres.saveStudent(student3);

		dataProviderPostgres.saveGroup(group);
		dataProviderPostgres.saveGroup(group2);
		dataProviderPostgres.saveGroup(group3);
		assertNotNull(dataProviderPostgres.getGroupById(group.getGroupNumber()));
		assertNotNull(dataProviderPostgres.getGroupById(group2.getGroupNumber()));
		assertNotNull(dataProviderPostgres.getGroupById(group3.getGroupNumber()));
	}

	@Test
	public void testSaveGroupNegative() throws Exception {
		log.debug("testSaveGroupNegative[1]: test start");
		log.debug("testSaveGroupNegative[2]: group1: {}", group);
		dataProviderPostgres.saveGroup(group);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.saveGroup(group);
		});
		assertEquals("group with this id already exists", exception.getMessage());
	}

	@Test
	public void testSaveSubjectPositive() throws Exception {
		log.debug("testSaveSubjectPositive[1]: test start");
		log.debug("testSaveSubjectPositive[2]: subject: {}", subject);
		dataProviderPostgres.saveSubject(subject);
		dataProviderPostgres.saveSubject(subject2);
		dataProviderPostgres.saveSubject(subject3);
		assertNotNull(dataProviderPostgres.getSubjectById(subject.getSubjectId()));
		assertNotNull(dataProviderPostgres.getSubjectById(subject2.getSubjectId()));
		assertNotNull(dataProviderPostgres.getSubjectById(subject3.getSubjectId()));
	}

	@Test
	public void testSaveSubjectNegative() throws Exception {
		log.debug("testSaveSubjectNegative[1]: test start");
		log.debug("testSaveSubjectNegative[2]: subject1: {}", subject);
		dataProviderPostgres.saveSubject(subject);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.saveSubject(subject);
		});
		assertEquals("subject with this id already exists", exception.getMessage());
	}

	@Test
	public void testSaveScheduleUnitPositive() throws Exception {
		log.debug("testSaveScheduleUnitPositive[1]: test start");
		log.debug("testSaveScheduleUnitPositive[2]: scheduleUnit1: {}", scheduleUnit);
		dataProviderPostgres.saveSubject(subject);
		dataProviderPostgres.saveTeacher(teacher);
		dataProviderPostgres.saveGroup(group);
		dataProviderPostgres.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		assertNotNull(dataProviderPostgres.getScheduleUnitById(scheduleUnit.getScheduleUnitId(), TypeOfSchedule.MAIN));
	}

	@Test
	public void testSaveScheduleUnitNegative() throws Exception {
		log.debug("testSaveScheduleUnitNegative[1]: test start");
		log.debug("testSaveScheduleUnitNegative[2]: scheduleUnit1: {}", scheduleUnit);
		dataProviderPostgres.saveSubject(subject);
		dataProviderPostgres.saveTeacher(teacher);
		dataProviderPostgres.saveGroup(group);
		dataProviderPostgres.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		});
		assertEquals("error during inserting scheduleUnit", exception.getMessage());
	}

	@Test
	public void testDeleteStudentByIdPositive() throws Exception {
		log.debug("testDeleteStudentById[1]: test start");
		List<Student> expectedStudentsAfterDelete = new ArrayList<>();
		expectedStudentsAfterDelete.add(student2);
		expectedStudentsAfterDelete.add(student3);

		dataProviderPostgres.saveStudent(student1);
		dataProviderPostgres.saveStudent(student2);
		dataProviderPostgres.saveStudent(student3);

		List<Student> studentsBeforeDelete = dataProviderPostgres.getAllStudents();
		log.debug("testDeleteStudentById[2]: students before removing: {}", studentsBeforeDelete);
		dataProviderPostgres.deleteStudentById(student1.getStudentId());
		List<Student> studentsAfterDelete = dataProviderPostgres.getAllStudents();
		log.debug("testDeleteStudentById[3]: students after removing: {}", studentsAfterDelete);
		assertEquals(expectedStudentsAfterDelete, studentsAfterDelete);
	}

	@Test
	public void testDeleteStudentByIdNegative() {
		log.debug("testDeleteStudentByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.deleteStudentById("qwertyuio");
		});
		assertEquals("there is no student with this id", exception.getMessage());
	}

	@Test
	public void testDeleteTeacherByIdPositive() throws Exception {
		log.debug("testDeleteTeacherById[1]: test start");
		List<Teacher> expectedTeachersAfterDelete = new ArrayList<>();
		expectedTeachersAfterDelete.add(teacher2);
		expectedTeachersAfterDelete.add(teacher3);

		dataProviderPostgres.saveTeacher(teacher);
		dataProviderPostgres.saveTeacher(teacher2);
		dataProviderPostgres.saveTeacher(teacher3);

		List<Teacher> teachersBeforeDelete = dataProviderPostgres.getAllTeachers();
		log.debug("testDeleteTeacherById[2]: teachers before removing: {}", teachersBeforeDelete);
		dataProviderPostgres.deleteTeacherById(teacher.getTeacherId());
		List<Teacher> teachersAfterDelete = dataProviderPostgres.getAllTeachers();
		log.debug("testDeleteTeacherById[3]: teachers after removing: {}", teachersAfterDelete);
		assertEquals(expectedTeachersAfterDelete, teachersAfterDelete);
	}

	@Test
	public void testDeleteTeacherByIdNegative() {
		log.debug("testDeleteTeacherByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.deleteTeacherById("qwertyuio");
		});
		assertEquals("there is no teacher with this id", exception.getMessage());
	}

	@Test
	public void testDeleteGroupByGroupNumberPositive() throws Exception {
		log.debug("testDeleteGroupByGroupNumber[1]: test start");
		List<Group> expectedGroupsAfterDelete = new ArrayList<>();
		expectedGroupsAfterDelete.add(group2);
		expectedGroupsAfterDelete.add(group3);

		dataProviderPostgres.saveGroup(group);
		dataProviderPostgres.saveGroup(group2);
		dataProviderPostgres.saveGroup(group3);

		List<Group> groupsBeforeDelete = dataProviderPostgres.getAllGroups();
		log.debug("testDeleteGroupByGroupNumber[2]: groups before removing: {}", groupsBeforeDelete);
		dataProviderPostgres.deleteGroupById(group.getGroupNumber());
		List<Group> groupsAfterDelete = dataProviderPostgres.getAllGroups();
		log.debug("testDeleteGroupByGroupNumber[3]: groups after removing: {}", groupsAfterDelete);
		assertEquals(expectedGroupsAfterDelete, groupsAfterDelete);
	}

	@Test
	public void testDeleteGroupByGroupNumberNegative() {
		log.debug("testDeleteGroupByGroupNumberNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.deleteGroupById("qwertyuio");
		});
		assertEquals("there is no group with this id", exception.getMessage());
	}

	@Test
	public void testDeleteSubjectByIdPositive() throws Exception {
		log.debug("testDeleteSubjectById[1]: test start");
		List<Subject> expectedSubjectsAfterDelete = new ArrayList<>();
		expectedSubjectsAfterDelete.add(subject2);
		expectedSubjectsAfterDelete.add(subject3);

		dataProviderPostgres.saveSubject(subject);
		dataProviderPostgres.saveSubject(subject2);
		dataProviderPostgres.saveSubject(subject3);

		List<Subject> subjectsBeforeDelete = dataProviderPostgres.getAllSubjects();
		log.debug("testDeleteSubjectById[2]: subjects before removing: {}", subjectsBeforeDelete);
		dataProviderPostgres.deleteSubjectById(subject.getSubjectId());
		List<Subject> subjectsAfterDelete = dataProviderPostgres.getAllSubjects();
		log.debug("testDeleteSubjectById[3]: subjects after removing: {}", subjectsAfterDelete);
		assertEquals(expectedSubjectsAfterDelete, subjectsAfterDelete);
	}

	@Test
	public void testDeleteSubjectByIdNegative() {
		log.debug("testDeleteSubjectByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.deleteSubjectById("qwertyuio");
		});
		assertEquals("there is no subject with this id", exception.getMessage());
	}

	@Test
	public void testDeleteScheduleUnitByIdPositive() throws Exception {
		log.debug("testDeleteScheduleUnitById[1]: test start");
		List<ScheduleUnit> expectedScheduleUnitsAfterDelete = new ArrayList<>();
		dataProviderPostgres.saveSubject(subject);
		dataProviderPostgres.saveTeacher(teacher);
		dataProviderPostgres.saveGroup(group);
		dataProviderPostgres.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);

		List<ScheduleUnit> scheduleUnitsBeforeDelete = dataProviderPostgres.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testDeleteScheduleUnitById[2]: schedule units before removing: {}", scheduleUnitsBeforeDelete);
		dataProviderPostgres.deleteScheduleUnitById(scheduleUnit.getScheduleUnitId(), TypeOfSchedule.MAIN);
		List<ScheduleUnit> scheduleUnitsAfterDelete = dataProviderPostgres.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testDeleteScheduleUnitById[3]: schedule units after removing: {}", scheduleUnitsAfterDelete);
		assertEquals(expectedScheduleUnitsAfterDelete, scheduleUnitsAfterDelete);
	}

	@Test
	public void testDeleteScheduleUnitByIdNegative() {
		log.debug("testDeleteScheduleUnitByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.deleteScheduleUnitById("qwertyuio", TypeOfSchedule.MAIN);
		});
		assertEquals("there is no scheduleUnit with this id", exception.getMessage());
	}

	@Test
	public void testGetStudentByIdPositive() throws Exception {
		log.debug("testGetStudentByIdPositive[1]: test start");
		dataProviderPostgres.saveStudent(student3);
		log.debug("testGetStudentByIdPositive[2]: expected student: {}", student3);
		Student actualStudent = dataProviderPostgres.getStudentById(student3.getStudentId());
		log.debug("testGetStudentByIdPositive[3]: actual student: {}", actualStudent);
		assertEquals(student3, actualStudent);
	}

	@Test
	public void testGetStudentByIdNegative() {
		log.debug("testGetStudentByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.getStudentById("12345");
		});
		assertEquals("there is no student with this id", exception.getMessage());
	}

	@Test
	public void testGetTeacherByIdPositive() throws Exception {
		log.debug("testGetTeacherByIdPositive[1]: test start");
		dataProviderPostgres.saveTeacher(teacher3);
		log.debug("testGetTeacherByIdPositive[2]: expected teacher: {}", teacher3);
		Teacher actualTeacher = dataProviderPostgres.getTeacherById(teacher3.getTeacherId());
		log.debug("testGetTeacherByIdPositive[3]: actual teacher: {}", actualTeacher);
		assertEquals(teacher3, actualTeacher);
	}

	@Test
	public void testGetTeacherByIdNegative() {
		log.debug("testGetTeacherByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.getTeacherById("12345");
		});
		assertEquals("there is no teacher with this id", exception.getMessage());
	}

	@Test
	public void testGetGroupByIdPositive() throws Exception {
		log.debug("testGetGroupByIdPositive[1]: test start");
//		dataProviderPostgres.saveStudent(student1);
//		dataProviderPostgres.saveStudent(student2);
//		dataProviderPostgres.saveStudent(student3);
		dataProviderPostgres.saveGroup(group);
		log.debug("testGetGroupByIdPositive[2]: expected group: {}", group);
		Group actualGroup = dataProviderPostgres.getGroupById(group.getGroupNumber());
		log.debug("testGetGroupByIdPositive[3]: actual group: {}", actualGroup);
		assertEquals(group, actualGroup);
	}

	@Test
	public void testGetGroupByIdNegative() {
		log.debug("testGetGroupByIdNegative[1]: test start");

		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.getGroupById("12345");
		});
		assertEquals("there is no group with this id", exception.getMessage());
	}

	@Test
	public void testGetSubjectByIdPositive() throws Exception {
		log.debug("testGetSubjectByIdPositive[1]: test start");
		dataProviderPostgres.saveSubject(subject);
		log.debug("testGetSubjectByIdPositive[2]: expected subject: {}", subject);
		Subject actualSubject = dataProviderPostgres.getSubjectById(subject.getSubjectId());
		log.debug("testGetSubjectByIdPositive[3]: actual subject: {}", actualSubject);
		assertEquals(subject, actualSubject);
	}

	@Test
	public void testGetSubjectByIdNegative() {
		log.debug("testGetSubjectByIdNegative[1]: test start");

		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.getSubjectById("12345");
		});
		assertEquals("there is no subject with this id", exception.getMessage());
	}

	@Test
	public void testGetScheduleUnitByIdPositive() throws Exception {
		log.debug("testGetScheduleUnitByIdPositive[1]: test start");
		dataProviderPostgres.saveSubject(subject);
		dataProviderPostgres.saveTeacher(teacher);
		dataProviderPostgres.saveGroup(group);
		dataProviderPostgres.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		log.debug("testGetScheduleUnitByIdPositive[2]: expected schedule unit: {}", scheduleUnit);
		ScheduleUnit actualScheduleUnit = dataProviderPostgres.getScheduleUnitById(scheduleUnit.getScheduleUnitId(), TypeOfSchedule.MAIN);
		log.debug("testGetScheduleUnitByIdPositive[3]: actual schedule unit: {}", actualScheduleUnit);
		assertEquals(scheduleUnit, actualScheduleUnit);
	}

	@Test
	public void testGetScheduleUnitByIdNegative() {
		log.debug("testGetScheduleUnitByIdNegative[1]: test start");

		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderPostgres.getScheduleUnitById("12345", TypeOfSchedule.MAIN);
		});
		assertEquals("there is no scheduleUnit with this id", exception.getMessage());
	}

	@Test
	public void testGetAllStudentsPositive() throws Exception {
		log.debug("testGetAllStudentsPositive[1]: start test");
		List<Student> expectedList = new ArrayList<>();
		expectedList.add(student1);
		expectedList.add(student2);
		dataProviderPostgres.saveStudent(student1);
		dataProviderPostgres.saveStudent(student2);
		List<Student> actualList = dataProviderPostgres.getAllStudents();
		log.debug("testGetAllStudentsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllStudentsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllStudentsNoStudents() {
		log.debug("testGetAllStudentsNoStudents[1]: start test");
		List<Student> expected = new ArrayList<>();
		List<Student> actual = dataProviderPostgres.getAllStudents();
		log.debug("testGetAllStudentsNoStudents[1]: expected: {}", expected);
		log.debug("testGetAllStudentsNoStudents[1]: actual: {}", actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllTeachersPositive() throws Exception {
		log.debug("testGetAllTeachersPositive[1]: start test");
		List<Teacher> expectedList = new ArrayList<>();
		expectedList.add(teacher);
		expectedList.add(teacher2);
		dataProviderPostgres.saveTeacher(teacher);
		dataProviderPostgres.saveTeacher(teacher2);
		List<Teacher> actualList = dataProviderPostgres.getAllTeachers();
		log.debug("testGetAllTeachersPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllTeachersPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllTeachersNoStudents() {
		log.debug("testGetAllTeachersNoStudents[1]: start test");
		List<Teacher> expected = new ArrayList<>();
		List<Teacher> actual = dataProviderPostgres.getAllTeachers();
		log.debug("testGetAllTeachersNoStudents[1]: expected: {}", expected);
		log.debug("testGetAllTeachersNoStudents[1]: actual: {}", actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllGroupsPositive() throws Exception {
		log.debug("testGetAllGroupsPositive[1]: start test");
		List<Group> expectedList = new ArrayList<>();
		expectedList.add(group);
		expectedList.add(group2);
		dataProviderPostgres.saveGroup(group);
		dataProviderPostgres.saveGroup(group2);
		List<Group> actualList = dataProviderPostgres.getAllGroups();
		log.debug("testGetAllGroupsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllGroupsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllGroupsNoGroups() {
		log.debug("testGetAllGroupsNoGroups[1]: start test");
		List<Group> expected = new ArrayList<>();
		List<Group> actual = dataProviderPostgres.getAllGroups();
		log.debug("testGetAllGroupsNoGroups[1]: expected: {}", expected);
		log.debug("testGetAllGroupsNoGroups[1]: actual: {}", actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllSubjectsPositive() throws Exception {
		log.debug("testGetAllSubjectsPositive[1]: start test");
		List<Subject> expectedList = new ArrayList<>();
		expectedList.add(subject);
		expectedList.add(subject2);
		dataProviderPostgres.saveSubject(subject);
		dataProviderPostgres.saveSubject(subject2);
		List<Subject> actualList = dataProviderPostgres.getAllSubjects();
		log.debug("testGetAllSubjectsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllSubjectsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllSubjectsNoSubjects() {
		log.debug("testGetAllSubjectsNoSubjects[1]: start test");
		List<Subject> expected = new ArrayList<>();
		List<Subject> actual = dataProviderPostgres.getAllSubjects();
		log.debug("testGetAllSubjectsNoSubjects[1]: expected: {}", expected);
		log.debug("testGetAllSubjectsNoSubjects[1]: actual: {}", actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllScheduleUnitsPositive() throws Exception {
		log.debug("testGetAllScheduleUnitsPositive[1]: start test");
		List<ScheduleUnit> expectedList = new ArrayList<>();
		dataProviderPostgres.saveSubject(subject);
		dataProviderPostgres.saveTeacher(teacher);
		dataProviderPostgres.saveGroup(group);
		expectedList.add(scheduleUnit);
		dataProviderPostgres.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		List<ScheduleUnit> actualList = dataProviderPostgres.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testGetAllScheduleUnitsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllScheduleUnitsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllScheduleUnitsNoUnits() {
		log.debug("testGetAllScheduleUnitsNoUnits[1]: start test");
		List<ScheduleUnit> expected = new ArrayList<>();
		List<ScheduleUnit> actual = dataProviderPostgres.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testGetAllScheduleUnitsNoUnits[1]: expected: {}", expected);
		log.debug("testGetAllScheduleUnitsNoUnits[1]: actual: {}", actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testDataTransform() throws Exception {
		log.debug("testDataTransform[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderPostgres.dataTransform(file.getPath());
		List<Student> students = dataProviderPostgres.getAllStudents();
		List<Teacher> teachers = dataProviderPostgres.getAllTeachers();
		List<Subject> subjects = dataProviderPostgres.getAllSubjects();
		List<Group> groups = dataProviderPostgres.getAllGroups();
		assertNotNull(students);
		assertNotNull(teachers);
		assertNotNull(subjects);
		assertNotNull(groups);
	}

	@Test
	public void testSaveSchedule() throws Exception {
		log.debug("testSaveSchedule[1]: start test");
		List<ScheduleUnit> scheduleUnits = List.of(scheduleUnit, scheduleUnit2);
		Schedule schedule = new Schedule(TypeOfSchedule.MAIN, scheduleUnits);
		List<Subject> subjects = List.of(subject, subject2);
		List<Teacher> teachers = List.of(teacher, teacher2);
		List<Group> groups = List.of(group, group2);
		dataProviderPostgres.saveEntities(subjects, Constants.SQL_INSERT_SUBJECT_TEST, new SubjectEntityMapper());
		dataProviderPostgres.saveEntities(teachers, Constants.SQL_INSERT_TEACHER_TEST, new TeacherEntityMapper());
		dataProviderPostgres.saveGroups(groups);
		dataProviderPostgres.saveSchedule(schedule);
		List<ScheduleUnit> expected = schedule.getUnits();
		List<ScheduleUnit> actual = dataProviderPostgres.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testSaveSchedule[1]: expected: {}", expected);
		log.debug("testSaveSchedule[1]: actual: {}", actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateMainSchedule() throws Exception {
		log.debug("createTestSchedule[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderPostgres.dataTransform(file.getPath());
//		dataProviderPostgres.saveSubject(testSubjectForSchedule);
//		dataProviderPostgres.saveTeacher(teacherForSchedule);
//		dataProviderPostgres.saveGroup(group);
//		dataProviderPostgres.saveGroup(group2);
//		dataProviderPostgres.saveGroup(group3);
		Schedule mainSchedule = createTestSchedule(dataProviderPostgres);
		log.debug("createTestSchedule[2]: test main schedule: {}", mainSchedule);
		dataProviderPostgres.saveSchedule(mainSchedule);
		assertNotNull(mainSchedule.getUnits());
	}

	@Test
	public void testCreateSchedule() throws Exception {
		log.debug("testCreateSchedule[1]: start test");
//		Schedule schedule = new Schedule(TypeOfSchedule.MAIN, dataProviderXml2.getAllScheduleUnits(TypeOfSchedule.MAIN));
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderPostgres.dataTransform(file.getPath());
		dataProviderPostgres.saveSubject(testSubjectForSchedule);
		dataProviderPostgres.saveTeacher(teacherForSchedule);
		Schedule mainSchedule = createTestSchedule(dataProviderPostgres);
		LocalDate startDate = LocalDate.of(2023, 11, 27);
		LocalDate endDate = LocalDate.of(2023, 12, 15);

		Schedule result = dataProviderPostgres.createSchedule(mainSchedule, startDate, endDate, true, false);
		dataProviderPostgres.saveSchedule(result);
		assertNotNull(result);
		assertNotNull(result.getUnits());
		log.debug("testCreateSchedule[2]: created schedule: {}", result);
	}
}
