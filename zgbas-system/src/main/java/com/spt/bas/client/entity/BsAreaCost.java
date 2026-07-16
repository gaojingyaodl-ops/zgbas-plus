package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.spt.bas.client.vo.CompanyAreaVo;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 省市区单价表
 * 
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_bs_area_cost")
public class BsAreaCost extends IdEntity {
	private static final long serialVersionUID = 1L;
	private String areaCode; // 地区代码
	private BigDecimal warehouseUnitCost; // 仓储费单价
	private String remark;// 备注
	private Long enterpriseId;

	private CompanyAreaVo areaVo; 
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public BigDecimal getWarehouseUnitCost() {
		return warehouseUnitCost;
	}

	public void setWarehouseUnitCost(BigDecimal warehouseUnitCost) {
		this.warehouseUnitCost = warehouseUnitCost;
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

	@Transient
	public CompanyAreaVo getAreaVo() {
		return areaVo;
	}

	public void setAreaVo(CompanyAreaVo areaVo) {
		this.areaVo = areaVo;
	}

	
}
