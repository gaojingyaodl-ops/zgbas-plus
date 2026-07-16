package com.spt.pm.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 序号列生成规则
 */
@Entity
@Table(name = "t_bs_key_sequence")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsKeySequence extends IdEntity {

	private static final long serialVersionUID = -1780201836805956863L;
	private String keyCategory; // 分类
	private String keyPrefix; // 前缀
	private String dateRule; // 生成规则; 'yyyyMMdd,yyyyMM,yyyy',
	private Long maxValue; // 当前最大值
	private Long seqLenth; // 顺序号长度
	private String lastDateVal; // 最近日期值
	private String remark;// 备注
	private	Long	enterpriseId;		//	企业账套ID

	public String getKeyCategory() {
		return keyCategory;
	}

	public void setKeyCategory(String keyCategory) {
		this.keyCategory = keyCategory;
	}

	public String getKeyPrefix() {
		return StringUtils.isBlank(keyPrefix) ? "" : keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public String getDateRule() {
		return dateRule;
	}

	public void setDateRule(String dateRule) {
		this.dateRule = dateRule;
	}

	public Long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}

	public Long getSeqLenth() {
		return seqLenth;
	}

	public void setSeqLenth(Long seqLenth) {
		this.seqLenth = seqLenth;
	}

	public String getLastDateVal() {
		return lastDateVal;
	}

	public void setLastDateVal(String lastDateVal) {
		this.lastDateVal = lastDateVal;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

}
