package ru.sfedu.retakescheduler.utils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	@Override
	public LocalDate unmarshal(String s) throws Exception {
		return LocalDate.parse(s, formatter);
	}

	@Override
	public String marshal(LocalDate localDate) throws Exception {
		return localDate.format(formatter);
	}
}
