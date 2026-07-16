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
 * 申请单-出库申请单
 */
@Entity
@Table(name = "t_apply_delivery_out")
public class ApplyDeliveryOut extends IdEntity implements IPmEntity {


    private static final long serialVersionUID = 8435560825962509054L;
    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 物流单ID
     */
    private Long logisticsId;

    /**
     * 物流提货ID
     */
    private Long logisticsDeliveryId;

    /**
     * 业务编号
     */
    private String businessNo;
    /**
     * 合同编号
     */
    private String contractNo;
    /**
     * 审批id
     */
    private Long approveId;
    /**
     * 审批编号
     */
    private String applyNo;
    /**
     * 交货日期
     */
    private Date deliveryDate;
    /**
     * 配送方式
     */
    private String deliveryType;
    /**
     * 交货方式
     */
    private String deliveryMode;
    /**
     * 仓库/配送电话
     */
    private String deliveryPhone;
    /**
     * 仓库/配送地址
     */
    private String deliveryAddr;
    /**
     * 已付金额
     */
    private BigDecimal payAmount;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 驾驶员身份证号
     */
    private String driverCardNo;
    /**
     * 司机
     */
    private String driverName;
    /**
     * 司机电话
     */
    private String driverPhone;
    /**
     * 附件id
     */
    private String fileId;
    /**
     * 状态
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 企业账套ID
     */
    private Long enterpriseId;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 公司名称
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
     * 柜数
     */
    private String countersNumber;
    /**
     * 交货仓库电话
     */
    private String warehousePhone;
    /**
     * 出库方式
     */
    private String warehouseOutType;
    /**
     * 出库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date warehouseOutDate;
    /**
     * 出库费用 元/吨
     */
    private BigDecimal deliveryOutFee;
    /**
     * 运输费
     */
    private BigDecimal transportAmount;
    /**
     * 仓储费
     */
    private BigDecimal warehouseAmount;

    /**
     * 采购方企业Id
     */
    private Long buyCompanyId;

    /**
     * 是否确认收货状态 0：未确认 1：已确认 2：确认中
     */
    private String confirmFlg;

    /**
     * 对应的收货确认id
     */
    private Long confirmReceiptApplyId;
    
    /**
     * 中游是否确认收货状态 0：未确认 1：已确认 2：确认中
     */
    private String confirmDcsxFlg = "0";

    /**
     * 对应的中游收货确认id
     */
    private Long confirmReceiptDcsxApplyId;

    /**
     * 中游确认收货数量（包含锁定数量）
     */
    private BigDecimal confirmDcsxNumber = BigDecimal.ZERO;

    private Long wxUserId;

    /**
     * 交货的仓库地址
     */
    private String wareCompanyName;

    /**
     * 申请来源 0：核心管理系统 1：采购管家小程序
     */
    private String applySource;

    /**
     * 附件类型
     */
    private Long fileTypeId;

    /**
     * 业务类型
     * DCSX:代采赊销
     */
    private String businessType;

    /**
     * 部门Id
     * @return
     */
    private Long deptId;
    /**
     * 承运商
     */
    private  String  carrier;

    /**
     * 则一对接--则一运单号
     */
    private String waybillCode;

    /**
     * 运费结算标识
     */
    private String freightSettlement;

    /**
     * 装卸费
     */
    private  BigDecimal  stevedorage;


    /**
     * 评分
     */
    private  BigDecimal score;

    /**
     * 描述
     */
    private  String evaluatev;

    /**
     * 其他费用
     */
    private BigDecimal otherFee;

    /**
     * 运费是否超出
     */
    private Boolean feeExceedFlg = false;

    /**
     * 运费超出金额
     */
    private BigDecimal feeExceedAmount;

    /**
     * 所属区域
     */
    private String ownRegion;

    /**
     * 托盘利息
     */
    private BigDecimal tpInterest;

    /**
     * 托盘天数
     */
    private Integer tpDays;

    /**
     * 上游采购单价
     */
    private BigDecimal buyDealPrice;

    /**
     * 托盘利率
     */
    private BigDecimal tpRate;

    /**
     * 上游付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyPayFullTime;

    /**
     * 超额标识
     */
    private Boolean overageFlg = false;

    /**
     * 超额提示
     */
    private String overageMessage;

