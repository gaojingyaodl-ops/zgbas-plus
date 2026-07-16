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
   *   申请单-销售退款单
 *
 */
@Entity
@Table(name = "t_apply_receive_refund")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyReceiveRefund extends IdEntity implements IPmEntity{
	private static final long serialVersionUID = -3226711653289325120L;
	/**
	 * 审批ID
	 */
	private Long approveId;
	/**
	 * 合同id
	 */
	private Long contractId;
	/**
	 * 合同编号
	 */
	private String contractNo;
	/**
	 * 合同总价
	 */
	private BigDecimal totalAmount;
	/**
	 * 已收金额
	 */
	private BigDecimal payedAmount;
	/**
	 * 未收金额
	 */
	private BigDecimal unpayedAmount;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	/**
	 * 退款日期
	 */
	private Date refundDate;
	/**
	 * 退款类型         A-协议退款   B-破包退款    C-其它
	 */
	private String refundType;
	/**
	 * 退款金额
	 */
	private BigDecimal refundAmount;
	/**
	 * 收款方式 现金-cash，电汇-telTransfer，承兑-accept，信用证-credit
	 */
	private String receiveMode;
	/**
	 * 票证期限
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date billDueTime;

	/**
	 * 到期时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date dueTime;
	/**
	 * 退款公司
	 */
	private Long companyId;
	/**
	 * 退款公司名称
	 */
	private String companyName;
	/**
	 * 状态 N-新增，D-完成
	 */
	private String status;
	/**
	 * 备注
	 */
	private String remark;
	private Long enterpriseId;
	/**
	 * 附件ID
	 */
	private String fileId;
	/**
	 * 我方公司名称
	 */
	private String ourCompanyName;
	/**
	 * 退款编号
	 */
	private String applyNo;

	/**
	 * 部门Id
	 * @return
	 */
	private Long deptId;

	/**
	 * 开户行
	 */
	private String bankName;

	/**
	 * 账号
	 */
	private String bankAccount;

	/**
	 * 所属区域
	 */
	private String ownRegion;

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
	public BigDecimal getUnpayedAmount() {
		return unpayedAmount;
	}
	public void setUnpayedAmount(BigDecimal unpayedAmount) {
		this.unpayedAmount = unpayedAmount;
	}
	public Date getRefundDate() {
		return refundDate;
	}
	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
	}
	public String getRefundType() {
		return refundType;
	}
	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}
	public BigDecimal getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getReceiveMode() {
		return receiveMode;
	}
	public void setReceiveMode(String receiveMode) {
		this.receiveMode = receiveMode;
	}
	public Date getBillDueTime() {
		return billDueTime;
	}
	public void setBillDueTime(Date billDueTime) {
		this.billDueTime = billDueTime;
	}
	public Date getDueTime() {
		return dueTime;
	}
	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getStatus() {
		return status;
	}
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
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getApplyNo() {
		return applyNo;
	}
	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
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

	public String getOwnRegion() {
		return ownRegion;
	}

	public void setOwnRegion(String ownRegion) {
		this.ownRegion = ownRegion;
	}
}
