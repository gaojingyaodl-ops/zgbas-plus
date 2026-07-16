package com.spt.bas.client.vo.rtVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 融拓赊销预算推送信息Vo
 *
 * @Author: gaojy
 * @create 2022/4/8 11:42
 * @version: 1.0
 * @description:
 */
public class RtContractReq extends RtBaseReq{
    /**
     * 实际金额
     */
    private BigDecimal actualContractAmount;

    /**
     * 确认收货数量
     */
    private BigDecimal applyConfirmReceiptNumber;

    /**
     * 附加交货时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date attachDeliveryTime;

    /**
     * 已收开票金额
     */
    private BigDecimal billedAmount;

    /**
     * 预付定金
     */
    private BigDecimal bondAmount;

    /**
     * 定金比率
     */
    private BigDecimal bondRate;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount;

    /**
     * 逾期天数
     */
    private Long breachDays;

    /**
     * 逾期罚息费率
     */
    private BigDecimal breachRate;

    /**
     * 买卖标识
     */
    private String buysellFlg;

    /**
     * 承运商
     */
    private String carrier;

    /**
     * 客户名称
     */
    private String companyName;

    /**
     * 对方联系地址
     */
    private String contactAddr;

    /**
     * 对方联系人
     */
    private String contactName;

    /**
     * 对方联系电话
     */
    private String contactPhone;

    /**
     * 合同结束日期(已全部收票,付款,入库,出库 的最后日期)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractEndTime;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同状态
     * 采购：N-新增，A-审批中，S-已签约(已盖章)，F1-已付款，G1-已收货，V1-已收票，D-完成，B-已审批，C-作废
     * 销售：N-新增，A-审批中，S-已签约(已盖章)，F2-已收款，G2-已发货，V2-已开票，D-完成，B-已审批，C-作废
     */
    private String contractStatus;

    /**
     * 合同时间
     */
    private Date contractTime;

    /**
     * 赊销天数
     */
    private Long creditCycle;

    /**
     * 是否使用授信
     */
    private Boolean creditFlg;

    /**
     * 不含税单价
     */
    private BigDecimal dealAmountNoTax;

    /**
     * 含税单价
     */
    private BigDecimal dealPrice;

    /**
     * 已收付款金额
     */
    private BigDecimal dealedAmount = BigDecimal.ZERO;

    /**
     * 是否完成支付货款，0未完成，1完成
     */
    private Boolean dealedFlg;

    /**
     * 交货地址
     */
    private String deliveryAddr;

    /**
     * 交货日期开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDateFrom;

    /**
     * 交货日期结束(到货时间)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDateTo;

    /**
     * 出/入库费用
     */
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    /**
     * 交货方式
     */
    private String deliveryMode;

    /**
     * 配送电话
     */
    private String deliveryPhone;

    /**
     * 提货方式
     */
    private String deliveryType;

    /**
     * 双签协议ID
     */
    private String doubleCheckFileId;

    /**
     * 补充条款
     */
    private String extraTerm;

    /**
     * 运费结算标识
     */
    private String freightSettlement;

    /**
     * 保理预算 收货证明附件Id
     */
    private String goodsFileId;

    /**
     * 罚息
     */
    private BigDecimal interestAmount = BigDecimal.ZERO;

    /**
     * 开票时间
     */
    private String invoiceDate;

    /**
     * 装车费
     */
    private BigDecimal LoadingAmount;

    /**
     * 损耗
     */
    private BigDecimal lossAmount;

    /**
     * 实际物流费用
     */
    private BigDecimal lossAmountByActual;

    /**
     * 物流方承担损耗金额
     */
    private BigDecimal lossAmountByLogistics;

    /**
     * 我方承担损耗金额
     */
    private BigDecimal lossAmountByOur;

    /**
     * 供应商承担损耗金额
     */
    private BigDecimal lossAmountBySupplier;

