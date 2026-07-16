package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

@Entity
@Table(name = "t_bs_area")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class BsArea  extends IdEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7075987451162639133L;
	private Long id;
	private String parentId;
	private String code;
	private String name;
	private Integer grand;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getGrand() {
		return grand;
	}
	public void setGrand(Integer grand) {
		this.grand = grand;
	}
	
}
