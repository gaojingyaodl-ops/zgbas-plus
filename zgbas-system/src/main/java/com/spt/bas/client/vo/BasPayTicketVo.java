/**
 * 
 */
package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author wlddh
 *
 */
public class BasPayTicketVo {
	private Long id;
	private String inInvoiceNo;// 进项发票
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date inInvoiceDate;// 进项发票日期
	private String inBillNo;// 进项记账凭证号

	private BigDecimal inTaxAmount;// 发票进项税
	private BigDecimal inAmountNotax;// 发票不含税价
	private BigDecimal inAmount;// 发票金额
	private String remark;
	
	private Long createUserId;// 创建人id
	private String createUserName;// 创建人姓名

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
