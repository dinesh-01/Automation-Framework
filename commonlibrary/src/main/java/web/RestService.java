package web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import common.Config;
import common.Log;

public class RestService {
	// private String exceptionName = "";
	protected String exceptionMessage = "";
	private static String serviceURL = "";
	private String filePath = "";
	private String uploadFilePath = "";
	private String uploadFileName = "";
	protected int statusCode = -1;
	public static String token = "";
	private HashMap<String, String> urlParameters = new HashMap<String, String>();
	private static MultivaluedMap<String, Object> headers = new MultivaluedHashMap<String, Object>();
	// private Client client = null;
	// WebTarget webResource = null;
	public String result = null;
	private String responseHeader = null;
	private Response response = null;
	

	public static void setBaseUrl(String baseUrl) {
		serviceURL = baseUrl;
	}

	public static void setServiceURL(String url) throws Exception {
		if (serviceURL.isEmpty()) {
			serviceURL = url;
		} else if (!serviceURL.endsWith("/") && !serviceURL.startsWith("/")) {
			serviceURL = serviceURL + "/" + url;
		} else {
			serviceURL = serviceURL + url;
		}
		Log.info("Setting service URL: " + serviceURL);
	}

	public void setAuthorizationToken(String authorizationToken) {
		token = authorizationToken;
		Log.info("Token from setAuthorizationToken" + token);
	}

