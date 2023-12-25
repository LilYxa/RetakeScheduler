package ru.sfedu.retakescheduler;

import ru.sfedu.retakescheduler.utils.FileUtil;

import java.io.File;

public class Constants {
	public static final String PROPERTIES_CONFIG_PATH = "src/main/resources/environment.properties";
	public static final String YAML_CONFIG_PATH = "src/main/resources/environment.yaml";
	public static final String XML_CONFIG_PATH = "src/main/resources/environment.xml";

	public static final String OUTPUT_FOLDER_PATH = "src/main/output/";

	public static final String PLANETS = "Planets";
	public static final String MONTH = "Months";

	public static final String MONGODB_PATH = "MONGODB_PATH";
	public static final String MONGO_DB_NAME = "MONGO_DB_NAME";
	public static final String MONGODB_COLLECTION = "MONGODB_COLLECTION";
	public static final String ACTOR_CHANGED_OBJECT_DEFAULT = "system";
	public static final String MONGO_FIELD_ID = "_id";
	public static final String MONGO_FIELD_CLASSNAME = "class_name";
	public static final String MONGO_FIELD_DATE = "date";
	public static final String MONGO_FIELD_ACTOR = "actor";
	public static final String MONGO_FIELD_METHOD = "method";
	public static final String MONGO_FIELD_OBJECT = "object";
	public static final String MONGO_FIELD_STATUS = "status";
	public static final String MODEL_PATH = "ru.sfedu.retakescheduler.model.";

	public static final String CSV_FOLDER_PATH = "src/main/resources/csv/";
	public static final String CSV_FOLDER = "csv/";
	public static final String CSV_FILE_TYPE = ".csv";
	public static final String TEST_FOLDER_PATH = "src/test/testFolder/";

	public static final String XML_FOLDER_PATH = "src/main/resources/xml/";
	public static final String XML_FOLDER = "xml/";
	public static final String XML_FILE_TYPE = ".xml";

	public static final String POSTGRES_HOST = "POSTGRES_HOST";
	public static final String POSTGRES_DB_NAME = "POSTGRES_DB_NAME";
	public static final String POSTGRES_DB_USER = "POSTGRES_DB_USER";
	public static final String POSTGRES_DB_PASSWORD = "POSTGRES_DB_PASSWORD";
	public static final String POSTGRES_JDBC_URL = "POSTGRES_JDBC_URL";

	public static final String PERSON_FILE = "people";
	public static final String STUDENT_FILE = "students";
	public static final String TEACHER_FILE = "teachers";
	public static final String GROUP_FILE = "groups";
	public static final String MAIN_SCHEDULE_UNIT_FILE = "mainScheduleUnits";
	public static final String RETAKE_SCHEDULE_UNIT_FILE = "retakeScheduleUnits";
	public static final String SUBJECT_FILE = "subjects";

	public static final String DEBTORS_EXCEL_FILE = "debtors";
	public static final String EXCEL_FOLDER = "src/main/resources/excel/";
	public static final String EXCEL_FILE_TYPE = ".xls.xlsx";
	public static final String EXCEL_RETAKE_SCHEDULE_FILE = "Расписание пересдач.xlsx";
	public static final String WORD_FOLDER = "src/main/resources/word/";
	public static final String DEFAULT_PATH_EXCEL_FILE = FileUtil.getListFilesInFolder(EXCEL_FOLDER).get(0).getPath();

	public static final String NAME_REGEX = "^[a-zA-Zа-яА-ЯёЁ]{2,25}$";
	public static final String PATRONYMIC_REGEX = "^[a-zA-Zа-яА-ЯёЁ]{0,25}$";
	public static final String GROUP_NUMBER_REGEX = "^[a-zA-Zа-яА-ЯёЁ0-9\\-\\.]{5,30}$";
	public static final String SUBJECT_REGEX = "^[а-яА-Яa-zA-Z0-9\\s.,!@#$%^&*()_+=:-]{2,200}$";
	public static final String CONTROL_TYPE_REGEX = "^[a-zA-Zа-яА-ЯёЁ\\s]{2,25}$";
	public static final String DOUBLE_LASTNAME_REGEX = "\\s*\\([^\\)]*\\)\\s*";

