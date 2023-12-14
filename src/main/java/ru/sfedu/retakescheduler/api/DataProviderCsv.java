package ru.sfedu.retakescheduler.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;
import ru.sfedu.retakescheduler.utils.ExcelUtil;
import ru.sfedu.retakescheduler.utils.FileUtil;

import static ru.sfedu.retakescheduler.utils.PropertiesConfigUtil.*;

import static ru.sfedu.retakescheduler.utils.FileUtil.*;

import java.io.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataProviderCsv implements IDataProvider {

	private static final Logger log = LogManager.getLogger(DataProviderCsv.class);
	private final MongoBeanHistory loggingObject = new MongoBeanHistory();

	private final String studentsFile;
	private final String teachersFile;
	private final String groupsFile;
	private final String subjectsFile;
	private final String mainScheduleUnitsFile;
	private final String retakeScheduleUnitsFile;

	public DataProviderCsv() {

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
		mainScheduleUnitsFile = finalFolder.concat(Constants.MAIN_SCHEDULE_UNIT_FILE).concat(Constants.CSV_FILE_TYPE);
		retakeScheduleUnitsFile = finalFolder.concat(Constants.RETAKE_SCHEDULE_UNIT_FILE).concat(Constants.CSV_FILE_TYPE);

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
		createFileIfNotExists(mainScheduleUnitsFile);
		createFileIfNotExists(retakeScheduleUnitsFile);
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
	public void saveStudent(Student student) throws Exception{
		log.debug("saveStudent[1]: save Student: {}", student);

		List<Student> students = getAllRecords(studentsFile, Student.class);
		boolean notExist = students.stream().noneMatch(obj -> ((Student) obj).getStudentId().equals(student.getStudentId()));

		if (notExist) {
			students.add(student);
			save(student, studentsFile, Student.class, getObjectFields(student));
		} else {
			log.error("saveStudent[2]: this student already exists");
			throw new Exception("this student already exists");
		}
	}

	@Override
	public void saveTeacher(Teacher teacher) throws Exception {
		log.debug("saveTeacher[1]: save Teacher: {}", teacher);

		List<Teacher> teachers = getAllRecords(teachersFile, Teacher.class);
		boolean notExist = teachers.stream().noneMatch(obj -> ((Teacher) obj).getTeacherId().equals(teacher.getTeacherId()));

		if (notExist) {
			teachers.add(teacher);
			save(teacher, teachersFile, Teacher.class, getObjectFields(teacher));
		} else {
			log.error("saveTeacher[2]: this teacher already exists");
			throw new Exception("this teacher already exists");
		}
	}

	@Override
	public void saveGroup(Group group) throws Exception {
		log.debug("saveGroup[1]: save Group: {}", group);
		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(groupsFile, true))) {

//			List<Group> groups = getAllRecords(groupsFile, Group.class);
			List<Group> groups = getAllGroups();
			boolean notExist = groups.stream().noneMatch(obj -> ((Group) obj).getGroupNumber().equals(group.getGroupNumber()));

			if (notExist) {
				groups.add(group);

				group.getStudents().stream()
						.map(student -> new String[]{
								group.getGroupNumber(),
								String.valueOf(group.getCourse()),
								group.getLevelOfTraining(),
								group.getBusyDay() == null ? "" : group.getBusyDay().toString(),
								student.getStudentId()
						})
						.forEach(csvWriter::writeNext);
			} else {
				log.error("saveGroup[2]: this group already exists");
				throw new Exception("this group already exists");
			}
		} catch (IOException e) {
			log.error("saveGroup[3]: error: {}", e.getMessage());
		}
	}

	public List<Group> getAllGroups() {
		try (Reader reader = new FileReader(groupsFile);
		     CSVReader csvReader = new CSVReader(reader)) {

			Map<String, List<String[]>> groupedLines = csvReader.readAll().stream()
					.collect(Collectors.groupingBy(line -> line[0])); // Группировка по номеру группы

			return groupedLines.entrySet().stream()
					.map(entry -> {
						Group group = new Group();
						group.setGroupNumber(entry.getKey());
						group.setCourse(Integer.parseInt(entry.getValue().get(0)[1]));
						group.setLevelOfTraining(entry.getValue().get(0)[2]);
						LocalDate date = null;
						date = entry.getValue().get(0)[3].equals("") ? null : LocalDate.now().with(DayOfWeek.TUESDAY);

						group.setBusyDay(date);

						List<Student> students = entry.getValue().stream()
								.map(arr -> getStudentById(arr[4]))
								.collect(Collectors.toList());
						group.setStudents(students);

						return group;
					})
					.collect(Collectors.toList());

		} catch (IOException | CsvException e) {
			log.error("getAllGroups[3]: error: {}", e.getMessage());
		}
		return Collections.emptyList();
	}

	@Override
	public void saveScheduleUnit(ScheduleUnit scheduleUnit, TypeOfSchedule type) throws Exception {
		log.debug("saveScheduleUnit[1]: save ScheduleUnit: {}", scheduleUnit);
		String scheduleUnitsFile = type.equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;

		List<ScheduleUnit> scheduleUnits = getAllRecords(scheduleUnitsFile, ScheduleUnit.class);
		boolean notExist = scheduleUnits.stream().noneMatch(obj -> ((ScheduleUnit) obj).getScheduleUnitId().equals(scheduleUnit.getScheduleUnitId()));

		if (notExist) {
			scheduleUnits.add(scheduleUnit);
			save(scheduleUnit, scheduleUnitsFile, ScheduleUnit.class, getObjectFields(scheduleUnit));
		} else {
			log.error("saveScheduleUnit[2]: this scheduleUnit already exists");
			throw new Exception("this scheduleUnit already exists");
		}
	}

	@Override
	public void saveSubject(Subject subject) throws Exception {
		log.debug("saveSubject[1]: save Subject: {}", subject);

		List<Subject> subjects = getAllRecords(subjectsFile, Subject.class);
		boolean notExist = subjects.stream().noneMatch(obj -> ((Subject) obj).getSubjectId().equals(subject.getSubjectId()));

		if (notExist) {
			subjects.add(subject);
			save(subject, subjectsFile, Subject.class, getObjectFields(subject));
		} else {
			log.error("saveSubject[2]: this subject already exists");
			throw new Exception("this subject already exists");
		}
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

	public void deleteStudentById(String studentId) throws Exception {
		log.debug("deleteStudentById[1]: studentId = {}", studentId);
		List<Student> students = getAllStudents();
		Student searchedStudent = new Student();
		try {
			searchedStudent = students.stream()
					.filter(student -> student.getStudentId().equals(studentId))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
//			log.error("deleteStudentById[2]: there is no student with this id");
//			return;
			throw new Exception("there is no student with this id");
		}
		log.debug("deleteStudentById[3]: searched student: {}", searchedStudent);
		loggingObject.logObject(searchedStudent, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		boolean removeResult = students.remove(searchedStudent);
		log.debug("deleteStudentById[4]: deletion result: {}", removeResult);

		File oldFile = new File(studentsFile);
		File newFile = new File(studentsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteStudentById[5]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(students, newFile.getPath(), Student.class, getObjectFields(searchedStudent));
		log.debug("deleteStudentById[6]: new file {} was created", newFile.getName());
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

		List<Group> groups = getAllGroups();

		boolean result = groups.remove(group);
		log.debug("deleteGroup[2]: group was deleted: {}", result);

		File oldFile = new File(groupsFile);
		File newFile = new File(groupsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteGroup[3]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveGroups(groups);
		log.debug("deleteGroup[4]: new file {} was created", newFile.getName());
	}

	@Override
	public void deleteScheduleUnit(ScheduleUnit scheduleUnit, TypeOfSchedule type) {
		log.debug("deleteScheduleUnit[1]: delete scheduleUnit: {}", scheduleUnit);
		String scheduleUnitsFile = type.equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;

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
		log.debug("getStudentById[1]: id = {}", id);
		List<Student> students = getAllRecords(studentsFile, Student.class);
		Student searchedStudent = null;
		try {
			searchedStudent = students.stream()
					.filter(student -> student.getStudentId().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getStudentById[2]: error: {}", e.getMessage());
		}
		return searchedStudent;
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
		List<Group> groups = getAllGroups();
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
	public ScheduleUnit getScheduleUnitById(String id, TypeOfSchedule type) {
		log.debug("getScheduleUnitById[1]: id = {}", id);
		String scheduleUnitsFile = type.equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;
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
	public List<Student> getAllStudents() {
		return getAllRecords(studentsFile, Student.class);
	}

	@Override
	public List<Teacher> getAllTeachers() {
		return getAllRecords(teachersFile, Teacher.class);
	}

	@Override
	public List<ScheduleUnit> getAllScheduleUnits(TypeOfSchedule type) {
		String scheduleUnitsFile = type.equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;
		return getAllRecords(scheduleUnitsFile, ScheduleUnit.class);
	}

	@Override
	public List<Subject> getAllSubjects() {
		return getAllRecords(subjectsFile, Subject.class);
	}

	public void saveGroups(List<Group> groups) {
		log.debug("saveGroups[1]: save groups: {}", groups);
		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(groupsFile))) {
//			String[] header = {"groupNumber", "course", "levelOfTraining", "busyDay", "studentId"};
//			csvWriter.writeNext(header);

			groups.stream()
					.flatMap(group -> group.getStudents().stream()
							.map(student -> new String[]{
									group.getGroupNumber(),
									String.valueOf(group.getCourse()),
									group.getLevelOfTraining(),
									group.getBusyDay() == null ? "" : group.getBusyDay().toString(),
									String.valueOf(student.getStudentId())
							}))
					.forEach(csvWriter::writeNext);
		} catch (IOException e) {
			log.error("saveGroups[2]: error: {}", e.getMessage());
		}
	}

	@Override
	public void dataTransform(String sourceFilePath) {
		log.debug("dataTransform[1]: transform data from file: {}", sourceFilePath);
		List<ExcelRow> excelRows = dataLoading(sourceFilePath);

		List<Student> students = convertToStudents(excelRows);
		List<Group> groups = convertToGroups(excelRows);
		List<Subject> subjects = convertToSubjects(excelRows);
		List<Teacher> teachers = convertToTeachers(excelRows);
		groups.forEach(group -> group.setStudents(getStudentsInGroup(group.getGroupNumber(), excelRows, students)));
		List<List<?>> objects = Arrays.asList(students, groups, subjects, teachers);

		log.debug("dataTransform[2]: list of students: {}", students);
		log.debug("dataTransform[3]: list of groups: {}", groups);
		log.debug("dataTransform[4]: list of subjects: {}", subjects);
		log.debug("dataTransform[5]: list of teachers: {}", teachers);

		List<HashMap<Object, HashMap<String, String>>> resultsOfValidation = objects.stream()
				.flatMap(innerList -> innerList.stream())
				.map(item -> validation(item))
				.filter(result -> !result.isEmpty())
				.toList();

		if (!resultsOfValidation.isEmpty()) {
			log.error("dataTransform[2]: Errors were detected during data validation: {}", resultsOfValidation);
			return;
		}

		saveRecords(students, studentsFile, Student.class, getObjectFields(new Student()));
		saveGroups(groups);
		saveRecords(subjects, subjectsFile, Subject.class, getObjectFields(new Subject()));
		saveRecords(teachers, teachersFile, Teacher.class, getObjectFields(new Teacher()));
		log.debug("dataTransform[3]: records were saved in CSV files");
	}

	private List<ExcelRow> dataLoading(String filePath) {
		List<ExcelRow> excelRows = new ArrayList<>();
		log.debug("dataLoading[1]: data loading from file: {}", filePath);
		try {
			excelRows = ExcelUtil.readFromExcel(filePath);
		} catch (IOException e) {
			log.error("dataLoading[2]: error: {}", e.getMessage());
		}
		return excelRows;
	}

	private List<Student> convertToStudents(List<ExcelRow> excelRows) {
		return new ArrayList<>(excelRows.stream()
				.map(this::convertToStudent)
				.collect(Collectors.toMap(
						student -> Arrays.asList(student.getLastName(), student.getFirstName(), student.getPatronymic()),
						Function.identity(),
						(existing, replacement) -> existing
				))
				.values());
	}

	private List<Group> convertToGroups(List<ExcelRow> excelRows) {
		return new ArrayList<>(excelRows.stream()
				.map(this::convertToGroup)
				.distinct()
				.collect(Collectors.toList()));
	}

	private List<Subject> convertToSubjects(List<ExcelRow> excelRows) {
		return new ArrayList<>(excelRows.stream()
				.map(this::convertToSubject)
				.collect(Collectors.toMap(
						subject -> subject.getSubjectName(),
						Function.identity(),
						(existing, replacement) -> existing
				))
				.values());
	}

	private List<Teacher> convertToTeachers(List<ExcelRow> excelRows) {
		return new ArrayList<>(excelRows.stream()
				.map(this::convertToTeacher)
				.filter(teacher -> !teacher.getLastName().equals(Constants.PHYSICAL_TRAINING_FIELD))
				.collect(Collectors.toMap(
						teacher -> Arrays.asList(teacher.getLastName(), teacher.getFirstName(), teacher.getPatronymic()),
						Function.identity(),
						(existing, replacement) -> existing
				))
				.values());
	}

	private Student convertToStudent(ExcelRow excelRow) {
		Student student = new Student();
		String[] studentName = excelRow.getStudentName().split(" ");
		if (studentName.length >= 3) {
			student.setLastName(studentName[0]);
			// Если после фамилии в скобках указана еще одна фамилия
			String firstName = studentName[1].startsWith("(") ? studentName[2] : studentName[1];
			String patronymic = studentName[1].startsWith("(") ? studentName[3] : studentName[2];
			student.setFirstName(firstName);
			student.setPatronymic(patronymic);
		} else if (studentName.length == 2) {
			// Не хватает отчества
			student.setLastName(studentName[0]);
			student.setFirstName(studentName[1]);
		} else if (studentName.length == 1) {
			// Не хватает и имени и отчества
			student.setLastName(studentName[0]);
		}
		double avgScore = (Math.random() * 55) + 25;
		student.setAverageScore(Math.round(avgScore * 100.0) / 100.0);
		return student;
	}

	private Group convertToGroup(ExcelRow excelRow) {
		Group group = new Group();
		group.setGroupNumber(excelRow.getGroup());
		group.setCourse(excelRow.getCourse());
		group.setLevelOfTraining(excelRow.getLevel());
		LocalDate localDate = LocalDate.now();
		localDate = localDate.with(DayOfWeek.TUESDAY);
		group.setBusyDay(localDate);
		return group;
	}

	private Subject convertToSubject(ExcelRow excelRow) {
		Subject subject = new Subject();
		subject.setSubjectName(excelRow.getDiscipline());
		subject.setControlType(excelRow.getControlType());
		return subject;
	}

	private Teacher convertToTeacher(ExcelRow excelRow) {
		String[] fullName = excelRow.getTeacherName().split(" ");
		Teacher teacher = new Teacher();
		if (fullName.length == 3) {
			teacher.setLastName(fullName[0]);
			teacher.setFirstName(fullName[1]);
			teacher.setPatronymic(fullName[2]);
		}
		else {
			teacher.setLastName(fullName[0]);
		}
		LocalDate currentDate = LocalDate.now();
		Random random = new Random();
		int randomDays = random.nextInt(7);
		LocalDate randomDate = currentDate.plusDays(randomDays);
		teacher.setBusyDay(randomDate);
		return teacher;
	}

	private List<Student> getStudentsInGroup(String groupNum, List<ExcelRow> excelRows, List<Student> students) {
		return new ArrayList<>(excelRows.stream()
				.filter(row -> row.getGroup().equals(groupNum))
				.map(row -> findStudent(row, students))
				.collect(Collectors.toMap(
						student -> Arrays.asList(student.getLastName(), student.getFirstName(), student.getPatronymic()),
						Function.identity(),
						(existing, replacement) -> existing
				))
				.values());
	}

	private Student findStudent(ExcelRow excelRow, List<Student> students) {
		String fullName = excelRow.getStudentName();

		// Ищем студента в списке students по полному имени
		return students.stream()
				.filter(student -> fullName.equals(student.getLastName() + " " + student.getFirstName() + " " + student.getPatronymic()))
				.findFirst()
				.orElseGet(() -> convertToStudent(excelRow)); // Если студент не найден, создаем нового с помощью convertToStudent
	}

	private HashMap<Object, HashMap<String, String>> validation(Object object) {
		log.debug("validation[1]: start validation, object: {}", object);
		HashMap<Object, HashMap<String, String>> validationResult = new HashMap<>();
		HashMap<String, String> errors = null;

		if (object instanceof Person) {
			errors = personValidation((Person) object);
		} else if (object instanceof Group) {
			errors = groupValidation((Group) object);
		} else {
			errors = subjectValidation((Subject) object);
		}

		if (!errors.isEmpty()) {
			validationResult.put(object, errors);
		}
		return validationResult;
	}

	private HashMap<String, String> personValidation(Person person) {
		log.debug("personValidation[1]: person: {}", person);
		HashMap<String, String> errors = new HashMap<>();

		if (!person.getLastName().matches(Constants.NAME_REGEX)) {
			errors.put(Constants.LASTNAME_FIELD, Constants.INCORRECT_LASTNAME);
		}

		if (!person.getFirstName().matches(Constants.NAME_REGEX)) {
			errors.put(Constants.FIRSTNAME_FIELD, Constants.INCORRECT_FIRSTNAME);
		}

		if (person.getPatronymic() != null && !person.getPatronymic().matches(Constants.PATRONYMIC_REGEX)) {
			errors.put(Constants.PATRONYMIC_FIELD, Constants.INCORRECT_PATRONYMIC);
		}
		log.debug("personValidation[2]: validation errors: {}", errors);
		return errors;
	}

	private HashMap<String, String> groupValidation(Group group) {
		log.debug("groupValidation[1]: group: {}", group);
		HashMap<String, String> errors = new HashMap<>();

		if (!group.getGroupNumber().matches(Constants.GROUP_NUMBER_REGEX)) {
			errors.put(Constants.GROUP_NUMBER_FIELD, Constants.INCORRECT_GROUP_NUMBER);
		}

		if (!group.getLevelOfTraining().matches(Constants.NAME_REGEX)) {
			errors.put(Constants.TRAINING_LEVEL_FIELD, Constants.INCORRECT_TRAINING_LEVEL);
		}

		if (group.getStudents().isEmpty()) {
			errors.put(Constants.GROUP_NUMBER_FIELD, Constants.EMPTY_GROUP);
		}

		log.debug("groupValidation[2]: validation errors: {}", errors);
		return errors;
	}

	private HashMap<String, String> subjectValidation(Subject subject) {
		log.debug("subjectValidation[1]: subject: {}", subject);
		HashMap<String, String> errors = new HashMap<>();

		if (!subject.getSubjectName().matches(Constants.SUBJECT_REGEX)) {
			errors.put(Constants.SUBJECT_FIELD, Constants.INCORRECT_SUBJECT);
		}

		if (!subject.getControlType().matches(Constants.CONTROL_TYPE_REGEX)) {
			errors.put(Constants.CONTROL_TYPE_FIELD, Constants.INCORRECT_CONTROL_TYPE);
		}
		log.debug("subjectValidation[2]: validation errors: {}", errors);
		return errors;
	}

	public Schedule createSchedule(Schedule mainSchedule, LocalDate startDate, LocalDate endDate, boolean exportToExcel, boolean sendEmail) {
		log.debug("createSchedule[1]: scheduling retakes from {} to {}", startDate, endDate);
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		List<ExcelRow> excelRows = dataLoading(file.getPath());

		List<Group> groups = getAllGroups();
		List<Teacher> teachers = getAllTeachers();
		List<Subject> subjects = getAllSubjects();
		List<Student> students = getAllStudents();

		LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.of(8, 0));
		LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.of(17, 50));

		TeacherSubjectMapping teacherSubjectMapping = new TeacherSubjectMapping();
		fillTeacherSubjectMapping(excelRows, teacherSubjectMapping, teachers, subjects);

		List<ScheduleUnit> retakes = new ArrayList<>();
		List<ScheduleUnit> mainSubjects = mainSchedule.getUnits();

		// Продолжительность урока (в минутах)
		int lessonDuration = 95;

		for (LocalDateTime currentDate = startDateTime; currentDate.isBefore(endDateTime); currentDate = currentDate.plusDays(1)) {
			if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
				for (Subject subject : subjects) {
					if (LocalTime.of(currentDate.getHour(), currentDate.getMinute()).isAfter(LocalTime.of(17, 50))) {
						currentDate = currentDate.plusDays(1);
						currentDate = currentDate.withHour(8).withMinute(0);
					}

					Group group = findGroupByDiscipline(excelRows, groups, subject);
					Teacher teacher = teacherSubjectMapping.getTeacherBySubject(subject);

					// Проверка, не занят ли учитель в текущий день
					if (teacher != null && !teacher.getBusyDay().equals(currentDate) && !teacher.getBusyDay().getDayOfWeek().equals(currentDate.getDayOfWeek())) {
						// Проверка, не занята ли группа в текущий день
						if (group != null && !group.getBusyDay().equals(currentDate) && !group.getBusyDay().getDayOfWeek().equals(currentDate.getDayOfWeek())) {

							// Проверка наложения с основным расписанием
							if (!isOverlapping(mainSubjects, currentDate, lessonDuration)) {
								ScheduleUnit retakeUnit = new ScheduleUnit();
								retakeUnit.setSubjectId(subject.getSubjectId());
								retakeUnit.setPersonId(teacher.getTeacherId());
								retakeUnit.setLocation("IVTiPT");
								retakeUnit.setGroupNumber(group.getGroupNumber());
								retakeUnit.setDateTime(currentDate);

								retakes.add(retakeUnit);
							}
							currentDate = currentDate.plusMinutes(lessonDuration);
						}
					}
				}
			}
		}
		Schedule retakeSchedule = new Schedule(TypeOfSchedule.RETAKE, retakes);

		if (sendEmail) {
			sendEmail(retakeSchedule, students);
		}

		if (exportToExcel) {
			exportInExcelFormat(retakeSchedule, Constants.EXCEL_FOLDER.concat(Constants.EXCEL_RETAKE_SCHEDULE_FILE));
		}

		return retakeSchedule;
	}

	// Метод проверки наложения с основным расписанием
	private boolean isOverlapping(List<ScheduleUnit> mainSubjects, LocalDateTime startTime, int lessonDuration) {
		LocalDateTime endTime = startTime.plusMinutes(lessonDuration);

		for (ScheduleUnit mainSubject : mainSubjects) {
			LocalDateTime mainStartTime = mainSubject.getDateTime();
			LocalDateTime mainEndTime = mainStartTime.plusMinutes(lessonDuration);

			if (startTime.isBefore(mainEndTime) && endTime.isAfter(mainStartTime)) {
				return true; // Наложение с основным расписанием
			}
		}

		return false; // Нет наложения
	}


	private static Group findGroupByDiscipline(List<ExcelRow> excelRows, List<Group> groups, Subject subject) {
		for (ExcelRow excelRow : excelRows) {
			if (excelRow.getDiscipline().equals(subject.getSubjectName())) {
				String groupNumber = excelRow.getGroup();
				for (Group group : groups) {
					if (group.getGroupNumber().equals(groupNumber)) {
						return group;
					}
				}
			}
		}
		return null;  // Возвращаем null, если группа не найдена
	}
	private static void fillTeacherSubjectMapping(List<ExcelRow> excelRows, TeacherSubjectMapping teacherSubjectMapping, List<Teacher> teachers, List<Subject> subjects) {
		for (ExcelRow excelRow : excelRows) {
			String teacherName = excelRow.getTeacherName();
			String[] teacherNameArr = teacherName.split(" ");
			if (teacherNameArr.length == 1)
				continue;
			String discipline = excelRow.getDiscipline();

			Optional<Teacher> optionalTeacher = teachers.stream()
					.filter(teacher -> teacher.getLastName().equals(teacherNameArr[0])
					&& teacher.getFirstName().equals(teacherNameArr[1])
					&& teacher.getPatronymic().equals(teacherNameArr[2]))
							.findFirst();

			Optional<Subject> optionalSubject = subjects.stream()
					.filter(subject -> subject.getSubjectName().equals(discipline))
					.findFirst();


			if (optionalTeacher.isPresent() && optionalSubject.isPresent()) {
				Teacher teacher = optionalTeacher.get();
				Subject subject = optionalSubject.get();

				// Добавляем в TeacherSubjectMapping
				teacherSubjectMapping.addTeacherSubject(teacher, subject);
			}
		}
	}

	private static Map<Subject, Set<Group>> fillGroupSubjectMapping(List<ExcelRow> excelRows, List<Group> groups, List<Subject> subjects) {
		Map<Subject, Set<Group>> subjectGroupMap = new HashMap<>();

		for (ExcelRow excelRow : excelRows) {
			String[] teacherNameArr = excelRow.getTeacherName().split(" ");
			if (teacherNameArr.length == 1)
				continue;

			String group = excelRow.getGroup();
			String discipline = excelRow.getDiscipline();

			Optional<Group> optionalGroup = groups.stream()
					.filter(group1 -> group1.getGroupNumber().equals(group))
					.findFirst();

			Optional<Subject> optionalSubject = subjects.stream()
					.filter(subject -> subject.getSubjectName().equals(discipline))
					.findFirst();

			if (optionalGroup.isPresent() && optionalSubject.isPresent()) {
				Group group1 = optionalGroup.get();
				Subject subject = optionalSubject.get();

				// Получаем или создаем множество групп для данного предмета
				Set<Group> groupSet = subjectGroupMap.computeIfAbsent(subject, k -> new HashSet<>());

				// Добавляем текущую группу в множество
				groupSet.add(group1);
			}
		}

		return subjectGroupMap;
	}

	private void sendEmail(Schedule schedule, List<Student> students) {
		log.debug("sendMail[1]: send mail to students: {}", students);
		String username = "";
		String password = "";
		Properties props = null;
		try {
			username = getProperty(Constants.SMTP_EMAIL);
			password = getProperty(Constants.SMTP_PASSWORD);

			props = new Properties();
			props.put(Constants.SMTP_PROP_AUTH, getProperty(Constants.SMTP_AUTH_FIELD));
			props.put(Constants.SMTP_PROP_TLS, getProperty(Constants.SMTP_TLS_FIELD));
			props.put(Constants.SMTP_PROP_HOST, getProperty(Constants.SMTP_HOST_FIELD));
			props.put(Constants.SMTP_PROP_PORT, getProperty(Constants.SMTP_PORT_FIELD));
		} catch (IOException e) {
			log.error("sendEmail[2]: error: {}", e.getMessage());
		}

		String finalUsername = username;
		String finalPassword = password;

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(finalUsername, finalPassword);
			}
		});

		for (Student student : students) {
			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(username));
				message.setSubject(Constants.MAIL_SUBJECT);

				String email = student.getEmail();
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));

				StringBuilder emailContent = new StringBuilder();
				emailContent.append(Constants.EMAIL_GREETING).append(student.getLastName()).append(" ").append(student.getFirstName()).append(" ").append(student.getPatronymic()).append("\n");
				emailContent.append(Constants.EMAIL_MSG_CONTENT);

				message.setText(emailContent.toString());
				Transport.send(message);

			} catch (MessagingException e) {
				log.error("sendMail[3]: error: {}", e.getMessage());
			}
		}
	}

	private void exportInExcelFormat(Schedule schedule, String pathToFile) {
		log.debug("exportInExcelFormat[1]: export schedule to file: {}", pathToFile);
		List<ScheduleUnit> scheduleUnits = schedule.getUnits();
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Расписание пересдач");

			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("Номер группы");
			headerRow.createCell(1).setCellValue("Предмет");
			headerRow.createCell(2).setCellValue("Дата и время");
			headerRow.createCell(3).setCellValue("Место");
			headerRow.createCell(4).setCellValue("Преподаватель");


			int rowNum = 1;
			for (ScheduleUnit scheduleUnit : scheduleUnits) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(scheduleUnit.getGroupNumber());

				Subject subject = getSubjectById(scheduleUnit.getSubjectId());
				Teacher teacher = getTeacherById(scheduleUnit.getPersonId());
				String teacherFullname = teacher.getLastName() + " " + teacher.getFirstName() + " " + teacher.getPatronymic();

				row.createCell(1).setCellValue(subject.getSubjectName());
				row.createCell(2).setCellValue(scheduleUnit.getDateTime().toString());
				row.createCell(3).setCellValue(scheduleUnit.getLocation());
				row.createCell(4).setCellValue(teacherFullname);
			}

			try (FileOutputStream fileOutputStream = new FileOutputStream(pathToFile)) {
				workbook.write(fileOutputStream);
				log.debug("exportInExcelFormat[2]: export completed successfully");
			}
		} catch (IOException e) {
			log.error("exportInExcelFormat[3]: error: {}", e.getMessage());
		}
	}
}
