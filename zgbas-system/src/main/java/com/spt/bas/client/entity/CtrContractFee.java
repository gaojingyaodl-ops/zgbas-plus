package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 合同费用明细表
 * 
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_ctr_contract_fee")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CtrContractFee extends IdEntity {
	private static final long serialVersionUID = 1L;
	private Long contractId;// 合同id
	private Long bizUserId;// 业务员id
	private String bizUserName;// 业务员姓名
	private String feeType;// 费用类型 WF-仓储费, TF-运输费, LF-装车费, IPF-罚息, SWF-系统仓储费 
	private BigDecimal feeAmount;// 费用金额  
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date feeDate;// 费用日期
	private String fileId;// 附件id
	private String remark;// 备注
	private Long enterpriseId;// 企业帐套ID
	private String contractNo; //企业编号
	private String assumePerson;		//承担人
	private BigDecimal feeRate;			//费率
	
    //private List<CtrContract> ctrContracts = new ArrayList<CtrContract>();

	
    /*@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "id",referencedColumnName = "contractId")
	public List<CtrContract> getCtrContracts() {
		return ctrContracts;
	}

	public void setCtrContracts(List<CtrContract> ctrContracts) {
		this.ctrContracts = ctrContracts;
	}*/

	//@Transient
	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Long getBizUserId() {
		return bizUserId;
	}

	public void setBizUserId(Long bizUserId) {
		this.bizUserId = bizUserId;
	}

	public String getBizUserName() {
		return bizUserName;
	}

	public void setBizUserName(String bizUserName) {
		this.bizUserName = bizUserName;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	public Date getFeeDate() {
		return feeDate;
	}

	public void setFeeDate(Date feeDate) {
		this.feeDate = feeDate;
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

	public String getAssumePerson() {
		return assumePerson;
	}

	public void setAssumePerson(String assumePerson) {
		this.assumePerson = assumePerson;
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	




	




	
}
