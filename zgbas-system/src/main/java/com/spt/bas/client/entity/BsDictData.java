package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "t_bs_dict_data")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsDictData extends IdEntity {
	private static final long serialVersionUID = 8932760423629468443L;
	private Long enterpriseId; 
	private BsDictType dictType;
	private String dictCd;
	private String dictName;
	private BigDecimal dispOrderNo;
	private Boolean enableFlg;
	private String remark;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "dict_type_id")
	public BsDictType getDictType() {
		return this.dictType;
	}

	public void setDictType(BsDictType dictType) {
		this.dictType = dictType;
	}

	@Column(length = 50)
	public String getDictCd() {
		return this.dictCd;
	}

	public void setDictCd(String dictCd) {
		this.dictCd = dictCd;
	}

	public String getDictName() {
		return this.dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
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

	public BsDictData() {
	}

	public BsDictData(String dictCd, String dictName) {
		this.dictCd = dictCd;
		this.dictName = dictName;
	}
}
