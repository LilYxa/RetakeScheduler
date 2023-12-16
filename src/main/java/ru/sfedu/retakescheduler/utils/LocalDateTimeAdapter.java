package ru.sfedu.retakescheduler.utils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
	@Override
	public LocalDateTime unmarshal(String s) throws Exception {
		return LocalDateTime.parse(s, formatter);
	}

	@Override
	public String marshal(LocalDateTime localDateTime) throws Exception {
		return localDateTime.format(formatter);
	}
}
