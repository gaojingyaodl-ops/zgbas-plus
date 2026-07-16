package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 快速查询
 */
@Entity
@Table(name = "t_sys_quick_search")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SysQuickSearch extends IdEntity {

	private static final long serialVersionUID = 3421946742037330519L;

	/**
	 * 模块url，如：/pm/approve
	 */
	private String moduleUrl;

	/**
	 * 【属性名称-属性值】的JSON数据
	 */
	private String propJson;

	/**
	 * 快速查询名称
	 */
	private String searchName;

	/**
	 * 创建人ID
	 */
	private Long userId;


	public String getModuleUrl() {
		return moduleUrl;
	}

	public void setModuleUrl(String moduleUrl) {
		this.moduleUrl = moduleUrl;
	}

	public String getPropJson() {
		return propJson;
	}

	public void setPropJson(String propJson) {
		this.propJson = propJson;
	}
	

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
