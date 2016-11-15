package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import web.Http;
import common.Log;
import environment.FileUtil;
import environment.Xml;

/**
 * Exposes static methods for parsing Fitnesse result, get fitnesse test cases
 * by Tag.
 * 
 * @author Administrator
 * 
 */
public class Fitnesse {

	private String testCaseId;
	private String defectId;
	private String testPageName;
	private String fieldName;
	private String fieldValue;

	private static HashMap<String, String> testMap = new HashMap<String, String>();
	private static HashMap<String, String> defectMap = new HashMap<String, String>();
	private static HashMap<String, String> settingsMap = new HashMap<String, String>();
	private static HashMap<String, String> toroSettingsMap = new HashMap<String, String>();
	private static HashMap<String, String> variableMap = new HashMap<String, String>();

	/**
	 * Set test case ID from FitNesse to build map
	 * 
	 * @param TestID
	 *            - test case id
	 */
	public void setTestID(String TestID) {
		this.testCaseId = TestID;
	}

	public void setFieldName(String field) {
		this.fieldName = field;
	}

	public void setFieldValue(String value) {
		this.fieldValue = value;
	}

	/**
	 * Set test page Name from FitNesse to build map
	 * 
	 * @param pageName
	 *            - FitNess test page name
	 */
	public void setPageName(String pageName) {
		this.testPageName = pageName;
	}

	/**
	 * Set defect ID from FitNesse to add it to defect map
	 * 
	 * @param defectId
	 *            - Etrack ID which is in Open state for that test case
	 */
	public void setDefectID(String defectId) {
		this.defectId = defectId;
	}

	/**
	 * Builds hash map of Fitnesse test page name and defect ID.
	 */
	public void buildDefectMap() {
		defectMap.put(this.testPageName, this.defectId);
	}

	/**
	 * BuildTestMap builds hashmap of Fitnesse test page name and test case ID.
	 * Mapping for test page name to Toro ID is provided on the SuiteSetup page
	 * of the suite. This function is invoked when the SuiteSetup page is
	 * executed.
	 */
	public void buildTestMap() {
		testMap.put(this.testPageName, this.testCaseId);
	}

