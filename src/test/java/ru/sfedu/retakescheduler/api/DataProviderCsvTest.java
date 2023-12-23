package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;
import ru.sfedu.retakescheduler.utils.ExcelUtil;
import ru.sfedu.retakescheduler.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static ru.sfedu.retakescheduler.utils.FileUtil.*;
import static ru.sfedu.retakescheduler.utils.DataUtil.*;
import static ru.sfedu.retakescheduler.utils.CsvUtil.*;

public class DataProviderCsvTest extends BaseTest {

	private static String studentsFile;
	private static String teachersFile;
	private static String groupsFile;
	private static String subjectsFile;
	private static String mainScheduleUnitsFile;
	private static String retakeScheduleUnitsFile;
	private static DataProviderCsv dataProviderCsv1;
	private static DataProviderCsv dataProviderCsv2;
	private static String testPath;

	Student student1 = new Student("Ivanov", "Ivan", "Ivanovich", "ivanov@mail.ru", "53b51af6-04df-4af5-8bdb-499436bc575a", 77.5);
	Student student2 = new Student("Petrov", "Petr", "Petrovich", "petrov@mail.ru", "8bccaa52-ef8c-4b1a-879f-10c6dfea861d", 94.6);
	final Student student3 = new Student("Sidorov", "Sidor", "Sidorovich","sidorov@mail.ru", "1e1d663e-29d4-4599-a2a2-18723e47f560", 88.8);
	Teacher teacher = new Teacher("Васильев", "Иван", "Николаевич", "vasiliev@mail.ru", "teach1", LocalDate.now());
	Teacher teacher2 = new Teacher("Васильев", "Вася", "Николаевич", "vasi@mail.ru", "teach2", LocalDate.now());
	Teacher teacher3 = new Teacher("Васил", "Иван", "Николаевич", "va@mail.ru", "teach3", LocalDate.now());
	Group group = new Group("22ВТ-12.03.01.01-о1", 1, "Бакалавриат", LocalDate.now().with(DayOfWeek.TUESDAY), new ArrayList<>(Arrays.asList(student1, student2, student3)));
	Group group2 = new Group("22ВТ-12.03.01.01-о2", 1, "Бакалавриат", LocalDate.now().with(DayOfWeek.TUESDAY), new ArrayList<>(Arrays.asList(student1, student2, student3)));
	Group group3 = new Group("22ВТ-12.03.01.01-о3", 1, "Бакалавриат", LocalDate.now().with(DayOfWeek.TUESDAY), new ArrayList<>(Arrays.asList(student1, student2, student3)));
	Subject subject = new Subject("q2dw1", "Математика", "Экзамен");
	Subject subject2 = new Subject("q2dw1fddxfd", "Физика", "Экзамен");
	Subject subject3 = new Subject("q2dw1kjb", "История", "Экзамен");

	ScheduleUnit scheduleUnit = new ScheduleUnit("jknkjwndkcjnwkdjcn", LocalDateTime.of(2023, 12, 12, 12, 12), "subjectId", "location", "personId", "groupId");
	ScheduleUnit scheduleUnit2 = new ScheduleUnit("jknkjwndk", LocalDateTime.of(2023, 12, 14, 12, 12), "subjectId", "location", "personId", "groupId");
	ScheduleUnit scheduleUnit3 = new ScheduleUnit("okwokdmwok", LocalDateTime.of(2023, 12, 11, 12, 12), "subjectId", "location", "personId", "groupId");

