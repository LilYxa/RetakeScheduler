package ru.sfedu.retakescheduler.utils;

import ru.sfedu.retakescheduler.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class XmlConfigUtil {

	private static String configPath = "";

	private static final Properties configuration = new Properties();

	public XmlConfigUtil() {

	}

	public static String getConfigPath() {
		return configPath;
	}

	public static void setConfigPath(String path) {
		XmlConfigUtil.configPath = path;
	}

	private static void loadConfiguration() throws IOException {
		File file;

		if (configPath.isEmpty()) {
			file = new File(Constants.XML_CONFIG_PATH);
		} else {
			file = new File(configPath);
		}

		try (InputStream inputStream = new FileInputStream(file)) {
			configuration.loadFromXML(inputStream);
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	public static Properties getConfiguration() throws IOException {
		if (configuration.isEmpty()) {
			loadConfiguration();
		}
		return configuration;
	}

	public static String getProperty(String key) throws IOException {
		return getConfiguration().getProperty(key);
	}
}
