package environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import autoitx4java.AutoItX;

import com.jacob.com.LibraryLoader;

import web.Selenium;
import common.Log;

public class Windows {

	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /IM ";
	private static AutoItX autoIt = null;

	/**
	 * Initializes the AutoIt
	 */
	public static void initialize() {
		String jacobDllVersionToUse;
		if (System.getProperty("sun.arch.data.model").contains("32")) {
			jacobDllVersionToUse = "jacob-1.18-M2-x86.dll";
		} else {
			jacobDllVersionToUse = "jacob-1.18-M2-x64.dll";
		}
		String relativePath = ".." + File.separator + "commonlibrary"
				+ File.separator + "target" + File.separator + "classes"
				+ File.separator + jacobDllVersionToUse;
		File file = new File(relativePath);
		Log.info("Jacob DLL path: " + file.getAbsolutePath());
		System.setProperty(LibraryLoader.JACOB_DLL_PATH, file.getAbsolutePath());
		autoIt = new AutoItX();
	}

	/**
	 * Run application at given path
	 * 
	 * @param applicationExecutablePath
	 */
	public static void startApplication(String applicationExecutablePath) {
		Log.info("Starting application: " + applicationExecutablePath);
		autoIt.run(applicationExecutablePath);
	}

	/**
	 * Set text on application using AutoIT
	 * 
	 * @param title
	 *            The title/hWnd/class of the window to access
	 * @param text
	 *            The text of the window to access
	 * @param controlID
	 *            The control to interact with
	 * @param valueToSet
	 *            The new text to be set into the control
	 * @throws InterruptedException
	 */
	public static void setText(String title, String text, String controlID,
			String valueToSet) throws InterruptedException {
		Log.info("Setting value " + valueToSet + " for " + controlID);
		boolean isTextSet = autoIt.ControlSetText(title, text, controlID,
				valueToSet);
		int count = 0;
		while (!isTextSet) {
			isTextSet = autoIt.ControlSetText(title, text, controlID,
					valueToSet);
			java.util.concurrent.TimeUnit.MILLISECONDS.sleep(100);
			Log.info("Waiting for set text");
			if (count == 20) {
				break;
			}
			count++;
		}
	}

	/**
	 * Wait for Windows to appear
	 * 
	 * @param title
	 * @param text
	 * @param timeout
	 */
	public static void waitForWindow(String title, String text, int timeout) {

		autoIt.winWait(title, text, timeout);

	}

	/**
	 * Get the focus on application using AutoIT
	 * 
	 * @param title
	 *            The title/hWnd/class of the window to access
	 * @param text
	 *            The text of the window to access
	 * @param controlID
	 *            The control to interact with
	 *
	 * @throws InterruptedException
	 */
	public static void setFocus(String title, String text, String controlID)
			throws InterruptedException {
		Log.info("Setting focus on " + controlID);
		// autoIt.controlGetFocus(Title, text);
		boolean setFocus = autoIt.controlFocus(title, text, controlID);
		int count = 0;
		while (!setFocus) {
			setFocus = autoIt.controlFocus(title, text, controlID);
			Log.info("Waiting to set focus");
			java.util.concurrent.TimeUnit.MILLISECONDS.sleep(100);
			if (count == 20) {
				break;
			}
			count++;
		}
	}

	/**
	 * Wait for specified time to appear window
	 * 
	 * @param seconds
	 */
	public void wait(int seconds) {
		Selenium.wait(seconds);
	}

	/**
	 * Click on application using AutoIT
	 * 
	 * @param title
	 *            The title/hWnd/class of the window to access
	 * @param text
	 *            The text of the window to access
	 * @param controlID
	 *            The control to interact with
	 * @param button
	 *            The button to click, "left", "right", "middle", "main",
	 *            "menu", "primary", "secondary". Default is the left button.
	 * @throws InterruptedException
	 */
	public static void click(String title, String text, String controlID,
			String button) throws InterruptedException {
		Log.info("Click on ID " + controlID);
		boolean isClicked = autoIt.controlClick(title, text, controlID, button);
		int count = 0;
		while (!isClicked) {
			isClicked = autoIt.controlClick(title, text, controlID, button);
			java.util.concurrent.TimeUnit.MILLISECONDS.sleep(100);
			Log.info("Waiting for click");
			if (count == 20) {
				break;
			}
			count++;
		}
	}

