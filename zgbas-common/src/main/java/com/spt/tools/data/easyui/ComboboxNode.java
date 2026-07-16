/**
 * 
 */
package com.spt.tools.data.easyui;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jian
 * 
 */
public class ComboboxNode {
	private String value;
	private String text;
	private Map<String, Object> attributes = new HashMap<String, Object>();// 其他属性

	public ComboboxNode() {

	}

	public void addAttr(String key, Object value) {
		attributes.put(key, value);
	}

	public ComboboxNode(String value, String text) {
		this.value = value;
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}
