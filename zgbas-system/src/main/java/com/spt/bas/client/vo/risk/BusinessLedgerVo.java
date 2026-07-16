package com.spt.bas.client.vo.risk;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;


@Data
public class BusinessLedgerVo implements Comparable {

    /**
     * 企业账套Id
     */
    private Long enterpriseId;

    /**
     * 所属公司
     */
    private String ourCompanyName;

    /**
     * 成交日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 系统合同编号
     */
    private String contractNo;

    /**
     * 货名
     */
    private String productsName;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员Name
     */
    private String matchUserName;

    /**
     * 审批ID
     */
    private Long approveId;

    /**
     * 下游收款完成日期，（实际收全款日期）
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellRealPayFullTime;

    /******** 上游合同信息 *****************************************************************/


    /**
     * 上游合同ID
     */
    private Long buyContractId;

    /**
     * 上游合同编号
     */
    private String buyContractNo;

    /**
     * 上游供应商名称
     */
    private String buyCompanyName;

    /**
     * 上游代采
     */
    private String buyOurCompanyName;

    /**
     *  上游单价
     */
    private BigDecimal buyDealPrice;

    /**
     * 上游合同金额
     */
    private BigDecimal buyTotalAmount;

    /**
     * 上游付定金日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyPayBondTime;

    /**
     * 上游付定金金额
     */
    private BigDecimal buyBondAmount;

    /**
     * 上游付款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyPayDate;

    /**
     * 上游付款金额
     */
    private BigDecimal buyPayAmount;

    /**
     * 上游未付款金额
     */
    private BigDecimal buyUnPayAmount;

    /**
     * 上游收票日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyBillDate;

    /**
     * 上游收票金额
     */
    private BigDecimal buyBillAmount;

    /**
     * 上游入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyWarehouseDate;

    /**
     * 上游入库数量
     */
    private BigDecimal buyWarehouseNumber;

/******** 中游合同信息 *****************************************************************/

    /**
     * 中游合同ID
     */
    private Long dcsxContractId;
    /**
     * 中游合同编号
     */
    private String dcsxContractNo;

    /**
     * 资方公司名称
     */
    private String dcsxOurCompanyName;

    /**
     *  中游单价
     */
    private BigDecimal dcsxDealPrice;

    /**
     * 中游合同金额
     */
    private BigDecimal dcsxTotalAmount;

    /**
     * 中游收定金日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxReceiveBondTime;

    /**
     * 中游收定金金额
     */
    private BigDecimal dcsxReceiveBondAmount;

    /**
     * 中游收款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxReceiveDate;

    /**
     * 中游收款金额
     */
    private BigDecimal dcsxReceiveAmount;

    /**
     * 中游未收款金额
     */
    private BigDecimal dcsxUnReceiveAmount;

    /**
     * 中游开票日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxReceiveBillDate;

    /**
     * 中游开票金额
     */
    private BigDecimal dcsxReceiveBillAmount;

    /**
     * 中游出库库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxWarehouseDate;

    /**
     * 中游出库数量
     */
    private BigDecimal dcsxWarehouseNumber;

    /**
     * 中游逾期罚息
     */
    private BigDecimal dcsxOverdueInterestAmount;

    /**
     * 中游约定付款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxPayFullTime;

    /**
     * 中游实际付款金额
     */
    private BigDecimal dcsxDealedAmount;

    /**
     * 已付逾期罚息
     */
    private BigDecimal dcsxReceiveOverdueInterest;

    /**
     * 应付逾期罚息
     */
    private BigDecimal dcsxReceivableOverdueInterest;

    /**
     * 合同额外成本
     */
    private BigDecimal extraCost = BigDecimal.ZERO;

    /**
     * 承兑贴息成本
     */
    private BigDecimal acceptDiscountCost = BigDecimal.ZERO;

    /**
     * 结算状态
     * 0-未结算
     * 1-申请中
     * 3.已结算
     */
    private String settlementStatus;

    /**
     * 中游实际付款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxPayDate;

    /**
     * 中游收款金额
     */
    private BigDecimal dcsxPayAmount;

    /**
     * 中游逾期罚息计算明细
     */
    private String calculateDetail;


/******** 下游合同信息 *****************************************************************/

    /**
     * 上游合同ID
     */
    private Long sellContractId;

    /**
     * 上游合同编号
     */
    private String sellContractNo;

    /**
     * 下游客户名称
     */
    private String sellCompanyName;

    /**
     *  下游单价
     */
    private BigDecimal sellDealPrice;

    /**
     * 下游合同金额
     */
    private BigDecimal sellTotalAmount;

    /**
     * 下游约定回款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellAppointPayFullTime;

    /**
     * 下游回款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellReceiveDate;

    /**
     * 下游回款金额
     */
    private BigDecimal sellReceiveAmount;

