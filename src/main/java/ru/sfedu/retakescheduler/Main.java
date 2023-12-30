package ru.sfedu.retakescheduler;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import ru.sfedu.retakescheduler.api.DataProviderCsv;
import ru.sfedu.retakescheduler.api.DataProviderPostgres;
import ru.sfedu.retakescheduler.api.DataProviderXml;
import ru.sfedu.retakescheduler.api.IDataProvider;
import ru.sfedu.retakescheduler.model.*;
import ru.sfedu.retakescheduler.utils.PropertiesConfigUtil;
import ru.sfedu.retakescheduler.utils.ScheduleUtil;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	private static final Logger log = LogManager.getLogger(Main.class.getName());

	public static void main(String[] args) {
		log.info("main[1]: start main");

		String configPath = System.getProperty(Constants.CONFIG_PATH_SYSTEM_PROPERTY);
		String log4jPath = System.getProperty(Constants.LOG4J_PATH_SYSTEM_PROPERTY);

		if (configPath != null) {
			PropertiesConfigUtil.setConfigPath(configPath);
		}
		if (log4jPath != null) {
			File file = new File(log4jPath);
			LoggerContext context = (LoggerContext) LogManager.getContext(false);
			context.setConfigLocation(file.toURI());
		}

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(createOptions(), args);
			IDataProvider dataProvider = new DataProviderXml();
			if (cmd.hasOption(Constants.CLI_DATA_TYPE)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_DATA_TYPE);
				switch (arguments[0]) {
					case "CSV" -> dataProvider = new DataProviderCsv();
					case "XML" -> dataProvider = new DataProviderXml();
					case "Postgres" -> dataProvider = new DataProviderPostgres();
					default -> log.info("main[2]: Такой тип данных отсутствует");
				}
				log.info("main[3]: Установлен {} data provider", dataProvider.getClass().getSimpleName());
			}

			if (cmd.hasOption(Constants.CLI_NEW_STUDENT)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_NEW_STUDENT);
				log.info("main[4]: Создание студента: {}", Arrays.toString(arguments));
				Student student = new Student(arguments[0], arguments[1], arguments[2], arguments[3], Double.parseDouble(arguments[4]));
				try {
					dataProvider.saveStudent(student);
				} catch (Exception e) {
					log.error("main[5]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_NEW_TEACHER)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_NEW_TEACHER);
				log.info("main[6]: Создание преподавателя: {}", Arrays.toString(arguments));
				Teacher teacher = new Teacher(arguments[0], arguments[1], arguments[2], arguments[3], LocalDate.parse(arguments[4]));
				try {
					dataProvider.saveTeacher(teacher);
				} catch (Exception e) {
					log.error("main[7]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_NEW_GROUP)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_NEW_GROUP);
				log.info("main[8]: Создание группы: {}", Arrays.toString(arguments));
				List<Student> students = new ArrayList<>();
				String[] studentsStrings = arguments[arguments.length - 1].trim().split(",");
				try {
					Arrays.stream(studentsStrings).forEach(student -> {
						String studentString = student.trim();
						studentString = studentString.replaceAll("[{}]", "");
						String[] studentArgs = studentString.split(" ");
						Student finalStudent = new Student(studentArgs[0], studentArgs[1], studentArgs[2], studentArgs[3], Double.parseDouble(studentArgs[4]));
						students.add(finalStudent);
					});
				} catch (Exception e) {
					log.error("main[9]: error: {}", e.getMessage());
				}
				log.debug("main[10]: Студенты в данной группе: {}", students);
				Group group = new Group(arguments[0], Integer.parseInt(arguments[1]), arguments[2], LocalDate.parse(arguments[3]), students);
				try {
					dataProvider.saveGroup(group);
				} catch (Exception e) {
					log.error("main[11]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_NEW_SCHEDULE_UNIT)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_NEW_SCHEDULE_UNIT);
				log.info("main[12]: Создание ячейки расписания: {}", Arrays.toString(arguments));
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				TypeOfSchedule type = arguments[0].equals(Constants.MAIN_SCHEDULE_TYPE) ? TypeOfSchedule.MAIN : TypeOfSchedule.RETAKE;
				try {
					Subject subject = dataProvider.getSubjectById(arguments[2]);
					Teacher teacher = dataProvider.getTeacherById(arguments[4]);
					Group group = dataProvider.getGroupById(arguments[5]);
					ScheduleUnit scheduleUnit = new ScheduleUnit(LocalDateTime.parse(arguments[1], dateTimeFormatter), subject, arguments[3], teacher, group);
					dataProvider.saveScheduleUnit(scheduleUnit, type);
				} catch (Exception e) {
					log.error("main[13]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_NEW_SUBJECT)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_NEW_SUBJECT);
				log.info("main[14]: Создание предмета: {}", Arrays.toString(arguments));
				Subject subject = new Subject(arguments[0], arguments[1]);
				try {
					dataProvider.saveSubject(subject);
				} catch (Exception e) {
					log.error("main[15]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_DELETE_RECORD)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_DELETE_RECORD);
				log.info("main[16]: Удаление {}, ID: {}", arguments[0], arguments[1]);
				try {
					switch (arguments[0]) {
						case "student" -> dataProvider.deleteStudentById(arguments[1]);
						case "teacher" -> dataProvider.deleteTeacherById(arguments[1]);
						case "group" -> dataProvider.deleteGroupById(arguments[1]);
						case "subject" -> dataProvider.deleteSubjectById(arguments[1]);
					}
				} catch (Exception e) {
					log.error("main[17]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_DELETE_SCHEDULE_UNIT)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_DELETE_SCHEDULE_UNIT);
				TypeOfSchedule type = arguments[0].equals(Constants.MAIN_SCHEDULE_TYPE) ? TypeOfSchedule.MAIN : TypeOfSchedule.RETAKE;
				log.info("main[18]: Удаление {} ячейки расписания с ID: {}", arguments[0], arguments[1]);
				try {
					dataProvider.deleteScheduleUnitById(arguments[1], type);
				} catch (Exception e) {
					log.error("main[19]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_DATA_TRANSFORM)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_DATA_TRANSFORM);
				String path = arguments == null ? Constants.DEFAULT_PATH_EXCEL_FILE : arguments[0];
				log.info("main[20]: Трансформация данных из файла: {}", path);
				try {
					dataProvider.dataTransform(path);
				} catch (Exception e) {
					log.error("main[21]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_CREATE_RETAKE_SCHEDULE)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_CREATE_RETAKE_SCHEDULE);
				log.info("main[22]: Создание распиания пересдач: {}", Arrays.toString(arguments));
				Schedule mainSchedule = new Schedule(TypeOfSchedule.MAIN, dataProvider.getAllScheduleUnits(TypeOfSchedule.MAIN));
				try {
					Schedule retake = dataProvider.createSchedule(mainSchedule, LocalDate.parse(arguments[0]), LocalDate.parse(arguments[1]), Boolean.parseBoolean(arguments[2]), Boolean.parseBoolean(arguments[3]));
					dataProvider.saveSchedule(retake);
				} catch (Exception e) {
					log.error("main[23]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_CREATE_MAIN_SCHEDULE)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_CREATE_MAIN_SCHEDULE);
				log.info("main[24]: Создание основного расписания: {}", Arrays.toString(arguments));
				try {
					Schedule mainSchedule = ScheduleUtil.createTestSchedule(dataProvider);
					dataProvider.saveSchedule(mainSchedule);
				} catch (Exception e) {
					log.error("main[25]: error: {}", e.getMessage());
				}
			}
			if (cmd.hasOption(Constants.CLI_PRINT_ALL_STUDENTS)) {
				List<Student> students = dataProvider.getAllStudents();
				printEntities(students, String.format(Constants.NO_STUDENTS_MESSAGE, 26));
			}
			if (cmd.hasOption(Constants.CLI_PRINT_ALL_TEACHERS)) {
				List<Teacher> teachers = dataProvider.getAllTeachers();
				printEntities(teachers, String.format(Constants.NO_TEACHERS_MESSAGE, 27));
			}
			if (cmd.hasOption(Constants.CLI_PRINT_ALL_SUBJECTS)) {
				List<Subject> subjects = dataProvider.getAllSubjects();
				printEntities(subjects, String.format(Constants.NO_SUBJECTS_MESSAGE, 28));
			}
			if (cmd.hasOption(Constants.CLI_PRINT_ALL_GROUPS)) {
				List<Group> groups = dataProvider.getAllGroups();
				printEntities(groups, String.format(Constants.NO_GROUPS_MESSAGE, 29));
			}
			if (cmd.hasOption(Constants.CLI_PRINT_ALL_SCHEDULE_UNITS)) {
				String[] arguments = cmd.getOptionValues(Constants.CLI_PRINT_ALL_SCHEDULE_UNITS);
				TypeOfSchedule type = arguments[0].equals(Constants.MAIN_SCHEDULE_TYPE) ? TypeOfSchedule.MAIN : TypeOfSchedule.RETAKE;
				List<ScheduleUnit> scheduleUnits = dataProvider.getAllScheduleUnits(type);
				printEntities(scheduleUnits, Constants.NO_SCHEDULE_UNITS_MESSAGE);
			}
			if (cmd.hasOption(Constants.CLI_HELP)) {
				HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.printHelp(
						120,
						Constants.CLI_COMMAND_SYNTAX,
						Constants.CLI_PRINT_HELP_HEADER,
						createOptions(),
						Constants.CLI_PRINT_HELP_FOOTER,
						true);
			}
		} catch (ParseException e) {
			log.error("main[26]: error: {}", e.getMessage());
		}
	}

	public void logBasicSystemInfo() {
		log.info("Launching the application...");
		log.info(
				"Operating System: " + System.getProperty("os.name") + " "
						+ System.getProperty("os.version")
		);
		log.info("JRE: " + System.getProperty("java.version"));
		log.info("Java Launched From: " + System.getProperty("java.home"));
		log.info("Class Path: " + System.getProperty("java.class.path"));
		log.info("Library Path: " + System.getProperty("java.library.path"));
		log.info("User Home Directory: " + System.getProperty("user.home"));
		log.info("User Working Directory: " + System.getProperty("user.dir"));
		log.info("Test INFO logging.");
	}

	private static <T> void printEntities(List<T> entities, String msg) {
		if (entities.isEmpty()) {
			log.info(msg);
		}
		entities.forEach(log::info);
	}

	private static Options createOptions() {
		Options options = new Options();

		Option dataTypeOption = new Option(Constants.CLI_DATA_TYPE, true, Constants.CLI_DESCRIPTION_DATA_TYPE);
		dataTypeOption.setArgName(Constants.CLI_ARGS_NAME_DATA_TYPE);
		dataTypeOption.setArgs(1);
		dataTypeOption.setOptionalArg(true);

		Option saveStudentOption = new Option(Constants.CLI_NEW_STUDENT, true, Constants.CLI_DESCRIPTION_NEW_STUDENT);
		saveStudentOption.setArgName(Constants.CLI_ARGS_NAME_NEW_STUDENT);
		saveStudentOption.setArgs(5);
		saveStudentOption.setOptionalArg(true);

		Option saveTeacherOption = new Option(Constants.CLI_NEW_TEACHER, true, Constants.CLI_DESCRIPTION_NEW_TEACHER);
		saveTeacherOption.setArgName(Constants.CLI_ARGS_NAME_NEW_TEACHER);
		saveTeacherOption.setArgs(5);
		saveTeacherOption.setOptionalArg(true);

		Option saveGroupOption = new Option(Constants.CLI_NEW_GROUP, true, Constants.CLI_DESCRIPTION_NEW_GROUP);
		saveGroupOption.setArgName(Constants.CLI_ARGS_NAME_NEW_GROUP);
		saveGroupOption.setArgs(50);
		saveGroupOption.setOptionalArg(true);

		Option saveScheduleUnitOption = new Option(Constants.CLI_NEW_SCHEDULE_UNIT, true, Constants.CLI_DESCRIPTION_NEW_SCHEDULE_UNIT);
		saveScheduleUnitOption.setArgName(Constants.CLI_ARGS_NAME_NEW_SCHEDULE_UNIT);
		saveScheduleUnitOption.setArgs(6);
		saveScheduleUnitOption.setOptionalArg(true);

		Option saveSubjectOption = new Option(Constants.CLI_NEW_SUBJECT, true, Constants.CLI_DESCRIPTION_NEW_SUBJECT);
		saveSubjectOption.setArgName(Constants.CLI_ARGS_NAME_NEW_SUBJECT);
		saveSubjectOption.setArgs(2);
		saveSubjectOption.setOptionalArg(true);

		Option deleteRecordOption = new Option(Constants.CLI_DELETE_RECORD, true, Constants.CLI_DESCRIPTION_DELETE_RECORD);
		deleteRecordOption.setArgName(Constants.CLI_ARGS_NAME_DELETE_RECORD);
		deleteRecordOption.setArgs(2);
		deleteRecordOption.setOptionalArg(true);

		Option deleteScheduleUnitOption = new Option(Constants.CLI_DELETE_SCHEDULE_UNIT, true, Constants.CLI_DESCRIPTION_DELETE_SCHEDULE_UNIT);
		deleteScheduleUnitOption.setArgName(Constants.CLI_ARGS_NAME_DELETE_SCHEDULE_UNIT);
		deleteScheduleUnitOption.setArgs(2);
		deleteScheduleUnitOption.setOptionalArg(true);

		Option dataTransformOption = new Option(Constants.CLI_DATA_TRANSFORM, true, Constants.CLI_DESCRIPTION_DATA_TRANSFORM);
		dataTransformOption.setArgName(Constants.CLI_ARGS_NAME_DATA_TRANSFORM);
		dataTransformOption.setArgs(1);
		dataTransformOption.setOptionalArg(true);

		Option createMainScheduleOption = new Option(Constants.CLI_CREATE_MAIN_SCHEDULE, true, Constants.CLI_DESCRIPTION_CREATE_MAIN_SCHEDULE);
		createMainScheduleOption.setOptionalArg(true);

		Option createRetakeScheduleOption = new Option(Constants.CLI_CREATE_RETAKE_SCHEDULE, true, Constants.CLI_DESCRIPTION_CREATE_RETAKE_SCHEDULE);
		createRetakeScheduleOption.setArgName(Constants.CLI_ARGS_NAME_CREATE_RETAKE_SCHEDULE);
		createRetakeScheduleOption.setArgs(4);
		createRetakeScheduleOption.setOptionalArg(true);

		Option printStudentsOption = new Option(Constants.CLI_PRINT_ALL_STUDENTS, false, Constants.CLI_DESCRIPTION_PRINT_ALL_STUDENTS);
		printStudentsOption.setOptionalArg(true);

		Option printTeachersOption = new Option(Constants.CLI_PRINT_ALL_TEACHERS, false, Constants.CLI_DESCRIPTION_PRINT_ALL_TEACHERS);
		printTeachersOption.setOptionalArg(true);

		Option printSubjectsOption = new Option(Constants.CLI_PRINT_ALL_SUBJECTS, false, Constants.CLI_DESCRIPTION_PRINT_ALL_SUBJECTS);
		printSubjectsOption.setOptionalArg(true);

		Option printGroupsOption = new Option(Constants.CLI_PRINT_ALL_GROUPS, false, Constants.CLI_DESCRIPTION_PRINT_ALL_GROUPS);
		printGroupsOption.setOptionalArg(true);

		Option printScheduleUnits = new Option(Constants.CLI_PRINT_ALL_SCHEDULE_UNITS, true, Constants.CLI_DESCRIPTION_PRINT_ALL_SCHEDULE_UNITS);
		printScheduleUnits.setArgName(Constants.CLI_ARGS_NAME_PRINT_SCHEDULE_UNITS);
		printScheduleUnits.setArgs(1);
		printScheduleUnits.setOptionalArg(true);

		Option helpOption = new Option(Constants.CLI_HELP, false, Constants.CLI_DESCRIPTION_HELP);
		helpOption.setOptionalArg(true);

		options.addOption(dataTypeOption);
		options.addOption(saveStudentOption);
		options.addOption(saveTeacherOption);
		options.addOption(saveGroupOption);
		options.addOption(saveScheduleUnitOption);
		options.addOption(saveSubjectOption);
		options.addOption(deleteRecordOption);
		options.addOption(deleteScheduleUnitOption);
		options.addOption(dataTransformOption);
		options.addOption(createMainScheduleOption);
		options.addOption(createRetakeScheduleOption);
		options.addOption(printStudentsOption);
		options.addOption(printTeachersOption);
		options.addOption(printSubjectsOption);
		options.addOption(printGroupsOption);
		options.addOption(printScheduleUnits);
		options.addOption(helpOption);

		return options;
	}
}
