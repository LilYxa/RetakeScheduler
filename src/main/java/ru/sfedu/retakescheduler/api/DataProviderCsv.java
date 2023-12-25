package ru.sfedu.retakescheduler.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;

import static ru.sfedu.retakescheduler.utils.DataUtil.*;
import static ru.sfedu.retakescheduler.utils.CsvUtil.*;
import static ru.sfedu.retakescheduler.utils.FileUtil.*;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

	/**
	 * Uses the {@link IDataProvider#saveStudent(Student) saveStudent} method to store data about the student.
	 */
	@Override
	public void saveStudent(Student student) throws Exception{
		log.debug("saveStudent[1]: save Student: {}", student);

		List<Student> students = getAllRecords(studentsFile, Student.class);
		checkIfEntityExist(students, student, "this student already exists");
		students.add(student);
		save(student, studentsFile, Student.class, getObjectFields(student));
		log.info("saveStudent[2]: save student: {}", student);
	}

	/**
	 * Uses the {@link IDataProvider#saveTeacher(Teacher) saveTeacher} method to store data about the teacher.
	 */
	@Override
	public void saveTeacher(Teacher teacher) throws Exception {
		log.debug("saveTeacher[1]: save Teacher: {}", teacher);

		List<Teacher> teachers = getAllRecords(teachersFile, Teacher.class);
		checkIfEntityExist(teachers, teacher, "this teacher already exists");
		teachers.add(teacher);
		save(teacher, teachersFile, Teacher.class, getObjectFields(teacher));
		log.info("saveTeacher[2]: save teacher: {}", teacher);
	}

	/**
	 * Uses the {@link IDataProvider#saveGroup(Group) saveGroup} method to store data about the group.
	 */
	@Override
	public void saveGroup(Group group) throws Exception {
		log.debug("saveGroup[1]: save Group: {}", group);
		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(groupsFile, true))) {

			List<Group> groups = getAllGroups();
			checkIfEntityExist(groups, group, "this group already exists");
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
			log.info("saveGroup[2]: save group: {}", group);
		} catch (IOException e) {
			log.error("saveGroup[3]: error: {}", e.getMessage());
		}
	}

	/**
	 * Uses the {@link IDataProvider#getAllGroups() getAllGroups} method to get a list of all groups in the system.
	 */
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
								.map(arr -> {
									try {
										return getStudentById(arr[4]);
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								})
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

	/**
	 * Uses the {@link IDataProvider#saveScheduleUnit(ScheduleUnit, TypeOfSchedule) saveScheduleUnit} method to store data about the schedule unit.
	 */
	@Override
	public void saveScheduleUnit(ScheduleUnit scheduleUnit, TypeOfSchedule type) throws Exception {
		log.debug("saveScheduleUnit[1]: save ScheduleUnit: {}", scheduleUnit);
		String scheduleUnitsFile = type.equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;

		List<ScheduleUnit> scheduleUnits = getAllRecords(scheduleUnitsFile, ScheduleUnit.class);
		checkIfEntityExist(scheduleUnits, scheduleUnit, "this scheduleUnit already exists");
		scheduleUnits.add(scheduleUnit);
		save(scheduleUnit, scheduleUnitsFile, ScheduleUnit.class, getObjectFields(scheduleUnit));
		log.info("saveScheduleUnit[2]: save schedule unit: {}", scheduleUnit);
	}

	/**
	 * Uses the {@link IDataProvider#saveSchedule(Schedule) saveSchedule} method to store data about the schedule.
	 */
	public void saveSchedule(Schedule schedule) {
		log.debug("saveSchedule[1]: saving {} schedule: {}", schedule.getTypeOfSchedule(), schedule);
		String scheduleUnitsFile = schedule.getTypeOfSchedule().equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;
		saveRecords(schedule.getUnits(), scheduleUnitsFile, ScheduleUnit.class, getObjectFields(new ScheduleUnit()));
	}

	/**
	 * Uses the {@link IDataProvider#saveSubject(Subject) saveSubject} method to store data about the subject.
	 */
	@Override
	public void saveSubject(Subject subject) throws Exception {
		log.debug("saveSubject[1]: save Subject: {}", subject);

		List<Subject> subjects = getAllRecords(subjectsFile, Subject.class);
		checkIfEntityExist(subjects, subject, "this subject already exists");
		subjects.add(subject);
		save(subject, subjectsFile, Subject.class, getObjectFields(subject));
		log.info("saveSubject[2]: save subject: {}", subject);
	}

	/**
	 * Uses the {@link IDataProvider#deleteStudentById(String) deleteStudentById} method to remove the student with the given ID.
	 */
	public void deleteStudentById(String studentId) throws Exception {
		log.info("deleteStudentById[1]: studentId = {}", studentId);
		List<Student> students = getAllStudents();
		Student searchedStudent = new Student();
		try {
			searchedStudent = students.stream()
					.filter(student -> student.getStudentId().equals(studentId))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("deleteStudentById[2]: there is no student with this id");
			throw new Exception("there is no student with this id");
		}
		log.debug("deleteStudentById[3]: searched student: {}", searchedStudent);
		loggingObject.logObject(searchedStudent, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		boolean removeResult = students.remove(searchedStudent);
		log.info("deleteStudentById[4]: deletion result: {}", removeResult);

		File oldFile = new File(studentsFile);
		File newFile = new File(studentsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteStudentById[5]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(students, newFile.getPath(), Student.class, getObjectFields(searchedStudent));
		log.debug("deleteStudentById[6]: new file {} was created", newFile.getName());
	}

	/**
	 * Uses the {@link IDataProvider#deleteTeacherById(String) deleteTeacherById} method to remove the teacher with the given ID.
	 */
	public void deleteTeacherById(String teacherId) throws Exception {
		log.info("deleteTeacherById[1]: teacherId = {}", teacherId);
		List<Teacher> teachers = getAllTeachers();
		Teacher searchedTeacher = new Teacher();
		try {
			searchedTeacher = teachers.stream()
					.filter(teacher -> teacher.getTeacherId().equals(teacherId))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("deleteTeacherById[2]: there is no teacher with this id");
			throw new Exception("there is no teacher with this id");
		}
		log.debug("deleteTeacherById[3]: searched teacher: {}", searchedTeacher);
		loggingObject.logObject(searchedTeacher, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		boolean removeResult = teachers.remove(searchedTeacher);
		log.info("deleteTeacherById[4]: deletion result: {}", removeResult);

		File oldFile = new File(teachersFile);
		File newFile = new File(teachersFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteTeacherById[5]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(teachers, newFile.getPath(), Teacher.class, getObjectFields(searchedTeacher));
		log.debug("deleteTeacherById[6]: new file {} was created", newFile.getName());
	}

	/**
	 * Uses the {@link IDataProvider#deleteGroupById(String) deleteGroupById} method to remove the group with the given ID.
	 */
	@Override
	public void deleteGroupById(String groupId) throws Exception {
		log.info("deleteGroupByGroupId[1]: groupNumber = {}", groupId);
		List<Group> groups = getAllGroups();
		Group searchedGroup;
		try {
			searchedGroup = groups.stream()
					.filter(group -> group.getGroupNumber().equals(groupId))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("deleteGroupByGroupId[2]: there is no group with this groupNumber");
			throw new Exception("there is no group with this groupNumber");
		}
		log.debug("deleteGroupByGroupId[3]: searched group: {}", searchedGroup);
		loggingObject.logObject(searchedGroup, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		boolean removeResult = groups.remove(searchedGroup);
		log.info("deleteGroupByGroupNumber[4]: deletion result: {}", removeResult);

		File oldFile = new File(groupsFile);
		File newFile = new File(groupsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteGroupByGroupNumber[5]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveGroups(groups);
		log.debug("deleteGroupByGroupNumber[6]: new file {} was created", newFile.getName());
	}

	/**
	 * Uses the {@link IDataProvider#deleteScheduleUnitById(String, TypeOfSchedule) deleteScheduleUnitById} method to remove the schedule unit with the given ID and type.
	 */
	@Override
	public void deleteScheduleUnitById(String scheduleUnitId, TypeOfSchedule type) throws Exception {
		log.info("deleteScheduleUnitById[1]: scheduleUnitId = {}", scheduleUnitId);
		String scheduleUnitsFile = type.equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;
		List<ScheduleUnit> scheduleUnits = getAllScheduleUnits(type);
		ScheduleUnit searchedScheduleUnit;
		try {
			searchedScheduleUnit = scheduleUnits.stream()
					.filter(unit -> unit.getScheduleUnitId().equals(scheduleUnitId))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("deleteScheduleUnitById[2]: there is no schedule unit with this id");
			throw new Exception("there is no schedule unit with this id");
		}
		log.debug("deleteScheduleUnitById[3]: searched schedule unit: {}", searchedScheduleUnit);
		loggingObject.logObject(searchedScheduleUnit, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		boolean removeResult = scheduleUnits.remove(searchedScheduleUnit);
		log.info("deleteScheduleUnitById[4]: deletion result: {}", removeResult);

		File oldFile = new File(scheduleUnitsFile);
		File newFile = new File(scheduleUnitsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteScheduleUnitById[5]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(scheduleUnits, newFile.getPath(), ScheduleUnit.class, getObjectFields(searchedScheduleUnit));
		log.debug("deleteScheduleUnitById[6]: new file {} was created", newFile.getName());
	}

	/**
	 * Uses the {@link IDataProvider#deleteSubjectById(String) deleteSubjectById} method to remove the subject with the given ID.
	 */
	@Override
	public void deleteSubjectById(String subjectId) throws Exception {
		log.info("deleteSubjectById[1]: subjectId = {}", subjectId);
		List<Subject> subjects = getAllSubjects();
		Subject searchedSubject;
		try {
			searchedSubject = subjects.stream()
					.filter(subject -> subject.getSubjectId().equals(subjectId))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("deleteSubjectById[2]: there is no subject with this id");
			throw new Exception("there is no subject with this id");
		}
		log.debug("deleteSubjectById[3]: searched subject: {}", searchedSubject);
		loggingObject.logObject(searchedSubject, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		boolean removeResult = subjects.remove(searchedSubject);
		log.info("deleteSubjectById[4]: deletion result: {}", removeResult);

		File oldFile = new File(subjectsFile);
		File newFile = new File(subjectsFile);

		boolean isFileDeleted = oldFile.delete();
		log.debug("deleteSubjectById[5]: old file {} was deleted: {}", oldFile.getName(), isFileDeleted);

		saveRecords(subjects, newFile.getPath(), Subject.class, getObjectFields(searchedSubject));
		log.debug("deleteSubjectById[6]: new file {} was created", newFile.getName());
	}

	/**
	 * Deletes a specified object from a file and updates the file with the remaining objects.
	 *
	 * This method deletes the provided object from the specified file, updates the file with the remaining objects,
	 * and logs relevant information such as the object type, deletion status, and file operations.
	 *
	 * @param object     The object to be deleted from the file.
	 * @param filePath   The path to the file containing the objects.
	 * @param <T>        The type of the object.
	 */
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

	/**
	 * Uses the {@link IDataProvider#getStudentById(String) getStudentById} method to retrieve data about the student with the given ID.
	 */
	@Override
	public Student getStudentById(String id) throws Exception {
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
			throw new Exception("there is no student with this id");
		}
		log.info("getStudentById[3]: searched student: {}", searchedStudent);
		return searchedStudent;
	}

	/**
	 * Uses the {@link IDataProvider#getTeacherById(String) getTeacherById} method to retrieve data about the teacher with the given ID.
	 */
	@Override
	public Teacher getTeacherById(String id) throws Exception {
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
			throw new Exception("there is no teacher with this id");
		}
		log.info("getTeacherById[3]: searched teacher: {}", searchedTeacher);
		return searchedTeacher;
	}

	/**
	 * Uses the {@link IDataProvider#getGroupById(String) getGroupById} method to retrieve data about the group with the given ID.
	 */
	@Override
	public Group getGroupById(String id) throws Exception {
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
			throw new Exception("there is no group with this id");
		}
		log.info("getGroupById[3]: searched group: {}", searchedGroup);
		return searchedGroup;
	}

	/**
	 * Uses the {@link IDataProvider#getScheduleUnitById(String, TypeOfSchedule) getScheduleUnitById} method to retrieve data about the schedule unit with the given ID and type.
	 */
	@Override
	public ScheduleUnit getScheduleUnitById(String id, TypeOfSchedule type) throws Exception {
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
			throw new Exception("there is no scheduleUnit with this id");
		}
		log.info("getScheduleUnitById[3]: searched schedule unit: {}", searchedScheduleUnit);
		return searchedScheduleUnit;
	}

	/**
	 * Uses the {@link IDataProvider#getSubjectById(String) getSubjectById} method to retrieve data about the subject with the given ID.
	 */
	@Override
	public Subject getSubjectById(String id) throws Exception {
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
			throw new Exception("there is no subject with this id");
		}
		log.info("getSubjectById[3]: searched subject: {}", searchedSubject);
		return searchedSubject;
	}

	/**
	 * Uses the {@link IDataProvider#getAllStudents() getAllStudents} method to get a list of all students in the system.
	 */
	@Override
	public List<Student> getAllStudents() {
		return getAllRecords(studentsFile, Student.class);
	}

	/**
	 * Uses the {@link IDataProvider#getAllTeachers() getAllTeachers} method to get a list of all teachers in the system.
	 */
	@Override
	public List<Teacher> getAllTeachers() {
		return getAllRecords(teachersFile, Teacher.class);
	}

	/**
	 * Uses the {@link IDataProvider#getAllScheduleUnits(TypeOfSchedule) getAllScheduleUnits} method to get a list of all schedule units of the specified type in the system.
	 */
	@Override
	public List<ScheduleUnit> getAllScheduleUnits(TypeOfSchedule type) {
		String scheduleUnitsFile = type.equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;
		return getAllRecords(scheduleUnitsFile, ScheduleUnit.class);
	}

	/**
	 * Uses the {@link IDataProvider#getAllSubjects() getAllSubjects} method to get a list of all subjects in the system.
	 */
	@Override
	public List<Subject> getAllSubjects() {
		return getAllRecords(subjectsFile, Subject.class);
	}

	/**
	 * Saves a list of groups along with their associated students to a CSV file.
	 *
	 * @param groups The list of Group objects to be saved to the CSV file.
	 *
	 */
	public void saveGroups(List<Group> groups) {
		log.debug("saveGroups[1]: save groups: {}", groups);
		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(groupsFile))) {

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

	/**
	 * Uses the {@link IDataProvider#dataTransform(String)} () dataTransform} method to transform data from excel to other format.
	 */
	@Override
	public void dataTransform(String sourceFilePath) throws Exception {
		log.info("dataTransform[1]: transform data from file: {}", sourceFilePath);
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
				.map(item -> validation((EntityInterface) item))
				.filter(result -> !result.isEmpty())
				.toList();

		if (!resultsOfValidation.isEmpty()) {
			log.error("dataTransform[2]: Errors were detected during data validation: {}", resultsOfValidation);
			throw new Exception("Errors were detected during data validation");
		}

		saveRecords(students, studentsFile, Student.class, getObjectFields(new Student()));
		saveGroups(groups);
		saveRecords(subjects, subjectsFile, Subject.class, getObjectFields(new Subject()));
		saveRecords(teachers, teachersFile, Teacher.class, getObjectFields(new Teacher()));
		log.debug("dataTransform[3]: records were saved in CSV files");
	}

}
