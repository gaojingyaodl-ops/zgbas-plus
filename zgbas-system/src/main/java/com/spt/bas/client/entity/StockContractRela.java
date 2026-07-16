package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 合同库存明细关联表
 */
@Entity
@Table(name = "t_stock_contract_rela")
public class StockContractRela extends IdEntity {
	public static final String RELATYPE_SELL = "S";
	public static final String RELATYPE_OUT = "O";
	public static final String RELATYPE_IN = "I";

	private static final long serialVersionUID = 7128656353943121096L;
	private Long stockContractId;
	private Long contractId;
	private Long ctrProductId;
	private BigDecimal relaNum;// 数量
	private String relaType;// 关联类型，S-销售,O-出库,I-入库
	private Long approveId;// 审批编号
	private Long enterpriseId;

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public BigDecimal getRelaNum() {
		return relaNum;
	}

	public void setRelaNum(BigDecimal relaNum) {
		this.relaNum = relaNum;
	}

	public String getRelaType() {
		return relaType;
	}

	public void setRelaType(String relaType) {
		this.relaType = relaType;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Long getStockContractId() {
		return stockContractId;
	}

	public void setStockContractId(Long stockContractId) {
		this.stockContractId = stockContractId;
	}

	public Long getCtrProductId() {
		return ctrProductId;
	}

	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

}
