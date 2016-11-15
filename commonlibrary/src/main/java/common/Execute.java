package common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

/**
 * Exposes methods to execute automation & common library functions from command
 * line.
 * 
 * @author Administrator
 * 
 */
public class Execute {

	private static final String DEFAULT_PACKAGE = "common.";

	public static void main(String[] args) throws Exception {
		Log.info("Inside Execute.main..");
		if (args != null && args.length >= 2) {
			executeCommand(args);
			return;
		}
	}

	private static void executeCommand(String[] args)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Log.info("Command line arguments found: " + Arrays.toString(args));
		String className = args[0];
		Log.info("Class name: " + className);
		String methodName = args[1];
		Log.info("Method name: " + methodName);
		final int argsLength = args.length;
		Object[] parameterValues = null;
		if (argsLength > 2) {
			parameterValues = new String[argsLength - 2];
			for (int i = 2; i < argsLength; i++) {
				parameterValues[i - 2] = args[i];
			}
		}
		Log.info("Method parameter values are : "
				+ Arrays.toString(parameterValues));
		Class<?> cls;
		try {
			cls = Class.forName(className);
		} catch (ClassNotFoundException classNotFoundException) {
			cls = Class.forName(DEFAULT_PACKAGE + className);
		}
		Log.info("Object of class [" + className + "] is created");
		Object obj = cls.newInstance();
		final Method[] declaredMethods = cls.getDeclaredMethods();
		for (Method method : declaredMethods) {
			final String methodNameDynamic = method.getName().toLowerCase();
			if (methodNameDynamic.equals(methodName.toLowerCase())) {
				if (parameterValues != null && parameterValues.length != 0) {
					method.invoke(obj, parameterValues);
				} else {
					method.invoke(obj);
				}
				Log.info("Method [" + methodName + "] is executed on class ["
						+ className + "] ");
				break;
			}
		}
	}

	@SuppressWarnings("unused")
	private static void createTestResultFolder() throws IOException {
		File file = new File(Config.TestResultFolder);
		if (file.exists()) {
			org.apache.commons.io.FileUtils.cleanDirectory(file);
			file.delete();
		}
		if (file.mkdir()) {
			// Config.TestResultFolder = newFolder;
			Log.info("Creating TestResult folder:" + Config.TestResultFolder);
		} else {
			FileUtils.forceMkdir(file);
		}

	}

}