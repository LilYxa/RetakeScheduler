package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.model.*;
import ru.sfedu.retakescheduler.utils.ExcelUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Elland Ilia
 */
public interface IDataProvider {
	static final Logger log = LogManager.getLogger(DataProviderCsv.class);

	/**
	 * Saves information about a student in the system.
	 *
	 * @param student An object of type Student containing data about the student.
	 * @throws Exception If an error occurs during the saving process. If such a student already exists.
	 */
	void saveStudent(Student student) throws Exception;
	/**
	 * Saves information about a teacher in the system.
	 *
	 * @param teacher An object of type Teacher containing data about the teacher.
	 * @throws Exception If an error occurs during the saving process. If such a teacher already exists.
	 */
	void saveTeacher(Teacher teacher) throws Exception;
	/**
	 * Saves information about a group in the system.
	 *
	 * @param group An object of type Group containing data about the group.
	 * @throws Exception If an error occurs during the saving process. If such a group already exists.
	 */
	void saveGroup(Group group) throws Exception;
	/**
	 * Saves information about a schedule unit in the system.
	 *
	 * @param scheduleUnit An object of type ScheduleUnit containing data about the schedule unit.
	 * @param type The type of schedule (e.g., "MAIN" or "RETAKE").
	 * @throws Exception If an error occurs during the saving process. If such a schedule unit already exists.
	 */
	void saveScheduleUnit(ScheduleUnit scheduleUnit, TypeOfSchedule type) throws Exception;
	/**
	 * Saves information about a subject in the system.
	 *
	 * @param subject An object of type Subject containing data about the subject.
	 * @throws Exception If an error occurs during the saving process. If such a subject already exists.
	 */
	void saveSubject(Subject subject) throws Exception;

	/**
	 * Deletes a student based on the specified ID.
	 *
	 * @param studentId The ID of the student.
	 * @throws Exception If an error occurs during the deletion process. If a student with this ID does not exist.
	 */
	void deleteStudentById(String studentId) throws Exception;
	/**
	 * Deletes a teacher based on the specified ID.
	 *
	 * @param teacherId The ID of the teacher.
	 * @throws Exception If an error occurs during the deletion process. If a teacher with this ID does not exist.
	 */
	void deleteTeacherById(String teacherId) throws Exception;
	/**
	 * Deletes a group based on the specified ID.
	 *
	 * @param groupId The ID of the group.
	 * @throws Exception If an error occurs during the deletion process. If a group with this ID does not exist.
	 */
	void deleteGroupById(String groupId) throws Exception;
	/**
	 * Deletes a schedule unit based on the specified ID and schedule type.
	 *
	 * @param scheduleUnitId The ID of the schedule unit.
	 * @param type The type of schedule (e.g., "MAIN" or "RETAKE").
	 * @throws Exception If an error occurs during the deletion process. If a schedule unit with this ID does not exist.
	 */
	void deleteScheduleUnitById(String scheduleUnitId, TypeOfSchedule type) throws Exception;
	/**
	 * Deletes a subject based on the specified ID.
	 *
	 * @param subjectId The ID of the subject.
	 * @throws Exception If an error occurs during the deletion process. If a subject with this ID does not exist.
	 */
	void deleteSubjectById(String subjectId) throws Exception;

	/**
	 * Retrieves information about a student based on the specified ID.
	 *
	 * @param id The ID of the student.
	 * @return An object of type Student with data about the student.
	 * @throws Exception If an error occurs during the data retrieval process. If a student with this ID does not exist.
	 */
	Student getStudentById(String id) throws Exception;
	/**
	 * Retrieves information about a teacher based on the specified ID.
	 *
	 * @param id The ID of the teacher.
	 * @return An object of type Teacher with data about the teacher.
	 * @throws Exception If an error occurs during the data retrieval process. If a teacher with this ID does not exist.
	 */
	Teacher getTeacherById(String id) throws Exception;
	/**
	 * Retrieves information about a group based on the specified ID.
	 *
	 * @param id The ID of the group.
	 * @return An object of type Group with data about the group.
	 * @throws Exception If an error occurs during the data retrieval process. If a group with this ID does not exist.
	 */
	Group getGroupById(String id) throws Exception;
	/**
	 * Retrieves information about a schedule unit based on the specified ID and schedule type.
	 *
	 * @param id The ID of the schedule unit.
	 * @param type The type of schedule (e.g., "MAIN" or "RETAKE").
	 * @return An object of type ScheduleUnit with data about the schedule unit.
	 * @throws Exception If an error occurs during the data retrieval process. If a schedule unit with this ID does not exist.
	 */
	ScheduleUnit getScheduleUnitById(String id, TypeOfSchedule type) throws Exception;
	/**
	 * Retrieves information about a subject based on the specified ID.
	 *
	 * @param id The ID of the subject.
	 * @return An object of type Subject with data about the subject.
	 * @throws Exception If an error occurs during the data retrieval process. If a subject with this ID does not exist.
	 */
	Subject getSubjectById(String id) throws Exception;

