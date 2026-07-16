package com.spt.tools.core.prop;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class PropertiesUtil implements EnvironmentAware {
	private static Environment env;

	public static String getProperty(String key) {
		if (env == null) {
			return null;
		}
		return env.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		if (env == null) {
			return defaultValue;
		}
		return env.getProperty(key, defaultValue);

	}

	@Override
	public void setEnvironment(Environment environment) {
		PropertiesUtil.env = environment;
	}

}
