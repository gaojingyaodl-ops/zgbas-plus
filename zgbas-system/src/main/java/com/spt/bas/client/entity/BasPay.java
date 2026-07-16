/**
 * 
 */
package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 付款信息
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_bas_pay")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BasPay extends IdEntity  implements IPmEntity{

	private static final long serialVersionUID = 8272446503512523792L;
	private Long contractId;// 合同id
	private String businessNo; // 业务编号
	private String contractNo;// 合同编号
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payDate;// 付款日期
	private Long companyId;// 企业id
	private String companyName;// 收款抬头
	private String ourCompanyName; // 我方企业名称
	private String productName;// 商品名称
	private String productCode;// 商品代码
	private BigDecimal dealPrice;// 单价
	private BigDecimal dealNumber;// 数量
	private String numberUnit;// 数量单位
	private BigDecimal taxAmount;// 进项税
	private BigDecimal dealAmountNotax;// 不含税价
	private BigDecimal dealAmount;// 合同总价
	private String bankName;// 开户银行
	private String bankAccount;// 银行账号
	private String billNo;// 记账凭证号
	private String inInvoiceNo;// 进项发票
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date inInvoiceDate;// 进项发票日期
	private String inBillNo;// 进项记账凭证号
	private String status;// 状态
	private Long approveId;// 审批id
	private String remark;
	private String fileId; // 附件id
	private BigDecimal bondRate;// 定金比率
	private BigDecimal payAmount;// 付款金额
	private String payType;//支付类型：B-定金，P-追加保证金，R-尾款，A-全款
	
	private BigDecimal inTaxAmount;// 发票进项税
	private BigDecimal inAmountNotax;// 发票不含税价
	private BigDecimal inAmount;// 发票金额
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean closeFlg;//是否闭口业务
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean receiveFlg;//对应收款已经到账
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public BigDecimal getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}

	public BigDecimal getDealNumber() {
		return dealNumber;
	}

	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}

	public String getNumberUnit() {
		return numberUnit;
	}

	public void setNumberUnit(String numberUnit) {
		this.numberUnit = numberUnit;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getDealAmountNotax() {
		return dealAmountNotax;
	}

	public void setDealAmountNotax(BigDecimal dealAmountNotax) {
		this.dealAmountNotax = dealAmountNotax;
	}

	public BigDecimal getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(BigDecimal dealAmount) {
		this.dealAmount = dealAmount;
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

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getInInvoiceNo() {
		return inInvoiceNo;
	}

	public void setInInvoiceNo(String inInvoiceNo) {
		this.inInvoiceNo = inInvoiceNo;
	}

	public Date getInInvoiceDate() {
		return inInvoiceDate;
	}

	public void setInInvoiceDate(Date inInvoiceDate) {
		this.inInvoiceDate = inInvoiceDate;
	}

	public String getInBillNo() {
		return inBillNo;
	}

	public void setInBillNo(String inBillNo) {
		this.inBillNo = inBillNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public BigDecimal getInTaxAmount() {
		return inTaxAmount;
	}

	public void setInTaxAmount(BigDecimal inTaxAmount) {
		this.inTaxAmount = inTaxAmount;
	}

	public BigDecimal getInAmountNotax() {
		return inAmountNotax;
	}

	public void setInAmountNotax(BigDecimal inAmountNotax) {
		this.inAmountNotax = inAmountNotax;
	}

	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public BigDecimal getBondRate() {
		return bondRate;
	}

	public void setBondRate(BigDecimal bondRate) {
		this.bondRate = bondRate;
	}

	public Boolean getCloseFlg() {
		return closeFlg;
	}

	public void setCloseFlg(Boolean closeFlg) {
		this.closeFlg = closeFlg;
	}

	public Boolean getReceiveFlg() {
		return receiveFlg;
	}

	public void setReceiveFlg(Boolean receiveFlg) {
		this.receiveFlg = receiveFlg;
	}
}
