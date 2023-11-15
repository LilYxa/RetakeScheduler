package ru.sfedu.retakescheduler.utils;

import java.io.IOException;
import java.util.Properties;

public interface IConfigUtil {
	public String getConfigPath();

	public void setConfigPath(String configPath);

	static Properties getConfiguration() throws IOException {
		return null;
	}

	static void loadConfiguration() throws IOException {

	}

	String getProperty(String key);

}