    /**
     * 下游未回款款金额
     */
    private BigDecimal sellUnReceiveAmount;

    /**
     * 是否逾期
     */
    private Boolean orverdurFlg;
    private String orverdurFlgStr;

    /**
     * 下游开票日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellBillDate;

    /**
     * 下游开票金额
     */
    private BigDecimal sellBillAmount;

    /**
     * 下游出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellWarehouseDate;

    /**
     * 下游出库数量
     */
    private BigDecimal sellWarehouseNumber;

    /**
     * 逾期天数
     */
    private Long sellBreachDays;

/******** 逾期罚息 *****************************************************************/

    /**
     * 已收罚息
     */
    private BigDecimal receiveInterestAmount;

    /**
     * 罚息收款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date interestAmountReceiveDate;

    /**
     * 剩余罚息
     */
    private BigDecimal unReceiveInterestAmount;

    /**
     * 预算编号
     */
    private String approveNo;

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
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

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public Long getBuyContractId() {
        return buyContractId;
    }

    public void setBuyContractId(Long buyContractId) {
        this.buyContractId = buyContractId;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public String getBuyCompanyName() {
        return buyCompanyName;
    }

    public void setBuyCompanyName(String buyCompanyName) {
        this.buyCompanyName = buyCompanyName;
    }

    public String getBuyOurCompanyName() {
        return buyOurCompanyName;
    }

    public void setBuyOurCompanyName(String buyOurCompanyName) {
        this.buyOurCompanyName = buyOurCompanyName;
    }

    public BigDecimal getBuyDealPrice() {
        return buyDealPrice;
    }

    public void setBuyDealPrice(BigDecimal buyDealPrice) {
        this.buyDealPrice = buyDealPrice;
    }

    public BigDecimal getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
    }

    public Date getBuyPayBondTime() {
        return buyPayBondTime;
    }

    public void setBuyPayBondTime(Date buyPayBondTime) {
        this.buyPayBondTime = buyPayBondTime;
    }

    public BigDecimal getBuyBondAmount() {
        return buyBondAmount;
    }

    public void setBuyBondAmount(BigDecimal buyBondAmount) {
        this.buyBondAmount = buyBondAmount;
    }

    public Date getBuyPayDate() {
        return buyPayDate;
    }

    public void setBuyPayDate(Date buyPayDate) {
        this.buyPayDate = buyPayDate;
    }

    public BigDecimal getBuyPayAmount() {
        return buyPayAmount;
    }

    public void setBuyPayAmount(BigDecimal buyPayAmount) {
        this.buyPayAmount = buyPayAmount;
    }

    public BigDecimal getBuyUnPayAmount() {
        return buyUnPayAmount;
    }

    public void setBuyUnPayAmount(BigDecimal buyUnPayAmount) {
        this.buyUnPayAmount = buyUnPayAmount;
    }

    public Date getBuyBillDate() {
        return buyBillDate;
    }

    public void setBuyBillDate(Date buyBillDate) {
        this.buyBillDate = buyBillDate;
    }

    public BigDecimal getBuyBillAmount() {
        return buyBillAmount;
    }

    public void setBuyBillAmount(BigDecimal buyBillAmount) {
        this.buyBillAmount = buyBillAmount;
    }

    public Date getBuyWarehouseDate() {
        return buyWarehouseDate;
    }

    public void setBuyWarehouseDate(Date buyWarehouseDate) {
        this.buyWarehouseDate = buyWarehouseDate;
    }

    public BigDecimal getBuyWarehouseNumber() {
        return buyWarehouseNumber;
    }

    public void setBuyWarehouseNumber(BigDecimal buyWarehouseNumber) {
        this.buyWarehouseNumber = buyWarehouseNumber;
    }

    public String getDcsxContractNo() {
        return dcsxContractNo;
    }

    public void setDcsxContractNo(String dcsxContractNo) {
        this.dcsxContractNo = dcsxContractNo;
    }

    public String getDcsxOurCompanyName() {
        return dcsxOurCompanyName;
    }

    public void setDcsxOurCompanyName(String dcsxOurCompanyName) {
        this.dcsxOurCompanyName = dcsxOurCompanyName;
    }

    public BigDecimal getDcsxDealPrice() {
        return dcsxDealPrice;
    }

    public void setDcsxDealPrice(BigDecimal dcsxDealPrice) {
        this.dcsxDealPrice = dcsxDealPrice;
    }

    public BigDecimal getDcsxTotalAmount() {
        return Objects.isNull(dcsxTotalAmount) ? BigDecimal.ZERO : dcsxTotalAmount;
    }

    public void setDcsxTotalAmount(BigDecimal dcsxTotalAmount) {
        this.dcsxTotalAmount = dcsxTotalAmount;
    }

