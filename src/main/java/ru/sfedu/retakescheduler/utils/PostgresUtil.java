package ru.sfedu.retakescheduler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.api.IDataProvider;
import ru.sfedu.retakescheduler.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresUtil {

	private static final Logger log = LogManager.getLogger(PostgresUtil.class.getName());
	public static Student mapResultSetToStudent(ResultSet resultSet) throws SQLException {
		log.debug("mapResultSetToStudent[1]: resultSet: {}", resultSet);
		Student student = new Student();
		student.setStudentId(resultSet.getString(Constants.STUDENT_ID_FIELD));
		student.setLastName(resultSet.getString(Constants.PERSON_LASTNAME_FIELD));
		student.setFirstName(resultSet.getString(Constants.PERSON_FIRSTNAME_FIELD));
		student.setPatronymic(resultSet.getString(Constants.PERSON_PATRONYMIC_FIELD));
		student.setEmail(resultSet.getString(Constants.PERSON_EMAIL_FIELD));
		student.setAverageScore(resultSet.getDouble(Constants.STUDENT_AVERAGE_SCORE_FIELD));
		log.debug("mapResultSetToStudent[2]: student from resultSet: {}", student);
		return student;
	}

	public static Teacher mapResultSetToTeacher(ResultSet resultSet) throws SQLException {
		log.debug("mapResultSetToTeacher[1]: resultSet: {}", resultSet);
		Teacher teacher = new Teacher();
		teacher.setTeacherId(resultSet.getString(Constants.TEACHER_ID_FIELD));
		teacher.setLastName(resultSet.getString(Constants.PERSON_LASTNAME_FIELD));
		teacher.setFirstName(resultSet.getString(Constants.PERSON_FIRSTNAME_FIELD));
		teacher.setPatronymic(resultSet.getString(Constants.PERSON_PATRONYMIC_FIELD));
		teacher.setEmail(resultSet.getString(Constants.PERSON_EMAIL_FIELD));
		teacher.setBusyDay(resultSet.getTimestamp(Constants.BUSY_DAY_FIELD).toLocalDateTime().toLocalDate());
		log.debug("mapResultSetToTeacher[2]: teacher from resultSet: {}", teacher);
		return teacher;
	}

	public static Group mapResultSetToGroup(ResultSet resultSet) throws SQLException {
		log.debug("mapResultSetToGroup[1]: resultSet: {}", resultSet);
		Group group = new Group();
		group.setGroupNumber(resultSet.getString(Constants.GROUP_NUMBER_FIELD));
		group.setCourse(resultSet.getInt(Constants.GROUP_COURSE_FIELD));
		group.setLevelOfTraining(resultSet.getString(Constants.GROUP_LEVEL_OF_TRAINING_FIELD));
		group.setBusyDay(resultSet.getTimestamp(Constants.BUSY_DAY_FIELD).toLocalDateTime().toLocalDate());
		log.debug("mapResultSetToGroup[2]: group from resultSet: {}", group);
		return group;
	}

	public static ScheduleUnit mapResultSetToScheduleUnit(ResultSet resultSet, IDataProvider dataProvider) throws Exception {
		log.debug("mapResultSetToScheduleUnit[1]: resultSet: {}", resultSet);
		ScheduleUnit scheduleUnit = new ScheduleUnit();
		scheduleUnit.setScheduleUnitId(resultSet.getString(Constants.SCHEDULE_UNIT_ID_FIELD));
		scheduleUnit.setDateTime(resultSet.getTimestamp(Constants.SCHEDULE_UNIT_DATETIME_FIELD).toLocalDateTime());
		scheduleUnit.setLocation(resultSet.getString(Constants.SCHEDULE_LOCATION_FIELD));
		scheduleUnit.setSubject(dataProvider.getSubjectById(resultSet.getString(Constants.SUBJECT_ID_FIELD)));
		scheduleUnit.setPerson(dataProvider.getTeacherById(resultSet.getString(Constants.TEACHER_ID_FIELD)));
		scheduleUnit.setGroup(dataProvider.getGroupById(resultSet.getString(Constants.GROUP_NUMBER_FIELD)));
		log.debug("mapResultSetToScheduleUnit[2]: scheduleUnit from resultSet: {}", scheduleUnit);
		return scheduleUnit;
	}

	public static Subject mapResultSetToSubject(ResultSet resultSet) throws SQLException {
		log.debug("mapResultSetToSubject[1]: resultSet: {}", resultSet);
		Subject subject = new Subject();
		subject.setSubjectId(resultSet.getString(Constants.SUBJECT_ID_FIELD));
		subject.setSubjectName(resultSet.getString(Constants.SUBJECT_NAME_FIELD));
		subject.setControlType(resultSet.getString(Constants.CONTROL_TYPE_FIELD));
		log.debug("mapResultSetToSubject[2]: subject from resultSet: {}", subject);
		return subject;
	}
}
