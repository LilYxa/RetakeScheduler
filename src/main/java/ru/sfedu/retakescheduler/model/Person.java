package ru.sfedu.retakescheduler.model;

import com.opencsv.bean.CsvBindByPosition;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements EntityInterface {

	@XmlElement(name = "lastName")
	@CsvBindByPosition(position = 0)
	private String lastName;
	@XmlElement(name = "firstName")
	@CsvBindByPosition(position = 1)
	private String firstName;
	@XmlElement(name = "patronymic")
	@CsvBindByPosition(position = 2)
	private String patronymic;
	@XmlElement(name = "email")
	@CsvBindByPosition(position = 3)
	private String email;

	public Person() {
	}

	public Person(String lastName, String firstName, String patronymic, String email) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.patronymic = patronymic;
		this.email = email;
	}

	public Person(String lastName, String firstName, String patronymic) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.patronymic = patronymic;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPatronymic() {
		return patronymic;
	}

	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public TypeOfEntity getType() {
		return TypeOfEntity.PERSON;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Person person = (Person) o;
		return Objects.equals(lastName, person.lastName) && Objects.equals(firstName, person.firstName) && Objects.equals(patronymic, person.patronymic) && Objects.equals(email, person.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(lastName, firstName, patronymic, email);
	}

	@Override
	public String toString() {
		return "Person{" +
				"lastName='" + lastName + '\'' +
				", firstName='" + firstName + '\'' +
				", patronymic='" + patronymic + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
