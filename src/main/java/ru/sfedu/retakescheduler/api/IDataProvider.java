package ru.sfedu.retakescheduler.api;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.*;
import ru.sfedu.retakescheduler.utils.ExcelUtil;
import ru.sfedu.retakescheduler.utils.FileUtil;
import static ru.sfedu.retakescheduler.utils.DataUtil.*;
import static ru.sfedu.retakescheduler.utils.PropertiesConfigUtil.getProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
	default Schedule createSchedule(Schedule mainSchedule, LocalDate startDate, LocalDate endDate, boolean exportToExcel, boolean sendEmail) {
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

		// Продолжительность урока (в минутах)
		int lessonDuration = Constants.LESSON_DURATION;

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
							if (!isOverlapping(mainSchedule, currentDate, lessonDuration)) {
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
			try {
				exportInExcelFormat(retakeSchedule, Constants.OUTPUT_FOLDER_PATH.concat(Constants.EXCEL_RETAKE_SCHEDULE_FILE));
			} catch (Exception e) {
				log.error("createSchedule[2]: error: {}", e.getMessage());
			}
		}

		return retakeSchedule;
	}

	/**
	 * Checks for overlapping time intervals between the given start time and duration
	 * with the units in the main schedule.
	 *
	 * @param mainSchedule  The main schedule to check for overlapping units.
	 * @param startTime     The start time of the retake schedule unit.
	 * @param lessonDuration The duration of the retake schedule unit in minutes.
	 * @return {@code true} if there is an overlap with the main schedule, {@code false} otherwise.
	 *
	 * <p>
	 * This method compares the specified start time and duration with each unit in the main schedule.
	 * It returns {@code true} if there is any overlapping time interval between the specified time
	 * range and the units in the main schedule, and {@code false} otherwise.
	 * </p>
	 */
	private boolean isOverlapping(Schedule mainSchedule, LocalDateTime startTime, int lessonDuration) {
		LocalDateTime endTime = startTime.plusMinutes(lessonDuration);

		return mainSchedule.getUnits().stream()
				.anyMatch(mainSubject -> {
					LocalDateTime mainStartTime = mainSubject.getDateTime();
					LocalDateTime mainEndTime = mainStartTime.plusMinutes(lessonDuration);

					return startTime.isBefore(mainEndTime) && endTime.isAfter(mainStartTime);
				});
	}

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

	/**
	 * Sends reminder emails to a list of students about upcoming retake exams based on the provided schedule.
	 *
	 * @param schedule The schedule containing information about upcoming retake exams.
	 * @param students The list of students to whom reminder emails will be sent.
	 *
	 * <p>
	 * This method sends reminder emails to the specified list of students about upcoming retake exams.
	 * It uses the provided schedule to gather information about the exams and sends emails individually to each student.
	 * </p>
	 */
	private void sendEmail(Schedule schedule, List<Student> students) {
		log.debug("sendMail[1]: send mail to students: {}", students);
		String username = "";
		String password = "";
		Properties props = null;

		username = getProperty(Constants.SMTP_EMAIL);
		password = getProperty(Constants.SMTP_PASSWORD);

		props = new Properties();
		props.put(Constants.SMTP_PROP_AUTH, getProperty(Constants.SMTP_AUTH_FIELD));
		props.put(Constants.SMTP_PROP_TLS, getProperty(Constants.SMTP_TLS_FIELD));
		props.put(Constants.SMTP_PROP_HOST, getProperty(Constants.SMTP_HOST_FIELD));
		props.put(Constants.SMTP_PROP_PORT, getProperty(Constants.SMTP_PORT_FIELD));

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

	/**
	 * Exports the retake schedule to an Excel file.
	 *
	 * @param schedule    The schedule containing retake information to be exported.
	 * @param pathToFile  The path to the Excel file where the schedule will be saved.
	 * @throws Exception  If an error occurs during the export process.
	 *
	 * <p>
	 * This method creates an Excel workbook and writes the retake schedule information to it.
	 * The schedule includes details such as group number, subject, date and time, location, and teacher.
	 * The workbook is then saved to the specified file path.
	 * </p>
	 */
	private void exportInExcelFormat(Schedule schedule, String pathToFile) throws Exception {
		log.debug("exportInExcelFormat[1]: export schedule to file: {}", pathToFile);
		FileUtil.createFolderIfNotExists(Constants.OUTPUT_FOLDER_PATH);
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
