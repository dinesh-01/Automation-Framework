package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
	
	/**
	 * Returns total count of a matched string
	 * @param content
	 * @param value
	 * @return
	 * 
	 */
	
	public static int totalCountOfString(String content, String value) {

		 int Count = 0;
		 Pattern pattern = Pattern.compile(value);
		 Matcher matcher = pattern.matcher(content);
		 
		 Log.info(value);
		 
		 while (matcher.find()) {
			 Log.info(matcher.toString());
 		      Count++;
		 }
		 
		return Count;
		
	}
	
	
	/**
	 * Returns number from string
	 * @param content
	 * @return int
	 * 
	 */
	 
	public static String removeNumberFromString(String content) {
		
		String digits = content.replaceAll("[^0-9]", "");
		return digits;
	
	}
	

}
