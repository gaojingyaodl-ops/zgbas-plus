package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 商品配置表
 */
@Entity
@Table(name = "t_bs_product_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsProductConfig extends IdEntity{

	private static final long serialVersionUID = 9178025329378605210L;
	/**
	 * 配置项
	 */
	private String configKey;

	/**
	 * 配置项名称
	 */
	private String configName;
	/**
	 * 配置内容Json
	 */
	private String configValue;
	/**
	 * 企业账套ID
	 */
	private Long enterpriseId;

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigKey() {
		return configKey;
	}
	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
}
