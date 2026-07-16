package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 送货单
 */
@Entity
@Table(name = "t_ctr_contract_delivery")
@DynamicInsert
@DynamicUpdate
public class CtrContractDelivery extends IdEntity {

    /**
     * 我方抬头
     */
    private String ourCompanyName;
    /**
     *销售合同id
     */
    private Long  contractId;
    /**
     * 则一对接--客户订单号
     */
    private String customerOrderCode;
    /**
     * 则一对接--则一运单号
     */
    private String waybillCode;
    /**
     *销售合同编号
     */
    private String contractNo;
    /**
     * 出库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDate;
    /**
     *要求到货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDateMust;
    /**
     *     发货联系人
     */
    private String deliveryContactor;
    /**
     *发货联系电话
     */
    private String deliveryContactPhone;
    /**
     *提货地址
     */
    private String deliveryAddress;
    /**
     *承运商，手填
     */
    private String transportCompany;
    /**
     *送货联系人
     */
    private String transportContactor;
    /**
     *送货联系电话
     */
    private String transportContactPhone;
    /**
     *送货单位地址
     */
    private String transportAddress;
    /**
     *收货单位
     */
    private String receiveCompany;
    /**
     *需方id
     */
    private String receiveCompanyId;
    /**
     *收货联系人
     */
    private String receiveContactor;
    /**
     *收货联系电话
     */
    private String receiveContactPhone;
    /**
     *收货单位地址
     */
    private String receiveAddress;
    /**
     *备注
     */
    private String remark;
    /**
     *司机姓名
     */
    private String driverName;
    /**
     *司机电话
     */
    private String driverPhone;
    /**
     *车牌号
     */
    private String plateNumber;
    /**
     *司机身份证
     */
    private String driverCardNo;
    /**
     *出库数量
     */
    private BigDecimal carryNumber;
    /**
     *出库数量
     */
    private BigDecimal loadNumber;
    /**
     *运费费
     */
    private BigDecimal carryAmount;
    /**
     *出库费
     */
    private BigDecimal loadAmount;
    /**
     *合计金额运费费+出库费
     */
    private BigDecimal totalAmount;
    /**
     *0-无效，1-有效
     */
    private String enableFlg;

    private Long matchUserId;
    /**
     * 部门Id
     * @return
     */
    private Long deptId;

    /**
     * 出库状态
     */
    private  String  productStatus;

    /**
     * 确认收货状态
     */
    private  String  receiptStatus;

    public String getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getCustomerOrderCode() {
        return customerOrderCode;
    }

    public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getDeliveryDateMust() {
        return deliveryDateMust;
    }

    public void setDeliveryDateMust(Date deliveryDateMust) {
        this.deliveryDateMust = deliveryDateMust;
    }

    public String getDeliveryContactor() {
        return deliveryContactor;
    }

    public void setDeliveryContactor(String deliveryContactor) {
        this.deliveryContactor = deliveryContactor;
    }

    public String getDeliveryContactPhone() {
        return deliveryContactPhone;
    }

    public void setDeliveryContactPhone(String deliveryContactPhone) {
        this.deliveryContactPhone = deliveryContactPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(String transportCompany) {
        this.transportCompany = transportCompany;
    }

    public String getTransportContactor() {
        return transportContactor;
    }

    public void setTransportContactor(String transportContactor) {
        this.transportContactor = transportContactor;
    }

    public String getTransportContactPhone() {
        return transportContactPhone;
    }

    public void setTransportContactPhone(String transportContactPhone) {
        this.transportContactPhone = transportContactPhone;
    }

    public String getTransportAddress() {
        return transportAddress;
    }

    public void setTransportAddress(String transportAddress) {
        this.transportAddress = transportAddress;
    }

    public String getReceiveCompany() {
        return receiveCompany;
    }

    public void setReceiveCompany(String receiveCompany) {
        this.receiveCompany = receiveCompany;
    }

    public String getReceiveCompanyId() {
        return receiveCompanyId;
    }

    public void setReceiveCompanyId(String receiveCompanyId) {
        this.receiveCompanyId = receiveCompanyId;
    }

    public String getReceiveContactor() {
        return receiveContactor;
    }

    public void setReceiveContactor(String receiveContactor) {
        this.receiveContactor = receiveContactor;
    }

    public String getReceiveContactPhone() {
        return receiveContactPhone;
    }

    public void setReceiveContactPhone(String receiveContactPhone) {
        this.receiveContactPhone = receiveContactPhone;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
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

    public BigDecimal getCarryNumber() {
        return carryNumber;
    }

    public void setCarryNumber(BigDecimal carryNumber) {
        this.carryNumber = carryNumber;
    }

    public BigDecimal getLoadNumber() {
        return loadNumber;
    }

    public void setLoadNumber(BigDecimal loadNumber) {
        this.loadNumber = loadNumber;
    }

    public BigDecimal getCarryAmount() {
        return carryAmount;
    }

    public void setCarryAmount(BigDecimal carryAmount) {
        this.carryAmount = carryAmount;
    }

    public BigDecimal getLoadAmount() {
        return loadAmount;
    }

    public void setLoadAmount(BigDecimal loadAmount) {
        this.loadAmount = loadAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(String enableFlg) {
        this.enableFlg = enableFlg;
    }
}
