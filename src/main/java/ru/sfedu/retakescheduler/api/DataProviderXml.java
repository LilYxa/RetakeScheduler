package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;

import java.io.IOException;
import java.util.*;

import static ru.sfedu.retakescheduler.utils.DataUtil.*;
import static ru.sfedu.retakescheduler.utils.FileUtil.*;
import static ru.sfedu.retakescheduler.utils.XmlUtil.*;

public class DataProviderXml implements IDataProvider{

	private static final Logger log = LogManager.getLogger(DataProviderXml.class.getName());
	private final MongoBeanHistory loggingObject = new MongoBeanHistory();

	private final String studentsFile;
	private final String teachersFile;
	private final String groupsFile;
	private final String subjectsFile;
	private final String mainScheduleUnitsFile;
	private final String retakeScheduleUnitsFile;

	public DataProviderXml() {
		this(Constants.XML_FOLDER_PATH);
	}

	public DataProviderXml(String pathToXml) {
		log.debug("DataProviderXml[1]: created DataProviderXml");
		String finalFolder = pathToXml.equals(Constants.XML_FOLDER_PATH) ? Constants.XML_FOLDER_PATH : pathToXml.concat(Constants.XML_FOLDER);
		studentsFile = finalFolder.concat(Constants.STUDENT_FILE).concat(Constants.XML_FILE_TYPE);
		teachersFile = finalFolder.concat(Constants.TEACHER_FILE).concat(Constants.XML_FILE_TYPE);
		groupsFile = finalFolder.concat(Constants.GROUP_FILE).concat(Constants.XML_FILE_TYPE);
		subjectsFile = finalFolder.concat(Constants.SUBJECT_FILE).concat(Constants.XML_FILE_TYPE);
		mainScheduleUnitsFile = finalFolder.concat(Constants.MAIN_SCHEDULE_UNIT_FILE).concat(Constants.XML_FILE_TYPE);
		retakeScheduleUnitsFile = finalFolder.concat(Constants.RETAKE_SCHEDULE_UNIT_FILE).concat(Constants.XML_FILE_TYPE);

		try {
			createFolderIfNotExists(finalFolder);
			createNecessaryFiles();
		} catch (IOException e) {
			log.error("DataProviderXml[2]: initialisation error: {}", e.getMessage());
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
	public void saveStudent(Student student) throws Exception {
		log.debug("saveStudent[1]: save {}", student);
		List<Student> students = getAllStudents();

		checkIfEntityExist(students, student, "this student already exists");
		students.add(student);
		saveRecord(student, studentsFile, Student.class);
		log.info("saveStudent[1]: student: {} were saved", student);
	}

	/**
	 * Uses the {@link IDataProvider#saveTeacher(Teacher) saveTeacher} method to store data about the teacher.
	 */
	@Override
	public void saveTeacher(Teacher teacher) throws Exception {
		log.debug("saveTeacher[1]: save {}", teacher);
		List<Teacher> teachers = getAllTeachers();

		checkIfEntityExist(teachers, teacher, "this teacher already exists");
		teachers.add(teacher);
		saveRecord(teacher, teachersFile, Teacher.class);
		log.info("saveTeacher[1]: teacher: {} were saved", teacher);
	}

	/**
	 * Uses the {@link IDataProvider#saveGroup(Group) saveGroup} method to store data about the group.
	 */
	@Override
	public void saveGroup(Group group) throws Exception {
		log.debug("saveGroup[1]: save {}", group);
		List<Group> groups = getAllGroups();

		checkIfEntityExist(groups, group, "this group already exists");
		groups.add(group);
		saveRecord(group, groupsFile, Group.class);
		group.getStudents().forEach(student -> {
			try {
				saveStudentIfNotExist(student, this);
			} catch (Exception e) {
				log.debug("saveGroup[2]: student {} already exists", student);
			}
		});
		log.info("saveGroup[3]: group: {} were saved", group);
	}

	/**
	 * Uses the {@link IDataProvider#saveScheduleUnit(ScheduleUnit, TypeOfSchedule) saveScheduleUnit} method to store data about the schedule unit.
	 */
	@Override
	public void saveScheduleUnit(ScheduleUnit scheduleUnit, TypeOfSchedule type) throws Exception {
		log.debug("saveScheduleUnit[1]: save ScheduleUnit: {}", scheduleUnit);
		String scheduleUnitsFile = type.equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;
		List<ScheduleUnit> scheduleUnits = getAllScheduleUnits(type);

		checkIfEntityExist(scheduleUnits, scheduleUnit, "this scheduleUnit already exists");
		checkScheduleUnitData(scheduleUnit, this);
		scheduleUnits.add(scheduleUnit);
		saveRecord(scheduleUnit, scheduleUnitsFile, ScheduleUnit.class);
		log.info("saveScheduleUnit[1]: schedule unit: {} were saved", scheduleUnit);
	}

	/**
	 * Uses the {@link IDataProvider#saveSchedule(Schedule) saveSchedule} method to store data about the schedule.
	 */
	public void saveSchedule(Schedule schedule) {
		log.debug("saveSchedule[1]: saving {} schedule: {}", schedule.getTypeOfSchedule(), schedule);
		String scheduleUnitsFile = schedule.getTypeOfSchedule().equals(TypeOfSchedule.MAIN) ? mainScheduleUnitsFile : retakeScheduleUnitsFile;
		saveRecords(schedule.getUnits(), scheduleUnitsFile, ScheduleUnit.class);
	}

	/**
	 * Uses the {@link IDataProvider#saveSubject(Subject) saveSubject} method to store data about the subject.
	 */
	@Override
	public void saveSubject(Subject subject) throws Exception {
		log.debug("saveSubject[1]: save {}", subject);
		List<Subject> subjects = getAllSubjects();

		checkIfEntityExist(subjects, subject, "this subject already exists");
		subjects.add(subject);
		saveRecord(subject, subjectsFile, Subject.class);
		log.info("saveSubject[1]: subject: {} were saved", subject);
	}

	/**
	 * Uses the {@link IDataProvider#deleteStudentById(String) deleteStudentById} method to remove the student with the given ID.
	 */
	@Override
	public void deleteStudentById(String studentId) throws Exception {
		log.info("deleteStudentById[1]: studentId: {}", studentId);
		List<Student> students = getAllStudents();
		Student searchedStudent;

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

		deleteFileOrFolder(studentsFile);
		createFileIfNotExists(studentsFile);
		saveRecords(students, studentsFile, Student.class);
	}

	/**
	 * Uses the {@link IDataProvider#deleteTeacherById(String) deleteTeacherById} method to remove the teacher with the given ID.
	 */
	@Override
	public void deleteTeacherById(String teacherId) throws Exception {
		log.info("deleteTeacherById[1]: teacherId: {}", teacherId);
		List<Teacher> teachers = getAllTeachers();
		Teacher searchedTeacher;

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

		deleteFileOrFolder(teachersFile);
		createFileIfNotExists(teachersFile);
		saveRecords(teachers, teachersFile, Teacher.class);
	}

	/**
	 * Uses the {@link IDataProvider#deleteGroupById(String) deleteGroupById} method to remove the group with the given ID.
	 */
	@Override
	public void deleteGroupById(String groupId) throws Exception {
		log.info("deleteGroupById[1]: groupNumber: {}", groupId);
		List<Group> groups = getAllGroups();
		Group searchedGroup;

		try {
			searchedGroup = groups.stream()
					.filter(group -> group.getGroupNumber().equals(groupId))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("deleteGroupById[2]: there is no group with this id");
			throw new Exception("there is no group with this id");
		}
		log.debug("deleteGroupById[3]: searched group: {}", searchedGroup);
		loggingObject.logObject(searchedGroup, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);

		boolean removeResult = groups.remove(searchedGroup);
		log.info("deleteGroupById[4]: deletion result: {}", removeResult);

		deleteFileOrFolder(groupsFile);
		createFileIfNotExists(groupsFile);
		saveRecords(groups, groupsFile, Group.class);
	}

	/**
	 * Uses the {@link IDataProvider#deleteScheduleUnitById(String, TypeOfSchedule) deleteScheduleUnitById} method to remove the schedule unit with the given ID and type.
	 */
	@Override
	public void deleteScheduleUnitById(String scheduleUnitId, TypeOfSchedule type) throws Exception {
		log.info("deleteScheduleUnitById[1]: scheduleUnitId: {}", scheduleUnitId);
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

		deleteFileOrFolder(scheduleUnitsFile);
		createFileIfNotExists(scheduleUnitsFile);
		saveRecords(scheduleUnits, scheduleUnitsFile, ScheduleUnit.class);
	}

	/**
	 * Uses the {@link IDataProvider#deleteSubjectById(String) deleteSubjectById} method to remove the subject with the given ID.
	 */
	@Override
	public void deleteSubjectById(String subjectId) throws Exception {
		log.info("deleteSubjectById[1]: subjectId: {}", subjectId);
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

		deleteFileOrFolder(subjectsFile);
		createFileIfNotExists(subjectsFile);
		saveRecords(subjects, subjectsFile, Subject.class);
	}

	/**
	 * Uses the {@link IDataProvider#getStudentById(String) getStudentById} method to retrieve data about the student with the given ID.
	 */
	@Override
	public Student getStudentById(String id) throws Exception {
		log.debug("getStudentById[1]: studentId = {}", id);
		List<Student> students = getAllStudents();
		Student searchedStudent;
		try {
			searchedStudent = students.stream()
					.filter(student -> student.getStudentId().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getStudentById[2]: there is no student with this id");
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
		log.debug("getTeacherById[1]: teacherId = {}", id);
		List<Teacher> teachers = getAllTeachers();
		Teacher searchedTeacher;
		try {
			searchedTeacher = teachers.stream()
					.filter(teacher -> teacher.getTeacherId().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getTeacherById[2]: there is no teacher with this id");
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
		log.debug("getGroupById[1]: groupId = {}", id);
		List<Group> groups = getAllGroups();
		Group searchedGroup;
		try {
			searchedGroup = groups.stream()
					.filter(group -> group.getGroupNumber().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getGroupById[2]: there is no group with this id");
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
		log.debug("getScheduleUnitById[1]: scheduleUnitId = {}", id);
		List<ScheduleUnit> scheduleUnits = getAllScheduleUnits(type);
		ScheduleUnit searchedScheduleUnit;
		try {
			searchedScheduleUnit = scheduleUnits.stream()
					.filter(scheduleUnit -> scheduleUnit.getScheduleUnitId().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getScheduleUnitById[2]: there is no scheduleUnit with this id");
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
		log.debug("getSubjectById[1]: subjectId = {}", id);
		List<Subject> subjects = getAllSubjects();
		Subject searchedSubject;
		try {
			searchedSubject = subjects.stream()
					.filter(subject -> subject.getSubjectId().equals(id))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			log.error("getSubjectById[2]: there is no subject with this id");
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
	 * Uses the {@link IDataProvider#getAllGroups() getAllGroups} method to get a list of all groups in the system.
	 */
	@Override
	public List<Group> getAllGroups() {
		return getAllRecords(groupsFile, Group.class);
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

		saveRecords(students, studentsFile, Student.class);
		saveRecords(groups, groupsFile, Group.class);
		saveRecords(subjects, subjectsFile, Subject.class);
		saveRecords(teachers, teachersFile, Teacher.class);
		log.debug("dataTransform[3]: records were saved in XML files");
	}
}
