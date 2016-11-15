package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import tools.Fitnesse;

////import com.sun.jna.platform.FileUtils;
/**
 * Exposes methods related to configuration initialization
 * 
 * @author Administrator
 * 
 */
public class Config {

	public static String AutomationExecutionMode = "Fitnesse";
	public static String TestResultFolder = "C:\\TestResults";
	public static String AutomationDepandacies = "Dependancies/";
	public static String AutomationDriverXML = "./Driver.xml";
	public static String ProductURL = "";
	public static String DatabaseURL = "";
	public static String DatabaseUser = "";
	public static String DatabasePassword = "";
	public static String Browser = "firefox";
	public static String Locale = "en-us";
	// private static String PropertiesFilePath = "";
	public static String SshUserName = "root";
	public static String SshPassword = "pass@123";
	public static String FitNesseTags = "";
	public static boolean runCodeCoverage = false;
	public static boolean UpdateTestResults = false;

	public static void Initialize() throws Exception {
		if (!getConfigParametersFromFitnesse()) {
			if (!getJVMArguments()) {
				getParametersFromPropertiesFile();
			}
		}
		File driverXML = new File("./Driver.xml");
		AutomationDriverXML = driverXML.getAbsolutePath();
		Log.info("STAF STAX Driver XML path:" + AutomationDriverXML);
		// Set logger in new directory.
		// setLogger();
		Log.info("Product URL:" + ProductURL);
		Log.info("Test Result Folder:" + TestResultFolder);
		Log.info("Database URL:" + DatabaseURL);
		Log.info("Database User:" + DatabaseUser);
		Log.info("Database Password:" + DatabasePassword);
		Log.info("Locale:" + Locale);
		Log.info("Browser:" + Browser);
		Log.info("SshUserName:" + SshUserName);
		Log.info("SshPassword:" + SshPassword);
		Log.info("FitNesseTags:" + FitNesseTags);
		Log.info("UpdateTestResults:" + UpdateTestResults);
	}

	@SuppressWarnings("unused")
	private static void setLogger() {
		// TODO Auto-generated method stub
		String parent = Log.logInstance.getName();
		// Logger logger = Logger.getLogger(loggerName);
		String loggerName = "New." + parent + ".TestRun_FileAppender";
		// Create Logging File Appender
		RollingFileAppender fileApp = new RollingFileAppender();
		fileApp.setName(loggerName);
		fileApp.setFile(Config.TestResultFolder + "\\log.log");
		fileApp.setLayout(new PatternLayout(
				"%d{yyyy-MM-dd HH:mm:ss} %-5p %%F:%L - %m%n"));
		fileApp.setThreshold(Level.INFO);
		fileApp.setMaxBackupIndex(1);
		fileApp.setMaxFileSize("10MB");
		fileApp.setAppend(true);

		fileApp.activateOptions();
		Log.logInstance.addAppender(fileApp);
		Log.info("Additivity is set to: " + Log.logInstance.getAdditivity());
		Log.info("Setting log file location to new Test folder Location:"
				+ Config.TestResultFolder);
	}

