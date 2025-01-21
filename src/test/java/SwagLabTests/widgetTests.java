package SwagLabTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.swing.*;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class widgetTests {
    WebDriver driver;

    @BeforeTest
    public void beforeTest(){
        WebDriverManager.chromedriver().clearDriverCache().driverVersion("").setup();
        ChromeOptions ops=new ChromeOptions();
        ops.addArguments("--remote-origin-allow=*");
        ops.addArguments("--start-maximized");
        ops.addArguments("--incognito");
        // --- these two lines get rid of the banner msg that chrome is being controlled
        ops.setExperimentalOption("useAutomationExtension", false);
        ops.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

        driver=new ChromeDriver(ops);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
    }

    @Test
    public void dragAndDrop() throws InterruptedException{
        driver.get("https://demoqa.com/droppable");

        Actions action=new Actions(driver);

        //--- get the drag and drop webelements
        WebElement drag=driver.findElement(By.id("draggable"));
        WebElement drop=driver.findElement(By.xpath("(//div[@id='droppable'])[1]"));

        action.dragAndDrop(drag,drop).perform();
        Thread.sleep(2000);
    }

    @Test
    public void testColorChange() throws InterruptedException {
        driver.get("https://demoqa.com/dynamic-properties");
        WebElement button=driver.findElement(By.id("colorChange"));

        // ---- Getting css values of web element
        String beforeColor=button.getCssValue("color");
        button.click();

        //color changes only after 5 secs so sleep
        Thread.sleep(6000);
        String afterColor=button.getCssValue("color");

        System.out.println(beforeColor+" "+afterColor);

        assert afterColor!=beforeColor;
    }

    @Test
    public void testSlider() throws InterruptedException {
        driver.get("https://demoqa.com/slider");
        WebElement slider = driver.findElement(By.xpath("//input[contains(@class,'range-slider')]"));

        //wait until the element is clickable
        WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(slider));

        //First Click on the slider and the press arrow right to move the slider
        Actions ac=new Actions(driver); // needed for mouse and keyboard actions
        slider.click();

        for(int i=0;i<5;i++){
            ac.sendKeys(Keys.ARROW_RIGHT).build().perform();
            Thread.sleep(500);
        }
    }

    @Test
    public void testDownload() throws InterruptedException {
        driver.get("https://demoqa.com/upload-download");

        WebElement downloadBtn = driver.findElement(By.id("downloadButton"));
        WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(downloadBtn));

        downloadBtn.click();

    }

    @Test
    public void testUpload() throws InterruptedException {
        driver.get("https://demoqa.com/upload-download");

        WebElement uploadBtn = driver.findElement(By.id("uploadFile"));
        WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(uploadBtn));

        Thread.sleep(1000);

        //-------------- To UPLOAD FILE ------------------------
        uploadBtn.sendKeys("C:\\Users\\meesh\\Downloads\\sampleFile.jpeg");

        Thread.sleep(2000);
        String uploadedFilePath=driver.findElement(By.id("uploadedFilePath")).getText();
        if(uploadedFilePath.contains("sampleFile.jpeg")){
            Assert.assertTrue(true);
        }else{
            Assert.fail("Incorrect file");
        }
    }

    @Test
    public void testDynamicButtons() throws InterruptedException {
        driver.get("https://demoqa.com/dynamic-properties");

        WebElement enableAfterBtn = driver.findElement(By.id("enableAfter"));
        WebElement visibleAfterBtn = driver.findElement(By.id("visibleAfter"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // --- wait conditions for the two diff buttons
        wait.until(ExpectedConditions.elementToBeClickable(enableAfterBtn));
        wait.until(ExpectedConditions.visibilityOf(visibleAfterBtn));

    }

    @Test
    public void testAutoComplete() throws InterruptedException {
        driver.get("https://jqueryui.com/autocomplete/");

        // --- For <i-frame> we need to switch to frame
        driver.switchTo().frame(driver.findElement(By.className("demo-frame")));
        Thread.sleep(2000);

        WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(10));

        //--- input j in the search box
        driver.findElement(By.id("tags")).sendKeys("j");
        WebElement optionBox=driver.findElement(By.id("ui-id-1"));

        // ---- wait until the options pop up
        wait.until(ExpectedConditions.visibilityOf(optionBox));

        List<WebElement> optionsList=optionBox.findElements(By.tagName("li"));

        for(WebElement option:optionsList){
            if(option.getText().equals("Java")){
                option.click();
                break;
            }
        }

    }

    @AfterTest
    public void afterTest() throws InterruptedException{
        Thread.sleep(2000);
        driver.quit();
    }
}
