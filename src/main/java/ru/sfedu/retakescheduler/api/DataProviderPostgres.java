package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;

import static ru.sfedu.retakescheduler.utils.DataUtil.*;
import static ru.sfedu.retakescheduler.utils.DataUtil.validation;
import static ru.sfedu.retakescheduler.utils.PropertiesConfigUtil.getProperty;
import static ru.sfedu.retakescheduler.utils.PostgresUtil.*;
import static ru.sfedu.retakescheduler.utils.XmlUtil.saveRecords;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class DataProviderPostgres implements IDataProvider{

	private static final Logger log = LogManager.getLogger(DataProviderPostgres.class);
	private final MongoBeanHistory loggingObject = new MongoBeanHistory();


	public DataProviderPostgres() {
		log.info("DataProviderPostgres[1]: created DataProviderPostgres");
		createTables();
	}

	private Connection getConnection() throws SQLException {
		log.debug("getConnection[1]: start getConnection");
		Connection connection = DriverManager.getConnection(
				getProperty(Constants.POSTGRES_JDBC_URL),
				getProperty(Constants.POSTGRES_DB_USER),
				getProperty(Constants.POSTGRES_DB_PASSWORD));
		return connection;
	}

	private void closeConnectionAndStatement(Connection connection, Statement statement) {
		try {
			if (connection != null && statement != null) {
				connection.close();
				statement.close();
				log.info("closeConnectionAndStatement[1]: connection and statement were closed");
			}
		} catch (SQLException e) {
			log.error("closeConnectionAndStatement[2]: error: {}", e.getMessage());
		}
	}

	private void createTables() {
		log.debug("createTables[1]: start create tables");
		try (Connection connection = getConnection();
			Statement statement = connection.createStatement()) {
			statement.executeUpdate(Constants.SQL_CREATE_STUDENT_TABLE);
			statement.executeUpdate(Constants.SQL_CREATE_TEACHER_TABLE);
			statement.executeUpdate(Constants.SQL_CREATE_SUBJECT_TABLE);
			statement.executeUpdate(Constants.SQL_CREATE_GROUP_TABLE);
			statement.executeUpdate(Constants.SQL_CREATE_GROUP_STUDENT_TABLE);
			statement.executeUpdate(String.format(Constants.SQL_CREATE_SCHEDULE_UNIT_TABLE, Constants.MAIN_SCHEDULE_UNITS_TABLE_NAME));
			statement.executeUpdate(String.format(Constants.SQL_CREATE_SCHEDULE_UNIT_TABLE, Constants.RETAKE_SCHEDULE_UNITS_TABLE_NAME));
		} catch (SQLException e) {
			log.error("createTables[2]: initialization error: {}", e.getMessage());
		}
	}

	public static String generateInsertQuery(String template, Object... fields) {
		if (fields.length == 0) {
			return template;
		}

		String sqlQuery = IntStream.range(0, fields.length)
				.boxed()
				.reduce(template, (query, i) -> query.replaceFirst("\\?", getFieldValue(fields[i])), String::concat);

		return sqlQuery;
	}

	private static String getFieldValue(Object field) {
		return (field != null) ? "'" + field.toString() + "'" : "'null'";
	}

	@Override
	public void saveStudent(Student student) throws Exception {
		log.debug("saveStudent[1]: save student: {}", student);
		String sql = generateInsertQuery(
				Constants.SQL_INSERT_STUDENT,
				student.getStudentId(),
				student.getLastName(),
				student.getFirstName(),
				student.getPatronymic(),
				student.getEmail(),
				student.getAverageScore()
		);
		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement()) {
			statement.executeUpdate(sql);
			log.info("saveStudent[2]: student: {} were inserted", student);
		} catch (SQLException e) {
			log.error("saveStudent[3]: error: {}", e.getMessage());
			throw new Exception("student with this id already exists");
		}
		log.debug("saveStudent[4]: sql: {}", sql);
	}

	@Override
	public void saveTeacher(Teacher teacher) throws Exception {
		log.debug("saveTeacher[1]: save teacher: {}", teacher);
		String sql = generateInsertQuery(
				Constants.SQL_INSERT_TEACHER,
				teacher.getTeacherId(),
				teacher.getLastName(),
				teacher.getFirstName(),
				teacher.getPatronymic(),
				teacher.getEmail(),
				teacher.getBusyDay()
		);
		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement()) {
			statement.executeUpdate(sql);
			log.info("saveTeacher[2]: teacher: {} were inserted", teacher);
		} catch (SQLException e) {
			log.error("saveTeacher[3]: error: {}", e.getMessage());
			throw new Exception("teacher with this id already exists");
		}
		log.debug("saveTeacher[2]: sql: {}", sql);
	}

	@Override
	public void saveGroup(Group group) throws Exception {
		log.debug("saveGroup[1]: save group: {}", group);
		String sql = generateInsertQuery(
				Constants.SQL_INSERT_GROUP,
				group.getGroupNumber(),
				group.getCourse(),
				group.getLevelOfTraining(),
				group.getBusyDay()
		);
		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement()) {
			group.getStudents().forEach(student -> {
				try {
					saveStudentIfNotExists(student, connection);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});

			statement.executeUpdate(sql);
			log.info("saveGroup[2]: group: {} were inserted", group);

			group.getStudents().stream()
					.map(Student::getStudentId)
					.forEach(studentId -> saveStudentAndGroupRelation(connection, group.getGroupNumber(), studentId));
		} catch (SQLException e) {
			log.error("saveGroup[3]: error: {}", e.getMessage());
			throw new Exception("group with this id already exists");
		}
		log.debug("saveGroup[2]: sql: {}", sql);
	}

	private void saveStudentIfNotExists(Student student, Connection connection) throws Exception {
		String studentId = student.getStudentId();

		if (!doesStudentExist(connection, studentId)) {
			saveStudent(student);
			log.info("saveStudentIfNotExists[1]: student {} inserted", studentId);
		}
	}

	private boolean doesStudentExist(Connection connection, String studentId) {
		log.debug("doesStudentExist[1]: studentId: {}", studentId);
		try (Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(String.format(Constants.SQL_SELECT_COUNT_STUDENTS, studentId))) {
			resultSet.next();
			int count = resultSet.getInt(1);
			log.debug("doesStudentExist[2]: student exist: {}", count > 0);
			return count > 0;
		} catch (SQLException e) {
			log.error("doesStudentExits[3]: error: {}", e.getMessage());
		}
		return false;
	}

	private void saveStudentAndGroupRelation(Connection connection, String groupNumber, String studentId) {
		log.debug("saveStudentAndGroupRelation[1]: save group: {}, student: {} relation", groupNumber, studentId);
		String sql = generateInsertQuery(
				Constants.SQL_INSERT_GROUP_STUDENT,
				groupNumber,
				studentId
		);
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(sql);
			log.info("saveStudentAndGroupRelation[2]: student {} added to group {}", studentId, groupNumber);
		} catch (SQLException e) {
			log.error("saveStudentAndGroupRelation[3]: error: {}", e.getMessage());
		}
		log.debug("saveStudentAndGroupRelation[4]: sql: {}", sql);
	}

	@Override
	public void saveScheduleUnit(ScheduleUnit scheduleUnit, TypeOfSchedule type) throws Exception {
		log.debug("saveScheduleUnit[1]: save scheduleUnit: {}, type: {}", scheduleUnit, type);
		final String tableName = type.equals(TypeOfSchedule.MAIN) ? Constants.MAIN_SCHEDULE_UNITS_TABLE_NAME : Constants.RETAKE_SCHEDULE_UNITS_TABLE_NAME;
		String sql = generateInsertQuery(
				String.format(Constants.SQL_INSERT_SCHEDULE_UNIT, tableName),
				scheduleUnit.getScheduleUnitId(),
				scheduleUnit.getDateTime(),
				scheduleUnit.getLocation(),
				scheduleUnit.getSubjectId(),
				scheduleUnit.getPersonId(),
				scheduleUnit.getGroupNumber()
		);
		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement()) {
			statement.executeUpdate(sql);
			log.info("saveScheduleUnit[2]: scheduleUnit: {} were inserted", scheduleUnit);
		} catch (SQLException e) {
			log.error("saveScheduleUnit[3]: error: {}", e.getMessage());
			throw new Exception("error during inserting scheduleUnit");
		}
		log.debug("saveScheduleUnit[2]: sql: {}", sql);
	}

	@Override
	public void saveSubject(Subject subject) throws Exception {
		log.debug("saveSubject[1]: save subject: {}", subject);
		String sql = generateInsertQuery(
				Constants.SQL_INSERT_SUBJECT,
				subject.getSubjectId(),
				subject.getSubjectName(),
				subject.getControlType()
		);
		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement()) {
			statement.executeUpdate(sql);
			log.info("saveSubject[2]: subject: {} were inserted", subject);
		} catch (SQLException e) {
			log.error("saveSubject[3]: error: {}", e.getMessage());
			throw new Exception("subject with this id already exists");
		}
		log.debug("saveSubject[2]: sql: {}", sql);
	}

	private <T> void saveEntities(List<T> entities, ThrowingConsumer<T, Exception> saveFunction) {
		entities.forEach(entity -> {
			try {
				saveFunction.accept(entity);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void deleteStudentById(String studentId) throws Exception {
		log.debug("deleteStudentById[1]: delete student with id: {}", studentId);
		String sql = String.format(Constants.SQL_DELETE_ENTITY, Constants.STUDENT_TABLE_NAME, Constants.STUDENT_ID_FIELD, studentId);

		try {
			// Проверяем существование студента перед удалением
			Student student = getStudentById(studentId);

			// Если студент существует, выполняем удаление
			try (Connection connection = getConnection();
			     Statement statement = connection.createStatement()
			) {
				int rowsAffected = statement.executeUpdate(sql);
				if (rowsAffected <= 0) {
					throw new Exception("Failed to delete student with id: " + studentId);
				}
				loggingObject.logObject(student, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);
			}
		} catch (Exception e) {
			log.error("deleteStudentById[2]: error: {}", e.getMessage());
			throw new Exception("there is no student with this id");
		}
	}

	@Override
	public void deleteTeacherById(String teacherId) throws Exception {
		log.debug("deleteTeacherById[1]: delete teacher with id: {}", teacherId);
		String sql = String.format(Constants.SQL_DELETE_ENTITY, Constants.TEACHER_TABLE_NAME, Constants.TEACHER_ID_FIELD, teacherId);

		try {
			// Проверяем существование преподавателя перед удалением
			Teacher teacher = getTeacherById(teacherId);

			// Если преподаватель существует, выполняем удаление
			try (Connection connection = getConnection();
			     Statement statement = connection.createStatement()
			) {
				int rowsAffected = statement.executeUpdate(sql);
				if (rowsAffected <= 0) {
					throw new Exception("Failed to delete teacher with id: " + teacherId);
				}
				loggingObject.logObject(teacher, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);
			}
		} catch (Exception e) {
			log.error("deleteTeacherById[2]: error: {}", e.getMessage());
			throw new Exception("there is no teacher with this id");
		}
	}

	@Override
	public void deleteGroupById(String groupId) throws Exception {
		log.debug("deleteGroupById[1]: delete group with id: {}", groupId);
		String sql = String.format(Constants.SQL_DELETE_ENTITY, Constants.GROUP_TABLE_NAME, Constants.GROUP_ID_FIELD, groupId);

		try {
			// Проверяем существование группы перед удалением
			Group group = getGroupById(groupId);

			// Если группа существует, выполняем удаление
			try (Connection connection = getConnection();
			     Statement statement = connection.createStatement()
			) {
				int rowsAffected = statement.executeUpdate(sql);
				if (rowsAffected <= 0) {
					throw new Exception("Failed to delete group with id: " + groupId);
				}
				loggingObject.logObject(group, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);
			}
		} catch (Exception e) {
			log.error("deleteGroupById[2]: error: {}", e.getMessage());
			throw new Exception("there is no group with this id");
		}
	}

	@Override
	public void deleteScheduleUnitById(String scheduleUnitId, TypeOfSchedule type) throws Exception {
		log.debug("deleteScheduleUnitById[1]: delete scheduleUNit with id: {}", scheduleUnitId);
		final String tableName = type.equals(TypeOfSchedule.MAIN) ? Constants.MAIN_SCHEDULE_UNITS_TABLE_NAME : Constants.RETAKE_SCHEDULE_UNITS_TABLE_NAME;
		String sql = String.format(Constants.SQL_DELETE_ENTITY, tableName, Constants.SCHEDULE_UNIT_ID_FIELD, scheduleUnitId);

		try {
			// Проверяем существование ячейки расписания перед удалением
			ScheduleUnit scheduleUnit = getScheduleUnitById(scheduleUnitId, type);

			// Если ячейка расписания существует, выполняем удаление
			try (Connection connection = getConnection();
			     Statement statement = connection.createStatement()
			) {
				int rowsAffected = statement.executeUpdate(sql);
				if (rowsAffected <= 0) {
					throw new Exception("Failed to delete scheduleUnit with id: " + scheduleUnitId);
				}
				loggingObject.logObject(scheduleUnit, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);
			}
		} catch (Exception e) {
			log.error("deleteScheduleUnitById[2]: error: {}", e.getMessage());
			throw new Exception("there is no scheduleUnit with this id");
		}
	}

	@Override
	public void deleteSubjectById(String subjectId) throws Exception {
		log.debug("deleteSubjectById[1]: delete subject with id: {}", subjectId);
		String sql = String.format(Constants.SQL_DELETE_ENTITY, Constants.SUBJECT_ID_FIELD, Constants.SUBJECT_ID_FIELD, subjectId);

		try {
			// Проверяем существование предмета перед удалением
			Subject subject = getSubjectById(subjectId);

			// Если предмет существует, выполняем удаление
			try (Connection connection = getConnection();
			     Statement statement = connection.createStatement()
			) {
				int rowsAffected = statement.executeUpdate(sql);
				if (rowsAffected <= 0) {
					throw new Exception("Failed to delete subject with id: " +subjectId);
				}
				loggingObject.logObject(subject, Thread.currentThread().getStackTrace()[1].getMethodName(), Status.SUCCESS);
			}
		} catch (Exception e) {
			log.error("deleteSubjectById[2]: error: {}", e.getMessage());
			throw new Exception("there is no subject with this id");
		}
	}

	@Override
	public Student getStudentById(String id) throws Exception {
		log.debug("getStudentById[1]: get student with id: {}", id);
		String sql = String.format(Constants.SQL_SELECT_BY_ID, Constants.STUDENT_TABLE_NAME, Constants.STUDENT_ID_FIELD, id);

		try (Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql)
		) {
			Student student = null;
			if (resultSet.next()) {
				student = mapResultSetToStudent(resultSet);
			}

			return Optional.ofNullable(student)
					.orElseThrow(() -> {
						log.error("getStudentById[2]: there is no student with this id");
						return new Exception("there is no student with this id");
					});
		}
	}

	@Override
	public Teacher getTeacherById(String id) throws Exception {
		log.debug("getTeacherById[1]: get teacher with id: {}", id);
		String sql = String.format(Constants.SQL_SELECT_BY_ID, Constants.TEACHER_TABLE_NAME, Constants.TEACHER_ID_FIELD, id);

		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(sql)
		) {
			Teacher teacher = null;
			if (resultSet.next()) {
				teacher = mapResultSetToTeacher(resultSet);
			}

			return Optional.ofNullable(teacher)
					.orElseThrow(() -> {
						log.error("getTeacherById[2]: there is no teacher with this id");
						return new Exception("there is no teacher with this id");
					});
		}
	}

	@Override
	public Group getGroupById(String id) throws Exception {
		log.debug("getGroupById[1]: get group with id: {}", id);
		String sql = String.format(Constants.SQL_SELECT_BY_ID, Constants.GROUP_TABLE_NAME, Constants.GROUP_ID_FIELD, id);

		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(sql)
		) {
			Group group = null;
			if (resultSet.next()) {
				group = mapResultSetToGroup(resultSet);
				List<Student> students = getStudentsByGroup(group.getGroupNumber());
				group.setStudents(students);
			}

			return Optional.ofNullable(group)
					.orElseThrow(() -> {
						log.error("getGroupById[2]: there is no group with this id");
						return new Exception("there is no group with this id");
					});
		}
	}

	private List<Student> getStudentsByGroup(String groupNumber) {
		log.debug("getStudentsByGroup[1]: get students from group: {}", groupNumber);
		String sql = String.format(Constants.SQL_SELECT_STUDENTS_ID_BY_GROUP, groupNumber);
		List<Student> students = new ArrayList<>();

		try (Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql)
		) {
			while (resultSet.next()) {
				String studentId = resultSet.getString(Constants.STUDENT_ID_FIELD);
				Student student = getStudentById(studentId);
				students.add(student);
			}
		} catch (Exception e) {
			log.error("getStudentByGroup[3]: error: {}", e.getMessage());
		}
		return students;
	}

	@Override
	public ScheduleUnit getScheduleUnitById(String id, TypeOfSchedule type) throws Exception {
		log.debug("getScheduleUnitById[1]: get scheduleUnit with id: {}", id);
		final String tableName = type.equals(TypeOfSchedule.MAIN) ? Constants.MAIN_SCHEDULE_UNITS_TABLE_NAME : Constants.RETAKE_SCHEDULE_UNITS_TABLE_NAME;
		String sql = String.format(Constants.SQL_SELECT_BY_ID, tableName, Constants.SCHEDULE_UNIT_ID_FIELD, id);

		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(sql)
		) {
			ScheduleUnit scheduleUnit = null;
			if (resultSet.next()) {
				scheduleUnit = mapResultSetToScheduleUnit(resultSet);
			}

			return Optional.ofNullable(scheduleUnit)
					.orElseThrow(() -> {
						log.error("getScheduleUnitById[2]: there is no scheduleUnit with this id");
						return new Exception("there is no scheduleUnit with this id");
					});
		}
	}

	@Override
	public Subject getSubjectById(String id) throws Exception {
		log.debug("getSubjectById[1]: get subject with id: {}", id);
		String sql = String.format(Constants.SQL_SELECT_BY_ID, Constants.SUBJECT_TABLE_NAME, Constants.SUBJECT_ID_FIELD, id);

		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(sql)
		) {
			Subject subject = null;
			if (resultSet.next()) {
				subject = mapResultSetToSubject(resultSet);
			}

			return Optional.ofNullable(subject)
					.orElseThrow(() -> {
						log.error("getSubjectById[2]: there is no subject with this id");
						return new Exception("there is no subject with this id");
					});
		}
	}

	@Override
	public List<Student> getAllStudents() {
		log.debug("getAllStudents[1]: get all records from students table");
		String sql = String.format(Constants.SQL_SELECT_ALL_RECORDS, Constants.STUDENT_TABLE_NAME);
		List<Student> students = new ArrayList<>();

		try (Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql)
		) {
			while (resultSet.next()) {
				students.add(mapResultSetToStudent(resultSet));
			}
		} catch (SQLException e) {
			log.error("getAllStudents[2]: error: {}", e.getMessage());
		}
		log.debug("getAllStudents[3]: students: {}", students);
		return students;
	}

	@Override
	public List<Teacher> getAllTeachers() {
		log.debug("getAllTeachers[1]: get all records from teachers table");
		String sql = String.format(Constants.SQL_SELECT_ALL_RECORDS, Constants.TEACHER_TABLE_NAME);
		List<Teacher> teachers = new ArrayList<>();

		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(sql)
		) {
			while (resultSet.next()) {
				teachers.add(mapResultSetToTeacher(resultSet));
			}
		} catch (SQLException e) {
			log.error("getAllTeachers[2]: error: {}", e.getMessage());
		}
		log.debug("getAllTeachers[3]: students: {}", teachers);
		return teachers;
	}

	@Override
	public List<Group> getAllGroups() {
		log.debug("getAllGroups[1]: get all records from groups table");
		String sql = String.format(Constants.SQL_SELECT_ALL_RECORDS, Constants.GROUP_TABLE_NAME);
		List<Group> groups = new ArrayList<>();

		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(sql)
		) {
			while (resultSet.next()) {
				Group group = mapResultSetToGroup(resultSet);
				List<Student> students = getStudentsByGroup(group.getGroupNumber());
				group.setStudents(students);
				groups.add(group);
			}
		} catch (SQLException e) {
			log.error("getAllGroups[2]: error: {}", e.getMessage());
		}
		log.debug("getAllGroups[3]: groups: {}", groups);
		return groups;
	}

	@Override
	public List<ScheduleUnit> getAllScheduleUnits(TypeOfSchedule type) {
		log.debug("getAllTeachers[1]: get all records from teachers table");
		final String tableName = type.equals(TypeOfSchedule.MAIN) ? Constants.MAIN_SCHEDULE_UNITS_TABLE_NAME : Constants.RETAKE_SCHEDULE_UNITS_TABLE_NAME;
		String sql = String.format(Constants.SQL_SELECT_ALL_RECORDS, tableName);
		List<ScheduleUnit> scheduleUnits = new ArrayList<>();

		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(sql)
		) {
			while (resultSet.next()) {
				scheduleUnits.add(mapResultSetToScheduleUnit(resultSet));
			}
		} catch (SQLException e) {
			log.error("getAllScheduleUnits[2]: error: {}", e.getMessage());
		}
		log.debug("getAllScheduleUnits[3]: scheduleUnits: {}", scheduleUnits);
		return scheduleUnits;
	}

	@Override
	public List<Subject> getAllSubjects() {
		log.debug("getAllSubjects[1]: get all records from subjects table");
		String sql = String.format(Constants.SQL_SELECT_ALL_RECORDS, Constants.SUBJECT_TABLE_NAME);
		List<Subject> subjects = new ArrayList<>();

		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(sql)
		) {
			while (resultSet.next()) {
				subjects.add(mapResultSetToSubject(resultSet));
			}
		} catch (SQLException e) {
			log.error("getAllSubjects[2]: error: {}", e.getMessage());
		}
		log.debug("getAllSubjects[3]: subjects: {}", subjects);
		return subjects;
	}

	@Override
	public void dataTransform(String sourceFilePath) throws Exception {
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
				.map(item -> validation((EntityInterface) item))
				.filter(result -> !result.isEmpty())
				.toList();

		if (!resultsOfValidation.isEmpty()) {
			log.error("dataTransform[2]: Errors were detected during data validation: {}", resultsOfValidation);
			throw new Exception("Errors were detected during data validation");
		}

		saveEntities(students, this::saveStudent);
		saveEntities(teachers, this::saveTeacher);
		saveEntities(groups, this::saveGroup);
		saveEntities(subjects, this::saveSubject);
		log.debug("dataTransform[3]: records were saved in PostgresDB files");
	}
}
