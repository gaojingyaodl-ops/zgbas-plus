package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;


import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @Author: wm
 * @Date: Created in 2022-04-25 13:28
 */
@Entity
@Table(name = "t_logistics_company_config")
public class LogisticsCompanyConfig extends IdEntity {
    /**
     * 合同-状态历史表
     */
    private static final long serialVersionUID = -414150608183832769L;
    private String carrier; //承运商
    private String bankName;//开户行
    private String bankAccount; //账号
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 营业执照号
     */
    private String licenseNumber;
    /**
     * 合同签订日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date startDate;
    /**
     * 合同到期日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date endDate;
    /**
     * 我方企业
     */
    private String ourCompanyNames;

    /**
     * 地址
     */
    private String address;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否有效
     * 0-无效，1-有效
     */
    private Boolean enableFlg = false;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * 平均分（用于分页菜单展示 暂且不做保存 后期可做定时任务定期维护）
     */
    private BigDecimal averageScore;

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
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

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOurCompanyNames() {
        return ourCompanyNames;
    }

    public void setOurCompanyNames(String ourCompanyNames) {
        this.ourCompanyNames = ourCompanyNames;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
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
}
