package common;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import environment.FileUtil;

/**
 * Exposes methods to initialize logger which would write logs to: 1. Standard
 * I/O 2. Fitnesse 3. Log files
 */
public class Log {

	static Logger logInstance = Logger.getLogger("file");
	public static String appendCharacter = "";

	/**
	 * Log error messages
	 * 
	 * @param message
	 *            - error message to log
	 */
	public static void error(String message) {
		if (!appendCharacter.equals("")) {
			message = appendCharacter + message;
		}
		logInstance.error(message);
	}

	/**
	 * Log information messages
	 * 
	 * @param message
	 *            - message to log
	 */
	public static void info(String message) {
		if (!appendCharacter.equals("")) {
			message = appendCharacter + message;
		}
		logInstance.info(message);
	}

	/**
	 * Log debug information
	 * 
	 * @param message
	 *            - message to log
	 */
	public static void debug(String message) {
		if (!appendCharacter.equals("")) {
			message = appendCharacter + message;
		}
		logInstance.debug(message);
	}

	/**
	 * delete all log files.
	 * @throws IOException
	 */
	public static void cleanUp() throws IOException {
		File[] logFiles = FileUtil.findFilesWithExtension(".", "log");
		FileUtil.deleteFiles(logFiles);
	}

	/**
	 * Get log file page.
	 * @return directory of log files.
	 */
	public static String getLogFilePath() {
		Logger logger = Logger.getLogger("rootLogger"); // Defining the Logger
		@SuppressWarnings("static-access")
		FileAppender appender = (FileAppender) logger.getRootLogger()
				.getAppender("file");
		String directory = "";
		File file = new File(appender.getFile());
		if (null != file) {
			directory = file.getPath();
			Log.info("Log directory path is: " + directory);
		}
		return directory;
	}
}
