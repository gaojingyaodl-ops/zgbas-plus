package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class ContractSearchVo extends PageSearchVo{
	private Long userId;//用户ID
	private boolean admin;
	private String status;
	private String contractTypes;
	private String type;// 'I':收/开票   'R':收/付款   'W':出/入库  'B':退款
	private String orverdurStatus;
	private Long ctrContractId;  	//合同Id
	private String ophisType;		//合同操作记录 类型
	private Long enterpriseId;
	private String payCondition;	//付款条件
	private String receiveCondition; // 收款条件
	private String warehouseCondition;//出入库条件
	private String billCondition; //收票条件
	private String invoiceBillCondition; //开票条件
	private String productTypeCondition; //产品类型条件
	private Long deptLeaderId;	  //中心负责人ID
	private String searchType;//A:查看本中心所有合同  P:查看本中心所有预售合同
	private Boolean cancelFlg = false;//作废申请
	private Boolean piccRemainCredit;
	private String businessType;
	private Boolean matchCreditFlg = false;
	private Boolean saasContractFlg = false; //是否为查看saas合同
	private String contractSource;
	private Long buyContractId;
	private Long sellContractId;
	private Boolean funderFlg = false;
	private List<String> companyNameList;

	private Boolean outLineFlg = false;
	private Boolean payDateChange;

	private Boolean dcsxSearchFlg = false;
	private Boolean fundViewFlag = true;

	private String requestUrl;
	private String downLoadType;

	/**
	 * 确认收货单据上传时间-开始
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date confirmFileDateBegin;

	/**
	 * 确认收货单据上传时间-截止
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date confirmFileDateEnd;

	private String productType;

	/**
	 * 化工业务员ID集合
	 */
	private List<Long> hgMatchUserIdList;
	
	private List<Long> deptIdList;
	
	public String getOrverdurStatus() {
		return orverdurStatus;
	}

	public void setOrverdurStatus(String orverdurStatus) {
		this.orverdurStatus = orverdurStatus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContractTypes() {
		return contractTypes;
	}

	public void setContractTypes(String contractTypes) {
		this.contractTypes = contractTypes;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Long getCtrContractId() {
		return ctrContractId;
	}

	public void setCtrContractId(Long ctrContractId) {
		this.ctrContractId = ctrContractId;
	}

	public String getOphisType() {
		return ophisType;
	}

	public void setOphisType(String ophisType) {
		this.ophisType = ophisType;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getPayCondition() {
		return payCondition;
	}

	public void setPayCondition(String payCondition) {
		this.payCondition = payCondition;
	}

	public String getWarehouseCondition() {
		return warehouseCondition;
	}

	public void setWarehouseCondition(String warehouseCondition) {
		this.warehouseCondition = warehouseCondition;
	}

	public String getBillCondition() {
		return billCondition;
	}

	public void setBillCondition(String billCondition) {
		this.billCondition = billCondition;
	}

	public Long getDeptLeaderId() {
		return deptLeaderId;
	}

	public void setDeptLeaderId(Long deptLeaderId) {
		this.deptLeaderId = deptLeaderId;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public Boolean getCancelFlg() {
		return cancelFlg;
	}

	public void setCancelFlg(Boolean cancelFlg) {
		this.cancelFlg = cancelFlg;
	}

	public Boolean getPiccRemainCredit() {
		return piccRemainCredit;
	}

	public void setPiccRemainCredit(Boolean piccRemainCredit) {
		this.piccRemainCredit = piccRemainCredit;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Boolean getMatchCreditFlg() {
		return matchCreditFlg;
	}

	public void setMatchCreditFlg(Boolean matchCreditFlg) {
		this.matchCreditFlg = matchCreditFlg;
	}

	public Boolean getSaasContractFlg() {
		return saasContractFlg;
	}

	public void setSaasContractFlg(Boolean saasContractFlg) {
		this.saasContractFlg = saasContractFlg;
	}

	public String getContractSource() {
		return contractSource;
	}

	public void setContractSource(String contractSource) {
		this.contractSource = contractSource;
	}

	public Long getBuyContractId() {
		return buyContractId;
	}

	public void setBuyContractId(Long buyContractId) {
		this.buyContractId = buyContractId;
	}

	public Long getSellContractId() {
		return sellContractId;
	}

	public void setSellContractId(Long sellContractId) {
		this.sellContractId = sellContractId;
	}

	public Boolean getFunderFlg() {
		return funderFlg;
	}

	public void setFunderFlg(Boolean funderFlg) {
		this.funderFlg = funderFlg;
	}

	public List<String> getCompanyNameList() {
		return companyNameList;
	}

	public void setCompanyNameList(List<String> companyNameList) {
		this.companyNameList = companyNameList;
	}

	public Boolean getPayDateChange() {
		return payDateChange;
	}

	public void setPayDateChange(Boolean payDateChange) {
		this.payDateChange = payDateChange;
	}

	public Boolean getOutLineFlg() {
		return outLineFlg;
	}

	public void setOutLineFlg(Boolean outLineFlg) {
		this.outLineFlg = outLineFlg;
	}

	public Boolean getDcsxSearchFlg() {
		return dcsxSearchFlg;
	}

	public void setDcsxSearchFlg(Boolean dcsxSearchFlg) {
		this.dcsxSearchFlg = dcsxSearchFlg;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getReceiveCondition() {
		return receiveCondition;
	}

	public void setReceiveCondition(String receiveCondition) {
		this.receiveCondition = receiveCondition;
	}

	public String getInvoiceBillCondition() {
		return invoiceBillCondition;
	}

	public void setInvoiceBillCondition(String invoiceBillCondition) {
		this.invoiceBillCondition = invoiceBillCondition;
	}

	public String getProductTypeCondition() {
		return productTypeCondition;
	}

	public void setProductTypeCondition(String productTypeCondition) {
		this.productTypeCondition = productTypeCondition;
	}

	public Date getConfirmFileDateBegin() {
		return confirmFileDateBegin;
	}

	public void setConfirmFileDateBegin(Date confirmFileDateBegin) {
		this.confirmFileDateBegin = confirmFileDateBegin;
	}

	public Date getConfirmFileDateEnd() {
		return confirmFileDateEnd;
	}

	public void setConfirmFileDateEnd(Date confirmFileDateEnd) {
		this.confirmFileDateEnd = confirmFileDateEnd;
	}

	public String getDownLoadType() {
		return downLoadType;
	}

	public void setDownLoadType(String downLoadType) {
		this.downLoadType = downLoadType;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public List<Long> getHgMatchUserIdList() {
		return hgMatchUserIdList;
	}

	public void setHgMatchUserIdList(List<Long> hgMatchUserIdList) {
		this.hgMatchUserIdList = hgMatchUserIdList;
	}

	public Boolean getFundViewFlag() {
		return fundViewFlag;
	}

	public void setFundViewFlag(Boolean fundViewFlag) {
		this.fundViewFlag = fundViewFlag;
	}

	public List<Long> getDeptIdList() {
		return deptIdList;
	}

	public void setDeptIdList(List<Long> deptIdList) {
		this.deptIdList = deptIdList;
	}
}
