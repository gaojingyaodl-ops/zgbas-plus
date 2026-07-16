package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 申请单-收款申请单
 */
@Entity
@Table(name = "t_apply_receive")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyReceive extends IdEntity implements IPmEntity {

	private static final long serialVersionUID = 6488116581043215777L;

	/**
	 * 审批ID
	 */
	private Long approveId;

	/**
	 * 合同ID
	 */
	private Long contractId;

	/**
	 * 业务编号
	 */
	private String businessNo;

	/**
	 * 合同编号
	 */
	private String contractNo;

	/**
	 * 合同总价
	 */
	private BigDecimal totalAmount = BigDecimal.ZERO;

	/**
	 * 已收金额
	 */
	private BigDecimal payedAmount = BigDecimal.ZERO;

	/**
	 * 未收金额
	 */
	private BigDecimal unpayedAmount = BigDecimal.ZERO;

	/**
	 * 待收贴现费用
	 */
	private BigDecimal unDiscountAmount = BigDecimal.ZERO;

	/**
	 * 收款日期
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveDate;

	/**
	 * 付款公司ID
	 */
	private Long companyId;

	/**
	 * 付款方企业名称
	 */
	private String companyName;

	/**
	 * 我方抬头名称
	 */
	private String ourCompanyName;

	/**
	 * 收款类型 B-定金，R-尾款，A-全款，Z-逐笔，P-部分，M-违约金
	 */
	private String receiveType;

	/**
	 * 收款方式 H-票汇，Z-支票，D-信用证，T-电汇
	 */
	private String receiveMode;

	/**
	 * 收款金额
	 */
	private BigDecimal receiveAmount = BigDecimal.ZERO;

	/**
	 * 贴息费用承担方
	 * K-客户承担
	 * Y-业务员承担
	 * W-我方承担
	 */
	private String discountTarget;

	/**
	 * 贴现费用
	 */
	private BigDecimal discountAmount = BigDecimal.ZERO;

	/**
	 * 贴现利率
	 */
	private BigDecimal discountRate = BigDecimal.ZERO;

	/**
	 * 收款时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveTime;

	/**
	 * 收承兑日期
	 * 票证期限 开始时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date billDueTime;

	/**
	 * 承兑到期日
	 * 票证期限 到期时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date dueTime;

	/**
	 * 状态 N-新增，D-完成
	 */
	private String status;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 企业账套ID
	 */
	private Long enterpriseId;

	/**
	 * 附件ID
	 */
	private String fileId;

	/**
	 * 申请单号
	 */
	private String applyNo;

	/**
	 * 采购方企业ID
	 */
	private Long buyCompanyId;

	/**
	 * 是否需要风控审批
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean riskApproveFlg;//是否需要风控审批

	/**
	 * 微信用户ID
	 */
	private Long wxUserId;

	/**
	 * 发起来源 0：核心管理系统 1：采购管家小程序
	 */
	private String applySource;

	/**
	 * 合同逾期罚息金额
	 */
	private BigDecimal breachAmount = BigDecimal.ZERO;

	/**
	 * 已收合同逾期罚息金额
	 */
	private BigDecimal receiveBreachAmount = BigDecimal.ZERO;

	/**
	 * 托盘利息
	 */
	private BigDecimal tpInterest = BigDecimal.ZERO;

	/**
	 * 已收托盘利息
	 */
	private BigDecimal approveTpInterest = BigDecimal.ZERO;

	/**
	 * 业务类型
	 * DCSX:代采赊销
	 */
	private String businessType;

	/**
	 *部门Id
	 */
	private Long deptId;

	/**
	 *	主收款单ID
	 */
	private Long parentReceiveId;

	/**
	 * 收款明细数据
	 */
	private List<ApplyReceive> receiveDetailList;

	/**
	 * 合同明细数据
	 */
	private List<CtrContract> contractDetailList;

    private String contractDetailListStr;

	private String receiveDetailListStr;

	public Long getApproveId() {
		return approveId;
	}

	@Override
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
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

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
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

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public String getReceiveType() {
		return receiveType;
	}

	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}

	public String getReceiveMode() {
		return receiveMode;
	}

	public void setReceiveMode(String receiveMode) {
		this.receiveMode = receiveMode;
	}

	public BigDecimal getReceiveAmount() {
		return receiveAmount;
	}

	public void setReceiveAmount(BigDecimal receiveAmount) {
		this.receiveAmount = receiveAmount;
	}

	public BigDecimal getDiscountAmount() {
		return Objects.isNull(discountAmount) ? BigDecimal.ZERO : discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
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

	public String getFileId() {
		return fileId;
	}

	@Override
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getApplyNo() {
		return applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	public Long getBuyCompanyId() {
		return buyCompanyId;
	}

	public void setBuyCompanyId(Long buyCompanyId) {
		this.buyCompanyId = buyCompanyId;
	}

	public Boolean getRiskApproveFlg() {
		return riskApproveFlg;
	}

	public void setRiskApproveFlg(Boolean riskApproveFlg) {
		this.riskApproveFlg = riskApproveFlg;
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
		return Objects.isNull(breachAmount) ? BigDecimal.ZERO : breachAmount;
	}

	public void setBreachAmount(BigDecimal breachAmount) {
		this.breachAmount = breachAmount;
	}

	public BigDecimal getReceiveBreachAmount() {
		return Objects.isNull(receiveBreachAmount) ? BigDecimal.ZERO : receiveBreachAmount;
	}

	public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
		this.receiveBreachAmount = receiveBreachAmount;
	}

	public BigDecimal getTpInterest() {
		return tpInterest;
	}

	public void setTpInterest(BigDecimal tpInterest) {
		this.tpInterest = tpInterest;
	}

	public BigDecimal getApproveTpInterest() {
		return approveTpInterest;
	}

	public void setApproveTpInterest(BigDecimal approveTpInterest) {
		this.approveTpInterest = approveTpInterest;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	@Transient
	public List<ApplyReceive> getReceiveDetailList() {
		return receiveDetailList;
	}

	public void setReceiveDetailList(List<ApplyReceive> receiveDetailList) {
		this.receiveDetailList = receiveDetailList;
	}

	@Transient
	public String getReceiveDetailListStr() {
		return receiveDetailListStr;
	}


	public void setReceiveDetailListStr(String receiveDetailListStr) {
		this.receiveDetailListStr = receiveDetailListStr;
	}

	public Long getParentReceiveId() {
		return parentReceiveId;
	}

	public void setParentReceiveId(Long parentReceiveId) {
		this.parentReceiveId = parentReceiveId;
	}

	@Transient
	public List<CtrContract> getContractDetailList() {
		return contractDetailList;
	}

	public void setContractDetailList(List<CtrContract> contractDetailList) {
		this.contractDetailList = contractDetailList;
	}

	@Transient
	public String getContractDetailListStr() {
		return contractDetailListStr;
	}

	public void setContractDetailListStr(String contractDetailListStr) {
		this.contractDetailListStr = contractDetailListStr;
	}

	public String getDiscountTarget() {
		return discountTarget;
	}

	public void setDiscountTarget(String discountTarget) {
		this.discountTarget = discountTarget;
	}

	public BigDecimal getUnDiscountAmount() {
		return unDiscountAmount;
	}

	public void setUnDiscountAmount(BigDecimal unDiscountAmount) {
		this.unDiscountAmount = unDiscountAmount;
	}
}
