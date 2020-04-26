package example;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * Automate the following scenarios on Rakuten TV https://www.rakuten.tv/es
 * 1. Sign in -> #1 and #2 seem the same to me so I changed it to "Sign Up"=Register, which should run once per new user
 * 2. Log in -> using the user created above Log in
 * 3. Search content & check content detail page
 * 4. Add content to wishlist.
 *
 * NOTE: The tests are proof of concept, not even 10% exhaustive or optimal
 */
public class RakutenTest {
    private WebDriver driver;
    //All the strings can be replaced with enums or factory calls based on certain languages or the URL used.
    //TODO:reset to spanish or change to the language you have access to
//    private String homePageUrl= "https://www.rakuten.tv/es";
    private String homePageUrl= "https://www.rakuten.tv/ro";
    private String registerButton = "[data-test-id='menu-desktop-register-link']";
    private String loginButton = "[data-test-id='menu-desktop-login-link']";
//    private String homePageTitle = "Rakuten TV - Tu cine en casa";
    private String homePageTitle = "Rakuten TV - Your cinema at home";
    private String user = "APrakuten";
    private String password = "APrakuten123";
    private String emailField = "email";
    private String passwordField = "password";
    private String searchField = "search";
    private String termsCheckBox = ".i-checkbox";
    private String loggedUser = "div.nav__items__user.nav__items__user--logged > div > span";
    private String movieTitle = "Bohemian Rhapsody";

//    @Test
//    public void signUp(){
//        goToHomepage();
//        registerAccount(user,password);
//        checkLoggedIn();
//    }

//    @Test
//    public void logIn(){
//        goToHomepage();
//        logIn(user, password);
//        checkHomePageLoaded();
//        checkLoggedIn();
//    }

    @Test
    public void searchContent(){
        goToHomepage();
        logIn(user, password);
        checkHomePageLoaded();
        checkLoggedIn();
        searchByName(movieTitle);
        checkDetailPage();
    }


    @Test
    public void addToWishList(){
        goToHomepage();
        logIn(user, password);
        checkHomePageLoaded();
        checkLoggedIn();
        addWishList();
    }

    private void addWishList() {
        searchByName(movieTitle);
        List<WebElement> searchResults = driver.findElement(By.cssSelector(".contents__list")).findElements(By.cssSelector(".list__item.list__item--movies"));
        searchResults.get(0).click();
        WebElement wishListElem = driver.findElement(By.cssSelector(".round-action.round-action--wishlist"));
        String actualTitle = driver.findElement(By.cssSelector(".detail__info")).findElement(By.cssSelector(".detail__data__meta__title.detail__data__meta__title--full")).getText();
        Assert.assertEquals("The movie is already in the wishlist: " + actualTitle,
                "ADD TO WISHLIST",wishListElem.getText());
        wishListElem.click();
        waitOneSecond();
        //Update the web element to capture changes
        wishListElem = driver.findElement(By.cssSelector(".round-action.round-action--wishlist"));
        Assert.assertEquals("The movie was not added to the wishlist: " + actualTitle,
                "REMOVE FROM WISHLIST", wishListElem.getText());
        //Cleanup
        wishListElem.click();
        waitOneSecond();
        wishListElem = driver.findElement(By.cssSelector(".round-action.round-action--wishlist"));
        Assert.assertEquals("The movie is already in the wishlist: " + actualTitle,
                "ADD TO WISHLIST",wishListElem.getText());
    }


    private void checkDetailPage() {
        List<WebElement> searchResults = driver.findElement(By.cssSelector(".contents__list")).findElements(By.cssSelector(".list__item.list__item--movies"));
        searchResults.get(0).click();
        WebElement detailInfo = driver.findElement(By.cssSelector(".detail__info"));
        //Assert what you want to check. Example title check.
        String actualTitle = detailInfo.findElement(By.cssSelector(".detail__data__meta__title.detail__data__meta__title--full")).getText();
        Assert.assertTrue("The current movie is not as expected!",actualTitle.contains(movieTitle));
    }

    private void searchByName(String movieTitle) {
        WebElement search = driver.findElement(By.name(searchField));
        search.sendKeys(movieTitle+Keys.ENTER);
        List<WebElement> searchResults = driver.findElement(By.cssSelector(".contents__list")).findElements(By.cssSelector(".list__item.list__item--movies"));
        Assert.assertFalse("There are no results for the movie: "+ movieTitle,searchResults.isEmpty());
        String actualTitle;
        for (WebElement movie : searchResults) {
            actualTitle = movie.findElement(By.cssSelector(".artwork__title")).getText();
            Assert.assertTrue("The search result didn't return expected movies!" +
                            "\nExpected: " + movieTitle +
                            "\nActual: " + actualTitle,
                    actualTitle.contains(movieTitle));
        }
    }

    private void checkLoggedIn(){
        Assert.assertEquals(String.format("The '%s' user was not registered!",user),
                user,driver.findElement(By.cssSelector(loggedUser)).getText());
    }

    private void logIn(String user, String password){
        String email = user+"@gmail.com";
        driver.findElement( By.cssSelector(loginButton)).click();
        if (!driver.findElement(By.name(emailField)).isEnabled()){
            Assert.fail("The Email field is not present! The page shows:\n"+
                    driver.findElement(By.cssSelector(".card.card--credentials")).getText());
        }
        WebElement loginElement = driver.findElement(By.cssSelector(".card.card--login"));
        loginElement.findElement(By.name(emailField)).sendKeys(email);
        loginElement.findElement(By.name(passwordField)).sendKeys(password);
        loginElement.findElement(By.cssSelector(".form__submit")).click();
    }

    private void registerAccount(String user, String password) {
        String email = user+"@gmail.com";
        driver.findElement( By.cssSelector(registerButton)).click();
        if (!driver.findElement(By.name(emailField)).isEnabled()){
            Assert.fail("The Email field is not present! The page shows:\n"+
                            driver.findElement(By.cssSelector(".card.card--credentials")).getText());
        }
        WebElement registerElement = driver.findElement(By.cssSelector(".card.card--register"));
        registerElement.findElement(By.name(emailField)).sendKeys(email);
        registerElement.findElement(By.name(emailField+"Confirmation")).sendKeys(email);
        registerElement.findElement(By.name(passwordField)).sendKeys(password);

        List<WebElement> termsBox = registerElement.findElements(By.cssSelector(termsCheckBox));
        for (WebElement box : termsBox) {
            box.click();
            WebElement input = box.findElement(By.cssSelector("input"));
            Assert.assertTrue(String.format("The '%s' checkbox was not selected",input.getAttribute("id")),input.isSelected());
        }
        registerElement.findElement(By.cssSelector(".form__submit")).click();
    }

    private void goToHomepage(){
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        driver.navigate().to(homePageUrl);
        driver.manage().window().maximize();
        checkHomePageLoaded();
    }

    private void checkHomePageLoaded() {
        int repeatCounter = 0;
        while (!homePageUrl.equals(driver.getCurrentUrl()) && repeatCounter<10) {
            waitOneSecond();
            repeatCounter++;
        }
        Assert.assertEquals("The URL is not expected!", homePageUrl, driver.getCurrentUrl());
        Assert.assertEquals("The home page page is not expected!", homePageTitle, driver.getTitle());
    }

    private void waitOneSecond(){
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void closeDriver(){
        driver.close();
        driver.quit();
    }
}
