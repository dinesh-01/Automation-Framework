package common;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

/**
 * Exposes methods to generate report for automation
 */
public class Report {
	private static Map<Integer, String> testcases = new Hashtable<Integer, String>();
	private static Map<Integer, String> testcaseStatus = new Hashtable<Integer, String>();
	private static Map<Integer, String> testcaseDetails = new Hashtable<Integer, String>();
	private static Map<Integer, String> suites = new Hashtable<Integer, String>();
	private static Map<Integer, List<Integer>> suiteTestcases = new Hashtable<Integer, List<Integer>>();
	private static Map<Integer, List<Integer>> suitesChild = new Hashtable<Integer, List<Integer>>();
	private static int failedTestcases = 0;
	private static int passedTestcases = 0;
	private static int skippedTestcases = 0;

	public static void getResultsInTestNgFormat(String resultFilePath,
			String fitNessServer) {
		File file = new File(resultFilePath);
		if (!file.exists()) {
			Log.error("Failed to generate TestNg results, file not found: "
					+ file.getAbsolutePath());
			return;
		}
		String cvsSplitBy = ",";
		try {
			List<String> lines = org.apache.commons.io.FileUtils
					.readLines(file);
			int count = 0;
			boolean firstTime = true;
			for (String line : lines) {
				if (firstTime) {
					firstTime = false;
					continue;
				}
				// use comma as separator
				String[] testCaseDetails = line.split(cvsSplitBy);
				if (testCaseDetails.length >= 4) {
					// Error
				}
				String testCase = testCaseDetails[0];
				String result = testCaseDetails[3];
				String[] tempSuites = testCase.split("\\.");

				testcases.put(count, tempSuites[tempSuites.length - 1]);
				testcaseStatus.put(count, result);
				if (result.toLowerCase().equals("pass")) {
					passedTestcases++;
				} else if (result.toLowerCase().equals("fail")) {
					failedTestcases++;
				} else {
					skippedTestcases++;
				}

				String fitNesseURL = fitNessServer + testCase + "?pageHistory"
						+ "\n" + fitNessServer + "ErrorLogs." + testCase;

				testcaseDetails.put(count, fitNesseURL);

				int suiteCount = suites.size();
				boolean foundNewSuite = false;

				for (int i = 0; i < tempSuites.length - 1; i++) {
					if (getIDofSuite(tempSuites[i]) == -1) {
						foundNewSuite = true;
						suites.put(suiteCount, tempSuites[i]);
						suiteCount++;
					}
				}

				List<Integer> testCasesforSuite = new ArrayList<Integer>();
				int testCaseSuite = getIDofSuite(tempSuites[tempSuites.length - 2]);

				if (suiteTestcases.containsKey(testCaseSuite)) {
					testCasesforSuite = suiteTestcases.get(testCaseSuite);
					testCasesforSuite.add(count);
				} else {
					testCasesforSuite.add(count);
				}
				suiteTestcases.put(testCaseSuite, testCasesforSuite);

				if (foundNewSuite) {
					for (int j = 0; j < tempSuites.length - 1; j++) {
						List<Integer> suiteChilds = new ArrayList<Integer>();
						if (j + 1 != tempSuites.length) {
							Integer key = getIDofSuite(tempSuites[j]);
							Integer value = getIDofSuite(tempSuites[j + 1]);
							if (suitesChild.containsKey(key)) {
								suiteChilds = suitesChild.get(key);
								suiteChilds.add(value);
							} else {
								suiteChilds.add(value);
							}
							suitesChild.put(key, suiteChilds);
						}
					}
				}
				count++;
			}// End of for

			Log.info("Total test cases:" + String.valueOf(testcases.size()));
			Log.info("Pass test cases:" + String.valueOf(passedTestcases));
			Log.info("Failed test cases:" + String.valueOf(failedTestcases));
			Log.info("Skipped test cases:" + String.valueOf(skippedTestcases));
			createXMLForDashboard();
		} catch (Exception exception) {
			Log.error(exception.getMessage());
			Log.error(exception.getStackTrace().toString());
		}
	}

