package web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import common.Config;
import common.Log;

/**
 * This calss is used to call https apis
 * 
 * @author Administrator
 */
public class Http extends HttpEntityEnclosingRequestBase {
	protected int statusCode = -1;
	private static String serviceURL = "";
	private static String token = "";
	private static ArrayList<org.apache.http.message.BasicHeader> headerList = new ArrayList<org.apache.http.message.BasicHeader>();
	List<BasicNameValuePair> formParameters = new ArrayList<BasicNameValuePair>();
	public static final String METHOD_NAME = "DELETE";
	CloseableHttpResponse response = null;
	CloseableHttpClient httpclient = null;

	public String getMethod() {
		return METHOD_NAME;
	}

	public Http(final String uri) {
		super();
		setURI(URI.create(uri));
	}

	public Http(final URI uri) {
		super();
		setURI(uri);
	}

	public Http() {
		super();
	}

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

	/**
	 * Get Status code
	 * 
	 * @return
	 */
	public int getStatusCode() {
		// Log.Message(String.valueOf(this.statusCode), LogLevel.INFO);
		return this.statusCode;
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
		org.apache.http.message.BasicHeader header = new BasicHeader(
				headername, headervalue);
		headerList.add(header);
	}

	/**
	 * Set authorization token
	 * 
	 * @param authorizationToken
	 */
	public void setAuthorizationToken(String authorizationToken) {
		token = authorizationToken;
	}

	public void addFormParameter(String name, String value) {
		formParameters.add(new BasicNameValuePair(name, value));
	}

	public String getHeader(String name) {
		String headerValue = "";
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			Log.info("Header Name:" + header.getName() + " Header value: "
					+ header.getValue());
			if (header.getValue().contains(name)) {
				headerValue = header.getValue();
				break;
			}
		}
		return headerValue;
	}

	/**
	 * Sends http get request to provided URL & returns the response.
	 * 
	 * @param url
	 *            - URL to send get request.
	 * @return
	 * @throws Exception
	 */
	public static String getRequest(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
		Log.info("Sending 'GET' request to URL : " + url);
		Log.info("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	/**
	 * This method accepts input json and perform HTTP DELETE operation using
	 * the message body. It returns the output response.
	 * 
	 * @param inputJsonString
	 * @return result - output response.
	 * @throws Exception
	 */
	public String deleteRequestWithBody(String inputJsonString)
			throws Exception {
		Log.info("Getting product URL:" + Config.ProductURL);
		Log.info("Getting service path:" + serviceURL);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		Log.info("Running test...");
		Http httpDelete = new Http(serviceURL);
		StringEntity input = new StringEntity(inputJsonString,
				ContentType.APPLICATION_JSON);
		Log.info("Executing service ");
		if (!token.isEmpty()) {
			Log.info("Setting authorization token:" + token);
			httpDelete.setHeader("Authorization", token);
		}
		if (headerList != null) {
			for (org.apache.http.message.BasicHeader header : headerList) {
				httpDelete.setHeader(header);
			}
		}
		httpDelete.setEntity(input);
		CloseableHttpResponse response = httpclient.execute(httpDelete);
		String result = "";
		this.statusCode = response.getStatusLine().getStatusCode();
		if (response.getEntity() != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			String output = "";
			while ((output = br.readLine()) != null) {
				result = result + output;
			}
		}
		Log.info("Status code for response is - " + this.statusCode);
		Log.info("Returning result -  " + result);
		return result;
	}

	/**
	 * This method will use the httpsclient (buildHttpsClient) created to ignore
	 * the ssl handshakes & provide GET request functionality.
	 * 
	 * @return jsonResponse
	 * @throws Exception
	 */
	public String getRequestForHttps() throws Exception {
		Log.info("Getting product URL:" + Config.ProductURL);
		Log.info("Getting service path:" + serviceURL);
		CloseableHttpClient httpclient = buildHttpsClient();
		Log.info("Running test...");
		HttpGet request = new HttpGet(serviceURL);
		CloseableHttpResponse response = httpclient.execute(request);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));
		String result = "";
		String output = "";
		while ((output = br.readLine()) != null) {
			result = result + output;
		}
		this.statusCode = response.getStatusLine().getStatusCode();
		Log.info("Status code for response is - " + this.statusCode);
		Log.info("Returning result -  " + result);
		return result;
	}

	/**
	 * This method will create a httpsClient which will ignore certificate & it
	 * will not cause SSLHandshakeException while requesting to a https url.
	 * 
	 * @return
	 */
	private CloseableHttpClient buildHttpsClient() {
		try {
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustStrategy() {
				// @Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1)
						throws CertificateException {
					return true;
				}
			});
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					builder.build(),
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException(exception);
		}
	}

	/**
	 * This method gets the response for request types such as get, post, put,
	 * delete and head *
	 * 
	 * @param mediaType
	 *            - Application/Json or
	 * @param requestType
	 *            - get, post, put, delete,head
	 * @param jsonString
	 *            - input string in Json format
	 * @param mediaType
	 * @param requestType
	 * @param inputString
	 * @return
	 * @throws Exception
	 */
	public String getResponse(String mediaType, String requestType,
			String inputString) throws Exception {
		if (null == httpclient) {
			httpclient = buildHttpsClient();
		}

		switch (requestType.toLowerCase()) {
		case "get":
			HttpGet getRequest = new HttpGet(Http.serviceURL);
			getRequest.setHeader("Content-Type", mediaType);

			response = httpclient.execute(getRequest);
			break;

		case "put":
			HttpPut putRequest = new HttpPut(Http.serviceURL);
			putRequest.setHeader("Content-Type", mediaType);
			if (!inputString.isEmpty()) {
				StringEntity input = new StringEntity(inputString);
				putRequest.setEntity(input);
			}

			response = httpclient.execute(putRequest);
			break;

		case "post":
			HttpPost postRequest = new HttpPost(Http.serviceURL);
			if (!headerList.isEmpty()) {
				for (org.apache.http.message.BasicHeader header : headerList) {
					postRequest.setHeader(header);
				}
			}
			if (this.formParameters.isEmpty()) {
				postRequest.setHeader("Content-Type", mediaType);
				StringEntity input = new StringEntity(inputString);
				postRequest.setEntity(input);
			} else {
				postRequest.setEntity(new UrlEncodedFormEntity(
						this.formParameters, "UTF-8"));
			}
			response = httpclient.execute(postRequest);
			break;
		case "delete":
			HttpDelete deleteRequest = new HttpDelete(Http.serviceURL);
			deleteRequest.setHeader("Content-Type", mediaType);

			response = httpclient.execute(deleteRequest);
			break;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));

		String result = "";
		String output = "";
		while ((output = br.readLine()) != null) {
			result = result + output;
		}
		this.statusCode = response.getStatusLine().getStatusCode();
		System.out.println(result);
		System.out.println(this.statusCode);
		return result;
	}
}
