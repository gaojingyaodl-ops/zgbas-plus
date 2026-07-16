package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_ctr_dcsx")
public class ApplyCtrDCSX extends IdEntity implements IPmEntity {

    /**
     * 附件类型
     */
    private Long fileTypeId;
    /**
     * 记账凭证号
     */
    private  String inBillNo;
    /**
     * 发票号
     */
    private  String  InvoiceNo;

    /**
     * 发票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date InvoiceDate;

    /**
     * 盖章日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
     private Date stampDate;

    private Long enterpriseId;

    private String contractType;

    private String businessNo;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 预算编号
     */
    private String  budgetNo;

    public String getBudgetNo() {
        return budgetNo;
    }

    public void setBudgetNo(String budgetNo) {
        this.budgetNo = budgetNo;
    }

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;
    /**
     * 合同时间（签订日期）
     */
    private Date contractTime;
    /**
     * 对方企业ID
     */
    private Long companyId;
    /**
     * 对方企业名称
     */
    private String companyName;
    /**
     * 我方企业名称
     */
    private String ourCompanyName;
    /**
     * 提货方式
     */
    private String deliveryType;
    /**
     * 交货方式
     */
    private String deliveryMode;
    /**
     * 货物状态:B-采购，S-销售，I-入库，O-出库，PI-部分入库，PO-部分出库
     */
    private String productStatus;

    // 采购：N-新增，A-审批中，S-已签约(已盖章)，F1-已付款，G1-已    货，V1-已收票，D-完成，B-已审批，C-作废
    // 销售：N-新增，A-审批中，S-已签约(已盖章)，F2-已收款，G2-已发货，V2-已开票，D-完成，B-已审批，C-作废
    // 合同状态
    private String contractStatus;
    /**
     * 合同附件ID
     */
    private String fileId;
    /**
     * 审批ID
     */
    private Long approveId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 定金比率
     */
    private BigDecimal bondRate;
    /**
     * 状态
     */
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payBondTime;// 收付定金日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;// 收付全款日期

    /**
     * 上游付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyPayFullTime;
    
    /**
     * 下游交货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellDeliveryDate;
    
    
    private Long matchUserId;// 业务员ID
    private String matchUserName;// 业务员名称

    /**
     * 合作业务员ID
     */
    private Long cooperationMatchUserId;

    /**
     * 合作业务员名称
     */
    private String cooperationMatchUserName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean buysellFlg;// 买卖标识
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDateFrom;// 交货日期开始
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDateTo;// 交货日期结束(到货时间)
    /**
     * 配送电话
     */
    private String deliveryPhone;
    /**
     * 交货地址
     */
    private String deliveryAddr;
    private Boolean transferFlg; // 是否需转货权
    private String contactName;// 对方企业联系人
    private String contactPhone;// 对方联系电话
    private String contactAddr;// 对方联系地址
    private BigDecimal dealedAmount = BigDecimal.ZERO;// 已收付款金额
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean billFlg = false;// 发票状态
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean fondFlg = false;// 资金状态
    private BigDecimal transportAmount = BigDecimal.ZERO;// 运输费
    private BigDecimal warehouseAmount = BigDecimal.ZERO;// 仓储费
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean warehouseFlg = false;// 仓库状态
    /**
     * 预付定金：采购：付款金额，销售：收款金额
     */
    private BigDecimal bondAmount=BigDecimal.ZERO;
    /**
     * 付款方式
     */
    private String payType;

