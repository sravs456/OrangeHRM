package StepDefinitions;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.After;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class HRMSteps {
    WebDriver driver;
    public List<String> addedEmployeeNames = new ArrayList<>();
    private void retryClick(WebElement element) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                element.click();
                break;
            } catch (StaleElementReferenceException e) {
                // Element became stale, retry locating the element
                element = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(element));
            }
            attempts++;
        }
    }

    @Given("User is on the login page")
    public void userIsOnLoginPage() {
        // Set up WebDriver and navigate to the login page
        System.setProperty("web-driver.chrome.driver", "C://Users//Sravani//Downloads//chromedriver-win64//chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        driver.manage().window().maximize();
    }

    @When("User enters valid username and password")
    public void userEntersValidCredentials() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3000));
        WebElement userName = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='username']")));

        userName.click();

        userName.sendKeys("Admin");

        WebElement password = driver.findElement(By.xpath("//input[@type= 'password']"));
        password.click();
        password.sendKeys("admin123");

    }

    @When("User clicks on the login button")
    public void userClicksOnLoginButton() {
        driver.findElement(By.xpath("//button[@type= 'submit']")).click();
    }

    @When("User clicks on PIM")
    public void userClicksOnPIM() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));
        WebElement pimMenu = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//body/div[@id='app']/div[1]/div[1]/aside[1]/nav[1]/div[2]/ul[1]/li[2]/a[1]/span[1]")));
        pimMenu.click();
    }

    @Then("User adds employees from the data in the CSV file")
    public void userAddsEmployeesFromCSV() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));
        String csvFilePath = "C://Users//Sravani//OneDrive//Desktop//Important//OrangeHRM.csv";

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> employeeDataList = reader.readAll();

            // Skip the header row (if present)
            if (!employeeDataList.isEmpty()) {
                employeeDataList.remove(0);
            }
            for (String[] employeeData : employeeDataList) {
                String firstName = employeeData[0];
                String middleName = employeeData[1];
                String lastName = employeeData[2];
                addEmployee(firstName, middleName, lastName);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public void addEmployee(String firstName, String middleName, String lastName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1000));
        WebElement addEmp = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//html/body/div/div[1]/div[1]/header/div[2]/nav/ul/li[3]")));
        addEmp.click();
        WebElement first = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='firstName']")));
        first.sendKeys(firstName);
        driver.findElement(By.xpath("//input[@name='middleName']")).sendKeys(middleName);
        driver.findElement(By.xpath("//input[@name='lastName']")).sendKeys(lastName);
        WebElement addButton = driver.findElement(By.xpath("//html[1]/body[1]/div[1]/div[1]/div[2]/div[2]/div[1]/div[1]/form[1]/div[3]/button[2]"));
        addButton.click();
        addedEmployeeNames.add(firstName);
        driver.navigate().back();
    }
    @And("User verifies the presence of added employee on employee list page")
    public void userClicksOnEmployeeList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement employeeListLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='app']/div[1]/div[1]/header/div[2]/nav/ul/li[2]/a")));
        employeeListLink.click();

        Actions actions = new Actions(driver);

        for (String employeeName : addedEmployeeNames) {
            // Clear the input field
            WebElement empNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='app']/div[1]/div[2]/div[2]/div/div[1]/div[2]/form/div[1]/div/div[1]/div/div[2]/div/div/input")));
            empNameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);

            // Type the employee name
            empNameInput.sendKeys(employeeName);

            // Use Actions class to press down arrow and then press Enter to select the first autosuggestion
            actions.sendKeys(empNameInput, Keys.ARROW_DOWN).sendKeys(Keys.ENTER).perform();

            // Wait for the brief moment for the suggestion to become stable before verifying
            wait.until(ExpectedConditions.attributeToBe(empNameInput, "value", employeeName));

            // Verify if the employee name is present in the input field
            if (empNameInput.getAttribute("value").toLowerCase().contains(employeeName.toLowerCase())) {
                System.out.println("Verified: " + employeeName);
            } else {
                System.out.println("No records found for: " + employeeName);
            }
        }
    }

//    public void userClicksOnEmployeeList() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        WebElement employeeListLink = wait.until(ExpectedConditions.presenceOfElementLocated
//                (By.xpath("//*[@id='app']/div[1]/div[1]/header/div[2]/nav/ul/li[2]/a")));
//        employeeListLink.click();
//
//        Actions actions = new Actions(driver);
//
//        for (String employeeName : addedEmployeeNames) {
//            // Clear the input field
//            WebElement empNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated
//                    (By.xpath("//*[@id='app']/div[1]/div[2]/div[2]/div/div[1]/div[2]/form/div[1]/div/div[1]/div/div[2]/div/div/input")));
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            empNameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            // Type the employee name
//            empNameInput.sendKeys(employeeName);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            // Wait for the autosuggestion list to appear
////            WebElement autosuggestionList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@class='autocomplete-content dropdown-content']")));
////            try {
////                Thread.sleep(1000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//
//            // Use Actions class to press down arrow and then press Enter to select the first autosuggestion
//            actions.sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).perform();
//
//            // Wait for a brief moment for the suggestion to become stable before verifying
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            // Verify if the employee name is present in the input field
//            if (empNameInput.getAttribute("value").toLowerCase().contains(employeeName.toLowerCase())) {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("Verified: " + employeeName);
//            } else {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("No records found for: " + employeeName);
//            }
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            empNameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
//        }
//    }


    @Then("User logs out from the dashboard")
    public void userLogsOut() {
        // Implement the code to log out from the dashboard
        WebElement logoutLink = driver.findElement(By.xpath("//header/div[1]/div[2]/ul[1]/li[1]/span[1]/i[1]"));
        logoutLink.click();
        WebElement logoutButton = driver.findElement(By.xpath("//a[contains(text(),'Logout')]"));
        logoutButton.click();
    }

    @After
    public void tearDown() {
        // Close the WebDriver after the test execution
        driver.quit();
    }
}
