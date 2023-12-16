package ru.sfedu.retakescheduler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.api.DataProviderCsv;
import ru.sfedu.retakescheduler.model.ExcelRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ru.sfedu.retakescheduler.utils.FileUtil.*;

public class ExcelUtil {
	private static final Logger log = LogManager.getLogger(ExcelUtil.class);

	public static List<ExcelRow> readFromExcel(String filePath) throws IOException {
		log.debug("readFromExcel[1]: read from file: {}", filePath);
		FileInputStream file = new FileInputStream(new File(filePath));
		Workbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheetAt(0);

		Iterator<Row> rowIterator = sheet.rowIterator();
		if (rowIterator.hasNext()) {
			rowIterator.next();
			return readRows(rowIterator);
		}
		return null;
 	}

	private static List<String> getHeaders(Row headerRow) {
		List<String> headers = new ArrayList<>();
		Iterator<Cell> cellIterator = headerRow.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			headers.add(cell.toString());
		}
		return headers;
	}

	private static ExcelRow createObjectFromRow(Row row) {
		String level = row.getCell(0).toString();
		int course = (int) row.getCell(1).getNumericCellValue();
		String group = row.getCell(2).toString();
		String controlType = row.getCell(3).toString();
		String subject = row.getCell(4).toString();
		String studentName = row.getCell(5).toString();
		String teacherName = row.getCell(6).toString();
		int finalRating = (int) row.getCell(7).getNumericCellValue();
		return new ExcelRow(level, course, group, controlType, subject, studentName, teacherName, finalRating);
	}

	private static List<ExcelRow> readRows(Iterator<Row> rowIterator) {
		List<ExcelRow> resultList = new ArrayList<>();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			ExcelRow excelRow = createObjectFromRow(row);
			resultList.add(excelRow);
		}
		return resultList;
	}

}
