package com.naukri.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NaukriJobApplyPage {
    private static final Logger logger = LoggerFactory.getLogger(NaukriJobApplyPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final String STATE_FILE = "src/com/naukri/config/job_apply.properties";
    private static final int MAX_APPLICATIONS_PER_RUN = 10;

    public NaukriJobApplyPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public List<String> searchAndApply(String keyword, String location) {
        Properties appliedJobs = loadAppliedJobs();
        List<String> appliedList = new ArrayList<>();

        try {
            String encoded = keyword.replace(" ", "+");
            String searchUrl = "https://www.naukri.com/job-search?k=" + encoded
                + "&l=" + location.replace(" ", "+");
            logger.info("Searching jobs: {}", searchUrl);
            driver.get(searchUrl);
            Thread.sleep(3000);

            List<WebElement> jobCards = driver.findElements(
                By.xpath("//article[contains(@class,'jobTuple') or contains(@class,'job-card')] | //div[contains(@class,'jobTuple')] | //div[contains(@class,'srp-jobtuple')]"));
            if (jobCards.isEmpty()) {
                jobCards = driver.findElements(By.xpath("//div[contains(@data-job-id,'')]"));
            }
            logger.info("Found {} job cards on page", jobCards.size());

            for (int i = 0; i < jobCards.size() && appliedList.size() < MAX_APPLICATIONS_PER_RUN; i++) {
                try {
                    WebElement card = jobCards.get(i);
                    String jobId = card.getAttribute("data-job-id");
                    if (jobId == null || jobId.isEmpty()) {
                        jobId = String.valueOf(card.hashCode());
                    }

                    if (isAlreadyApplied(appliedJobs, jobId)) {
                        continue;
                    }

                    By[] titleLocators = {
                        By.xpath(".//a[contains(@class,'title')]"),
                        By.xpath(".//a[contains(@class,'job-title')]"),
                        By.xpath(".//a[@data-job-id]"),
                        By.xpath(".//a[contains(@class,'jobTuple')]//a[2]"),
                        By.xpath(".//a[contains(@href,'/job-listings/')]")
                    };
                    String jobTitle = "";
                    for (By loc : titleLocators) {
                        try { jobTitle = card.findElement(loc).getText().trim(); if (!jobTitle.isEmpty()) break; } catch (Exception ignored) {}
                    }
                    if (jobTitle.isEmpty()) {
                        jobTitle = "Job #" + i;
                    }

                    By[] companyLocators = {
                        By.xpath(".//a[contains(@class,'subTitle')]"),
                        By.xpath(".//a[contains(@class,'company')]"),
                        By.xpath(".//span[contains(@class,'company')]"),
                        By.xpath(".//a[contains(@class,'org')]"),
                        By.xpath(".//div[contains(@class,'company')]//a")
                    };
                    String company = "";
                    for (By loc : companyLocators) {
                        try { company = card.findElement(loc).getText().trim(); if (!company.isEmpty()) break; } catch (Exception ignored) {}
                    }

                    if (tryEasyApply(card, jobId, jobTitle, company)) {
                        appliedList.add(company + " - " + jobTitle);
                        markApplied(appliedJobs, jobId, jobTitle, company);
                    }

                    int delay = 30000 + (int)(Math.random() * 60000);
                    logger.info("Waiting {}s before next application...", delay / 1000);
                    Thread.sleep(delay);

                } catch (Exception e) {
                    logger.debug("Skipping job card {}: {}", i, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Job search failed: {}", e.getMessage());
        }

        saveAppliedJobs(appliedJobs);
        return appliedList;
    }

    private boolean tryEasyApply(WebElement card, String jobId, String title, String company) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", card);
            Thread.sleep(500);

            By[] applyLocators = {
                By.xpath(".//*[contains(text(),'Easy Apply')]"),
                By.xpath(".//*[contains(text(),'Apply')]"),
                By.xpath(".//button[contains(@class,'apply')]"),
                By.xpath(".//a[contains(@class,'apply')]"),
                By.xpath(".//*[contains(@class,'apply')][not(self::script)]")
            };
            WebElement applyBtn = null;
            for (By loc : applyLocators) {
                try {
                    applyBtn = card.findElement(loc);
                    if (applyBtn.isDisplayed()) break;
                } catch (Exception ignored) {}
            }
            if (!applyBtn.isDisplayed()) return false;

            js.executeScript("arguments[0].click();", applyBtn);
            logger.info("Applied to: {} at {} (jobId: {})", title, company, jobId);
            Thread.sleep(2000);

            try {
                WebElement submitBtn = driver.findElement(
                    By.xpath("//button[contains(text(),'Submit') or contains(text(),'Send')]"));
                if (submitBtn.isDisplayed()) {
                    submitBtn.click();
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                logger.debug("No extra submit step needed");
            }

            try {
                driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                Thread.sleep(1000);
            } catch (Exception ignored) {}

            return true;

        } catch (Exception e) {
            logger.debug("Could not apply to {}: {}", title, e.getMessage());
            return false;
        }
    }

    private Properties loadAppliedJobs() {
        Properties props = new Properties();
        File f = new File(STATE_FILE);
        if (f.exists()) {
            try (InputStream in = new FileInputStream(f)) {
                props.load(in);
            } catch (IOException e) {
                logger.warn("Could not load applied jobs state");
            }
        }
        return props;
    }

    private boolean isAlreadyApplied(Properties props, String jobId) {
        String val = props.getProperty(jobId);
        if (val == null) return false;
        String[] parts = val.split("\\|");
        if (parts.length < 2) return true;
        try {
            LocalDate appliedDate = LocalDate.parse(parts[0].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            return appliedDate.isAfter(LocalDate.now().minusDays(30));
        } catch (Exception e) {
            return true;
        }
    }

    private void markApplied(Properties props, String jobId, String title, String company) {
        String value = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + " | " + title + " | " + company;
        props.setProperty(jobId, value);
    }

    private void saveAppliedJobs(Properties props) {
        File f = new File(STATE_FILE);
        f.getParentFile().mkdirs();
        try (OutputStream out = new FileOutputStream(f)) {
            props.store(out, "Auto-applied jobs - " + LocalDate.now());
        } catch (IOException e) {
            logger.warn("Could not save applied jobs state");
        }
    }
}
