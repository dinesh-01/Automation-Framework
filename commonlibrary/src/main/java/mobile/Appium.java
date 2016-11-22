package mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;





import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import common.Log;

/**
 * Exposes method to write automation for web UI.
 */
public class Appium {
	private static final String NEW_LINE = "\n";
	public  static RemoteWebDriver webDriver = null;
	public static  AndroidDriver androidDriver = null;
	public static  IOSDriver iosDriver = null;
	public  static AppiumDriver mobileDriver = null;
	private static String url = "";
	private static URL remoteUrl = null;
	private static final int timeout = 12;
	private static WebElement table = null;
	private static WebElement highChart = null;
	private static String logFile = "Selenium.log";
	private static String logLevel = "ERROR";
	private static DesiredCapabilities capabilities = new DesiredCapabilities();
	
	
	

	public static void setBaseUrl(String baseUrl) {
		url = baseUrl;
	}

	
   	
	/**
	 * Initializes Mobile the Appium object <code>webDriver</code> fro mobile web 
	 * 
	 * @param platform
	 *            the possible values are 'anriod', 'ios'.
	 *            In case this value is set anything apart from these values or
	 *            blank, the default browser set is ios.
	          
	 *@param devictype
	 *             the possiable values are emulator or real device            
	 *            
	 * @throws Exception
	 */
	
	public static void mobileWeb(String platform, String deviceType)
			throws Exception {
		
		if (Appium.webDriver != null) {
			return; 	
		}
		
	switch (platform.toLowerCase()) {
	    case "android":
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceType);
			capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "chrome");
			remoteUrl = new URL(url);
			Appium.mobileDriver = new AndroidDriver (remoteUrl, capabilities);
			Appium.webDriver = Appium.mobileDriver;
            break;
		default:
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceType);
			capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "safari");
			remoteUrl = new URL(url);
			Appium.mobileDriver = new IOSDriver (remoteUrl, capabilities);
			Appium.webDriver = Appium.mobileDriver;
			break;
		}
		
			
	     
	}
	
	
	