	/**
	 * Stop application at given path
	 * 
	 * @param title
	 *            The title/hWnd/class of the window to close
	 */
	public static void closeWindow(String title) {
		Log.info("Stopping application: " + title);
		autoIt.winClose(title);
	}

	/**
	 * Check if process is running
	 * 
	 * @param processName
	 *            - name of the process.
	 * @return - true if process is running & false otherwise.
	 * @throws Exception
	 */
	public static boolean isProcessRunning(String processName) throws Exception {
		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			Log.info(line);
			if (line.contains(processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Kills the running process.
	 * 
	 * @param processName
	 *            - process name
	 * @throws Exception
	 */
	public static void killProcess(String processName) throws Exception {
		Runtime.getRuntime().exec(KILL + processName);
	}

	/**
	 * Navigates to the working directory and executes the command in the
	 * windows command-line editor.
	 * 
	 * @param workingDirectory
	 *            the working directory where the <code>command</code> needs to
	 *            be executed
	 * @param command
	 *            the windows command to be executed
	 * @param separator
	 *            this is appended at the end of each line of the output
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String executeCommand(String workingDirectory,
			String command, String separator) throws IOException,
			InterruptedException {
		List<String> commands = new ArrayList<String>();
		commands.add("cmd");
		commands.add("/c");
		commands.add(command);
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		File outPutfile = new File("./output.txt");
		outPutfile.deleteOnExit();
		processBuilder.redirectOutput(outPutfile);
		processBuilder.redirectError(outPutfile);
		if (!workingDirectory.isEmpty()) {
			File file = new File(workingDirectory);
			processBuilder.directory(file);
		}
		Process process = processBuilder.start();
		process.waitFor();
		// process.wait(2000);
		Log.info("Command [" + command + "] is executed.");
		String response = "";
		if (separator.isEmpty()) {
			response = FileUtils.readFileToString(outPutfile);
		} else {
			List<String> lines = FileUtils.readLines(outPutfile);
			boolean firstTime = true;
			for (String line : lines) {
				if (firstTime) {
					response = line;
					firstTime = false;
				} else {
					response = response + separator + line;
				}
			}
		}
		Log.info("Fetching response from the command :: " + response);
		return response;
	}

	/**
	 * Execute command on remote machine
	 * 
	 * @param ipAddress
	 *            - IP Address of machine
	 * @param userName
	 *            - user name
	 * @param password
	 *            - password
	 * @param command
	 *            - command to execute
	 * @param separator
	 *            - Separator to be used to separate command output
	 * @return Command output post execution
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String executeCommandOnRemoteMachine(String ipAddress,
			String userName, String password, String command, String separator)
			throws IOException, InterruptedException {
		String workingDirectory = "../utilities/PSTools";
		String cmd = "PsExec ";
		if (userName.isEmpty()) {
			cmd = cmd + "\\\\" + ipAddress + " " + command;
		} else {
			cmd = cmd + "\\\\" + ipAddress + " -accepteula -u " + userName
					+ " -p " + password + " " + command;
		}
		return executeCommand(workingDirectory, cmd, separator);
	}

	/**
	 * Execute the given batch file
	 * 
	 * @param result
	 * @param batScriptPath
	 * @return
	 * @throws IOException
	 */
	public static String executeBatchFile(String batScriptPath)
			throws IOException {
		String command = "cmd /c start /wait " + batScriptPath;
		return execute(command);
	}

	/**
	 * Execute the given powershell file
	 * 
	 * @param powershellPSFile
	 * @return
	 * @throws IOException
	 */
	public static String executePowershellFile(String powershellPSFile)
			throws IOException {
		String command = "cmd /c powershell -ExecutionPolicy RemoteSigned -noprofile -noninteractive -file "
				+ powershellPSFile;
		return execute(command);
	}

	private static String execute(String command) throws IOException {

		StringBuffer sbInput = new StringBuffer();
		StringBuffer sbError = new StringBuffer();

		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command);
		proc.getOutputStream().close();
		InputStream inputstream = proc.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

		String line;
		while ((line = bufferedreader.readLine()) != null) {
			sbInput.append(line + "\n");
		}

		inputstream = proc.getErrorStream();
		inputstreamreader = new InputStreamReader(inputstream);
		bufferedreader = new BufferedReader(inputstreamreader);
		while ((line = bufferedreader.readLine()) != null) {
			sbError.append(line + "\n");
		}

		if (sbError.length() > 0) {
			return sbError.toString();
		}

		return sbInput.toString();

	}

	/**
	 * Copy file to and from remote windows machine e.g. Source: C:\file.txt to
	 * destination: \\10.211.64.119\C$ Source: \\10.211.64.119\C$\file.txt to
	 * destination: C:\ Source: C:\temp\*.* to destination
	 * \\10.211.64.119\C$\temp
	 * 
	 * @param sourceFile
	 *            - source file to copy
	 * @param destination
	 *            - destination folder to copy files
	 * @param userName
	 *            - user name for remote machine
	 * @param password
	 *            - password
	 * @throws IOException
	 */
	public static String copyFile(String sourceFile, String destination,
			String userName, String password, String recursive)
			throws IOException {

		if (sourceFile.endsWith("\\")) {
			sourceFile = sourceFile.substring(0, sourceFile.length() - 1);
		}
		if (destination.endsWith("\\")) {
			destination = destination.substring(0, destination.length() - 1);
		}
		Log.info("Copying file '" + sourceFile + "' to destination: "
				+ destination);
		List<String> batchFileContents = new ArrayList<String>();
		batchFileContents.add("Import-Module bitstransfer");
		batchFileContents.add("$sourcePath = \"" + sourceFile + "\"");
		batchFileContents.add("$destPath = \"" + destination + "\"");

		if (userName.length() > 0) {
			batchFileContents.add("$username=\"" + userName + "\"");
			batchFileContents.add("$password= \"" + password + "\"");
			batchFileContents
					.add("$secstr = New-Object -TypeName System.Security.SecureString");
			batchFileContents
					.add("$password.ToCharArray() | ForEach-Object {$secstr.AppendChar($_)}");
			batchFileContents
					.add("$cred = new-object -typename System.Management.Automation.PSCredential -argumentlist $username, $secstr");

			if (recursive.toLowerCase().equals("true")
					|| recursive.toLowerCase().equals("yes")) {
				batchFileContents.add("xcopy.exe /T /E \"" + sourceFile
						+ "\\*.*\" \"" + destination + "\\*.*\" /Y ");
				batchFileContents
						.add("Get-ChildItem -Path $sourcePath -Recurse | ?{$_.PSisContainer} | foreach {$spath = $_.FullName.Remove(0,$sourcePath.Length+1); Start-BitsTransfer -Source $sourcePath\\$spath\\*.* -Destination $destPath\\$spath  -Credential $cred}");
				batchFileContents
						.add("Start-BitsTransfer $sourcePath\\*.* $destPath  -Credential $cred");
			} else {
				batchFileContents
						.add("Start-BitsTransfer -Source $sourcePath -Destination $destPath -Credential $cred");
			}
		} else {
			if (recursive.toLowerCase().equals("true")
					|| recursive.toLowerCase().equals("yes")) {
				batchFileContents.add("xcopy.exe /T /E \"" + sourceFile
						+ "\\*.*\" \"" + destination + "\\*.*\" /Y ");
				batchFileContents
						.add("Get-ChildItem -Path $sourcePath -Recurse | ?{$_.PSisContainer} | foreach {$spath = $_.FullName.Remove(0,$sourcePath.Length+1); Start-BitsTransfer -Source $sourcePath\\$spath\\*.* -Destination $destPath\\$spath}");
				batchFileContents
						.add("Start-BitsTransfer $sourcePath\\*.* $destPath ");
			} else {
				batchFileContents
						.add("Start-BitsTransfer -Source $sourcePath -Destination $destPath");
			}
		}

		File file = new File("C:\\copyFiles.ps1");
		if (file.exists()) {
			file.delete();
		}
		FileUtils.writeLines(file, batchFileContents);
		return Windows.executePowershellFile(file.getAbsolutePath());
	}
}