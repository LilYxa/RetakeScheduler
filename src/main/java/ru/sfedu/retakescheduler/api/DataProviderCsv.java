package ru.sfedu.retakescheduler.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;
import static ru.sfedu.retakescheduler.utils.FileUtil.*;

import java.io.*;
import java.util.List;

public class DataProviderCsv implements IDataProvider{

	private static final Logger log = LogManager.getLogger(DataProviderCsv.class);
	private final MongoBeanHistory logObject = new MongoBeanHistory();

	private final String studentsFile;
	private final String teachersFile;
	private final String groupsFile;
	private final String subjectsFile;
	private final String scheduleUnitsFile;

	public DataProviderCsv() {
//		log.debug("DataProviderCsv[1]: created DataProviderCsv");
//		studentsFile = Constants.CSV_FOLDER_PATH.concat(Constants.STUDENT_FILE).concat(Constants.CSV_FILE_TYPE);
//		teachersFile = Constants.CSV_FOLDER_PATH.concat(Constants.TEACHER_FILE).concat(Constants.CSV_FILE_TYPE);
//		groupsFile = Constants.CSV_FOLDER_PATH.concat(Constants.GROUP_FILE).concat(Constants.CSV_FILE_TYPE);
//		subjectsFile = Constants.CSV_FOLDER_PATH.concat(Constants.SUBJECT_FILE).concat(Constants.CSV_FILE_TYPE);
//		scheduleUnitsFile = Constants.CSV_FOLDER_PATH.concat(Constants.SCHEDULE_UNIT_FILE).concat(Constants.CSV_FILE_TYPE);
//
//		try {
//			createFolderIfNotExists(Constants.CSV_FOLDER_PATH);
//			createNecessaryFiles();
//		} catch (IOException e) {
//			log.error("DataProviderCsv[2]: initialisation error: {}", e.getMessage());
//		}
		this(Constants.CSV_FOLDER_PATH);
	}

	public DataProviderCsv(String pathToCsv) {
		log.debug("DataProviderCsv[1]: created DataProviderCsv");
//		String finalFolder = pathToCsv.concat(Constants.CSV_FOLDER_PATH);
		String finalFolder = pathToCsv.equals(Constants.CSV_FOLDER_PATH) ? Constants.CSV_FOLDER_PATH : pathToCsv.concat(Constants.CSV_FOLDER_PATH);
		studentsFile = finalFolder.concat(Constants.STUDENT_FILE).concat(Constants.CSV_FILE_TYPE);
		teachersFile = finalFolder.concat(Constants.TEACHER_FILE).concat(Constants.CSV_FILE_TYPE);
		groupsFile = finalFolder.concat(Constants.GROUP_FILE).concat(Constants.CSV_FILE_TYPE);
		subjectsFile = finalFolder.concat(Constants.SUBJECT_FILE).concat(Constants.CSV_FILE_TYPE);
		scheduleUnitsFile = finalFolder.concat(Constants.SCHEDULE_UNIT_FILE).concat(Constants.CSV_FILE_TYPE);

		try {
			createFolderIfNotExists(finalFolder);
			createNecessaryFiles();
		} catch (IOException e) {
			log.error("DataProviderCsv[2]: initialisation error: {}", e.getMessage());
		}
	}

	private void createNecessaryFiles() throws IOException {
		createFileIfNotExists(studentsFile);
		createFileIfNotExists(teachersFile);
		createFileIfNotExists(groupsFile);
		createFileIfNotExists(subjectsFile);
		createFileIfNotExists(scheduleUnitsFile);
	}

	public <T> List<T> getAllRecords(String pathToFile, Class<T> tClass) {
		log.debug("getAllRecords[1]: start");
		try (Reader reader = new FileReader(pathToFile);
		     CSVReader csvReader = new CSVReaderBuilder(reader).build()) {
			CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
					.withType(tClass)
					.build();
			return csvToBean.parse();
		} catch (IOException e) {
			log.error("getAllRecords[2]: error: {}", e.getMessage());
		}
		return null;
	}

	@Override
	public void savePerson(Person person) {

	}

	@Override
	public void saveStudent(Student student) {
		log.debug("saveStudent[1]: save Student: {}", student);
		try (CSVWriter writer = new CSVWriter(new FileWriter(studentsFile, true))) {
			StatefulBeanToCsv<Student> sbc = new StatefulBeanToCsvBuilder<Student>(writer)
					.withQuotechar('\'')
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.build();
			sbc.write(student);

		} catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
			log.error("saveStudent[2]: error: {}", e.getMessage());
		}
	}

	@Override
	public void saveTeacher(Teacher teacher) {

	}

	@Override
	public void saveGroup(Group group) {

	}

	@Override
	public void saveScheduleUnit(ScheduleUnit scheduleUnit) {

	}

	@Override
	public void saveSubject(Subject subject) {

	}

	@Override
	public void deletePerson(Person person) {

	}

	@Override
	public void deleteStudent(Student student) {

	}

	@Override
	public void deleteTeacher(Teacher teacher) {

	}

	@Override
	public void deleteGroup(Group group) {

	}

	@Override
	public void deleteScheduleUnit(ScheduleUnit scheduleUnit) {

	}

	@Override
	public void deleteSubject(Subject subject) {

	}

	@Override
	public Person getPersonById(String id) {
		return null;
	}

	@Override
	public Student getStudentById(String id) {
		return null;
	}

	@Override
	public Teacher getTeacherById(String id) {
		return null;
	}

	@Override
	public Group getGroupById(String id) {
		return null;
	}

	@Override
	public ScheduleUnit getScheduleUnitById(String id) {
		return null;
	}

	@Override
	public Subject getSubjectById(String id) {
		return null;
	}

	@Override
	public List<Person> getAllPeople() {
		return null;
	}

	@Override
	public List<Student> getAllStudents() {
		return null;
	}

	@Override
	public List<Teacher> getAllTeachers() {
		return null;
	}

	@Override
	public List<Group> getAllGroups() {
		return null;
	}

	@Override
	public List<ScheduleUnit> getAllScheduleUnits() {
		return null;
	}

	@Override
	public List<Subject> getAllSubjectId() {
		return null;
	}
}
