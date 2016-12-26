import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by Лешка on 25.12.2016.
 */
public class TestCode {
    WebDriver driver;
    WebDriver.Options options;
    WebDriverWait explWait;

    @BeforeClass
    @Parameters("browser")
    public void launching(int browser) {
        try {
            if (browser==1) {
                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
            }
            else if (browser==2) {
                DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
            }
            else {
                DesiredCapabilities capabilities = DesiredCapabilities.android();
                driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SkipException("Unable to create RemoteWebDriver Instance");
        }
        options = driver.manage();
        options.timeouts().implicitlyWait(9, TimeUnit.SECONDS);
        explWait = new WebDriverWait(driver, 20);

    }

    @Test
    public void point1_2() {
        driver.navigate().to("http://www.bing.com/");
        explWait.until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"sbox\"]/div[1]")),
        ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"bLogoExp\"]"))));  //проверка включает андроидную верстку
        Assert.assertTrue(driver.findElement(By.xpath("//*[@id=\"sbox\"]/div[1]")).isDisplayed() ||
                        driver.findElement(By.xpath("//*[@id=\"bLogoExp\"]")).isDisplayed(),
                "Logo isn't displayed");
        Assert.assertNotNull(driver.findElement(By.id("sb_form_q")), "Input Field is absent");
        Assert.assertTrue(driver.findElement(By.id("sb_form_q")).isDisplayed(), "Input field is invisible");
        Assert.assertNotNull(driver.findElement(By.xpath("//*[@type='submit']")), "The submit button is absent");
        Assert.assertTrue(driver.findElement(By.xpath("//*[@type='submit']")).isDisplayed());

    }

    @Test(dataProvider = "queries", dataProviderClass = DataProviders.class, dependsOnMethods = "point1_2")
    public void restOfPoints(String text) {
        driver.findElement(By.id("sb_form_q")).click();
        driver.findElement(By.id("sb_form_q")).sendKeys(text);
        explWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("sa_sg")));
        driver.findElement(By.cssSelector("#sa_5005")).click();
        explWait.until(ExpectedConditions.titleContains(text));
        String url1 = driver.findElement(By.xpath("//*[@id='b_results']/li[1]//cite")).getText();
        if (!url1.startsWith("https")) {
            url1 = "http://" + driver.findElement(By.xpath("//*[@id='b_results']/li[1]//cite")).getText() + "/";
        }
        System.out.println(url1);
        String searchTitle = driver.getTitle();
        driver.findElement(By.xpath("//*[@id=\"b_results\"]/li[1]//h2")).click();
        explWait.until(ExpectedConditions.not(ExpectedConditions.titleIs(searchTitle)));
        String url2 = driver.getCurrentUrl();
        System.out.println(url2);
        Assert.assertEquals(url2,url1, "URLs aren't matched!!1!");
        driver.navigate().back();
        explWait.until(ExpectedConditions.titleContains(text));
        driver.findElement(By.id("sb_form_q")).clear();
        explWait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(By.id("sb_form_q"), "value", text)));
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