    private BigDecimal defaultNum(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public String getEvaluatev() {
        return evaluatev;
    }

    public void setEvaluatev(String evaluatev) {
        this.evaluatev = evaluatev;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getStevedorage() {
        return stevedorage;
    }

    public void setStevedorage(BigDecimal stevedorage) {
        this.stevedorage = stevedorage;
    }

    public String getFreightSettlement() {
        return freightSettlement;
    }

    public void setFreightSettlement(String freightSettlement) {
        this.freightSettlement = freightSettlement;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String approveNo) {
        this.applyNo = approveNo;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
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

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
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

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

    public String getWarehouseOutType() {
        return warehouseOutType;
    }

    public void setWarehouseOutType(String warehouseOutType) {
        this.warehouseOutType = warehouseOutType;
    }

    public Date getWarehouseOutDate() {
        return warehouseOutDate;
    }

    public void setWarehouseOutDate(Date warehouseOutDate) {
        this.warehouseOutDate = warehouseOutDate;
    }

    public String getCountersNumber() {
        return countersNumber;
    }

    public void setCountersNumber(String countersNumber) {
        this.countersNumber = countersNumber;
    }

    public String getWarehousePhone() {
        return warehousePhone;
    }

    public void setWarehousePhone(String warehousePhone) {
        this.warehousePhone = warehousePhone;
    }

    public BigDecimal getDeliveryOutFee() {
        return deliveryOutFee;
    }

    public void setDeliveryOutFee(BigDecimal deliveryOutFee) {
        this.deliveryOutFee = deliveryOutFee;
    }

    public BigDecimal getTransportAmount() {
        return transportAmount;
    }

    public void setTransportAmount(BigDecimal transportAmount) {
        this.transportAmount = transportAmount;
    }

    public BigDecimal getWarehouseAmount() {
        return warehouseAmount;
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public Long getBuyCompanyId() {
        return buyCompanyId;
    }

    public void setBuyCompanyId(Long buyCompanyId) {
        this.buyCompanyId = buyCompanyId;
    }

    public String getConfirmFlg() {
        return confirmFlg;
    }

    public void setConfirmFlg(String confirmFlg) {
        this.confirmFlg = confirmFlg;
    }

    public Long getConfirmReceiptApplyId() {
        return confirmReceiptApplyId;
    }

    public void setConfirmReceiptApplyId(Long confirmReceiptApplyId) {
        this.confirmReceiptApplyId = confirmReceiptApplyId;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public String getWareCompanyName() {
        return wareCompanyName;
    }

    public void setWareCompanyName(String wareCompanyName) {
        this.wareCompanyName = wareCompanyName;
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

    public String getConfirmDcsxFlg() {
        return confirmDcsxFlg;
    }

    public void setConfirmDcsxFlg(String confirmDcsxFlg) {
        this.confirmDcsxFlg = confirmDcsxFlg;
    }

    public Long getConfirmReceiptDcsxApplyId() {
        return confirmReceiptDcsxApplyId;
    }

    public void setConfirmReceiptDcsxApplyId(Long confirmReceiptDcsxApplyId) {
        this.confirmReceiptDcsxApplyId = confirmReceiptDcsxApplyId;
    }

    public BigDecimal getConfirmDcsxNumber() {
        return confirmDcsxNumber;
    }

    public void setConfirmDcsxNumber(BigDecimal confirmDcsxNumber) {
        this.confirmDcsxNumber = confirmDcsxNumber;
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

    public Boolean getFeeExceedFlg() {
        return feeExceedFlg;
    }

    public void setFeeExceedFlg(Boolean feeExceedFlg) {
        this.feeExceedFlg = feeExceedFlg;
    }

    public BigDecimal getFeeExceedAmount() {
        return feeExceedAmount;
    }

    public void setFeeExceedAmount(BigDecimal feeExceedAmount) {
        this.feeExceedAmount = feeExceedAmount;
    }

    public String getOwnRegion() {
        return ownRegion;
    }

    public void setOwnRegion(String ownRegion) {
        this.ownRegion = ownRegion;
    }

    public BigDecimal getTpInterest() {
        return tpInterest;
    }

    public void setTpInterest(BigDecimal tpInterest) {
        this.tpInterest = tpInterest;
    }

    public Integer getTpDays() {
        return tpDays;
    }

    public void setTpDays(Integer tpDays) {
        this.tpDays = tpDays;
    }

    public BigDecimal getBuyDealPrice() {
        return buyDealPrice;
    }

    public void setBuyDealPrice(BigDecimal buyDealPrice) {
        this.buyDealPrice = buyDealPrice;
    }

    public BigDecimal getTpRate() {
        return defaultNum(tpRate);
    }

    public void setTpRate(BigDecimal tpRate) {
        this.tpRate = tpRate;
    }

    public Date getBuyPayFullTime() {
        return buyPayFullTime;
    }

    public void setBuyPayFullTime(Date buyPayFullTime) {
        this.buyPayFullTime = buyPayFullTime;
    }

    public Boolean getOverageFlg() {
        return overageFlg;
    }

    public void setOverageFlg(Boolean overageFlg) {
        this.overageFlg = overageFlg;
    }

    public String getOverageMessage() {
        return overageMessage;
    }

    public void setOverageMessage(String overageMessage) {
        this.overageMessage = overageMessage;
    }
}
