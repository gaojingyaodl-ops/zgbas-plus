/**
 * 
 */
package com.spt.tools.file.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONObject;

/**
 * @author wlddh
 *
 */
public class XmlUtils {


	public static JSONObject xml2JSON(String strXML) throws Exception {
		JSONObject json = new JSONObject();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
		org.w3c.dom.Document doc = documentBuilder.parse(stream);
		doc.getDocumentElement().normalize();
		Element root = doc.getDocumentElement();
		json.put(root.getNodeName(), iterateElement(root));
		return json;
	}

	private static JSONObject iterateElement(Node element) {
		NodeList nodeList = element.getChildNodes();
		Node et = null;
		JSONObject obj = new JSONObject();
		List<Object> list = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			list = new LinkedList<>();
			et = nodeList.item(i);
			if (et.getNodeType() == Node.ELEMENT_NODE) {
				if (et.hasChildNodes() && et.getChildNodes().getLength() > 1) {
					if (obj.containsKey(et.getNodeName())) {
						list = (List<Object>) obj.get(et.getNodeName());
					}
					list.add(iterateElement(et));
					obj.put(et.getNodeName(), list);
				} else {

					obj.put(et.getNodeName(), et.getTextContent());
				}
			}
		}
		return obj;
	}

	/**
	 * XML格式字符串转换为Map
	 *
	 * @param strXML
	 *            XML字符串
	 * @return XML数据转换后的Map
	 * @throws Exception
	 */
	public static Map<String, String> xmlToMap(String strXML) throws Exception {
		Map<String, String> data = new HashMap<>();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
		org.w3c.dom.Document doc = documentBuilder.parse(stream);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getDocumentElement().getChildNodes();
		for (int idx = 0; idx < nodeList.getLength(); ++idx) {
			Node node = nodeList.item(idx);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				org.w3c.dom.Element element = (org.w3c.dom.Element) node;
				data.put(element.getNodeName(), element.getTextContent());
			}
		}
		try {
			stream.close();
		} catch (Exception ex) {

		}
		return data;
	}

	/**
	 * 将Map转换为XML格式的字符串
	 *
	 * @param data
	 *            Map类型数据
	 * @return XML格式的字符串
	 * @throws Exception
	 */
	public static String mapToXml(Map<String, String> data) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		org.w3c.dom.Document document = documentBuilder.newDocument();
		org.w3c.dom.Element root = document.createElement("xml");
		document.appendChild(root);
		for (String key : data.keySet()) {
			String value = data.get(key);
			if (value == null) {
				value = "";
			}
			value = value.trim();
			org.w3c.dom.Element filed = document.createElement(key);
			filed.appendChild(document.createTextNode(value));
			root.appendChild(filed);
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		String output = writer.getBuffer().toString(); // .replaceAll("\n|\r", "");
		try {
			writer.close();
		} catch (Exception ex) {
		}
		return output;
	}

	public static void main(String[] args) {
		String str = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><returnsms>  <statusbox>  <mobile>18261118555</mobile>  <taskid>857468</taskid>  <status>10</status>  <receivetime>2018-12-14 14:43:17</receivetime>  <errorcode><![CDATA[DELIVRD]]></errorcode>  <extno><![CDATA[174]]></extno>  </statusbox>  <statusbox>  <mobile>15251792736</mobile>  <taskid>857467</taskid>  <status>10</status>  <receivetime>2018-12-14 14:43:17</receivetime>  <errorcode><![CDATA[DELIVRD]]></errorcode>  <extno><![CDATA[174]]></extno>  </statusbox> </returnsms>";

		try {
			JSONObject map = XmlUtils.xml2JSON(str);
			System.out.println(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
