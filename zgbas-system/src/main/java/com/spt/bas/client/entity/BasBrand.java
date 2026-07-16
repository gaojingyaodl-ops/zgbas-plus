package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 牌号
 */
@Entity
@Table(name = "t_bas_brand")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BasBrand extends IdEntity {

	private static final long serialVersionUID = -5691008959189240574L;
	private String productCd; // 品类
	private String brandNumber; // 牌号
	private Long enterpriseId; // 企业账套Id

	/**
	 * 是否是安全牌号
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean safeFlg;


	/**
	 *流程Id
	 */
	private Long approveId;

	/**
	 * 牌号分类
	 */
	private String brandSort;

	/**
	 * 备注
	 */
	private String remark;



	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public String getBrandSort() {
		return brandSort;
	}

	public void setBrandSort(String brandSort) {
		this.brandSort = brandSort;
	}

	public String getProductCd() {
		return productCd;
	}

	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}

	public String getBrandNumber() {
		return brandNumber;
	}

	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Boolean getSafeFlg() {
		return safeFlg;
	}

	public void setSafeFlg(Boolean safeFlg) {
		this.safeFlg = safeFlg;
	}
}