    private BigDecimal billedAmount = BigDecimal.ZERO;//已收票金额
    /**
     * 开票金额
     */
    private BigDecimal invoiceBillAmount= BigDecimal.ZERO;
    /**
     * 合同数量（吨位数）
     */
    private BigDecimal totalNumber;
    private BigDecimal warehouseNumber = BigDecimal.ZERO;// 实际已入\出库数量
    private String source;// 来源
    private String contractAttr;// 合同属性：N-现货，F-期货 ，X-赊销
    private String linkContractId;// 关联ID
    private Long deptId;// 部门ID
    private String productsName; // 货名
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean longFlg = false;// 是否长约
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean orverdurFlg;// 合同逾期标识
    private String qualityStandard; //质量标准  Y-原厂标准，G-过渡料，F-副牌料
    private String payMode;            // 付款方式
    private String attachDeliveryTime; // 附加交货时间
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean creditFlg = false;//是否使用授信
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean onLineFlg = false;//是否具备线上化
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean piccPushFlg = false;//PICC合同投保状态
    private BigDecimal confirmReceiveNumber = BigDecimal.ZERO;//确认收货数量

    /**
     * 是否完成确认收货，0未完成，1完成。
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean confirmReceiptFlg;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmDate;//确认收货日期
    private String businessType;//业务类型
    private String extraTerm;//补充条款
    private BigDecimal refundAmount = BigDecimal.ZERO;//退款金额
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractEndTime;    //合同结束日期(已全部收票,付款,入库,出库 的最后日期)
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean adjustFlg = false;//是否经过合同调整
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean applyCancelFlg = false;//是否发起合同作废申请
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean matchCreditFlg = false;//背靠背赊销标识
    /**
     * 收开票附件ID
     */
    private String invoiceFileId;
    private BigDecimal interestAmount = BigDecimal.ZERO;//罚息
    private BigDecimal receiveInterestAmount = BigDecimal.ZERO;//已收罚息
    private String pairCode;    //撮合合同排序号
    private String buyContentFileId;        //上传采购合同附件ID
    private String sellContentFileId;        //上传销售合同附件ID
    private Long bsTemplateContractId;        //电子合同模板ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean sealFlg = false;        //是否已通过盖章审核
    private BigDecimal serviceAmount = BigDecimal.ZERO;    //服务费
    private BigDecimal receiveServiceAmount = BigDecimal.ZERO;//已收服务费
    private Long serviceContractId;            //服务合同ID
    /**
     * 是否完成支付货款，0未完成，1完成
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean dealedFlg;
    /**
     * 是否完成支付服务费，0未完成，1完成
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean interestDealFlg;
    /**
     * 是否完成开货款发票，0未完成，1完成
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean billedFlg;
    /**
     * 是否完成开服务费发票，0未完成，1完成
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean interestBilledFlg;
    /**
     * 采购管家合同状态
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String contractStatusWx;
    /**
     * 结算方式 （背靠背白条业务专用） 0：赊销（一票制）  1：赊销（两票制）
     */
    private String settlementType;
    /**
     * 服务费已开票金额
     */
    private BigDecimal serviceBilledAmount = BigDecimal.ZERO;
    /**
     * 逾期罚息比例
     */
    private BigDecimal breachRate;
    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount = BigDecimal.ZERO;
    /**
     * 已收逾期罚息
     */
    private BigDecimal receiveBreachAmount = BigDecimal.ZERO;
    /**
     * 逾期天数
     */
    private Long breachDays;

    /**
     * 赊销天数
     */
    private Long creditCycle;
    /**
     * 损耗
     */
    private BigDecimal lossAmount;
    /**
     * 采购合同加价字段
     */
    private BigDecimal premium;

    /**
     * 使用抬头
     */
    private String companyTitle;
    /**
     * 含税单价
     */
    private BigDecimal dealPrice;

    /**
     * 不含税单价
     */
    private BigDecimal dealAmountNoTax;

    /**
     * 结算单价
     */
    private BigDecimal settlementDealPrice;

    /**
     * 结算总价
     */
    private BigDecimal settlementTotalAmount;

    /**
     * 损耗数量
     */
    private BigDecimal lossNumber;

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

    /**
     * 回款周期
     */
    private Long creditDays;

    /**
     * 品种
     */
    private String productBrand;

    /**
     *牌号
     */
    private  String productNum;

    /**
     * 付款时间
     */
    private  Date lastPayDate;

    /**
     * 厂商ID
     */
    private Long factoryId;

