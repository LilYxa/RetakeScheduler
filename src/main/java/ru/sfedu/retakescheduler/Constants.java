package ru.sfedu.retakescheduler;

public class Constants {
	public static final String PROPERTIES_CONFIG_PATH = "src/main/resources/environment.properties";
	public static final String YAML_CONFIG_PATH = "src/main/resources/environment.yaml";
	public static final String XML_CONFIG_PATH = "src/main/resources/environment.xml";

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
}
