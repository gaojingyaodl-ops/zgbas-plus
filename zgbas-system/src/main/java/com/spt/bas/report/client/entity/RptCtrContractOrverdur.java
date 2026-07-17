package com.spt.bas.report.client.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
/**
 * 合同主表
 */
public class RptCtrContractOrverdur extends PageSearchVo{
	private Long id;
	private Long enterpriseId; //企业账套Id
	private String contractType;//合同类型
	private String businessNo;//业务编号
	private String contractNo;//合同编号	
	private BigDecimal totalAmount=BigDecimal.ZERO;//合同总价
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTime;//合同时间
	private Long companyId;//对方企业ID
	private String companyName;//对方企业名称
	private String ourCompanyName;//我方企业名称
	private String deliveryType;//提货方式
	private String payType;//付款方式
	private BigDecimal totalNumber=BigDecimal.ZERO;//合同数量
	private String deliveryMode;//交货方式
	private String deliveryAddr;//交货地址
	private String 	deliveryPhone;//配送电话														
	private String productStatus;//货物状态:B-采购，S-销售，I-入库，O-出库，PI-部分入库，PO-部分出库
	// 采购：N-新增，S-已签约，F1-已付款，G1-已收货，V1-已收票，C-已作废
	// 销售：N-新增，S-已签约，F2-已收款，G2-已发货，V2-已开票，C-已作废
	private String contractStatus;//合同状态
	private String fileId;//附件ID
	private Long approveId;//审批ID
	private String remark;//备注
	private BigDecimal bondRate;//定金比率
	private BigDecimal bondAmount=BigDecimal.ZERO;//预付定金：采购：付款金额，销售：收款金额
	private String status;//状态
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payBondTime;//收付定金日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;//收付全款日期
	private Long matchUserId;//业务员ID
	private String matchUserName;//业务员名称
	private Boolean buysellFlg;//买卖标识
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDateFrom;//交货日期开始
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDateTo;//交货日期结束(到货时间)
	private Boolean transferFlg; //是否需转货权
	private String contactName;//对方企业联系人
	private String contactPhone;//对方联系电话
	private String contactAddr;//对方联系地址
	private Boolean billFlg = false;//发票状态
	private Boolean fondFlg = false;//资金状态
	private BigDecimal transportAmount;//运输费
	private BigDecimal warehouseAmount;//仓储费
	private Boolean transportFlg;
	private Boolean warehouseFlg;//仓库状态
	//2.采购合同，增加查询条件“状态”：未入库、未付款、未收票
	//3.销售合同，增加查询条件“状态”：未出库、未收款、未开票
	private BigDecimal billedAmount = BigDecimal.ZERO;//已开票已收票金额
	private BigDecimal dealedAmount = BigDecimal.ZERO;//已收付款金额
	private BigDecimal warehouseNumber = BigDecimal.ZERO;//实际已入\出库数量
	//private BigDecimal buyInNumber = BigDecimal.ZERO;;//若为销售合同，则为销售对应的采购审批入库数量；若为采购，则为本采购入库数量
	private String source;//来源
	private String contractAttr;//合同属性：N-现货，F-期货
	private String linkContractId;//关联ID
	private Long deptId;//部门ID
	private String productsName; //货名
	private Boolean longFlg = false;//是否长约
	private String foreignContractNo;//外商合同号
	private Boolean orverdurFlg;//合同逾期标识
	private BigDecimal orverdurAmount;//逾期金额
	private String deptName;
	private List<Long> deptIds;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date beginTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date endTime;
	private String contractTypes;
	private String replyStatus;
	private String businessType;
	private List<Long> deptIdList;
	private String searchType; //搜索类型：invoice:逾期开票/payOrReceive:逾期付款或逾期收款
	
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
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
	public Date getContractTime() {
		return contractTime;
	}
	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
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
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public BigDecimal getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
	public String getDeliveryAddr() {
		return deliveryAddr;
	}
	public void setDeliveryAddr(String deliveryAddr) {
		this.deliveryAddr = deliveryAddr;
	}
	public String getDeliveryPhone() {
		return deliveryPhone;
	}
	public void setDeliveryPhone(String deliveryPhone) {
		this.deliveryPhone = deliveryPhone;
	}
	public String getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}
	public String getContractStatus() {
		return contractStatus;
	}
	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public BigDecimal getBondRate() {
		return bondRate;
	}
	public void setBondRate(BigDecimal bondRate) {
		this.bondRate = bondRate;
	}
	public BigDecimal getBondAmount() {
		return bondAmount;
	}
	public void setBondAmount(BigDecimal bondAmount) {
		this.bondAmount = bondAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getPayBondTime() {
		return payBondTime;
	}
	public void setPayBondTime(Date payBondTime) {
		this.payBondTime = payBondTime;
	}
	public Date getPayFullTime() {
		return payFullTime;
	}
	public void setPayFullTime(Date payFullTime) {
		this.payFullTime = payFullTime;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public Boolean getBuysellFlg() {
		return buysellFlg;
	}
	public void setBuysellFlg(Boolean buysellFlg) {
		this.buysellFlg = buysellFlg;
	}
	public Date getDeliveryDateFrom() {
		return deliveryDateFrom;
	}
	public void setDeliveryDateFrom(Date deliveryDateFrom) {
		this.deliveryDateFrom = deliveryDateFrom;
	}
	public Date getDeliveryDateTo() {
		return deliveryDateTo;
	}
	public void setDeliveryDateTo(Date deliveryDateTo) {
		this.deliveryDateTo = deliveryDateTo;
	}
	public Boolean getTransferFlg() {
		return transferFlg;
	}
	public void setTransferFlg(Boolean transferFlg) {
		this.transferFlg = transferFlg;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getContactAddr() {
		return contactAddr;
	}
	public void setContactAddr(String contactAddr) {
		this.contactAddr = contactAddr;
	}
	public Boolean getBillFlg() {
		return billFlg;
	}
	public void setBillFlg(Boolean billFlg) {
		this.billFlg = billFlg;
	}
	public Boolean getFondFlg() {
		return fondFlg;
	}
	public void setFondFlg(Boolean fondFlg) {
		this.fondFlg = fondFlg;
	}
	public BigDecimal getTransportAmount() {
		return transportAmount;
	}
	public void setTransportAmount(BigDecimal transportAmount) {
		this.transportAmount = transportAmount;
	}
	public BigDecimal getWarehouseAmount() {
		return warehouseAmount;
	}
	public void setWarehouseAmount(BigDecimal warehouseAmount) {
		this.warehouseAmount = warehouseAmount;
	}
	public Boolean getTransportFlg() {
		return transportFlg;
	}
	public void setTransportFlg(Boolean transportFlg) {
		this.transportFlg = transportFlg;
	}
	public Boolean getWarehouseFlg() {
		return warehouseFlg;
	}
	public void setWarehouseFlg(Boolean warehouseFlg) {
		this.warehouseFlg = warehouseFlg;
	}
	public BigDecimal getBilledAmount() {
		return billedAmount;
	}
	public void setBilledAmount(BigDecimal billedAmount) {
		this.billedAmount = billedAmount;
	}
	public BigDecimal getDealedAmount() {
		return dealedAmount;
	}
	public void setDealedAmount(BigDecimal dealedAmount) {
		this.dealedAmount = dealedAmount;
	}
	public BigDecimal getWarehouseNumber() {
		return warehouseNumber;
	}
	public void setWarehouseNumber(BigDecimal warehouseNumber) {
		this.warehouseNumber = warehouseNumber;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	public String getLinkContractId() {
		return linkContractId;
	}
	public void setLinkContractId(String linkContractId) {
		this.linkContractId = linkContractId;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public String getProductsName() {
		return productsName;
	}
	public void setProductsName(String productsName) {
		this.productsName = productsName;
	}
	public Boolean getLongFlg() {
		return longFlg;
	}
	public void setLongFlg(Boolean longFlg) {
		this.longFlg = longFlg;
	}
	public String getForeignContractNo() {
		return foreignContractNo;
	}
	public void setForeignContractNo(String foreignContractNo) {
		this.foreignContractNo = foreignContractNo;
	}
	public Boolean getOrverdurFlg() {
		return orverdurFlg;
	}
	public void setOrverdurFlg(Boolean orverdurFlg) {
		this.orverdurFlg = orverdurFlg;
	}
	public BigDecimal getOrverdurAmount() {
		return orverdurAmount;
	}
	public void setOrverdurAmount(BigDecimal orverdurAmount) {
		this.orverdurAmount = orverdurAmount;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public List<Long> getDeptIds() {
		return deptIds;
	}
	public void setDeptIds(List<Long> deptIds) {
		this.deptIds = deptIds;
	}
	public String getContractTypes() {
		return contractTypes;
	}
	public void setContractTypes(String contractTypes) {
		this.contractTypes = contractTypes;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getReplyStatus() {
		return replyStatus;
	}
	public void setReplyStatus(String replyStatus) {
		this.replyStatus = replyStatus;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public List<Long> getDeptIdList() {
		return deptIdList;
	}
	public void setDeptIdList(List<Long> deptIdList) {
		this.deptIdList = deptIdList;
	}
	
}