    public Date getDcsxReceiveBondTime() {
        return dcsxReceiveBondTime;
    }

    public void setDcsxReceiveBondTime(Date dcsxReceiveBondTime) {
        this.dcsxReceiveBondTime = dcsxReceiveBondTime;
    }

    public BigDecimal getDcsxReceiveBondAmount() {
        return dcsxReceiveBondAmount;
    }

    public void setDcsxReceiveBondAmount(BigDecimal dcsxReceiveBondAmount) {
        this.dcsxReceiveBondAmount = dcsxReceiveBondAmount;
    }

    public Date getDcsxReceiveDate() {
        return dcsxReceiveDate;
    }

    public void setDcsxReceiveDate(Date dcsxReceiveDate) {
        this.dcsxReceiveDate = dcsxReceiveDate;
    }

    public BigDecimal getDcsxReceiveAmount() {
        return dcsxReceiveAmount;
    }

    public void setDcsxReceiveAmount(BigDecimal dcsxReceiveAmount) {
        this.dcsxReceiveAmount = dcsxReceiveAmount;
    }

    public BigDecimal getDcsxUnReceiveAmount() {
        return dcsxUnReceiveAmount;
    }

    public void setDcsxUnReceiveAmount(BigDecimal dcsxUnReceiveAmount) {
        this.dcsxUnReceiveAmount = dcsxUnReceiveAmount;
    }

    public Date getDcsxReceiveBillDate() {
        return dcsxReceiveBillDate;
    }

    public void setDcsxReceiveBillDate(Date dcsxReceiveBillDate) {
        this.dcsxReceiveBillDate = dcsxReceiveBillDate;
    }

    public BigDecimal getDcsxReceiveBillAmount() {
        return dcsxReceiveBillAmount;
    }

    public void setDcsxReceiveBillAmount(BigDecimal dcsxReceiveBillAmount) {
        this.dcsxReceiveBillAmount = dcsxReceiveBillAmount;
    }

    public Date getDcsxWarehouseDate() {
        return dcsxWarehouseDate;
    }

    public void setDcsxWarehouseDate(Date dcsxWarehouseDate) {
        this.dcsxWarehouseDate = dcsxWarehouseDate;
    }

    public BigDecimal getDcsxWarehouseNumber() {
        return dcsxWarehouseNumber;
    }

    public void setDcsxWarehouseNumber(BigDecimal dcsxWarehouseNumber) {
        this.dcsxWarehouseNumber = dcsxWarehouseNumber;
    }

    public BigDecimal getDcsxOverdueInterestAmount() {
        return dcsxOverdueInterestAmount;
    }

    public void setDcsxOverdueInterestAmount(BigDecimal dcsxOverdueInterestAmount) {
        this.dcsxOverdueInterestAmount = dcsxOverdueInterestAmount;
    }

    public Long getSellContractId() {
        return sellContractId;
    }