	public static final String LASTNAME_FIELD = "lastName";
	public static final String FIRSTNAME_FIELD = "firstName";
	public static final String PATRONYMIC_FIELD = "patronymic";
	public static final String GROUP_NUMBER_FIELD = "groupNumber";
	public static final String TRAINING_LEVEL_FIELD = "levelOfTraining";
	public static final String SUBJECT_FIELD = "subject";
	public static final String CONTROL_TYPE_FIELD = "controlType";

	public static final String MAIN_SCHEDULE_TYPE = "MAIN";
	public static final String RETAKE_SCHEDULE_TYPE = "RETAKE";

	public static final String INCORRECT_FIRSTNAME = "Имя может состоять из букв латинского и русского алфавита длиною от 2 до 25 символов";
	public static final String INCORRECT_LASTNAME = "Фамилия может состоять из букв латинского и русского алфавита длиною от 2 до 25 символов";
	public static final String INCORRECT_PATRONYMIC = "Отчество может состоять из букв латинского и русского алфавита длиною от 0 до 25 символов";
	public static final String INCORRECT_GROUP_NUMBER = "Номер группы может состоять из букв латинского и русского алфавита, символов (- и .). Длина от 5 до 30 символов";
	public static final String EMPTY_GROUP = "Группа не может быть пустой";
	public static final String INCORRECT_TRAINING_LEVEL = "Уровень подготовки может состоять из букв латинского и русского алфавита длиною от 0 до 25 символов";
	public static final String INCORRECT_SUBJECT = "Название предмета может состоять из букв латинского и русского алфавита, символов (.,!@#$%^&*()_+=:-), пробелов. Длина от 2 до 200 символов";
	public static final String INCORRECT_CONTROL_TYPE = "Тип контроля может состоять из букв латинского и русского алфавита длиною от 2 до 25 символов";

	public static final String PHYSICAL_TRAINING_FIELD = "ФИЗ-РА";
	public static final int LESSON_DURATION = 95;

	public static final String SMTP_EMAIL = "EMAIL_FOR_SMTP";
	public static final String SMTP_PASSWORD = "PASSWORD_FOR_SMTP";
	public static final String SMTP_PROPS = "mail.smtp.";
	public static final String SMTP_PROP_AUTH = SMTP_PROPS.concat("auth");
	public static final String SMTP_PROP_TLS = SMTP_PROPS.concat("starttls.enable");
	public static final String SMTP_PROP_HOST = SMTP_PROPS.concat("host");
	public static final String SMTP_PROP_PORT = SMTP_PROPS.concat("port");
	public static final String SMTP_AUTH_FIELD = "SMTP_AUTH";
	public static final String SMTP_TLS_FIELD = "SMTP_TLS";
	public static final String SMTP_HOST_FIELD = "SMTP_HOST";
	public static final String SMTP_PORT_FIELD = "SMTP_PORT";

	public static final String MAIL_SUBJECT = "Расписание пересдач";
	public static final String EMAIL_GREETING = "Здравствуйте, ";
	public static final String EMAIL_MSG_CONTENT = "У вас есть незачтенные предметы:\n";

	public static final String RETAKE_SCHEDULE_UNITS_TABLE_NAME = "retakeScheduleUnits";
	public static final String MAIN_SCHEDULE_UNITS_TABLE_NAME = "mainScheduleUnits";
	public static final String STUDENT_TABLE_NAME = "students";
	public static final String TEACHER_TABLE_NAME = "teachers";
	public static final String GROUP_TABLE_NAME = "groups";
	public static final String SUBJECT_TABLE_NAME = "subjects";
	public static final String GROUP_STUDENT_TABLE_NAME = "groupStudent";

