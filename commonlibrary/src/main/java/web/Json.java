package web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.Log;

public class Json {

	/**
	 * Extracts string value from JSON
	 * 
	 * @param json
	 * @param field
	 *            , Input example: student.address.city
	 * @return
	 */
	public static String getValueFromJSON(String json, String field) {
		String[] objectHierarchy = field.split("\\.");
		JSONObject jsonObject;
		String value = "";
		try {
			// json = json.substring(1, json.length()-1);
			Log.info(json);
			jsonObject = new JSONObject(json);
			for (int i = 0; i < objectHierarchy.length; i++) {
				String objectStr = objectHierarchy[i];
				if (i == objectHierarchy.length - 1) {
					value = jsonObject.getString(objectStr);
					Log.info(value);
				} else {
					jsonObject = jsonObject.getJSONObject(objectStr);
				}
			}
		} catch (Exception exception) {
			Log.error(exception.toString());
		}

		return value;
	}

	/**
	 * Extracts JSON Object from JSON containing more than one JSON objects
	 * 
	 * @param json
	 * @param object
	 *            index Example : o,1,2,...n etc.
	 * @return JSON object at provided index
	 */
	public static JSONObject getJSONObjectFromJsonArray(String json, int index) {
		JSONArray jSONArray;
		JSONObject jSONObject = null;
		try {
			jSONArray = new JSONArray(json);
			jSONObject = (JSONObject) jSONArray.get(index);
		} catch (JSONException exception) {
			// exception.printStackTrace();
			Log.error(exception.getMessage());
		}
		return jSONObject;
	}

	/**
	 * Extracts specified type of value from JSON
	 * 
	 * @param json
	 * @param field
	 * @param type
	 *            example int.class, String.class etc
	 * @return string value of specified type of field
	 */
	public static String getValueFromJSON(String json, String field,
			Class<?> type) {
		String[] objectHierarchy = field.split("\\.");
		String strValue = "";
		JSONObject jsonObject;

		try {
			// Log.Message(json, LogLevel.INFO);
			jsonObject = new JSONObject(json);

			for (int i = 0; i < objectHierarchy.length; i++) {
				String objectStr = objectHierarchy[i];

				if (i == objectHierarchy.length - 1) {
					String className = type.getName();

					switch (className) {
					case "int":
						int intValue = jsonObject.getInt(field);
						strValue = Integer.toString(intValue);
						break;
					case "boolean":
						boolean boolValue = jsonObject.getBoolean(field);
						strValue = Boolean.toString(boolValue);
						break;
					case "double":
						double floatValue = jsonObject.getDouble(field);
						strValue = Double.toString(floatValue);
						break;
					case "java.lang.String":
						strValue = jsonObject.getString(field);
						break;
					}
				} else {
					jsonObject = jsonObject.getJSONObject(objectStr);
				}
			}
		} catch (Exception exception) {
			Log.error(exception.toString());
		}
		Log.info(strValue);
		return strValue;
	}

	/**
	 * Extracts specified type of value from JSON based on the data type
	 * specified
	 * 
	 * @param json
	 *            -Actual json
	 * @param field
	 *            -Name of the field to be extracted example:[0].datamodelID.
	 *            Here square bracket indicates Array. Above example will search
	 *            for object on 0th index in array. Then it will return value of
	 *            field 'datamodelID' of the object.
	 * @param type
	 *            example int,boolean,double,java.lang.String etc
	 * @return string value of specified type of field
	 */
	public static String getValueFromJSONByType(String json, String field,
			String className) {
		json = json.trim();
		String[] objectHierarchy = field.split("\\.");
		String strValue = "";
		JSONObject jsonObject;
		JSONArray jSONArray;
		int i = 0;

		try {
			// Log.Message(json, LogLevel.INFO);
			if (json.startsWith("[")) {
				jSONArray = new JSONArray(json);
				String objectStr = objectHierarchy[i++];
				final String[] splitWithOpenBracket = objectStr.split("\\[");
				final String[] splitWithCloseBracket = splitWithOpenBracket[1]
						.split("\\]");
				int arrayIndex = Integer.parseInt(splitWithCloseBracket[0]);
				jsonObject = jSONArray.getJSONObject(arrayIndex);
			} else {
				jsonObject = new JSONObject(json);
			}

			for (; i < objectHierarchy.length; i++) {
				String objectStr = objectHierarchy[i];
				if (objectStr.contains("[")) {
					final String[] splitWithOpenBracket = objectStr
							.split("\\[");
					final String[] splitWithCloseBracket = splitWithOpenBracket[1]
							.split("\\]");
					String arrayName = splitWithOpenBracket[0];
					int arrayIndex = Integer.parseInt(splitWithCloseBracket[0]);
					JSONArray jsonArray = jsonObject.getJSONArray(arrayName);
					Object valueFromArray = jsonArray.get(arrayIndex);
					if (valueFromArray instanceof JSONObject) {
						jsonObject = (JSONObject) valueFromArray;
					} else if (valueFromArray instanceof String) {
						return (String) valueFromArray;
					}
					continue;
				}
				if (i == objectHierarchy.length - 1) {

					switch (className) {
					case "int":
						int intValue = jsonObject.getInt(objectStr);
						strValue = Integer.toString(intValue);
						break;
					case "boolean":
						boolean boolValue = jsonObject.getBoolean(objectStr);
						strValue = Boolean.toString(boolValue);
						break;
					case "double":
						double floatValue = jsonObject.getDouble(objectStr);
						strValue = Double.toString(floatValue);
						break;
					case "java.lang.String":
						strValue = jsonObject.getString(objectStr);
						break;
					}
				} else {
					jsonObject = jsonObject.getJSONObject(objectStr);
				}
			}
		} catch (Exception exception) {
			Log.error(exception.toString());
			System.out.println(exception);
		}
		Log.info(strValue);
		return strValue;
	}

