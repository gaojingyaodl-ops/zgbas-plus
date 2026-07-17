package com.spt.bas.report.client.payload;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

/**
 * <p>
 *  财务统计报表查询字段
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-19 09:13
 */
public class FinanceStatics extends PageSearchVo {
    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 业务员
     */
    private String matchUserName;

    /**
     * 业务员
     */
    private String matchUserId;

    /**
     * 货名
     */
    private String productsName;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 我方抬头列表
     */
    private List<String> ourCompanyNames;

    /**
     * 配送方式
     */
    private String deliveryType;

    /**
     * 收/付款状态
     */
    private Boolean dealedFlg;

    /**
     * 财务查询的合同的状态
     */
    private String financeContractStatus;

    /**
     * 签订日开始时间
     */
    private String contractTimeFrom;

    /**
     * 签订日结束时间
     */
    private String contractTimeTo;

    /**
     * 白条：1；代采：2；自营采购：3；自营销售：4
     * 代采赊销：5
     */
    private String budgetType;

    /**
     * 是否完成审批（销售合同）
     */
    private Boolean sellApproveFlg;

    /**
     * 是否盖章(签约)
     */
    private Boolean sellSealFlg;

    /**
     * 是否付款
     */
    private Boolean sellDealedFlg;

    /**
     * 是否收货
     */
    private Boolean sellWarehouseFlg;

    /**
     * 是否确认收货
     */
    private Boolean sellConfirmFlg;

    /**
     * 是否收票
     */
    private Boolean sellBilledFlg;

    /**
     * 是否作废
     */
    private Boolean sellInvalidFlg;

    /**
     * 是否完成
     */
    private Boolean sellCompleteFlg;

    /**
     * 是否逾期
     */
    private Boolean sellOverdueFlg;

    /**
     * 是否违约
     */
    private Boolean sellBreachFlg;

    /**
     * 是否完成审批
     */
    private Boolean buyApproveFlg;

    /**
     * 是否盖章
     */
    private Boolean buySealFlg;

    /**
     * 是否付款
     */
    private Boolean buyDealedFlg;

    /**
     * 是否收货
     */
    private Boolean buyWarehouseFlg;

    /**
     * 是否收票
     */
    private Boolean buyBilledFlg;

    /**
     * 是否作废
     */
    private Boolean buyInvalidFlg;

    /**
     * 是否完成
     */
    private Boolean buyCompleteFlg;

    /**
     * 是否逾期
     */
    private Boolean buyOverdueFlg;

    /**
     * 是否违约
     */
    private Boolean buyBreachFlg;

    /**
     * 是否正常
     */
    private Boolean commonFlg;

    /**
     * 是否罚金不足状态
     */
    private Boolean sellNoFineFlg;

    private List<Long> approveIds;

    /**
     * 付全款日开始
     */
    private String bpayFullTimeFrom;

    /**
     * 付全款日结束
     */
    private String bpayFullTimeTo;

    /**
     * 收全款日开始
     */
    private String spayFullTimeFrom;

    /**
     * 收全款日结束
     */
    private String spayFullTimeTo;

    /**
     * 区域
     */
    private String deptName;

    /**
     * 因决算统计页面品种/牌号/厂商是sql拼接出来的，故查询时在此特殊处理
     */
    private String productsNameOne;
    private String productsNameTwo;
    private String productsNameThree;
    private String level;

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

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public List<String> getOurCompanyNames() {
        return ourCompanyNames;
    }

    public void setOurCompanyNames(List<String> ourCompanyNames) {
        this.ourCompanyNames = ourCompanyNames;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Boolean getDealedFlg() {
        return dealedFlg;
    }

    public void setDealedFlg(Boolean dealedFlg) {
        this.dealedFlg = dealedFlg;
    }

    public String getFinanceContractStatus() {
        return financeContractStatus;
    }

    public void setFinanceContractStatus(String financeContractStatus) {
        this.financeContractStatus = financeContractStatus;
    }

    public String getContractTimeFrom() {
        return contractTimeFrom;
    }

    public void setContractTimeFrom(String contractTimeFrom) {
        this.contractTimeFrom = contractTimeFrom;
    }

    public String getContractTimeTo() {
        return contractTimeTo;
    }

    public void setContractTimeTo(String contractTimeTo) {
        this.contractTimeTo = contractTimeTo;
    }

    public String getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }

    public Boolean getSellApproveFlg() {
        return sellApproveFlg;
    }

    public void setSellApproveFlg(Boolean sellApproveFlg) {
        this.sellApproveFlg = sellApproveFlg;
    }

    public Boolean getSellSealFlg() {
        return sellSealFlg;
    }

    public void setSellSealFlg(Boolean sellSealFlg) {
        this.sellSealFlg = sellSealFlg;
    }

    public Boolean getSellDealedFlg() {
        return sellDealedFlg;
    }

    public void setSellDealedFlg(Boolean sellDealedFlg) {
        this.sellDealedFlg = sellDealedFlg;
    }

    public Boolean getSellWarehouseFlg() {
        return sellWarehouseFlg;
    }

    public void setSellWarehouseFlg(Boolean sellWarehouseFlg) {
        this.sellWarehouseFlg = sellWarehouseFlg;
    }

    public Boolean getSellConfirmFlg() {
        return sellConfirmFlg;
    }

