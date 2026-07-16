package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * <p>
 * 手动决算申请表
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-04-21 14:16
 */
@Entity
@Table(name = "t_apply_manual_settlement")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyManualSettlement extends IdEntity implements IPmEntity {

    private Long approveId;

    /**
     * 审批状态
     * N-新增，A-审批中，B-驳回，D-完成
     */
    private String status;

    /**
     * 预算审批编号
     */
    private Long matchId;

    /**
     * 合同修改类型
     */
    private String changeType;

    /**
     * 附件id
     */
    private String fileId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收票金额
     */
    private BigDecimal receiveInvoiceAmount;

    /**
     * 调整后收票金额
     */
    private BigDecimal receiveInvoiceAmountB;

    /**
     * 采购合同金额
     */
    private BigDecimal buyContractTotalAmount;

    /**
     * 调整后采购合同金额
     */
    private BigDecimal buyContractTotalAmountB;

    /**
     * 开票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 调整后开票金额
     */
    private BigDecimal invoiceAmountB;

    /**
     * 销售合同总额
     */
    private BigDecimal sellContractTotalAmount;

    /**
     * 调整后销售合同总额
     */
    private BigDecimal sellContractTotalAmountB;

    /**
     * 付款金额
     */
    private BigDecimal payAmount;

    /**
     * 调整后付款金额
     */
    private BigDecimal payAmountB;

    /**
     * 是否完成盖章
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean sealFlg;

    /**
     * 调整后是否盖章
     */
    private Boolean sealFlgB;

    /**
     * 收款金额
     */
    private BigDecimal receiveAmount;

    /**
     * 调整后收款金额
     */
    private BigDecimal receiveAmountB;

    /**
     * 确认收货数量
     */
    private BigDecimal confirmReceivedGoods;

    /**
     * 调整后确认收货数量
     */
    private BigDecimal confirmReceivedGoodsB;

    /**
     * 入库数量
     */
    private BigDecimal deliveryInNumber;

    /**
     * 调整后入库数量
     */
    private BigDecimal deliveryInNumberB;

    /**
     * 出库数量
     */
    private BigDecimal deliveryOutNumber;

    /**
     * 调整后出库数量
     */
    private BigDecimal deliveryOutNumberB;

    /**
     * 约定收全款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date payFullTime;

    /**
     * 调整后收全款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date payFullTimeB;

    /**
     * 服务合同金额
     */
    private BigDecimal serviceTotalAmount;

    /**
     * 调整后服务合同金额
     */
    private BigDecimal serviceTotalAmountB;

    /**
     * 已收服务费金额
     */
    private BigDecimal receiveServiceAmount;

    /**
     * 调整后服务费金额
     */
    private BigDecimal receiveServiceAmountB;

    /**
     * 服务费开票金额
     */
    private BigDecimal serviceInvoiceAmount;

    /**
     * 调整后服务费开票金额
     */
    private BigDecimal serviceInvoiceAmountB;

    /**
     * 下游运输费
     */
    private BigDecimal sellTransAmount;

    /**
     * 下游运输费(修改后)
     */
    private BigDecimal sellTransAmountB;

    /**
     * 下游仓储费
     */
    private BigDecimal sellWarehouseAmount;

    /**
     * 下游仓储费(修改后)
     */
    private BigDecimal sellWarehouseAmountB;

    /**
     * 销售合同号
     */
    private String sellContractNo;

    /**
     * 采购合同号
     */
    private String buyContractNo;

    private String buyCompanyName;

    private String sellCompanyName;

    private String buyMatchUserName;

    private String sellMatchUserName;

    /**
     * 采购合同单价
     */
    private BigDecimal buyUnitPrice;

    /**
     * 销售合同单价
     */
    private BigDecimal sellUnitPrice;

    // ======损耗
    /**
     * 损耗数量
     */
    private BigDecimal lossNumber;

    /**
     * 损耗金额
     */
    private BigDecimal lossAmount;

    /**
     * 物流方承担损耗金额
     */
    private BigDecimal lossAmountByLogistics;

    /**
     * 实际物流费用
     */
    private BigDecimal lossAmountByActual;

    /**
     * 供应商承担损耗金额
     */
    private BigDecimal lossAmountBySupplier;

    /**
     * 我方承担损耗金额
     */
    private BigDecimal lossAmountByOur;

    // ======决算
    // 逾期天数
    private Integer breachDays;
    // 逾期服务费
    private BigDecimal breachAmount;
    // 已收逾期服务费
    private BigDecimal receiveBreachAmount;
    // 业务员罚金
    private BigDecimal fineOfSalesman;
    // 利润
    private BigDecimal marginAmount;
    // 利润率
    private BigDecimal grossProfitRate;
    // 采购提成
    private BigDecimal buyCommissionAmount;
    // 销售提成
    private BigDecimal sellCommissionAmount;
    // 管理提成
    private BigDecimal manageCommissionAmount;
    // 公司毛利收入
    private BigDecimal companyCommissionAmount;

    //部门Id
    private Long deptId;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getReceiveInvoiceAmount() {
        return receiveInvoiceAmount;
    }

    public void setReceiveInvoiceAmount(BigDecimal receiveInvoiceAmount) {
        this.receiveInvoiceAmount = receiveInvoiceAmount;
    }

    public BigDecimal getReceiveInvoiceAmountB() {
        return receiveInvoiceAmountB;
    }

    public void setReceiveInvoiceAmountB(BigDecimal receiveInvoiceAmountB) {
        this.receiveInvoiceAmountB = receiveInvoiceAmountB;
    }

    public BigDecimal getBuyContractTotalAmount() {
        return buyContractTotalAmount;
    }

    public void setBuyContractTotalAmount(BigDecimal buyContractTotalAmount) {
        this.buyContractTotalAmount = buyContractTotalAmount;
    }

    public BigDecimal getBuyContractTotalAmountB() {
        return buyContractTotalAmountB;
    }

    public void setBuyContractTotalAmountB(BigDecimal buyContractTotalAmountB) {
        this.buyContractTotalAmountB = buyContractTotalAmountB;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getInvoiceAmountB() {
        return invoiceAmountB;
    }

    public void setInvoiceAmountB(BigDecimal invoiceAmountB) {
        this.invoiceAmountB = invoiceAmountB;
    }

    public BigDecimal getSellContractTotalAmount() {
        return sellContractTotalAmount;
    }

    public void setSellContractTotalAmount(BigDecimal sellContractTotalAmount) {
        this.sellContractTotalAmount = sellContractTotalAmount;
    }

    public BigDecimal getSellContractTotalAmountB() {
        return sellContractTotalAmountB;
    }

    public void setSellContractTotalAmountB(BigDecimal sellContractTotalAmountB) {
        this.sellContractTotalAmountB = sellContractTotalAmountB;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getPayAmountB() {
        return payAmountB;
    }

    public void setPayAmountB(BigDecimal payAmountB) {
        this.payAmountB = payAmountB;
    }

    public Boolean getSealFlg() {
        return sealFlg;
    }

    public void setSealFlg(Boolean sealFlg) {
        this.sealFlg = sealFlg;
    }

    public Boolean getSealFlgB() {
        return sealFlgB;
    }

    public void setSealFlgB(Boolean sealFlgB) {
        this.sealFlgB = sealFlgB;
    }

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public BigDecimal getReceiveAmountB() {
        return receiveAmountB;
    }

    public void setReceiveAmountB(BigDecimal receiveAmountB) {
        this.receiveAmountB = receiveAmountB;
    }

    public BigDecimal getConfirmReceivedGoods() {
        return confirmReceivedGoods;
    }

    public void setConfirmReceivedGoods(BigDecimal confirmReceivedGoods) {
        this.confirmReceivedGoods = confirmReceivedGoods;
    }

    public BigDecimal getConfirmReceivedGoodsB() {
        return confirmReceivedGoodsB;
    }

    public void setConfirmReceivedGoodsB(BigDecimal confirmReceivedGoodsB) {
        this.confirmReceivedGoodsB = confirmReceivedGoodsB;
    }

    public BigDecimal getDeliveryInNumber() {
        return deliveryInNumber;
    }

    public void setDeliveryInNumber(BigDecimal deliveryInNumber) {
        this.deliveryInNumber = deliveryInNumber;
    }

    public BigDecimal getDeliveryInNumberB() {
        return deliveryInNumberB;
    }

    public void setDeliveryInNumberB(BigDecimal deliveryInNumberB) {
        this.deliveryInNumberB = deliveryInNumberB;
    }

    public BigDecimal getDeliveryOutNumber() {
        return deliveryOutNumber;
    }

    public void setDeliveryOutNumber(BigDecimal deliveryOutNumber) {
        this.deliveryOutNumber = deliveryOutNumber;
    }

    public BigDecimal getDeliveryOutNumberB() {
        return deliveryOutNumberB;
    }

    public void setDeliveryOutNumberB(BigDecimal deliveryOutNumberB) {
        this.deliveryOutNumberB = deliveryOutNumberB;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public Date getPayFullTimeB() {
        return payFullTimeB;
    }

    public void setPayFullTimeB(Date payFullTimeB) {
        this.payFullTimeB = payFullTimeB;
    }

    public BigDecimal getServiceTotalAmount() {
        return serviceTotalAmount;
    }

    public void setServiceTotalAmount(BigDecimal serviceTotalAmount) {
        this.serviceTotalAmount = serviceTotalAmount;
    }

    public BigDecimal getServiceTotalAmountB() {
        return serviceTotalAmountB;
    }

    public void setServiceTotalAmountB(BigDecimal serviceTotalAmountB) {
        this.serviceTotalAmountB = serviceTotalAmountB;
    }

    public BigDecimal getReceiveServiceAmount() {
        return receiveServiceAmount;
    }

    public void setReceiveServiceAmount(BigDecimal receiveServiceAmount) {
        this.receiveServiceAmount = receiveServiceAmount;
    }

    public BigDecimal getReceiveServiceAmountB() {
        return receiveServiceAmountB;
    }

    public void setReceiveServiceAmountB(BigDecimal receiveServiceAmountB) {
        this.receiveServiceAmountB = receiveServiceAmountB;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public BigDecimal getServiceInvoiceAmount() {
        return serviceInvoiceAmount;
    }

    public void setServiceInvoiceAmount(BigDecimal serviceInvoiceAmount) {
        this.serviceInvoiceAmount = serviceInvoiceAmount;
    }

    public BigDecimal getServiceInvoiceAmountB() {
        return serviceInvoiceAmountB;
    }

    public void setServiceInvoiceAmountB(BigDecimal serviceInvoiceAmountB) {
        this.serviceInvoiceAmountB = serviceInvoiceAmountB;
    }

    public String getBuyCompanyName() {
        return buyCompanyName;
    }

    public void setBuyCompanyName(String buyCompanyName) {
        this.buyCompanyName = buyCompanyName;
    }

    public String getSellCompanyName() {
        return sellCompanyName;
    }

    public void setSellCompanyName(String sellCompanyName) {
        this.sellCompanyName = sellCompanyName;
    }

    public String getBuyMatchUserName() {
        return buyMatchUserName;
    }

    public void setBuyMatchUserName(String buyMatchUserName) {
        this.buyMatchUserName = buyMatchUserName;
    }

    public String getSellMatchUserName() {
        return sellMatchUserName;
    }

    public void setSellMatchUserName(String sellMatchUserName) {
        this.sellMatchUserName = sellMatchUserName;
    }

    public Integer getBreachDays() {
        return breachDays;
    }

    public void setBreachDays(Integer breachDays) {
        this.breachDays = breachDays;
    }

    public BigDecimal getBreachAmount() {
        return breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }

    public BigDecimal getFineOfSalesman() {
        return fineOfSalesman;
    }

    public BigDecimal getReceiveBreachAmount() {
        return receiveBreachAmount;
    }

    public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
        this.receiveBreachAmount = receiveBreachAmount;
    }

    public void setFineOfSalesman(BigDecimal fineOfSalesman) {
        this.fineOfSalesman = fineOfSalesman;
    }

    public BigDecimal getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(BigDecimal marginAmount) {
        this.marginAmount = marginAmount;
    }

    public BigDecimal getGrossProfitRate() {
        return grossProfitRate;
    }

    public void setGrossProfitRate(BigDecimal grossProfitRate) {
        this.grossProfitRate = grossProfitRate;
    }

    public BigDecimal getBuyCommissionAmount() {
        return buyCommissionAmount;
    }

    public void setBuyCommissionAmount(BigDecimal buyCommissionAmount) {
        this.buyCommissionAmount = buyCommissionAmount;
    }

    public BigDecimal getSellCommissionAmount() {
        return sellCommissionAmount;
    }

    public void setSellCommissionAmount(BigDecimal sellCommissionAmount) {
        this.sellCommissionAmount = sellCommissionAmount;
    }

    public BigDecimal getManageCommissionAmount() {
        return manageCommissionAmount;
    }

    public void setManageCommissionAmount(BigDecimal manageCommissionAmount) {
        this.manageCommissionAmount = manageCommissionAmount;
    }

    public BigDecimal getCompanyCommissionAmount() {
        return companyCommissionAmount;
    }

    public void setCompanyCommissionAmount(BigDecimal companyCommissionAmount) {
        this.companyCommissionAmount = companyCommissionAmount;
    }

    public BigDecimal getBuyUnitPrice() {
        return buyUnitPrice;
    }

    public void setBuyUnitPrice(BigDecimal buyUnitPrice) {
        this.buyUnitPrice = buyUnitPrice;
    }

    public BigDecimal getSellUnitPrice() {
        return sellUnitPrice;
    }

    public void setSellUnitPrice(BigDecimal sellUnitPrice) {
        this.sellUnitPrice = sellUnitPrice;
    }

    public BigDecimal getSellTransAmount() {
        return sellTransAmount;
    }

    public void setSellTransAmount(BigDecimal sellTransAmount) {
        this.sellTransAmount = sellTransAmount;
    }

    public BigDecimal getSellTransAmountB() {
        return sellTransAmountB;
    }

    public void setSellTransAmountB(BigDecimal sellTransAmountB) {
        this.sellTransAmountB = sellTransAmountB;
    }

    public BigDecimal getSellWarehouseAmount() {
        return sellWarehouseAmount;
    }

    public void setSellWarehouseAmount(BigDecimal sellWarehouseAmount) {
        this.sellWarehouseAmount = sellWarehouseAmount;
    }

    public BigDecimal getSellWarehouseAmountB() {
        return sellWarehouseAmountB;
    }

    public void setSellWarehouseAmountB(BigDecimal sellWarehouseAmountB) {
        this.sellWarehouseAmountB = sellWarehouseAmountB;
    }

    public BigDecimal getLossNumber() {
        return lossNumber;
    }

    public void setLossNumber(BigDecimal lossNumber) {
        this.lossNumber = lossNumber;
    }

    public BigDecimal getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    public BigDecimal getLossAmountByLogistics() {
        return lossAmountByLogistics;
    }

    public void setLossAmountByLogistics(BigDecimal lossAmountByLogistics) {
        this.lossAmountByLogistics = lossAmountByLogistics;
    }

    public BigDecimal getLossAmountByActual() {
        return lossAmountByActual;
    }

    public void setLossAmountByActual(BigDecimal lossAmountByActual) {
        this.lossAmountByActual = lossAmountByActual;
    }

    public BigDecimal getLossAmountBySupplier() {
        return lossAmountBySupplier;
    }

    public void setLossAmountBySupplier(BigDecimal lossAmountBySupplier) {
        this.lossAmountBySupplier = lossAmountBySupplier;
    }

    public BigDecimal getLossAmountByOur() {
        return lossAmountByOur;
    }

    public void setLossAmountByOur(BigDecimal lossAmountByOur) {
        this.lossAmountByOur = lossAmountByOur;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
