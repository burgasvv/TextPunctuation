package com.burgas.textpunctuation.manager;

import com.burgas.textpunctuation.exception.PropertiesManagerException;

import java.io.*;
import java.util.Properties;

public class PropertiesManager {

    private static final Properties PROPERTIES = new Properties();

    public static Properties getProperties() {

        try (InputStream inputStream =
                     PropertiesManager.class.getClassLoader().getResourceAsStream("files.properties")){
            PROPERTIES.load(inputStream);

        } catch (IOException e) {
            throw new PropertiesManagerException(e.getCause());
        }

        return PROPERTIES;
    }
}