	private static final Logger log = LogManager.getLogger(DataProviderCsvTest.class);
	@BeforeEach
	public void beforeEach() {
		testPath = Constants.TEST_FOLDER_PATH;
		studentsFile = testPath.concat(Constants.CSV_FOLDER).concat(Constants.STUDENT_FILE).concat(Constants.CSV_FILE_TYPE);
		teachersFile = testPath.concat(Constants.CSV_FOLDER).concat(Constants.TEACHER_FILE).concat(Constants.CSV_FILE_TYPE);
		groupsFile = testPath.concat(Constants.CSV_FOLDER).concat(Constants.GROUP_FILE).concat(Constants.CSV_FILE_TYPE);
		subjectsFile = testPath.concat(Constants.CSV_FOLDER).concat(Constants.SUBJECT_FILE).concat(Constants.CSV_FILE_TYPE);
//		scheduleUnitsFile = testPath.concat(Constants.SCHEDULE_UNIT_FILE).concat(Constants.CSV_FILE_TYPE);
		mainScheduleUnitsFile = testPath.concat(Constants.CSV_FOLDER).concat(Constants.MAIN_SCHEDULE_UNIT_FILE).concat(Constants.CSV_FILE_TYPE);
		retakeScheduleUnitsFile = testPath.concat(Constants.CSV_FOLDER).concat(Constants.RETAKE_SCHEDULE_UNIT_FILE).concat(Constants.CSV_FILE_TYPE);
		dataProviderCsv1 = new DataProviderCsv();
		dataProviderCsv2 = new DataProviderCsv(testPath);
	}

	@AfterAll
	public static void afterAll() {
		deleteFiles();
	}

	@AfterEach
	public void prepareForTest() {
		deleteFiles();
	}

	private static void deleteFiles() {
		deleteFileOrFolder(studentsFile);
		deleteFileOrFolder(groupsFile);
		deleteFileOrFolder(mainScheduleUnitsFile);
		deleteFileOrFolder(retakeScheduleUnitsFile);
		deleteFileOrFolder(subjectsFile);
		deleteFileOrFolder(teachersFile);
	}

	@Test
	public void testSaveStudentPositive() throws Exception {
		log.debug("testSaveStudentPositive[1]: test start");
		log.debug("testSaveStudentPositive[2]: student1: {}", student1);
		log.debug("testSaveStudentPositive[3]: student2: {}", student2);
		log.debug("testSaveStudentPositive[4]: student3: {}", student3);
		dataProviderCsv2.saveStudent(student1);
		dataProviderCsv2.saveStudent(student2);
		dataProviderCsv2.saveStudent(student3);
		assertNotNull(dataProviderCsv2.getStudentById(student1.getStudentId()));
		assertNotNull(dataProviderCsv2.getStudentById(student2.getStudentId()));
		assertNotNull(dataProviderCsv2.getStudentById(student3.getStudentId()));
	}

