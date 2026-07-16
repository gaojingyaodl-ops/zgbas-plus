/**
 * 
 */
package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 商品类型
 * 
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_bs_product_type")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsProductType extends IdEntity {
	private static final long serialVersionUID = -3907433711045468159L;
	private String typeCode; // 商品类别标识
	private String typeName; // 商品类别名称
	private String typeNameEn; // 商品类别名称（英文）
	private String typeBizCode; // 商品业务代码
	private Integer level; // 商品类别层级
	private Long dispOrderNo;// 序号
	private String remark; // 备注
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean enableFlg = true;// 是否有效

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeNameEn() {
		return typeNameEn;
	}

	public void setTypeNameEn(String typeNameEn) {
		this.typeNameEn = typeNameEn;
	}

	public String getTypeBizCode() {
		return typeBizCode;
	}

	public void setTypeBizCode(String typeBizCode) {
		this.typeBizCode = typeBizCode;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Long getDispOrderNo() {
		return dispOrderNo;
	}

	public void setDispOrderNo(Long dispOrderNo) {
		this.dispOrderNo = dispOrderNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getEnableFlg() {
		return enableFlg;
	}

	public void setEnableFlg(Boolean enableFlg) {
		this.enableFlg = enableFlg;
	}
}
