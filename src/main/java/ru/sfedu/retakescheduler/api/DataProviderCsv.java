package ru.sfedu.retakescheduler.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;
import static ru.sfedu.retakescheduler.utils.FileUtil.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataProviderCsv implements IDataProvider{

	private static final Logger log = LogManager.getLogger(DataProviderCsv.class);
	private final MongoBeanHistory loggingObject = new MongoBeanHistory();

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
			log.error("getAllRecords[3]: error: {}", e.getMessage());
		}
		return null;
	}

	public <T> void save(T object, String pathToFile, Class<T> tClass, String[] columns) {
		log.debug("save[1]: save {}: {}", object.getClass().getSimpleName(), object);
		try (CSVWriter writer = new CSVWriter(new FileWriter(pathToFile, true))) {
			ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(tClass);
			mappingStrategy.setColumnMapping(columns);

			StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
					.withMappingStrategy(mappingStrategy)
					.build();
			beanToCsv.write(object);
		} catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
			log.error("save[2]: error: {}", e.getMessage());
		}
	}

	public <T> void saveRecords(List<T> list, String pathToFile, Class<T> tClass, String[] columns) {
		log.debug("saveRecords[1]: save records: {}", list);
		try (CSVWriter writer = new CSVWriter(new FileWriter(pathToFile, false))) {
			ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(tClass);
			mappingStrategy.setColumnMapping(columns);

			StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
					.withMappingStrategy(mappingStrategy)
					.build();
			beanToCsv.write(list);
		} catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
			log.error("saveRecords[2]: error: {}", e.getMessage());
		}
	}

//	public <T> boolean checkObjectExistenceInFile(String pathToFile, Class<T> tClass) {
//		List<T> allObjects = getAllRecords(pathToFile, tClass);
//		boolean recordExist = allObjects.stream().noneMatch(obj -> )
//	}

	public <T> String[] getObjectFields(T object) {
		List<Field> childClassFields = Arrays.stream(object.getClass().getDeclaredFields()).toList();
		List<Field> parentClassFields = Arrays.stream(object.getClass().getSuperclass().getDeclaredFields()).toList();

		List<Field> allFields = new ArrayList<>();
		allFields.addAll(parentClassFields);
		allFields.addAll(childClassFields);

		String[] columns = new String[allFields.size()];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = allFields.get(i).getName();
		}
		return columns;
	}

	@Override
	public void savePerson(Person person) {

	}

	@Override
	public void saveStudent(Student student) {
		log.debug("saveStudent[1]: save Student: {}", student);

		List<Student> students = getAllRecords(studentsFile, Student.class);
		boolean isNotExist = students.stream().noneMatch(obj -> ((Student) obj).getStudentId().equals(student.getStudentId()));

		if (isNotExist) {
			students.add(student);
			save(student, studentsFile, Student.class, getObjectFields(student));
		} else {
			log.error("saveStudent[2]: this student already exists");
		}
	}

	@Override
	public void saveTeacher(Teacher teacher) {
		log.debug("saveTeacher[1]: save Teacher: {}", teacher);

		List<Teacher> teachers = getAllRecords(teachersFile, Teacher.class);
		boolean isNotExist = teachers.stream().noneMatch(obj -> ((Teacher) obj).getTeacherId().equals(teacher.getTeacherId()));

		if (isNotExist) {
			teachers.add(teacher);
			save(teacher, teachersFile, Teacher.class, getObjectFields(teacher));
		} else {
			log.error("saveTeacher[2]: this teacher already exists");
		}
	}

	@Override
	public void saveGroup(Group group) {
		log.debug("saveGroup[1]: save Group: {}", group);

		List<Group> groups = getAllRecords(groupsFile, Group.class);
		boolean isNotExist = groups.stream().noneMatch(obj -> ((Group) obj).getGroupNumber().equals(group.getGroupNumber()));

		if (isNotExist) {
			groups.add(group);
			save(group, groupsFile, Group.class, getObjectFields(group));
		} else {
			log.error("saveGroup[2]: this group already exists");
		}
	}

	@Override
	public void saveScheduleUnit(ScheduleUnit scheduleUnit) {
		log.debug("saveScheduleUnit[1]: save ScheduleUnit: {}", scheduleUnit);

		List<ScheduleUnit> scheduleUnits = getAllRecords(scheduleUnitsFile, ScheduleUnit.class);
		boolean isNotExist = scheduleUnits.stream().noneMatch(obj -> ((ScheduleUnit) obj).getScheduleUnitId().equals(scheduleUnit.getScheduleUnitId()));

		if (isNotExist) {
			scheduleUnits.add(scheduleUnit);
			save(scheduleUnit, scheduleUnitsFile, ScheduleUnit.class, getObjectFields(scheduleUnit));
		} else {
			log.error("saveScheduleUnit[2]: this scheduleUnit already exists");
		}
	}

	@Override
	public void saveSubject(Subject subject) {
		log.debug("saveSubject[1]: save Subject: {}", subject);

		List<Subject> subjects = getAllRecords(subjectsFile, Subject.class);
		boolean isNotExist = subjects.stream().noneMatch(obj -> ((Subject) obj).getSubjectId().equals(subject.getSubjectId()));

		if (isNotExist) {
			subjects.add(subject);
			save(subject, subjectsFile, Subject.class, getObjectFields(subject));
		} else {
			log.error("saveSubject[2]: this subject already exists");
		}
	}

	@Override
	public void deletePerson(Person person) {

	}

	@Override
	public void deleteStudent(Student student) {
		log.debug("deleteStudent[1]: delete student: {}", student);

		loggingObject.logObject(student, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		List<Student> students = getAllRecords(studentsFile, Student.class);

		boolean result = students.remove(student);
		log.debug("deleteStudent[2]: student was deleted: {}", result);

		File oldFile = new File(studentsFile);
		File newFile = new File(studentsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteStudent[3]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(students, newFile.getPath(), Student.class, getObjectFields(student));
		log.debug("deleteStudent[4]: new file {} was created", newFile.getName());
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
		return getAllRecords(studentsFile, Student.class);
	}

	@Override
	public List<Teacher> getAllTeachers() {
		return getAllRecords(teachersFile, Teacher.class);
	}

	@Override
	public List<Group> getAllGroups() {
		return getAllRecords(groupsFile, Group.class);
	}

	@Override
	public List<ScheduleUnit> getAllScheduleUnits() {
		return getAllRecords(scheduleUnitsFile, ScheduleUnit.class);
	}

	@Override
	public List<Subject> getAllSubjects() {
		return getAllRecords(subjectsFile, Subject.class);
	}
}
