package ru.sfedu.retakescheduler.utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.api.DataProviderXml;
import ru.sfedu.retakescheduler.model.XmlWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmlUtil {
	private static final Logger log = LogManager.getLogger(DataProviderXml.class);
	public static <T> List<T> getAllRecords(String pathToFile, Class<T> tClass) {
		log.debug("getAllRecords[1]: get records from file: {}", pathToFile);
		try {
			File file = new File(pathToFile);

			if (!file.exists() || file.length() == 0) {
				log.warn("getAllRecords[2]: file is empty or doesn't exist {}", pathToFile);
				return new ArrayList<>();
			}

			JAXBContext context = JAXBContext.newInstance(XmlWrapper.class, tClass);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			XmlWrapper<T> wrapper = (XmlWrapper<T>) unmarshaller.unmarshal(file);
			return wrapper.getItems();
		} catch (Exception e) {
			log.error("getAllRecords[3]: error: {}", e.getMessage());
		}
		return null;
	}

	public static  <T> void saveRecord(T object, String pathToFile, Class<T> tClass) {
		log.debug("saveRecord[1]: save {}: {}", object.getClass().getSimpleName(), object);
		save(Collections.singletonList(object), pathToFile, tClass);
	}

	public static  <T> void saveRecords(List<T> objects, String pathToFile, Class<T> tClass) {
		log.debug("saveRecords[1]: save objects: {}", objects);
		save(objects, pathToFile, tClass);
	}

	private static <T> void save(List<T> objects, String pathToFile, Class<T> tClass) {
		log.debug("save[1]: save {} records", objects.size());
		try {
			File file = new File(pathToFile);

			List<T> existingItems = getAllRecords(pathToFile, tClass);
			if (existingItems == null) {
				existingItems = new ArrayList<>();
			}
			existingItems.addAll(objects);

			XmlWrapper<T> wrapper = new XmlWrapper<>();
			wrapper.setItems(existingItems);

			JAXBContext context = JAXBContext.newInstance(XmlWrapper.class, tClass);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(wrapper, file);
		} catch (Exception e) {
			log.error("save[2]: error: {}", e.getMessage());
		}
	}
}
