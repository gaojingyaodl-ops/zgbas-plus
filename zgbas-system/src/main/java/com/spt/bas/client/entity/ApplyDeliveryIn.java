package com.spt.bas.client.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 申请单-入库申请单
 */
@Entity
@Table(name = "t_apply_delivery_in")
public class ApplyDeliveryIn extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = 1530976683810599809L;
    /**
     * 企业ID
     */
    private Long enterpriseId;
    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 物流附件单据ID
     */
    private Long logisticsFileId;

    /**
     * 业务编号
     */
    private String businessNo;
    /**
     * 合同编号
     */
    private String contractNo;
    /**
     * 配送方式		自提ZT、配送PS
     */
    private String deliveryType;
    /**
     * 是否需转货权
     */
    private boolean transferFlg;
    /**
     * 供货商名称
     */
    private String companyName;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 联系地址
     */
    private String contactAddr;
    /**
     * 提货电话
     */
    private String deliveryPhone;
    /**
     * 提货地址
     */
    private String deliveryAddr;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 附件id
     */
    private String fileId;
    /**
     * 审批状态
     * N-新增，A-审批中，B-驳回，D-完成
     */
    private String status;
    /**
     * 审批id
     */
    private Long approveId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 审批编号
     */
    private String applyNo;
    /**
     * 仓位/货位
     */
    private String warehousePosition;
    /**
     * 批号
     */
    private String warehouseBatchNo;
    /**
     * 仓库单号
     */
    private String warehouseNo;
    /**
     * 库存类型
     */
    private String stockType;
    /**
     * 货权类型
     */
    private String spotType;
    /**
     * 入库方式
     */
    private String warehouseInType;
    /**
     * 上家提单号
     */
    private String billNoPre;
    /**
     * 入库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date warehouseInDate;
    /**
     * 库存性质
     */
    private String warehouseKind;

    /**
     * 附件类型id
     */
    private Long fileTypeId;

    /**
     * 业务类型
     * DCSX:代采赊销
     */
    private String businessType;

    private Long deptId;

    /**
     * 承运商
     */
    private  String  carrier;

    /**
     * 中标状态
     */
    private  String  biddinStatus;

    /**
     * 第三方物流
     */
    private  String logisticsQuotation;

    public String getLogisticsQuotation() {
        return logisticsQuotation;
    }

    public void setLogisticsQuotation(String logisticsQuotation) {
        this.logisticsQuotation = logisticsQuotation;
    }

    public String getBiddinStatus() {
        return biddinStatus;
    }

    public void setBiddinStatus(String biddinStatus) {
        this.biddinStatus = biddinStatus;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
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

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public boolean isTransferFlg() {
        return transferFlg;
    }

    public void setTransferFlg(boolean transferFlg) {
        this.transferFlg = transferFlg;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getContactAddr() {
        return StringUtils.isBlank(contactAddr) ? "" : contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String approveNo) {
        this.applyNo = approveNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getDeliveryAddr() {
        return StringUtils.isBlank(deliveryAddr) ? "" : deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getWarehousePosition() {
        return warehousePosition;
    }

    public void setWarehousePosition(String warehousePosition) {
        this.warehousePosition = warehousePosition;
    }

    public String getWarehouseBatchNo() {
        return warehouseBatchNo;
    }

    public void setWarehouseBatchNo(String warehouseBatchNo) {
        this.warehouseBatchNo = warehouseBatchNo;
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    public String getSpotType() {
        return spotType;
    }

    public void setSpotType(String spotType) {
        this.spotType = spotType;
    }

    public String getWarehouseInType() {
        return warehouseInType;
    }

    public void setWarehouseInType(String warehouseInType) {
        this.warehouseInType = warehouseInType;
    }

    public String getBillNoPre() {
        return billNoPre;
    }

    public void setBillNoPre(String billNoPre) {
        this.billNoPre = billNoPre;
    }

    public Date getWarehouseInDate() {
        return warehouseInDate;
    }

    public void setWarehouseInDate(Date warehouseInDate) {
        this.warehouseInDate = warehouseInDate;
    }

    public String getWarehouseKind() {
        return warehouseKind;
    }

    public void setWarehouseKind(String warehouseKind) {
        this.warehouseKind = warehouseKind;
    }

    public Long getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getLogisticsFileId() {
        return logisticsFileId;
    }

    public void setLogisticsFileId(Long logisticsFileId) {
        this.logisticsFileId = logisticsFileId;
    }
}
