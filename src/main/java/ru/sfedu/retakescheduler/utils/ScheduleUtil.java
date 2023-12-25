package ru.sfedu.retakescheduler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.api.DataProviderXml;
import ru.sfedu.retakescheduler.api.IDataProvider;
import ru.sfedu.retakescheduler.model.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScheduleUtil {
	private static final Logger log = LogManager.getLogger(ScheduleUnit.class);
	public static Schedule createTestSchedule(IDataProvider dataProvider) {
		List<ScheduleUnit> schedule = new ArrayList<>();
		Random random = new Random();

		LocalDateTime currentDate = LocalDateTime.of(2023, 11, 27, 8, 0);
		LocalDateTime endDate = LocalDateTime.of(2023, 12, 1, 17, 25);
		List<Group> testGroups = dataProvider.getAllGroups();
		List<Subject> testSubjects = dataProvider.getAllSubjects();
		List<Teacher> testTeachers = dataProvider.getAllTeachers();

		int[] pairsPerDay = {3, 2, 4, 3, 5}; // Количество пар в каждый день недели

		int timeBetweenClasses = 15; // Время между занятиями в минутах

		for (int currentDay = 0; currentDate.isBefore(endDate); ) {
			// Пропускаем создание занятий в субботу и воскресенье
			if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
				// Проверяем, не превышено ли количество пар в текущий день
				if (currentDay < pairsPerDay.length && pairsPerDay[currentDay] > 0) {
					// Создаем занятие
					ScheduleUnit scheduleUnit = new ScheduleUnit();
					scheduleUnit.setScheduleUnitId("ID" + schedule.size());

					// Определяем номер пары на основе текущего значения
					scheduleUnit.setDateTime(currentDate);

//					Subject testSubject = new Subject("1wfwef-hbehdh-qwwqq-dw1sqs","Test Subject", "Test Type");
					Subject testSubject = testSubjects.get(0);
					scheduleUnit.setSubjectId(testSubject.getSubjectId());

					scheduleUnit.setLocation("Classroom 101");

					LocalDate currentLocalDate = LocalDate.now();
					int randomDays = random.nextInt(7);
					LocalDate randomDate = currentLocalDate.plusDays(randomDays);
//					Teacher teacher = new Teacher("Doe", "John", "Johnovich", "john@mail.ru", "4e61290b-c004-491e-8c7a-ee194711ee47", randomDate);
					Teacher teacher = testTeachers.get(0);
					scheduleUnit.setPersonId(teacher.getTeacherId());

//					List<Group> testGroups = dataProvider.getAllGroups();
					Group testGroup = testGroups.stream()
							.skip(new Random().nextInt(testGroups.size()))
							.findFirst()
							.get();
					scheduleUnit.setGroupNumber(testGroup.getGroupNumber());

					schedule.add(scheduleUnit);

					// Уменьшаем количество пар в текущий день
					pairsPerDay[currentDay]--;

					// Переходим к следующему времени (95 минут + временное окно)
					currentDate = currentDate.plusMinutes(95 + timeBetweenClasses);
				} else {
					// Если все пары в текущий день уже созданы, переходим к следующему дню
					currentDay++;
					currentDate = LocalDateTime.of(currentDate.toLocalDate().plusDays(1), LocalTime.of(8, 0));
				}
			} else {
				// Переход на следующий день и установка времени начала на 8:00
				currentDay = 0; // Сброс индекса текущего дня
				currentDate = LocalDateTime.of(currentDate.toLocalDate().plusDays(1), LocalTime.of(8, 0));
			}
		}

		Schedule schedule1 = new Schedule(TypeOfSchedule.MAIN, schedule);
		log.debug("schedule: {}", schedule1);
		return schedule1;
	}
}
