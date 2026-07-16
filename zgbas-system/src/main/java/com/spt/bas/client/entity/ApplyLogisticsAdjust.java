package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物流调整申请单
 */
@Entity
@Table(name = "t_apply_logistics_adjust")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyLogisticsAdjust extends IdEntity implements IPmEntity {
    
    private static final long serialVersionUID = -2554866099497311658L;


    private Long contractId; //	bigint	合同id
    private String contractNo; //	varchar(50)	合同编号
    private String companyName; //	varchar(100)	企业名称
    private String productsName; //	varchar(100)	货名
    private BigDecimal totalNumber; //	decimal(16,3)	合同数量
    private BigDecimal dealPrice; //	decimal(16,2)	合同单价
    private BigDecimal totalAmount; //	decimal(16,2)	合同金额
    /**
     * 收付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;

    /**
     * 交货日期结束(到货时间)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDate;
    /**
     * 出库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date warehouseOutDate; //	datetime	出库日期
    
    private String carrier; //	varchar(50)	承运商
    private String deliveryAddr; //	varchar(100)	配送地址
    private BigDecimal outNumber; //	decimal(16,3)	已出库数量
    private BigDecimal transportCost = BigDecimal.ZERO; //	decimal(16,2)	运输费
    private BigDecimal warehouseCost = BigDecimal.ZERO; //	decimal(16,2)	仓储费
    private BigDecimal deliveryOutFee = BigDecimal.ZERO; //	decimal(16,2)	出库费
    private BigDecimal stevedorage = BigDecimal.ZERO; //	decimal(16,2)	装卸费
    private BigDecimal transportCost2; //	decimal(16,2)	修改运输费
    private BigDecimal warehouseCost2; //	decimal(16,2)	修改仓储费
    private BigDecimal deliveryOutFee2; //	decimal(16,2)	修改出库费
    private BigDecimal stevedorage2; //	decimal(16,2)	修改装卸费
    private String fileId; //	varchar(50)	附件id
    private String remark; //	varchar(1000)	调整原因
    private Long approveId; //	bigint	审批id
    private String status; //	char(1)	审批状态
    
    /**
     * 其他费用
     */
    private BigDecimal otherFee = BigDecimal.ZERO;

    /**
     * 修改其他费用
     */
    private BigDecimal otherFee2;

    /**
     * 出库单ID
     */
    private Long deliveryOutId;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getWarehouseOutDate() {
        return warehouseOutDate;
    }

    public void setWarehouseOutDate(Date warehouseOutDate) {
        this.warehouseOutDate = warehouseOutDate;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public BigDecimal getOutNumber() {
        return outNumber;
    }

    public void setOutNumber(BigDecimal outNumber) {
        this.outNumber = outNumber;
    }

    public BigDecimal getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(BigDecimal transportCost) {
        this.transportCost = transportCost;
    }

    public BigDecimal getWarehouseCost() {
        return warehouseCost;
    }

    public void setWarehouseCost(BigDecimal warehouseCost) {
        this.warehouseCost = warehouseCost;
    }

    public BigDecimal getDeliveryOutFee() {
        return deliveryOutFee;
    }

    public void setDeliveryOutFee(BigDecimal deliveryOutFee) {
        this.deliveryOutFee = deliveryOutFee;
    }

    public BigDecimal getStevedorage() {
        return stevedorage;
    }

    public void setStevedorage(BigDecimal stevedorage) {
        this.stevedorage = stevedorage;
    }

    public BigDecimal getTransportCost2() {
        return transportCost2;
    }

    public void setTransportCost2(BigDecimal transportCost2) {
        this.transportCost2 = transportCost2;
    }

    public BigDecimal getWarehouseCost2() {
        return warehouseCost2;
    }

    public void setWarehouseCost2(BigDecimal warehouseCost2) {
        this.warehouseCost2 = warehouseCost2;
    }

    public BigDecimal getDeliveryOutFee2() {
        return deliveryOutFee2;
    }

    public void setDeliveryOutFee2(BigDecimal deliveryOutFee2) {
        this.deliveryOutFee2 = deliveryOutFee2;
    }

    public BigDecimal getStevedorage2() {
        return stevedorage2;
    }

    public void setStevedorage2(BigDecimal stevedorage2) {
        this.stevedorage2 = stevedorage2;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getOtherFee2() {
        return otherFee2;
    }

    public void setOtherFee2(BigDecimal otherFee2) {
        this.otherFee2 = otherFee2;
    }

    public Long getDeliveryOutId() {
        return deliveryOutId;
    }

    public void setDeliveryOutId(Long deliveryOutId) {
        this.deliveryOutId = deliveryOutId;
    }
}
