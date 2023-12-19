package ru.sfedu.retakescheduler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataUtil {
	private static final Logger log = LogManager.getLogger(DataUtil.class);

	public static HashMap<Object, HashMap<String, String>> validation(EntityInterface object) {
		log.debug("validation[1]: start validation, object: {}", object);
		HashMap<Object, HashMap<String, String>> validationResult = new HashMap<>();
		HashMap<String, String> errors = null;

		TypeOfEntity typeOfEntity = object.getType();
		switch (typeOfEntity) {
			case PERSON -> errors = personValidation((Person) object);
			case GROUP -> errors = groupValidation((Group) object);
			case SUBJECT -> errors = subjectValidation((Subject) object);
		}

		if (!errors.isEmpty()) {
			validationResult.put(object, errors);
		}
		return validationResult;
	}

	private static Map.Entry<String, String> validateField(String value, String regex, String fieldName, String errorMessage) {
		if (value != null && !value.matches(regex)) {
			return new AbstractMap.SimpleEntry<>(fieldName, errorMessage);
		}
		return null;
	}

	private static HashMap<String, String> personValidation(Person person) {
		log.debug("personValidation[1]: person: {}", person);
		HashMap<String, String> errors = Stream.of(
						validateField(person.getLastName(), Constants.NAME_REGEX, Constants.LASTNAME_FIELD, Constants.INCORRECT_LASTNAME),
						validateField(person.getFirstName(), Constants.NAME_REGEX, Constants.FIRSTNAME_FIELD, Constants.INCORRECT_FIRSTNAME),
						validateField(person.getPatronymic(), Constants.PATRONYMIC_REGEX, Constants.PATRONYMIC_FIELD, Constants.INCORRECT_PATRONYMIC)
				)
				.filter(Objects::nonNull)
				.collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);

		log.debug("personValidation[2]: validation errors: {}", errors);
		return errors;
	}

	private static HashMap<String, String> groupValidation(Group group) {
		log.debug("groupValidation[1]: group: {}", group);
		HashMap<String, String> errors = Stream.of(
						validateField(group.getGroupNumber(), Constants.GROUP_NUMBER_REGEX, Constants.GROUP_NUMBER_FIELD, Constants.INCORRECT_GROUP_NUMBER),
						validateField(group.getLevelOfTraining(), Constants.NAME_REGEX, Constants.TRAINING_LEVEL_FIELD, Constants.INCORRECT_TRAINING_LEVEL),
						group.getStudents().isEmpty() ? new AbstractMap.SimpleEntry<>(Constants.GROUP_NUMBER_FIELD, Constants.EMPTY_GROUP) : null
				)
				.filter(Objects::nonNull)
				.collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);

		log.debug("groupValidation[2]: validation errors: {}", errors);
		return errors;
	}

	private static HashMap<String, String> subjectValidation(Subject subject) {
		log.debug("subjectValidation[1]: subject: {}", subject);
		HashMap<String, String> errors = Stream.of(
						validateField(subject.getSubjectName(), Constants.SUBJECT_REGEX, Constants.SUBJECT_FIELD, Constants.INCORRECT_SUBJECT),
						validateField(subject.getControlType(), Constants.CONTROL_TYPE_REGEX, Constants.CONTROL_TYPE_FIELD, Constants.INCORRECT_CONTROL_TYPE)
				)
				.filter(Objects::nonNull)
				.collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);

		log.debug("subjectValidation[2]: validation errors: {}", errors);
		return errors;
	}
	public static List<Student> convertToStudents(List<ExcelRow> excelRows) {
		return new ArrayList<>(excelRows.stream()
				.map(DataUtil::convertToStudent)
				.collect(Collectors.toMap(
						student -> Arrays.asList(student.getLastName(), student.getFirstName(), student.getPatronymic()),
						Function.identity(),
						(existing, replacement) -> existing
				))
				.values());
	}

	public static List<Group> convertToGroups(List<ExcelRow> excelRows) {
		return new ArrayList<>(excelRows.stream()
				.map(DataUtil::convertToGroup)
				.distinct()
				.collect(Collectors.toList()));
	}

	public static List<Subject> convertToSubjects(List<ExcelRow> excelRows) {
		return new ArrayList<>(excelRows.stream()
				.map(DataUtil::convertToSubject)
				.collect(Collectors.toMap(
						subject -> subject.getSubjectName(),
						Function.identity(),
						(existing, replacement) -> existing
				))
				.values());
	}

	public static List<Teacher> convertToTeachers(List<ExcelRow> excelRows) {
		return new ArrayList<>(excelRows.stream()
				.map(DataUtil::convertToTeacher)
				.filter(teacher -> !teacher.getLastName().equals(Constants.PHYSICAL_TRAINING_FIELD))
				.collect(Collectors.toMap(
						teacher -> Arrays.asList(teacher.getLastName(), teacher.getFirstName(), teacher.getPatronymic()),
						Function.identity(),
						(existing, replacement) -> existing
				))
				.values());
	}

	public static Student convertToStudent(ExcelRow excelRow) {
		Student student = new Student();
		String[] studentName = excelRow.getStudentName().split(" ");

		student.setLastName(studentName[0]);

		String firstName = (studentName.length > 1) ? (studentName[1].startsWith("(") ? studentName[2] : studentName[1]) : "";
		String patronymic = (studentName.length > 2) ? (studentName[1].startsWith("(") ? studentName[3] : studentName[2]) : "";

		student.setFirstName(firstName);
		student.setPatronymic(patronymic);

		double avgScore = (Math.random() * 55) + 25;
		student.setAverageScore(Math.round(avgScore * 100.0) / 100.0);

		return student;
	}

	public static Group convertToGroup(ExcelRow excelRow) {
		Group group = new Group();
		group.setGroupNumber(excelRow.getGroup());
		group.setCourse(excelRow.getCourse());
		group.setLevelOfTraining(excelRow.getLevel());
		LocalDate localDate = LocalDate.now();
		localDate = localDate.with(DayOfWeek.TUESDAY);
		group.setBusyDay(localDate);
		return group;
	}

	public static Subject convertToSubject(ExcelRow excelRow) {
		Subject subject = new Subject();
		subject.setSubjectName(excelRow.getDiscipline());
		subject.setControlType(excelRow.getControlType());
		return subject;
	}

	public static Teacher convertToTeacher(ExcelRow excelRow) {
		String[] fullName = excelRow.getTeacherName().split(" ");
		Teacher teacher = new Teacher();
		teacher.setLastName(fullName[0]);
		teacher.setFirstName(fullName.length == 3 ? fullName[1] : "");
		teacher.setPatronymic(fullName.length == 3 ? fullName[2] : "");
		LocalDate currentDate = LocalDate.now();
		Random random = new Random();
		int randomDays = random.nextInt(7);
		LocalDate randomDate = currentDate.plusDays(randomDays);
		teacher.setBusyDay(randomDate);
		return teacher;
	}

	public static List<Student> getStudentsInGroup(String groupNum, List<ExcelRow> excelRows, List<Student> students) {
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

	public static Student findStudent(ExcelRow excelRow, List<Student> students) {
		String fullName = excelRow.getStudentName();
		String res = fullName.replaceAll(Constants.DOUBLE_LASTNAME_REGEX, " ");
		String[] nameParts = (res.split(" ").length < 3) ? Arrays.copyOf(res.split(" "), 3) : res.split(" ");
		nameParts[2] = (nameParts[2] == null) ? "" : nameParts[2];

		// Ищем студента в списке students по полному имени
		return students.stream()
				.filter(student -> nameParts[0].equals(student.getLastName()) && nameParts[1].equals(student.getFirstName()) && nameParts[2].equals(student.getPatronymic()))
				.findFirst()
				.orElseGet(() -> convertToStudent(excelRow)); // Если студент не найден, создаем нового с помощью convertToStudent
	}

	public static <T> String[] getObjectFields(T object) {
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

	public static Group findGroupByDiscipline(List<ExcelRow> excelRows, List<Group> groups, Subject subject) {
		return excelRows.stream()
				.filter(excelRow -> excelRow.getDiscipline().equals(subject.getSubjectName()))
				.map(ExcelRow::getGroup)
				.flatMap(groupNumber -> groups.stream().filter(group -> group.getGroupNumber().equals(groupNumber)).findFirst().stream())
				.findFirst()
				.orElse(null);
	}

	public static void fillTeacherSubjectMapping(List<ExcelRow> excelRows, TeacherSubjectMapping teacherSubjectMapping, List<Teacher> teachers, List<Subject> subjects) {
		excelRows.stream()
				.filter(excelRow -> excelRow.getTeacherName().split(" ").length > 1) // Избавляюсь от ФИЗ-РЫ
				.forEach(excelRow -> {
					String[] teacherNameArr = excelRow.getTeacherName().split(" ");
					String discipline = excelRow.getDiscipline();

					Optional<Teacher> optionalTeacher = teachers.stream()
							.filter(teacher -> teacher.getLastName().equals(teacherNameArr[0])
									&& teacher.getFirstName().equals(teacherNameArr[1])
									&& teacher.getPatronymic().equals(teacherNameArr[2]))
							.findFirst();

					Optional<Subject> optionalSubject = subjects.stream()
							.filter(subject -> subject.getSubjectName().equals(discipline))
							.findFirst();

					optionalTeacher.ifPresent(teacher ->
							optionalSubject.ifPresent(subject ->
									teacherSubjectMapping.addTeacherSubject(teacher, subject)));
				});
	}
}
