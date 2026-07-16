package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "t_ctr_out_in_ledger")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CtrOutInLedger extends IdEntity {
    private static final long serialVersionUID = 1530976683810599809L;

    /**
     * 操作时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date operTime;

    /**
     * 操作
     */
    private String operation;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 来源ID
     */
    private Long sourceId;

    /**
     * 货名
     */
    private String productsName;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 交货方式
     */
    private String deliveryMode;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 实际合同编号
     */
    private String realContractNo;

    /**
     * 审批ID
     */
    private Long approveId;

    /**
     * 我方企业名称
     */
    private String ourCompanyName;

    /**
     * 业务部门ID
     */
    private Long deptId;

    /**
     * 业务部门名称
     */
    private String deptName;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员名称
     */
    private String matchUserName;

    /**
     * 对方企业ID
     */
    private Long companyId;

    /**
     * 对方企业名称
     */
    private String companyName;

    /**
     * 对方传真
     */
    private String companyFax;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 可提取数量
     */
    private BigDecimal extractNumber;

    /**
     * 入\出库数量
     */
    private BigDecimal warehouseNumber;

    /**
     * 结余数量
     */
    private BigDecimal surplusNumber;

    /**
     * 承运商
     */
    private String carrier;

    /**
     * 仓库地址/送达地址
     */
    private String deliveryAddr;

    /**
     * 仓库电话
     */
    private String deliveryPhone;

    /**
     * 其他费用
     */
    private BigDecimal otherAmount = BigDecimal.ZERO;

    /**
     * 运输费
     */
    private BigDecimal transportAmount = BigDecimal.ZERO;

    /**
     * 出库费用 元/吨
     */
    private BigDecimal deliveryOutFee;

    /**
     * 车牌号
     */
    private String plateNumber;

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

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getRealContractNo() {
        return realContractNo;
    }

    public void setRealContractNo(String realContractNo) {
        this.realContractNo = realContractNo;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
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

    public String getCompanyFax() {
        return companyFax;
    }

    public void setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getExtractNumber() {
        return extractNumber;
    }

    public void setExtractNumber(BigDecimal extractNumber) {
        this.extractNumber = extractNumber;
    }

    public BigDecimal getWarehouseNumber() {
        return warehouseNumber;
    }

    public void setWarehouseNumber(BigDecimal warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
    }

    public BigDecimal getSurplusNumber() {
        return surplusNumber;
    }

    public void setSurplusNumber(BigDecimal surplusNumber) {
        this.surplusNumber = surplusNumber;
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

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public BigDecimal getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(BigDecimal otherAmount) {
        this.otherAmount = otherAmount;
    }

    public BigDecimal getTransportAmount() {
        return transportAmount;
    }

    public void setTransportAmount(BigDecimal transportAmount) {
        this.transportAmount = transportAmount;
    }

    public BigDecimal getDeliveryOutFee() {
        return deliveryOutFee;
    }

    public void setDeliveryOutFee(BigDecimal deliveryOutFee) {
        this.deliveryOutFee = deliveryOutFee;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
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
}
