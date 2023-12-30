package ru.sfedu.retakescheduler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.sfedu.retakescheduler.Constants;

import java.io.File;
import java.io.IOException;

public class FileUtilTest {
	private static final Logger log = LogManager.getLogger(FileUtil.class.getName());

	@Test
	public void testCreateFolderIfNotExists() throws IOException {
		log.debug("testCreateFolderIfNotExists[1]: test start");

		FileUtil.createFolderIfNotExists(Constants.CSV_FOLDER_PATH);

		File folder = new File(Constants.CSV_FOLDER_PATH);
		boolean flag = folder.exists();
		assertTrue(flag);

		log.debug("testCreateFolderIfNotExists[2]: folder was created: {}", flag);

	}

	@Test
	public void testCreateFileIfNotExists() throws IOException {
		log.debug("testCreateFileIfNotExists[1]: test start");

		String filePath = Constants.CSV_FOLDER_PATH.concat(Constants.PERSON_FILE).concat(Constants.CSV_FILE_TYPE);
		FileUtil.createFileIfNotExists(filePath);

		File file = new File(filePath);
		boolean flag = file.exists();
		assertTrue(flag);

		log.debug("testCreateFileIfNotExists[2]: file was created: {}", flag);
	}

}
