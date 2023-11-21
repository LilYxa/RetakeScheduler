package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.Student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.sfedu.retakescheduler.utils.FileUtil.*;

public class DataProviderCsvTest {

	private static String studentsFile;
	private static String teachersFile;
	private static String groupsFile;
	private static String subjectsFile;
	private static String scheduleUnitsFile;
	private static DataProviderCsv dataProviderCsv1;
	private static DataProviderCsv dataProviderCsv2;
	private static String testPath;

	private static final Logger log = LogManager.getLogger(DataProviderCsvTest.class);
	@Before
	public void beforeAll() {
		testPath = Constants.TEST_FOLDER_PATH;
		studentsFile = testPath.concat(Constants.CSV_FOLDER_PATH).concat(Constants.STUDENT_FILE).concat(Constants.CSV_FILE_TYPE);
		teachersFile = testPath.concat(Constants.TEACHER_FILE).concat(Constants.CSV_FILE_TYPE);
		groupsFile = testPath.concat(Constants.GROUP_FILE).concat(Constants.CSV_FILE_TYPE);
		subjectsFile = testPath.concat(Constants.SUBJECT_FILE).concat(Constants.CSV_FILE_TYPE);
		scheduleUnitsFile = testPath.concat(Constants.SCHEDULE_UNIT_FILE).concat(Constants.CSV_FILE_TYPE);
		dataProviderCsv1 = new DataProviderCsv();
		dataProviderCsv2 = new DataProviderCsv(testPath);
	}

//	@After
//	public void afterAll() {
//		deleteFileOrFolder(testPath);
//	}

	@Test
	public void testSaveStudent() {
		log.debug("testSaveStudent[1]: test start");
		Student student1 = new Student("Ivanov", "Ivan", "Ivanovich", "ivanov@mail.ru", "VT-22022", 100);
		Student student2 = new Student("Petrov", "Petr", "Petrovich", "petrov@mail.ru", "VT-22023", 99);
		Student student3 = new Student("Sidorov", "Sidor", "Sidorovich", "sidorov@mail.ru", "VT-22024", 110);
		log.debug("testSaveStudent[2]: student1: {}", student1);
		log.debug("testSaveStudent[3]: student2: {}", student2);
		log.debug("testSaveStudent[4]: student3: {}", student3);
		dataProviderCsv2.saveStudent(student1);
		dataProviderCsv2.saveStudent(student2);
		dataProviderCsv2.saveStudent(student3);
	}

	@Test
	public void testGetAllRecords() {
		log.debug("testGetAllRecords[1]: test start");
		List<Student> res = dataProviderCsv2.getAllRecords(studentsFile, Student.class);
		for (Student student : res) {
			log.debug("testGetAllRecords[2] studentLastName = {}", student.getLastName());
			log.debug("testGetAllRecords[3] studentFirstName = {}", student.getFirstName());
		}
		log.debug("testGetAllRecords[4]: students: {}", res);
	}

	@Test
	public void testGetObjectFields() {
		log.debug("testGetObjectFields[1]: test start");
		String[] expectedStudentColumns = {"lastName", "firstName", "patronymic", "email", "studentId", "finalRating"};
		log.debug("testGetObjectFields[2]: expected fields: {}", Arrays.stream(expectedStudentColumns).toList());
		Student student1 = new Student("Ivanov", "Ivan", "Ivanovich", "ivanov@mail.ru", "VT-22022", 100);

		String[] actualStudentColumns = dataProviderCsv1.getObjectFields(student1);
		assertEquals(expectedStudentColumns, actualStudentColumns);
		log.debug("testGetObjectFields[3]: actual fields: {}", Arrays.stream(actualStudentColumns).toList());
		log.debug("testGetObjectFields[4]: test finish");
	}

	@Test
	public void testDeleteStudent() {
		log.debug("testDeleteStudents[1]: test start");
		List<Student> studentsBeforeDelete = dataProviderCsv2.getAllStudents();
		log.debug("testDeleteStudents[2]: students before removing: {}", studentsBeforeDelete);
		Student student1 = new Student("Ivanov", "Ivan", "Ivanovich", "ivanov@mail.ru", "VT-22022", 100);
		dataProviderCsv2.deleteStudent(student1);
		List<Student> studentsAfterDelete = dataProviderCsv2.getAllStudents();
		log.debug("testDeleteStudents[3]: students after removing: {}", studentsAfterDelete);
	}

	@Test
	public void testDeleteObject() {
		log.debug("testDeleteObject[1]: test start");
		List<Student> studentsBeforeDelete = dataProviderCsv2.getAllStudents();
		log.debug("testDeleteObject[2]: students before removing: {}", studentsBeforeDelete);
		Student student1 = new Student("Ivanov", "Ivan", "Ivanovich", "ivanov@mail.ru", "VT-22022", 100);
		dataProviderCsv2.deleteObject(student1, studentsFile);
		List<Student> studentsAfterDelete = dataProviderCsv2.getAllStudents();

		List<Student> expectedStudentsAfterDelete = new ArrayList<>();
		Student student2 = new Student("Petrov", "Petr", "Petrovich", "petrov@mail.ru", "VT-22023", 99);
		Student student3 = new Student("Sidorov", "Sidor", "Sidorovich", "sidorov@mail.ru", "VT-22024", 110);
		expectedStudentsAfterDelete.add(student2);
		expectedStudentsAfterDelete.add(student3);
		assertEquals(expectedStudentsAfterDelete, studentsAfterDelete);

		log.debug("testDeleteStudents[3]: students after removing: {}", studentsAfterDelete);
	}

	@Test
	public void testGetStudentByIdPositive() {
		log.debug("testGetStudentByIdPositive[1]: test start");
		Student expectedStudent = new Student("Sidorov", "Sidor", "Sidorovich", "sidorov@mail.ru", "VT-22024", 110);

		log.debug("testGetStudentByIdPositive[2]: expected student: {}", expectedStudent);
		Student actualStudent = dataProviderCsv2.getStudentById(expectedStudent.getStudentId());
		log.debug("testGetStudentByIdPositive[3]: actual student: {}", actualStudent);
		assertEquals(expectedStudent, actualStudent);
	}

	@Test
	public void testGetStudentByIdNegative() {
		log.debug("testGetStudentByIdNegative[1]: test start");

		Student actualStudent = dataProviderCsv2.getStudentById("12345");
		assertNull(actualStudent);
	}
}
