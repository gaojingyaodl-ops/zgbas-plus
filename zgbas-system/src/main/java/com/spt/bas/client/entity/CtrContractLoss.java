package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.spt.tools.jpa.vo.IdEntity;


/**
 *登记_合同损耗明细
 * @author zhangshuaibing
 *
 */
@Entity
@Table(name = "t_ctr_contract_loss")
public class CtrContractLoss extends IdEntity {
	private static final long serialVersionUID = -2150684488381933273L;

	private Long contractId; //	bigint	合同id
	private String contractNo; //	varchar(50)	合同编号
	private String companyName; //	varchar(100)	企业名称
	private String ourCompanyName; //	varchar(100)	我方名称
	private String lossTypeFrom; //	varchar(10)	责任方
	private String lossTypeTo; //	varchar(10)	承担方
	private BigDecimal lossNum; //	decimal(16,3)	货损数量
	private BigDecimal lossAmount; //	decimal(16,2)	货损金额
	private BigDecimal payAmount; //	decimal(16,2)	赔付金额
	private String fileId; //	varchar(50)	附件id
	private String remark; //	varchar(1000)	备注
	private Long enterpriseId;
	private Boolean enableFlg = true;

//	private Long contractId; //合同id
//	private BigDecimal lossNum;  // 损耗数量
//	private BigDecimal lossAmount; //损耗金额
//	private String fileId; // 附件id
//	private String remark; //备注
//	private Long enterpriseId;//企业账套ID


	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public String getLossTypeFrom() {
		return lossTypeFrom;
	}

	public void setLossTypeFrom(String lossTypeFrom) {
		this.lossTypeFrom = lossTypeFrom;
	}

	public String getLossTypeTo() {
		return lossTypeTo;
	}

	public void setLossTypeTo(String lossTypeTo) {
		this.lossTypeTo = lossTypeTo;
	}

	public BigDecimal getLossNum() {
		return lossNum;
	}

	public void setLossNum(BigDecimal lossNum) {
		this.lossNum = lossNum;
	}

	public BigDecimal getLossAmount() {
		return lossAmount;
	}

	public void setLossAmount(BigDecimal lossAmount) {
		this.lossAmount = lossAmount;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
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

	public Boolean getEnableFlg() {
		return enableFlg;
	}

	public void setEnableFlg(Boolean enableFlg) {
		this.enableFlg = enableFlg;
	}
}