	private static int getIDofSuite(String value) {
		int id = -1;
		Iterator<Entry<Integer, String>> iterator = suites.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) iterator.next();
			if (pairs.getValue().toString().equals(value)) {
				id = (Integer) pairs.getKey();
				break;
			}
		}
		return id;
	}

	private static void createXMLForDashboard() {
		String fileName = "./testng-results.xml";
		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();

			XMLStreamWriter xmlWriter = factory
					.createXMLStreamWriter(new FileWriter(fileName));

			xmlWriter.writeEndDocument();
			xmlWriter.writeStartElement("testng-results");
			xmlWriter.writeAttribute("skipped",
					String.valueOf(skippedTestcases));
			xmlWriter.writeAttribute("failed", String.valueOf(failedTestcases));
			xmlWriter.writeAttribute("total", String.valueOf(testcases.size()));
			xmlWriter.writeAttribute("passed", String.valueOf(passedTestcases));
			xmlWriter.writeStartElement("reporter-output");
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement("suite");
			xmlWriter.writeAttribute("name", "AutomationSuite");
			xmlWriter.writeAttribute("duration-ms", "1404");
			xmlWriter.writeAttribute("started-at", "2014-02-27T20:51:04Z");
			xmlWriter.writeAttribute("finished-at", "2014-02-27T20:51:06Z");
			xmlWriter.writeStartElement("test");
			xmlWriter.writeAttribute("name", "FitnessTestcases");
			xmlWriter = createXmlElement(0, xmlWriter);
			xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();

			xmlWriter.flush();
			xmlWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static XMLStreamWriter createXmlElement(Integer value,
			XMLStreamWriter xmlWriter) throws Exception {
		List<Integer> values = suitesChild.get(value);
		// List<String> values = Arrays.asList(tempValues);
		if (values.size() == 1 && values.get(0) == -1) {
			xmlWriter.writeStartElement("class");
			xmlWriter.writeAttribute("name", getClassName(value));
			// xmlWriter.WriteAttributeString("duration-ms", "0");
			// xmlWriter.WriteAttributeString("started-at", "");
			// xmlWriter.WriteAttributeString("finished-at", "");

			List<Integer> testcasesList = new ArrayList<Integer>();
			if (suiteTestcases.containsKey(value)) {
				testcasesList = suiteTestcases.get(value);
			}
			for (int testcase : testcasesList) {
				xmlWriter.writeStartElement("test-method");
				xmlWriter.writeAttribute("name", testcases.get(testcase));
				xmlWriter
						.writeAttribute("status", testcaseStatus.get(testcase));

				xmlWriter.writeAttribute("duration-ms", "1404");
				xmlWriter.writeAttribute("started-at", "2014-02-27T20:51:04Z");
				xmlWriter.writeAttribute("finished-at", "2014-02-27T20:51:06Z");
				xmlWriter.writeStartElement("exception");
				xmlWriter.writeAttribute("class", "org.testng.TestException");
				xmlWriter.writeStartElement("message");
				xmlWriter.writeCData(testcaseDetails.get(testcase));
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement("full-stacktrace");
				xmlWriter.writeCData("");
				xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();
		} else {
			for (Integer suiteId : values) {
				xmlWriter = createXmlElement(suiteId, xmlWriter);
			}
		}
		return xmlWriter;
	}

	private static String getClassName(Integer suiteId) {
		String value = suites.get(suiteId);
		while (suiteId != 0) {
			Iterator<Entry<Integer, List<Integer>>> iterator = suitesChild
					.entrySet().iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pairs = (Map.Entry) iterator.next();
				@SuppressWarnings("unchecked")
				List<Integer> values = (List<Integer>) pairs.getValue();
				if (values.contains((suiteId))) {
					value = suites.get(pairs.getKey()) + "." + value;
					suiteId = (Integer) pairs.getKey();
					break;
				}
			}
		}
		return value;
	}
}
