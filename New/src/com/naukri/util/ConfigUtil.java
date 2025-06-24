package com.naukri.util;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    public static String getConfig(String envKey, String propKey) {
        // 1. Try environment variable
        String value = System.getenv(envKey);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        // 2. Fallback to properties file
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("C:\\Users\\hites\\git\\Naukri\\New\\src\\com\\naukri\\config\\config.properties")) {
            props.load(fis);
            return props.getProperty(propKey);
        } catch (IOException e) {
            throw new RuntimeException("Config not found for key: " + propKey, e);
        }
    }

    public static void main(String[] args) {
        String username = getConfig("NAUKRI_USERNAME", "username");
        String password = getConfig("NAUKRI_PASSWORD", "password");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
    }
}