    public void setSellContractId(Long sellContractId) {
        this.sellContractId = sellContractId;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public String getSellCompanyName() {
        return sellCompanyName;
    }

    public void setSellCompanyName(String sellCompanyName) {
        this.sellCompanyName = sellCompanyName;
    }

    public BigDecimal getSellDealPrice() {
        return sellDealPrice;
    }

    public void setSellDealPrice(BigDecimal sellDealPrice) {
        this.sellDealPrice = sellDealPrice;
    }

    public BigDecimal getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(BigDecimal sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public Date getSellAppointPayFullTime() {
        return sellAppointPayFullTime;
    }

    public void setSellAppointPayFullTime(Date sellAppointPayFullTime) {
        this.sellAppointPayFullTime = sellAppointPayFullTime;
    }

    public Date getSellReceiveDate() {
        return sellReceiveDate;
    }

    public void setSellReceiveDate(Date sellReceiveDate) {
        this.sellReceiveDate = sellReceiveDate;
    }

    public BigDecimal getSellReceiveAmount() {
        return Objects.isNull(sellReceiveAmount) ? BigDecimal.ZERO : sellReceiveAmount;
    }

    public void setSellReceiveAmount(BigDecimal sellReceiveAmount) {
        this.sellReceiveAmount = sellReceiveAmount;
    }

    public BigDecimal getSellUnReceiveAmount() {
        return sellUnReceiveAmount;
    }

    public void setSellUnReceiveAmount(BigDecimal sellUnReceiveAmount) {
        this.sellUnReceiveAmount = sellUnReceiveAmount;
    }

    public Boolean getOrverdurFlg() {
        return orverdurFlg;
    }

    public void setOrverdurFlg(Boolean orverdurFlg) {
        this.orverdurFlg = orverdurFlg;
    }

    public Date getSellBillDate() {
        return sellBillDate;
    }

    public void setSellBillDate(Date sellBillDate) {
        this.sellBillDate = sellBillDate;
    }

    public BigDecimal getSellBillAmount() {
        return sellBillAmount;
    }

    public void setSellBillAmount(BigDecimal sellBillAmount) {
        this.sellBillAmount = sellBillAmount;
    }

    public Date getSellWarehouseDate() {
        return sellWarehouseDate;
    }

    public void setSellWarehouseDate(Date sellWarehouseDate) {
        this.sellWarehouseDate = sellWarehouseDate;
    }

    public BigDecimal getSellWarehouseNumber() {
        return sellWarehouseNumber;
    }

    public void setSellWarehouseNumber(BigDecimal sellWarehouseNumber) {
        this.sellWarehouseNumber = sellWarehouseNumber;
    }

    public BigDecimal getReceiveInterestAmount() {
        return receiveInterestAmount;
    }

    public void setReceiveInterestAmount(BigDecimal receiveInterestAmount) {
        this.receiveInterestAmount = receiveInterestAmount;
    }

    public Date getInterestAmountReceiveDate() {
        return interestAmountReceiveDate;
    }

    public void setInterestAmountReceiveDate(Date interestAmountReceiveDate) {
        this.interestAmountReceiveDate = interestAmountReceiveDate;
    }

    public BigDecimal getUnReceiveInterestAmount() {
        return unReceiveInterestAmount;
    }

    public void setUnReceiveInterestAmount(BigDecimal unReceiveInterestAmount) {
        this.unReceiveInterestAmount = unReceiveInterestAmount;
    }

    public String getOrverdurFlgStr() {
        return orverdurFlgStr;
    }

    public void setOrverdurFlgStr(String orverdurFlgStr) {
        this.orverdurFlgStr = orverdurFlgStr;
    }

    public Date getDcsxPayFullTime() {
        return dcsxPayFullTime;
    }

    public void setDcsxPayFullTime(Date dcsxPayFullTime) {
        this.dcsxPayFullTime = dcsxPayFullTime;
    }

    public BigDecimal getDcsxDealedAmount() {
        return Objects.isNull(dcsxDealedAmount) ? BigDecimal.ZERO : dcsxDealedAmount;
    }

    public void setDcsxDealedAmount(BigDecimal dcsxDealedAmount) {
        this.dcsxDealedAmount = dcsxDealedAmount;
    }

    public Date getDcsxPayDate() {
        return dcsxPayDate;
    }

    public void setDcsxPayDate(Date dcsxPayDate) {
        this.dcsxPayDate = dcsxPayDate;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public BigDecimal getDcsxReceiveOverdueInterest() {
        return dcsxReceiveOverdueInterest;
    }

    public void setDcsxReceiveOverdueInterest(BigDecimal dcsxReceiveOverdueInterest) {
        this.dcsxReceiveOverdueInterest = dcsxReceiveOverdueInterest;
    }

    public Long getDcsxContractId() {
        return dcsxContractId;
    }

    public void setDcsxContractId(Long dcsxContractId) {
        this.dcsxContractId = dcsxContractId;
    }

    public BigDecimal getDcsxReceivableOverdueInterest() {
        return dcsxReceivableOverdueInterest;
    }

    public void setDcsxReceivableOverdueInterest(BigDecimal dcsxReceivableOverdueInterest) {
        this.dcsxReceivableOverdueInterest = dcsxReceivableOverdueInterest;
    }

    public Date getSellRealPayFullTime() {
        return sellRealPayFullTime;
    }

    public void setSellRealPayFullTime(Date sellRealPayFullTime) {
        this.sellRealPayFullTime = sellRealPayFullTime;
    }

    public Long getSellBreachDays() {
        return sellBreachDays;
    }

    public void setSellBreachDays(Long sellBreachDays) {
        this.sellBreachDays = sellBreachDays;
    }

    public BigDecimal getDcsxPayAmount() {
        return dcsxPayAmount;
    }

    public void setDcsxPayAmount(BigDecimal dcsxPayAmount) {
        this.dcsxPayAmount = dcsxPayAmount;
    }

    @Override
    public int compareTo(Object o) {
        return this.getApproveNo().compareTo(((BusinessLedgerVo) o).getApproveNo());
    }

    public String getCalculateDetail() {
        return calculateDetail;
    }

    public void setCalculateDetail(String calculateDetail) {
        this.calculateDetail = calculateDetail;
    }

    public BigDecimal getExtraCost() {
        return extraCost;
    }

    public void setExtraCost(BigDecimal extraCost) {
        this.extraCost = extraCost;
    }

    public BigDecimal getAcceptDiscountCost() {
        return acceptDiscountCost;
    }

    public void setAcceptDiscountCost(BigDecimal acceptDiscountCost) {
        this.acceptDiscountCost = acceptDiscountCost;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }
}
