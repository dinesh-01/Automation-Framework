package web;




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
public class Selenium {
	private static final String NEW_LINE = "\n";
	public  static RemoteWebDriver webDriver = null;
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
	 * Initializes the Selenium object <code>webDriver</code>
	 * 
	 * @param browser
	 *            the possible values are 'ie', 'chrome', 'safari' or 'firefox'.
	 *            In case this value is set anything apart from these values or
	 *            blank, the default browser set is firefox.
	 * @param locale
	 *            this is the locale in which the browser will open. The values
	 *            vary depending on the browser.
	 * @throws Exception
	 */
	public static RemoteWebDriver initialize(String browser, String locale)
			throws Exception {
		
		switch (browser.toLowerCase()) {
		
		case "ie":
		
			Log.info("Initializing driver for " + browser);
			break;
			
		case "chrome":
			
			Selenium.webDriver = new ChromeDriver();
			Log.info("Initializing driver for " + browser);
			break;
			
		case "chromeHD":
			
			ChromeOptions options = new ChromeOptions();
	        options.addArguments("headless");
	        options.addArguments("window-size=1200x600");
	        Selenium.webDriver = new ChromeDriver(options);
			Log.info("Initializing driver for " + browser);
		    break;
			
		case "safari":
		
			Log.info("Initializing driver for safari");
			break;
			
		default:
		
			Log.info("Initializing driver for Firefox");
			break;
		}
		// Wait for browser to load.
		Selenium.webDriver.manage().window().setSize(new Dimension(1600,900));
		Selenium.webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		return Selenium.webDriver;

	}
	
	
		
	
	

	
	//Lanching page
	
	public static void launchBrowser(String pageUrl,RemoteWebDriver driver) throws Exception {
		
		if (url.isEmpty() || pageUrl.contains("http")) {
			url = pageUrl;
		} else if (!url.endsWith("/") && !url.startsWith("/")) {
			url = url + "/" + pageUrl;
		} else {
			url = url + pageUrl;
		}
		
		Log.info("Setting URL: " + url);
		driver.get(url);
	     
	}
	
	/**
	 * Closes the browser instance associated with web driver.
	 */
	public static void closeBrowser(RemoteWebDriver driver) {
		try {
			driver.close();
			driver.quit();
			Log.info("Closing browser.");
			wait(2);
			driver = null;
		} catch (Exception exception) {
			Log.error("Error in closing browser: " + exception.getMessage());
		}
	}
	
