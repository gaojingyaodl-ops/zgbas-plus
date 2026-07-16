package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 申请单-收货确认申请
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-10 11:43
 */
@Entity
@Table(name = "t_apply_confirm_receipt")
public class ApplyConfirmReceipt extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = -761948221926089378L;

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
     * 备注
     */
    private String remark;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 公司名
     */
    private String companyName;

    /**
     * 附件id
     */
    private String fileId;

    private String applyNo;

    /**
     * 收货时间
     */
    private Date deliveryDateTo;

    /**
     * 该批次的发货金额
     */
    private BigDecimal confirmReceiptAmount;

    /**
     * 该批次的发货数量
     */
    private BigDecimal confirmReceiptNumber;

    /**
     * Z：自提；P：配送
     */
    private String deliveryType;

    /**
     * 发货地
     */
    private String deliveryAddrSell;

    /**
     * 收货地
     */
    private String deliveryAddr;

    /**
     * 仓库地址Id
     */
    private Long warehouseId;

    /**
     * 客户上传的收货确认单
     */
    private String uploadFileId;

    /**
     * 申请来源 0：核心管理系统 1：采购管家小程序
     */
    private String applySource;

    private Long wxUserId;

    /**
     * 附件类型
     */
    private Long fileTypeId;

    /**
     * 实际到货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmReceiptDate;

    /**
     * 损耗数量
     */
    private BigDecimal lossNumber;

    /**
     * 损耗类型
     * 1-上游损耗；2-物流损耗；
     */
    private String lossType;

    /**
     * 损耗金额
     */
    private BigDecimal lossAmount;

    /**
     * 物流方承担损耗金额
     */
    private BigDecimal lossAmountByLogistics;

    /**
     * 实际物流费用
     */
    private BigDecimal lossAmountByActual;

    /**
     * 供应商承担损耗金额
     */
    private BigDecimal lossAmountBySupplier;

    /**
     * 我方承担损耗金额
     */
    private BigDecimal lossAmountByOur;

    /**
     * 该批次货物预定的收款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date actualContractPayFullTime;

    /**
     * 业务类型
     * DCSX:代采赊销
     */
    private String businessType;


    /**
     * 则一订单号
     */
    private String   zyCode;



    /**
     * 则一送达时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date arriveTime;

    /**
     * 則以運單號
     */
    private String waybillCode;

    /**
     * 预算物流费用
     */
    private String logisticsCosts;

    /**
     * 物流附件单据ID
     */
    private Long logisticsFileId;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机身份证号
     */
    private String driverCardNo;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 司机手机号
     */
    private String driverPhone;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getZyCode() {
        return zyCode;
    }

    public void setZyCode(String zyCode) {
        this.zyCode = zyCode;
    }

    public Date getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Date arriveTime) {
        this.arriveTime = arriveTime;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }


    public BigDecimal getConfirmReceiptAmount() {
        return defaultNum(confirmReceiptAmount);
    }

    public void setConfirmReceiptAmount(BigDecimal confirmReceiptAmount) {
        this.confirmReceiptAmount = confirmReceiptAmount;
    }

    public BigDecimal getConfirmReceiptNumber() {
        return defaultNum(confirmReceiptNumber);
    }

    public void setConfirmReceiptNumber(BigDecimal confirmReceiptNumber) {
        this.confirmReceiptNumber = confirmReceiptNumber;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryAddrSell() {
        return deliveryAddrSell;
    }

    public void setDeliveryAddrSell(String deliveryAddrSell) {
        this.deliveryAddrSell = deliveryAddrSell;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getUploadFileId() {
        return uploadFileId;
    }

    public void setUploadFileId(String uploadFileId) {
        this.uploadFileId = uploadFileId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo(Date deliveryDateTo) {
        this.deliveryDateTo = deliveryDateTo;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public Long getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public Date getConfirmReceiptDate() {
        return confirmReceiptDate;
    }

    public void setConfirmReceiptDate(Date confirmReceiptDate) {
        this.confirmReceiptDate = confirmReceiptDate;
    }

    public BigDecimal getLossNumber() {
        return defaultNum(lossNumber);
    }

    public void setLossNumber(BigDecimal lossNumber) {
        this.lossNumber = lossNumber;
    }

    public BigDecimal getLossAmount() {
        return defaultNum(lossAmount);
    }

    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    public BigDecimal getLossAmountByLogistics() {
        return defaultNum(lossAmountByLogistics);
    }

    public void setLossAmountByLogistics(BigDecimal lossAmountByLogistics) {
        this.lossAmountByLogistics = lossAmountByLogistics;
    }

    public BigDecimal getLossAmountByActual() {
        return defaultNum(lossAmountByActual);
    }

    public void setLossAmountByActual(BigDecimal lossAmountByActual) {
        this.lossAmountByActual = lossAmountByActual;
    }

    public BigDecimal getLossAmountBySupplier() {
        return defaultNum(lossAmountBySupplier);
    }

    public void setLossAmountBySupplier(BigDecimal lossAmountBySupplier) {
        this.lossAmountBySupplier = lossAmountBySupplier;
    }

    public BigDecimal getLossAmountByOur() {
        return defaultNum(lossAmountByOur);
    }

    public void setLossAmountByOur(BigDecimal lossAmountByOur) {
        this.lossAmountByOur = lossAmountByOur;
    }

    public Date getActualContractPayFullTime() {
        return actualContractPayFullTime;
    }

    public void setActualContractPayFullTime(Date actualContractPayFullTime) {
        this.actualContractPayFullTime = actualContractPayFullTime;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    private BigDecimal defaultNum(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public String getLossType() {
        return lossType;
    }

    public void setLossType(String lossType) {
        this.lossType = lossType;
    }

    public String getLogisticsCosts() {
        return logisticsCosts;
    }

    public void setLogisticsCosts(String logisticsCosts) {
        this.logisticsCosts = logisticsCosts;
    }

    public Long getLogisticsFileId() {
        return logisticsFileId;
    }

    public void setLogisticsFileId(Long logisticsFileId) {
        this.logisticsFileId = logisticsFileId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverCardNo() {
        return driverCardNo;
    }

    public void setDriverCardNo(String driverCardNo) {
        this.driverCardNo = driverCardNo;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }
}
