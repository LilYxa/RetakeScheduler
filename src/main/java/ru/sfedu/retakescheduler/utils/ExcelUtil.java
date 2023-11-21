package ru.sfedu.retakescheduler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.api.DataProviderCsv;

import java.io.IOException;

import static ru.sfedu.retakescheduler.utils.FileUtil.*;

public class ExcelUtil {
	private static final Logger log = LogManager.getLogger(DataProviderCsv.class);
	private String excelFile;

	public ExcelUtil() {
		this(Constants.DEBTORS_EXCEL_FILE, Constants.EXCEL_FOLDER);
	}

	public ExcelUtil(String fileName, String pathToExcelFile) {
		log.debug("ExcelFile[1]: ");
		String finalFolder = pathToExcelFile.equals(Constants.EXCEL_FOLDER) ? Constants.EXCEL_FOLDER : pathToExcelFile;
		excelFile = finalFolder.concat(Constants.DEBTORS_EXCEL_FILE).concat(Constants.EXCEL_FILE_TYPE);

		try {
			createFolderIfNotExists(finalFolder);
		} catch (IOException e) {

		}
	}

}