	public static final String SCHEDULE_UNIT_ID_FIELD = "scheduleUnitId";
	public static final String STUDENT_ID_FIELD = "studentId";
	public static final String TEACHER_ID_FIELD = "teacherId";
	public static final String SUBJECT_ID_FIELD = "subjectId";
	public static final String GROUP_ID_FIELD = "groupNumber";
	public static final String PERSON_LASTNAME_FIELD = "lastName";
	public static final String PERSON_FIRSTNAME_FIELD = "firstName";
	public static final String PERSON_PATRONYMIC_FIELD = "patronymic";
	public static final String PERSON_EMAIL_FIELD = "email";
	public static final String STUDENT_AVERAGE_SCORE_FIELD = "averageScore";
	public static final String SUBJECT_NAME_FIELD = "subjectName";
	public static final String SUBJECT_CONTROL_TYPE_FIELD = "controlType";
	public static final String GROUP_COURSE_FIELD = "course";
	public static final String GROUP_LEVEL_OF_TRAINING_FIELD = "levelOfTraining";
	public static final String BUSY_DAY_FIELD = "busyDay";
	public static final String SCHEDULE_UNIT_DATETIME_FIELD = "dateTime";
	public static final String SCHEDULE_LOCATION_FIELD = "location";

	public static final String SQL_CREATE_STUDENT_TABLE = """
                CREATE TABLE IF NOT EXISTS students (
                    studentId VARCHAR(255) PRIMARY KEY,
                    lastName VARCHAR(255),
                    firstName VARCHAR(255),
                    patronymic VARCHAR(255),
                    email VARCHAR(255),
                    averageScore DOUBLE PRECISION
                );
			""";
	public static final String SQL_CREATE_TEACHER_TABLE = """
                CREATE TABLE IF NOT EXISTS teachers (
                    teacherId VARCHAR(255) PRIMARY KEY,
                    lastName VARCHAR(255),
                    firstName VARCHAR(255),
                    patronymic VARCHAR(255),
                    email VARCHAR(255),
                    busyDay TIMESTAMPTZ
                );
			""";
	public static final String SQL_CREATE_SUBJECT_TABLE = """
	            CREATE TABLE IF NOT EXISTS subjects (
	                subjectId VARCHAR(255) PRIMARY KEY,
	                subjectName VARCHAR(255),
	                controlType VARCHAR(255)
	            );
			""";
	public static final String SQL_CREATE_GROUP_TABLE = """
				CREATE TABLE IF NOT EXISTS groups (
			        groupNumber VARCHAR(255) PRIMARY KEY,
			        course INT,
			        levelOfTraining VARCHAR(255),
			        busyDay TIMESTAMPTZ
			    );
			""";
	public static final String SQL_CREATE_GROUP_STUDENT_TABLE = """
				CREATE TABLE IF NOT EXISTS groupStudent (
				    groupNumber VARCHAR(255),
				    studentId VARCHAR(255),
				    PRIMARY KEY (groupNumber, studentId),
				    FOREIGN KEY (groupNumber) REFERENCES groups(groupNumber) ON DELETE CASCADE,
				    FOREIGN KEY (studentId) REFERENCES students(studentId) ON DELETE CASCADE
				);
			""";

	public static final String SQL_CREATE_SCHEDULE_UNIT_TABLE = """
				CREATE TABLE IF NOT EXISTS %s (
				    scheduleUnitId VARCHAR(255) PRIMARY KEY,
				    dateTime TIMESTAMPTZ,
				    location VARCHAR(255),
				    subjectId VARCHAR(255),
				    teacherId VARCHAR(255),
				    groupNumber VARCHAR(255),
				    FOREIGN KEY (subjectId) REFERENCES subjects(subjectId) ON DELETE CASCADE,
				    FOREIGN KEY (teacherId) REFERENCES teachers(teacherId) ON DELETE CASCADE,
				    FOREIGN KEY (groupNumber) REFERENCES groups(groupNumber) ON DELETE CASCADE
				);
			""";