	private static boolean getConfigParametersFromFitnesse()
			throws IOException, FileNotFoundException {
		boolean result = false;
		Log.info("Reading configuration from Fitnesse....");
		ProductURL = Fitnesse.getSettingsFieldValue("ProductURL");
		DatabaseURL = Fitnesse.getSettingsFieldValue("DatabaseURL") == null ? ""
				: Fitnesse.getSettingsFieldValue("DatabaseURL");
		DatabaseUser = Fitnesse.getSettingsFieldValue("DatabaseUser") == null ? ""
				: Fitnesse.getSettingsFieldValue("DatabaseUser");
		DatabasePassword = Fitnesse.getSettingsFieldValue("DatabasePassword") == null ? ""
				: Fitnesse.getSettingsFieldValue("DatabasePassword");
		Browser = Fitnesse.getSettingsFieldValue("Browser") == null ? ""
				: Fitnesse.getSettingsFieldValue("Browser");
		Locale = Fitnesse.getSettingsFieldValue("Locale") == null ? ""
				: Fitnesse.getSettingsFieldValue("Locale");
		TestResultFolder = Fitnesse.getSettingsFieldValue("TestResultFolder") == null ? ""
				: Fitnesse.getSettingsFieldValue("TestResultFolder");
		FitNesseTags = Fitnesse.getSettingsFieldValue("FitNesseTags") == null ? ""
				: Fitnesse.getSettingsFieldValue("FitNesseTags");
		runCodeCoverage = Fitnesse.getSettingsFieldValue("runCodeCoverage") == null ? false
				: Boolean.parseBoolean(Fitnesse
						.getSettingsFieldValue("runCodeCoverage"));
		UpdateTestResults = Fitnesse.getSettingsFieldValue("updateTestResults") == null ? false
				: Boolean.parseBoolean(Fitnesse
						.getSettingsFieldValue("updateTestResults"));
		if (null != ProductURL) {
			result = true;
		} else {
			Log.info("Configuration not found in Fitnesse..");
		}
		return result;
	}

	private static boolean getJVMArguments() throws IOException,
			FileNotFoundException {
		boolean result = false;
		Log.info("Reading configuration from environment variable....");
		ProductURL = System.getProperty("ProductURL");
		DatabaseURL = System.getProperty("DatabaseURL") == null ? "" : System
				.getProperty("DatabaseURL");
		DatabaseUser = System.getProperty("DatabaseUser") == null ? "" : System
				.getProperty("DatabaseUser");
		DatabasePassword = System.getProperty("DatabasePassword") == null ? ""
				: System.getProperty("DatabasePassword");
		Browser = System.getProperty("Browser") == null ? "" : System
				.getProperty("Browser");
		Locale = System.getProperty("Locale") == null ? "" : System
				.getProperty("Locale");
		TestResultFolder = System.getProperty("TestResultFolder") == null ? ""
				: System.getProperty("TestResultFolder");
		FitNesseTags = System.getProperty("FitNesseTags") == null ? "" : System
				.getProperty("FitNesseTags");
		runCodeCoverage = System.getProperty("runCodeCoverage") == null ? false
				: Boolean.parseBoolean(System.getProperty("runCodeCoverage"));
		UpdateTestResults = System.getProperty("updateTestResults") == null ? false
				: Boolean.parseBoolean(System.getProperty("updateTestResults"));

		if (null != ProductURL) {
			result = true;
		} else {
			Log.info("JVM Arguments not found..");
		}
		return result;
	}

	private static boolean getParametersFromPropertiesFile() throws IOException {
		Log.info("Reading confifuration from properties file");
		InputStream streamPropertiesFile = Config.class
				.getResourceAsStream("/automation.properties");
		if (null == streamPropertiesFile) {
			Log.info("Properties file not found.");
			return false;
		}
		Properties prop = new Properties();
		prop.load(streamPropertiesFile);
		// get the property value and print it out
		ProductURL = prop.getProperty("ProductURL");
		TestResultFolder = prop.getProperty("TestResultFolder");
		// Read database URL
		DatabaseURL = prop.getProperty("DatabaseURL");
		DatabaseUser = prop.getProperty("DatabaseUser");
		DatabasePassword = prop.getProperty("DatabasePassword");
		Locale = prop.getProperty("Locale");
		Browser = prop.getProperty("Browser");
		SshUserName = prop.getProperty("SshUserName");
		SshPassword = prop.getProperty("SshPassword");
		FitNesseTags = prop.getProperty("FitNesseTags");
		runCodeCoverage = Boolean.parseBoolean(prop
				.getProperty("runCodeCoverage"));
		UpdateTestResults = Boolean.parseBoolean(prop
				.getProperty("updateTestResults"));
		return true;
	}
	
	
}
