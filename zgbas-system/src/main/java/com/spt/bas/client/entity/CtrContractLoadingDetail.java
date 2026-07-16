package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 提货单/委托配送单货物详情表
 * @Author: gaojy
 * @create 2022/6/20 9:32
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_ctr_contract_loading_detail")
public class CtrContractLoadingDetail extends IdEntity {
    private static final long serialVersionUID = 3347005484465063506L;

    /**
     * 品名
     */
    private String productName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 单位
     */
    private String numberUnit;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机手机号
     */
    // private String driverPhone;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 司机身份证
     */
    private String driverCardNo;

    private CtrContractLoading loading;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public String getNumberUnit() {
        return numberUnit;
    }

    public void setNumberUnit(String numberUnit) {
        this.numberUnit = numberUnit;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loading_id")
    public CtrContractLoading getLoading() {
        return loading;
    }

    public void setLoading(CtrContractLoading loading) {
        this.loading = loading;
    }

    public CtrContractLoadingDetail() {
    }

}