    /**
     * 损耗数量
     */
    private BigDecimal lossNumber;

    /**
     * 业务员名称
     */
    private String matchUserName;

    /**
     * 原始金额
     */
    private BigDecimal originalContractAmount;

    /**
     * 我方企业名称
     */
    private String ourCompanyName;

    /**
     * 收付定金日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payBondTime;

    /**
     * 收付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;

    /**
     * 付款方式
     */
    private String payMode;

    /**
     * 记录客户预选收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date preselectionConfirmDate;

    /**
     * 货名
     */
    private String productsName;

    /**
     * 质量标准  Y-原厂标准，G-过渡料，F-副牌料
     */
    private String qualityStandard;


    /**
     * 实际确认收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date realConfirmReceiptDate;

    /**
     * 实际收付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date realPayFullTime;

    /**
     * 实际运输费
     */
    private BigDecimal realTransportAmount;

    /**
     * 实际仓储费
     */
    private BigDecimal realWarehouseAmount;

    /**
     * 已收逾期罚息
     */
    private BigDecimal receiveBreachAmount = BigDecimal.ZERO;

    /**
     * 已收罚息
     */
    private BigDecimal receiveInterestAmount = BigDecimal.ZERO;

    /**
     * 是否已通过盖章审核
     */
    private Boolean sealFlg;

    /**
     * 结算方式 （背靠背白条业务专用） 0：赊销（一票制）  1：赊销（两票制）
     */
    private String settlementType;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 仓储费
     */
    private BigDecimal warehouseAmount = BigDecimal.ZERO;

    /**
     * 出入库附件ID
     */
    private String warehouseFileId;

    /**
     * 实际已入\出库数量
     */
    private BigDecimal warehouseNumber = BigDecimal.ZERO;

    /**
     * 出库费用
     */
    private BigDecimal deliveryOutFee;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 驾驶员身份证号
     */
    private String driverCardNo;

    /**
     * 司机
     */
    private String driverName;

    /**
     * 司机电话
     */
    private String driverPhone;

    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 品名
     */
    private String productName;

    /**
     * 状态
     */
    private String status;

    /**
     * 运输费
     */
    private BigDecimal transportAmount;

    /**
     * 交货的仓库地址
     */
    private String wareCompanyName;

    /**
     * 仓位/货位
     */
    private String warehousePosition;
    /**
     * 批号
     */
    private String warehouseBatchNo;

    /**
     * 仓库单号
     */
    private String warehouseNo;

    /**
     * 出库方式
     */
    private String warehouseOutType;

    /**
     * 出库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date warehouseOutDate;

    /**
     * 交货仓库电话
     */
    private String warehousePhone;


    public BigDecimal getActualContractAmount() {
        return actualContractAmount;
    }

    public void setActualContractAmount(BigDecimal actualContractAmount) {
        this.actualContractAmount = actualContractAmount;
    }

    public BigDecimal getApplyConfirmReceiptNumber() {
        return applyConfirmReceiptNumber;
    }

    public void setApplyConfirmReceiptNumber(BigDecimal applyConfirmReceiptNumber) {
        this.applyConfirmReceiptNumber = applyConfirmReceiptNumber;
    }

    public Date getAttachDeliveryTime() {
        return attachDeliveryTime;
    }

