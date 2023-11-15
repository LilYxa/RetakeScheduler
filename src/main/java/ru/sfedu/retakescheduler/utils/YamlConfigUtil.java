package ru.sfedu.retakescheduler.utils;

import org.yaml.snakeyaml.Yaml;
import ru.sfedu.retakescheduler.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class YamlConfigUtil {
	private static String configPath = "";
	private static Map<String, Object> configuration;
	private static final Yaml yaml = new Yaml();

	public YamlConfigUtil() {

	}

	public static String getConfigPath() {
		return configPath;
	}

	public static void setConfigPath(String configPath) {
		YamlConfigUtil.configPath = configPath;
	}

	public static Map<String, Object> getConfiguration() throws IOException {
		if (configuration == null) {
			loadConfiguration();
		}
		return configuration;
	}

	private static void loadConfiguration() throws IOException {
		File file;
		if (configPath.isEmpty()) {
			file = new File(Constants.YAML_CONFIG_PATH);
		} else {
			file = new File(configPath);
		}
		try (InputStream inputStream = new FileInputStream(file)) {
			configuration = yaml.load(inputStream);
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	public static String getProperty(String key) throws IOException {
		Map<String, Object> config = getConfiguration();
		String[] keys = key.split("\\.");

		for (int i = 0; i < keys.length - 1; i++) {
			String currentKey = keys[i];
			if (config.containsKey(currentKey)) {
				config = (Map<String, Object>) config.get(currentKey);
			} else {
				return null;
			}
		}

		return config.get(keys[keys.length - 1]).toString();
	}
}