	/**
	 * Find the element and click on it
	 * 
	 * @param elementId
	 *            - element identifier value
	 * @param findBy
	 *            - element identifier type e.g. id, xpath, css, class
	 */
	public void click(String elementIdentifier, String findBy,RemoteWebDriver driver) {
		WebElement element = getElement(elementIdentifier, findBy, driver);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout),driver);
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
	public void clickAll(String elementIdentifier, String findBy, RemoteWebDriver driver) {
		List<WebElement> elements = getElements(elementIdentifier, findBy, driver);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout),driver);
		
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
	public void clickByIndex(String elementIdentifier, String findBy,int index,RemoteWebDriver driver) {
		List<WebElement> elements = getElements(elementIdentifier, findBy,driver);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout),driver);
		
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
	private void mouseHover(WebElement element,RemoteWebDriver driver) {
		waitForElementToBeDisplayed(element);
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.perform();
		Log.info("Hovering on element: " + element);
	}

	/**
	 * Mouse Hover to element provided.o
	 * 
	 * @param elementIdentifier
	 * @param findBy
	 */
	public void mouseHover(String elementIdentifier, String findBy,RemoteWebDriver driver) {
		Log.info("Hovering on element: " + elementIdentifier);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout),driver);
		WebElement element = getElement(elementIdentifier, findBy,driver);
		Actions actions = new Actions(driver);
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
	public void mouseClick(String elementIdentifier, String findBy,RemoteWebDriver driver) {
		Log.info("Click on element: " + elementIdentifier);
		waitForElementToBeDisplayed(elementIdentifier, findBy,
				String.valueOf(timeout),driver);
		WebElement element = getElement(elementIdentifier, findBy, driver);
		Actions actions = new Actions(driver);
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
	public void sendKeys(String elementIdentifier, String findBy, String text,RemoteWebDriver driver) {
		Log.info("Getting localized data for keyword " + text);
		// text = Localization.getData("zh-cn", text);
		Log.info("Sending keys");
		WebElement element = getElement(elementIdentifier, findBy, driver);
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
	public void sendKeys(String elementIdentifier, String findBy, int position,String text,RemoteWebDriver driver) {
		Log.info("Getting localized data for keyword " + text);
		// text = Localization.getData("zh-cn", text);
		Log.info("Sending keys");
		List<WebElement> elements = getElements(elementIdentifier, findBy,driver);
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
			String keyName,RemoteWebDriver driver) {
		try {
			Keys key = Keys.valueOf(keyName);
			Log.info("Pressing the keyboard key: " + keyName);
			WebElement element = getElement(elementIdentifier, findBy, driver);
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
	private static WebElement getElement(String elementIdentifier, String findBy,RemoteWebDriver driver) {
		Log.info("Finding element by identifier " + elementIdentifier
				+ " using " + findBy.toString());
		WebElement element = null;

		switch (findBy.toLowerCase()) {
		case "name":
			element = driver.findElement(By.name(elementIdentifier));
			break;
		case "id":
			element = driver.findElement(By.id(elementIdentifier));
			break;
		case "xpath":
			element = driver.findElement(By
					.xpath(elementIdentifier));
			break;
		case "linktext":
			element = driver.findElement(By
					.linkText(elementIdentifier));
			break;
		case "cssselector":
			element = driver.findElement(By
					.cssSelector(elementIdentifier));
			break;
		case "tagname":
			element = driver.findElement(By
					.tagName(elementIdentifier));
			break;
		case "classname":
			element = driver.findElement(By
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
	private static List<WebElement> getElements(String elementIdentifier, String findBy,RemoteWebDriver driver) {
		Log.info("Finding element by identifier " + elementIdentifier
				+ " using " + findBy.toString());
		List<WebElement> element = null;

		switch (findBy.toLowerCase()) {
		case "name":
			element = driver.findElements(By.name(elementIdentifier));
			break;
		case "id":
			element = driver.findElements(By.id(elementIdentifier));
			break;
		case "xpath":
			element = driver.findElements(By.xpath(elementIdentifier));
			break;
		case "linktext":
			element = driver.findElements(By.linkText(elementIdentifier));
			break;
		case "cssselector":
			element = driver.findElements(By.cssSelector(elementIdentifier));
			break;
		case "tagname":
			element = driver.findElements(By.tagName(elementIdentifier));
			break;
		case "classname":
			element = driver.findElements(By.className(elementIdentifier));
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
	public String getText(String elementIdentifier, String findBy, RemoteWebDriver driver) {
		String result = "";
		wait(1);
		Log.info("Getting the text value...");
		WebElement element = getElement(elementIdentifier, findBy, driver);

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
	
	public int getCountOfElements(String elementIdentifier, String findBy, RemoteWebDriver driver) {
		
		List<WebElement>  elements = getElements(elementIdentifier, findBy, driver);
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
	public String getValue(String elementIdentifier, String findBy, RemoteWebDriver driver) {
		String result = "";
		wait(1);
		Log.info("Getting the text value...");
		WebElement element = getElement(elementIdentifier, findBy, driver);

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
	public String getValues(String elementIdentifier, String findBy, RemoteWebDriver driver) {
		Iterator<WebElement> results;
		String result = "";
		wait(1);
		Log.info("Getting the text values...");
		List<WebElement> elements = getElements(elementIdentifier, findBy, driver);
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
	
	 public void clearValue(String elementIdentifier, String findBy, RemoteWebDriver driver)  {
		 
		 WebElement element = getElement(elementIdentifier, findBy, driver);
         element.clear();
		 Log.info("The Value is cleared in" + elementIdentifier + "element");
		 
	}
	
	
	
	
	/**
	 * Get the current window title
	 * 
	 * @return Name of the window
	 */
	public String getWindowTitle(RemoteWebDriver driver) {
		String message = "";
		try {
			Log.info("Getting the window title..." + message.toString());
			message = driver.getTitle();
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
	public boolean isElementPresent(String elementIdentifier, String findBy, RemoteWebDriver driver) {
		boolean result = false;
		try {
			Log.info("Checking for element present: " + elementIdentifier);
			getElement(elementIdentifier, findBy, driver);
			result = true;
		} catch (Exception exception) {
			Log.error("Element not found" + exception.getMessage());
		}
		return result;
	}
	


	/**
	 * Check whether the alert is present or not
	 * 
	 */
	public boolean isAlertPresent(RemoteWebDriver driver) {
		boolean result = false;
		try {
			Log.info("Checking for alert...");
			driver.switchTo().alert();
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
	public void acceptAlert(RemoteWebDriver driver) {
		
		Selenium.wait(2);
		Log.info("Calling alert.accept");
		Alert alert = driver.switchTo().alert();
		alert.accept();
	    
	}
	
	
	/**
	 * Prompt Alert
	 * 
	 */
	public void promptAlert(String text,RemoteWebDriver driver) {
		
		Selenium.wait(2);
		Log.info("Calling prompt alert.accept");
		Alert alert = driver.switchTo().alert();
		alert.sendKeys(text);
		alert.accept();
	}
	

	/**
	 * Dismiss the alert
	 * 
	 */
	public void dismissAlert(RemoteWebDriver driver) {
		
		Selenium.wait(2);
		Log.info("Calling alert.dismiss");
		Alert alert = driver.switchTo().alert();
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
	public void navigateBack(RemoteWebDriver driver) {
		try {
			Log.info("Navigating to back");
			driver.navigate().back();
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
	public void navigateForward(RemoteWebDriver driver) {
		try {
			Log.info("Navigating forward to next page");
			driver.navigate().forward();
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
	public void refresh(RemoteWebDriver driver) {
		try {
			Log.info("Refreshing the current page");
			driver.navigate().refresh();
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
	public WebElement waitForElementToLoad(By by, int timeOut,RemoteWebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
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
	public String getPopupMessage(RemoteWebDriver driver) {
		String message = null;
		Log.info("Getting the pop up message");
		Alert alert = driver.switchTo().alert();
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
	public String getToolTipText(By locator,RemoteWebDriver driver) {
		Log.info("Getting the tooltip");
		String tooltip = driver.findElement(locator).getAttribute(
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
			String value,RemoteWebDriver driver) {
		boolean result = false;
		try {
			Log.info("Selecting the dropdown element " + value);
			Select dropdown = new Select(getElement(elementIdentifier,
					findBy,driver));
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
			String findBy, String index,RemoteWebDriver driver) {
		boolean result = false;
		try {
			Log.info("Selecting the dropdown element by index " + index);
			Select dropdown = new Select(getElement(elementIdentifier,
					findBy,driver));
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
	
	 public String currentUrl(RemoteWebDriver driver) {
		 return driver.getCurrentUrl();
	 }
	
	
	
	
	/**
	 * Navigate to Url
	 * 
	 * @param Url
	 *            - Url to navigate to.
	 */
	public void navigateToUrl(String Url,RemoteWebDriver driver) {   
		try {
			Log.info("Navigating to Url" + Url);
			driver.navigate().to(Url);			
		} catch (Exception exception) {
			Log.error("Failed to navigate to Url" + exception.getMessage());
		}
	}

	/**
	 * Delete all cookies
	 * 
	 */
	public void deleteAllCookies(RemoteWebDriver driver) {
		try {
			Log.info("Deleting all cookies from browser");
			driver.manage().deleteAllCookies();
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

	public void pressKey(String key,RemoteWebDriver driver) {
		try {
			Keys keyName = Keys.valueOf(key);
			Log.info("Pressing the keyboard key: " + key);
			Actions action = new Actions(driver);
			action.keyDown(keyName);

		} catch (Exception exception) {
			Log.error("Error in pressing the keyboard key " + key
					+ exception.getMessage());
		}
	}
	
	public static void clearDriver(RemoteWebDriver driver) {
		driver = null;
	}

	/**
	 * Get table method finds table using attribute name & value.
	 * 
	 * @param attributeName
	 *            - name of the attribute
	 * @param attributeValue
	 *            - name of the value
	 */
	public void getTable(String attributeName, String attributeValue, RemoteWebDriver driver) {
		WebElement webElement = null;
		Log.info("Finding table with attribute name: " + attributeName
				+ " and attribute value " + attributeValue);
		List<WebElement> elements = driver.findElementsByTagName("table");
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
		Selenium.table = webElement;
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
			String findBy, String tableNumber,RemoteWebDriver driver) {
		boolean isTableFound = true;
		WebElement webElement = getElement(containerElementIdentifier, findBy, driver);
		List<WebElement> elements = webElement.findElements(By.tagName("table"));
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
			Selenium.table = webElement;
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
	public int getTableColumnCountInRow(int rowIndex,RemoteWebDriver driver) {
		int columnCount = 0;
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
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
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
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
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
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
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
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
			String textValue, String timeOut,RemoteWebDriver driver) {

		int rowIndex = Integer.parseInt(row);
		int columnIndex = Integer.parseInt(col);
		String cellContents = "";
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
		if (rows.size() > 0) {
			List<WebElement> cells = rows.get(rowIndex).findElements(
					By.tagName("td"));
			waitForTextToBePresentInElement(cells.get(columnIndex), textValue,
					Integer.parseInt(timeOut),driver);
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
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
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
	public String getPageTitle(RemoteWebDriver driver) {
		String pageTitle = driver.getTitle();
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
			String findBy, String timeOut,RemoteWebDriver driver) {
		Log.info("Waiting for element [" + elementIdentifier
				+ "] to appear for " + timeOut + " seconds.");
		WebDriverWait wait = new WebDriverWait(driver,
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
			String findBy, String timeOut,RemoteWebDriver driver) {
		waitForElementToBeClickable(elementIdentifier, findBy, timeOut,driver);
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
			String findBy, String timeOut,RemoteWebDriver driver) {
		Log.info("Waiting for element [" + elementIdentifier
				+ "] to be displayed for " + timeOut + " seconds.");
		WebElement webElement = getElement(elementIdentifier, findBy, driver);
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
			String textValue, int timeOut,RemoteWebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		return wait.until(ExpectedConditions.textToBePresentInElementValue(
				element, textValue));
	}

	
	/**
	 * Take screenshot of the page & saves it to specified location
	 * 
	 * @param fileName
	 *            - file path to save screenshot.
	 * @throws IOException
	 */
	public static void takeScreenshot(String fileName,RemoteWebDriver driver) throws IOException {
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
	public String validateTabOrder(RemoteWebDriver driver) {
		String elementsNotHavingCorrectTabOrder = "";
		Map<String, WebElement> elements = getAllPageElementsForTabOrder();
		Iterator<java.util.Map.Entry<String, WebElement>> entries = elements
				.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, WebElement> entry = entries.next();
			WebElement webElement = entry.getValue();
			String xPath = entry.getKey().toString();
			if (!webElement.equals(driver.switchTo().activeElement())) {
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
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
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
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
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
		WebElement tagBody = Selenium.table.findElement(By.tagName("tbody"));
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
			String findBy, String seperator,RemoteWebDriver driver) {
		String listContents = "";
		WebElement list = getElement(elementIdentifier, findBy, driver);
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
	public String isElementDisplayed(String elementIdentifier, String findBy,RemoteWebDriver driver) {
		String result = "fail";
		WebElement element = getElement(elementIdentifier, findBy, driver);
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
	public String isElementEnabled(String elementIdentifier, String findBy, RemoteWebDriver driver) {
		String result = "fail";
		WebElement element = getElement(elementIdentifier, findBy, driver);
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
	public String isElementSelected(String elementIdentifier, String findBy,RemoteWebDriver driver) {
		String result = "fail";
		WebElement element = getElement(elementIdentifier, findBy, driver);
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
		WebElement tableBody = Selenium.table.findElement(By.tagName("tbody"));
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
			String tagNameForCount, RemoteWebDriver driver) {
		WebElement element = getElement(elementIdentifier, findBy, driver);
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
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
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
		List<WebElement> rows = Selenium.table.findElements(By.tagName("tr"));
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
	public static void selectBrowserTab(String tabIndex,RemoteWebDriver driver) {
		Set<String> winHandlesList = driver.getWindowHandles();
		int i = 1;
		for (String winHandle : winHandlesList) {
			if (i == Integer.parseInt(tabIndex)) {
				driver.switchTo().window(winHandle);
				Log.info("Switched to window index #" + i
						+ ". Window handle :: [" + winHandle + "].");
				Log.info("Switched window title :: "
						+ driver.getTitle());
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
			String attributeName,RemoteWebDriver driver) {
		WebElement element = getElement(elementIdentifier, findBy, driver);
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
	 public void switchToFrame(String elementIdentifier, String findBy,RemoteWebDriver driver) {
		 WebElement element = getElement(elementIdentifier, findBy, driver);
		 driver.switchTo().frame(element);
	 }
	
	 
	 /**
	  * This method will scroll down the page
	  * @param current driver 
	  */
	 
	 public void scrollDown(RemoteWebDriver driver) {
		 
		 JavascriptExecutor js = (JavascriptExecutor) driver;
		 driver.manage().window().maximize();

	     // This  will scroll down the page by  1000 pixel vertical		
	     js.executeScript("window.scrollBy(0,1000)");
	 }
	 
	 /**
	  * This method will scroll down the page
	  * @param current driver 
	  */
	 
	 public void scrollUp(RemoteWebDriver driver) {
		 
		 JavascriptExecutor js = (JavascriptExecutor) driver;
		 driver.manage().window().maximize();

	     // This  will scroll down the page by  1000 pixel vertical		
	     js.executeScript("window.scrollBy(0,-1000)");
	 }
	 
	
	/**
	 * This method will enable element based on action
	 **
	 * @param elementIdentifier
	 * @param findBy
	 * @param action
	 */
	
	public void changeElementView(String elementIdentifier, String findBy,
			String action,RemoteWebDriver driver) {
    	
    	WebElement element = getElement(elementIdentifier, findBy, driver);
    	JavascriptExecutor executor= (JavascriptExecutor) driver;
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
  			String className,RemoteWebDriver driver) {
    	  
    		WebElement element = getElement(elementIdentifier, findBy, driver);
        	JavascriptExecutor executor= (JavascriptExecutor) driver;
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
       
      public String innerHtmlCode(String findBy,String element, RemoteWebDriver driver) {
    	  
    	 JavascriptExecutor executor= (JavascriptExecutor) driver;
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
      
      public void formSubmit(String action,RemoteWebDriver driver) {
    	  
    	  JavascriptExecutor executor= (JavascriptExecutor) driver;
     	  executor.executeScript("return document.forms."+action+".submit();");
     	
      }
 
}
    
