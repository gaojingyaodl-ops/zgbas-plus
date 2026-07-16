package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.BatchSize;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 提货单/委托配送单
 *
 * @Author: gaojy
 * @create 2022/3/15 17:29
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_ctr_contract_loading")
public class CtrContractLoading extends IdEntity {

    private static final long serialVersionUID = 8663831675197964966L;
    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 品名
     */
    private String productName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 单位
     */
    private String numberUnit;

    /**
     * 备注
     */
    private String remark;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机手机号
     */
    private String driverPhone;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 司机身份证
     */
    private String driverCardNo;

    /**
     * 电子合同编号
     */
    private String cfcaContractNo;

    /**
     * 签署短链接URL
     */
    private String shortUrl;

    /**
     * 附件ID
     */
    private String fileId;

    /**
     * 提货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date loadingDate;

    /**
     * 签署日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
    private Date signDate;

    /**
     * 是否有效
     */
    private Boolean enableFlg;

    /**
     * 企业账号ID
     */
    private Long enterpriseId;

    /**
     * 单据类型 T-提货单;P委托配送单
     */
    private String billType;

    /**
     * 委托配送单-单号
     */
    private String deliveryNo;

    /**
     * 委托配送单-联系人
     */
    private String contactName;

    /**
     * 委托配送单-联系电话
     */
    private String contactPhone;

    /**
     * 委托配送单-配送地址
     */
    private String contactAddress;

    /**
     * 关联盖章审批单号
     */
    private String sealUsageApproveNo;

    /**
     * 关联盖章审批单ID
     */
    private Long sealUsageApproveId;

    private List<CtrContractLoadingDetail> loadingDetails;

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getDriverCardNo() {
        return driverCardNo;
    }

    public void setDriverCardNo(String driverCardNo) {
        this.driverCardNo = driverCardNo;
    }

    public String getCfcaContractNo() {
        return cfcaContractNo;
    }

    public void setCfcaContractNo(String cfcaContractNo) {
        this.cfcaContractNo = cfcaContractNo;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Date getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(Date loadingDate) {
        this.loadingDate = loadingDate;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
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

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getSealUsageApproveNo() {
        return sealUsageApproveNo;
    }

    public void setSealUsageApproveNo(String sealUsageApproveNo) {
        this.sealUsageApproveNo = sealUsageApproveNo;
    }

    public Long getSealUsageApproveId() {
        return sealUsageApproveId;
    }

    public void setSealUsageApproveId(Long sealUsageApproveId) {
        this.sealUsageApproveId = sealUsageApproveId;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "loading")
    @BatchSize(size = 10)
    public List<CtrContractLoadingDetail> getLoadingDetails() {
        return loadingDetails;
    }

    public void setLoadingDetails(List<CtrContractLoadingDetail> loadingDetails) {
        this.loadingDetails = loadingDetails;
    }

    public CtrContractLoading() {
    }
}
