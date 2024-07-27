package JobFindingPackage;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SoftwareTesterLinkedIn {

    private static final String URL = "https://www.linkedin.com/checkpoint/lg/sign-in-another-account?trk=guest_homepage-basic_nav-header-signin";
    private static final String USERNAME = "Sample@gmail.com";
    private static final String PASSWORD = "123";
    private static final String CSV_FILE = "job_links.csv";

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        try {
            // Setup WebDriver and open LinkedIn
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.get(URL);

            // Log in to LinkedIn with the credentials
            loginToLinkedIn(driver);

            // Search for jobs
            searchForJobs(driver);

            // Extract job links from the search results
            Set<String> jobLinks = getJobLinks(driver);

            // Read existing job links from CSV
            Set<String> existingLinks = readExistingLinks(CSV_FILE);

            // Identify and filter out new job links
            Set<String> newLinks = filterNewLinks(jobLinks, existingLinks);

            // Write new job links to the CSV file
            writeNewLinks(CSV_FILE, newLinks);

            System.out.println(newLinks.size() + " new links have been added to " + CSV_FILE);
        } finally {
            driver.quit();
        }
    }

    private static void loginToLinkedIn(WebDriver driver) throws InterruptedException {
        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys(USERNAME);

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(PASSWORD);

        WebElement signInButton = driver.findElement(By.xpath("//button[@aria-label='Sign in']"));
        signInButton.click();

        // Wait for login to complete
        Thread.sleep(2000);
    }

    private static void searchForJobs(WebDriver driver) throws InterruptedException {
        WebElement jobsButton = driver.findElement(By.xpath("//a[@href='/jobs/?']"));
        jobsButton.click();

        // Wait for jobs page to load
        Thread.sleep(2000);

        WebElement searchBox = driver.findElement(By.cssSelector("input[id^='jobs-search-box-keyword-id-ember']"));
        searchBox.sendKeys("Software Tester");
        searchBox.sendKeys(Keys.RETURN);

        // Wait for search results to load
        Thread.sleep(5000);

        // Apply date filter for job postings
        WebElement dateFilter = driver.findElement(By.id("searchFilter_timePostedRange"));
        dateFilter.click();

        WebElement dateButton = driver.findElement(By.id("timePostedRange-r86400"));
        dateButton.click();

        WebElement applyFilterButton = driver.findElement(By.xpath("//button[starts-with(@aria-label,'Apply current filter')]"));
        applyFilterButton.click();

        // Wait for filters to apply
        Thread.sleep(2000);
    }

    private static Set<String> getJobLinks(WebDriver driver) {
        Set<String> jobLinks = new HashSet<>();
        List<WebElement> jobLinkElements = driver.findElements(By.xpath("//a[contains(@class, 'job-card-list__title')]"));
        for (WebElement jobLinkElement : jobLinkElements) {
            String jobUrl = jobLinkElement.getAttribute("href");
            jobLinks.add(jobUrl);
        }
        return jobLinks;
    }

    private static Set<String> readExistingLinks(String fileName) {
        Set<String> links = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                links.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return links;
    }

    private static Set<String> filterNewLinks(Set<String> jobLinks, Set<String> existingLinks) {
        Set<String> newLinks = new HashSet<>();
        for (String jobLink : jobLinks) {
            if (!existingLinks.contains(jobLink)) {
                newLinks.add(jobLink);
            }
        }
        return newLinks;
    }

    private static void writeNewLinks(String fileName, Set<String> newLinks) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            for (String link : newLinks) {
                bw.write(link);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
