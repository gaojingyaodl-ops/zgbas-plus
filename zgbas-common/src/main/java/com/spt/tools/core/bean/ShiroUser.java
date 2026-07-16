/**
 * 
 */
package com.spt.tools.core.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangjian
 * 
 */
public class ShiroUser implements Serializable {
	private static final long serialVersionUID = 1L;
	public Long id;
	public String loginName;
	public String name;
	private Map<String, Object> prop=new HashMap<>();
//	private Object data;
	

	public ShiroUser() {
		
	}
	public ShiroUser(Long id, String loginName, String name) {
		this.id = id;
		this.loginName = loginName;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * 本函数输出将作为默认的<shiro:principal/>输出.
	 */
	@Override
	public String toString() {
		return loginName;
	}

	/**
	 * 重载hashCode,只计算loginName;
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(loginName);
	}

	/**
	 * 重载equals,只计算loginName;
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShiroUser other = (ShiroUser) obj;
		if (loginName == null) {
			if (other.loginName != null)
				return false;
		} else if (!loginName.equals(other.loginName))
			return false;
		return true;
	}
	
	public void addProp(String key,Object val){
		this.prop.put(key, val);
	}

	public Map<String, Object> getProp() {
		return prop;
	}

	public void setProp(Map<String, Object> prop) {
		this.prop = prop;
	}
//	public <T> T getData() {
//		return (T)data;
//	}
//	public void setData(Object data) {
//		this.data = data;
//	}
}
