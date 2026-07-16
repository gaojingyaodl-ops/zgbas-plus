package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 合同物流提货表
 *
 * @Author MoonLight
 * @Date 2023/7/5 15:55
 * @Version 1.0
 */
@Entity
@Table(name = "t_ctr_logistics_delivery")
public class CtrLogisticsDelivery extends IdEntity {
    private static final long serialVersionUID = -1L;

    /**
     * 物流单ID
     */
    private Long logisticsId;

    /**
     * 车次
     */
    private Long trainNum;

    /**
     * 提货数量
     */
    private BigDecimal logisticsNumber = BigDecimal.ZERO;

    /**
     * 提货次数
     */
    private String logisticsCount;

    /**
     * 交货方式
     */
    private String deliveryType;

    /**
     * 手机号保护标识
     */
    private String phoneProtect;

    /**
     * 提货单（配送单）备注
     */
    private String remark;

    /**
     * 送货通知单备注
     */
    private String deliveryRemark;

    /**
     * 日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date logisticsDate;

    /**
     * 实际提货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date realDeliveryDate;

    /**
     * 实际到货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date realArrivalDate;
    
    

    /**
     * 联系人
     */
    private String masterPorter;

    /**
     * 联系电话
     */
    private String masterPhone;

    /**
     * 承运商
     */
    private String carrier;

    /**
     * 承运商Id
     */
    private Long carrierId;

    /**
     * 运费总金额
     */
    private BigDecimal transportAmount;
    
    /**
     * 出库费总金额
     */
    private BigDecimal deliveryOutFee;
    
    /**
     * 其他费用
     */
    private BigDecimal otherFee;

    /**
     * 装卸费
     */
    private BigDecimal stevedorage;

    public CtrLogisticsDelivery() {
    }

    public CtrLogisticsDelivery(Long logisticsId, Long trainNum, BigDecimal logisticsNumber, String deliveryType, String phoneProtect,String logisticsCount,String carrier,Long carrierId) {
        this.logisticsId = logisticsId;
        this.trainNum = trainNum;
        this.logisticsNumber = logisticsNumber;
        this.deliveryType = deliveryType;
        this.phoneProtect = phoneProtect;
        this.logisticsCount = logisticsCount;
        this.carrier = carrier;
        this.carrierId = carrierId;
    }

    public CtrLogisticsDelivery(Long trainNum, BigDecimal logisticsNumber, String logisticsCount, String deliveryType, String phoneProtect, String remark, String deliveryRemark, Date logisticsDate, Date realDeliveryDate, Date realArrivalDate, String masterPorter, String masterPhone, String carrier, Long carrierId, BigDecimal transportAmount, BigDecimal deliveryOutFee, BigDecimal otherFee) {
        this.trainNum = trainNum;
        this.logisticsNumber = logisticsNumber;
        this.logisticsCount = logisticsCount;
        this.deliveryType = deliveryType;
        this.phoneProtect = phoneProtect;
        this.remark = remark;
        this.deliveryRemark = deliveryRemark;
        this.logisticsDate = logisticsDate;
        this.realDeliveryDate = realDeliveryDate;
        this.realArrivalDate = realArrivalDate;
        this.masterPorter = masterPorter;
        this.masterPhone = masterPhone;
        this.carrier = carrier;
        this.carrierId = carrierId;
        this.transportAmount = transportAmount;
        this.deliveryOutFee = deliveryOutFee;
        this.otherFee = otherFee;
    }

    public Long getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Long logisticsId) {
        this.logisticsId = logisticsId;
    }

    public Long getTrainNum() {
        return trainNum;
    }

    public void setTrainNum(Long trainNum) {
        this.trainNum = trainNum;
    }

    public BigDecimal getLogisticsNumber() {
        return logisticsNumber;
    }

    public void setLogisticsNumber(BigDecimal logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getPhoneProtect() {
        return phoneProtect;
    }

    public void setPhoneProtect(String phoneProtect) {
        this.phoneProtect = phoneProtect;
    }

    public String getLogisticsCount() {
        return logisticsCount;
    }

    public void setLogisticsCount(String logisticsCount) {
        this.logisticsCount = logisticsCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getLogisticsDate() {
        return logisticsDate;
    }

    public void setLogisticsDate(Date logisticsDate) {
        this.logisticsDate = logisticsDate;
    }

    public String getMasterPorter() {
        return masterPorter;
    }

    public void setMasterPorter(String masterPorter) {
        this.masterPorter = masterPorter;
    }

    public String getMasterPhone() {
        return masterPhone;
    }

    public void setMasterPhone(String masterPhone) {
        this.masterPhone = masterPhone;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
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

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public String getDeliveryRemark() {
        return deliveryRemark;
    }

    public void setDeliveryRemark(String deliveryRemark) {
        this.deliveryRemark = deliveryRemark;
    }

    public Date getRealDeliveryDate() {
        return realDeliveryDate;
    }

    public void setRealDeliveryDate(Date realDeliveryDate) {
        this.realDeliveryDate = realDeliveryDate;
    }

    public Date getRealArrivalDate() {
        return realArrivalDate;
    }

    public void setRealArrivalDate(Date realArrivalDate) {
        this.realArrivalDate = realArrivalDate;
    }

    public BigDecimal getStevedorage() {
        return stevedorage;
    }

    public void setStevedorage(BigDecimal stevedorage) {
        this.stevedorage = stevedorage;
    }
}
