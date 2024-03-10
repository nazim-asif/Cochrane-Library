package com.cochranelibrary.infrastructure.config;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
@Slf4j
public class PropertyConfig {
    private final Properties properties;
    public PropertyConfig() {
        properties = new Properties();
        String propFileName = "application.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        try {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

    public String getPropertyValue(String key) {
        return properties.getProperty(key);
    }
}
