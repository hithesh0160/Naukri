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
    private static final int MAX_APPLICATIONS_PER_RUN = 5;
    private static final int MAX_JOBS_TO_SCAN_PER_KEYWORD = 20;

    public NaukriJobApplyPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public List<String> searchAndApply(String keyword, String location) {
        Properties appliedJobs = loadAppliedJobs();
        List<String> appliedList = new ArrayList<>();

        try {
            String searchUrl = buildSearchUrl(keyword, location);
            logger.info("Searching jobs: {}", searchUrl);
            driver.get(searchUrl);
            Thread.sleep(3000);

            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//article[contains(@class,'jobTuple') or contains(@class,'job-card') or contains(@class,'srp-jobtuple')]")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href,'/job-listings-')]")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href,'/job-listings/')]"))
            ));

            List<JobLead> jobs = collectJobLeads();
            logger.info("Collected {} job links to scan", jobs.size());

            int scanCount = Math.min(jobs.size(), MAX_JOBS_TO_SCAN_PER_KEYWORD);
            for (int i = 0; i < scanCount && appliedList.size() < MAX_APPLICATIONS_PER_RUN; i++) {
                try {
                    JobLead job = jobs.get(i);

                    if (isAlreadyApplied(appliedJobs, job.id)) {
                        logger.info("Skipping already applied job: {} at {}", job.title, job.company);
                        continue;
                    }

                    if (tryApplyFromJobPage(job)) {
                        appliedList.add(job.company + " - " + job.title);
                        markApplied(appliedJobs, job.id, job.title, job.company);

                        int delay = 30000 + (int)(Math.random() * 60000);
                        logger.info("Waiting {}s before next application...", delay / 1000);
                        Thread.sleep(delay);
                    } else {
                        Thread.sleep(2000 + (long)(Math.random() * 3000));
                    }

                } catch (Exception e) {
                    logger.debug("Skipping job {}: {}", i, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Job search failed: {}", e.getMessage());
        }

        saveAppliedJobs(appliedJobs);
        return appliedList;
    }

    private String buildSearchUrl(String keyword, String location) {
        String keywordSlug = toNaukriSlug(keyword);
        String locationSlug = toNaukriSlug(normalizeLocation(location));
        if (locationSlug.isEmpty()) {
            return "https://www.naukri.com/" + keywordSlug + "-jobs";
        }
        return "https://www.naukri.com/" + keywordSlug + "-jobs-in-" + locationSlug;
    }

    private String normalizeLocation(String location) {
        if (location == null) {
            return "";
        }
        String normalized = location.trim();
        if (normalized.equalsIgnoreCase("Bengaluru")) {
            return "Bangalore";
        }
        return normalized;
    }

    private String toNaukriSlug(String value) {
        if (value == null) {
            return "";
        }
        String slug = value.toLowerCase(Locale.ROOT)
            .replace("&", " and ")
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
        return slug.isBlank() ? "jobs" : slug;
    }

    private List<JobLead> collectJobLeads() {
        List<JobLead> jobs = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();

        List<WebElement> links = driver.findElements(By.xpath("//a[contains(@href,'/job-listings-')] | //a[contains(@href,'/job-listings/')]"));
        logger.info("Found {} job listing links on page", links.size());

        for (int i = 0; i < links.size(); i++) {
            WebElement link = links.get(i);
            try {
                String href = link.getAttribute("href");
                if (href == null || href.isBlank() || !seenUrls.add(href)) {
                    continue;
                }

                WebElement card = findJobCardContainer(link);
                String jobId = card.getAttribute("data-job-id");
                if (jobId == null || jobId.isBlank()) {
                    jobId = extractJobId(href);
                }

                String title = firstNonBlank(link.getText(), "Job #" + i);
                String company = textFromFirst(card, new By[]{
                    By.xpath(".//a[contains(@class,'subTitle')]"),
                    By.xpath(".//a[contains(@class,'company')]"),
                    By.xpath(".//span[contains(@class,'company')]"),
                    By.xpath(".//a[contains(@class,'org')]"),
                    By.xpath(".//div[contains(@class,'company')]//a")
                });

                jobs.add(new JobLead(jobId, title, company, href));
            } catch (Exception e) {
                logger.debug("Could not collect job lead {}: {}", i, e.getMessage());
            }
        }
        return jobs;
    }

    private WebElement findJobCardContainer(WebElement link) {
        try {
            return link.findElement(By.xpath("./ancestor::*[.//a[contains(@href,'/job-listings-') or contains(@href,'/job-listings/')]][contains(@class,'srp-jobtuple') or contains(@class,'jobTuple') or contains(@class,'job-card') or .//h2][1]"));
        } catch (Exception ignored) {
            try {
                return link.findElement(By.xpath("./ancestor::*[.//h2][1]"));
            } catch (Exception ignoredAgain) {
                return link;
            }
        }
    }

    private boolean tryApplyFromJobPage(JobLead job) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            logger.info("Opening job: {} at {} ({})", job.title, job.company, job.url);
            driver.get(job.url);
            Thread.sleep(3000);
            closeOverlays();

            if (pageContains("already applied") || pageContains("applied")) {
                logger.info("Job is already applied: {} at {}", job.title, job.company);
                return false;
            }

            WebElement applyBtn = findDisplayed(new By[]{
                By.xpath("//button[normalize-space()='Apply']"),
                By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'apply')]"),
                By.xpath("//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'apply')]"),
                By.xpath("//*[contains(@class,'apply') and (self::button or self::a or @role='button')]")
            }, 8);
            if (applyBtn == null) {
                logger.info("No apply button found for: {} at {}", job.title, job.company);
                return false;
            }

            js.executeScript("arguments[0].click();", applyBtn);
            logger.info("Clicked Apply for: {} at {} (jobId: {})", job.title, job.company, job.id);
            Thread.sleep(3000);

            // Aggressively close any popups/overlays that appeared after clicking Apply
            // Naukri shows multiple popups: resume upload prompts, recommendations, chatbots, etc.
            closeOverlays(3);

            // Check if clicking Apply opened a new tab or window (external redirect)
            String urlAfterClick = driver.getCurrentUrl();
            // Case 1: URL changed to a different domain (redirected to external site)
            if (!urlAfterClick.contains("naukri.com")) {
                logger.warn("Apply redirected to external site: {}. Skipping this job.", urlAfterClick);
                // Navigate back to the job page
                driver.navigate().to(job.url);
                Thread.sleep(2000);
                return false;
            }

            // Case 2: A new tab/window was opened (external site)
            String originalWindow = driver.getWindowHandle();
            Set<String> windowHandles = driver.getWindowHandles();
            if (windowHandles.size() > 1) {
                logger.warn("Apply opened a new window/tab (likely external site). Skipping this job.");
                // Close the new window and switch back
                for (String handle : windowHandles) {
                    if (!handle.equals(originalWindow)) {
                        driver.switchTo().window(handle);
                        String newWindowUrl = driver.getCurrentUrl();
                        logger.warn("New window URL: {}", newWindowUrl);
                        driver.close();
                    }
                }
                driver.switchTo().window(originalWindow);
                // Navigate back to the job page
                driver.navigate().to(job.url);
                Thread.sleep(2000);
                return false;
            }

            // Dismiss popups again before looking for submit button
            closeOverlays(2);

            WebElement submitBtn = findDisplayed(new By[]{
                By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submit')]"),
                By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'send')]"),
                By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'apply')]")
            }, 3);
            if (submitBtn != null && submitBtn.isEnabled()) {
                js.executeScript("arguments[0].click();", submitBtn);
                Thread.sleep(2500);
            }

            // Final popup cleanup after submission
            closeOverlays(2);
            if (pageContains("successfully applied")
                || pageContains("application sent")
                || pageContains("applied successfully")
                || pageContains("you have applied")
                || pageContains("already applied")) {
                logger.info("Application confirmed for: {} at {}", job.title, job.company);
                return true;
            }

            logger.info("Apply click did not produce a success confirmation for: {} at {}", job.title, job.company);
            return false;

        } catch (Exception e) {
            logger.debug("Could not apply to {}: {}", job.title, e.getMessage());
            return false;
        }
    }

    private WebElement findFirst(WebElement root, By[] locators) {
        for (By locator : locators) {
            try {
                WebElement element = root.findElement(locator);
                if (element != null) {
                    return element;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private WebElement findDisplayed(By[] locators, int timeoutSeconds) {
        long end = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        while (System.currentTimeMillis() < end) {
            for (By locator : locators) {
                try {
                    for (WebElement element : driver.findElements(locator)) {
                        if (element.isDisplayed()) {
                            return element;
                        }
                    }
                } catch (Exception ignored) {}
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    private String textFromFirst(WebElement root, By[] locators) {
        WebElement element = findFirst(root, locators);
        return element == null ? "" : firstNonBlank(element.getText(), "");
    }

    private String firstNonBlank(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private String extractJobId(String href) {
        if (href == null) {
            return UUID.randomUUID().toString();
        }
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("-(\\d+)(?:\\?|$)").matcher(href);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return String.valueOf(href.hashCode());
    }

    private boolean pageContains(String text) {
        try {
            return driver.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT).contains(text);
        } catch (Exception e) {
            return false;
        }
    }

    private void closeOverlays() {
        closeOverlays(1);
    }

    /**
     * Aggressively close all popups/overlays/dialogs. Retries multiple rounds with increasing XPath coverage.
     */
    private void closeOverlays(int rounds) {
        try {
            for (int r = 0; r < rounds; r++) {
                boolean closed = false;

                // Round 1: All possible close/cross/dismiss buttons
                By[] closeLocators = {
                    By.xpath("//div[contains(@class,'crossIcon')]"),
                    By.xpath("//span[contains(@class,'crossIcon')]"),
                    By.xpath("//i[contains(@class,'crossIcon')]"),
                    By.xpath("//*[contains(@class,'crossLayer') or contains(@class,'draft-cross')]"),
                    By.xpath("//button[contains(@class,'close')]"),
                    By.xpath("//span[contains(@class,'close')]"),
                    By.xpath("//*[contains(@class,'dismiss')]"),
                    By.xpath("//*[contains(text(),'Not now')]"),
                    By.xpath("//*[contains(text(),'Not Now')]"),
                    By.xpath("//*[contains(text(),'Skip')]"),
                    By.xpath("//*[contains(text(),'Maybe later')]"),
                    By.xpath("//*[contains(@aria-label,'Close')]"),
                    By.xpath("//*[contains(@aria-label,'close')]"),
                    By.xpath("//button[normalize-space()='Cancel']"),
                    By.xpath("//span[normalize-space()='Cancel']"),
                    By.cssSelector("button[class*='close']"),
                    By.cssSelector("span[class*='close']"),
                    By.cssSelector("div[class*='close']"),
                    By.xpath("//*[contains(@class,'modal')]//*[self::button or self::span or self::i][contains(@class,'close') or contains(@class,'cross')]"),
                    By.xpath("//*[contains(@class,'drawer')]//*[self::button or self::span or self::i][contains(@class,'close') or contains(@class,'cross')]"),
                    By.xpath("//*[contains(@class,'lightbox')]//*[self::button or self::span or self::i][contains(@class,'close') or contains(@class,'cross')]")
                };
                for (By locator : closeLocators) {
                    try {
                        for (WebElement el : driver.findElements(locator)) {
                            if (el.isDisplayed()) {
                                try {
                                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                                    closed = true;
                                    Thread.sleep(300);
                                } catch (Exception ignored) {}
                            }
                        }
                    } catch (Exception ignored) {}
                }

                // Round 2: Try clicking any visible button/text that says close/dismiss
                try {
                    WebElement body = driver.findElement(By.tagName("body"));
                    String bodyText = body.getText().toLowerCase(Locale.ROOT);
                    for (String keyword : new String[]{"close", "cancel", "dismiss", "not now", "skip", "later"}) {
                        if (bodyText.contains(keyword)) {
                            try {
                                WebElement el = driver.findElement(By.xpath(
                                    "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + keyword + "')]"
                                ));
                                if (el.isDisplayed()) {
                                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                                    closed = true;
                                    Thread.sleep(300);
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                } catch (Exception ignored) {}

                // Round 3: Send ESCAPE key multiple times
                if (!closed) {
                    try {
                        WebElement body = driver.findElement(By.tagName("body"));
                        for (int i = 0; i < 3; i++) {
                            body.sendKeys(Keys.ESCAPE);
                            Thread.sleep(300);
                        }
                    } catch (Exception ignored) {}
                }

                // Round 4: Blur active element
                try {
                    ((JavascriptExecutor) driver).executeScript("document.activeElement?.blur();");
                } catch (Exception ignored) {}

                if (!closed) break; // No more popups to close
            }
            Thread.sleep(500);
        } catch (Exception e) {
            logger.debug("Error closing overlays: {}", e.getMessage());
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

    private static class JobLead {
        final String id;
        final String title;
        final String company;
        final String url;

        JobLead(String id, String title, String company, String url) {
            this.id = id;
            this.title = title;
            this.company = company;
            this.url = url;
        }
    }
}
