package com.naukri.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ResumeManager {
    private static final Logger logger = LoggerFactory.getLogger(ResumeManager.class);
    
    private static final String STATE_FILE = "src/com/naukri/config/last_resume_uploaded.properties";
    private static final String[] RESUME_FILES = {
        "data/Mr.Hithesh_3Y_Experienced_Tester_Resume.pdf",
        "data/Mr.Hithesh_3Y_Experienced_Tester_Resume2.pdf"
    };
    
    public File getNextResumeFile() throws IOException {
        Properties props = new Properties();
        Path statePath = findStatePath();
        
        String lastResume = null;
        if (Files.exists(statePath)) {
            try {
                props.load(Files.newInputStream(statePath));
                lastResume = props.getProperty("last");
                logger.info("Last uploaded resume: {}", lastResume);
            } catch (IOException e) {
                logger.warn("Could not read state file, will use first resume", e);
            }
        }
        
        // Pick the resume that was NOT uploaded last time
        String nextResume = RESUME_FILES[0];
        if (lastResume != null && lastResume.equals(RESUME_FILES[0])) {
            nextResume = RESUME_FILES[1];
        }
        
        File resumeFile = findResumeFile(nextResume);
        if (!resumeFile.exists()) {
            throw new IOException("Resume file not found: " + resumeFile.getAbsolutePath());
        }
        
        // Save the resume being uploaded this time
        saveState(statePath, nextResume);
        
        logger.info("Selected resume for upload: {}", resumeFile.getAbsolutePath());
        return resumeFile;
    }
    
    private Path findStatePath() {
        String[] possiblePaths = {
            STATE_FILE,
            "New/" + STATE_FILE,
            "../" + STATE_FILE
        };
        
        for (String pathStr : possiblePaths) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path.getParent())) {
                return path;
            }
        }
        
        return Paths.get(STATE_FILE);
    }
    
    private File findResumeFile(String relativePath) {
        String[] possiblePaths = {
            relativePath,
            "New/" + relativePath,
            "../" + relativePath
        };
        
        for (String pathStr : possiblePaths) {
            File file = new File(pathStr);
            if (file.exists()) {
                return file;
            }
        }
        
        return new File(relativePath);
    }
    
    private void saveState(Path statePath, String resumePath) {
        try {
            Properties props = new Properties();
            props.setProperty("last", resumePath);
            
            Files.createDirectories(statePath.getParent());
            props.store(Files.newOutputStream(statePath), "Last uploaded resume");
            logger.info("Saved state to: {}", statePath.toAbsolutePath());
        } catch (IOException e) {
            logger.warn("Could not save state file: {}", e.getMessage());
        }
    }
}
