package ru.sfedu.retakescheduler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtil {
	private static final Logger log = LogManager.getLogger(FileUtil.class);

	public static void createFileIfNotExists(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			boolean flag = file.createNewFile();
			log.debug("createFileIfNotExists[1]: file {} was created: {}", file.getAbsolutePath(), flag);
		} else {
			log.debug("createFileIfNotExists[2]: file {} already exists", file.getAbsolutePath());
		}
	}

	public static void createFolderIfNotExists(String folderPath) throws IOException {
		File folder = new File(folderPath);
		if (!folder.exists()) {
			boolean flag = folder.mkdirs();
			log.debug("createFolderIfNotExists[1]: folder {} was created: {}", folder.getAbsolutePath(), flag);
		} else {
			log.debug("createFolderIfNotExists[2]: folder {} already exists", folder.getAbsolutePath());
		}
	}

	public static void deleteFileOrFolder(String pathName) {
		File file = new File(pathName);
		if (file.exists()) {
			boolean flag = file.delete();
			log.debug("deleteFileOrFolder[1]: deletion {} passed: {}", file.getAbsolutePath(), flag);
		} else {
			log.debug("deleteFileOrFolder[2]: file or folder {} doesn't exist", file.getAbsolutePath());
		}
	}

	public static List<File> getListFilesInFolder(String pathToFolder) {
		File folder = new File(pathToFolder);
		return List.<File>of(folder.listFiles());
	}
}
