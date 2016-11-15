package environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import common.Config;
import common.Log;

/**
 * Exposes methods to execute command on Unix & getting result back.
 * 
 * @author Administrator
 */
public class Unix {
	// JSCH variables
	private int timeOut = 2000;
	private static JSch jsch = new JSch();
	private static com.jcraft.jsch.Session jschSession;
	private static ch.ethz.ssh2.Session session;
	// Connection
	private static Connection connection;

	public static void initialize(String ip, String userName, String password,
			String port) throws IOException, JSchException {
		/* Create a connection instance */
		connection = new Connection(ip);
		connection.connect();
		boolean isAuthenticated;
		isAuthenticated = connection.authenticateWithPassword(userName,
				password);
		if (isAuthenticated == false) {
			Log.error("SSH Authentication failed | Host name: [" + ip + "] "
					+ "| User name: [" + userName + "] | " + "Password: ["
					+ password + "]");
		} else {
			Log.info("SSH Authentication successful");
			session = connection.openSession();
		}
		jschInitialize(ip, userName, password, port);
	}

	/**
	 * Set time out for command. Default time out will be 2 seconds
	 * 
	 * @param timeOutToBeSet
	 * 
	 * @return nothing
	 * @throws Exception
	 */
	public void setTimeOut(int timeOutToBeSet) {
		timeOut = timeOutToBeSet;
	}

	/**
	 * Execute the given command on the server without expecting any output In
	 * order to execute batch provide commands separated by semicolon (;)
	 * 
	 * @param command
	 * @param String
	 *            Separator
	 * @return nothing
	 * @throws Exception
	 */
	public void executeCommand(String command) throws Exception {
		session.execCommand(command);
		Log.info("SSH command [" + command + "] executed");
		// Ensure complete execution of command by waiting for 2 seconds
		Thread.sleep(timeOut);
		// Show exit status, if available (otherwise "null")
		Log.info("SSH ExitCode: " + session.getExitStatus());
	}

	/**
	 * Copy file on the host specified in initialization
	 * 
	 * @param fileName
	 * @param readWriteMode
	 * @param destinationDirectory
	 * @throws IOException
	 */
	public void copyFile(String fileName, String readWriteMode,
			String destinationDirectory) throws IOException {
		Log.info("Copying file " + fileName);
		SCPClient scpc = connection.createSCPClient();
		scpc.put(fileName, destinationDirectory, readWriteMode);
		Log.info("SSH | File [" + fileName + "] copied in dir " + "["
				+ destinationDirectory + "] with mode " + "[" + readWriteMode
				+ "]");
	}

	/**
	 * Close connection
	 */
	public void close() {
		try {
			/* Close the connection */
			if (jschSession.isConnected()) {
				jschSession.disconnect();
			}
			if (null != session) {
				session.close();
				connection.close();
			}

		} catch (Exception exception) {
			Log.error("Error closing connection");
		}
	}

	/**
	 * Execute SSH command using JSCH library
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public String executeCommandAndGetOutput(String command, String seperator)
			throws Exception {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Channel channel = null;
		channel = jschSession.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		((ChannelExec) channel).setPty(true);
		inputStream = channel.getInputStream();
		outputStream = channel.getOutputStream();
		channel.connect();
		outputStream.flush();
		Log.info("Command [" + command + "] is executed");
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String readLine = bufferedReader.readLine();
		String content = readLine;
		while ((readLine = bufferedReader.readLine()) != null) {
			content = content + seperator + readLine;
		}
		Log.info("Command output: [" + content + "]");
		return content;
	}

	/**
	 * Execute SSH command using JSCH library
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public String jschExecuteCommandWithoutOutput(String command) {
		String result = "";
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Channel channel = null;
		try {
			channel = jschSession.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			((ChannelExec) channel).setPty(true);
			inputStream = channel.getInputStream();
			outputStream = channel.getOutputStream();
			channel.connect();
			outputStream.flush();
			int exitStatus = channel.getExitStatus();
			Log.info("Command [" + command + "] is executed with exit code ["
					+ exitStatus + "]");
			if (null != inputStream)
				inputStream.close();
			if (null != outputStream)
				outputStream.close();

		} catch (Exception exception) {
			Log.error(exception.getMessage());
			result = exception.getMessage();
		} finally {
			if (null != channel)
				channel.disconnect();
		}
		return result;
	}

	/**
	 * Initialize JSCH utility
	 * 
	 * @param host
	 * @param user
	 * @param passwd
	 * @throws JSchException
	 */
	private static void jschInitialize(String host, String user, String passwd,
			String port) throws JSchException {
		jschSession = jsch.getSession(user, host, Integer.parseInt(port));
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		jschSession.setConfig(config);
		jschSession.setPassword(passwd);
		// session.setConfig("StrictHostKeyChecking", "no");
		jschSession.connect();
	}

	/**
	 * Copy file on the host specified in initialization
	 * 
	 * @param fileName
	 * @param readWriteMode
	 * @param destinationDirectory
	 * @throws IOException
	 */
	public void copyFileFromUnix(String filePath, String localDirectory)
			throws IOException {
		SCPClient scpc = connection.createSCPClient();
		if (!localDirectory.contains("\\") && !localDirectory.contains("/")) {
			localDirectory = "./";
		}
		Log.info("Copying file " + filePath + " to " + localDirectory);
		scpc.get(filePath, localDirectory);
		Log.info("File [" + filePath + "] copied in dir " + "["
				+ localDirectory + "]");
	}
}
