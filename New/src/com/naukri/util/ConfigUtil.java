package com.naukri.util;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    public static String getConfig(String key) {
        // 1. Try environment variable
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        // 2. Fallback to properties file
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("C:\\Users\\hites\\git\\Naukri\\New\\src\\com\\n" + //
                        "aukri\\config\\config.properties")) {
            props.load(fis);
            return props.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException("Config not found for key: " + key, e);
        }
    }

    public static void main(String[] args) {
        String username = getConfig("username"); // or "username" if you want to use the property key
        String password = getConfig("password"); // or "password"
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
    }
}