	public static final String SQL_INSERT_TEACHER = "INSERT INTO teachers (teacherId, lastName, firstName, patronymic, email, busyDay) VALUES (?, ?, ?, ?, ?, ?)";
	public static final String SQL_INSERT_TEACHER_TEST = "INSERT INTO teachers (teacherId, lastName, firstName, patronymic, email, busyDay) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')";
	public static final String SQL_INSERT_STUDENT = "INSERT INTO students (studentId, lastName, firstName, patronymic, email, averageScore) VALUES (?, ?, ?, ?, ?, ?)";
	public static final String SQL_INSERT_STUDENT_TEST = "INSERT INTO students (studentId, lastName, firstName, patronymic, email, averageScore) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')";
	public static final String SQL_INSERT_SUBJECT = "INSERT INTO subjects (subjectId, subjectName, controlType) VALUES (?, ?, ?)";
	public static final String SQL_INSERT_SUBJECT_TEST = "INSERT INTO subjects (subjectId, subjectName, controlType) VALUES ('%s', '%s', '%s')";
	public static final String SQL_INSERT_GROUP = "INSERT INTO groups (groupNumber, course, levelOfTraining, busyDay) VALUES (?, ?, ?, ?)";
	public static final String SQL_INSERT_GROUP_TEST = "INSERT INTO groups (groupNumber, course, levelOfTraining, busyDay) VALUES ('%s', '%s', '%s', '%s')";
	public static final String SQL_INSERT_GROUP_STUDENT = "INSERT INTO groupStudent (groupNumber, studentId) VALUES (?, ?)";
	public static final String SQL_INSERT_GROUP_STUDENT_TEST = "INSERT INTO groupStudent (groupNumber, studentId) VALUES ('%s', '%s')";
	public static final String SQL_INSERT_SCHEDULE_UNIT = "INSERT INTO %s (scheduleUnitId, dateTime, location, subjectId, teacherId, groupNumber) VALUES (?, ?, ?, ?, ?, ?)";
	public static final String SQL_INSERT_SCHEDULE_UNIT_TEST = "INSERT INTO %s (scheduleUnitId, dateTime, location, subjectId, teacherId, groupNumber) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')";

	public static final String SQL_SELECT_COUNT_STUDENTS = "SELECT COUNT(*) FROM students WHERE studentId = '%s'";
	public static final String SQL_SELECT_BY_ID = "SELECT * FROM %s WHERE %s = '%s'";
	public static final String SQL_SELECT_STUDENTS_ID_BY_GROUP = "SELECT s.studentId FROM students s JOIN groupStudent gs ON s.studentId=gs.studentId WHERE gs.groupNumber = '%s'";
	public static final String SQL_SELECT_ALL_RECORDS = "SELECT * FROM %s";

	public static final String SQL_DELETE_ENTITY = "DELETE FROM %s WHERE %s = '%s'";

	public static final String SQL_TRUNCATE_TABLE = "TRUNCATE TABLE %s CASCADE";

	public static final String CONFIG_PATH_SYSTEM_PROPERTY = "configPath";
	public static final String LOG4J_PATH_SYSTEM_PROPERTY = "log4jPath";

	public static final String CLI_DATA_TYPE = "dType";
	public static final String CLI_NEW_STUDENT = "nst";
	public static final String CLI_NEW_TEACHER = "nt";
	public static final String CLI_NEW_GROUP = "ng";
	public static final String CLI_NEW_SCHEDULE_UNIT = "nsu";
	public static final String CLI_NEW_SUBJECT = "ns";
	public static final String CLI_DELETE_RECORD = "d";
	public static final String CLI_DELETE_SCHEDULE_UNIT = "dsu";
	public static final String CLI_DATA_TRANSFORM = "dt";
	public static final String CLI_CREATE_MAIN_SCHEDULE = "cms";
	public static final String CLI_CREATE_RETAKE_SCHEDULE = "crs";
	public static final String CLI_HELP = "help";

	public static final String CLI_PRINT_HELP_HEADER = "--OPTIONS--";
	public static final String CLI_PRINT_HELP_FOOTER = "--HELP--";
	public static final String CLI_COMMAND_SYNTAX = "java -DconfigPath=<путь к файлу конфигураций свойств> -Dlog4jpath=<путь к файлу конфигураций логгера> -jar RetakeScheduler.jar";