    public void setAttachDeliveryTime(Date attachDeliveryTime) {
        this.attachDeliveryTime = attachDeliveryTime;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public BigDecimal getBondAmount() {
        return bondAmount;
    }

    public void setBondAmount(BigDecimal bondAmount) {
        this.bondAmount = bondAmount;
    }

    public BigDecimal getBondRate() {
        return bondRate;
    }

    public void setBondRate(BigDecimal bondRate) {
        this.bondRate = bondRate;
    }

    public BigDecimal getBreachAmount() {
        return breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }

    public Long getBreachDays() {
        return breachDays;
    }

    public void setBreachDays(Long breachDays) {
        this.breachDays = breachDays;
    }

    public BigDecimal getBreachRate() {
        return breachRate;
    }

    public void setBreachRate(BigDecimal breachRate) {
        this.breachRate = breachRate;
    }

    public String getBuysellFlg() {
        return buysellFlg;
    }

    public void setBuysellFlg(String buysellFlg) {
        this.buysellFlg = buysellFlg;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Date getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(Date contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public Long getCreditCycle() {
        return creditCycle;
    }

    public void setCreditCycle(Long creditCycle) {
        this.creditCycle = creditCycle;
    }

    public Boolean getCreditFlg() {
        return creditFlg;
    }

    public void setCreditFlg(Boolean creditFlg) {
        this.creditFlg = creditFlg;
    }

    public BigDecimal getDealAmountNoTax() {
        return dealAmountNoTax;
    }

    public void setDealAmountNoTax(BigDecimal dealAmountNoTax) {
        this.dealAmountNoTax = dealAmountNoTax;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getDealedAmount() {
        return dealedAmount;
    }

    public void setDealedAmount(BigDecimal dealedAmount) {
        this.dealedAmount = dealedAmount;
    }

    public Boolean getDealedFlg() {
        return dealedFlg;
    }

    public void setDealedFlg(Boolean dealedFlg) {
        this.dealedFlg = dealedFlg;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public Date getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom(Date deliveryDateFrom) {
        this.deliveryDateFrom = deliveryDateFrom;
    }

    public Date getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo(Date deliveryDateTo) {
        this.deliveryDateTo = deliveryDateTo;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDoubleCheckFileId() {
        return doubleCheckFileId;
    }

    public void setDoubleCheckFileId(String doubleCheckFileId) {
        this.doubleCheckFileId = doubleCheckFileId;
    }

    public String getExtraTerm() {
        return extraTerm;
    }

    public void setExtraTerm(String extraTerm) {
        this.extraTerm = extraTerm;
    }

    public String getFreightSettlement() {
        return freightSettlement;
    }

    public void setFreightSettlement(String freightSettlement) {
        this.freightSettlement = freightSettlement;
    }

    public String getGoodsFileId() {
        return goodsFileId;
    }

    public void setGoodsFileId(String goodsFileId) {
        this.goodsFileId = goodsFileId;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getLoadingAmount() {
        return LoadingAmount;
    }

    public void setLoadingAmount(BigDecimal loadingAmount) {
        LoadingAmount = loadingAmount;
    }

    public BigDecimal getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    public BigDecimal getLossAmountByActual() {
        return lossAmountByActual;
    }

    public void setLossAmountByActual(BigDecimal lossAmountByActual) {
        this.lossAmountByActual = lossAmountByActual;
    }

    public BigDecimal getLossAmountByLogistics() {
        return lossAmountByLogistics;
    }

    public void setLossAmountByLogistics(BigDecimal lossAmountByLogistics) {
        this.lossAmountByLogistics = lossAmountByLogistics;
    }

    public BigDecimal getLossAmountByOur() {
        return lossAmountByOur;
    }

    public void setLossAmountByOur(BigDecimal lossAmountByOur) {
        this.lossAmountByOur = lossAmountByOur;
    }

    public BigDecimal getLossAmountBySupplier() {
        return lossAmountBySupplier;
    }

    public void setLossAmountBySupplier(BigDecimal lossAmountBySupplier) {
        this.lossAmountBySupplier = lossAmountBySupplier;
    }

    public BigDecimal getLossNumber() {
        return lossNumber;
    }

    public void setLossNumber(BigDecimal lossNumber) {
        this.lossNumber = lossNumber;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public BigDecimal getOriginalContractAmount() {
        return originalContractAmount;
    }

    public void setOriginalContractAmount(BigDecimal originalContractAmount) {
        this.originalContractAmount = originalContractAmount;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public Date getPayBondTime() {
        return payBondTime;
    }

    public void setPayBondTime(Date payBondTime) {
        this.payBondTime = payBondTime;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public Date getPreselectionConfirmDate() {
        return preselectionConfirmDate;
    }

    public void setPreselectionConfirmDate(Date preselectionConfirmDate) {
        this.preselectionConfirmDate = preselectionConfirmDate;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public String getQualityStandard() {
        return qualityStandard;
    }

    public void setQualityStandard(String qualityStandard) {
        this.qualityStandard = qualityStandard;
    }

    public Date getRealConfirmReceiptDate() {
        return realConfirmReceiptDate;
    }

    public void setRealConfirmReceiptDate(Date realConfirmReceiptDate) {
        this.realConfirmReceiptDate = realConfirmReceiptDate;
    }

    public Date getRealPayFullTime() {
        return realPayFullTime;
    }

    public void setRealPayFullTime(Date realPayFullTime) {
        this.realPayFullTime = realPayFullTime;
    }

    public BigDecimal getRealTransportAmount() {
        return realTransportAmount;
    }

    public void setRealTransportAmount(BigDecimal realTransportAmount) {
        this.realTransportAmount = realTransportAmount;
    }

    public BigDecimal getRealWarehouseAmount() {
        return realWarehouseAmount;
    }

    public void setRealWarehouseAmount(BigDecimal realWarehouseAmount) {
        this.realWarehouseAmount = realWarehouseAmount;
    }

    public BigDecimal getReceiveBreachAmount() {
        return receiveBreachAmount;
    }

    public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
        this.receiveBreachAmount = receiveBreachAmount;
    }

    public BigDecimal getReceiveInterestAmount() {
        return receiveInterestAmount;
    }

    public void setReceiveInterestAmount(BigDecimal receiveInterestAmount) {
        this.receiveInterestAmount = receiveInterestAmount;
    }

    public Boolean getSealFlg() {
        return sealFlg;
    }

    public void setSealFlg(Boolean sealFlg) {
        this.sealFlg = sealFlg;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getWarehouseAmount() {
        return warehouseAmount;
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public String getWarehouseFileId() {
        return warehouseFileId;
    }

    public void setWarehouseFileId(String warehouseFileId) {
        this.warehouseFileId = warehouseFileId;
    }

    public BigDecimal getWarehouseNumber() {
        return warehouseNumber;
    }

    public void setWarehouseNumber(BigDecimal warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
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

    public String getDriverCardNo() {
        return driverCardNo;
    }

    public void setDriverCardNo(String driverCardNo) {
        this.driverCardNo = driverCardNo;
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

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTransportAmount() {
        return transportAmount;
    }

    public void setTransportAmount(BigDecimal transportAmount) {
        this.transportAmount = transportAmount;
    }

    public String getWareCompanyName() {
        return wareCompanyName;
    }

    public void setWareCompanyName(String wareCompanyName) {
        this.wareCompanyName = wareCompanyName;
    }

    public String getWarehousePosition() {
        return warehousePosition;
    }

    public void setWarehousePosition(String warehousePosition) {
        this.warehousePosition = warehousePosition;
    }

    public String getWarehouseBatchNo() {
        return warehouseBatchNo;
    }

    public void setWarehouseBatchNo(String warehouseBatchNo) {
        this.warehouseBatchNo = warehouseBatchNo;
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

    public String getWarehouseOutType() {
        return warehouseOutType;
    }

    public void setWarehouseOutType(String warehouseOutType) {
        this.warehouseOutType = warehouseOutType;
    }

    public Date getWarehouseOutDate() {
        return warehouseOutDate;
    }

    public void setWarehouseOutDate(Date warehouseOutDate) {
        this.warehouseOutDate = warehouseOutDate;
    }

    public String getWarehousePhone() {
        return warehousePhone;
    }

    public void setWarehousePhone(String warehousePhone) {
        this.warehousePhone = warehousePhone;
    }
}
