package SwagLabTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;
import java.time.Duration;

public class allTests {

    // ---- Declare the WebDriver ----
    WebDriver driver;

    @BeforeTest
    public void beforeTest(){

        // ---- Get the chrome driver from WebDrivverManager, clear cache with version as nill ---
        WebDriverManager.chromedriver().clearDriverCache().driverVersion("").setup();

        // specify different chromeoptions
        ChromeOptions ops=new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*"); // needed for jenkins
        ops.addArguments("--start-maximized");
        ops.addArguments("--incognito");

        // --- these two lines get rid of the banner msg that chrome is being controlled
        ops.setExperimentalOption("useAutomationExtension", false);
        ops.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

        // initialize the driver with ChromeDriver
        driver=new ChromeDriver(ops);
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        //-- implicitly wait for 10 seconds before loading the webpage, so that all the elements are loaded
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
    }

    @Test(priority = 1)
    public void invalidLogin() throws InterruptedException{

        // ------ launch the website
        driver.get("https://www.saucedemo.com/");
        Thread.sleep(2000);

        // -- input uname and password and hit login
        driver.findElement(By.id("user-name")).sendKeys("locked_out_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        Thread.sleep(2000);
        driver.findElement(By.id("login-button")).click();
        Thread.sleep(2000);

        // ---------------------------------------------------------------------------------------------------
        // Wait for 10 secs until error msg is displayed, check every 500 ms i.e 2 times every second
        WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),\"this user has been locked out.\")]")));

        // --- check if CORRECT error msg is displayed
        String errorMsg=driver.findElement(By.xpath("//h3[contains(text(),\"this user has been locked out.\")]")).getText();
        if(errorMsg.contains("user has been locked out")){
            Assert.assertTrue(true);  // test passed

        }else{
            Assert.fail(" Error Message is not correct!");  //test failed
        }
    }

    @Test(priority = 2)
    public void validLogin() throws InterruptedException {
        // -- input uname and password and hit login
//        driver.findElement(By.id("user-name")).clear();
//        driver.findElement(By.id("password")).clear();
        driver.get("https://www.saucedemo.com/");
        Thread.sleep(1000);
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        Thread.sleep(2000);
        driver.findElement(By.id("login-button")).click();
        Thread.sleep(2000);

        String pageTitleBar=driver.findElement(By.xpath("//span[@class=\"title\"]")).getText();
        if(pageTitleBar.equals("Products")){
            Assert.assertTrue(true);
            System.out.println("Logged In successfully!");
        }else{
            Assert.fail("Not the page after login!");
        }
    }

    @Test(priority = 3)
    public void addToCart() throws InterruptedException {
        Thread.sleep(1000);
        // --- click on 2 consecutive Add to cart buttons among the 6 present in the page
        for(int i=1;i<3;i++){
            driver.findElement(By.xpath("(//button[text()='Add to cart'])["+i+"]")).click();
            Thread.sleep(2000);
            JavascriptExecutor jse = (JavascriptExecutor)driver;
            jse.executeScript("window.scrollBy(0,250)");
        }

        Thread.sleep(3000);

        // -- Check if correct cart count is displayed
        int cartCounter=Integer.parseInt(driver.findElement(By.xpath("//span[@class='shopping_cart_badge']")).getText());
        if(cartCounter==2){
            Assert.assertTrue(true);
            System.out.println("Cart count is correct!");
        }else{
            Assert.fail("Cart count is Incorrect!");
        }

    }

    @Test(priority = 4)
    public void verifyNumOfItemsInCart() throws InterruptedException {
        // Click on the cart
        driver.findElement(By.className("shopping_cart_link")).click();
        Thread.sleep(3000);

        // get the total num of cart items as a list of WebElements
        List<WebElement> elements=driver.findElements(By.className("cart_item"));

        if(elements.size()==2){
            Assert.assertTrue(true);
            System.out.println(" Correct number of items in cart");
        }else{
            Assert.fail(" Incorrect numb er of items in cart");
        }
    }

    @Test(priority = 5)
    public void verifyCartTotalBeforeTax() throws InterruptedException {

        // ---- Click on checkout
        driver.findElement(By.id("checkout")).click();
        Thread.sleep(2000);

        // Checking if I click on continue without fillin g out the fields
        driver.findElement(By.id("continue")).click();
        Thread.sleep(2000);

        // Then wait and check for the correct error banner to pop up
        WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),'is required')]")));

        String errMsg=driver.findElement(By.xpath("//h3[contains(text(),'is required')]")).getText();
        if(errMsg.contains("is required")){
            Assert.assertTrue(true);
            System.out.println("Error msg for empty fileds displayed");
        }
        else {
            Assert.fail("Incorrect error msg.");
        }

        //---- Now fill in the fields and hit continue
        driver.findElement(By.id("first-name")).sendKeys("Shraddha");
        driver.findElement(By.id("last-name")).sendKeys("Shrestha");
        driver.findElement(By.id("postal-code")).sendKeys("76005");
        Thread.sleep(2000);
        driver.findElement(By.id("continue")).click();


        // Check if the cart item subtotal before tax is correct
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("window.scrollBy(0,250)");
        List<WebElement> priceList=driver.findElements(By.xpath("//div[@class='inventory_item_price']"));
        Double totalBeforeTax=0.00;
        for(WebElement p:priceList){
            String strPrice=p.getText();
            strPrice=strPrice.replaceAll("[a-zA-Z$]","");
            totalBeforeTax+=Double.parseDouble(strPrice);
        }

        // Get the subtotal from webpage
        String displayedTotalStr=driver.findElement(By.className("summary_subtotal_label")).getText().replaceAll("[a-zA-Z$:]","").trim();
        Double displayedTotal=Double.parseDouble(displayedTotalStr);
        Thread.sleep(3000);
        if(displayedTotal.equals(totalBeforeTax)){
            Assert.assertTrue(true);
            System.out.println("Total before tax is correct");
        }else{
            Assert.fail("Incorrect Total");
        }


    }
    @AfterTest
    public void afterTest(){
        driver.quit();
    }

}
