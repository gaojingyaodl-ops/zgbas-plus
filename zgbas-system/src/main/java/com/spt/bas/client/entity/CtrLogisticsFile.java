package com.spt.bas.client.entity;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.vo.CtrLogisticsReqVo;
import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * 合同物流提货表附件表
 *
 * @Author MoonLight
 * @Date 2023/7/5 15:55
 * @Version 1.0
 */
@Entity
@Table(name = "t_ctr_logistics_file")
public class CtrLogisticsFile extends IdEntity {
    private static final long serialVersionUID = -1L;

    /**
     * 物流单ID
     */
    private Long logisticsId;

    /**
     * 物流提货单ID
     */
    private Long logisticsDeliveryId;

    /**
     * 附件类型
     */
    private String fileType;


    private String oldFileId;

    /**
     * 附件ID
     */
    private String fileId;

    /**
     * 签署标识
     */
    private Boolean signFlg = false;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 安心签合同编号
     */
    private String cfcaContractNo;

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 审批编号
     */
    private String approveNo;

    /**
     * 签署方
     */
    private String signCompanyName;

    /**
     * 印章类型
     */
    private String sealType;

    /**
     * 备注
     */
    private String remark;

    public CtrLogisticsFile() {
    }

    public CtrLogisticsFile(Long logisticsId, Long logisticsDeliveryId) {
        this.logisticsId = logisticsId;
        this.logisticsDeliveryId = logisticsDeliveryId;
    }

    public CtrLogisticsFile(Long logisticsId, Long logisticsDeliveryId, String fileType, String fileId, String contractNo, String cfcaContractNo, String approveNo, String remark) {
        this.logisticsId = logisticsId;
        this.logisticsDeliveryId = logisticsDeliveryId;
        this.fileType = fileType;
        this.fileId = fileId;
        this.contractNo = contractNo;
        this.cfcaContractNo = cfcaContractNo;
        this.approveNo = approveNo;
        this.remark = remark;
    }

    public CtrLogisticsFile(CtrLogisticsDelivery delivery, LogisticsEnum logisticsEnum, String fileId, String contractNo, String cfcaContractNo) {
        this.logisticsId = delivery.getLogisticsId();
        this.logisticsDeliveryId = delivery.getId();
        this.fileType = logisticsEnum.getLogisticsCode();
        this.fileId = fileId.endsWith(BasConstants.COMMA) ? fileId : fileId + BasConstants.COMMA;
        this.contractNo = contractNo;
        this.cfcaContractNo = cfcaContractNo;
        this.remark = delivery.getRemark();
    }

    public CtrLogisticsFile(CtrLogisticsDelivery delivery, String fileId, String cfcaContractNo, CtrLogisticsReqVo reqVo) {
        this.logisticsId = delivery.getLogisticsId();
        this.logisticsDeliveryId = delivery.getId();
        this.fileType = reqVo.getLogisticsEnum().getLogisticsCode();
        this.fileId = fileId.endsWith(BasConstants.COMMA) ? fileId : fileId + BasConstants.COMMA;
        this.contractNo = reqVo.getContractNo();
        this.cfcaContractNo = cfcaContractNo;
        this.remark = delivery.getRemark();
        this.signCompanyName = reqVo.getSignCompanyName();
        this.sealType = reqVo.getSealType();
    }

    public CtrLogisticsFile(Long logisticsId, String fileId, String cfcaContractNo, CtrLogisticsReqVo reqVo) {
        this.logisticsId = logisticsId;
        this.fileType = reqVo.getLogisticsEnum().getLogisticsCode();
        this.fileId = fileId.endsWith(BasConstants.COMMA) ? fileId : fileId + BasConstants.COMMA;
        this.contractNo = reqVo.getContractNo();
        this.cfcaContractNo = cfcaContractNo;
        this.signCompanyName = reqVo.getSignCompanyName();
        this.sealType = reqVo.getSealType();
    }

    public Long getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Long logisticsId) {
        this.logisticsId = logisticsId;
    }

    public Long getLogisticsDeliveryId() {
        return logisticsDeliveryId;
    }

    public void setLogisticsDeliveryId(Long logisticsDeliveryId) {
        this.logisticsDeliveryId = logisticsDeliveryId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCfcaContractNo() {
        return cfcaContractNo;
    }

    public void setCfcaContractNo(String cfcaContractNo) {
        this.cfcaContractNo = cfcaContractNo;
    }

    public String getOldFileId() {
        return oldFileId;
    }

    public void setOldFileId(String oldFileId) {
        this.oldFileId = oldFileId;
    }

    public Boolean getSignFlg() {
        return signFlg;
    }

    public void setSignFlg(Boolean signFlg) {
        this.signFlg = signFlg;
    }

    public String getSignCompanyName() {
        return signCompanyName;
    }

    public void setSignCompanyName(String signCompanyName) {
        this.signCompanyName = signCompanyName;
    }

    public String getSealType() {
        return sealType;
    }

    public void setSealType(String sealType) {
        this.sealType = sealType;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }
}