    public void setSellConfirmFlg(Boolean sellConfirmFlg) {
        this.sellConfirmFlg = sellConfirmFlg;
    }

    public Boolean getSellBilledFlg() {
        return sellBilledFlg;
    }

    public void setSellBilledFlg(Boolean sellBilledFlg) {
        this.sellBilledFlg = sellBilledFlg;
    }

    public Boolean getSellInvalidFlg() {
        return sellInvalidFlg;
    }

    public void setSellInvalidFlg(Boolean sellInvalidFlg) {
        this.sellInvalidFlg = sellInvalidFlg;
    }

    public Boolean getSellCompleteFlg() {
        return sellCompleteFlg;
    }

    public void setSellCompleteFlg(Boolean sellCompleteFlg) {
        this.sellCompleteFlg = sellCompleteFlg;
    }

    public Boolean getSellOverdueFlg() {
        return sellOverdueFlg;
    }

    public void setSellOverdueFlg(Boolean sellOverdueFlg) {
        this.sellOverdueFlg = sellOverdueFlg;
    }

    public Boolean getSellBreachFlg() {
        return sellBreachFlg;
    }

    public void setSellBreachFlg(Boolean sellBreachFlg) {
        this.sellBreachFlg = sellBreachFlg;
    }

    public Boolean getBuyApproveFlg() {
        return buyApproveFlg;
    }

    public void setBuyApproveFlg(Boolean buyApproveFlg) {
        this.buyApproveFlg = buyApproveFlg;
    }

    public Boolean getBuySealFlg() {
        return buySealFlg;
    }

    public void setBuySealFlg(Boolean buySealFlg) {
        this.buySealFlg = buySealFlg;
    }

    public Boolean getBuyDealedFlg() {
        return buyDealedFlg;
    }

    public void setBuyDealedFlg(Boolean buyDealedFlg) {
        this.buyDealedFlg = buyDealedFlg;
    }

    public Boolean getBuyWarehouseFlg() {
        return buyWarehouseFlg;
    }

    public void setBuyWarehouseFlg(Boolean buyWarehouseFlg) {
        this.buyWarehouseFlg = buyWarehouseFlg;
    }

    public Boolean getBuyBilledFlg() {
        return buyBilledFlg;
    }

    public void setBuyBilledFlg(Boolean buyBilledFlg) {
        this.buyBilledFlg = buyBilledFlg;
    }

    public Boolean getBuyInvalidFlg() {
        return buyInvalidFlg;
    }

    public void setBuyInvalidFlg(Boolean buyInvalidFlg) {
        this.buyInvalidFlg = buyInvalidFlg;
    }

    public Boolean getBuyCompleteFlg() {
        return buyCompleteFlg;
    }

    public void setBuyCompleteFlg(Boolean buyCompleteFlg) {
        this.buyCompleteFlg = buyCompleteFlg;
    }

    public Boolean getBuyOverdueFlg() {
        return buyOverdueFlg;
    }

    public void setBuyOverdueFlg(Boolean buyOverdueFlg) {
        this.buyOverdueFlg = buyOverdueFlg;
    }

    public Boolean getBuyBreachFlg() {
        return buyBreachFlg;
    }

    public void setBuyBreachFlg(Boolean buyBreachFlg) {
        this.buyBreachFlg = buyBreachFlg;
    }

    public Boolean getCommonFlg() {
        return commonFlg;
    }

    public void setCommonFlg(Boolean commonFlg) {
        this.commonFlg = commonFlg;
    }

    public List<Long> getApproveIds() {
        return approveIds;
    }

    public void setApproveIds(List<Long> approveIds) {
        this.approveIds = approveIds;
    }

    public Boolean getSellNoFineFlg() {
        return sellNoFineFlg;
    }

    public void setSellNoFineFlg(Boolean sellNoFineFlg) {
        this.sellNoFineFlg = sellNoFineFlg;
    }

    public String getBpayFullTimeFrom() {
        return bpayFullTimeFrom;
    }

    public void setBpayFullTimeFrom(String bpayFullTimeFrom) {
        this.bpayFullTimeFrom = bpayFullTimeFrom;
    }

    public String getBpayFullTimeTo() {
        return bpayFullTimeTo;
    }

    public void setBpayFullTimeTo(String bpayFullTimeTo) {
        this.bpayFullTimeTo = bpayFullTimeTo;
    }

    public String getSpayFullTimeFrom() {
        return spayFullTimeFrom;
    }

    public void setSpayFullTimeFrom(String spayFullTimeFrom) {
        this.spayFullTimeFrom = spayFullTimeFrom;
    }

    public String getSpayFullTimeTo() {
        return spayFullTimeTo;
    }

    public void setSpayFullTimeTo(String spayFullTimeTo) {
        this.spayFullTimeTo = spayFullTimeTo;
    }

    public String getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(String matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getProductsNameOne() {
        return productsNameOne;
    }

    public void setProductsNameOne(String productsNameOne) {
        this.productsNameOne = productsNameOne;
    }

    public String getProductsNameTwo() {
        return productsNameTwo;
    }

    public void setProductsNameTwo(String productsNameTwo) {
        this.productsNameTwo = productsNameTwo;
    }

    public String getProductsNameThree() {
        return productsNameThree;
    }

    public void setProductsNameThree(String productsNameThree) {
        this.productsNameThree = productsNameThree;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
