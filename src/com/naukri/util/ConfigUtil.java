package com.naukri.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    private static Properties cachedProps = null;
    
    private static final String CONFIG_FILE = "src/com/naukri/config/config.properties";
    
    public static String getConfig(String envKey, String propKey) {
        // Priority 1: Try properties file first (for local development)
        if (cachedProps == null) {
            cachedProps = loadProperties();
        }
        
        String value = cachedProps.getProperty(propKey);
        if (value != null && !value.isEmpty()) {
            logger.debug("Config '{}' loaded from properties file", propKey);
            return value;
        }
        
        // Priority 2: Fallback to environment variable (for CI/CD)
        value = System.getenv(envKey);
        if (value != null && !value.isEmpty()) {
            logger.debug("Config '{}' loaded from environment variable", envKey);
            return value;
        }
        
        throw new RuntimeException("Configuration not found for key: " + propKey + 
                                   " (env: " + envKey + "). Please add to config.properties or set environment variable");
    }
    
    private static Properties loadProperties() {
        Properties props = new Properties();
        
        // Try multiple possible locations
        String[] possiblePaths = {
            CONFIG_FILE,
            "New/" + CONFIG_FILE,
            "../" + CONFIG_FILE
        };
        
        for (String pathStr : possiblePaths) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path)) {
                try (InputStream fis = new FileInputStream(path.toFile())) {
                    props.load(fis);
                    logger.info("Loaded configuration from: {}", path.toAbsolutePath());
                    return props;
                } catch (IOException e) {
                    logger.warn("Failed to load config from {}: {}", path, e.getMessage());
                }
            }
        }
        
        logger.info("No config.properties file found. Will use environment variables.");
        return props;
    }
    
    public static void validateConfig() {
        logger.info("Validating configuration...");
        try {
            String username = getConfig("NAUKRI_USERNAME", "username");
            String password = getConfig("NAUKRI_PASSWORD", "password");
            
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("Username is empty");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("Password is empty");
            }
            
            logger.info("Configuration validated successfully");
        } catch (Exception e) {
            logger.error("Configuration validation failed: {}", e.getMessage());
            throw e;
        }
    }
}