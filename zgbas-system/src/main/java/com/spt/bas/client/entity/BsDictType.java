package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OrderBy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 数据字典
 * 
 * @author jianhuang
 * 
 */
@Entity
@Table(name = "t_bs_dict_type")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsDictType extends IdEntity {
	private static final long serialVersionUID = 8781055268279521047L;
	private Long enterpriseId; 
	private String dictTypeCd;
	private String dictTypeName;
	private BigDecimal dispOrderNo;
	private String remark;
	private Boolean enableFlg;
	private List<BsDictData> dictDatas;

	public String getDictTypeCd() {
		return this.dictTypeCd;
	}

	public void setDictTypeCd(String dictTypeCd) {
		this.dictTypeCd = dictTypeCd;
	}

	public String getDictTypeName() {
		return this.dictTypeName;
	}

	public void setDictTypeName(String dictTypeName) {
		this.dictTypeName = dictTypeName;
	}

	@Column(precision = 12, scale = 0)
	public BigDecimal getDispOrderNo() {
		return this.dispOrderNo;
	}

	public void setDispOrderNo(BigDecimal dispOrderNo) {
		this.dispOrderNo = dispOrderNo;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@OneToMany(fetch = FetchType.EAGER,mappedBy="dictType")
	@BatchSize(size = 10)
	@OrderBy(clause = "disp_Order_No asc")
	public List<BsDictData> getDictDatas() {
		return this.dictDatas;
	}

	public void setDictDatas(List<BsDictData> dictDatas) {
		this.dictDatas = dictDatas;
	}
	@JsonSerialize(using = ToStringSerializer.class)
	public Boolean getEnableFlg() {
		return enableFlg;
	}

	public void setEnableFlg(Boolean enableFlg) {
		this.enableFlg = enableFlg;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

}