	/**
	 * Retrieves a list of all students.
	 *
	 * @return A list of objects of type Student.
	 */
	List<Student> getAllStudents();
	/**
	 * Retrieves a list of all teachers.
	 *
	 * @return A list of objects of type Teacher.
	 */
	List<Teacher> getAllTeachers();
	/**
	 * Retrieves a list of all groups.
	 *
	 * @return A list of objects of type Group.
	 */
	List<Group> getAllGroups();
	/**
	 * Retrieves a list of all schedule units of the specified type.
	 *
	 * @param type The type of schedule (e.g., "MAIN" or "RETAKE").
	 * @return A list of objects of type ScheduleUnit.
	 */
	List<ScheduleUnit> getAllScheduleUnits(TypeOfSchedule type);
	/**
	 * Retrieves a list of all subjects.
	 *
	 * @return A list of objects of type Subject.
	 */
	List<Subject> getAllSubjects();

	/**
	 * Transforms data from an Excel file to other data sources such as CSV, XML, or a database.
	 * Validates the transformed data, throwing an exception if some data fails validation.
	 *
	 * @param sourceFilePath The path to the Excel file containing the source data.
	 * @throws Exception If an error occurs during the data transformation or if the transformed data fails validation.
	 */
	void dataTransform(String sourceFilePath) throws Exception;
	/**
	 * Creates a retake schedule based on the specified main schedule and time interval.
	 *
	 * @param mainSchedule The main schedule.
	 * @param startDate The start date of the time interval.
	 * @param endDate The end date of the time interval.
	 * @param exportToExcel A flag indicating whether to export to Excel.
	 * @param sendEmail A flag indicating whether to send emails.
	 * @return An object of type Schedule representing the created schedule.
	 */
	Schedule createSchedule(Schedule mainSchedule, LocalDate startDate, LocalDate endDate, boolean exportToExcel, boolean sendEmail);

	/**
	 * Checks if the specified entity exists in the list and throws an exception if it does.
	 *
	 * @param entities The list of entities.
	 * @param entity The entity to check.
	 * @param errorMsg The error message if the entity already exists.
	 * @throws Exception If the entity already exists in the list.
	 */
	default <T> void checkIfEntityExist(List<T> entities,T entity, String errorMsg) throws Exception {
		log.debug("checkIfEntityExist[1]: entity: {}", entity);
		boolean notExist = entities.stream().noneMatch(obj -> obj.equals(entity));
		if (!notExist) {
			log.error("checkIfEntityExist[2]: this {} already exist", entity.getClass().getSimpleName());
			throw new Exception(errorMsg);
		}
	}

	/**
	 * Loads data from an Excel file into a list of ExcelRow objects.
	 * This method is used as part of the data transformation process.
	 *
	 * @param filePath The path to the Excel file from which data will be loaded.
	 * @return A list of ExcelRow objects representing the data from the Excel file.
	 *         An empty list is returned if there is an error during data loading.
	 */
	default List<ExcelRow> dataLoading(String filePath) {
		List<ExcelRow> excelRows = new ArrayList<>();
		log.debug("dataLoading[1]: data loading from file: {}", filePath);
		try {
			excelRows = ExcelUtil.readFromExcel(filePath);
		} catch (IOException e) {
			log.error("dataLoading[2]: error: {}", e.getMessage());
		}
		return excelRows;
	}

}
