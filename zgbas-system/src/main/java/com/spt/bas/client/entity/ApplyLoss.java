package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 损耗申请单
 */
@Entity
@Table(name = "t_apply_loss")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyLoss extends IdEntity implements IPmEntity{

	private static final long serialVersionUID = -2554866099497311623L;
	private Long contractId; //	bigint	合同id
	private String contractNo; //	varchar(50)	合同编号
	private String companyName; //	varchar(100)	企业名称
	private String ourCompanyName; //	varchar(100)	我方名称
	private String productsName; //	varchar(100)	货名
	private BigDecimal totalNumber; //	decimal(16,3)	合同数量
	private BigDecimal totalAmount; //	decimal(16,2)	合同金额
	private String lossTypeFrom; //	varchar(10)	责任方
	private String lossTypeTo; //	varchar(10)	承担方
	private BigDecimal lossNum; //	decimal(16,3)	货损数量
	private BigDecimal lossAmount; //	decimal(16,2)	货损金额
	private BigDecimal payAmount; //	decimal(16,2)	赔付金额
	private String fileId; //	varchar(50)	附件id
	private String remark; //	varchar(1000)	备注
	private Long approveId; //	bigint	审批id
	private String status; //	char(1)	审批状态 N-新增，A-审批中，B-驳回，D-完成
	
//	private Long buyContractId;				//采购合同ID
//	private String buyContractNo;			//采购合同编号
//	private Long sellContractId;			//销售合同ID
//	private String sellContractNo;			//销售合同编号
//	private BigDecimal lossNumber;			//损耗数量
//	private BigDecimal lossAmount;			//损耗金额
//	private Long dutyCompanyId;				//责任方企业ID
//	private String dutyCompanyName;			//责任方企业名称
//	private String fileId;					//附件ID
//	private	String	remark;				    //备注
//	private	String	status;				    //申请状态		N-新增，A-审批中，B-驳回，D-完成
//	private	Long approveId;			        //审批ID
//	private	String	approveNo;			    //审批编号
//	private Long enterpriseId;				//企业账套ID


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

	public String getProductsName() {
		return productsName;
	}

	public void setProductsName(String productsName) {
		this.productsName = productsName;
	}

	public BigDecimal getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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

	@Override
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getApproveId() {
		return approveId;
	}

	@Override
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}
}
