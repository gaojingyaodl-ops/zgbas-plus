package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 申请单-中游合同收货确认申请
 * </p>
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_apply_confirm_receipt_dcsx")
public class ApplyConfirmReceiptDcsx extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = 2963973584560508977L;

    /**
     * 合同id
     */
    private Long contractId;
    /**
     * 合同编号
     */
    private String contractNo;
    /**
     * 审批状态		N-新增，A-审批中，B-驳回，D-完成
     */
    private String status;
    /**
     * 审批id
     */
    private Long approveId;

    /**
     * 出库单ID
     */
    private Long deliveryOutId;

    /**
     * 我方
     */
    private String ourCompanyName;
    
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 公司名(签署公司)
     */
    private String companyName;

    /**
     * 签署短链接
     */
    private String signShortUrl;

    /**
     * 签署人姓名
     */
    private String signUserName;

    /**
     * 签署人手机号
     */
    private String signUserPhone;

    /**
     * 签署二维码图片Base64
     */
    private String signImageData;

    /**
     * 确认收货电子签是否已签署
     */
    private Boolean signFlg = false;

    /**
     * cfca安心签合同编号
     */
    private String signCfcaContractNo;

    /**
     * 实际到货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmReceiptDate;

    /**
     * 物流费用
     */
    private String logisticsCosts;

    /**
     * 仓库/配送地址
     */
    private String deliveryAddr;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 司机
     */
    private String driverName;
    /**
     * 司机电话
     */
    private String driverPhone;
    /**
     * 驾驶员身份证号
     */
    private String driverCardNo;
    /**
     * 备注
     */
    private String remark;
    /**
     * 附件id
     */
    private String fileId;

    private String applyNo;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 签署短链接生成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+08:00")
    private Date signCreatedDate;

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

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

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

    public Date getConfirmReceiptDate() {
        return confirmReceiptDate;
    }

    public void setConfirmReceiptDate(Date confirmReceiptDate) {
        this.confirmReceiptDate = confirmReceiptDate;
    }

    public String getLogisticsCosts() {
        return logisticsCosts;
    }

    public void setLogisticsCosts(String logisticsCosts) {
        this.logisticsCosts = logisticsCosts;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
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

    public String getDriverCardNo() {
        return driverCardNo;
    }

    public void setDriverCardNo(String driverCardNo) {
        this.driverCardNo = driverCardNo;
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

    public String getSignShortUrl() {
        return signShortUrl;
    }

    public void setSignShortUrl(String signShortUrl) {
        this.signShortUrl = signShortUrl;
    }

    public String getSignUserName() {
        return signUserName;
    }

    public void setSignUserName(String signUserName) {
        this.signUserName = signUserName;
    }

    public String getSignUserPhone() {
        return signUserPhone;
    }

    public void setSignUserPhone(String signUserPhone) {
        this.signUserPhone = signUserPhone;
    }

    public String getSignImageData() {
        return signImageData;
    }

    public void setSignImageData(String signImageData) {
        this.signImageData = signImageData;
    }

    public Long getDeliveryOutId() {
        return deliveryOutId;
    }

    public void setDeliveryOutId(Long deliveryOutId) {
        this.deliveryOutId = deliveryOutId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Boolean getSignFlg() {
        return signFlg;
    }

    public void setSignFlg(Boolean signFlg) {
        this.signFlg = signFlg;
    }

    public ApplyConfirmReceiptDcsx() {
    }

    public ApplyConfirmReceiptDcsx(String fileId, String applyNo) {
        this.fileId = fileId;
        this.applyNo = applyNo;
    }

    public Date getSignCreatedDate() {
        return signCreatedDate;
    }

    public void setSignCreatedDate(Date signCreatedDate) {
        this.signCreatedDate = signCreatedDate;
    }

    public String getSignCfcaContractNo() {
        return signCfcaContractNo;
    }

    public void setSignCfcaContractNo(String signCfcaContractNo) {
        this.signCfcaContractNo = signCfcaContractNo;
    }
}
