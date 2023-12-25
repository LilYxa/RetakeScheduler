package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.model.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BaseTest {
	private static final Logger log = LogManager.getLogger(BaseTest.class);

	Student student1 = createStudent("Ivanov", "Ivan", "Ivanovich", "ivanov@mail.ru", "53b51af6-04df-4af5-8bdb-499436bc575a", 77.5);
	Student student2 = createStudent("Petrov", "Petr", "Petrovich", "petrov@mail.ru", "8bccaa52-ef8c-4b1a-879f-10c6dfea861d", 94.6);
	final Student student3 = createStudent("Sidorov", "Sidor", "Sidorovich", "sidorov@mail.ru", "1e1d663e-29d4-4599-a2a2-18723e47f560", 88.8);

	Teacher teacher = createTeacher("Васильев", "Иван", "Николаевич", "vasiliev@mail.ru", "teach1", LocalDate.now());
	Teacher teacher2 = createTeacher("Васильев", "Вася", "Николаевич", "vasi@mail.ru", "teach2", LocalDate.now());
	Teacher teacher3 = createTeacher("Васил", "Иван", "Николаевич", "va@mail.ru", "teach3", LocalDate.now());
	Teacher teacherForSchedule = createTeacher("Doe", "John", "Johnovich", "john@mail.ru", "4e61290b-c004-491e-8c7a-ee194711ee47", LocalDate.now());

	Group group = createGroup("22ВТ-12.03.01.01-о1", 1, "Бакалавриат", LocalDate.now().with(DayOfWeek.TUESDAY),
			new ArrayList<>(Arrays.asList(student3, student1, student2)));
	Group group2 = createGroup("22ВТ-12.03.01.01-о2", 1, "Бакалавриат", LocalDate.now().with(DayOfWeek.TUESDAY),
			new ArrayList<>(Arrays.asList(student3, student1, student2)));
	Group group3 = createGroup("22ВТ-12.03.01.01-о3", 1, "Бакалавриат", LocalDate.now().with(DayOfWeek.TUESDAY),
			new ArrayList<>(Arrays.asList(student3, student1, student2)));

	Subject subject = createSubject("q2dw1", "Математика", "Экзамен");
	Subject subject2 = createSubject("q2dw1fddxfd", "Физика", "Экзамен");
	Subject subject3 = createSubject("q2dw1kjb", "История", "Экзамен");
	Subject testSubjectForSchedule = createSubject("1wfwef-hbehdh-qwwqq-dw1sqs", "Test Subject", "Test Type");

	ScheduleUnit scheduleUnit = createSchedule("jknkjwndkcjnwkdjcn", LocalDateTime.of(2023, 12, 12, 12, 12), "q2dw1", "location", "teach1", "22ВТ-12.03.01.01-о1");
	ScheduleUnit scheduleUnit2 = createSchedule("jknkjwndk", LocalDateTime.of(2023, 12, 14, 12, 12), "q2dw1fddxfd", "location", "teach2", "22ВТ-12.03.01.01-о2");
	ScheduleUnit scheduleUnit3 = createSchedule("okwokdmwok", LocalDateTime.of(2023, 12, 11, 12, 12), "q2dw1kjb", "location", "teach3", "22ВТ-12.03.01.01-о3");

	static protected Student createStudent(
			String lastName,
			String firstName,
			String patronymic,
			String email,
			String studentId,
			Double avgScore
	) {
		return new Student(lastName, firstName, patronymic, email, studentId, avgScore);
	}

	static protected Teacher createTeacher(
			String lastName,
			String firstName,
			String patronymic,
			String email,
			String teacherId,
			LocalDate busyDay
	) {
		return new Teacher(lastName, firstName, patronymic, email, teacherId, busyDay);
	}

	static protected Group createGroup(
			String groupNum,
			Integer course,
			String levelOfTraining,
			LocalDate busyDay,
			List<Student> students
	) {
		return new Group(groupNum, course, levelOfTraining, busyDay, students);
	}

	static protected ScheduleUnit createSchedule(
			String scheduleUnitId,
			LocalDateTime dateTime,
			String subjectId,
			String location,
			String personId,
			String groupNumber
	) {
		return new ScheduleUnit(scheduleUnitId, dateTime, subjectId, location, personId, groupNumber);
	}

	static protected Subject createSubject(
			String subjectId,
			String subjectName,
			String controlType
	) {
		return new Subject(subjectId, subjectName, controlType);
	}

//	public Schedule createTestSchedule(IDataProvider dataProvider) {
//		List<ScheduleUnit> schedule = new ArrayList<>();
//		Random random = new Random();
//
//		LocalDateTime currentDate = LocalDateTime.of(2023, 11, 27, 8, 0);
//		LocalDateTime endDate = LocalDateTime.of(2023, 12, 1, 17, 25);
//		List<Group> testGroups = dataProvider.getAllGroups();
//		List<Subject> testSubjects = dataProvider.getAllSubjects();
//		List<Teacher> testTeachers = dataProvider.getAllTeachers();
//
//		int[] pairsPerDay = {3, 2, 4, 3, 5}; // Количество пар в каждый день недели
//
//		int timeBetweenClasses = 15; // Время между занятиями в минутах
//
//		for (int currentDay = 0; currentDate.isBefore(endDate); ) {
//			// Пропускаем создание занятий в субботу и воскресенье
//			if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
//				// Проверяем, не превышено ли количество пар в текущий день
//				if (currentDay < pairsPerDay.length && pairsPerDay[currentDay] > 0) {
//					// Создаем занятие
//					ScheduleUnit scheduleUnit = new ScheduleUnit();
//					scheduleUnit.setScheduleUnitId("ID" + schedule.size());
//
//					// Определяем номер пары на основе текущего значения
//					scheduleUnit.setDateTime(currentDate);
//
//					// Рандомно создаем предмет
////					Subject testSubject = new Subject("1wfwef-hbehdh-qwwqq-dw1sqs","Test Subject", "Test Type");
//					Subject testSubject = testSubjects.get(0);
//					scheduleUnit.setSubjectId(testSubject.getSubjectId());
//
//					scheduleUnit.setLocation("Classroom 101");
//
//					// Рандомно создаем тестового преподавателя
//					LocalDate currentLocalDate = LocalDate.now();
//					int randomDays = random.nextInt(7);
//					LocalDate randomDate = currentLocalDate.plusDays(randomDays);
////					Teacher teacher = new Teacher("Doe", "John", "Johnovich", "john@mail.ru", "4e61290b-c004-491e-8c7a-ee194711ee47", randomDate);
//					Teacher teacher = testTeachers.get(0);
//					scheduleUnit.setPersonId(teacher.getTeacherId());
//
////					List<Group> testGroups = dataProvider.getAllGroups();
//					Group testGroup = testGroups.stream()
//							.skip(new Random().nextInt(testGroups.size()))
//							.findFirst()
//							.get();
//					scheduleUnit.setGroupNumber(testGroup.getGroupNumber());
//
//					schedule.add(scheduleUnit);
//
//					// Уменьшаем количество пар в текущий день
//					pairsPerDay[currentDay]--;
//
//					// Переходим к следующему времени (95 минут + временное окно)
//					currentDate = currentDate.plusMinutes(95 + timeBetweenClasses);
//				} else {
//					// Если все пары в текущий день уже созданы, переходим к следующему дню
//					currentDay++;
//					currentDate = LocalDateTime.of(currentDate.toLocalDate().plusDays(1), LocalTime.of(8, 0));
//				}
//			} else {
//				// Переход на следующий день и установка времени начала на 8:00
//				currentDay = 0; // Сброс индекса текущего дня
//				currentDate = LocalDateTime.of(currentDate.toLocalDate().plusDays(1), LocalTime.of(8, 0));
//			}
//		}
//
//		Schedule schedule1 = new Schedule(TypeOfSchedule.MAIN, schedule);
//		log.debug("schedule: {}", schedule1);
//		return schedule1;
//	}
}
