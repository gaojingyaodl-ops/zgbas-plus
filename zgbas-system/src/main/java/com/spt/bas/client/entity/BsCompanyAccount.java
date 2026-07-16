package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


/**
 * 企业财务信息表
 * @author zhangyanping
 *
 */
@Entity
@Table(name = "t_bs_company_account")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
@DynamicInsert
public class BsCompanyAccount extends IdEntity{

	private static final long serialVersionUID = -5385038624926006374L;

	/**
	 * 公司Id
	 */
	private Long companyId;

	/**
	 * 开户行
	 */
	private String bankName;

	/**
	 * 开户账号
	 */
	private String bankAccount;

	/**
	 * 开户名
	 */
	private String accountName;

	/**
	 * 税号
	 */
	private String taxNo;

	/**
	 * 是否默认
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean defaultFlg;

	/**
	 * 企业套账Id
	 */
	private Long enterpriseId;

	/**
	 * 联系人
	 */
	private String contactPerson;

	/**
	 * 联系人手机号
	 */
	private String contactPhone;

	/**
	 * 发票寄送地址
	 */
	private String contactAddress;

	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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
	public String getTaxNo() {
		return taxNo;
	}
	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}
	public Boolean getDefaultFlg() {
		return defaultFlg;
	}
	public void setDefaultFlg(Boolean defaultFlg) {
		this.defaultFlg = defaultFlg;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}
