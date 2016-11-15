package common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import web.Selenium;

/**
 * This class contains common methods for I18n support.
 * */
public class Localization extends Selenium {

	// Store label values for all elements for English locale for individual URL
	private static HashMap<String, HashMap<String, String>> psedoBuildEnglishLabels = new HashMap<String, HashMap<String, String>>();
	// Store label values for all elements for localized changes for individual
	// URL
	private static HashMap<String, HashMap<String, String>> psedoBuildLocalizedLabels = new HashMap<String, HashMap<String, String>>();
	private static List<String> hardCodedStrings = new ArrayList<String>();
	private static List<String> hardCodedStringsLocalized = new ArrayList<String>();
	private static List<String> elementsForWorkFlow = new ArrayList<String>();
	private String prefix = "";
	private String suffix = "";

	/**
	 * Set prefix & suffix for pseudo build.
	 * 
	 * @param prefix
	 *            - prefix to append to English word while comparing
	 * @param sufix
	 *            - suffix to append to English word while comparing
	 */
	public void setPrefixAndSuffix(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	/**
	 * Get locale specific data for keyword from that locale's property file.
	 * Property file for project is maintained by each project.
	 * 
	 * @param locale
	 *            - locale to get data for, default it reads from English.
	 * @param keyWord
	 *            - keyword to get data for.
	 * @return locale specific data from properties file.
	 */
	public static String getData(String locale, String keyWord) {
		String data = "";
		java.util.Locale currentLocale = java.util.Locale.ENGLISH;
		switch (locale) {
		case "fr-fr":
			currentLocale = java.util.Locale.FRENCH;
			break;
		case "zh-cn":
			currentLocale = java.util.Locale.CHINESE;
			break;
		}
		ResourceBundle labels = ResourceBundle.getBundle("SeleniumInputs",
				currentLocale);
		data = labels.getString(keyWord);
		return data;
	}

	
	public static void setElementsForWorkFlow(String elements) {
		String[] tempElements = elements.split(",");
		elementsForWorkFlow = Arrays.asList(tempElements);
	}

	/**
	 * Get label values for all the elements on the page.
	 * 
	 * @param url
	 *            - URL to load
	 * @param browser
	 *            - browser to open URL in.
	 * @param locale
	 *            - locale for browser.
	 * @throws Exception
	 */
	public static void getLablesForPseudoBuild(String url, String locale)
			throws Exception {
		HashMap<String, String> allElementsValues = getAllPageElementsLable(elementsForWorkFlow);
		if (locale.equalsIgnoreCase("en-us")) {
			psedoBuildEnglishLabels.put(url, allElementsValues);
		} else {
			psedoBuildLocalizedLabels.put(url, allElementsValues);
		}
		String[] temp = url.split("/");
		Log.info("Array length:" + temp.length);
		String filePath = locale;
		for (int i = 3; i < temp.length; i++) {
			filePath = filePath + "_" + temp[i];
		}
		Log.info("File path:" + filePath);
		takeScreenshot(filePath);
		closeBrowser();
		webDriver = null;
	}

	/**
	 * Compare labels of English locale with localized version of the same page.
	 * 
	 * @return
	 */
	public String validateChanges() {
		boolean result = false;
		String message = "Hardcoded Strings:\n";
		Iterator<java.util.Map.Entry<String, HashMap<String, String>>> entries = psedoBuildLocalizedLabels
				.entrySet().iterator();
		boolean firstTime = true;
		while (entries.hasNext()) {
			@SuppressWarnings("rawtypes")
			Entry entry = entries.next();
			@SuppressWarnings("unchecked")
			HashMap<String, String> allLocalizedValues = (HashMap<String, String>) entry
					.getValue();
			HashMap<String, String> allEnglishValues = psedoBuildEnglishLabels
					.get(entry.getKey());
			Iterator<java.util.Map.Entry<String, String>> elementIterator = allEnglishValues
					.entrySet().iterator();

			while (elementIterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Entry elementEntry = elementIterator.next();
				Object elementKey = elementEntry.getKey();
				String englishValue = elementEntry.getValue().toString();
				String orginalValue = englishValue;
				englishValue = this.prefix + englishValue + this.suffix;
				String localizedValue = allLocalizedValues.get(elementKey);
				if (hardCodedStrings.contains(localizedValue)) {
					hardCodedStringsLocalized.add(localizedValue);
				}
				Log.info("Element value in English [" + englishValue + "]");
				Log.info("Element value in Localized format ["
						+ localizedValue + "]");
				if (englishValue.equalsIgnoreCase(localizedValue)) {
					if (firstTime) {
						result = true;
						firstTime = false;
					} else {
						result = result && true;
					}
				} else {
					result = false;
					Log.error("!! Validation Failed !!");
					message = message + orginalValue + "\n";
				}
			}
		}
		if (hardCodedStrings.size() == hardCodedStringsLocalized.size()) {
			result = result && true;
		} else {
			message = message
					+ "\n\n************************************\n Validation Failed for Hardcoded String\n";
			Log.error("Hard coded values are converted");
		}
		psedoBuildEnglishLabels.clear();
		psedoBuildLocalizedLabels.clear();
		hardCodedStrings.clear();
		hardCodedStringsLocalized.clear();

		if (result) {
			return "Pass";
		} else {
			return message;
		}
	}
	
	/**
	 * Creats pseudo file from english locale
	 * @param sourceFile - english locale file
	 * @param destinationFile - locale specific file
	 * @param propertiesSeperator - keyword value seperator
	 * @throws IOException
	 */
	public void createPseudoFile(String sourceFile, String destinationFile,
			String propertiesSeperator) throws IOException {
		File file = new File(sourceFile);
		List<String> lines = FileUtils.readLines(file);
		List<String> finalLines = new ArrayList<String>();
		String seperator = propertiesSeperator;
		String endingWith = "\",";
		String startWith = "\"";
		int count = 0;
		for (String line : lines) {
			count++;
			if (count == lines.size() - 1) {
				endingWith = "\"";
			}
			String finalLine = "";
			Log.info("Original line: " + line);
			if (line.contains(seperator)) {
				String[] temp = line.split(seperator);
				if (2 <= temp.length) {
					String keyWord = temp[0];
					String value = temp[1];
					value = value.trim();
					value = value.substring(1,
							value.length() - endingWith.length());
					value = startWith + prefix + value + suffix + endingWith;

					finalLine = keyWord + seperator + value;
					Log.info("Pseudo line: " + finalLine);
				}
			} else {
				finalLine = line;
			}
			finalLines.add(finalLine);
		}
		File destination = new File(destinationFile);
		FileUtils.writeLines(destination, finalLines);
	}

	public void addHardCodedStrings(String value, String seperator) {
		if (value.contains(seperator)) {
			String[] hardCodedValues = value.split(seperator);
			for (String hardCodedValue : hardCodedValues) {
				hardCodedStrings.add(hardCodedValue);
			}
		} else {
			hardCodedStrings.add(value);
		}
	}
}
