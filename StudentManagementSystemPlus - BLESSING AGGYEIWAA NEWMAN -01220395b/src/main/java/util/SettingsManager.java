package util;

import java.io.*;
import java.util.Properties;

public class SettingsManager {
    private static final String FILE_PATH = "data/settings.properties";

    public static String getSetting(String key, String defaultValue) {
        Properties props = new Properties();
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (InputStream input = new FileInputStream(file)) {
                props.load(input);
                return props.getProperty(key, defaultValue);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public static void saveSetting(String key, String value) {
        Properties props = new Properties();
        File file = new File(FILE_PATH);
        new File("data").mkdirs();

        try (OutputStream output = new FileOutputStream(file)) {
            props.setProperty(key, value);
            props.store(output, "Application Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}