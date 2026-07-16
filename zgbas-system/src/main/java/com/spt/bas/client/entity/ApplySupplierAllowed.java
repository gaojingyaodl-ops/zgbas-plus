package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 供应商准入申请
 */
@Entity
@Table(name = "t_apply_supplier_allowed")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplySupplierAllowed extends IdEntity implements IPmEntity {

    private Long approveId;
    private String status;
    private Long companyId;
    private String companyName;
    private Long applyUserId;
    private String applyUserName;
    private Long enterpriseId;
    private String fileId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date startDate; // 成立日期
    private Integer insuredNumber; // 参保人员
    private String existJudicialFreeze; // 是否存在执行冻结案件
    private String existOverDueTax; // 是否存在欠税记录
    private Integer billImageNumber; // 与下游的开票照片(张)
    private String businessAddress; // 实际办公地址
    private Integer officeImageNumber; // 办公场地照片(张)
    private String supplierRating; // {'白名单': 'W', '黑名单': 'B', '灰名单': 'G'},
    private String supplierCategory; // 企业性质

    private Long deptId; //部门Id
    private String supplierDeliveryOne; // 是否允许供应商配送
    private String supplierFutureOne; // 是否允许供应商远期合同

    /**
     * 是否已存在合作合同
     */
    private Boolean cooperationFlg = false;

    public String getSupplierCategory() {
        return supplierCategory;
    }

    public void setSupplierCategory(String supplierCategory) {
        this.supplierCategory = supplierCategory;
    }

    public String getSupplierDeliveryOne() {
        return supplierDeliveryOne;
    }

    public void setSupplierDeliveryOne(String supplierDeliveryOne) {
        this.supplierDeliveryOne = supplierDeliveryOne;
    }

    public String getSupplierFutureOne() {
        return supplierFutureOne;
    }

    public void setSupplierFutureOne(String supplierFutureOne) {
        this.supplierFutureOne = supplierFutureOne;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApproveId() {
        return approveId;
    }

    public String getStatus() {
        return status;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getExistJudicialFreeze() {
        return existJudicialFreeze;
    }

    public void setExistJudicialFreeze(String existJudicialFreeze) {
        this.existJudicialFreeze = existJudicialFreeze;
    }

    public String getExistOverDueTax() {
        return existOverDueTax;
    }

    public void setExistOverDueTax(String existOverDueTax) {
        this.existOverDueTax = existOverDueTax;
    }

    public Integer getBillImageNumber() {
        return billImageNumber;
    }

    public void setBillImageNumber(Integer billImageNumber) {
        this.billImageNumber = billImageNumber;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public Integer getOfficeImageNumber() {
        return officeImageNumber;
    }

    public void setOfficeImageNumber(Integer officeImageNumber) {
        this.officeImageNumber = officeImageNumber;
    }

    public String getSupplierRating() {
        return supplierRating;
    }

    public void setSupplierRating(String supplierRating) {
        this.supplierRating = supplierRating;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public Integer getInsuredNumber() {
        return insuredNumber;
    }

    public void setInsuredNumber(Integer insuredNumber) {
        this.insuredNumber = insuredNumber;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Boolean getCooperationFlg() {
        return cooperationFlg;
    }

    public void setCooperationFlg(Boolean cooperationFlg) {
        this.cooperationFlg = cooperationFlg;
    }
}