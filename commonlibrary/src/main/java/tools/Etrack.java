package tools;

import web.Http;
import web.Json;

/**
 * This class exposes functions related to Etrack
 */
public class Etrack {

	private static String authURL = "";
	private static String url = "";
	private static String eTrackUserName = "";
	private static String eTrackPassword = "";

	public static void initialize(String authenticationUrl,String eTrackUrl,String userName,String password)
	{
		authURL = authenticationUrl;
		url = eTrackUrl;
		eTrackUserName = userName;
		eTrackPassword = password;
	}
	
	public static String getDefectDetails(String defectId, String fieldName)
			throws Exception {
		String result = "";
		url = url + "/incidents/" + defectId;
		Http http = new Http();
		http.setServiceURL(authURL);
		http.addFormParameter("j_username", eTrackUserName);
		http.addFormParameter("j_password", eTrackPassword);
		http.addFormParameter("_etrack_remember_me", "on");
		String response = http.getResponse("application/x-www-form-urlencoded",
				"post", "");
		http.setServiceURL(url);
		response = http.getResponse("application/json", "get", "");
		result = Json.getValueFromJSON(response, fieldName);
		return result;
	}
	
	
}
