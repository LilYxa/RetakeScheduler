package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;
import ru.sfedu.retakescheduler.utils.mappers.StudentEntityMapper;
import ru.sfedu.retakescheduler.utils.mappers.SubjectEntityMapper;
import ru.sfedu.retakescheduler.utils.mappers.TeacherEntityMapper;

import static ru.sfedu.retakescheduler.utils.DataUtil.*;
import static ru.sfedu.retakescheduler.utils.DataUtil.validation;
import static ru.sfedu.retakescheduler.utils.PropertiesConfigUtil.getProperty;
import static ru.sfedu.retakescheduler.utils.PostgresUtil.*;

import java.sql.*;
import java.util.*;
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

	/**
	 * Creates tables in the database required for storing information about students, teachers, subjects, groups,
	 * group-student associations, and schedule units.
	 */
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

	/**
	 * Uses the {@link IDataProvider#saveStudent(Student) saveStudent} method to store data about the student.
	 */
	@Override
	public void saveStudent(Student student) throws Exception {
		log.info("saveStudent[1]: save student: {}", student);
		String sql = String.format(
				Constants.SQL_INSERT_STUDENT_TEST,
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

	/**
	 * Uses the {@link IDataProvider#saveTeacher(Teacher) saveTeacher} method to store data about the teacher.
	 */
	@Override
	public void saveTeacher(Teacher teacher) throws Exception {
		log.info("saveTeacher[1]: save teacher: {}", teacher);
		String sql = String.format(
				Constants.SQL_INSERT_TEACHER_TEST,
				teacher.getTeacherId(),
				teacher.getLastName(),
				teacher.getFirstName(),
				teacher.getPatronymic(),
				teacher.getEmail(),
				teacher.getBusyDay().toString()
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

	/**
	 * Uses the {@link IDataProvider#saveGroup(Group) saveGroup} method to store data about the group.
	 */
	@Override
	public void saveGroup(Group group) throws Exception {
		log.info("saveGroup[1]: save group: {}", group);
		String sql = String.format(
				Constants.SQL_INSERT_GROUP_TEST,
				group.getGroupNumber(),
				group.getCourse(),
				group.getLevelOfTraining(),
				group.getBusyDay().toString()
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

	/**
	 * Saves a student to the database if the student does not already exist.
	 *
	 * @param student    The student to be saved.
	 * @param connection The database connection.
	 *
	 * @throws Exception    If an exception occurs during the student saving process.
	 *                      If an exception occurs during execution of an SQL query
	 */
	private void saveStudentIfNotExists(Student student, Connection connection) throws Exception {
		String studentId = student.getStudentId();

		if (!doesStudentExist(connection, studentId)) {
			saveStudent(student);
			log.info("saveStudentIfNotExists[1]: student {} inserted", studentId);
		}
	}

	/**
	 * Checks if a student with the specified student ID exists in the database.
	 *
	 * @param connection The database connection.
	 * @param studentId  The student ID to check for existence.
	 *
	 * @return true if a student with the specified ID exists in the database, false otherwise.
	 *
	 */
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

	/**
	 * Saves the relationship between a student and a group in the database.
	 *
	 * @param connection  The database connection.
	 * @param groupNumber The group number to which the student belongs.
	 * @param studentId   The student ID for whom the relationship is being established.
	 *
	 */
	private void saveStudentAndGroupRelation(Connection connection, String groupNumber, String studentId) {
		log.debug("saveStudentAndGroupRelation[1]: save group: {}, student: {} relation", groupNumber, studentId);
		String sql = String.format(
				Constants.SQL_INSERT_GROUP_STUDENT_TEST,
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

	/**
	 * Uses the {@link IDataProvider#saveScheduleUnit(ScheduleUnit, TypeOfSchedule) saveScheduleUnit} method to store data about the schedule unit.
	 */
	@Override
	public void saveScheduleUnit(ScheduleUnit scheduleUnit, TypeOfSchedule type) throws Exception {
		log.info("saveScheduleUnit[1]: save scheduleUnit: {}, type: {}", scheduleUnit, type);
		final String tableName = type.equals(TypeOfSchedule.MAIN) ? Constants.MAIN_SCHEDULE_UNITS_TABLE_NAME : Constants.RETAKE_SCHEDULE_UNITS_TABLE_NAME;
		String sql = String.format(
				Constants.SQL_INSERT_SCHEDULE_UNIT_TEST,
				tableName,
				scheduleUnit.getScheduleUnitId(),
				scheduleUnit.getDateTime().toString(),
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

	/**
	 * Uses the {@link IDataProvider#saveSubject(Subject) saveSubject} method to store data about the subject.
	 */
	@Override
	public void saveSubject(Subject subject) throws Exception {
		log.info("saveSubject[1]: save subject: {}", subject);
		String sql = String.format(
				Constants.SQL_INSERT_SUBJECT_TEST,
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

	/**
	 * Uses the {@link IDataProvider#saveSchedule(Schedule) saveSchedule} method to store data about the schedule.
	 */
	public void saveSchedule(Schedule schedule) throws Exception {
		log.info("saveSchedule[1]: saving {} schedule: {}", schedule.getTypeOfSchedule(), schedule);
		final String tableName = schedule.getTypeOfSchedule().equals(TypeOfSchedule.MAIN) ? Constants.MAIN_SCHEDULE_UNITS_TABLE_NAME : Constants.RETAKE_SCHEDULE_UNITS_TABLE_NAME;
		try (Connection connection = getConnection();
			Statement statement = connection.createStatement()
		) {
			for (ScheduleUnit scheduleUnit : schedule.getUnits()) {
				String sql = String.format(
						Constants.SQL_INSERT_SCHEDULE_UNIT_TEST,
						tableName,
						scheduleUnit.getScheduleUnitId(),
						scheduleUnit.getDateTime().toString(),
						scheduleUnit.getLocation(),
						scheduleUnit.getSubjectId(),
						scheduleUnit.getPersonId(),
						scheduleUnit.getGroupNumber()
				);
				statement.addBatch(sql);
				log.info("saveSchedule[2]: scheduleUnit: {} added to batch", scheduleUnit);
			}
			statement.executeBatch();
			log.info("saveSchedule[3]: all scheduleUnits were inserted");
		} catch (SQLException e) {
			log.error("saveSchedule[2]: error: {}", e.getMessage());
			throw new Exception(e);
		}
	}

	/**
	 * Saves a list of groups and their associated students to the database using batch processing.
	 *
	 * @param groups The list of Group objects to be saved to the database.
	 *
	 * @throws Exception    If an exception occurs during the saving process.
	 *                      If an SQL exception occurs while executing the batch insert or establishing relationships.
	 */
	public void saveGroups(List<Group> groups) throws Exception {
		log.debug("saveGroups[1]: save groups: {}", groups);

		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement()) {

			for (Group group : groups) {
				String sql = String.format(
						Constants.SQL_INSERT_GROUP_TEST,
						group.getGroupNumber(),
						group.getCourse(),
						group.getLevelOfTraining(),
						group.getBusyDay().toString()
				);

				group.getStudents().forEach(student -> {
					try {
						saveStudentIfNotExists(student, connection);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});

				statement.addBatch(sql);
				log.info("saveGroup[2]: group: {} added to batch", group);
			}

			// Выполнение всех запросов в одной транзакции
			statement.executeBatch();

			groups.forEach(group -> {
				group.getStudents().stream()
						.map(Student::getStudentId)
						.forEach(studentId -> saveStudentAndGroupRelation(connection, group.getGroupNumber(), studentId));
			});
			log.info("saveGroups[3]: all groups were inserted");
		} catch (SQLException e) {
			log.error("saveGroups[4]: error: {}", e.getMessage());
			throw new Exception("Error during batch insert", e);
		}
	}

	/**
	 * Saves a list of entities to the database using batch processing.
	 *
	 * @param entities       The list of entities to be saved to the database.
	 * @param insertSqlFormat The SQL insert statement format with placeholders for entity values.
	 * @param entityMapper   The mapper that transforms each entity into a set of values for the insert statement.
	 * @param <T>            The type of entities being saved.
	 *
	 * @throws Exception    If an exception occurs during the saving process.
	 *                      If an SQL exception occurs while executing the batch insert.
	 */
	public <T> void saveEntities(List<T> entities, String insertSqlFormat, EntityMapper<T> entityMapper) throws Exception {
		log.debug("saveEntities[1]: saving entities: {}", entities);
		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement()) {

			for (T entity : entities) {
				String insertSql = String.format(insertSqlFormat, entityMapper.mapEntity(entity));
				statement.addBatch(insertSql);
			}

			statement.executeBatch();
		} catch (SQLException e) {
			log.error("saveEntities[]: error: {}", e.getMessage());
			throw new Exception("Error during batch insert", e);
		}
	}

	/**
	 * Uses the {@link IDataProvider#deleteStudentById(String) deleteStudentById} method to remove the student with the given ID.
	 */
	@Override
	public void deleteStudentById(String studentId) throws Exception {
		log.info("deleteStudentById[1]: delete student with id: {}", studentId);
		String sql = String.format(Constants.SQL_DELETE_ENTITY, Constants.STUDENT_TABLE_NAME, Constants.STUDENT_ID_FIELD, studentId);

		try {
			// Проверяем существование студента перед удалением
			Student student = getStudentById(studentId);
			log.debug("deleteStudentById[2]: student: {}", student);

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
			log.error("deleteStudentById[3]: error: {}", e.getMessage());
			throw new Exception("there is no student with this id");
		}
	}

	/**
	 * Uses the {@link IDataProvider#deleteTeacherById(String) deleteTeacherById} method to remove the teacher with the given ID.
	 */
	@Override
	public void deleteTeacherById(String teacherId) throws Exception {
		log.debug("deleteTeacherById[1]: delete teacher with id: {}", teacherId);
		String sql = String.format(Constants.SQL_DELETE_ENTITY, Constants.TEACHER_TABLE_NAME, Constants.TEACHER_ID_FIELD, teacherId);

		try {
			// Проверяем существование преподавателя перед удалением
			Teacher teacher = getTeacherById(teacherId);
			log.debug("deleteTeacherById[2]: teacher: {}", teacher);

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

	/**
	 * Uses the {@link IDataProvider#deleteGroupById(String) deleteGroupById} method to remove the group with the given ID.
	 */
	@Override
	public void deleteGroupById(String groupId) throws Exception {
		log.debug("deleteGroupById[1]: delete group with id: {}", groupId);
		String sql = String.format(Constants.SQL_DELETE_ENTITY, Constants.GROUP_TABLE_NAME, Constants.GROUP_ID_FIELD, groupId);

		try {
			// Проверяем существование группы перед удалением
			Group group = getGroupById(groupId);
			log.debug("deleteGroupById[2]: group: {}", group);

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

	/**
	 * Uses the {@link IDataProvider#deleteScheduleUnitById(String, TypeOfSchedule) deleteScheduleUnitById} method to remove the schedule unit with the given ID and type.
	 */
	@Override
	public void deleteScheduleUnitById(String scheduleUnitId, TypeOfSchedule type) throws Exception {
		log.debug("deleteScheduleUnitById[1]: delete scheduleUNit with id: {}", scheduleUnitId);
		final String tableName = type.equals(TypeOfSchedule.MAIN) ? Constants.MAIN_SCHEDULE_UNITS_TABLE_NAME : Constants.RETAKE_SCHEDULE_UNITS_TABLE_NAME;
		String sql = String.format(Constants.SQL_DELETE_ENTITY, tableName, Constants.SCHEDULE_UNIT_ID_FIELD, scheduleUnitId);

		try {
			// Проверяем существование ячейки расписания перед удалением
			ScheduleUnit scheduleUnit = getScheduleUnitById(scheduleUnitId, type);
			log.debug("deleteScheduleUnitById[2]: schedule unit: {}", scheduleUnit);

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

	/**
	 * Uses the {@link IDataProvider#deleteSubjectById(String) deleteSubjectById} method to remove the subject with the given ID.
	 */
	@Override
	public void deleteSubjectById(String subjectId) throws Exception {
		log.debug("deleteSubjectById[1]: delete subject with id: {}", subjectId);
		String sql = String.format(Constants.SQL_DELETE_ENTITY, Constants.SUBJECT_TABLE_NAME, Constants.SUBJECT_ID_FIELD, subjectId);

		try {
			// Проверяем существование предмета перед удалением
			Subject subject = getSubjectById(subjectId);
			log.debug("deleteSubjectById[2]: subject: {}", subject);

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

	/**
	 * Uses the {@link IDataProvider#getStudentById(String) getStudentById} method to retrieve data about the student with the given ID.
	 */
	@Override
	public Student getStudentById(String id) throws Exception {
		log.info("getStudentById[1]: get student with id: {}", id);
		String sql = String.format(Constants.SQL_SELECT_BY_ID, Constants.STUDENT_TABLE_NAME, Constants.STUDENT_ID_FIELD, id);

		try (Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql)
		) {
			Student student = null;
			if (resultSet.next()) {
				student = mapResultSetToStudent(resultSet);
				log.debug("getStudentById[2]: student: {}", student);
			}

			return Optional.ofNullable(student)
					.orElseThrow(() -> {
						log.error("getStudentById[3]: there is no student with this id");
						return new Exception("there is no student with this id");
					});
		}
	}

	/**
	 * Uses the {@link IDataProvider#getTeacherById(String) getTeacherById} method to retrieve data about the teacher with the given ID.
	 */
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
				log.debug("getTeacherById[2]: teacher: {}", teacher);
			}

			return Optional.ofNullable(teacher)
					.orElseThrow(() -> {
						log.error("getTeacherById[2]: there is no teacher with this id");
						return new Exception("there is no teacher with this id");
					});
		}
	}

	/**
	 * Uses the {@link IDataProvider#getGroupById(String) getGroupById} method to retrieve data about the group with the given ID.
	 */
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
				List<Student> students = getStudentsByGroup(group.getGroupNumber(), connection);
				group.setStudents(students);
				log.debug("getGroupById[2]: group: {}", group);
			}

			return Optional.ofNullable(group)
					.orElseThrow(() -> {
						log.error("getGroupById[2]: there is no group with this id");
						return new Exception("there is no group with this id");
					});
		}
	}

	/**
	 * Retrieves a list of students belonging to a specified group based on the group number.
	 *
	 * @param groupNumber The group number for which to retrieve students.
	 * @param connection The database connection to use for executing the SQL query.
	 * @return A list of {@link Student} objects representing students in the specified group.
	 * @see #getStudentById(String)
	 */
	private List<Student> getStudentsByGroup(String groupNumber, Connection connection) {
		log.debug("getStudentsByGroup[1]: get students from group: {}", groupNumber);
		String sql = String.format(Constants.SQL_SELECT_STUDENTS_ID_BY_GROUP, groupNumber);
		List<Student> students = new ArrayList<>();

		try (
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

	/**
	 * Uses the {@link IDataProvider#getScheduleUnitById(String, TypeOfSchedule) getScheduleUnitById} method to retrieve data about the schedule unit with the given ID and type.
	 */
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
				log.debug("getScheduleUnitById[2]: schedule unit: {}", scheduleUnit);
			}

			return Optional.ofNullable(scheduleUnit)
					.orElseThrow(() -> {
						log.error("getScheduleUnitById[2]: there is no scheduleUnit with this id");
						return new Exception("there is no scheduleUnit with this id");
					});
		}
	}

	/**
	 * Uses the {@link IDataProvider#getSubjectById(String) getSubjectById} method to retrieve data about the subject with the given ID.
	 */
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
				log.debug("getSubjectById[2]: subject: {}", subject);
			}

			return Optional.ofNullable(subject)
					.orElseThrow(() -> {
						log.error("getSubjectById[2]: there is no subject with this id");
						return new Exception("there is no subject with this id");
					});
		}
	}

	/**
	 * Uses the {@link IDataProvider#getAllStudents() getAllStudents} method to get a list of all students in the system.
	 */
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

	/**
	 * Uses the {@link IDataProvider#getAllTeachers() getAllTeachers} method to get a list of all teachers in the system.
	 */
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
		log.debug("getAllTeachers[3]: teachers{}", teachers);
		return teachers;
	}

	/**
	 * Uses the {@link IDataProvider#getAllGroups() getAllGroups} method to get a list of all groups in the system.
	 */
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
				List<Student> students = getStudentsByGroup(group.getGroupNumber(), connection);
				group.setStudents(students);
				groups.add(group);
			}
		} catch (SQLException e) {
			log.error("getAllGroups[2]: error: {}", e.getMessage());
		}
		log.debug("getAllGroups[3]: groups: {}", groups);
		return groups;
	}

	/**
	 * Uses the {@link IDataProvider#getAllScheduleUnits(TypeOfSchedule) getAllScheduleUnits} method to get a list of all schedule units of the specified type in the system.
	 */
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

	/**
	 * Uses the {@link IDataProvider#getAllSubjects() getAllSubjects} method to get a list of all subjects in the system.
	 */
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

		saveEntities(students, Constants.SQL_INSERT_STUDENT_TEST, new StudentEntityMapper());
		saveEntities(teachers, Constants.SQL_INSERT_TEACHER_TEST, new TeacherEntityMapper());
		saveEntities(subjects, Constants.SQL_INSERT_SUBJECT_TEST, new SubjectEntityMapper());
		saveGroups(groups);
		log.debug("dataTransform[3]: records were saved in PostgresDB files");
	}
}
