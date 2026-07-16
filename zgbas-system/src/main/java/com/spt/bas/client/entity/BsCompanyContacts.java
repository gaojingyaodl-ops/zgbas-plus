package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 企业联系人表
 * @author wanjie
 *
 */
@Entity
@Table(name = "t_bs_company_contacts")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyContacts extends IdEntity{

	private static final long serialVersionUID = -3717003933467536002L;
	private Long companyId;			//企业id
	private String remark;			//备注 
	private String positionName;	//职位
	private String contactName;		//联系人
	private String contactPhone;	//联系电话  座机
	private Long enterpriseId; //企业ID
	private String mobile;//手机
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer sex;// 性别
	private String deptName;//部门
	private String companyRole;//角色
	private String QQ;//QQ
	private String wechatNo;//微信
	private String email;//邮箱
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
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
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getCompanyRole() {
		return companyRole;
	}
	public void setCompanyRole(String companyRole) {
		this.companyRole = companyRole;
	}
	public String getQQ() {
		return QQ;
	}
	public void setQQ(String qQ) {
		QQ = qQ;
	}
	public String getWechatNo() {
		return wechatNo;
	}
	public void setWechatNo(String wechatNo) {
		this.wechatNo = wechatNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}	
	
}
