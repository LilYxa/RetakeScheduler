package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;
import ru.sfedu.retakescheduler.utils.FileUtil;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.sfedu.retakescheduler.utils.ScheduleUtil.createTestSchedule;
import static ru.sfedu.retakescheduler.utils.XmlUtil.*;
import static ru.sfedu.retakescheduler.utils.FileUtil.deleteFileOrFolder;

public class DataProviderXmlTest extends BaseTest {
	private static String studentsFile;
	private static String teachersFile;
	private static String groupsFile;
	private static String subjectsFile;
	private static String mainScheduleUnitsFile;
	private static String retakeScheduleUnitsFile;
	private static DataProviderXml dataProviderXml1;
	private static DataProviderXml dataProviderXml2;
	private static String testPath;
	private static final Logger log = LogManager.getLogger(DataProviderXmlTest.class);

	@BeforeEach
	public void beforeEach() {
		testPath = Constants.TEST_FOLDER_PATH;
		studentsFile = testPath.concat(Constants.XML_FOLDER).concat(Constants.STUDENT_FILE).concat(Constants.XML_FILE_TYPE);
		teachersFile = testPath.concat(Constants.XML_FOLDER).concat(Constants.TEACHER_FILE).concat(Constants.XML_FILE_TYPE);
		groupsFile = testPath.concat(Constants.XML_FOLDER).concat(Constants.GROUP_FILE).concat(Constants.XML_FILE_TYPE);
		subjectsFile = testPath.concat(Constants.XML_FOLDER).concat(Constants.SUBJECT_FILE).concat(Constants.XML_FILE_TYPE);
//		scheduleUnitsFile = testPath.concat(Constants.SCHEDULE_UNIT_FILE).concat(Constants.XML_FILE_TYPE);
		mainScheduleUnitsFile = testPath.concat(Constants.XML_FOLDER).concat(Constants.MAIN_SCHEDULE_UNIT_FILE).concat(Constants.XML_FILE_TYPE);
		retakeScheduleUnitsFile = testPath.concat(Constants.XML_FOLDER).concat(Constants.RETAKE_SCHEDULE_UNIT_FILE).concat(Constants.XML_FILE_TYPE);
		dataProviderXml1 = new DataProviderXml();
		dataProviderXml2 = new DataProviderXml(testPath);
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
		dataProviderXml2.saveStudent(student1);
		dataProviderXml2.saveStudent(student2);
		dataProviderXml2.saveStudent(student3);
		assertNotNull(dataProviderXml2.getStudentById(student1.getStudentId()));
		assertNotNull(dataProviderXml2.getStudentById(student2.getStudentId()));
		assertNotNull(dataProviderXml2.getStudentById(student3.getStudentId()));
	}

