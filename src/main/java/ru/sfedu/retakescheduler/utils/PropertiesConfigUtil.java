package ru.sfedu.retakescheduler.utils;

import ru.sfedu.retakescheduler.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesConfigUtil {
	private static String configPath = "";
	private static final Properties config = new Properties();

	public PropertiesConfigUtil() {

	}

	public static String getConfigPath() {
		return configPath;
	}

	public static void setConfigPath(String configPath) {
		PropertiesConfigUtil.configPath = configPath;
	}

	public static Properties getConfiguration() throws IOException {
		if (config.isEmpty()) {
			loadConfiguration();
		}
		return config;
	}

	private static void loadConfiguration() throws IOException {
		File file;
		if (configPath.isEmpty()) {
			file = new File(Constants.PROPERTIES_CONFIG_PATH);
		} else {
			file = new File(configPath);
		}
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			config.load(fileInputStream);
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	public static String getProperty(String key) throws IOException {
		return getConfiguration().getProperty(key);
	}
}
