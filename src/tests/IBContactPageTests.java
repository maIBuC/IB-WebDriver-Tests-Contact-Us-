import org.testng.annotations.BeforeTest;
import org.testng.asserts.SoftAssert;
import org.testng.Assert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class IBContactPageTests {

    // Logic
    SoftAssert softAssert; // Assert Types reference: https://www.toolsqa.com/testng/testng-asserts/
    WebDriver chrome;
    JavascriptExecutor js;
    WebElement acceptButton;
    WebElement firstName;
    WebElement lastName;
    WebElement companyName;
    WebElement email;
    WebElement mobile;
    Select country;
    WebElement enquiryDetails;
    WebElement checkbox;

    //#region Test Functions

    @BeforeTest
    private void setupPreTestElements() {

        setupSoftAssert();
        setupChrome();
        closeCookiesPopup();
        setupInputFields();
        insertDataIntoAllContactUsFields();
    }

    @Test
    private void isFirstNameEntered() {
        Assert.assertNotEquals(getValue(firstName),"","The firstName field should be filled in.");
        System.out.println("Name was entered into 'First Name' field.");
    }

    @Test
    private void doesEmailMatchFormat() {

        Assert.assertTrue(getValue(email).matches("([A-z]+.[A-z]+@[A-z]+.[A-z.]+)"), "The Email field needs to be formatted like so: [firstName].[lastName]@[domain].\n" +
                "Email value that was entered: " +  (email));
        System.out.println("Email entered into 'Email' field matches expected format.");
    }

    @Test
    private void doesMobileMatchFormat() {

        Assert.assertTrue(getValue(mobile).matches("([0-9]{4}\\s[0-9]{3}\\s[0-9]{3})"), "Ensure Mobile is formatted like so: [4 nums][space][3 nums][space][3 nums].\n" +
                "Mobile value that was entered: " + getValueIfOneWasEntered(mobile));
        System.out.println("Mobile entered into 'Mobile' field matches expected format.");
    }

    //#endregion

    //#region Non Test Functions

    public void run() {

        setupPreTestElements();
        validateContactPageData();
    }

    //#region Miscellaneous Functions

    private WebElement findElementByParameters(String ElementType, String Attribute, String Value) {

        return chrome.findElement(By.xpath("//" + ElementType + "[@" + Attribute + "='" + Value + "']"));
    }

    private void waitUntilPossibleToClick(int Seconds, WebElement Element) {

        new WebDriverWait(chrome, Duration.ofSeconds(Seconds)).until(ExpectedConditions.elementToBeClickable(Element)).click(); // WebDriverWait Clickable Reference: https://stackoverflow.com/questions/49864965/org-openqa-selenium-elementnotinteractableexception-element-is-not-reachable-by
    }

    private String getValue(WebElement element) {

        return element.getAttribute("value");
    }

    private String getValueIfOneWasEntered(WebElement element) {

        var value = element.getAttribute("value");
        return value != "" ? value : "[No input]";
    }

    private void setupSoftAssert() {

        softAssert = new SoftAssert();
    }

    //#endregion

    //#region Test Preparation Functions

    private void setupChrome() {

        // Point To Local Chromedrive File So Java Can Run It
        System.setProperty("webdriver.chrome.driver", "chromedriver_mac_arm64/chromedriver");

        // Ensure Chrome Can Accept Remote Origin Connections (ie. WebDriver Requests/Tests)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*"); // Solution found here: https://groups.google.com/g/chromedriver-users/c/xL5-13_qGaA

        // Connect To Chrome & Webpage
        chrome = new ChromeDriver(options);
        chrome.get("https://www.intelligencebank.com/contact-us/");

        chrome.manage().window().maximize();
        chrome.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    private void closeCookiesPopup() {

        acceptButton = findElementByParameters("a", "id", "cookie_action_close_header");
        waitUntilPossibleToClick(60, acceptButton);

        // Remove Element From Page Reference: https://stackoverflow.com/questions/33199740/webdriver-remove-element-from-page
        js = (JavascriptExecutor) chrome;
        js.executeScript("return document.getElementsByClassName('chatbot__wrapper')[0].remove();"); // Done to ensure the overlay doesn't cover any elements we need to interact with.
        
        WebElement contactUsInputFrame = findElementByParameters("iframe", "src", "https://go.intelligencebank.com/l/941293/2021-08-09/2gdc87p?Form_url=https://www.intelligencebank.com/contact-us");
        chrome.switchTo().frame(contactUsInputFrame); // Switch to iframe via xPath reference: https://www.toolsqa.com/selenium-webdriver/handle-iframes-in-selenium/
    }

    private void setupInputFields() {

        firstName = findElementByParameters("input", "id", "First_Namepi_First_Name");
        lastName = findElementByParameters("input", "id", "Last_Namepi_Last_Name");
        companyName = findElementByParameters("input", "id", "Companypi_Company");
        email = findElementByParameters("input", "id", "Emailpi_Email");
        mobile = findElementByParameters("input", "id", "941293_138982pi_941293_138982");
        country = new Select(findElementByParameters("select", "id", "941293_138980pi_941293_138980"));
        enquiryDetails = findElementByParameters("textarea", "id", "941293_138986pi_941293_138986");
        checkbox = findElementByParameters("input", "id", "941293_139028pi_941293_139028_631736");
    }

    private void insertDataIntoAllContactUsFields() {

        firstName.sendKeys("Michael");
        lastName.sendKeys("Anning");
        companyName.sendKeys("IntelligenceBank");
        email.sendKeys("michael.anning@intellignecebank.com");
        // email.sendKeys("badEmail");
        mobile.sendKeys("0000 000 000");
        // mobile.sendKeys("badNumber");
        country.selectByVisibleText("Australia");
        enquiryDetails.sendKeys("I'm testing to see if I can use Selenium to perform assertion tests. This has nothing to do with contacting you, actually. Please ignore this. I BEG you.");
        js.executeScript("arguments[0].click();", checkbox);  // Click via JavaScript Reference: https://stackoverflow.com/questions/37879010/selenium-debugging-element-is-not-clickable-at-point-x-y
                                                                    // This won't work if you try to call 'checkBox.click()'. It'll say that another element beside it will be clicked instead.
    }

    private void validateContactPageData() {

        isFirstNameEntered();
        doesEmailMatchFormat();
        doesMobileMatchFormat();
    }

    //#endregion

    //#endregion
}