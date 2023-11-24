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
import ru.sfedu.retakescheduler.utils.ExcelUtil;

import static ru.sfedu.retakescheduler.utils.FileUtil.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		String finalFolder = pathToCsv.equals(Constants.CSV_FOLDER_PATH) ? Constants.CSV_FOLDER_PATH : pathToCsv.concat(Constants.CSV_FOLDER);
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

	public <T> void deleteObject(T object, String filePath) {
		log.debug("deleteObject[1]: object type: {}, object: {}", object.getClass().getSimpleName(), object);
		loggingObject.logObject(object, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);
		List<T> objects = getAllRecords(filePath, (Class<T>) object.getClass());

		boolean recordDeleteResult = objects.remove(object);
		log.debug("deleteObject[2]: object {} was deleted: {}", object.getClass().getSimpleName(), recordDeleteResult);

		File oldFile = new File(filePath);
		File newFile = new File(filePath);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteObject[3] old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(objects, newFile.getPath(), (Class<T>) object.getClass(), getObjectFields(object));
		log.debug("deleteObject[4]: new file {} was created", newFile.getName());
	}

	@Override
	public void deleteTeacher(Teacher teacher) {
		log.debug("deleteTeacher[1]: delete teacher: {}", teacher);

		loggingObject.logObject(teacher, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		List<Teacher> teachers = getAllRecords(teachersFile, Teacher.class);

		boolean result = teachers.remove(teacher);
		log.debug("deleteTeacher[2]: teacher was deleted: {}", result);

		File oldFile = new File(teachersFile);
		File newFile = new File(teachersFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteTeacher[3]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(teachers, newFile.getPath(), Teacher.class, getObjectFields(teacher));
		log.debug("deleteStudent[4]: new file {} was created", newFile.getName());
	}

	@Override
	public void deleteGroup(Group group) {
		log.debug("deleteGroup[1]: delete group: {}", group);

		loggingObject.logObject(group, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		List<Group> groups = getAllRecords(groupsFile, Group.class);

		boolean result = groups.remove(group);
		log.debug("deleteGroup[2]: group was deleted: {}", result);

		File oldFile = new File(groupsFile);
		File newFile = new File(groupsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteGroup[3]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(groups, newFile.getPath(), Group.class, getObjectFields(group));
		log.debug("deleteGroup[4]: new file {} was created", newFile.getName());
	}

	@Override
	public void deleteScheduleUnit(ScheduleUnit scheduleUnit) {
		log.debug("deleteScheduleUnit[1]: delete scheduleUnit: {}", scheduleUnit);

		loggingObject.logObject(scheduleUnit, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		List<ScheduleUnit> scheduleUnits = getAllRecords(scheduleUnitsFile, ScheduleUnit.class);

		boolean result = scheduleUnits.remove(scheduleUnit);
		log.debug("deleteScheduleUnit[2]: scheduleUnit was deleted: {}", result);

		File oldFile = new File(scheduleUnitsFile);
		File newFile = new File(scheduleUnitsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteScheduleUnits[3]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(scheduleUnits, newFile.getPath(), ScheduleUnit.class, getObjectFields(scheduleUnit));
		log.debug("deleteScheduleUnit[4]: new file {} was created", newFile.getName());
	}

	@Override
	public void deleteSubject(Subject subject) {
		log.debug("deleteSubject[1]: delete subject: {}", subject);

		loggingObject.logObject(subject, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		List<Subject> subjects = getAllRecords(subjectsFile, Subject.class);

		boolean result = subjects.remove(subject);
		log.debug("deleteSubject[2]: subject was deleted: {}", result);

		File oldFile = new File(subjectsFile);
		File newFile = new File(subjectsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteSubject[3]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(subjects, newFile.getPath(), Subject.class, getObjectFields(subject));
		log.debug("deleteSubject[4]: new file {} was created", newFile.getName());
	}

	@Override
	public Person getPersonById(String id) {
		return null;
	}

	private <T> T getObjectById(String id, Class<T> tClass, String filePath, Function<T, String> idExtractor) {
		log.debug("getObjectById[1]: object {}, id = {}", tClass.getSimpleName(), id);
		List<T> objects = getAllRecords(filePath, tClass);
		T result = null;
		try {
			result = objects.stream()
					.filter(object -> idExtractor.apply(object).equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getObjectById[2]: error: {}", e.getMessage());
		}
		return result;
	}

	@Override
	public Student getStudentById(String id) {
//		log.debug("getStudentById[1]: id = {}", id);
//		List<Student> students = getAllRecords(studentsFile, Student.class);
//		Student searchedStudent = null;
//		try {
//			searchedStudent = students.stream()
//					.filter(student -> student.getStudentId().equals(id))
//					.findFirst()
//					.get();
//		} catch (NoSuchElementException e) {
//			log.error("getStudentById[2]: error: {}", e.getMessage());
//		}
//		return searchedStudent;
		return getObjectById(id, Student.class, studentsFile, Student::getStudentId);
	}

	@Override
	public Teacher getTeacherById(String id) {
		log.debug("getTeacherById[1]: id = {}", id);
		List<Teacher> teachers = getAllRecords(teachersFile, Teacher.class);
		Teacher searchedTeacher = null;
		try {
			searchedTeacher = teachers.stream()
					.filter(teacher -> teacher.getTeacherId().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getTeacherById[2]: error: {}", e.getMessage());
		}
		return searchedTeacher;
	}

	@Override
	public Group getGroupById(String id) {
		log.debug("getGroupById[1]: id = {}", id);
		List<Group> groups = getAllRecords(groupsFile, Group.class);
		Group searchedGroup = null;
		try {
			searchedGroup = groups.stream()
					.filter(group -> group.getGroupNumber().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getGroupById[2]: error: {}", e.getMessage());
		}
		return searchedGroup;
	}

	@Override
	public ScheduleUnit getScheduleUnitById(String id) {
		log.debug("getScheduleUnitById[1]: id = {}", id);
		List<ScheduleUnit> scheduleUnits = getAllRecords(scheduleUnitsFile, ScheduleUnit.class);
		ScheduleUnit searchedScheduleUnit = null;
		try {
			searchedScheduleUnit = scheduleUnits.stream()
					.filter(scheduleUnit -> scheduleUnit.getScheduleUnitId().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getScheduleUnitById[2]: error: {}", e.getMessage());
		}
		return searchedScheduleUnit;
	}

	@Override
	public Subject getSubjectById(String id) {
		log.debug("getSubjectById[1]: id = {}", id);
		List<Subject> subjects = getAllRecords(subjectsFile, Subject.class);
		Subject searchedSubject = null;
		try {
			searchedSubject = subjects.stream()
					.filter(subject -> subject.getSubjectId().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getSubjectById[2]: error: {}", e.getMessage());
		}
		return searchedSubject;
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

	@Override
	public void dataTransform(String sourceFilePath) {

	}

	public List<List<?>> dataLoading(String filePath) {
		List<List<?>> resultList = new ArrayList<>();
		List<ExcelRow> excelRows = new ArrayList<>();
		log.debug("dataLoading[1]: data loading from file: {}", filePath);
		try {
			excelRows = ExcelUtil.readFromExcel(filePath);
		} catch (IOException e) {
			log.error("dataLoading[2]: error: {}", e.getMessage());
		}

		List<Student> students = convertToStudents(excelRows);
		log.debug("dataLoading[3]: list of students: {}", students);
		resultList.add(students);
		return resultList;
	}

	private List<Student> convertToStudents(List<ExcelRow> excelRows) {
		return excelRows.stream()
				.map(this::convertToStudent)
				.collect(Collectors.toList());
	}

	private Student convertToStudent(ExcelRow excelRow) {
		Student student = new Student();
		String[] studentName = excelRow.getStudentName().split(" ");
		if (studentName.length >= 3) {
			student.setLastName(studentName[0]);
			student.setFirstName(studentName[1]);
			student.setPatronymic(studentName[2]);
		} else if (studentName.length == 2) {
			// не хватает отчества
			student.setLastName(studentName[0]);
			student.setFirstName(studentName[1]);
		} else if (studentName.length == 1) {
			// не хватает и имени и отчества
			student.setLastName(studentName[0]);
		}
		student.setFinalRating(excelRow.getFinalRating());
		return student;
	}


}