	public static final String CLI_ARGS_NAME_DATA_TYPE = "data_type";
	public static final String CLI_ARGS_NAME_NEW_STUDENT = "last_name first_name patronymic email average_score";
	public static final String CLI_ARGS_NAME_NEW_TEACHER = "last_name first_name patronymic email busy_day";
	public static final String CLI_ARGS_NAME_NEW_GROUP = "group_number course level_of_training busy_day students";
	public static final String CLI_ARGS_NAME_NEW_SCHEDULE_UNIT = "schedule_type date_time subject_id location teacher_id group_number";
	public static final String CLI_ARGS_NAME_NEW_SUBJECT = "subject_name control_type";
	public static final String CLI_ARGS_NAME_DELETE_RECORD = "entity_type entity_id";
	public static final String CLI_ARGS_NAME_DELETE_SCHEDULE_UNIT = "type schedule_unit_id";
	public static final String CLI_ARGS_NAME_DATA_TRANSFORM = "path_to_file";
	public static final String CLI_ARGS_NAME_CREATE_RETAKE_SCHEDULE = "start_date end_date export_to_excel send_email";
	public static final String CLI_ARGS_LAST_NAME = "last_name";
	public static final String CLI_ARGS_FIRST_NAME = "first_name";
	public static final String CLI_ARGS_PATRONYMIC = "patronymic";
	public static final String CLI_ARGS_EMAIL = "email";
	public static final String CLI_ARGS_STUDENT_ID = "student_id";
	public static final String CLI_ARGS_AVERAGE_SCORE = "average_score";
	public static final String CLI_ARGS_TEACHER_ID = "teacher_id";
	public static final String CLI_ARGS_BUSY_DAY = "busy_day";
	public static final String CLI_ARGS_GROUP_NUMBER = "group_number";
	public static final String CLI_ARGS_COURSE = "course";
	public static final String CLI_ARGS_LEVEL_OF_TRAINING = "level_of_training";
	public static final String CLI_ARGS_STUDENTS = "students";
	public static final String CLI_ARGS_SCHEDULE_UNIT_ID = "schedule_unit_id";
	public static final String CLI_ARGS_DATE_TIME = "date_time";
	public static final String CLI_ARGS_LOCATION = "location";
	public static final String CLI_ARGS_SUBJECT_ID = "subject_id";
	public static final String CLI_ARGS_SUBJECT_NAME = "subject_name";
	public static final String CLI_ARGS_CONTROL_TYPE = "control_type";

	public static final String CLI_DESCRIPTION_DATA_TYPE = "Указание типа данных (CSV, XML, Postgres). По умолчанию XML";
	public static final String CLI_DESCRIPTION_NEW_STUDENT = "Создание новой записи о студенте.";
	public static final String CLI_DESCRIPTION_NEW_TEACHER = "Создание новой записи о преподавателе.";
	public static final String CLI_DESCRIPTION_NEW_GROUP = "Создание новой записи о группе студентов.";
	public static final String CLI_DESCRIPTION_NEW_SCHEDULE_UNIT = "Создание новой записи о занятии в расписании.";
	public static final String CLI_DESCRIPTION_NEW_SUBJECT = "Создание новой записи о предмете.";
	public static final String CLI_DESCRIPTION_DELETE_RECORD = "Удаление записи по указанному идентификатору и типу сущности.";
	public static final String CLI_DESCRIPTION_DELETE_SCHEDULE_UNIT = "Удаление ячейки расписания по указанному идентификатору и типу расписания.";
	public static final String CLI_DESCRIPTION_DATA_TRANSFORM = "Выполнение преобразования данных из Excel формата в другой (CSV, XML, Postgres).";
	public static final String CLI_DESCRIPTION_CREATE_MAIN_SCHEDULE = "Создание основного расписания с занятиями для всех групп и преподавателей.";
	public static final String CLI_DESCRIPTION_CREATE_RETAKE_SCHEDULE = "Создание расписания пересдач для студентов.";
	public static final String CLI_DESCRIPTION_HELP = "Вспомогательная информация по использованию данного сервиса";


}
