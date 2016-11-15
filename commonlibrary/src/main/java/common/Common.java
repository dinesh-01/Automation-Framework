package common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 * Update 1
 * Exposes methods which are very common & generic like generating random
 * number, getting current date & time.
 * 
 * @author Administrator
 * 
 */
public class Common {

	/**
	 * @param valueToCheck
	 *            - string to check
	 * @param validateInString
	 *            - string to check into
	 * @return returns true if string exists in provided string
	 */
	public boolean checkIfStringExistsIn(String valueToCheck,
			String validateInString) {
		if (validateInString.contains(valueToCheck)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param valueToCheck
	 *            - string to check
	 * @param getIndexInString
	 *            - string to check into
	 * @return Returns the index of first occurrence of string
	 */
	public int getIndexOf(String valueToCheck, String getIndexInString) {
		return getIndexInString.indexOf(valueToCheck);
	}

	/**
	 * @param firstString
	 * @param secondString
	 * @return true if strings are equal, considers case
	 */
	public boolean checkIfStringAreEqual(String firstString, String secondString) {
		if (firstString.equals(secondString)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param firstString
	 * @param secondString
	 * @return returns true if strings are equal, ignores case
	 */
	public boolean checkIfStringAreEqualIgnoreCase(String firstString,
			String secondString) {
		if (firstString.equalsIgnoreCase(secondString)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param String as array 
	 * @return false if array contains unequal elemets else true
	 */
	
	public boolean checkIfStringAreEqualInArray(String[] args) {
		
		if(Arrays.asList(args).contains("false")) {
			return false;
		}else{
			return true;
		}

	}

	/**
	 * 
	 * @param inputString
	 * @param startIndex
	 * @param endIndex
	 * @return returns the sub string from a string
	 */
	public String getSubString(String inputString, int startIndex, int endIndex) {
		if (startIndex < 0)
			return inputString;
		else
			return inputString.substring(startIndex, endIndex);
	}
	
	
	/**
	 * 
	 * @param inputstring
	 * @param separator
	 * @param value
	 * @return
	 */
	
	
	public String splitString(String inputstring,String separator, int value)
	{
		
		String[] searchname = inputstring.split(separator);
		return searchname[value];
		
	}
	
	
	
	
	
	
	
	
	/**
	 * Get Substring by another String
	 * @param inputString
	 * @param start
	 * @param end
	 * @return result
	 */
	
	public String getSubStringByString(String inputString, String start, String end) {
		
		String result = StringUtils.substringBetween(inputString, start, end);
		return result;
		
	}

	/**
	 * 
	 * @param inputSting
	 * @return get length of string
	 */
	public int getStringLength(String inputSting) {
		return inputSting.length();
	}

	/**
	 * @return get current date time stamp in format formats
	 */
	public String getCurrentDateTimeStamp(String formats) {
		
		Date date = new Date();
		String showFormat;
		
		switch (formats) {
		case "dd-MM-yyyy HH-mm-ss":
			showFormat = "dd-MM-yyyy HH-mm-ss";
			break;
		case "yMMdd":
			showFormat = "yMMdd";
			break;
		case "yMMddHHmmss":
			showFormat = "yMMddHHmmss";
			break;
		case "dd-MM-yyyy-HH-mm-ss":
			showFormat = "dd-MM-yyyy-HH-mm-ss";
			break;
		case "yyyy/MM/dd HH:mm:ss":
			showFormat = "yyyy/MM/dd HH:mm:ss";
			break;	
		default:
			showFormat = "Y-m-d";	
		}
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(showFormat);
		return dateFormat.format(date);
	
	}

	/** Returns a random number between 1 and 10000 */
	public int getRandomNumber(int min, int max) {

		Random random = new Random();
		int randomNumber = random.nextInt((max - min) + 1) + min;
		return randomNumber;
	}
	
	
	
	
	/** Returns a random IP */
	public String getRandomIP() {
		
         Random r = new Random();
         String randomIp =  r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
         return randomIp;
         
    }
	
	
	/**
	 * 
	 * @param String as array 
	 * @return random value from an array
	 */
	
	public String  getRandomValueFromArray(String[] args) {
		
		int rnd = new Random().nextInt(args.length);
	    return args[rnd];

	}

	

	/**
	 * @param sizeOfString
	 *            - size of string to generate.
	 * @param type
	 *            - numeric,alphanumeric,alpha
	 * @return random string of provided type of provided length.
	 * @throws Exception
	 */
	public String getRandomString(String sizeOfString, String type)
			throws Exception {
		StringBuffer buffer = new StringBuffer();
		String characters = "";
		int length = Integer.parseInt(sizeOfString);

		switch (type.toUpperCase()) {
		case "ALPHA":
			characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			break;
		case "ALPHANUMERIC":
			characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
			break;
		case "NUMERIC":
			characters = "1234567890";
			break;
		default:
			characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		}

		int charactersLength = characters.length();
		for (int i = 0; i < length; i++) {
			double index = Math.random() * charactersLength;
			buffer.append(characters.charAt((int) index));
		}
		Log.info("Generating random string of " + type + " type and of "
				+ length + " characters length.");
		return buffer.toString();
	}

	/**
	 * @param number1
	 * @param operator
	 * @param number2
	 * @return Returns calculation of two integers
	 */
	public int getResultOfCalculation(int number1, char operator, int number2) {
		switch (operator) {
		case '+':
			return (number1 + number2);

		case '-':
			return (number1 - number2);

		case '*':
			return (number1 * number2);

		case '/':
			return (number1 / number2);

		}
		Log.error("Invalid operator provided");
		return 0;
	}
	
	
	/**
	 * 
	 * @param message
	 * @param algorithm // algorithm can be "MD5", "SHA-1", "SHA-256"
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	
	
	public String hashString(String message, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
	       
	        MessageDigest digest = MessageDigest.getInstance(algorithm);
	        byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
	        
	        StringBuffer stringBuffer = new StringBuffer();
	        for (int i = 0; i < hashedBytes.length; i++) {
	            stringBuffer.append(Integer.toString((hashedBytes[i] & 0xff) + 0x100, 16)
	                    .substring(1));
	        }
	        
	        return stringBuffer.toString();
	        
	        
	}
	
	/**
	 * Change string to upper case
	 * @param message
	 * @return
	 */
	
	public String upperCase(String message) {
		
		message = message.toUpperCase();
		return message;
		
	}
	
	/**
	 * Get File Path
	 * @param fileName
	 * @return
	 */
	
	public String getPath(String fileName,String pathType) {
		
		File f = new File(fileName);
		String path = null;
		
		switch (pathType) {
		    case "fullpath":  
			path = f.getAbsolutePath();
			break;
		}
		
		return path;
		
	}
	
	/**
	 * @param number1
	 * @param operator
	 * @param number2
	 * @return Returns comparison result of two operator
	 * 
	 */	
	
	 public static boolean comparisonResult (int number1, String operator, int number2) {
		
		 boolean result = false;
		 
		 
		 
		 switch (operator) {
		 
			case "<":
				result = (number1 < number2) ?  true : false;
			case ">":
				result = (number1 > number2) ?  true : false;
			case "<=":
				result = (number1 <= number2) ? true : false;
			case ">=":
				result = (number1 >= number2) ? true : false;
			case "equal":
				result = (number1 == number2) ? true : false;
			case "!=":
				result = (number1 != number2) ? true : false;
         }
			
		 Log.error("Invalid operator provided");
			
		 return result;
		 
	 }
	 
	 /**
		 * @param String 
		 * @return returns string data
		 * 
		 */
	 
	public static String getTrim(String value) {
		value = value.trim();
		return value;
	}
}
