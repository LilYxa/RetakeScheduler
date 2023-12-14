package ru.sfedu.retakescheduler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.model.ExcelRow;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExcelUtilTest {
	private static final Logger log = LogManager.getLogger(ExcelUtil.class);
	private String filePath = Constants.EXCEL_FOLDER.concat(Constants.DEBTORS_EXCEL_FILE).concat(Constants.EXCEL_FILE_TYPE);

	@Test
	public void testReadFromExcel() throws IOException {
		log.debug("testReadFromExcel[1]: start test");
		List<File> files = FileUtil.getListFilesInFolder(Constants.EXCEL_FOLDER);
		File file = files.get(0);
		log.debug("testReadFromExcel[2]: files in excel directory: {}", files);
		List<ExcelRow> excelRows = ExcelUtil.readFromExcel(file.getPath());
		log.debug("testReadFromExcel[3]: excel rows: {}", excelRows);
	}
}
