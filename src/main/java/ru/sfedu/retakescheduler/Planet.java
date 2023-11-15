package ru.sfedu.retakescheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.utils.PropertiesConfigUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Planet {
	private static final Logger log = LogManager.getLogger(Planet.class);

	public List<String> listOfPlanets() throws IOException {
		List<String> planets = List.of(PropertiesConfigUtil.getProperty(Constants.PLANETS).split(","));
		log.info("Планеты: " + planets);
		return planets;
	}

	public Map<Integer, String> nameOfMonth() throws IOException {
		Map<Integer, String> monthMap = new HashMap<Integer, String>();
		String[] months = PropertiesConfigUtil.getProperty(Constants.MONTH).split(",");

		List.of(months).forEach(m -> {
			int key;
			String value;

			String[] arr = m.split(":");
			key = Integer.parseInt(arr[0]);
			value = arr[1];
			monthMap.put(key, value);
		});
		log.info("Месяцы: " + monthMap);
		return monthMap;
	}
}
