package com.spt.bas.report.client.entity;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-09 10:29
 */
public class RptCtrContractSearch extends PageSearchVo {
    /**
     * 白条（一票制）：1，
     * 白条（两票制）：2，
     * 白条：3，
     * 代采：4，
     * 所有：0
     */
    private String serviceType;

    /**
     * 品种、牌号
     */
    private String productsName;

    /**
     * Z：自提；P：配送
     */
    private String deliveryType;

    /**
     * W待收货、S待付服务费、P待付款、B待收票（包括待收服务费发票和待收货款发票）、L逾期、T违约、O已完成
     */
    private String contractStatus;

    /**
     *
     */
    private String deliveryDateTo;

    /**
     * 小程序用户id
     */
    private Long userId;

    private String contractNo;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo(String deliveryDateTo) {
        this.deliveryDateTo = deliveryDateTo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }
}
