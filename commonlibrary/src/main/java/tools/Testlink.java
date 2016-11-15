package tools;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ActionOnDuplicate;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestImportance;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStep;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import common.Log;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIResults;

/**
 * API interface to let fitnesse to interact with testlink
 * @author Administrator
 *
 */


public class Testlink {
	
	
	/**
	 * @param DEVKEY
	 *            - API key for interacting with testlink portal
	 * @param URL
	 *            - Server URL of the testlink 
	 * 
	*/
	
	public static String DEVKEY;
	public static String url;
	
	public Testlink(String Key, String link) {
		
		DEVKEY  = Key;
		url     = link;
		
	}
	
	
	/**
	 * 
	 * @param TestProject
	 *            - Current project name
	 * @param TestPlan
	 *            - Current test plan
	 * @param Testcase
	 *            - TestCase name or id  
	 * @param Build
	 *            - Build Name
	 * @param Notes
	 *            - Any Specfic Notes
	 * @param Result
	 *            - End result true or false
	 */
	
	public static void executeTestCase(String TestProject,String TestPlan,String Testcase,String Build,String Notes,String Result) {
		
		TestLinkAPIClient api = new TestLinkAPIClient(DEVKEY, url);
	
		if( Result.equals("true") || Result.equals("pass") ) {
			Result= TestLinkAPIResults.TEST_PASSED;
		}else{
			Result= TestLinkAPIResults.TEST_FAILED;
		}
	
		
		try {
			api.reportTestCaseResult(TestProject, TestPlan, Testcase, Build, Notes, Result);
			Log.info("Successfully Interacting with TestCase Results Report");
			Log.info("TestCase Executed Successfully");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.info("Unable to interact with TestCase Results Report with follwing error "+e);
		}
		
   }
	
	
	/**
	 * Creating New Test Cases
	 * 
	 * @param testcaseName
	 * @param testSuiteId
	 * @param testProjectId
	 * @param authorLogin
	 * @param importance
	 * @throws MalformedURLException
	 */
	
	
   public static void createTestCase(String testcaseName,int testSuiteId,int testProjectId,String authorLogin,String importance) throws MalformedURLException {
	   
	   URL testlinkURL = new URL(url);
	   TestLinkAPI api = new TestLinkAPI(testlinkURL, DEVKEY);
	  
	  List<TestCaseStep> steps = null;
	   try {
		api.createTestCase(
				testcaseName, // testCaseName
		            new Integer(testSuiteId), // testSuiteId
		            new Integer(testProjectId), // testProjectId
		            authorLogin, // authorLogin
		            "No summary", // summary
		            steps, // steps
		            null, // preconditions
		            TestImportance.valueOf(importance), // importance
		            ExecutionType.AUTOMATED, // execution
		            new Integer(10), // order
		            null, // internalId
		            true, // checkDuplicatedName 
		            ActionOnDuplicate.BLOCK); // actionOnDuplicatedName
	  Log.info("Test Case "+ testcaseName + "Created Successfully");	
	} catch (TestLinkAPIException e) {
      Log.info("Test Case "+ testcaseName + "Unable to create due to follwing error " + e ); 
	}

	}
	
	
   
   public static void addToPlan(String projectName,String planName,String testCaseName) {
	   
	   TestLinkAPIClient api = new TestLinkAPIClient(DEVKEY, url);
	   try {
		api.addTestCaseToTestPlan(projectName, planName, testCaseName);
		Log.info("Added Successfullt to test plan");
	} catch (testlink.api.java.client.TestLinkAPIException e) {
		// TODO Auto-generated catch block
		Log.info("Unable to add the test case due to following error" + e);
	}
	   
   }
   
	
}	
	