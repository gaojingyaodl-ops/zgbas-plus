package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 申请单-代采赊销付退款申请单
 *
 */
@Entity
@Table(name = "t_apply_pay_refund_dcsx")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyPayRefundDcsx extends IdEntity implements IPmEntity{
	private static final long serialVersionUID = 5351547410253259853L;
	
	
	/**
	 * 审批id
	 */
	private	Long approveId;
	
	/**
	 * 合同Id
	 */
	private Long contractId;
	
	/**
	 * 合同编号
	 */
	private	String contractNo;
	
	/**
	 * 退款日期
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date refundDate;
	
	/**
	 * 合同总价
	 */
	private	BigDecimal totalAmount;
	
	/**
	 * 已付金额
	 */
	private	BigDecimal payedAmount;
	
	/**
	 * 退款金额
	 */
	private	BigDecimal refundAmount;
	
	/**
	 * 退款公司名称
	 */
	private	String companyName;
	/**
	 * 退款银行
	 */
	private	String bankName;

	/**
	 * 银行账号
	 */
	private String bankAccount;
	
	/**
	 * 状态			N-新增，A-审批中，B-驳回，D-完成
	 */
	private	String status;
	private	String remark;				//备注
	private	Long enterpriseId;			//企业账套ID
	private	String applyNo;				//退款编号
	private String fileId;              //附件ID
	private String ourCompanyName;		//我方抬头

	private Long deptId;                //部门Id


	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

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

	public Date getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getPayedAmount() {
		return payedAmount;
	}

	public void setPayedAmount(BigDecimal payedAmount) {
		this.payedAmount = payedAmount;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
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

	@Override
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getApplyNo() {
		return applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	public String getFileId() {
		return fileId;
	}

	@Override
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
}