	@Test
	public void testSaveStudentNegative() throws Exception {
		log.debug("testSaveStudentNegative[1]: test start");
		log.debug("testSaveStudentNegative[2]: student1: {}", student1);
		dataProviderXml2.saveStudent(student1);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.saveStudent(student1);
		});
		assertEquals("this student already exists", exception.getMessage());
	}

	@Test
	public void testSaveTeacherPositive() throws Exception {
		log.debug("testSaveTeacherPositive[1]: test start");
		log.debug("testSaveTeacherPositive[2]: teacher: {}", teacher);
		dataProviderXml2.saveTeacher(teacher);
		dataProviderXml2.saveTeacher(teacher2);
		dataProviderXml2.saveTeacher(teacher3);
		assertNotNull(dataProviderXml2.getTeacherById(teacher.getTeacherId()));
		assertNotNull(dataProviderXml2.getTeacherById(teacher2.getTeacherId()));
		assertNotNull(dataProviderXml2.getTeacherById(teacher3.getTeacherId()));
	}

	@Test
	public void testSaveTeacherNegative() throws Exception {
		log.debug("testSaveTeacherNegative[1]: test start");
		log.debug("testSaveTeacherNegative[2]: teacher: {}", teacher);
		dataProviderXml2.saveTeacher(teacher);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.saveTeacher(teacher);
		});
		assertEquals("this teacher already exists", exception.getMessage());
	}

	@Test
	public void testSaveGroupPositive() throws Exception {
		log.debug("testSaveGroupPositive[1]: test start");
		log.debug("testSaveGroupPositive[2]: group1: {}", group);

		dataProviderXml2.saveStudent(student1);
		dataProviderXml2.saveStudent(student2);
		dataProviderXml2.saveStudent(student3);

		dataProviderXml2.saveGroup(group);
		dataProviderXml2.saveGroup(group2);
		dataProviderXml2.saveGroup(group3);
		assertNotNull(dataProviderXml2.getGroupById(group.getGroupNumber()));
		assertNotNull(dataProviderXml2.getGroupById(group2.getGroupNumber()));
		assertNotNull(dataProviderXml2.getGroupById(group3.getGroupNumber()));
	}

	@Test
	public void testSaveGroupNegative() throws Exception {
		log.debug("testSaveGroupNegative[1]: test start");
		log.debug("testSaveGroupNegative[2]: group1: {}", group);
		dataProviderXml2.saveStudent(student1);
		dataProviderXml2.saveStudent(student2);
		dataProviderXml2.saveStudent(student3);
		dataProviderXml2.saveGroup(group);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.saveGroup(group);
		});
		assertEquals("this group already exists", exception.getMessage());
	}

	@Test
	public void testSaveScheduleUnitPositive() throws Exception {
		log.debug("testSaveScheduleUnitPositive[1]: test start");
		log.debug("testSaveScheduleUnitPositive[2]: scheduleUnit1: {}", scheduleUnit);
		dataProviderXml2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		dataProviderXml2.saveScheduleUnit(scheduleUnit2, TypeOfSchedule.MAIN);
		dataProviderXml2.saveScheduleUnit(scheduleUnit3, TypeOfSchedule.MAIN);
		assertNotNull(dataProviderXml2.getScheduleUnitById(scheduleUnit.getScheduleUnitId(), TypeOfSchedule.MAIN));
		assertNotNull(dataProviderXml2.getScheduleUnitById(scheduleUnit2.getScheduleUnitId(), TypeOfSchedule.MAIN));
		assertNotNull(dataProviderXml2.getScheduleUnitById(scheduleUnit3.getScheduleUnitId(), TypeOfSchedule.MAIN));
	}

	@Test
	public void testSaveScheduleUnitNegative() throws Exception {
		log.debug("testSaveScheduleUnitNegative[1]: test start");
		log.debug("testSaveScheduleUnitNegative[2]: scheduleUnit1: {}", scheduleUnit);
		dataProviderXml2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		});
		assertEquals("this scheduleUnit already exists", exception.getMessage());
	}

	@Test
	public void testSaveSubjectPositive() throws Exception {
		log.debug("testSaveSubjectPositive[1]: test start");
		log.debug("testSaveSubjectPositive[2]: subject: {}", subject);
		dataProviderXml2.saveSubject(subject);
		dataProviderXml2.saveSubject(subject2);
		dataProviderXml2.saveSubject(subject3);
		assertNotNull(dataProviderXml2.getSubjectById(subject.getSubjectId()));
		assertNotNull(dataProviderXml2.getSubjectById(subject2.getSubjectId()));
		assertNotNull(dataProviderXml2.getSubjectById(subject3.getSubjectId()));
	}

	@Test
	public void testSaveSubjectNegative() throws Exception {
		log.debug("testSaveSubjectNegative[1]: test start");
		log.debug("testSaveSubjectNegative[2]: subject1: {}", subject);
		dataProviderXml2.saveSubject(subject);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.saveSubject(subject);
		});
		assertEquals("this subject already exists", exception.getMessage());
	}

	@Test
	public void testSaveSchedulePositive() throws Exception {
		log.debug("testSaveSchedulePositive[1]: start test");
		Schedule schedule = new Schedule(TypeOfSchedule.MAIN, List.of(scheduleUnit, scheduleUnit2, scheduleUnit3));
		log.debug("testSaveSchedulePositive[2]: schedule: {}", schedule);
		dataProviderXml2.saveSchedule(schedule);
		assertNotNull(dataProviderXml2.getAllScheduleUnits(TypeOfSchedule.MAIN));
	}

	@Test
	public void testSaveScheduleEmpty() throws Exception {
		log.debug("testSaveScheduleEmpty[1]: start test");
		Schedule schedule = new Schedule(TypeOfSchedule.MAIN);
		log.debug("testSaveScheduleEmpty[2]: schedule: {}", schedule);
		assertThrows(NullPointerException.class, () -> {
			dataProviderXml2.saveSchedule(schedule);
		});
	}

	@Test
	public void testDeleteStudentByIdPositive() throws Exception {
		log.debug("testDeleteStudentById[1]: test start");
		List<Student> expectedStudentsAfterDelete = new ArrayList<>();
		expectedStudentsAfterDelete.add(student2);
		expectedStudentsAfterDelete.add(student3);

		dataProviderXml2.saveStudent(student1);
		dataProviderXml2.saveStudent(student2);
		dataProviderXml2.saveStudent(student3);

		List<Student> studentsBeforeDelete = dataProviderXml2.getAllStudents();
		log.debug("testDeleteStudentById[2]: students before removing: {}", studentsBeforeDelete);
		dataProviderXml2.deleteStudentById(student1.getStudentId());
		List<Student> studentsAfterDelete = dataProviderXml2.getAllStudents();
		log.debug("testDeleteStudentById[3]: students after removing: {}", studentsAfterDelete);
		assertEquals(expectedStudentsAfterDelete, studentsAfterDelete);
	}

	@Test
	public void testDeleteStudentByIdNegative() {
		log.debug("testDeleteStudentByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.deleteStudentById("qwertyuio");
		});
		assertEquals("there is no student with this id", exception.getMessage());
	}

	@Test
	public void testDeleteTeacherByIdPositive() throws Exception {
		log.debug("testDeleteTeacherById[1]: test start");
		List<Teacher> expectedTeachersAfterDelete = new ArrayList<>();
		expectedTeachersAfterDelete.add(teacher2);
		expectedTeachersAfterDelete.add(teacher3);

		dataProviderXml2.saveTeacher(teacher);
		dataProviderXml2.saveTeacher(teacher2);
		dataProviderXml2.saveTeacher(teacher3);

		List<Teacher> teachersBeforeDelete = dataProviderXml2.getAllTeachers();
		log.debug("testDeleteTeacherById[2]: teachers before removing: {}", teachersBeforeDelete);
		dataProviderXml2.deleteTeacherById(teacher.getTeacherId());
		List<Teacher> teachersAfterDelete = dataProviderXml2.getAllTeachers();
		log.debug("testDeleteTeacherById[3]: teachers after removing: {}", teachersAfterDelete);
		assertEquals(expectedTeachersAfterDelete, teachersAfterDelete);
	}

	@Test
	public void testDeleteTeacherByIdNegative() {
		log.debug("testDeleteTeacherByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.deleteTeacherById("qwertyuio");
		});
		assertEquals("there is no teacher with this id", exception.getMessage());
	}

	@Test
	public void testDeleteGroupByGroupNumberPositive() throws Exception {
		log.debug("testDeleteGroupByGroupNumber[1]: test start");
		List<Group> expectedGroupsAfterDelete = new ArrayList<>();
		expectedGroupsAfterDelete.add(group2);
		expectedGroupsAfterDelete.add(group3);

		dataProviderXml2.saveGroup(group);
		dataProviderXml2.saveGroup(group2);
		dataProviderXml2.saveGroup(group3);

		List<Group> groupsBeforeDelete = dataProviderXml2.getAllGroups();
		log.debug("testDeleteGroupByGroupNumber[2]: groups before removing: {}", groupsBeforeDelete);
		dataProviderXml2.deleteGroupById(group.getGroupNumber());
		List<Group> groupsAfterDelete = dataProviderXml2.getAllGroups();
		log.debug("testDeleteGroupByGroupNumber[3]: groups after removing: {}", groupsAfterDelete);
		assertEquals(expectedGroupsAfterDelete, groupsAfterDelete);
	}

	@Test
	public void testDeleteGroupByGroupNumberNegative() {
		log.debug("testDeleteGroupByGroupNumberNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.deleteGroupById("qwertyuio");
		});
		assertEquals("there is no group with this id", exception.getMessage());
	}

	@Test
	public void testDeleteSubjectByIdPositive() throws Exception {
		log.debug("testDeleteSubjectById[1]: test start");
		List<Subject> expectedSubjectsAfterDelete = new ArrayList<>();
		expectedSubjectsAfterDelete.add(subject2);
		expectedSubjectsAfterDelete.add(subject3);

		dataProviderXml2.saveSubject(subject);
		dataProviderXml2.saveSubject(subject2);
		dataProviderXml2.saveSubject(subject3);

		List<Subject> subjectsBeforeDelete = dataProviderXml2.getAllSubjects();
		log.debug("testDeleteSubjectById[2]: subjects before removing: {}", subjectsBeforeDelete);
		dataProviderXml2.deleteSubjectById(subject.getSubjectId());
		List<Subject> subjectsAfterDelete = dataProviderXml2.getAllSubjects();
		log.debug("testDeleteSubjectById[3]: subjects after removing: {}", subjectsAfterDelete);
		assertEquals(expectedSubjectsAfterDelete, subjectsAfterDelete);
	}

	@Test
	public void testDeleteSubjectByIdNegative() {
		log.debug("testDeleteSubjectByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.deleteSubjectById("qwertyuio");
		});
		assertEquals("there is no subject with this id", exception.getMessage());
	}

	@Test
	public void testDeleteScheduleUnitByIdPositive() throws Exception {
		log.debug("testDeleteScheduleUnitById[1]: test start");
		List<ScheduleUnit> expectedScheduleUnitsAfterDelete = new ArrayList<>();
		expectedScheduleUnitsAfterDelete.add(scheduleUnit2);
		expectedScheduleUnitsAfterDelete.add(scheduleUnit3);

		dataProviderXml2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		dataProviderXml2.saveScheduleUnit(scheduleUnit2, TypeOfSchedule.MAIN);
		dataProviderXml2.saveScheduleUnit(scheduleUnit3, TypeOfSchedule.MAIN);

		List<ScheduleUnit> scheduleUnitsBeforeDelete = dataProviderXml2.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testDeleteScheduleUnitById[2]: schedule units before removing: {}", scheduleUnitsBeforeDelete);
		dataProviderXml2.deleteScheduleUnitById(scheduleUnit.getScheduleUnitId(), TypeOfSchedule.MAIN);
		List<ScheduleUnit> scheduleUnitsAfterDelete = dataProviderXml2.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testDeleteScheduleUnitById[3]: schedule units after removing: {}", scheduleUnitsAfterDelete);
		assertEquals(expectedScheduleUnitsAfterDelete, scheduleUnitsAfterDelete);
	}

	@Test
	public void testDeleteScheduleUnitByIdNegative() {
		log.debug("testDeleteScheduleUnitByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.deleteScheduleUnitById("qwertyuio", TypeOfSchedule.MAIN);
		});
		assertEquals("there is no schedule unit with this id", exception.getMessage());
	}

	@Test
	public void testGetStudentByIdPositive() throws Exception {
		log.debug("testGetStudentByIdPositive[1]: test start");
		dataProviderXml2.saveStudent(student3);
		log.debug("testGetStudentByIdPositive[2]: expected student: {}", student3);
		Student actualStudent = dataProviderXml2.getStudentById(student3.getStudentId());
		log.debug("testGetStudentByIdPositive[3]: actual student: {}", actualStudent);
		assertEquals(student3, actualStudent);
	}

	@Test
	public void testGetStudentByIdNegative() {
		log.debug("testGetStudentByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.getStudentById("12345");
		});
		assertEquals("there is no student with this id", exception.getMessage());
	}

	@Test
	public void testGetTeacherByIdPositive() throws Exception {
		log.debug("testGetTeacherByIdPositive[1]: test start");
		dataProviderXml2.saveTeacher(teacher3);
		log.debug("testGetTeacherByIdPositive[2]: expected teacher: {}", teacher3);
		Teacher actualTeacher = dataProviderXml2.getTeacherById(teacher3.getTeacherId());
		log.debug("testGetTeacherByIdPositive[3]: actual teacher: {}", actualTeacher);
		assertEquals(teacher3, actualTeacher);
	}

	@Test
	public void testGetTeacherByIdNegative() {
		log.debug("testGetTeacherByIdNegative[1]: test start");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.getTeacherById("12345");
		});
		assertEquals("there is no teacher with this id", exception.getMessage());
	}

	@Test
	public void testGetGroupByIdPositive() throws Exception {
		log.debug("testGetGroupByIdPositive[1]: test start");
		dataProviderXml2.saveStudent(student1);
		dataProviderXml2.saveStudent(student2);
		dataProviderXml2.saveStudent(student3);
		dataProviderXml2.saveGroup(group);
		log.debug("testGetGroupByIdPositive[2]: expected group: {}", group);
		Group actualGroup = dataProviderXml2.getGroupById(group.getGroupNumber());
		log.debug("testGetGroupByIdPositive[3]: actual group: {}", actualGroup);
		assertEquals(group, actualGroup);
	}

	@Test
	public void testGetGroupByIdNegative() {
		log.debug("testGetGroupByIdNegative[1]: test start");

		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.getGroupById("12345");
		});
		assertEquals("there is no group with this id", exception.getMessage());
	}

	@Test
	public void testGetSubjectByIdPositive() throws Exception {
		log.debug("testGetSubjectByIdPositive[1]: test start");
		dataProviderXml2.saveSubject(subject);
		log.debug("testGetSubjectByIdPositive[2]: expected subject: {}", subject);
		Subject actualSubject = dataProviderXml2.getSubjectById(subject.getSubjectId());
		log.debug("testGetSubjectByIdPositive[3]: actual subject: {}", actualSubject);
		assertEquals(subject, actualSubject);
	}

	@Test
	public void testGetSubjectByIdNegative() {
		log.debug("testGetSubjectByIdNegative[1]: test start");

		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.getSubjectById("12345");
		});
		assertEquals("there is no subject with this id", exception.getMessage());
	}

	@Test
	public void testGetScheduleUnitByIdPositive() throws Exception {
		log.debug("testGetScheduleUnitByIdPositive[1]: test start");
		dataProviderXml2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		log.debug("testGetScheduleUnitByIdPositive[2]: expected schedule unit: {}", scheduleUnit);
		ScheduleUnit actualScheduleUnit = dataProviderXml2.getScheduleUnitById(scheduleUnit.getScheduleUnitId(), TypeOfSchedule.MAIN);
		log.debug("testGetScheduleUnitByIdPositive[3]: actual schedule unit: {}", actualScheduleUnit);
		assertEquals(scheduleUnit, actualScheduleUnit);
	}

	@Test
	public void testGetScheduleUnitByIdNegative() {
		log.debug("testGetScheduleUnitByIdNegative[1]: test start");

		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.getScheduleUnitById("12345", TypeOfSchedule.MAIN);
		});
		assertEquals("there is no scheduleUnit with this id", exception.getMessage());
	}

	@Test
	public void testDataTransform() throws Exception {
		log.debug("testDataTransform[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderXml2.dataTransform(file.getPath());
		List<Student> students = dataProviderXml2.getAllStudents();
		List<Teacher> teachers = dataProviderXml2.getAllTeachers();
		List<Subject> subjects = dataProviderXml2.getAllSubjects();
		List<Group> groups = dataProviderXml2.getAllGroups();
		assertNotNull(students);
		assertNotNull(teachers);
		assertNotNull(subjects);
		assertNotNull(groups);
	}

	@Test
	public void testDataTransformNegative() {
		log.debug("testDataTransformNegative[1]: start test");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.dataTransform("a non-existent path");
		});
		assertEquals("there is no file", exception.getMessage());
	}

	@Test
	public void testCreateMainSchedule() throws Exception {
		log.debug("createTestSchedule[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderXml2.dataTransform(file.getPath());
		Schedule mainSchedule = createTestSchedule(dataProviderXml2);
		log.debug("createTestSchedule[2]: test main schedule: {}", mainSchedule);
//		saveRecords(mainSchedule.getUnits(), mainScheduleUnitsFile, ScheduleUnit.class);
		dataProviderXml2.saveSchedule(mainSchedule);
		assertNotNull(mainSchedule.getUnits());
	}

	@Test
	public void testCreateSchedule() throws Exception {
		log.debug("testCreateSchedule[1]: start test");
//		Schedule schedule = new Schedule(TypeOfSchedule.MAIN, dataProviderXml2.getAllScheduleUnits(TypeOfSchedule.MAIN));
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderXml2.dataTransform(file.getPath());
		Schedule mainSchedule = createTestSchedule(dataProviderXml2);
		LocalDate startDate = LocalDate.of(2023, 11, 27);
		LocalDate endDate = LocalDate.of(2023, 12, 15);

		Schedule result = dataProviderXml2.createSchedule(mainSchedule, startDate, endDate, false, false);
//		saveRecords(result.getUnits(), retakeScheduleUnitsFile, ScheduleUnit.class);
		dataProviderXml2.saveSchedule(result);
		assertNotNull(result);
		log.debug("testCreateSchedule[2]: created schedule: {}", result);
	}

	@Test
	public void testCreateScheduleEmptyMainSchedule() throws Exception {
		log.debug("testCreateScheduleEmptyMainSchedule[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		dataProviderXml2.dataTransform(file.getPath());
		Schedule main = new Schedule(TypeOfSchedule.MAIN);
		LocalDate startDate = LocalDate.of(2023, 11, 27);
		LocalDate endDate = LocalDate.of(2023, 12, 15);
		Exception exception = assertThrows(NullPointerException.class, () -> {
			Schedule retake = dataProviderXml2.createSchedule(main, startDate, endDate, false, false);
		});
	}

	@Test
	public void testDataLoadingPositive() throws Exception {
		log.debug("testDataLoadingPositive[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		List<ExcelRow> result = dataProviderXml2.dataLoading(file.getPath());
		log.debug("testDataLoadingPositive[2]: list after loading: {}", result);
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@Test
	public void testDataLoadingNegative() {
		log.debug("testDataLoadingNegative[1]: start test");
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.dataLoading("non-existing file");
		});
		assertEquals("there is no file", exception.getMessage());
	}

	@Test
	public void testCheckEntityExistenceIfExist() throws Exception {
		log.debug("testCheckEntityExistenceIfExist[1]: start test");
		List<Student> students = List.of(student1, student2, student3);
		log.debug("testCheckEntityExistenceIfExist[1]: students: {}, searched student: {}", students, student1);
		Exception exception = assertThrows(Exception.class, () -> {
			dataProviderXml2.checkIfEntityExist(students, student1, "this student already exist");
		});
		assertEquals("this student already exist", exception.getMessage());
	}

	@Test
	public void testCheckEntityExistenceIfNotExist() throws Exception {
		log.debug("testCheckEntityExistenceIfNotExist[1]: start test");
		List<Student> students = List.of(student2, student3);
		log.debug("testCheckEntityExistenceIfNotExist[1]: students: {}, searched student: {}", students, student1);
		try {
			dataProviderXml2.checkIfEntityExist(students, student1, "this student already exist");
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetAllStudentsPositive() throws Exception {
		log.debug("testGetAllStudentsPositive[1]: start test");
		List<Student> expectedList = new ArrayList<>();
		expectedList.add(student1);
		expectedList.add(student2);
		dataProviderXml2.saveStudent(student1);
		dataProviderXml2.saveStudent(student2);
		List<Student> actualList = dataProviderXml2.getAllStudents();
		log.debug("testGetAllStudentsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllStudentsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllStudentsNoStudents() {
		log.debug("testGetAllStudentsNoStudents[1]: start test");
		List<Student> expected = new ArrayList<>();
		List<Student> actual = dataProviderXml2.getAllStudents();
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
		dataProviderXml2.saveTeacher(teacher);
		dataProviderXml2.saveTeacher(teacher2);
		List<Teacher> actualList = dataProviderXml2.getAllTeachers();
		log.debug("testGetAllTeachersPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllTeachersPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllTeachersNoStudents() {
		log.debug("testGetAllTeachersNoStudents[1]: start test");
		List<Teacher> expected = new ArrayList<>();
		List<Teacher> actual = dataProviderXml2.getAllTeachers();
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
		dataProviderXml2.saveStudent(student1);
		dataProviderXml2.saveStudent(student2);
		dataProviderXml2.saveStudent(student3);
		dataProviderXml2.saveGroup(group);
		dataProviderXml2.saveGroup(group2);
		List<Group> actualList = dataProviderXml2.getAllGroups();
		log.debug("testGetAllGroupsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllGroupsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllGroupsNoGroups() {
		log.debug("testGetAllGroupsNoGroups[1]: start test");
		List<Group> expected = new ArrayList<>();
		List<Group> actual = dataProviderXml2.getAllGroups();
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
		dataProviderXml2.saveSubject(subject);
		dataProviderXml2.saveSubject(subject2);
		List<Subject> actualList = dataProviderXml2.getAllSubjects();
		log.debug("testGetAllSubjectsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllSubjectsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllSubjectsNoSubjects() {
		log.debug("testGetAllSubjectsNoSubjects[1]: start test");
		List<Subject> expected = new ArrayList<>();
		List<Subject> actual = dataProviderXml2.getAllSubjects();
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
		dataProviderXml2.saveScheduleUnit(scheduleUnit, TypeOfSchedule.MAIN);
		dataProviderXml2.saveScheduleUnit(scheduleUnit2, TypeOfSchedule.MAIN);
		List<ScheduleUnit> actualList = dataProviderXml2.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testGetAllScheduleUnitsPositive[1]: expected: {}", expectedList);
		log.debug("testGetAllScheduleUnitsPositive[1]: actual: {}", actualList);
		assertEquals(expectedList, actualList);
	}

	@Test
	public void testGetAllScheduleUnitsNoUnits() {
		log.debug("testGetAllScheduleUnitsNoUnits[1]: start test");
		List<ScheduleUnit> expected = new ArrayList<>();
		List<ScheduleUnit> actual = dataProviderXml2.getAllScheduleUnits(TypeOfSchedule.MAIN);
		log.debug("testGetAllScheduleUnitsNoUnits[1]: expected: {}", expected);
		log.debug("testGetAllScheduleUnitsNoUnits[1]: actual: {}", actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllRecords() {
		log.debug("testGetAllRecords[1]: start test");
		List<Student> students = getAllRecords(studentsFile, Student.class);
		List<Group> groups = getAllRecords(groupsFile, Group.class);
		log.debug("testGetAllRecords[2]: students: {}", students);
		log.debug("testGetAllRecords[3]: groups: {}", groups);
	}
}
