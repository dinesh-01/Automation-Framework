package environment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import common.Log;

/**
 * This class provides utility methods for XML parsing.
 * 
 * @author Administrator
 */
public class Xml {

	/**
	 * User defined type for expression representing field, operator & value
	 */
	private static class Expression {
		public String field;
		public String value;
		public String operator;

		Expression(String field, String operator, String value) {
			this.field = field;
			this.value = value;
			this.operator = operator;
		}
	}

	private static List<Expression> expressions = new ArrayList<Expression>();

	/**
	 * Creates expression list which would be used to find element matching with
	 * these expressions
	 * 
	 * @param name
	 *            - name of xml tag
	 * @param value
	 *            - value of the xml tag
	 * @param operator
	 *            - operator to compare with
	 */
	public static void expression(String name, String value, String operator) {
		expressions.add(new Expression(name, value, operator));
	}

	/**
	 * Returns a XML document object from XML String
	 * 
	 * @param stringInXmlFormat
	 * @return
	 * @throws Exception
	 */
	public static Document getXmlDocumentForString(String stringInXmlFormat)
			throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputStream inputStream = new ByteArrayInputStream(
				stringInXmlFormat.getBytes(StandardCharsets.UTF_8));
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		InputSource inputSource = new InputSource(reader);
		inputSource.setEncoding("UTF-8");
		// inputSource.setCharacterStream(new StringReader(stringInXmlFormat));
		return dBuilder.parse(inputSource);
	}

	/**
	 * This method generates a list of values for all the occurrences of the
	 * given node in the specified xml.
	 * 
	 * @param stringInXmlFormat
	 * @param nodeToSearch
	 *            eg: For xml <test><name>xyz</name></test> , the nodeToSearch
	 *            will be 'name' eg: For the given xml, the nodeToSearch will be
	 *            'Scope/name' <scope> <id>resgroup-v50</id>
	 *            <objectTypeName>VirtualApp</objectTypeName>
	 *            <name>SalesCRMApp</name> </scope>
	 * @return
	 */
	public static NodeList createNodeListFromXml(String stringInXmlFormat,
			String nodeToSearch) {
		NodeList nodes = null;
		Document doc = null;
		// Log.Message("XML string : " + stringInXmlFormat, LogLevel.INFO);
		try {
			doc = getXmlDocumentForString(stringInXmlFormat);
		} catch (Exception e1) {
			e1.printStackTrace();
			return nodes;
		}
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = null;
		try {
			expr = xpath.compile("//" + nodeToSearch);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return nodes;
		}
		Object result = null;
		try {
			result = expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return nodes;
		}
		nodes = (NodeList) result;
		if (nodes.getLength() == 0) {
			Log.error("Given nodes '" + nodeToSearch + "' are not found");
			return nodes;
		} else {
			Log.info("Total List size:" + nodes.getLength());
		}
		return nodes;
	}

	/**
	 * This method finds a value of a requested node from the same record(index)
	 * where the required keynode & its value is matched.
	 * 
	 * @param xmlString
	 * @param keyNodeName
	 * @param keyNodeValue
	 * @param nodeNameToFetchValue
	 * @return eg: User wants to search "objectId" from the below xml for the
	 *         record where "name" is 'Share-win-DB'. keyNodeName = name
	 *         keyNodeValue = Share-win-DB valueToBeFecthedForNodeName =
	 *         objectId
	 * 
	 *         <basicinfo> <objectId>vm-41</objectId> <name>Share-win-DB</name>
	 *         </basicinfo> <basicinfo> <objectId>vm-39</objectId>
	 *         <name>Share-win-Web</name> </basicinfo>
	 * 
	 */
	public static String searchNodeValueInXmlFromSameIndexOfGivenKey(
			String xmlString, String keyNodeName, String keyNodeValue,
			String nodeNameToFetchValue) {
		String valueNodeValue = "";
		NodeList keyList = createNodeListFromXml(xmlString, keyNodeName);
		NodeList valueList = createNodeListFromXml(xmlString,
				nodeNameToFetchValue);
		if (keyList.getLength() != 0 && valueList.getLength() != 0) {
			for (int i = 0; i < keyList.getLength(); i++) {
				if (keyList.item(i).getNodeValue().equals(keyNodeValue)) {
					valueNodeValue = valueList.item(i).getNodeValue();
					Log.info("keyNode:" + keyNodeName + ",keyValue:"
							+ keyNodeValue + "Found at record :" + i);
					Log.info("Value:" + valueNodeValue + "For Node:"
							+ nodeNameToFetchValue);
					break;
				}
			}
		} else {
			Log.info(
					"Either one of the given node are not found in the XML.");
		}
		return valueNodeValue;
	}

	/**
	 * Get the value of specified node from XML, if expression list is specified
	 * it returns the element mathcing with the criteria mentioned in the
	 * expression otherwise returns the element as is.
	 * 
	 * @param filePath
	 *            - path of xml
	 * @param keyNodeName
	 *            - node name to search for
	 * @param seperator
	 *            - Separetor in case more than one element foudn with the same
	 *            name.
	 * @return
	 * @throws IOException 
	 */
	public static String getNodeValues(String filePath, String keyNodeName,
			String seperator) throws IOException {
		String nodeValue = "";
		String xmlString = FileUtil.getFileContent(filePath);
		NodeList keyList = createNodeListFromXml(xmlString, keyNodeName);
		if (keyList.getLength() != 0) {
			Log.info("Found [" + keyList.getLength()
					+ "] nodes matching with key: " + keyNodeName);
			boolean firstTime = true;
			for (int i = 0; i < keyList.getLength(); i++) {
				Node parentNode = keyList.item(i).getParentNode();
				String nodeContent = convertNodeToString(parentNode);
				boolean result = true;
				if (expressions.size() > 0) {
					Log.info("Expression evaluated to:" + result);
					result = evaluateMethod(nodeContent);
				}
				if (result) {
					if (firstTime) {
						nodeValue = keyList.item(i).getTextContent();
						firstTime = false;
					} else {
						nodeValue = nodeValue + seperator
								+ keyList.item(i).getTextContent();
					}
				}
			}
		}
		return nodeValue;
	}

	/**
	 * Convert node to xml for further processing
	 * 
	 * @param node
	 *            - node
	 * @return - xml in string format
	 */
	private static String convertNodeToString(Node node) {
		String xmlString = "";
		try {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			// transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			Source source = new DOMSource(node);

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);

			transformer.transform(source, result);
			xmlString = sw.toString();

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return xmlString;
	}

	/**
	 * Evaluate expression & return result as true for false on given xml
	 * contents.
	 * 
	 * @param nodeContent
	 * @return
	 */
	private static boolean evaluateMethod(String nodeContent) {
		String logicalOperator = "OR";
		boolean result = false;
		boolean firstTime = true;
		for (Expression expression : expressions) {
			NodeList childNodes = createNodeListFromXml(nodeContent,
					expression.field);

			Log.info("Found [" + childNodes.getLength()
					+ "] nodes matching with key: " + expression.field);
			Log.info("Evaluating expression");
			Log.info("Expression field: " + expression.field);
			Log.info("Expression value: " + expression.value);
			Log.info("Expression operator: " + expression.operator);
			String value = childNodes.item(0).getTextContent();
			//String name = childNodes.item(0).getNodeName();
			Log.info("Comparing actual value: " + value
					+ " with expression value: " + expression.value);
			if (Integer.parseInt(value) > Integer.parseInt(expression.value)) {
				if (firstTime) {
					result = true;
					firstTime = false;
				} else {
					if (logicalOperator.toLowerCase().equals("or")) {
						result = result || true;
					} else {
						result = result && true;
					}
				}
			}
		}
		return result;
	}

	/**
	 * This method returns true if the given key node & it's value exists in the
	 * xml.
	 * 
	 * @param xmlString
	 * @param keyNodeName
	 * @param keyNodeValue
	 * @return
	 */
	public static boolean isNodeValueExistsInXml(String xmlString,
			String keyNodeName, String keyNodeValue) {
		boolean isNodeValueExistsInXml = false;
		NodeList keyList = createNodeListFromXml(xmlString, keyNodeName);
		if (keyList.getLength() != 0) {
			for (int i = 0; i < keyList.getLength(); i++) {
				if (keyList.item(i).getNodeValue().equals(keyNodeValue)) {
					isNodeValueExistsInXml = true;
					Log.info("The given node:" + keyNodeName
							+ " and its value:" + keyNodeValue
							+ " exists in XML at record number:" + i);
					break;
				}
			}
		} else {
			Log.info("The given node is not found in the XML.");
		}
		return isNodeValueExistsInXml;
	}

	/**
	 * Read value of the tag/attribute identified by tagPath
	 * 
	 * @param xmlString
	 * @param tagPath
	 * @param tagIndex
	 * @return
	 */
	public static String getValueFromXML(String xmlString, String tagPath,
			String tagIndex) {
		String value = null;
		NodeList keyList = createNodeListFromXml(xmlString, tagPath);
		int length = keyList.getLength();
		int tagIndexInt = Integer.parseInt(tagIndex);
		if (tagIndexInt < length) {
			Node node = keyList.item(tagIndexInt);
			// value = node.getNodeValue();
			value = node.getTextContent();
		}
		return value;
	}

	private static Element getElement(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		return doc.getDocumentElement();
	}

	public static void addElementsToXml(String filePath, String sibling,
			List<String> elements) throws Exception, IOException {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setIgnoringComments(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document document = builder.parse(new File(filePath));

		NodeList nodes = document.getElementsByTagName(sibling);
		Log.info("Found element with name: " + sibling);

		for (String element : elements) {
			Element xmlElement = getElement(element);
			Log.info(
					"Converted string to element: "
							+ xmlElement.getTextContent());
			Node importedNode = document.importNode(xmlElement, true);
			nodes.item(0).getParentNode().appendChild(importedNode);
		}

		DOMSource source = new DOMSource(document);

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(filePath);
		transformer.transform(source, result);

	}

	public static void addElementToXml(String filePath, String sibling,
			String element) throws Exception, IOException {
		File file = new File(filePath);
		if (file.exists()) {
			if (file.canWrite()) {
				Log.info("File is writable!");
			} else {
				Log.info("File is in read only mode!");
			}
			// Now make our file writable
			file.setWritable(true);
			if (file.canWrite()) {
				Log.info("Changed file to writable!");
			}
		}
		else
		{
			Log.info("File not found " + filePath);
		}

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setIgnoringComments(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document document = builder.parse(file);

		NodeList nodes = document.getElementsByTagName(sibling);
		Log.info("Found element with name: " + sibling);
		Element xmlElement = getElement(element);
		Node importedNode = document.importNode(xmlElement, true);
		Log.info("Added element to "
				+ nodes.item(0).getParentNode().getNodeName());
		nodes.item(0).getParentNode().appendChild(importedNode);

		DOMSource source = new DOMSource(document);
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		StreamResult result = new StreamResult(filePath);
		transformer.transform(source, result);
	}
}
