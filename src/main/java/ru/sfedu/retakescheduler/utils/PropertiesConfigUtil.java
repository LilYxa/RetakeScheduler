package ru.sfedu.retakescheduler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.retakescheduler.Constants;
import ru.sfedu.retakescheduler.api.DataProviderPostgres;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesConfigUtil {
	private static final Logger log = LogManager.getLogger(DataProviderPostgres.class.getName());
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
		File file = new File(configPath.isEmpty() ? Constants.PROPERTIES_CONFIG_PATH : configPath);
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			config.load(fileInputStream);
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	public static String getProperty(String key) {
		log.debug("getProperty[1]: key: {}", key);
		try {
			return getConfiguration().getProperty(key);
		} catch (IOException e) {
			log.error("getProperty[2]: error: {}", e.getMessage());
		}
		return "";
	}
}