	/**
	 * This method accepts a JSON array as a String and returns an ArrayList of
	 * map, which is populated with values in input JSON
	 * 
	 * @param json
	 *            : JSON array
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getMapArrayListFromJsonArray(
			String json) {
		ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
		JSONArray jSONArray;
		JSONObject jSONObject = null;
		try {
			jSONArray = new JSONArray(json);
			int length = jSONArray.length();
			for (int index = 0; index < length; index++) {
				jSONObject = (JSONObject) jSONArray.get(index);
				String[] jsonFieldNames = JSONObject.getNames(jSONObject);
				HashMap<String, String> row = new HashMap<String, String>();
				for (String jsonFieldName : jsonFieldNames) {
					Object jsonFieldValue = jSONObject.get(jsonFieldName);
					row.put(jsonFieldName, jsonFieldValue.toString());
				}
				resultList.add(row);
			}
		} catch (JSONException exception) {
			exception.printStackTrace();
			Log.error(exception.getMessage());
		}
		return resultList;
	}

	/**
	 * @param json
	 *            - Pass the json in string format in which user wants to find
	 *            the expected value
	 * @param key
	 *            - key which user wants to find in the json
	 * @param keyValue
	 *            - key value which when gets matched, the corresponding index
	 *            value from json will be read.
	 * @param field
	 *            - expected field for which value has to be retrieved.
	 * @return jsonArrayIndex - Index in json where the expected key value is
	 *         matched.
	 * 
	 *         This index can be further used to send input parameters to
	 *         getValueFromJSONByType from fitnesse wiki
	 * 
	 */
	public static int getKeyIndexFromJSONByKeyAndValueType(String json,
			String key, String keyValue) {
		int jsonArrayIndex = 1000;

		ArrayList<HashMap<String, String>> resultJson = new ArrayList<HashMap<String, String>>();

		if (!json.isEmpty()) {
			Log.info("Converting JSON Response to Array List Using Common Function");
			resultJson = getMapArrayListFromJsonArray(json);
			Log.info("Total size of Response JSON-" + resultJson.size());
		} else {
			Log.info("Total size of Response JSON - 0");
			return jsonArrayIndex;
		}
		boolean isKeyExists = false;

		int i = 0;
		for (HashMap<String, String> row : resultJson) {
			// Get the key from json
			Object expectedKey = row.get(key);
			if (keyValue.equals(expectedKey.toString())) {
				Log.info("Key is matched -" + key + ","
						+ expectedKey.toString());
				isKeyExists = true;
				String test = row.toString();
				Log.info("Row is -" + test);
				jsonArrayIndex = i;
				break;
			}
			i++;
		}
		if (!isKeyExists) {
			String strValue = "Key not found in json- " + key + "," + keyValue;
			Log.info(strValue);
		}

		return jsonArrayIndex;
	}

	/**
	 * This method gets File path of a json and returns it in string format
	 * 
	 * @param jsonFilePath
	 *            - path of file containing input json
	 * @return json in string format
	 * @throws Exception
	 */
	public String getJsonFromFile(String jsonFilePath) throws Exception {
		String jsonfromfile = "";

		BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		jsonfromfile = stringBuilder.toString();
		Log.info("Reading json from file: " + jsonFilePath);

		return jsonfromfile;
	}

	public String TrimJson(String jsonString) {
		String outPutjson = jsonString.replaceAll("[\n\r\t]", "");
		Log.info("Trimmed Json [" + outPutjson + "]");
		return outPutjson;

	}

}