	/**
	 * Mapping for defect with test case, this is created to skip those test
	 * cases from running.
	 * 
	 * @throws Exception
	 */
	public void validateAndSkipTestCase() throws Exception {
		@SuppressWarnings("rawtypes")
		Iterator iterator = defectMap.entrySet().iterator();
		@SuppressWarnings("unused")
		List<String> listOfElements = new ArrayList<String>();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) iterator.next();
			String defectId = pairs.getValue().toString();
			String pagePath = pairs.getKey().toString();
			String state = Etrack.getDefectDetails(defectId, "state");
			if (!state.equalsIgnoreCase("closed")) {
				String testCasePath = ".\\FitNesseRoot\\"
						+ pagePath.replace('.', '\\');
				String propertiesFilePath = testCasePath + "\\properties.xml";
				String nodeValue = "";
				try {
					nodeValue = Xml.getNodeValues(propertiesFilePath, "Suites",
							":");
				} catch (Exception exception) {
					// do nothing
				}
				if (!(nodeValue.isEmpty() || nodeValue.equals("") || nodeValue
						.contains("SKIP"))) {
					String[] values = nodeValue.split(",");
					String valueToReplace = "<Suites>" + nodeValue
							+ "</Suites>";
					String valueToReplaceWith = "<Suites>SKIP</Suites>";
					if (values.length > 0) {
						valueToReplaceWith = "<Suites>" + nodeValue
								+ ",SKIP</Suites>";
					}
					FileUtil.replaceFileContent(propertiesFilePath,
							valueToReplace, valueToReplaceWith);
				}
				else if(!nodeValue.contains("SKIP"))
				{
					Xml.addElementToXml(propertiesFilePath, "Test", "<Suites>SKIP</Suites>");
				}
			}
		}
	}

	public void buildSettingsMap() {
		Log.info("Adding key " + this.fieldName + " and value "
				+ this.fieldValue);
		settingsMap.put(this.fieldName, this.fieldValue);
	}

	/**
	 * This methods reads toro settings form Fitneese & puts it into
	 * toroSettiongsMap hashmap.
	 */
	public void buildToroSettingsMap() {
		Log.info("Adding key " + this.fieldName + " and value "
				+ this.fieldValue);
		toroSettingsMap.put(this.fieldName, this.fieldValue);
	}

	/**
	 * Gets settings value from Toro settings.
	 * 
	 * @param fieldName
	 *            - settings name to get value for.
	 * @return - value for provided setting.
	 */
	public static String getToroSettingsValue(String fieldName) {
		String fieldValue = null;
		try {
			if (toroSettingsMap.containsKey(fieldName)) {
				fieldValue = toroSettingsMap.get(fieldName);
			}
		} catch (Exception exception) {
			Log.info("Error while reading configuration from Fitnesse"
					+ exception.getMessage());
		}
		return fieldValue;
	}

	/**
	 * Gets test id for page name from the Hash Map
	 * 
	 * @param Testpagename
	 * @return
	 */
	public static String getTestId(String testPageName) {
		String testCaseId = null;
		try {
			if (testMap.containsKey(testPageName)) {
				testCaseId = testMap.get(testPageName);
				Log.info("Toro ID for Page name " + testPageName + " is "
						+ testCaseId);
			}
		} catch (Exception exception) {
			Log.error(
					"Error while getting test case id" + exception.getMessage());
		}
		return testCaseId;
	}

	/**
	 * Gets test id for page name from the Hash Map
	 * 
	 * @param Testpagename
	 * @return
	 */
	public static String getSettingsFieldValue(String fieldName) {
		String fieldValue = null;
		try {
			if (settingsMap.containsKey(fieldName)) {
				fieldValue = settingsMap.get(fieldName);
			}
		} catch (Exception exception) {
			Log.error("Error while reading configuration from Fitnesse"
					+ exception.getMessage());
		}
		return fieldValue;
	}

	/**
	 * Add variable to map
	 * 
	 * @param name
	 *            - name of the variable
	 * @param value
	 *            - value of the variable
	 */
	public void addVariable(String name, String value) {
		Log.info("Adding variable " + name + " and value " + value);
		variableMap.put(name, value);
	}

	/**
	 * Get variable value from map
	 * 
	 * @param name
	 *            - name of the variable
	 */
	public String getValue(String variableName) {
		String fieldValue = null;
		try {
			if (variableMap.containsKey(variableName)) {
				fieldValue = variableMap.get(variableName);
			}
		} catch (Exception exception) {
			Log.error("Error while reading variable value from Fitnesse"
					+ exception.getMessage());
		}
		return fieldValue;
	}

	public static String parseFitnesseTestResult(String fitnesseTestCase) {

		int wrongcount = -1;
		int exceptionscount = -1;

		try {
			String currentDir = System.getProperty("user.dir");
			Log.info("current path is " + currentDir);
			String filePath = ".\\FitNesseRoot\\files\\testResults\\"
					+ fitnesseTestCase;
			Log.info("Test results file path is " + filePath);
			String errorFileName = ".\\FitNesseRoot\\ErrorLogs\\"
					+ fitnesseTestCase.replace('.', '\\') + "\\content.txt";
			File file = FileUtil.getLastModifiedFile(filePath);
			Log.info("Latest File is " + file.getName());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			// Get the required Node
			NodeList nList = doc.getElementsByTagName("finalCounts");
			Node nNode = nList.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				wrongcount = Integer
						.parseInt(eElement.getElementsByTagName("wrong")
								.item(0).getTextContent());
				exceptionscount = Integer.parseInt(eElement
						.getElementsByTagName("exceptions").item(0)
						.getTextContent());
			}
			Log.info("Wrong Count = " + wrongcount);
			Log.info("Exceptions Count = " + exceptionscount);
			if (wrongcount == 0 && exceptionscount == 0) {
				Log.info(fitnesseTestCase + "status is [Pass]");
				return "Pass";
			} else {
				File errorFile = new File(errorFileName);
				if (errorFile.exists()) {
					Log.info("\nError File Path: " + errorFileName);
				}
				Log.info(fitnesseTestCase + "status is [Fail]");
				return "Fail";
			}
		} catch (Exception exception) {
			Log.error(fitnesseTestCase + "status is [Error]");
			Log.error(exception.getMessage());
			Log.error(exception.getStackTrace().toString());
			return "Error";
		}
	}

	// --------------- Support Functions

	/**
	 * Prepares the test cases list as per the input
	 * 
	 * @param fitnessServer
	 *            - Fitnesse URL with IP & Port e.g. http://10.211.64.119:443
	 * @param tagName
	 *            - name of the tag to fetch test cases tagged with this name.
	 */
	public static void GetFitnesseTestCasesFromTag(String fitnessServer,
			String tagName) throws Exception {
		List<String> testCases = new ArrayList<>();
		if (tagName.isEmpty()) {
			testCases = getTestCasesFromFitnesseForTag(fitnessServer, tagName);
		} else {
			String[] tags = tagName.split(",");
			for (String tag : tags) {
				testCases.addAll(getTestCasesFromFitnesseForTag(fitnessServer,
						tag));
			}
		}

		Log.info("Writing test cases to csv");
		if (testCases.size() > 0) {
			File file = new File("./AutomatedTestCases.csv");
			file.createNewFile();
			org.apache.commons.io.FileUtils.writeLines(file, testCases);
		} else {
			throw new Exception("No test cases are found for given tag.");
		}
	}

	private static List<String> getTestCasesFromFitnesseForTag(
			String fitnessServer, String tagName) throws Exception {
		List<String> testCases = new ArrayList<>();
		String suiteName = "AutomationSuites";
		String url = fitnessServer
				+ suiteName
				+ "?responder=executeSearchProperties&ExcludeSetUp&ExcludeTearDown&PageType=Test&Suites="
				+ tagName;

		if (tagName.isEmpty()) {
			url = fitnessServer
					+ suiteName
					+ "?responder=executeSearchProperties&ExcludeSetUp&ExcludeTearDown&PageType=Test";
		}

		Log.info("Fetching test cases from Fitnesse server: " + url
				+ " For tag " + tagName);
		String response = Http.getRequest(url);
		Log.info("Parsing test cases from Fitnesse response");
		int startIndex = response.indexOf("<tbody>");
		int endIndex = response.indexOf("</tbody>");
		String testcasesInHtmlFormat = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ response.substring(startIndex, endIndex) + "</tbody>";
		Document document = Xml.getXmlDocumentForString(testcasesInHtmlFormat);
		NodeList nList = document.getElementsByTagName("tr");
		Log.info("No. of test cases found:" + nList.getLength());
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				NodeList nodeList = eElement.getElementsByTagName("a");
				Element linkElement = (Element) nodeList.item(0);
				String testCaseName = linkElement.getAttribute("href");
				testCaseName = suiteName
						+ "."
						+ testCaseName.substring(0,
								testCaseName.indexOf("?test"));
				Log.info("Adding test case: " + testCaseName);
				testCases.add("Fitnesse,Fitnesse," + testCaseName + ",,,"
						+ "Sequence,1234,8");
			}
		}
		// print result
		Log.info("Total Fitnesse test cases found for Tag " + tagName + ": "
				+ testCases.size());
		return testCases;
	}

	public void begin(String pageName) {
		Log.info("ENTER  " + pageName);
		Log.appendCharacter = "\t";
	}

	public void end(String pageName) {
		Log.appendCharacter = "";
		Log.info("LEAVE " + pageName);
	}

	/**
	 * Place holder for pause, needs to add implementation
	 */
	public void pause() {

	}

	public void updateFitnesseReportForTestcaseWithOpenDefect()
			throws Exception {
		String etrackLink = "https://engtools.engba.symantec.com/Etrack/readonly_inc.php?sid=etrack&incident=";
		@SuppressWarnings("rawtypes")
		Iterator iterator = defectMap.entrySet().iterator();
		List<String> listOfElements = new ArrayList<String>();
		String content =  "<div class=\"contents\">" + "<table><tr><td class=\"error\">Test case failed due to open defect.";
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) iterator.next();
			String reportData = "<result><counts><right>0</right><wrong>1</wrong>"
					+ "<ignores>0</ignores><exceptions>0</exceptions></counts><runTimeInMillis>6000</runTimeInMillis>"
					+ "<relativePageName>|pageName|</relativePageName><pageHistoryLink>|defectLink|</pageHistoryLink>"
					+ "<tags>BAT, Regression</tags><content><![CDATA[|content|]]></content></result>";
			String key = pairs.getKey().toString();
			Log.info("Updating report for test case: " + key);
			String[] temp = key.split(Pattern.quote("."));
			Log.info("Updating page name: " + temp[temp.length - 1]);
			reportData = reportData
					.replace("|pageName|", temp[temp.length - 1]);
			etrackLink = etrackLink + pairs.getValue();
			etrackLink = content + "<a href='" + etrackLink + "'>Click here to navigate to defect</a>" + "</td></tr></table></div>";
			reportData = reportData.replace("|defectLink|", key + "?pageHistory");
			reportData = reportData.replace("|content|", etrackLink);
			Log.info("Updating content as: " + reportData);
			listOfElements.add(reportData);
		}
		Xml.addElementsToXml("../fitnesse-results.xml", "result",
				listOfElements);
	}
	
}
