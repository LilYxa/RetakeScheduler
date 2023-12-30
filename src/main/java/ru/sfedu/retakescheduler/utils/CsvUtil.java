package ru.sfedu.retakescheduler.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.api.DataProviderCsv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class CsvUtil {
	private static final Logger log = LogManager.getLogger(CsvUtil.class.getName());
	public static <T> List<T> getAllRecords(String pathToFile, Class<T> tClass) {
		log.debug("getAllRecords[1]: start");
		try (Reader reader = new FileReader(pathToFile);
		     CSVReader csvReader = new CSVReaderBuilder(reader).build()) {
			CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
					.withType(tClass)
					.build();
			return csvToBean.parse();
		} catch (IOException e) {
			log.error("getAllRecords[3]: error: {}", e.getMessage());
		}
		return null;
	}

	public static <T> void save(T object, String pathToFile, Class<T> tClass, String[] columns) {
		log.debug("save[1]: save {}: {}", object.getClass().getSimpleName(), object);
		try (CSVWriter writer = new CSVWriter(new FileWriter(pathToFile, true))) {
			ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(tClass);
			mappingStrategy.setColumnMapping(columns);

			StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
					.withMappingStrategy(mappingStrategy)
					.build();
			beanToCsv.write(object);
			log.info("save[2]: object {} were saved: {}", object.getClass().getSimpleName(), object);
		} catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
			log.error("save[3]: error: {}", e.getMessage());
		}
	}

	public static <T> void saveRecords(List<T> list, String pathToFile, Class<T> tClass, String[] columns) {
		log.debug("saveRecords[1]: save records: {}", list);
		try (CSVWriter writer = new CSVWriter(new FileWriter(pathToFile, false))) {
			ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(tClass);
			mappingStrategy.setColumnMapping(columns);

			StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
					.withMappingStrategy(mappingStrategy)
					.build();
			beanToCsv.write(list);
		} catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
			log.error("saveRecords[2]: error: {}", e.getMessage());
		}
	}
}
