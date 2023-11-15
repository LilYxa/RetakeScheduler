package ru.sfedu.retakescheduler.model;

import java.util.Objects;

public class Person {
	private String lastName;
	private String firstName;
	private String patronymic;
	private String email;

	public Person() {
	}

	public Person(String lastName, String firstName, String patronymic, String email) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.patronymic = patronymic;
		this.email = email;
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