//Lanching page
	
	public static void launchBrowser(String pageUrl) throws Exception {
		
		if (url.isEmpty() || pageUrl.contains("http")) {
			url = pageUrl;
		} else if (!url.endsWith("/") && !url.startsWith("/")) {
			url = url + "/" + pageUrl;
		} else {
			url = url + pageUrl;
		}
		
		Log.info("Setting URL: " + url);
	    Appium.webDriver.get(url);
	     
	}
	
	/**
	 * Find the element and click on it
	 * 
	 * @param elementId
	 *            - element identifier value
	 * @param findBy
	 *            - element identifier type e.g. id, xpath, css, class
	 */
	public void click(String elementIdentifier, String findBy) {
		WebElement element = getElement(elementIdentifier, findBy);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout));
		Log.info("Clicking on element: " + element);
		element.click();
	}
	
	
	/**
	 * Find the elements and click on it all
	 * 
	 * @param elementId
	 *            - element identifier value
	 * @param findBy
	 *            - element identifier type e.g. id, xpath, css, class
	 */
	public void clickAll(String elementIdentifier, String findBy) {
		List<WebElement> elements = getElements(elementIdentifier, findBy);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout));
		
		Log.info("Clicking on all elements: " + elements);
		for (WebElement element : elements) {
			element.click();
		}
	}
	
	
	/**
	 * Find the elements and click on it all
	 * 
	 * @param elementId
	 *            - element identifier value
	 * @param findBy
	 *            - element identifier type e.g. id, xpath, css, class
	 */
	public void clickByIndex(String elementIdentifier, String findBy,int index) {
		List<WebElement> elements = getElements(elementIdentifier, findBy);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout));
		
		Log.info("Clicking on respective element: " + elements);
		elements.get(index).click();
	}
	
	
	/**
	 * Find the elements and click on it
	 * 
	 * @param elementId
	 *            - element identifier value
	 * @param findBy
	 *            - element identifier type e.g. id, xpath, css, class
	 */

	/**
	 * Mouse Hover to element provided.
	 * 
	 * @param element
	 *            the WebElement on which to hover the mouse pointer.
	 */
	private void mouseHover(WebElement element) {
		waitForElementToBeDisplayed(element);
		Actions actions = new Actions(Appium.webDriver);
		actions.moveToElement(element);
		actions.perform();
		Log.info("Hovering on element: " + element);
	}

	/**
	 * Mouse Hover to element provided.
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 */
	public void mouseHover(String elementIdentifier, String findBy) {
		Log.info("Hovering on element: " + elementIdentifier);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout));
		WebElement element = getElement(elementIdentifier, findBy);
		Actions actions = new Actions(Appium.webDriver);
		actions.moveToElement(element);
		actions.perform();
		Log.info("Mouse hover on element: " + element);
	}
	
	
	/**
	 * Mouse Click to element provided
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 */
	public void mouseClick(String elementIdentifier, String findBy) {
		Log.info("Click on element: " + elementIdentifier);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout));
		WebElement element = getElement(elementIdentifier, findBy);
		Actions actions = new Actions(Appium.webDriver);
		actions.moveToElement(element);
		actions.click().build().perform();
		Log.info("Mouse hover on element: " + element);
	}
	

	/**
	 * Find the element and type the text in it
	 * 
	 * @param elementId
	 *            - element identifier value
	 * @param findBy
	 *            - element identifier type e.g. id, xpath, css, class
	 * @param text
	 *            - values to type
	 */
	public void sendKeys(String elementIdentifier, String findBy, String text) {
		Log.info("Getting localized data for keyword " + text);
		// text = Localization.getData("zh-cn", text);
		Log.info("Sending keys");
		WebElement element = getElement(elementIdentifier, findBy);
		//element.clear();
		element.sendKeys(text);
	}
	
	/**
	 * Find the elements with lists and place with position and type the text in it
	 * 
	 * @param elementId
	 *            - element identifier value
	 * @param findBy
	 *            - element identifier type e.g. id, xpath, css, class
	 * @param text
	 *            - values to type
	 */
	public void sendKeys(String elementIdentifier, String findBy, int position,String text) {
		Log.info("Getting localized data for keyword " + text);
		// text = Localization.getData("zh-cn", text);
		Log.info("Sending keys");
		List<WebElement> elements = getElements(elementIdentifier, findBy);
		//element.clear();
		elements.get(position).sendKeys(text);
	}

	/**
	 * Find the element and send control keys like Enter, Shift, Control.
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 * @param keyName
	 */
	public void sendControlKey(String elementIdentifier, String findBy,
			String keyName) {
		try {
			Keys key = Keys.valueOf(keyName);
			Log.info("Pressing the keyboard key: " + keyName);
			WebElement element = getElement(elementIdentifier, findBy);
			element.sendKeys(key);
		} catch (Exception exception) {
			Log.error("Error in pressing '" + keyName + "' key: "
					+ exception.getMessage());
		}
	}

	/**
	 * Find the element according to given identifier and identifier type
	 * 
	 * @param elementIdentifier
	 *            - element identifier value
	 * @param identifierType
	 *            - element identifier type e.g. id, xpath, css, class
	 * @return element
	 */
	private static WebElement getElement(String elementIdentifier, String findBy) {
		Log.info("Finding element by identifier " + elementIdentifier
				+ " using " + findBy.toString());
		WebElement element = null;

		switch (findBy.toLowerCase()) {
		case "name":
			element = Appium.webDriver.findElement(By.name(elementIdentifier));
			break;
		case "id":
			element = Appium.webDriver.findElement(By.id(elementIdentifier));
			break;
		case "xpath":
			element = Appium.webDriver.findElement(By
					.xpath(elementIdentifier));
			break;
		case "linktext":
			element = Appium.webDriver.findElement(By
					.linkText(elementIdentifier));
			break;
		case "cssselector":
			element = Appium.webDriver.findElement(By
					.cssSelector(elementIdentifier));
			break;
		case "tagname":
			element = Appium.webDriver.findElement(By
					.tagName(elementIdentifier));
			break;
		case "classname":
			element = Appium.webDriver.findElement(By
					.className(elementIdentifier));
			break;
		}
		return element;
	}

	
	/**
	 * Find the elements according to given identifier and identifier type
	 * 
	 * @param elementIdentifier
	 *            - element identifier value
	 * @param identifierType
	 *            - element identifier type e.g. id, xpath, css, class
	 * @return element
	 */
	private static List<WebElement> getElements(String elementIdentifier, String findBy) {
		Log.info("Finding element by identifier " + elementIdentifier
				+ " using " + findBy.toString());
		List<WebElement> element = null;

		switch (findBy.toLowerCase()) {
		case "name":
			element = Appium.webDriver.findElements(By.name(elementIdentifier));
			break;
		case "id":
			element = Appium.webDriver.findElements(By.id(elementIdentifier));
			break;
		case "xpath":
			element = Appium.webDriver.findElements(By
					.xpath(elementIdentifier));
			break;
		case "linktext":
			element = Appium.webDriver.findElements(By
					.linkText(elementIdentifier));
			break;
		case "cssselector":
			element = Appium.webDriver.findElements(By
					.cssSelector(elementIdentifier));
			break;
		case "tagname":
			element = Appium.webDriver.findElements(By
					.tagName(elementIdentifier));
			break;
		case "classname":
			element = Appium.webDriver.findElements(By
					.className(elementIdentifier));
			break;
		}
		return element;
	}
	
	/**
	 * Find a By which locates elements by the value of the given parameter
	 * 
	 * @param elementIdentifier
	 *            - element identifier value
	 * @param findBy
	 *            - element identifier type e.g. id, xpath, css, class
	 * @return byObject - the By object of the element provided
	 */
	private By getByObjectOfElement(String elementIdentifier, String findBy) {
		Log.info("Finding element by identifier " + elementIdentifier
				+ " using " + findBy.toString());
		By byObject = null;
		switch (findBy.toLowerCase()) {
		case "name":
			byObject = By.name(elementIdentifier);
			break;
		case "id":
			byObject = By.id(elementIdentifier);
			break;
		case "xpath":
			byObject = By.xpath(elementIdentifier);
			break;
		case "linktext":
			byObject = By.linkText(elementIdentifier);
			break;
		case "cssselector":
			byObject = By.cssSelector(elementIdentifier);
			break;
		case "tagname":
			byObject = By.tagName(elementIdentifier);
			break;
		case "classname":
			byObject = By.className(elementIdentifier);
			break;
		}
		return byObject;
	}

	/**
	 * Get the text of object/element
	 * 
	 * @param elementIdentifier
	 *            - element identifier value
	 * @param identifierType
	 *            - element identifier type e.g. id, xpath, css, class
	 * @return text value
	 */
	public String getText(String elementIdentifier, String findBy) {
		String result = "";
		wait(1);
		Log.info("Getting the text value...");
		WebElement element = getElement(elementIdentifier, findBy);

		result = element.getText();
		Log.info("The text value is:" + result.toString());
		return result;
	}
	
	
	/**
	 * Getting Elements Count
	 * 
	 * param elementIdentifier
	 *            - element identifier value
	 * @param identifierType
	 *            - element identifier type e.g. id, xpath, css, class
	 * @return counts
	 */
	
	public int getCountOfElements(String elementIdentifier, String findBy) {
		
		List<WebElement>  elements = getElements(elementIdentifier, findBy);
		int count = elements.size();
		return count;
		
	}
	
	
	/**
	 * Get the text of textbox or textfield
	 * 
	 * @param elementIdentifier
	 *            - element identifier value
	 * @param identifierType
	 *            - element identifier type e.g. id, xpath, css, class
	 * @return text value
	 */
	public String getValue(String elementIdentifier, String findBy) {
		String result = "";
		wait(1);
		Log.info("Getting the text value...");
		WebElement element = getElement(elementIdentifier, findBy);

		result = element.getAttribute("value");
		Log.info("The text value is:" + result.toString());
		return result;
	}
	
	
	/**
	 * Get the text of textbox or textfield
	 * 
	 * @param elementIdentifier
	 *            - element identifier value
	 * @param identifierType
	 *            - element identifier type e.g. id, xpath, css, class
	 * @return text value
	 */
	public String getValues(String elementIdentifier, String findBy) {
		Iterator<WebElement> results;
		String result = "";
		wait(1);
		Log.info("Getting the text values...");
		List<WebElement> elements = getElements(elementIdentifier, findBy);
        results = elements.iterator();
		
        while(results.hasNext()) {
            Object element = results.next().getText();
            result += element.toString() + ",";
         }
		
        return result;
	}
	
	
	/**
	 * Clear the value in a element 
	 *                  example:textbox
	 * @param elementIdentifier
	 *      - element identifier value
	 * @param identifierType
	 *      - element identifier type e.g. id, xpath, css, class
	 */
	
	 public void clearValue(String elementIdentifier, String findBy)  {
		 
		 WebElement element = getElement(elementIdentifier, findBy);
         element.clear();
		 Log.info("The Value is cleared in" + elementIdentifier + "element");
		 
	}
	
	
	
	
	/**
	 * Get the current window title
	 * 
	 * @return Name of the window
	 */
	public String getWindowTitle() {
		String message = "";
		try {
			Log.info("Getting the window title..." + message.toString());
			message = Appium.webDriver.getTitle();
			Log.info("Window title is: " + message.toString());
		} catch (Exception ex) {
			Log.error("Not able to get the window title");
		}
		return message;
	}

	/**
	 * Check whether the element is present or not identifier and identifier
	 * type
	 * 
	 * @param elementIdentifier
	 *            - element identifier value
	 * @param identifierType
	 *            - element identifier type e.g. id, xpath, css, class
	 * 
	 */
	public boolean isElementPresent(String elementIdentifier, String findBy) {
		boolean result = false;
		try {
			Log.info("Checking for element present: " + elementIdentifier);
			getElement(elementIdentifier, findBy);
			result = true;
		} catch (Exception exception) {
			Log.error("Element not found" + exception.getMessage());
		}
		return result;
	}
	
	/**
	 * Checking whether javascript disabled 
	 * 
	 */
	public static void disableJavascript() {
		
		FirefoxProfile p = new FirefoxProfile();
		p.setPreference("javascript.enabled", false);
		Appium.webDriver = new FirefoxDriver(p);
		
		
	}
	

	/**
	 * Check whether the alert is present or not
	 * 
	 */
	public boolean isAlertPresent() {
		boolean result = false;
		try {
			Log.info("Checking for alert...");
			Appium.webDriver.switchTo().alert();
			Log.info("Alert is present");
			result = true;
		} catch (Exception exception) {
			Log.error("Alert is not present" + exception.getMessage());
			result = false;
		}
		return result;
	}

	/**
	 * Accept the alert
	 * 
	 */
	public void acceptAlert() {
		
		Appium.wait(2);
		Log.info("Calling alert.accept");
		Alert alert = Appium.webDriver.switchTo().alert();
		alert.accept();
	    
	}
	
	
	/**
	 * Prompt Alert
	 * 
	 */
	public void promptAlert(String text) {
		
		Appium.wait(2);
		Log.info("Calling prompt alert.accept");
		Alert alert = Appium.webDriver.switchTo().alert();
		alert.sendKeys(text);
		alert.accept();
	}
	

	/**
	 * Dismiss the alert
	 * 
	 */
	public void dismissAlert() {
		
		Appium.wait(2);
		Log.info("Calling alert.dismiss");
		Alert alert = Appium.webDriver.switchTo().alert();
		alert.dismiss();
		Log.info("Alert is dismissed");
	}

	/**
	 * Wait for the specified seconds
	 * 
	 * @param interget
	 *            value
	 * @throws InterruptedException
	 */
	public static void wait(int i) {
		Log.info("Waiting for time: " + i + " seconds ");
		try {
			java.util.concurrent.TimeUnit.SECONDS.sleep(i);
		} catch (InterruptedException e) {
			Log.error(e.getMessage());
		}
	}

	/**
	 * Navigate back to last page
	 * 
	 */
	public void navigateBack() {
		try {
			Log.info("Navigating to back");
			Appium.webDriver.navigate().back();
			wait(2);
			Log.info("Navigated to back to last page");
		} catch (Exception exception) {
			Log.error("Failed to navigate back");
		}
	}

	/**
	 * Navigate forward to next page
	 * 
	 */
	public void navigateForward() {
		try {
			Log.info("Navigating forward to next page");
			Appium.webDriver.navigate().forward();
			wait(2);
			Log.info("Navigated forward to next page");
		} catch (Exception exception) {
			Log.error("Failed to navigate forward");
		}
	}

	/**
	 * Refresh the current page
	 * 
	 */
	public void refresh() {
		try {
			Log.info("Refreshing the current page");
			Appium.webDriver.navigate().refresh();
			wait(2);
			Log.info("refreshed the current page");
		} catch (Exception exception) {
			Log.error("Failed to refresh the page");
		}
	}

	/**
	 * Wait for Element to load
	 * 
	 * @param by
	 * @param timeOut
	 *            in seconds
	 * @return element
	 */
	public WebElement waitForElementToLoad(By by, int timeOut) {
		WebDriverWait wait = new WebDriverWait(Appium.webDriver, timeOut);
		Log.info("Waiting for " + timeOut + " seconds for element to present.");
		WebElement element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(by));
		Log.info("Waited for " + timeOut + " seconds for element to present.");
		return element;
	}

	/**
	 * Get the pop up message
	 * 
	 * @return pop up message
	 */
	public String getPopupMessage() {
		String message = null;
		Log.info("Getting the pop up message");
		Alert alert = Appium.webDriver.switchTo().alert();
		message = alert.getText();
		Log.info("Pop up message is: " + message.toString());
		return message;
	}

	/**
	 * Get the tool tip text for element
	 * 
	 * @param locator
	 *            e.g. xPath, Id etc.
	 * @return tool tip text
	 */
	public String getToolTipText(By locator) {
		Log.info("Getting the tooltip");
		String tooltip = Appium.webDriver.findElement(locator).getAttribute(
				"title");
		Log.info("Tooltip as: " + tooltip.toString());
		return tooltip;
	}

	/**
	 * Select provided value from drop down.
	 * 
	 * @param elementIdentifier
	 *            e.g value of identifiers xPath, Id, CSS etc.
	 * @param findBy
	 *            type of identifier like xPath, Id, CSS etc.
	 * @param value
	 *            drop down value to select.
	 * @return true/false
	 */
	public boolean selectDropdownValue(String elementIdentifier, String findBy,
			String value) {
		boolean result = false;
		try {
			Log.info("Selecting the dropdown element " + value);
			Select dropdown = new Select(getElement(elementIdentifier,
					findBy));
			dropdown.selectByValue(value);
			result = true;
		} catch (Exception exception) {
			Log.error("Error in selecting dropdown value "
					+ exception.getMessage());
		}
		return result;
	}

	/**
	 * Select provided value from drop down.
	 * 
	 * @param elementIdentifier
	 *            e.g value of identifiers xPath, Id, CSS etc.
	 * @param findBy
	 *            type of identifier like xPath, Id, CSS etc.
	 * @param index
	 *            index of element to select form drop down.
	 * @return true/false
	 */
	public boolean selectDropdownValueByIndex(String elementIdentifier,
			String findBy, String index) {
		boolean result = false;
		try {
			Log.info("Selecting the dropdown element by index " + index);
			Select dropdown = new Select(getElement(elementIdentifier,
					findBy));
			dropdown.selectByIndex(Integer.valueOf(index));
			result = true;
		} catch (Exception exception) {
			Log.error("Error in selecting dropdown value "
					+ exception.getMessage());
		}
		return result;
	}

	
	/**
	 * 
	 * Geting Current URL from Browser
	 * 
	 */
	
	 public String currentUrl() {
		 return Appium.webDriver.getCurrentUrl();
	 }
	
	
	
	
	/**
	 * Navigate to Url
	 * 
	 * @param Url
	 *            - Url to navigate to.
	 */
	public void navigateToUrl(String Url) {   
		try {
			Log.info("Navigating to Url" + Url);
			Appium.webDriver.navigate().to(Url);			
		} catch (Exception exception) {
			Log.error("Failed to navigate to Url" + exception.getMessage());
		}
	}

	/**
	 * Delete all cookies
	 * 
	 */
	public void deleteAllCookies() {
		try {
			Log.info("Deleting all cookies from browser");
			Appium.webDriver.manage().deleteAllCookies();
			wait(3);
		} catch (Exception exception) {
			Log.error("Failed to delete cookies" + exception.getMessage());
		}
	}

	/**
	 * Press keyboard key given in the parameter
	 * 
	 * @param Name
	 *            of the key for. e.g BACK_SPACE
	 */

	public void pressKey(String key) {
		try {
			Keys keyName = Keys.valueOf(key);
			Log.info("Pressing the keyboard key: " + key);
			Actions action = new Actions(Appium.webDriver);
			action.keyDown(keyName);

		} catch (Exception exception) {
			Log.error("Error in pressing the keyboard key " + key
					+ exception.getMessage());
		}
	}

	/**
	 * Closes the browser instance associated with web driver.
	 */
	public static void closeBrowser() {
		try {
			Appium.webDriver.close();
			Appium.webDriver.quit();
			Log.info("Closing browser.");
			wait(2);
			Appium.webDriver = null;
		} catch (Exception exception) {
			Log.error("Error in closing browser: " + exception.getMessage());
		}
	}
	
	public static void clearDriver() {
		Appium.webDriver = null;
	}

	/**
	 * Get table method finds table using attribute name & value.
	 * 
	 * @param attributeName
	 *            - name of the attribute
	 * @param attributeValue
	 *            - name of the value
	 */
	public void getTable(String attributeName, String attributeValue) {
		WebElement webElement = null;
		Log.info("Finding table with attribute name: " + attributeName
				+ " and attribute value " + attributeValue);
		List<WebElement> elements = webDriver.findElementsByTagName("table");
		Log.info("Total table elements found: " + elements.size());
		for (WebElement element : elements) {
			if (attributeValue.equalsIgnoreCase(element
					.getAttribute(attributeName))) {
				webElement = element;
				Log.info("Found table with attribute name: " + attributeName
						+ " and attribute value " + attributeValue);
				break;
			}
		}
		Appium.table = webElement;
	}

	/**
	 * Finds tables inside a WebElement container provided and then initializes
	 * the table as per the <code>tableNumber</code> that is provided as
	 * parameter.
	 * 
	 * @param containerElementIdentifier
	 *            - identifier of the WebElement containing single/multiple
	 *            tables.
	 * @param findBy
	 *            - By object on the basis of which the
	 *            <code>containerElementIdentifier</code> needs to be
	 *            identified.
	 * @param tableNumber
	 *            - table number to be acted upon (value should be from 1 and
	 *            onwards).
	 * @return
	 */
	public String getTableFromContainer(String containerElementIdentifier,
			String findBy, String tableNumber) {
		boolean isTableFound = true;
		WebElement webElement = getElement(containerElementIdentifier, findBy);
		List<WebElement> elements = webElement
				.findElements(By.tagName("table"));
		Log.info("Total table elements found : " + elements.size());
		if (elements.size() == 0) {
			Log.error("No table found inside the provided identifier.");
			Log.error("Identifier value : " + containerElementIdentifier
					+ "\n Indentifier type : " + findBy);
			isTableFound = false;
		}

		if (isTableFound) {
			int selectedTableNumber = Integer.parseInt(tableNumber);
			if (selectedTableNumber > 0
					&& selectedTableNumber <= elements.size()) {
				webElement = elements.get(selectedTableNumber - 1);
				Log.info("Table found. Initializing Table #"
						+ selectedTableNumber + " on current page.");
			} else {
				Log.error("Table number provided does not exist on the page. #"
						+ selectedTableNumber + ".");
				selectedTableNumber = 0;
				webElement = elements.get(selectedTableNumber);

			}
			Appium.table = webElement;
		}
		return Boolean.toString(isTableFound);

	}

	/**
	 * Get column count in row
	 * 
	 * @param rowIndex
	 *            - row number to get column count for.
	 * @return
	 */
	public int getTableColumnCountInRow(int rowIndex) {
		int columnCount = 0;
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		if (rows.size() > 0) {
			if (rowIndex == 0) {
				columnCount = rows.get(rowIndex).findElements(By.tagName("th"))
						.size();
			}
			if (columnCount == 0) {
				columnCount = rows.get(rowIndex).findElements(By.tagName("td"))
						.size();
			}
		}
		return columnCount;
	}

	/**
	 * Get values in the header of table.
	 * 
	 * @param separator
	 *            - value separator
	 * @return
	 */
	public String getHeaderValues(String separator) {
		String columnContent = "";
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		if (rows.size() > 0) {
			List<WebElement> cells = new ArrayList<WebElement>();
			cells = rows.get(0).findElements(By.tagName("th"));
			boolean firstTime = true;
			for (WebElement cell : cells) {
				if (firstTime) {
					columnContent = cell.getText();
					firstTime = false;
				} else {
					columnContent = columnContent + separator + cell.getText();
				}
			}
		}
		return columnContent;
	}

	/**
	 * Get values in row.
	 * 
	 * @param rowIndex
	 *            - row number to get values in.
	 * @param separator
	 *            - value separator
	 * @return
	 */
	public String getTableColumnValuesInRow(int rowIndex, String separator) {
		String columnContent = "";
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		if (rows.size() > 0) {
			List<WebElement> cells = rows.get(rowIndex).findElements(
					By.tagName("td"));
			boolean firstTime = true;
			for (WebElement cell : cells) {
				if (firstTime) {
					columnContent = getTableCellContent(cell);
					firstTime = false;
				} else {
					columnContent = columnContent + separator
							+ getTableCellContent(cell);
				}
			}
		}
		return columnContent;
	}

	/**
	 * Get cell value in particular row & column
	 * 
	 * @param rowIndex
	 *            - row number to get values in.
	 * @param columnIndex
	 *            - column number to get values in.
	 * @return cell value as string
	 */
	public String getTableCellValue(String row, String col) {
		int rowIndex = Integer.parseInt(row);
		int columnIndex = Integer.parseInt(col);
		String cellContents = "";
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		if (rows.size() > 0) {
			List<WebElement> cells = rows.get(rowIndex).findElements(
					By.tagName("td"));
			cellContents = getTableCellContent(cells.get(columnIndex));
		}
		return cellContents;
	}

	/**
	 * Waits for <code>textValue</code> to be present in the specified row &
	 * column of the initialized static table. Pre-requisites:
	 * <code>getTable</code> or <code>getTableFromContainer</code> functions to
	 * be invoked.
	 * 
	 * @param rowIndex
	 *            - row number to get values in.
	 * @param columnIndex
	 *            - column number to get values in.
	 * @return cell value as string
	 */
	public String waitForTextToBePresentInTableCell(String row, String col,
			String textValue, String timeOut) {

		int rowIndex = Integer.parseInt(row);
		int columnIndex = Integer.parseInt(col);
		String cellContents = "";
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		if (rows.size() > 0) {
			List<WebElement> cells = rows.get(rowIndex).findElements(
					By.tagName("td"));
			waitForTextToBePresentInElement(cells.get(columnIndex), textValue,
					Integer.parseInt(timeOut));
			cellContents = getTableCellContent(cells.get(columnIndex));
		}
		return cellContents;
	}

	/**
	 * Get table contents. Pre-requisites: <code>getTable</code> or
	 * <code>getTableFromContainer</code> functions to be invoked.
	 * 
	 * @param seperator
	 *            - value separator for column values in row while one row would
	 *            be added as new line.
	 * @return
	 */
	public String getTableContents(String seperator) {
		String tableContents = "";
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		boolean firstTime = true;
		if (rows.size() > 0) {
			for (WebElement row : rows) {
				List<WebElement> cells = row.findElements(By.tagName("td"));
				for (WebElement cell : cells) {
					if (firstTime) {
						tableContents = getTableCellContent(cell);
						firstTime = false;
					} else {
						tableContents = tableContents + seperator
								+ getTableCellContent(cell);
					}
				}
				tableContents = tableContents + NEW_LINE;
			}
		}
		return tableContents;
	}

	/**
	 * Get cell content as string
	 * 
	 * @param cell
	 *            - cell element.
	 * @return cell content as String
	 */
	private String getTableCellContent(WebElement cell) {
		return cell.getText();
		// String cellContent = "";
		// List<WebElement> elements = cell.findElements(By.xpath(".//*"));
		// for(WebElement child : elements)
		// {
		// cellContent = cellContent + child.getText() + " ";
		// }
		// return cellContent.trim();
	}

	/**
	 * Search for the given tag in the given element and return the first match
	 * 
	 * @param parentWebElement
	 * @param tag
	 * @return
	 */
	public WebElement getSubElementWithTag(WebElement parentWebElement,
			String tag) {
		List<WebElement> children = parentWebElement.findElements(By
				.tagName(tag));
		if (children.size() == 0) {
			Log.error("Tag[" + tag + "]not found in parentWebElement");
			return null;
		}
		return children.get(0);
	}

	/**
	 * Returns the page title
	 * 
	 * @return
	 */
	public String getPageTitle() {
		String pageTitle = Appium.webDriver.getTitle();
		Log.info("Page title is :[" + pageTitle + "]");
		return pageTitle;
	}

	/**
	 * Waits for the element to be displayed on the UI
	 * 
	 * @param element
	 */
	private void waitForElementToBeDisplayed(WebElement element) {
		WebDriverWait wait = new WebDriverWait(webDriver, timeout);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * Waits for the element to be displayed on the UI.
	 * 
	 * @param elementIdentifier
	 *            - identifier of the WebElement
	 * @param findBy
	 *            - By object on the basis of which the
	 *            <code>elementIdentifier</code> needs to be identified.
	 * @param timeOut
	 *            - in seconds
	 */
	public void waitForElementToBeDisplayed(String elementIdentifier,
			String findBy, String timeOut) {
		Log.info("Waiting for element [" + elementIdentifier
				+ "] to appear for " + timeOut + " seconds.");
		WebDriverWait wait = new WebDriverWait(webDriver,
				Integer.parseInt(timeOut));
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(getByObjectOfElement(
						elementIdentifier, findBy)));
		wait(2);
	}

	/**
	 * Waits for the element to be visible and enabled on the UI.
	 * 
	 * @param elementIdentifier
	 *            - identifier of the WebElement
	 * @param findBy
	 *            - By object on the basis of which the
	 *            <code>elementIdentifier</code> needs to be identified.
	 * @param timeOut
	 *            - in seconds
	 */
	public void waitForElementToBeEnabled(String elementIdentifier,
			String findBy, String timeOut) {
		waitForElementToBeClickable(elementIdentifier, findBy, timeOut);
	}

	/**
	 * Waits for the element to be visible and enabled on the UI.
	 * 
	 * @param elementIdentifier
	 *            - identifier of the WebElement
	 * @param findBy
	 *            - By object on the basis of which the
	 *            <code>elementIdentifier</code> needs to be identified.
	 * @param timeOut
	 *            - in seconds
	 */
	public void waitForElementToBeClickable(String elementIdentifier,
			String findBy, String timeOut) {
		Log.info("Waiting for element [" + elementIdentifier
				+ "] to be displayed for " + timeOut + " seconds.");
		WebElement webElement = getElement(elementIdentifier, findBy);
		WebDriverWait wait = new WebDriverWait(webDriver,
				Integer.parseInt(timeOut));
		wait.until(ExpectedConditions.elementToBeClickable(webElement));
	}

	/**
	 * Waits for the element to disappear from the UI.
	 * 
	 * @param elementIdentifier
	 *            - identifier of the WebElement
	 * @param findBy
	 *            - By object on the basis of which the
	 *            <code>elementIdentifier</code> needs to be identified.
	 * @param timeOut
	 *            - in seconds
	 */
	public void waitForElementToDisappear(String elementIdentifier,
			String findBy, String timeOut) {
		Log.info("Waiting for element [" + elementIdentifier
				+ "] to disappear for " + timeOut + " seconds.");
		WebDriverWait wait = new WebDriverWait(webDriver,
				Integer.parseInt(timeOut));
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(getByObjectOfElement(
						elementIdentifier, findBy)));
	}

	/**
	 * Waits for the element to be visible and enabled on the UI.
	 * 
	 * @param elementIdentifier
	 *            - identifier of the WebElement
	 * @param findBy
	 *            - By object on the basis of which the
	 *            <code>elementIdentifier</code> needs to be identified
	 * @param textValue
	 *            - text to be populated in the element
	 * @param timeOut
	 *            - in seconds
	 * @return true/false
	 */
	public String waitForTextToBePresentInElement(String elementIdentifier,
			String findBy, String textValue, String timeOut) {
		Log.info("Waiting for element [" + elementIdentifier
				+ "] to populate '" + textValue + "' value be displayed for "
				+ timeOut + " seconds.");

		WebDriverWait wait = new WebDriverWait(webDriver,
				Integer.parseInt(timeOut));
		Boolean isTextFound = wait.until(ExpectedConditions
				.textToBePresentInElementLocated(
						getByObjectOfElement(elementIdentifier, findBy),
						textValue));
		return isTextFound.toString();
	}

	/**
	 * Waits for the given text to be present in the identified element.
	 * 
	 * @param element
	 *            - identified WebElement
	 * @param textValue
	 *            - text to be populated in the element
	 * @param timeOut
	 *            - in seconds
	 * @return
	 */
	private boolean waitForTextToBePresentInElement(WebElement element,
			String textValue, int timeOut) {
		WebDriverWait wait = new WebDriverWait(webDriver, timeOut);
		return wait.until(ExpectedConditions.textToBePresentInElementValue(
				element, textValue));
	}

	/**
	 * Get chart by Title.
	 * 
	 * @param chartTitle
	 *            - title of chart to search by.
	 */
	public void getChart(String chartTitle) {
		WebElement webElement = null;
		Log.info("Finding high chart with title: " + chartTitle);
		List<WebElement> elements = webDriver
				.findElementsByCssSelector(".panel,.panel-default");
		Log.info("Total elements found: " + elements.size());
		for (WebElement element : elements) {
			List<WebElement> headingElements = element.findElements(By
					.className("panel-heading"));
			if (headingElements.size() > 0) {
				if (headingElements.get(0).getText()
						.equalsIgnoreCase(chartTitle)) {
					Log.info("Found high chart with title: " + chartTitle);
					webElement = element;
					break;
				}
			}
		}
		highChart = webElement;
	}

	/**
	 * Click on Edit button for the chart.
	 */
	public void editChart() {
		Log.info("Getting pencil element by CCS selector: fa-pencil");
		List<WebElement> links = highChart.findElements(By.tagName("a"));
		for (WebElement link : links) {
			WebElement editIcon = link.findElement(By.className("fa-pencil"));
			if (null != editIcon) {
				link.click();
				break;
			}
		}
	}

	/**
	 * Get legend values separated with given separator.
	 * 
	 * @param separator
	 *            - legend value separator.
	 * @return legend values
	 */
	public String getLegendValues(String separator) {
		String legendValues = "";
		List<WebElement> legends = highChart.findElements(By
				.className("highcharts-legend-item"));
		boolean firstTime = true;
		for (WebElement legend : legends) {
			if (firstTime) {
				legendValues = legend.getText();
				firstTime = false;
			} else {
				legendValues = legendValues + separator + legend.getText();
			}
		}
		return legendValues;
	}

	/**
	 * Get legend color for provided legend.
	 * 
	 * @param legendText
	 *            - legend text to search for.
	 * @param tagName
	 *            - tag name, this is used to identify the chart type.
	 * @return
	 */
	private String getLegendColor(String legendText, String tagName) {
		String legendColor = "";
		List<WebElement> legends = highChart.findElements(By
				.className("highcharts-legend-item"));
		for (WebElement legend : legends) {
			WebElement legendTextElement = legend.findElement(By
					.tagName("text"));
			if (legendTextElement.getText().equalsIgnoreCase(legendText)) {
				List<WebElement> webElements = legend.findElements(By
						.tagName(tagName));
				for (WebElement webElement : webElements) {
					String value = webElement.getAttribute("fill");
					if (!value.equalsIgnoreCase("none")) {
						legendColor = value;
						break;
					}
				}
				if (!legendColor.isEmpty()) {
					break;
				}
			}
		}
		return legendColor;
	}

	/**
	 * Get series data for provided legend with values separated by given
	 * separator.
	 * 
	 * @param legend
	 *            - legend name
	 * @param chartType
	 *            - chart type
	 * @param separator
	 *            - value separator
	 * @return series values
	 */
	public String getSeriesDataForLegend(String legend, String chartType,
			String separator) {
		String seriesData = "";
		WebElement seriesGroup = highChart.findElement(By
				.className("highcharts-series-group"));
		String tagName = "";
		String className = "";
		switch (chartType) {
		case "line":
			className = "highcharts-markers";
			tagName = "path";
			break;
		}
		String legendColor = getLegendColor(legend, tagName);
		List<WebElement> seriesList = seriesGroup.findElements(By
				.className(className));
		WebElement seriesParent = null;
		for (WebElement series : seriesList) {
			WebElement child = series.findElements(By.tagName(tagName)).get(0);
			String value = child.getAttribute("fill");
			if (value.equalsIgnoreCase(legendColor)) {
				seriesParent = series;
				break;
			}
		}

		List<WebElement> pointers = new ArrayList<WebElement>();
		if (null != seriesParent) {
			pointers = seriesParent.findElements(By.tagName(tagName));
			boolean firstTime = true;
			for (WebElement pointer : pointers) {
				// Mouse hover
				mouseHover(pointer);
				WebElement toolTip = highChart.findElement(By
						.className("highcharts-tooltip"));
				WebElement textElement = toolTip
						.findElement(By.tagName("text"));
				if (firstTime) {
					seriesData = textElement.getText();
					firstTime = false;
				} else {
					seriesData = seriesData + separator + textElement.getText();
				}
			}
		}
		return seriesData;
	}

	/**
	 * Get X Axis title for the chart
	 * 
	 * @return - X Axis title
	 */
	public String getXAxisTitle() {
		WebElement axis = highChart.findElement(By
				.className(" highcharts-xaxis-title"));
		return axis.getText();
	}

	/**
	 * Get Y Axis title for the chart
	 * 
	 * @return - Y Axis title
	 */
	public String getYAxisTitle() {
		WebElement axis = highChart.findElement(By
				.className(" highcharts-yaxis-title"));
		return axis.getText();
	}

	/**
	 * Take screenshot of the page & saves it to specified location
	 * 
	 * @param fileName
	 *            - file path to save screenshot.
	 * @throws IOException
	 */
	public static void takeScreenshot(String fileName) throws IOException {
		// take the screenshot at the end of every test
		File scrFile = ((TakesScreenshot) webDriver)
				.getScreenshotAs(OutputType.FILE);
		// now save the screenshot to a file some place
		FileUtils.copyFile(scrFile, new File(fileName + ".png"));
		Log.info("Saving screenshot to: " + fileName);
	}

	/**
	 * Get all page elements & store them as key & value pair in hash map. This
	 * is used in localization class.
	 * 
	 * @return key value pair for element & its value.
	 */
	public static HashMap<String, String> getAllPageElementsLable(
			List<String> pageElements) {
		HashMap<String, String> elementLables = new HashMap<String, String>();
		int i = -1;
		do {
			if (i != -1) {
				WebElement element = getElement(pageElements.get(i), "xpath");
				element.click();
			}
			List<WebElement> webElements = webDriver.findElements(By
					.xpath(".//*"));
			for (WebElement webElement : webElements) {
				String value = "";
				String elementType = webElement.getTagName();
				String xPath = getAbsoluteXPath(webElement);
				switch (elementType.toLowerCase()) {
				case "span":
				case "button":
				case "p":
				case "th":
				case "a":
				case "h1":
				case "h2":
				case "h3":
				case "h4":
				case "h5":
				case "h6":
				case "i":
				case "lable":
				case "item":
				case "select":
					value = webElement.getText();
					break;
				case "input":
					value = webElement.getAttribute("placeholder");
					break;
				}
				if (!value.equalsIgnoreCase("")) {
					Log.info("Adding element " + xPath);
					Log.info("Adding element value " + value);
					elementLables.put(xPath, value);
				}
			}
			i++;
		} while (i < pageElements.size());
		Log.info("No of elements found: " + elementLables.size());
		return elementLables;
	}

	/**
	 * Get all page elements & store them as key & value pair in hash map. This
	 * is used in localization class.
	 * 
	 * @return key value pair for element & its value.
	 */
	private Map<String, WebElement> getAllPageElementsForTabOrder() {
		Map<String, WebElement> elements = new LinkedHashMap<String, WebElement>();
		List<WebElement> webElements = webDriver.findElements(By.xpath(".//*"));
		for (WebElement webElement : webElements) {
			String elementType = webElement.getTagName();
			String xPath = getAbsoluteXPath(webElement);
			if (elementType.toLowerCase().equals("input")
					|| elementType.toLowerCase().equals("button")
					|| elementType.toLowerCase().equals("a")
					|| elementType.toLowerCase().equals("menu")
					|| elementType.toLowerCase().equals("menuitem")
					|| elementType.toLowerCase().equals("select")) {
				Log.info("Adding element " + xPath);
				elements.put(xPath, webElement);
			}
		}
		Log.info("No of elements found: " + elements.size());
		return elements;
	}

	/**
	 * Validates tab order on given page, it should be left to right & top to
	 * down.
	 * 
	 * @return blank if tab order is correct otherwise returns the xPath for
	 *         element with invalid tab order.
	 */
	public String validateTabOrder() {
		String elementsNotHavingCorrectTabOrder = "";
		Map<String, WebElement> elements = getAllPageElementsForTabOrder();
		Iterator<java.util.Map.Entry<String, WebElement>> entries = elements
				.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, WebElement> entry = entries.next();
			WebElement webElement = entry.getValue();
			String xPath = entry.getKey().toString();
			if (!webElement.equals(webDriver.switchTo().activeElement())) {
				Log.error("Invalid tab order for element, xPath: " + xPath);
				elementsNotHavingCorrectTabOrder = elementsNotHavingCorrectTabOrder
						+ "," + xPath;
				continue;
			}
			webElement.sendKeys(Keys.TAB);
		}
		return elementsNotHavingCorrectTabOrder;
	}

	/**
	 * Get the xPath for provided web element
	 * 
	 * @param webElement
	 *            - web element to get xPath for.
	 * @return - xPath for web element
	 */
	@SuppressWarnings("unused")
	private static String getXPath(WebElement webElement) {
		String jscript = "function getPathTo(node) {" + "  var stack = [];"
				+ "  while(node.parentNode !== null) {"
				+ "    stack.unshift(node.tagName);"
				+ "    node = node.parentNode;" + "  }"
				+ "  return stack.join('/');" + "}"
				+ "return getPathTo(arguments[0]);";
		return (String) webDriver.executeScript(jscript, webElement);
	}

	/**
	 * Get the xPath for provided web element, this is not used any where.
	 * 
	 * @param webElement
	 *            - web element to get xPath for.
	 * @return - xPath for web element
	 */
	private static String getAbsoluteXPath(WebElement element) {
		return (String) ((JavascriptExecutor) webDriver)
				.executeScript(
						"function absoluteXPath(element) {"
								+ "var comp, comps = [];"
								+ "var parent = null;"
								+ "var xpath = '';"
								+ "var getPos = function(element) {"
								+ "var position = 1, curNode;"
								+ "if (element.nodeType == Node.ATTRIBUTE_NODE) {"
								+ "return null;"
								+ "}"
								+ "for (curNode = element.previousSibling; curNode; curNode = curNode.previousSibling) {"
								+ "if (curNode.nodeName == element.nodeName) {"
								+ "++position;"
								+ "}"
								+ "}"
								+ "return position;"
								+ "};"
								+

								"if (element instanceof Document) {"
								+ "return '/';"
								+ "}"
								+

								"for (; element && !(element instanceof Document); element = element.nodeType == Node.ATTRIBUTE_NODE ? element.ownerElement : element.parentNode) {"
								+ "comp = comps[comps.length] = {};"
								+ "switch (element.nodeType) {"
								+ "case Node.TEXT_NODE:"
								+ "comp.name = 'text()';" + "break;"
								+ "case Node.ATTRIBUTE_NODE:"
								+ "comp.name = '@' + element.nodeName;"
								+ "break;"
								+ "case Node.PROCESSING_INSTRUCTION_NODE:"
								+ "comp.name = 'processing-instruction()';"
								+ "break;" + "case Node.COMMENT_NODE:"
								+ "comp.name = 'comment()';" + "break;"
								+ "case Node.ELEMENT_NODE:"
								+ "comp.name = element.nodeName;" + "break;"
								+ "}" + "comp.position = getPos(element);"
								+ "}" +

								"for (var i = comps.length - 1; i >= 0; i--) {"
								+ "comp = comps[i];"
								+ "xpath += '/' + comp.name.toLowerCase();"
								+ "if (comp.position !== null) {"
								+ "xpath += '[' + comp.position + ']';" + "}"
								+ "}" +

								"return xpath;" +

								"} return absoluteXPath(arguments[0]);",
						element);
	}

	/**
	 * Click an element in the specified cell in the table
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @param tagName
	 *            - the tag of the child node of the cell that is to be clicked
	 */
	public void clickElementInTableCell(String rowIndex, String columnIndex,
			String tagName) {
		WebElement targetElement = findElementInTableCell(rowIndex,
				columnIndex, tagName);
		targetElement.click();
	}

	/**
	 * Click an element as per the xpath provided in the specified cell in the
	 * table
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @param elementXpath
	 *            - the 'relative xpath' of the child node of the cell that is
	 *            to be clicked (Note: not to include '//' while specifying the
	 *            relative xpath td node of the cell).
	 */
	public void clickElementInTableCellByXpath(String rowIndex,
			String columnIndex, String elementXpath) {
		WebElement targetElement = findElementInTableCellByXpath(rowIndex,
				columnIndex, elementXpath);
		targetElement.click();
	}

	/**
	 * Find element in table cell by Xpath
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @param tagName
	 * @return
	 */
	private WebElement findElementInTableCell(String rowIndex,
			String columnIndex, String tagName) {
		int rowIndexInt = Integer.parseInt(rowIndex);
		int colIndexInt = Integer.parseInt(columnIndex);
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		WebElement targetTow = rows.get(rowIndexInt);
		List<WebElement> cells = targetTow.findElements(By.tagName("td"));
		WebElement targetCell = cells.get(colIndexInt);
		WebElement targetElement = targetCell.findElement(By.tagName(tagName));
		// Loop to identify all the cell values in the table
		// Log.info("findElementInTableCell" + rows.size());
		// for (int i = 0; i < rows.size(); i++) {
		// List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
		// for (int j = 0; j < cells.size(); j++) {
		// Log.info("Row :: " + i + ", Column :: " + j + ", Data :: "
		// + cells.get(j).getText());
		// }
		// }

		return targetElement;
	}

	/**
	 * Find element in table cell by Xpath
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @param tagName
	 * @return
	 */
	private WebElement findElementInTableCellByXpath(String rowIndex,
			String columnIndex, String xPath) {
		int rowIndexInt = Integer.parseInt(rowIndex);
		int colIndexInt = Integer.parseInt(columnIndex);
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		WebElement targetRow = rows.get(rowIndexInt);
		List<WebElement> cells = targetRow.findElements(By.tagName("td"));
		WebElement targetCell = cells.get(colIndexInt);
		WebElement targetElement = targetCell.findElement(By.xpath(xPath));
		return targetElement;
	}

	/**
	 * Search each row with the given value in given column. Returns index of
	 * row where the search is successful.
	 * 
	 * @param columnIndex
	 *            - value starting from 0.
	 * @param valueToSearch
	 * @return successful search means the return value would be greater than or
	 *         equal to zero, and unsuccessful search would return '-1'.
	 */
	public String getTableRowIndexByCellValue(String columnIndex,
			String valueToSearch) {
		int colIndexInt = Integer.parseInt(columnIndex);
		WebElement tagBody = Appium.table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tagBody.findElements(By.tagName("tr"));
		int rowIndex = -1;
		for (WebElement row : rows) {
			rowIndex++;
			List<WebElement> cells = row.findElements(By.tagName("td"));
			WebElement cellInColumn = cells.get(colIndexInt);
			String actualText = cellInColumn.getText();
			if (valueToSearch.equals(actualText)) {
				return String.valueOf(rowIndex);
			}
		}
		return "-1";
	}

	/**
	 * Returns contents of list
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 * @param seperator
	 * @return
	 */
	public String getListContentsByIdentifier(String elementIdentifier,
			String findBy, String seperator) {
		String listContents = "";
		WebElement list = getElement(elementIdentifier, findBy);
		List<WebElement> listElements = list.findElements(By.tagName("li"));
		boolean firstTime = true;
		for (WebElement listElement : listElements) {
			if (firstTime) {
				listContents = listElement.getText();
				firstTime = false;
			} else {
				listContents = listContents + seperator + listElement.getText();
			}
		}
		return listContents;
	}

	/**
	 * Verify if element is displayed
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 * @return
	 */
	public String isElementDisplayed(String elementIdentifier, String findBy) {
		String result = "fail";
		WebElement element = getElement(elementIdentifier, findBy);
		boolean displayed = element.isDisplayed();
		if (displayed) {
			result = "pass";
		}
		return result;
	}

	/**
	 * Verify if element is enabled
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 * @return
	 */
	public String isElementEnabled(String elementIdentifier, String findBy) {
		String result = "fail";
		WebElement element = getElement(elementIdentifier, findBy);
		boolean enabled = element.isEnabled();
		if (enabled) {
			result = "pass";
		}
		return result;
	}

	/**
	 * Verify if element is Selected
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 * @return
	 */
	public String isElementSelected(String elementIdentifier, String findBy) {
		String result = "fail";
		WebElement element = getElement(elementIdentifier, findBy);
		boolean selected = element.isSelected();
		if (selected) {
			result = "pass";
		}
		return result;
	}

	/**
	 * Returns row count of the table
	 * 
	 * @return
	 */
	public String getTableRowCount() {
		WebElement tableBody = Appium.table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		int rowCount = rows.size();
		return Integer.toString(rowCount);
	}

	/**
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 * @param tagNameForCount
	 * @return
	 */
	public String getTagCountInElement(String elementIdentifier, String findBy,
			String tagNameForCount) {
		WebElement element = getElement(elementIdentifier, findBy);
		List<WebElement> tagList = element.findElements(By
				.tagName(tagNameForCount));
		int tagCount = tagList.size();
		return Integer.toString(tagCount);
	}

	/**
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 * @param tagNameForCount
	 * @return
	 */
	public String getTagCountInTdElement(String rowIndex, String columnIndex,
			String tagNameForCount) {

		int rowIndexInt = Integer.parseInt(rowIndex);
		int colIndexInt = Integer.parseInt(columnIndex);
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		WebElement targetTow = rows.get(rowIndexInt);
		List<WebElement> cells = targetTow.findElements(By.tagName("td"));
		WebElement targetCell = cells.get(colIndexInt);
		int tagCountInTd = targetCell.findElements(By.tagName(tagNameForCount))
				.size();
		return Integer.toString(tagCountInTd);

	}

	/**
	 * Get column contents from table
	 * 
	 * @param index
	 * @param seperator
	 * @return
	 */
	public String getTableColumnContents(String index, String seperator) {
		int columnIndex = Integer.parseInt(index);
		String tableContents = "";
		List<WebElement> rows = Appium.table.findElements(By.tagName("tr"));
		boolean firstTime = true;
		if (rows.size() > 0) {
			for (WebElement row : rows) {
				List<WebElement> cells = row.findElements(By.tagName("td"));
				int cellIndex = -1;
				for (WebElement cell : cells) {
					cellIndex++;
					if (cellIndex != columnIndex) {
						continue;
					}
					if (firstTime) {
						tableContents = getTableCellContent(cell);
						firstTime = false;
					} else {
						tableContents = tableContents
								+ getTableCellContent(cell);
					}
				}
				tableContents = tableContents + seperator;
			}
		}
		return tableContents;
	}

	/**
	 * Select new browser tab *
	 * 
	 * @param tabIndex
	 */
	public static void selectBrowserTab(String tabIndex) {
		Set<String> winHandlesList = Appium.webDriver.getWindowHandles();
		int i = 1;
		for (String winHandle : winHandlesList) {
			if (i == Integer.parseInt(tabIndex)) {
				Appium.webDriver.switchTo().window(winHandle);
				Log.info("Switched to window index #" + i
						+ ". Window handle :: [" + winHandle + "].");
				Log.info("Switched window title :: "
						+ Appium.webDriver.getTitle());
				break;
			}
			i++;
		}
	}

	/**
	 * This method returns attribute values for given attribute of given element
	 * *
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 * @param attributeName
	 * @return
	 */
	public String getElementAttribute(String elementIdentifier, String findBy,
			String attributeName) {
		WebElement element = getElement(elementIdentifier, findBy);
		String attributeValue = element.getAttribute(attributeName);
		Log.info("Attribute[" + attributeName + "] of element["
				+ elementIdentifier + "] has value [" + attributeValue + "]");
		return attributeValue;
	}
	
	
	/**
	 *  Working with frames
	 *  This method returns frame based on framelist 
	 *  @param elementIdentifier
	 *  @param findBy
	 *  @return 
	 *  
	 */
	 public void switchToFrame(String elementIdentifier, String findBy) {
		 WebElement element = getElement(elementIdentifier, findBy);
		 Appium.webDriver.switchTo().frame(element);
	 }
	
	
	/**
	 * This method will enable element based on action
	 **
	 * @param elementIdentifier
	 * @param findBy
	 * @param action
	 */
	
	public void changeElementView(String elementIdentifier, String findBy,
			String action) {
    	
    	WebElement element = getElement(elementIdentifier, findBy);
    	JavascriptExecutor executor= (JavascriptExecutor) Appium.webDriver;
    	String js = null;
    	
    	switch (action) {
			case "block":
				 js = "arguments[0].style.display='block';";
				 executor.executeScript(js,element);
				 Log.info("block style has been executed successfully");
				break;
			case "visible":
				 js = "arguments[0].style.visibility='visible';";
				 executor.executeScript(js,element);
				 Log.info("visible style has been executed successfully");
				break;
			default:
	        	 js = "arguments[0].style.display='none';";
	        	 executor.executeScript(js,element);
	        	 Log.info("None style has been executed successfully");
				 break;
		} 
    	
     }
    
    
    /**
     * Changing class name for an element  
     **
     * @param elementIdentifier
     * @param findBy
     * @param className
     */
    
    
      public void changeClassName(String elementIdentifier, String findBy,
  			String className) {
    	  
    		WebElement element = getElement(elementIdentifier, findBy);
        	JavascriptExecutor executor= (JavascriptExecutor) Appium.webDriver;
        	String js = null;
        	js = "arguments[0].className='"+className+"'";
			executor.executeScript(js,element);
			Log.info("Class name has been changed successfully");
    	  
      }
      
      /**
       * Trigger javaScript action to view HTML code
       * @param findBy
       * @param element
       * @return InnerHtmlcode
       */
       
      public String innerHtmlCode(String findBy,String element) {
    	  
    	 JavascriptExecutor executor= (JavascriptExecutor) Appium.webDriver;
    	 String htmlCode;

    	 
    	 switch (findBy) {
			case "classname":
				 htmlCode =(String) executor.executeScript("return document.getElementsByClassName"+"('"+element+"')[0].innerHTML");
				 Log.info("extract HTML code By id "+ findBy);
				break;  		
			default:
				 htmlCode =(String) executor.executeScript("return document.getElementById"+"('"+element+"').innerHTML");
				 Log.info("extract HTML code By id "+ findBy);
				 break;
       	}
    	 
    	 		return htmlCode;
    	  
    	}
      
      /**
       * Submiting form using javascript action
       * @param action
       */
      
      public void formSubmit(String action) {
    	  
    	  JavascriptExecutor executor= (JavascriptExecutor) Appium.webDriver;
     	  executor.executeScript("return document.forms."+action+".submit();");
     	
      }
      

      
      /**
  	 * Initializes Mobile the Selenium object <code>webDriver</code> fro Native Web 
  	 * 
  	 * @param platform
  	 *            the possible values are 'anriod', 'ios'.
  	 *            In case this value is set anything apart from these values or
  	 *            blank, the default browser set is ios.
  	          
  	 *@param devictype
  	 *             the possiable values are emulator or real device            
  	 *            
  	 * @throws Exception
  	 */
  	
  	public static void initialize (String platform,String appPath ,String deviceType)
  			throws Exception {
  		
  		if (Appium.webDriver != null) { return; 	}
  		
  		
  	switch (platform.toLowerCase()) {
  		case "android":
  			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceType);
  			capabilities.setCapability(MobileCapabilityType.APP, appPath);
  			capabilities.setCapability("autoLaunch", false);
  			capabilities.setCapability("newCommandTimeout", 60 * 500);
  			remoteUrl = new URL(url);
  			Appium.androidDriver = new AndroidDriver (remoteUrl, capabilities);
  			Appium.webDriver     = Appium.androidDriver;
  			Appium.mobileDriver  = Appium.androidDriver;
  			break;
  		default:
  			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceType);
  			remoteUrl = new URL(url);
  			Appium.iosDriver    = new IOSDriver (remoteUrl, capabilities);
  			Appium.webDriver    = Appium.iosDriver;
  			Appium.mobileDriver = Appium.iosDriver;
  			break;
  		}
  		
  			
  	     
  	}
  	   
      
   /**
    * 
    * Launching Application
    * 
    */
      
     public void launchApplication() {
    	 Appium.mobileDriver.launchApp();
    }
     
     /**
      * Switching from native to web or vice versa
      * 
      */
     
	@SuppressWarnings("unchecked")
	public static Set<String>  appView() {
    	 
    	Set<String> contextNames = Appium.mobileDriver.getContextHandles();
    	return contextNames;
    	 
     }
     
      
    /**
     * Scroll  and click the element by specfifing the text
     * @param text
     */
      
   /*   public void scrollToClick(String text) {
  		Selenium.mobileDriver.scrollTo(text).click();
  	  }
    */  
      
      /**
       * Swipe Function
             @param elementIdentifier
	   *            - element identifier value
	   *      @param findBy
	   *            - element identifier type e.g. id, xpath, css, class 
	   *      @param direction
	   *            - direction to perform e.g up,down,right,left. 
       */
      
      public void swipeScreen(String elementIdentifier,String findBy,String direction) {
    	  
    	MobileElement element = (MobileElement) getElement(elementIdentifier, findBy);
  		waitForElementToBeDisplayed(elementIdentifier, findBy,
  				String.valueOf(timeout));
  		Log.info("Swiping the screen on element: " + element);
  		
  		
	  		switch (direction.toLowerCase()) {
				case "up":
					element.swipe(SwipeElementDirection.UP,2000);
					break; 
					
				case "down":
					element.swipe(SwipeElementDirection.DOWN,2000);
				    break; 
				    
				case "left":
					element.swipe(SwipeElementDirection.LEFT,2000);
					break; 
					
				case "right":
					element.swipe(SwipeElementDirection.RIGHT,2000);
					 break;
	    	}
    	  
      }
      
      /**
       * 
       * @param elementIdentifier
       * @param findBy
       * @param direction
       * @param cycle
       */
      
      public static void swipeMultipleTimes(String elementIdentifier,String findBy,String direction,int cycle){
    	  
    	  MobileElement element = (MobileElement) getElement(elementIdentifier, findBy);
    	  Log.info("Swiping the screen on element: " + element);
    	  Dimension dimensions = Appium.mobileDriver.manage().window().getSize();
    		
  	  		switch (direction.toLowerCase()) {
  				
  				case "down":
	  					 for(int i = 0;i<cycle;i++) {
	  					    Double screenHeightStart = dimensions.getHeight() * 0.5;
	  						int scrollStart = screenHeightStart.intValue();
	  						Double screenHeightEnd = dimensions.getHeight() * 0.2;
	  						int scrollEnd = screenHeightEnd.intValue();
	  						Appium.mobileDriver.swipe(0,scrollStart,0,scrollEnd,2000);
	  						wait(1);
	  					 }	
  				break; 
  			
  	    	}
      	  
    	  
    	  
      }
      
      /**
       * Zoom Function
             @param elementIdentifier
	   *            - element identifier value
	   *      @param findBy
	   *            - element identifier type e.g. id, xpath, css, class 
       */
      
      public void zoomElement(String elementIdentifier,String findBy) {
    	  
    	MobileElement element = (MobileElement) getElement(elementIdentifier, findBy);
  		waitForElementToBeDisplayed(elementIdentifier, findBy,
  				String.valueOf(timeout));
  		Log.info("Zooming on element: " + element);
  		element.zoom();
  		
	  	}
      
      /**
       * Tab a Element
       *  @param elementIdentifier
	   *            - element identifier value
	   *      @param findBy
	   *            - element identifier type e.g. id, xpath, css, class 
       */
      
      
      public void tabElement(String elementIdentifier,String findBy) {
    	  
    	    WebElement element = getElement(elementIdentifier, findBy);
    		waitForElementToBeDisplayed(elementIdentifier, findBy,
    				String.valueOf(timeout));
    		Log.info("Tab on element: " + element);
    		
    		//Performing touchAction
    		TouchAction touchAction = new TouchAction(Appium.mobileDriver);
    		touchAction.tap(element).perform();
    	  
      }
      
      /**
       * Scroll Text with direction
       * 
       */
      public void scrollText() {
    	  
    	  JavascriptExecutor js = (JavascriptExecutor) Appium.mobileDriver;
    	  HashMap<String, Double> scrollObjects = new HashMap<String, Double>();
    	  scrollObjects.put("startX", 116.00);
    	  scrollObjects.put("startY", 1268.00); 
    	  scrollObjects.put("endX", 164.00);
    	  scrollObjects.put("endY", 1118.00); 
    	  js.executeScript("mobile: scroll", scrollObjects);
    	
      }
      
      /**
       * Hide the keypad
       * 
       */
      public void hideKeypad() {
    	  Appium.mobileDriver.hideKeyboard();;
    	}
      
      /**
       *  Long Press Element
       *  @param elementIdentifier
	   *            - element identifier value
	   *      @param findBy
	   *            - element identifier type e.g. id, xpath, css, class 
       */
      
      
      public void longPress(String elementIdentifier,String findBy) {
    	  
    	    WebElement element = getElement(elementIdentifier, findBy);
    		waitForElementToBeDisplayed(elementIdentifier, findBy,
    				String.valueOf(timeout));
    		Log.info("Tab on element: " + element);
    		
    		//Performing touchAction
    		TouchAction touchAction = new TouchAction(Appium.mobileDriver);
    		touchAction.longPress(element).release().perform();
    	  
      }
      
      /**
       *   Rotate Screen
       * 
       */
      
      public void rotateScreen(String position) {
    	  
    	  switch (position.toLowerCase()) {
			case "landscape":
				 Appium.mobileDriver.rotate(ScreenOrientation.LANDSCAPE);
				break; 
			case "portrait":
				 Appium.mobileDriver.rotate(ScreenOrientation.PORTRAIT);
				break; 
    	  }		
    	 
      }
      
      
     /**
      * Uninstall the app
      * 
      */
  	 public static void removeApplication(String path) {
  		Appium.mobileDriver.removeApp(path);
  	 }
  	 
  	 /**
  	  * Reset Application
  	  * 
  	  */
  	 public static void resetApplication() {
  		 Appium.mobileDriver.resetApp();
  	 }
  	
      
      /**
  	 * Closes the Application instance associated with Mobile driver.
  	 */
  	public static void closeApplication() {
  		try {
  			Appium.mobileDriver.closeApp();
  			Log.info("Closing Application.");
  		} catch (Exception exception) {
  			Log.error("Error in closing Application: " + exception.getMessage());
  		}
  	}
      
}
    
