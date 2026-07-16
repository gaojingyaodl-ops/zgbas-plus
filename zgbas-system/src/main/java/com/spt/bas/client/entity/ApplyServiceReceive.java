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
 * 申请单-服务费收款申请单
 */
@Entity
@Table(name = "t_apply_service_receive")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyServiceReceive extends IdEntity implements IPmEntity {

	private static final long serialVersionUID = 8986117752043215837L;
	private Long approveId; 		// 审批ID
	private String applyNo;
	private Long contractId; 		// 合同id
	private Long serviceContractId;	// 服务合同ID
	private String serviceContractNo;//服务合同编号
	private BigDecimal totalAmount;//合同总价
	private BigDecimal receivedAmount; // 已收
	private BigDecimal unReceivedAmount; // 未收
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveDate; 		// 收款日期
	private BigDecimal receiveAmount; // 收款金额
	private String receiveMode; 	// 收款方式 现金-cash，电汇-telTransfer，承兑-accept，信用证-credit
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date billDueTime; 		// 票证期限
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date dueTime; 			// 到期时间
	private Long companyId; 		// 付款公司id
	private String companyName; 	// 付款方抬头
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String status; 			// 状态 N-新增，D-完成
	private String remark; 			// 备注
	private Long enterpriseId; 		// 企业公司ID
	private String fileId; 			// 附件ID
	private String ourCompanyName; 	//我方公司名称

	private Long wxUserId;

	/**
	 * 发起来源 0：核心管理系统 1：采购管家小程序
	 */
	private String applySource;

	private BigDecimal breachAmount;
	private BigDecimal receiveBreachAmount;

	/**
	 *部门Id
	 */
	private Long deptId;

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

	public Long getServiceContractId() {
		return serviceContractId;
	}

	public void setServiceContractId(Long serviceContractId) {
		this.serviceContractId = serviceContractId;
	}

	public String getServiceContractNo() {
		return serviceContractNo;
	}

	public void setServiceContractNo(String serviceContractNo) {
		this.serviceContractNo = serviceContractNo;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getReceivedAmount() {
		return receivedAmount;
	}

	public void setReceivedAmount(BigDecimal receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

	public BigDecimal getUnReceivedAmount() {
		return unReceivedAmount;
	}

	public void setUnReceivedAmount(BigDecimal unReceivedAmount) {
		this.unReceivedAmount = unReceivedAmount;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public BigDecimal getReceiveAmount() {
		return receiveAmount;
	}

	public void setReceiveAmount(BigDecimal receiveAmount) {
		this.receiveAmount = receiveAmount;
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

	public Long getWxUserId() {
		return wxUserId;
	}

	public void setWxUserId(Long wxUserId) {
		this.wxUserId = wxUserId;
	}

	public String getApplySource() {
		return applySource;
	}

	public void setApplySource(String applySource) {
		this.applySource = applySource;
	}

	public BigDecimal getBreachAmount() {
		return breachAmount;
	}

	public void setBreachAmount(BigDecimal breachAmount) {
		this.breachAmount = breachAmount;
	}

	public BigDecimal getReceiveBreachAmount() {
		return receiveBreachAmount;
	}

	public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
		this.receiveBreachAmount = receiveBreachAmount;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
}
