package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

public class RptCtrContractUnDeliverySearchVo extends PageSearchVo {

    private String contractNo;
    private String companyName;
    private String ourCompanyName;
    private String payCondition;	//收付款条件
    private String warehouseInCondition;//出入库条件
    private String warehouseOutCondition;//出入库条件
    private String deliveryDateFrom;
    private String deliveryDateTo;
    private Boolean matchCreditFlg;
    private String contractType;
    /**
     * 产品类型
     */
    private String productType;

    /**
     * 化工业务员ID集合
     */
    private List<Long> hgMatchUserIdList;

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

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getPayCondition() {
        return payCondition;
    }

    public void setPayCondition(String payCondition) {
        this.payCondition = payCondition;
    }

    public String getWarehouseInCondition() {
        return warehouseInCondition;
    }

    public void setWarehouseInCondition(String warehouseInCondition) {
        this.warehouseInCondition = warehouseInCondition;
    }

    public String getWarehouseOutCondition() {
        return warehouseOutCondition;
    }

    public void setWarehouseOutCondition(String warehouseOutCondition) {
        this.warehouseOutCondition = warehouseOutCondition;
    }

    public String getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom(String deliveryDateFrom) {
        this.deliveryDateFrom = deliveryDateFrom;
    }

    public String getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo(String deliveryDateTo) {
        this.deliveryDateTo = deliveryDateTo;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public List<Long> getHgMatchUserIdList() {
        return hgMatchUserIdList;
    }

    public void setHgMatchUserIdList(List<Long> hgMatchUserIdList) {
        this.hgMatchUserIdList = hgMatchUserIdList;
    }
}
