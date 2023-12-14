package ru.sfedu.retakescheduler.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static ru.sfedu.retakescheduler.api.MongoBeanHistory.objectToJsonArray;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.sfedu.retakescheduler.model.Person;
import ru.sfedu.retakescheduler.model.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MongoBeanHistoryTest {
	private static final Logger log = LogManager.getLogger(MongoBeanHistoryTest.class);

	@Test
	public void testObjectToJsonArray() {
		Person person = new Person("Doe", "John", "Petrovich", "john.doe@gmail.com");

		Map<String, Object> map = objectToJsonArray(person);
		assertNotNull(map);
	}

	@Test
	public void testJsonArrayToObjectList() {
		Person person1 = new Person("Doe", "John", "Petrovich", "john.doe@gmail.com");
		Map<String, Object> map1 = objectToJsonArray(person1);

		Person person2 = new Person("Pupkin", "Vasya", "Ivanovich", "vasya.pupkin@gmail.com");
		Map<String, Object> map2 = objectToJsonArray(person2);

		List<Map<String, Object>> objectList = new ArrayList<>();
		objectList.add(map1);
		objectList.add(map2);
		log.debug("testJsonArrayToObjectList[1]: objectList = {}", objectList);

		List<Person> people = MongoBeanHistory.jsonArrayToObjectList(objectList, Person.class);
//		log.debug("testJsonArrayToObjectList[2]: people = {}, person1 = {}, person2 = {}", people, people.get(0), people.get(1));
		assertNotNull(people);
	}

	@Test
	public void testLogObject() {
		log.debug("testLogObject[1]: start test");

		MongoBeanHistory mongoBeanHistory1 = new MongoBeanHistory();
		MongoBeanHistory mongoBeanHistory2 = new MongoBeanHistory("Test");
		Person person1 = new Person("Doe", "John", "Petrovich", "john.doe@gmail.com");

		Person person2 = new Person("Pupkin", "Vasya", "Ivanovich", "vasya.pupkin@gmail.com");
		Person person3 = new Person("Ivanov", "Ivan", "Ivanovich", "ivan.ivanov@gmail.com");

		log.debug("testLogObject[2]: person1 = {}", person1);
		log.debug("testLogObject[3]: person2 = {}", person2);
		log.debug("testLogObject[3]: person3 = {}", person3);

		mongoBeanHistory1.logObject(person1, "testLogObject", Status.SUCCESS);
		mongoBeanHistory1.logObject(person2, "testLogObject", Status.SUCCESS);
		mongoBeanHistory2.logObject(person3, "testLogObject", "tester", Status.FAULT);

		log.debug("testLogObject[4]: finish test");
	}

	@Test
	public void testGetObjectInDocumentByClassName() {
		log.debug("testGetObjectInDocumentByClassName[1]: start test");

		MongoBeanHistory mongoBeanHistory = new MongoBeanHistory();
		List<Object> people = mongoBeanHistory.getObjectInDocumentByClassName("Person");

		log.debug("testGetObjectInDocumentByClassName[2]: people = {}", people);

		assertNotNull(people);

		log.debug("testGetObjectInDocumentByClassName[3]: finish test");
	}
}