    /**
     * 厂商名称
     */
    private String factoryName;
    /**
     * 包装规格
     */
    private String wrapSpecs;
    /**
     * 付款编号
     */
    private   String applyNo;

    /**
     *未付金额
     */
    private  BigDecimal unpayedAmount;

    /**
     * 收/付款
     */
    private BigDecimal applyPayAmount = BigDecimal.ZERO;
    /**
     * 开/收票
     */
    private BigDecimal applyBillAmount = BigDecimal.ZERO;
    /**
     * 出/入库
     */
    private BigDecimal applyWarehouseNumber = BigDecimal.ZERO;

    public Date getTicketDueTime() {
        return ticketDueTime;
    }

    public void setTicketDueTime(Date ticketDueTime) {
        this.ticketDueTime = ticketDueTime;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
     private  Date ticketDueTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private  Date dueTime;

    /**
     * 采购合同单价
     */
    private BigDecimal buyPrice;

    /**
     * 收款金额
     */
    private BigDecimal receiveAmount  = BigDecimal.ZERO;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveDate;

    /**
     * 裝卸費
     */
    private BigDecimal stevedorage;

    /**
     * 每吨毛利润
     */
    private BigDecimal  grossProfit;

    /**
     * 付退款金额
     */
    private BigDecimal payRefundAmount = BigDecimal.ZERO;

    /**
     * 收退款金额
     */
    private BigDecimal receiveRefundAmount = BigDecimal.ZERO;

    /**
     * 逾期罚息
     */
    private BigDecimal overdueInterest = BigDecimal.ZERO;

    /**
     * (下游最后一笔回款日 - 苏高新第一笔付款日 + 1) <= 15，加合同金额的千1
     * 合同额外成本
     */
    private BigDecimal extraCost = BigDecimal.ZERO;

    /**
     * 承兑贴息成本：（承兑到期日 - 收承兑日期 + 1）* 0.02/360 - 已收客户贴息成本，贴息成本财务可以修改
     */
    private BigDecimal acceptDiscountCost = BigDecimal.ZERO;

    /**
     * 结算状态
     * 0-未结算
     * 1-申请中
     * 2.已结算
     */
    private String settlementStatus;

    /**
     * 已付逾期罚息
     */
    private BigDecimal receiveOverdueInterest = BigDecimal.ZERO;

    /**
     * 是否中止逾期罚息更新标识
     */
    private Boolean interestAbortFlg = false;

    /**
     * 逾期计算明细Json
     */
    private String calculateDetail;

    /**
     * 我方开户行
     */
    private String ourBankName;

    /**
     * 我方账号
     */
    private String ourBankAccount;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 账号
     */
    private String bankAccount;

    /**
     * 资方利率
     */
    private BigDecimal zfRate;

    /**
     * 资方利息
     */
    private BigDecimal zfInterest;

    /**
     * 已收资方利息
     */
    private BigDecimal receiveZfInterest;

    public BigDecimal getZfRate() {
        return defaultNum(zfRate);
    }

    public void setZfRate(BigDecimal zfRate) {
        this.zfRate = zfRate;
    }

    public BigDecimal getZfInterest() {
        return defaultNum(zfInterest);
    }

    public void setZfInterest(BigDecimal zfInterest) {
        this.zfInterest = zfInterest;
    }

    public BigDecimal getReceiveZfInterest() {
        return defaultNum(receiveZfInterest);
    }

    public void setReceiveZfInterest(BigDecimal receiveZfInterest) {
        this.receiveZfInterest = receiveZfInterest;
    }

    private BigDecimal defaultNum(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 库存采购中游合同标识
     */
    private Boolean virtualFlg = false;

    /**
     * 补充协议附件ID
     */
    private String protocolFileId;

    /**
     * cfca安心签合同编号
     */
    private String cfcaProtocolFileNo;

    /**
     * 外部隐藏
     */
    private String hideOut = "0";

    public String getProtocolFileId() {
        return protocolFileId;
    }

    public void setProtocolFileId(String protocolFileId) {
        this.protocolFileId = protocolFileId;
    }

    public String getCfcaProtocolFileNo() {
        return cfcaProtocolFileNo;
    }

    public void setCfcaProtocolFileNo(String cfcaProtocolFileNo) {
        this.cfcaProtocolFileNo = cfcaProtocolFileNo;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getStevedorage() {
        return stevedorage;
    }

    public void setStevedorage(BigDecimal stevedorage) {
        this.stevedorage = stevedorage;
    }

    public BigDecimal getReceiveAmount() {
        return Objects.isNull(receiveAmount) ? BigDecimal.ZERO : receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BigDecimal getUnpayedAmount() {
        return unpayedAmount;
    }

    public void setUnpayedAmount(BigDecimal unpayedAmount) {
        this.unpayedAmount = unpayedAmount;
    }


    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }



    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getWrapSpecs() {
        return wrapSpecs;
    }

    public void setWrapSpecs(String wrapSpecs) {
        this.wrapSpecs = wrapSpecs;
    }

    public Date getLastPayDate() {
        return lastPayDate;
    }

    public void setLastPayDate(Date lastPayDate) {
        this.lastPayDate = lastPayDate;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public Long getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }
    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String praoductNum) {
        this.productNum = praoductNum;
    }
    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }


    public Date getStampDate() {
        return stampDate;
    }

    public void setStampDate(Date stampDate) {
        this.stampDate = stampDate;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        InvoiceNo = invoiceNo;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getApproveId() {
        return approveId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getBondRate() {
        return bondRate;
    }

    public void setBondRate(BigDecimal bondRate) {
        this.bondRate = bondRate;
    }

    public String getStatus() {
        return status;
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

    public Date getBuyPayFullTime() {
        return buyPayFullTime;
    }

    public void setBuyPayFullTime(Date buyPayFullTime) {
        this.buyPayFullTime = buyPayFullTime;
    }

    public Date getSellDeliveryDate() {
        return sellDeliveryDate;
    }

    public void setSellDeliveryDate(Date sellDeliveryDate) {
        this.sellDeliveryDate = sellDeliveryDate;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
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

    public Boolean getBuysellFlg() {
        return buysellFlg;
    }

    public void setBuysellFlg(Boolean buysellFlg) {
        this.buysellFlg = buysellFlg;
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

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public Boolean getTransferFlg() {
        return transferFlg;
    }

    public void setTransferFlg(Boolean transferFlg) {
        this.transferFlg = transferFlg;
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

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public BigDecimal getDealedAmount() {
        return Objects.isNull(dealedAmount) ? BigDecimal.ZERO: dealedAmount;
    }

    public void setDealedAmount(BigDecimal dealedAmount) {
        this.dealedAmount = dealedAmount;
    }

    public Boolean getBillFlg() {
        return billFlg;
    }

    public void setBillFlg(Boolean billFlg) {
        this.billFlg = billFlg;
    }

    public Boolean getFondFlg() {
        return fondFlg;
    }

    public void setFondFlg(Boolean fondFlg) {
        this.fondFlg = fondFlg;
    }

    public BigDecimal getTransportAmount() {
        return transportAmount;
    }

    public void setTransportAmount(BigDecimal transportAmount) {
        this.transportAmount = transportAmount;
    }

    public BigDecimal getWarehouseAmount() {
        return warehouseAmount;
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public Boolean getWarehouseFlg() {
        return warehouseFlg;
    }

    public void setWarehouseFlg(Boolean warehouseFlg) {
        this.warehouseFlg = warehouseFlg;
    }

    public BigDecimal getBondAmount() {
        return bondAmount;
    }

    public void setBondAmount(BigDecimal bondAmount) {
        this.bondAmount = bondAmount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getWarehouseNumber() {
        return warehouseNumber;
    }

    public void setWarehouseNumber(BigDecimal warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContractAttr() {
        return contractAttr;
    }

    public void setContractAttr(String contractAttr) {
        this.contractAttr = contractAttr;
    }

    public String getLinkContractId() {
        return linkContractId;
    }

    public void setLinkContractId(String linkContractId) {
        this.linkContractId = linkContractId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public Boolean getLongFlg() {
        return longFlg;
    }

    public void setLongFlg(Boolean longFlg) {
        this.longFlg = longFlg;
    }

    public Boolean getOrverdurFlg() {
        return orverdurFlg;
    }

    public void setOrverdurFlg(Boolean orverdurFlg) {
        this.orverdurFlg = orverdurFlg;
    }

    public String getQualityStandard() {
        return qualityStandard;
    }

    public void setQualityStandard(String qualityStandard) {
        this.qualityStandard = qualityStandard;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getAttachDeliveryTime() {
        return attachDeliveryTime;
    }

    public void setAttachDeliveryTime(String attachDeliveryTime) {
        this.attachDeliveryTime = attachDeliveryTime;
    }

    public Boolean getCreditFlg() {
        return creditFlg;
    }

    public void setCreditFlg(Boolean creditFlg) {
        this.creditFlg = creditFlg;
    }

    public Boolean getOnLineFlg() {
        return onLineFlg;
    }

    public void setOnLineFlg(Boolean onLineFlg) {
        this.onLineFlg = onLineFlg;
    }

    public Boolean getPiccPushFlg() {
        return piccPushFlg;
    }

    public void setPiccPushFlg(Boolean piccPushFlg) {
        this.piccPushFlg = piccPushFlg;
    }

    public BigDecimal getConfirmReceiveNumber() {
        return confirmReceiveNumber;
    }

    public void setConfirmReceiveNumber(BigDecimal confirmReceiveNumber) {
        this.confirmReceiveNumber = confirmReceiveNumber;
    }

    public Boolean getConfirmReceiptFlg() {
        return confirmReceiptFlg;
    }

    public void setConfirmReceiptFlg(Boolean confirmReceiptFlg) {
        this.confirmReceiptFlg = confirmReceiptFlg;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getExtraTerm() {
        return extraTerm;
    }

    public void setExtraTerm(String extraTerm) {
        this.extraTerm = extraTerm;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Date getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(Date contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public Boolean getAdjustFlg() {
        return adjustFlg;
    }

    public void setAdjustFlg(Boolean adjustFlg) {
        this.adjustFlg = adjustFlg;
    }

    public Boolean getApplyCancelFlg() {
        return applyCancelFlg;
    }

    public void setApplyCancelFlg(Boolean applyCancelFlg) {
        this.applyCancelFlg = applyCancelFlg;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }

    public String getInvoiceFileId() {
        return invoiceFileId;
    }

    public void setInvoiceFileId(String invoiceFileId) {
        this.invoiceFileId = invoiceFileId;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public BigDecimal getReceiveInterestAmount() {
        return receiveInterestAmount;
    }

    public void setReceiveInterestAmount(BigDecimal receiveInterestAmount) {
        this.receiveInterestAmount = receiveInterestAmount;
    }
    public Date getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(Date inInvoiceDate) {
        InvoiceDate = InvoiceDate;
    }
    public String getPairCode() {
        return pairCode;
    }

    public void setPairCode(String pairCode) {
        this.pairCode = pairCode;
    }

    public String getBuyContentFileId() {
        return buyContentFileId;
    }

    public void setBuyContentFileId(String buyContentFileId) {
        this.buyContentFileId = buyContentFileId;
    }

    public String getSellContentFileId() {
        return sellContentFileId;
    }

    public void setSellContentFileId(String sellContentFileId) {
        this.sellContentFileId = sellContentFileId;
    }

    public Long getBsTemplateContractId() {
        return bsTemplateContractId;
    }

    public void setBsTemplateContractId(Long bsTemplateContractId) {
        this.bsTemplateContractId = bsTemplateContractId;
    }

    public Boolean getSealFlg() {
        return sealFlg;
    }

    public void setSealFlg(Boolean sealFlg) {
        this.sealFlg = sealFlg;
    }

    public BigDecimal getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(BigDecimal serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public BigDecimal getReceiveServiceAmount() {
        return receiveServiceAmount;
    }

    public void setReceiveServiceAmount(BigDecimal receiveServiceAmount) {
        this.receiveServiceAmount = receiveServiceAmount;
    }

    public Long getServiceContractId() {
        return serviceContractId;
    }

    public void setServiceContractId(Long serviceContractId) {
        this.serviceContractId = serviceContractId;
    }

    public Boolean getDealedFlg() {
        return dealedFlg;
    }

    public void setDealedFlg(Boolean dealedFlg) {
        this.dealedFlg = dealedFlg;
    }

    public Boolean getInterestDealFlg() {
        return interestDealFlg;
    }

    public void setInterestDealFlg(Boolean interestDealFlg) {
        this.interestDealFlg = interestDealFlg;
    }

    public Boolean getBilledFlg() {
        return billedFlg;
    }

    public void setBilledFlg(Boolean billedFlg) {
        this.billedFlg = billedFlg;
    }

    public Boolean getInterestBilledFlg() {
        return interestBilledFlg;
    }

    public void setInterestBilledFlg(Boolean interestBilledFlg) {
        this.interestBilledFlg = interestBilledFlg;
    }

    public String getContractStatusWx() {
        return contractStatusWx;
    }

    public void setContractStatusWx(String contractStatusWx) {
        this.contractStatusWx = contractStatusWx;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public BigDecimal getServiceBilledAmount() {
        return serviceBilledAmount;
    }

    public void setServiceBilledAmount(BigDecimal serviceBilledAmount) {
        this.serviceBilledAmount = serviceBilledAmount;
    }

    public BigDecimal getBreachRate() {
        return breachRate;
    }

    public void setBreachRate(BigDecimal breachRate) {
        this.breachRate = breachRate;
    }

    public BigDecimal getBreachAmount() {
        return breachAmount==null?BigDecimal.ZERO:breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }

    public BigDecimal getReceiveBreachAmount() {
        return receiveBreachAmount==null?BigDecimal.ZERO:receiveBreachAmount;
    }

    public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
        this.receiveBreachAmount = receiveBreachAmount;
    }
    public String getInBillNo() {
        return inBillNo;
    }

    public void setInBillNo(String inBillNo) {
        this.inBillNo = inBillNo;
    }
    public Long getBreachDays() {
        return breachDays;
    }

    public void setBreachDays(Long breachDays) {
        this.breachDays = breachDays;
    }

    public Long getCreditCycle() {
        return creditCycle;
    }

    public void setCreditCycle(Long creditCycle) {
        this.creditCycle = creditCycle;
    }

    public BigDecimal getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getCompanyTitle() {
        return companyTitle;
    }

    public void setCompanyTitle(String companyTitle) {
        this.companyTitle = companyTitle;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getDealAmountNoTax() {
        return dealAmountNoTax;
    }

    public void setDealAmountNoTax(BigDecimal dealAmountNoTax) {
        this.dealAmountNoTax = dealAmountNoTax;
    }

    public BigDecimal getSettlementDealPrice() {
        return settlementDealPrice;
    }

    public void setSettlementDealPrice(BigDecimal settlementDealPrice) {
        this.settlementDealPrice = settlementDealPrice;
    }

    public BigDecimal getSettlementTotalAmount() {
        return settlementTotalAmount;
    }

    public void setSettlementTotalAmount(BigDecimal settlementTotalAmount) {
        this.settlementTotalAmount = settlementTotalAmount;
    }

    public BigDecimal getLossNumber() {
        return lossNumber;
    }

    public void setLossNumber(BigDecimal lossNumber) {
        this.lossNumber = lossNumber;
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

    public Long getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Long creditDays) {
        this.creditDays = creditDays;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getApplyPayAmount() {
        return applyPayAmount;
    }

    public void setApplyPayAmount(BigDecimal applyPayAmount) {
        this.applyPayAmount = applyPayAmount;
    }

    public BigDecimal getApplyBillAmount() {
        return applyBillAmount;
    }

    public void setApplyBillAmount(BigDecimal applyBillAmount) {
        this.applyBillAmount = applyBillAmount;
    }

    public BigDecimal getApplyWarehouseNumber() {
        return applyWarehouseNumber;
    }

    public void setApplyWarehouseNumber(BigDecimal applyWarehouseNumber) {
        this.applyWarehouseNumber = applyWarehouseNumber;
    }

    public BigDecimal getInvoiceBillAmount() {
        return Objects.isNull(invoiceBillAmount) ? BigDecimal.ZERO: invoiceBillAmount;
    }

    public void setInvoiceBillAmount(BigDecimal invoiceBillAmount) {
        this.invoiceBillAmount = invoiceBillAmount;
    }

    public Long getCooperationMatchUserId() {
        return cooperationMatchUserId;
    }

    public void setCooperationMatchUserId(Long cooperationMatchUserId) {
        this.cooperationMatchUserId = cooperationMatchUserId;
    }

    public String getCooperationMatchUserName() {
        return cooperationMatchUserName;
    }

    public void setCooperationMatchUserName(String cooperationMatchUserName) {
        this.cooperationMatchUserName = cooperationMatchUserName;
    }

    public BigDecimal getPayRefundAmount() {
        if(payRefundAmount == null) {
            payRefundAmount = BigDecimal.ZERO;
        }
        return payRefundAmount;
    }

    public void setPayRefundAmount(BigDecimal payRefundAmount) {
        this.payRefundAmount = payRefundAmount;
    }

    public BigDecimal getReceiveRefundAmount() {
        if(receiveRefundAmount == null) {
            receiveRefundAmount = BigDecimal.ZERO;
        }
        return receiveRefundAmount;
    }

    public void setReceiveRefundAmount(BigDecimal receiveRefundAmount) {
        this.receiveRefundAmount = receiveRefundAmount;
    }

    public BigDecimal getOverdueInterest() {
        return Objects.isNull(overdueInterest) ? BigDecimal.ZERO: overdueInterest;
    }

    public void setOverdueInterest(BigDecimal overdueInterest) {
        this.overdueInterest = overdueInterest;
    }

    public BigDecimal getReceiveOverdueInterest() {
        return Objects.isNull(receiveOverdueInterest) ? BigDecimal.ZERO: receiveOverdueInterest;
    }

    public void setReceiveOverdueInterest(BigDecimal receiveOverdueInterest) {
        this.receiveOverdueInterest = receiveOverdueInterest;
    }

    public Boolean getInterestAbortFlg() {
        return interestAbortFlg;
    }

    public void setInterestAbortFlg(Boolean interestAbortFlg) {
        this.interestAbortFlg = interestAbortFlg;
    }

    public String getCalculateDetail() {
        return calculateDetail;
    }

    public void setCalculateDetail(String calculateDetail) {
        this.calculateDetail = calculateDetail;
    }

    public String getOurBankName() {
        return ourBankName;
    }

    public void setOurBankName(String ourBankName) {
        this.ourBankName = ourBankName;
    }

    public String getOurBankAccount() {
        return ourBankAccount;
    }

    public void setOurBankAccount(String ourBankAccount) {
        this.ourBankAccount = ourBankAccount;
    }

    public Boolean getVirtualFlg() {
        return virtualFlg;
    }

    public void setVirtualFlg(Boolean virtualFlg) {
        this.virtualFlg = virtualFlg;
    }

    public BigDecimal getExtraCost() {
        return defaultNum(extraCost);
    }

    public void setExtraCost(BigDecimal extraCost) {
        this.extraCost = extraCost;
    }

    public BigDecimal getAcceptDiscountCost() {
        return defaultNum(acceptDiscountCost);
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

    public String getHideOut() {
        return hideOut;
    }

    public void setHideOut(String hideOut) {
        this.hideOut = hideOut;
    }
}
