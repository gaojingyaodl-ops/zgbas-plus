package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 合同物流提货司机表
 *
 * @Author MoonLight
 * @Date 2023/7/5 15:55
 * @Version 1.0
 */
@Entity
@Table(name = "t_ctr_logistics_driver")
public class CtrLogisticsDriver extends IdEntity {
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
     * 提货数量
     */
    private BigDecimal logisticsNumber;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 司机身份证号
     */
    private String driverCardNo;

    public CtrLogisticsDriver() {
    }

    public CtrLogisticsDriver(Long logisticsId, Long logisticsDeliveryId) {
        this.logisticsId = logisticsId;
        this.logisticsDeliveryId = logisticsDeliveryId;
    }

    public CtrLogisticsDriver(Long logisticsId, Long logisticsDeliveryId, BigDecimal logisticsNumber, String contactPhone, String driverName, String plateNumber, String driverCardNo) {
        this.logisticsId = logisticsId;
        this.logisticsDeliveryId = logisticsDeliveryId;
        this.logisticsNumber = logisticsNumber;
        this.contactPhone = contactPhone;
        this.driverName = driverName;
        this.plateNumber = plateNumber;
        this.driverCardNo = driverCardNo;
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

    public BigDecimal getLogisticsNumber() {
        return logisticsNumber;
    }

    public void setLogisticsNumber(BigDecimal logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
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
}