	@Test
	public void testSaveStudentNegative() throws Exception {
		log.debug("testSaveStudentNegative[1]: test start");
		log.debug("testSaveStudentNegative[2]: student1: {}", student1);
		dataProviderCsv2.saveStudent(student1);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.saveStudent(student1);
		});
		assertEquals("this student already exists", exception.getMessage());
	}

	@Test
	public void testSaveTeacherPositive() throws Exception {
		log.debug("testSaveTeacherPositive[1]: test start");
		log.debug("testSaveTeacherPositive[2]: teacher: {}", teacher);
		dataProviderCsv2.saveTeacher(teacher);
		dataProviderCsv2.saveTeacher(teacher2);
		dataProviderCsv2.saveTeacher(teacher3);
		assertNotNull(dataProviderCsv2.getTeacherById(teacher.getTeacherId()));
		assertNotNull(dataProviderCsv2.getTeacherById(teacher2.getTeacherId()));
		assertNotNull(dataProviderCsv2.getTeacherById(teacher3.getTeacherId()));
	}

	@Test
	public void testSaveTeacherNegative() throws Exception {
		log.debug("testSaveTeacherNegative[1]: test start");
		log.debug("testSaveTeacherNegative[2]: teacher: {}", teacher);
		dataProviderCsv2.saveTeacher(teacher);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.saveTeacher(teacher);
		});
		assertEquals("this teacher already exists", exception.getMessage());
	}

	@Test
	public void testSaveSubjectPositive() throws Exception {
		log.debug("testSaveSubjectPositive[1]: test start");
		log.debug("testSaveSubjectPositive[2]: subject: {}", subject);
		dataProviderCsv2.saveSubject(subject);
		dataProviderCsv2.saveSubject(subject2);
		dataProviderCsv2.saveSubject(subject3);
		assertNotNull(dataProviderCsv2.getSubjectById(subject.getSubjectId()));
		assertNotNull(dataProviderCsv2.getSubjectById(subject2.getSubjectId()));
		assertNotNull(dataProviderCsv2.getSubjectById(subject3.getSubjectId()));
	}

	@Test
	public void testSaveSubjectNegative() throws Exception {
		log.debug("testSaveSubjectNegative[1]: test start");
		log.debug("testSaveSubjectNegative[2]: subject1: {}", subject);
		dataProviderCsv2.saveSubject(subject);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.saveSubject(subject);
		});
		assertEquals("this subject already exists", exception.getMessage());
	}

	@Test
	public void testSaveGroupPositive() throws Exception {
		log.debug("testSaveGroupPositive[1]: test start");
		log.debug("testSaveGroupPositive[2]: group1: {}", group);

		dataProviderCsv2.saveStudent(student1);
		dataProviderCsv2.saveStudent(student2);
		dataProviderCsv2.saveStudent(student3);

		dataProviderCsv2.saveGroup(group);
		dataProviderCsv2.saveGroup(group2);
		dataProviderCsv2.saveGroup(group3);
		assertNotNull(dataProviderCsv2.getGroupById(group.getGroupNumber()));
		assertNotNull(dataProviderCsv2.getGroupById(group2.getGroupNumber()));
		assertNotNull(dataProviderCsv2.getGroupById(group3.getGroupNumber()));
	}

	@Test
	public void testSaveGroupNegative() throws Exception {
		log.debug("testSaveGroupNegative[1]: test start");
		log.debug("testSaveGroupNegative[2]: group1: {}", group);
		dataProviderCsv2.saveStudent(student1);
		dataProviderCsv2.saveStudent(student2);
		dataProviderCsv2.saveStudent(student3);
		dataProviderCsv2.saveGroup(group);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.saveGroup(group);
		});
		assertEquals("this group already exists", exception.getMessage());
	}

	@Test
	public void testSaveScheduleUnitPositive() throws Exception {
		log.debug("testSaveScheduleUnitPositive[1]: test start");
		log.debug("testSaveScheduleUnitPositive[2]: scheduleUnit1: {}", scheduleUnit);
		dataProviderCsv2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		dataProviderCsv2.saveScheduleUnit(scheduleUnit2, TypeOfSchedule.MAIN);
		dataProviderCsv2.saveScheduleUnit(scheduleUnit3, TypeOfSchedule.MAIN);
		assertNotNull(dataProviderCsv2.getScheduleUnitById(scheduleUnit.getScheduleUnitId(), TypeOfSchedule.MAIN));
		assertNotNull(dataProviderCsv2.getScheduleUnitById(scheduleUnit2.getScheduleUnitId(), TypeOfSchedule.MAIN));
		assertNotNull(dataProviderCsv2.getScheduleUnitById(scheduleUnit3.getScheduleUnitId(), TypeOfSchedule.MAIN));
	}

	@Test
	public void testSaveScheduleUnitNegative() throws Exception {
		log.debug("testSaveScheduleUnitNegative[1]: test start");
		log.debug("testSaveScheduleUnitNegative[2]: scheduleUnit1: {}", scheduleUnit);
		dataProviderCsv2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		});
		assertEquals("this scheduleUnit already exists", exception.getMessage());
	}

	@Test
	public void testGetStudentByIdPositive() throws Exception {
		log.debug("testGetStudentByIdPositive[1]: test start");
		dataProviderCsv2.saveStudent(student3);
		log.debug("testGetStudentByIdPositive[2]: expected student: {}", student3);
		Student actualStudent = dataProviderCsv2.getStudentById(student3.getStudentId());
		log.debug("testGetStudentByIdPositive[3]: actual student: {}", actualStudent);
		assertEquals(student3, actualStudent);
	}

	@Test
	public void testGetStudentByIdNegative() {
		log.debug("testGetStudentByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.getStudentById("12345");
		});
		assertEquals("there is no student with this id", exception.getMessage());
	}

	@Test
	public void testGetTeacherByIdPositive() throws Exception {
		log.debug("testGetTeacherByIdPositive[1]: test start");
		dataProviderCsv2.saveTeacher(teacher3);
		log.debug("testGetTeacherByIdPositive[2]: expected teacher: {}", teacher3);
		Teacher actualTeacher = dataProviderCsv2.getTeacherById(teacher3.getTeacherId());
		log.debug("testGetTeacherByIdPositive[3]: actual teacher: {}", actualTeacher);
		assertEquals(teacher3, actualTeacher);
	}

	@Test
	public void testGetTeacherByIdNegative() {
		log.debug("testGetTeacherByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.getTeacherById("12345");
		});
		assertEquals("there is no teacher with this id", exception.getMessage());
	}

	@Test
	public void testGetGroupByIdPositive() throws Exception {
		log.debug("testGetGroupByIdPositive[1]: test start");
		dataProviderCsv2.saveStudent(student1);
		dataProviderCsv2.saveStudent(student2);
		dataProviderCsv2.saveStudent(student3);
		dataProviderCsv2.saveGroup(group);
		log.debug("testGetGroupByIdPositive[2]: expected group: {}", group);
		Group actualGroup = dataProviderCsv2.getGroupById(group.getGroupNumber());
		log.debug("testGetGroupByIdPositive[3]: actual group: {}", actualGroup);
		assertEquals(group, actualGroup);
	}

	@Test
	public void testGetGroupByIdNegative() {
		log.debug("testGetGroupByIdNegative[1]: test start");

		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.getGroupById("12345");
		});
		assertEquals("there is no group with this id", exception.getMessage());
	}

	// Тесты для getSubjectById
	@Test
	public void testGetSubjectByIdPositive() throws Exception {
		log.debug("testGetSubjectByIdPositive[1]: test start");
		dataProviderCsv2.saveSubject(subject);
		log.debug("testGetSubjectByIdPositive[2]: expected subject: {}", subject);
		Subject actualSubject = dataProviderCsv2.getSubjectById(subject.getSubjectId());
		log.debug("testGetSubjectByIdPositive[3]: actual subject: {}", actualSubject);
		assertEquals(subject, actualSubject);
	}

	@Test
	public void testGetSubjectByIdNegative() {
		log.debug("testGetSubjectByIdNegative[1]: test start");

		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.getSubjectById("12345");
		});
		assertEquals("there is no subject with this id", exception.getMessage());
	}

	@Test
	public void testGetScheduleUnitByIdPositive() throws Exception {
		log.debug("testGetScheduleUnitByIdPositive[1]: test start");
		dataProviderCsv2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		log.debug("testGetScheduleUnitByIdPositive[2]: expected schedule unit: {}", scheduleUnit);
		ScheduleUnit actualScheduleUnit = dataProviderCsv2.getScheduleUnitById(scheduleUnit.getScheduleUnitId(), TypeOfSchedule.MAIN);
		log.debug("testGetScheduleUnitByIdPositive[3]: actual schedule unit: {}", actualScheduleUnit);
		assertEquals(scheduleUnit, actualScheduleUnit);
	}

	@Test
	public void testGetScheduleUnitByIdNegative() {
		log.debug("testGetScheduleUnitByIdNegative[1]: test start");

		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.getScheduleUnitById("12345", TypeOfSchedule.MAIN);
		});
		assertEquals("there is no scheduleUnit with this id", exception.getMessage());
	}