	/**
	 * Download file from rest Sending request for request typeservice & save it
	 * to provided location.
	 * 
	 * @param filePath
	 *            - Absolute path for file.
	 */
	public void downloadFileInResponse(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * This method sets the upload file name & path. This file can be passed to
	 * post request for upload.
	 * 
	 * @param uploadFileName
	 *            - eg 'questionFile' in case of upload questions API
	 * @param uploadFilePath
	 *            - Absolute path for for file.
	 */
	public void uploadFileInRequest(String uploadFileName, String uploadFilePath) {
		this.uploadFileName = uploadFileName;
		Log.info("Setting uploadFileName:" + this.uploadFileName);
		this.uploadFilePath = uploadFilePath;
		Log.info("Setting uploadFilePath:" + this.uploadFilePath);
	}

	/**
	 * This method adds the header values for rest api request
	 * 
	 * @param name
	 *            - name of the attribute to be set
	 * @param value
	 *            - value of the attribute to be set
	 * @return nothing
	 * @throws Exception
	 */
	public void addHeader(String headername, String headervalue) {
		headers.putSingle(headername, headervalue);
		Log.info("Adding Header:" + headername + " " + headervalue);
	}

	/**
	 * Clears headers. This method should be called after api call completes, so
	 * that other API call does not inherit stale headers.
	 */
	public void clearHeaders() {
		headers.clear();
		Log.info("Headers cleared");
	}

	/**
	 * This method is used for getting the URL parameters of the Rest Service
	 * 
	 * @param name
	 *            - Name of the URL Parameter Variable e.g. SearchString
	 * @param value
	 *            - Value of the URL Parameter Variable e.g. Symc
	 */
	public void addUrlParameter(String name, String value) {
		urlParameters.put(name, value);

	}

	/**
	 * 
	 * Extracts the header from the reponse set by Get response method
	 * 
	 * @param fieldname
	 *            : user can provide the field name which you want to extract
	 *            from header
	 * @return
	 */
	public String getResponseHeader(String fieldname) {
		@SuppressWarnings("rawtypes")
		List headerlist = response.getHeaders().get(fieldname);
		if (null == headerlist) {
			Log.info("Header is empty in the reponse" + Config.ProductURL
					+ serviceURL);
			responseHeader = "Header is empty";
		}
		responseHeader = (String) headerlist.get(0);
		return responseHeader;
	}

	/**
	 * This method gets the response for request types such as get, post, put,
	 * delete and head
	 * 
	 * @param mediaType
	 *            - Application/Json or
	 * @param requestType
	 *            - get, post, put, delete,head
	 * @param jsonString
	 *            - input string in Json format
	 * @return
	 * @throws Exception
	 */
	public String GetResponse(String mediaType, String requestType,
			String jsonString) throws Exception {
		String result = null;
		Log.info("Token from get Reponse: " + token);
		Client client;
		// Required for File upload using REST API
		final FormDataMultiPart multiPart = new FormDataMultiPart();

		// If file upload is expected then client will be set using different
		// method else
		// it will continue using the previous method.
		if (!(this.uploadFileName.equals("") && this.uploadFilePath.equals(""))) {
			Log.info("Creating client for File upload");

			client = ClientBuilder.newBuilder()
					.register(MultiPartFeature.class).build();
			File uploadFile = new File(this.uploadFilePath);
			final FileDataBodyPart filePart = new FileDataBodyPart(
					this.uploadFileName, uploadFile);
			multiPart.bodyPart(filePart);

		} else {
			client = ClientBuilder.newClient();
		}

		client.property(ClientProperties.CONNECT_TIMEOUT, (60000) * 1);
		client.property(ClientProperties.READ_TIMEOUT, (60000) * 5);

		try {
			// JSONObject jsonObj = new JSONObject(jsonString);
			this.exceptionMessage = "";
			this.statusCode = -1;

			Log.info("Running test...");
			Log.info("Getting product URL:" + Config.ProductURL);
			Log.info("Getting service path:" + serviceURL);
			WebTarget webResource = client.target(serviceURL);
			
			if (urlParameters.size() > 0) {
				@SuppressWarnings("rawtypes")
				Iterator iterator = urlParameters.entrySet().iterator();
				Log.info("Adding Parameters to path:" + serviceURL);
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry pairs = (Map.Entry) iterator.next();
					System.out.println(pairs.getKey() + " = "
							+ pairs.getValue());
					webResource = webResource.queryParam(pairs.getKey()
							.toString(), pairs.getValue());
				}
			}

			Log.info("Setting JOSN input:" + jsonString);
			Log.info("Sending request to:" + webResource.getUri().toString());
			Log.info("Sending request for request type:" + requestType);
			Log.info("Headers to send with request: " + headers);
			switch (requestType.toLowerCase()) {
			case "get":
				if (!token.isEmpty()) {
					headers.putSingle("Authorization", token);
					Log.info("Setting authorization token:" + token);
					response = webResource.request(MediaType.APPLICATION_JSON)
							.headers(headers).get(Response.class);
				} else {
					response = webResource.request(MediaType.APPLICATION_JSON)
							.get(Response.class);
				}
				;
				break;
			case "post":
				if (!token.isEmpty()) {

					headers.putSingle("Authorization", token);
					Log.info("Setting authorization token:" + token);

					if (this.uploadFileName.equals("")
							&& this.uploadFilePath.equals("")) {
						response = webResource
								.request(MediaType.APPLICATION_JSON)
								.headers(headers)
								.post(Entity.entity(jsonString, mediaType));
					} else {
						Log.info("Actual Media type for upload is:"
								+ multiPart.getMediaType());
						response = webResource.request()
								.headers(headers)
								.accept(MediaType.APPLICATION_JSON_TYPE)
								.post(Entity.entity(multiPart, mediaType));
					}
				} else {

					if (this.uploadFileName.equals("")
							&& this.uploadFilePath.equals("")) {
						Log.info("Empty Auth Token");
						response = webResource.request(
								MediaType.APPLICATION_JSON).post(
								Entity.entity(jsonString, mediaType));
					} else {
						Log.info("Actual Media type for upload is:"
								+ multiPart.getMediaType());
						response = webResource.request()
								.headers(headers)
								.accept(MediaType.APPLICATION_JSON_TYPE)
								.post(Entity.entity(multiPart, mediaType));
					}
				}
				;
				break;
			case "put":
				if (!token.isEmpty()) {
					headers.putSingle("Authorization", token);
					Log.info("Setting authorization token:" + token);
					response = webResource.request(MediaType.APPLICATION_JSON)
							.headers(headers)
							.put(Entity.entity(jsonString, mediaType));
				} else {
					response = webResource.request(MediaType.APPLICATION_JSON)
							.put(Entity.entity(jsonString, mediaType));

				}
				;
				break;
			case "delete":
				if (!token.isEmpty()) {
					headers.putSingle("Authorization", token);
					Log.info("Setting authorization token:" + token);
					response = webResource.request(MediaType.APPLICATION_JSON)
							.headers(headers).delete();
				} else {
					response = webResource.request(MediaType.APPLICATION_JSON)
							.delete();
				}
				;
				break;
			case "head":
				if (!token.isEmpty()) {
					headers.putSingle("Authorization", token);
					Log.info("Setting authorization token:" + token);
					response = webResource.request(MediaType.APPLICATION_JSON)
							.headers(headers).head();
				} else {
					response = webResource.request(MediaType.APPLICATION_JSON)
							.head();
				}
				;
				break;
			}
			Log.info("Status code:" + this.statusCode);
			// this.setStatusCode(response.getStatus());
			if (!this.filePath.equals("")) {
				Log.info("Sending request to download API..");
				response = webResource.request(
						MediaType.APPLICATION_OCTET_STREAM).get(Response.class);
				Log.info("Downloading file");
				InputStream input = (InputStream) response.getEntity();
				byte[] SWFByteArray = IOUtils.toByteArray(input);
				Log.info("Saving file to location: " + this.filePath);
				FileOutputStream fileOutputStream = new FileOutputStream(
						new File(this.filePath));
				fileOutputStream.write(SWFByteArray);
				fileOutputStream.flush();
				fileOutputStream.close();
				result = "true";
			} else {
				result = response.readEntity(String.class);
			}
			this.result = result;
			this.statusCode = response.getStatus();
			Log.info(result);
			if (this.statusCode != 200) {
				this.exceptionMessage = result;
				Log.info("Exception message:" + this.exceptionMessage);
			}

		} catch (Exception exception) {
			Log.error("Exception occured in Get Response ["
					+ exception.getMessage() + "]");
			Log.error("Exception occured in Get Response ["
					+ exception.getStackTrace() + "]");
			result = exception.getMessage();
		} finally {
			client.close();
		}
		return result;
	}

	public String getExceptionMessage() {
		Log.info(this.exceptionMessage);
		return this.exceptionMessage;
	}

	public int getStatusCode() {
		Log.info(String.valueOf(this.statusCode));
		return this.statusCode;
	}

	public String getResult() {
		Log.info(String.valueOf(this.result));
		return this.result;
	}
}