//	@Test
//	public void testGetAllRecords() {
//		log.debug("testGetAllRecords[1]: test start");
//		List<Student> res = dataProviderCsv2.getAllRecords(studentsFile, Student.class);
//		for (Student student : res) {
//			log.debug("testGetAllRecords[2] studentLastName = {}", student.getLastName());
//			log.debug("testGetAllRecords[3] studentFirstName = {}", student.getFirstName());
//		}
//		log.debug("testGetAllRecords[4]: students: {}", res);
//	}

	@Test
	public void testDeleteStudentByIdPositive() throws Exception {
		log.debug("testDeleteStudentById[1]: test start");
		List<Student> expectedStudentsAfterDelete = new ArrayList<>();
		expectedStudentsAfterDelete.add(student2);
		expectedStudentsAfterDelete.add(student3);

		dataProviderCsv2.saveStudent(student1);
		dataProviderCsv2.saveStudent(student2);
		dataProviderCsv2.saveStudent(student3);

		List<Student> studentsBeforeDelete = dataProviderCsv2.getAllStudents();
		log.debug("testDeleteStudentById[2]: students before removing: {}", studentsBeforeDelete);
		dataProviderCsv2.deleteStudentById(student1.getStudentId());
		List<Student> studentsAfterDelete = dataProviderCsv2.getAllStudents();
		log.debug("testDeleteStudentById[3]: students after removing: {}", studentsAfterDelete);
		assertEquals(expectedStudentsAfterDelete, studentsAfterDelete);
	}

	@Test
	public void testDeleteStudentByIdNegative() {
		log.debug("testDeleteStudentByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.deleteStudentById("qwertyuio");
		});
		assertEquals("there is no student with this id", exception.getMessage());
	}

	@Test
	public void testDeleteTeacherByIdPositive() throws Exception {
		log.debug("testDeleteTeacherById[1]: test start");
		List<Teacher> expectedTeachersAfterDelete = new ArrayList<>();
		expectedTeachersAfterDelete.add(teacher2);
		expectedTeachersAfterDelete.add(teacher3);

		dataProviderCsv2.saveTeacher(teacher);
		dataProviderCsv2.saveTeacher(teacher2);
		dataProviderCsv2.saveTeacher(teacher3);

		List<Teacher> teachersBeforeDelete = dataProviderCsv2.getAllTeachers();
		log.debug("testDeleteTeacherById[2]: teachers before removing: {}", teachersBeforeDelete);
		dataProviderCsv2.deleteTeacherById(teacher.getTeacherId());
		List<Teacher> teachersAfterDelete = dataProviderCsv2.getAllTeachers();
		log.debug("testDeleteTeacherById[3]: teachers after removing: {}", teachersAfterDelete);
		assertEquals(expectedTeachersAfterDelete, teachersAfterDelete);
	}

	@Test
	public void testDeleteTeacherByIdNegative() {
		log.debug("testDeleteTeacherByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.deleteTeacherById("qwertyuio");
		});
		assertEquals("there is no teacher with this id", exception.getMessage());
	}

	@Test
	public void testDeleteGroupByGroupNumberPositive() throws Exception {
		log.debug("testDeleteGroupByGroupNumber[1]: test start");
		List<Group> expectedGroupsAfterDelete = new ArrayList<>();
		expectedGroupsAfterDelete.add(group3);
		expectedGroupsAfterDelete.add(group2);
		dataProviderCsv2.saveStudent(student1);
		dataProviderCsv2.saveStudent(student2);
		dataProviderCsv2.saveStudent(student3);

		dataProviderCsv2.saveGroup(group);
		dataProviderCsv2.saveGroup(group2);
		dataProviderCsv2.saveGroup(group3);

		List<Group> groupsBeforeDelete = dataProviderCsv2.getAllGroups();
		log.debug("testDeleteGroupByGroupNumber[2]: groups before removing: {}", groupsBeforeDelete);
		dataProviderCsv2.deleteGroupById(group.getGroupNumber());
		List<Group> groupsAfterDelete = dataProviderCsv2.getAllGroups();
		log.debug("testDeleteGroupByGroupNumber[3]: groups after removing: {}", groupsAfterDelete);
		assertEquals(expectedGroupsAfterDelete, groupsAfterDelete);
	}

	@Test
	public void testDeleteGroupByGroupNumberNegative() {
		log.debug("testDeleteGroupByGroupNumberNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.deleteGroupById("qwertyuio");
		});
		assertEquals("there is no group with this groupNumber", exception.getMessage());
	}

	@Test
	public void testDeleteSubjectByIdPositive() throws Exception {
		log.debug("testDeleteSubjectById[1]: test start");
		List<Subject> expectedSubjectsAfterDelete = new ArrayList<>();
		expectedSubjectsAfterDelete.add(subject2);
		expectedSubjectsAfterDelete.add(subject3);

		dataProviderCsv2.saveSubject(subject);
		dataProviderCsv2.saveSubject(subject2);
		dataProviderCsv2.saveSubject(subject3);

		List<Subject> subjectsBeforeDelete = dataProviderCsv2.getAllSubjects();
		log.debug("testDeleteSubjectById[2]: subjects before removing: {}", subjectsBeforeDelete);
		dataProviderCsv2.deleteSubjectById(subject.getSubjectId());
		List<Subject> subjectsAfterDelete = dataProviderCsv2.getAllSubjects();
		log.debug("testDeleteSubjectById[3]: subjects after removing: {}", subjectsAfterDelete);
		assertEquals(expectedSubjectsAfterDelete, subjectsAfterDelete);
	}

	@Test
	public void testDeleteSubjectByIdNegative() {
		log.debug("testDeleteSubjectByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.deleteSubjectById("qwertyuio");
		});
		assertEquals("there is no subject with this id", exception.getMessage());
	}

	@Test
	public void testDeleteScheduleUnitByIdPositive() throws Exception {
		log.debug("testDeleteScheduleUnitById[1]: test start");
		List<ScheduleUnit> expectedScheduleUnitsAfterDelete = new ArrayList<>();
		expectedScheduleUnitsAfterDelete.add(scheduleUnit2);
		expectedScheduleUnitsAfterDelete.add(scheduleUnit3);

		dataProviderCsv2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		dataProviderCsv2.saveScheduleUnit(scheduleUnit2, TypeOfSchedule.MAIN);
		dataProviderCsv2.saveScheduleUnit(scheduleUnit3, TypeOfSchedule.MAIN);

		List<ScheduleUnit> scheduleUnitsBeforeDelete = dataProviderCsv2.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testDeleteScheduleUnitById[2]: schedule units before removing: {}", scheduleUnitsBeforeDelete);
		dataProviderCsv2.deleteScheduleUnitById(scheduleUnit.getScheduleUnitId(), TypeOfSchedule.MAIN);
		List<ScheduleUnit> scheduleUnitsAfterDelete = dataProviderCsv2.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testDeleteScheduleUnitById[3]: schedule units after removing: {}", scheduleUnitsAfterDelete);
		assertEquals(expectedScheduleUnitsAfterDelete, scheduleUnitsAfterDelete);
	}

	@Test
	public void testDeleteScheduleUnitByIdNegative() {
		log.debug("testDeleteScheduleUnitByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderCsv2.deleteScheduleUnitById("qwertyuio", TypeOfSchedule.MAIN);
		});
		assertEquals("there is no schedule unit with this id", exception.getMessage());
	}

	@Test
	public void testGetAllStudentsPositive() throws Exception {
		log.debug("testGetAllStudentsPositive[1]: start test");
		List<Student> expectedList = new ArrayList<>();
		expectedList.add(student1);
		expectedList.add(student2);
		dataProviderCsv2.saveStudent(student1);
		dataProviderCsv2.saveStudent(student2);
		List<Student> actualList = dataProviderCsv2.getAllStudents();
		log.debug("testGetAllStudentsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllStudentsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllStudentsNoStudents() {
		log.debug("testGetAllStudentsNoStudents[1]: start test");
		List<Student> expected = new ArrayList<>();
		List<Student> actual = dataProviderCsv2.getAllStudents();
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
		dataProviderCsv2.saveTeacher(teacher);
		dataProviderCsv2.saveTeacher(teacher2);
		List<Teacher> actualList = dataProviderCsv2.getAllTeachers();
		log.debug("testGetAllTeachersPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllTeachersPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllTeachersNoStudents() {
		log.debug("testGetAllTeachersNoStudents[1]: start test");
		List<Teacher> expected = new ArrayList<>();
		List<Teacher> actual = dataProviderCsv2.getAllTeachers();
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
		dataProviderCsv2.saveStudent(student1);
		dataProviderCsv2.saveStudent(student2);
		dataProviderCsv2.saveStudent(student3);
		dataProviderCsv2.saveGroup(group);
		dataProviderCsv2.saveGroup(group2);
		List<Group> actualList = dataProviderCsv2.getAllGroups();
		log.debug("testGetAllGroupsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllGroupsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllGroupsNoGroups() {
		log.debug("testGetAllGroupsNoGroups[1]: start test");
		List<Group> expected = new ArrayList<>();
		List<Group> actual = dataProviderCsv2.getAllGroups();
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
		dataProviderCsv2.saveSubject(subject);
		dataProviderCsv2.saveSubject(subject2);
		List<Subject> actualList = dataProviderCsv2.getAllSubjects();
		log.debug("testGetAllSubjectsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllSubjectsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllSubjectsNoSubjects() {
		log.debug("testGetAllSubjectsNoSubjects[1]: start test");
		List<Subject> expected = new ArrayList<>();
		List<Subject> actual = dataProviderCsv2.getAllSubjects();
		log.debug("testGetAllSubjectsNoSubjects[1]: expected: {}", expected);
		log.debug("testGetAllSubjectsNoSubjects[1]: actual: {}", actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllScheduleUnitsPositive() throws Exception {
		log.debug("testGetAllScheduleUnitsPositive[1]: start test");
		List<ScheduleUnit> expectedList = new ArrayList<>();
		expectedList.add(scheduleUnit);
		expectedList.add(scheduleUnit2);
		dataProviderCsv2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		dataProviderCsv2.saveScheduleUnit(scheduleUnit2, TypeOfSchedule.MAIN);
		List<ScheduleUnit> actualList = dataProviderCsv2.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testGetAllScheduleUnitsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllScheduleUnitsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllScheduleUnitsNoUnits() {
		log.debug("testGetAllScheduleUnitsNoUnits[1]: start test");
		List<ScheduleUnit> expected = new ArrayList<>();
		List<ScheduleUnit> actual = dataProviderCsv2.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testGetAllScheduleUnitsNoUnits[1]: expected: {}", expected);
		log.debug("testGetAllScheduleUnitsNoUnits[1]: actual: {}", actual);
		assertEquals(expected, actual);
	}

//	@Test
//	public void testDeleteObject() {
//		log.debug("testDeleteObject[1]: test start");
//		List<Student> studentsBeforeDelete = dataProviderCsv2.getAllStudents();
//		log.debug("testDeleteObject[2]: students before removing: {}", studentsBeforeDelete);
//		dataProviderCsv2.deleteObject(student1, studentsFile);
//		List<Student> studentsAfterDelete = dataProviderCsv2.getAllStudents();
//
//		List<Student> expectedStudentsAfterDelete = new ArrayList<>();
//		expectedStudentsAfterDelete.add(student2);
//		expectedStudentsAfterDelete.add(student3);
//		assertEquals(expectedStudentsAfterDelete, studentsAfterDelete);
//
//		log.debug("testDeleteStudents[3]: students after removing: {}", studentsAfterDelete);
//	}

//	@Test
//	public void testDataLoading() throws IOException {
//		log.debug("testDataLoading[1]: start test");
//		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
//		File file = files.get(0);
//		List<List<?>> result = dataProviderCsv2.dataLoading(file.getPath());
//		log.debug("testDataLoading[2]: list after loading: {}", result);
//	}

//	@Test
//	public void testDeleteStudentById() {
//		log.debug("testDeleteStudentById[1]: start test");
//		List<Student> expectedStudents = Arrays.asList(student2, student3);
//		log.debug("testDeleteStudentById[2]: delete student: {}", student1);
////		dataProviderCsv2.deleteStudentById(student1.getStudentId());
//		List<Student> actualStudents = dataProviderCsv2.getAllStudents();
//		assertEquals(expectedStudents, actualStudents);
//		log.debug("testDeleteStudentById[3]: test succeeded");
//	}

//	@Test
//	public void testValidation() {
//		log.debug("testValidation[1]: start test");
//		List<Student> students = Arrays.asList(student1);
//
//		HashMap<Object, HashMap<String, String>> result = dataProviderCsv2.validation(subject);
//		log.debug("testValidation[2]: result of validation: {}", result);
//	}

	@Test
	public void testDataTransform() throws Exception {
		log.debug("testDataTransform[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderCsv2.dataTransform(file.getPath());
		List<Student> students = dataProviderCsv2.getAllStudents();
		List<Teacher> teachers = dataProviderCsv2.getAllTeachers();
		List<Subject> subjects = dataProviderCsv2.getAllSubjects();
		List<Group> groups = dataProviderCsv2.getAllGroups();
		assertNotNull(students);
		assertNotNull(teachers);
		assertNotNull(subjects);
		assertNotNull(groups);
	}

//	@Test
//	public void testGetObjectFields() {
//		log.debug("testGetObjectFields[1]: start test");
//		String[] fields1 = dataProviderCsv2.getObjectFields(subject);
//		String[] fields2 = dataProviderCsv2.getObjectFields(student1);
//		for (String field: fields1) {
//			log.debug("testGetObjectFields[2]: fieldsSubject: {}", field);
//		}
//		for (String field: fields2) {
//			log.debug("testGetObjectFields[3]: fieldsStudent: {}", field);
//		}
//	}

//	@Test
//	public void testGetAllGroups() {
//		log.debug("testGetAllGroups[1]: start test");
//		List<Group> groups = dataProviderCsv2.getAllGroups();
//		log.debug("testGetAllGroups[2]: groups: {}", groups);
//	}

//	@Test
//	public void testGetAllTeachers() throws Exception {
//		log.debug("testGetAllTeachers[1]: start test");
//		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
//		File file = files.get(0);
//		dataProviderCsv2.dataTransform(file.getPath());
//		List<Teacher> teachers = dataProviderCsv2.getAllTeachers();
//		log.debug("testGetAllTeachers[2]: teachers: {}", teachers);
//	}
//
//	@Test
//	public void testGetAllGroups() {
//		log.debug("testGetAllGroups[1]: start test");
//		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
//		File file = files.get(0);
////		dataProviderCsv2.dataTransform(file.getPath());
//		List<Group> groups = dataProviderCsv2.getAllGroups();
//		log.debug("testGetAllGroups[2]: groups: {}\ncount of groups: {}", groups, groups.size());
//	}
//
	@Test
	public void testCreateMainSchedule() throws Exception {
		log.debug("createTestSchedule[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderCsv2.dataTransform(file.getPath());
		Schedule mainSchedule = createTestSchedule(dataProviderCsv2);
		log.debug("createTestSchedule[2]: test main schedule: {}", mainSchedule);
//		saveRecords(mainSchedule.getUnits(), mainScheduleUnitsFile, ScheduleUnit.class, getObjectFields(new ScheduleUnit()));
		dataProviderCsv2.saveSchedule(mainSchedule);
		assertNotNull(mainSchedule.getUnits());
	}

	@Test
	public void testCreateSchedule() throws Exception {
		log.debug("testCreateSchedule[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderCsv2.dataTransform(file.getPath());
		Schedule mainSchedule = createTestSchedule(dataProviderCsv2);
//		Schedule schedule = new Schedule(TypeOfSchedule.MAIN, dataProviderCsv2.getAllScheduleUnits(TypeOfSchedule.MAIN));
		LocalDate startDate = LocalDate.of(2023, 11, 27);
		LocalDate endDate = LocalDate.of(2023, 12, 15);

		Schedule result = dataProviderCsv2.createSchedule(mainSchedule, startDate, endDate, false, false);
//		saveRecords(result.getUnits(), retakeScheduleUnitsFile, ScheduleUnit.class, getObjectFields(new ScheduleUnit()));
		dataProviderCsv2.saveSchedule(result);
		log.debug("testCreateSchedule[2]: created schedule: {}", result);
		assertNotNull(result);
	}
//
//	@Test
//	public void testGetAllScheduleUnits() {
//		log.debug("testGetAllScheduleUnits[1]: start test");
//		List<ScheduleUnit> scheduleUnits = dataProviderCsv2.getAllScheduleUnits(TypeOfSchedule.MAIN);
//		log.debug("testGetAllScheduleUnits[2]: list of schedule units: {}", scheduleUnits);
//	}

//	@Test
//	public void testSendEmail() {
//		log.debug("testSendEmail[1]: start test");
//		Schedule schedule = new Schedule(TypeOfSchedule.MAIN, new ArrayList<>());
//		List<Student> students = dataProviderCsv2.getAllStudents();
//		dataProviderCsv2.sendEmail(schedule, students);
//		log.debug("testSendEmail[2]: finish test");
//	}
//
//	@Test
//	public void testExportInExcel() throws Exception {
//		log.debug("testExportInExcelFormat[1]: start test");
//		List<ScheduleUnit> retakes = dataProviderCsv2.getAllScheduleUnits(TypeOfSchedule.RETAKE);
//		Schedule schedule = new Schedule(TypeOfSchedule.RETAKE, retakes);
//		dataProviderCsv2.exportInExcelFormat(schedule, Constants.OUTPUT_FOLDER_PATH.concat(Constants.EXCEL_RETAKE_SCHEDULE_FILE));
//	}
}



