package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 业务链条合同表
 */
@Entity
@Table(name = "t_ctr_contract_chain")
@DynamicInsert
@DynamicUpdate
public class CtrContractChain extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = 3286328644310294465L;

    /**
     * 企业账套Id
     */
    private Long enterpriseId;

    /**
     * 合同类型
     */
    private String contractType;

    /**
     * 业务编号
     */
    private String businessNo;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 合同时间
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
     * 付款方式
     */
    private String payType;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 交货方式
     */
    private String deliveryMode;

    /**
     * 交货地址
     */
    private String deliveryAddr;

    /**
     * 配送电话
     */
    private String deliveryPhone;

    /**
     * 货物状态:B-采购，S-销售，I-入库，O-出库，PI-部分入库，PO-部分出库
     */
    private String productStatus;

    /**
     * 合同状态
     * 采购：N-新增，A-审批中，S-已签约(已盖章)，F1-已付款，G1-已收货，V1-已收票，D-完成，B-已审批，C-作废 W-等待
     * 销售：N-新增，A-审批中，S-已签约(已盖章)，F2-已收款，G2-已发货，V2-已开票，D-完成，B-已审批，C-作废 W-等待
     */
    private String contractStatus;

    /**
     * 合同附件ID
     */
    private String fileId;

    /**
     * 收开票附件ID
     */
    private String invoiceFileId;

    /**
     * 收付款附件ID
     */
    private String amountFileId;

    /**
     * 出入库附件ID
     */
    private String warehouseFileId;

    /**
     * 双签协议ID
     */
    private String doubleCheckFileId;

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
     * 预付定金：采购：付款金额，销售：收款金额
     */
    private BigDecimal bondAmount;

    /**
     * 状态
     */
    private String status;

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
     * 约定收付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date appointPayFullTime;

    /**
     * 实际收付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date realPayFullTime;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员名称
     */
    private String matchUserName;

    /**
     * 买卖标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean buysellFlg;

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
     * 是否需转货权
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean transferFlg;

    /**
     * 对方企业联系人
     */
    private String contactName;

    /**
     * 对方联系电话
     */
    private String contactPhone;

    /**
     * 对方联系地址
     */
    private String contactAddr;

    /**
     * 发票状态
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean billFlg = false;

    /**
     * 资金状态
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean fondFlg = false;

    /**
     * 运输费
     */
    private BigDecimal transportAmount = BigDecimal.ZERO;

    /**
     * 仓储费
     */
    private BigDecimal warehouseAmount = BigDecimal.ZERO;

    /**
     * 实际运输费
     */
    private BigDecimal realTransportAmount;

    /**
     * 实际仓储费
     */
    private BigDecimal realWarehouseAmount;

    /**
     * 装车费
     */
    private BigDecimal LoadingAmount;

    /**
     * 罚息
     */
    private BigDecimal interestAmount = BigDecimal.ZERO;

    /**
     * 已收罚息
     */
    private BigDecimal receiveInterestAmount = BigDecimal.ZERO;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount = BigDecimal.ZERO;

    /**
     * 已收逾期罚息
     */
    private BigDecimal receiveBreachAmount = BigDecimal.ZERO;

    /**
     * 罚息费率
     */
    private BigDecimal interestRate;

    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean transportFlg;

    /**
     * 仓库状态
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean warehouseFlg = false;

    // 2.采购合同，增加查询条件“状态”：未入库、未付款、未收票
    // 3.销售合同，增加查询条件“状态”：未出库、未收款、未开票

    /**
     * 已开票已收票金额
     */
    private BigDecimal billedAmount = BigDecimal.ZERO;

    /**
     * 已收付款金额
     */
    private BigDecimal dealedAmount = BigDecimal.ZERO;

    /**
     * 实际已入\出库数量
     */
    private BigDecimal warehouseNumber = BigDecimal.ZERO;

    /**
     * 来源
     */
    private String source;

    /**
     * 合同属性：N-现货，F-期货 ，X-赊销
     */
    private String contractAttr;

    /**
     * 关联ID
     */
    private String linkContractId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 货名
     */
    private String productsName;

    /**
     * 是否长约
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean longFlg = false;
    private List<CtrContractApply> ctrContractApply;

    /**
     * 外商合同号
     */
    private String foreignContractNo;

    /**
     * 合同逾期标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean orverdurFlg;

    /**
     * saas采购申请订单Id
     */
    private String saasOrderId;

    /**
     * saas合同号
     */
    private String saasContractNo;

    /**
     * 质量标准  Y-原厂标准，G-过渡料，F-副牌料
     */
    private String qualityStandard;

    /**
     * 付款方式
     */
    private String payMode;

    /**
     * 附加交货时间
     */
    private String attachDeliveryTime;

    /**
     * 补充条款
     */
    private String extraTerm;

    /**
     * 开票时间
     */
    private String invoiceDate;

    /**
     * 是否使用授信
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean creditFlg = false;

    /**
     * 是否具备线上化
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean onLineFlg = false;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 确认收货数量
     */
    private BigDecimal confirmReceiveNumber = BigDecimal.ZERO;

    /**
     * 确认收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date confirmDate;

    /**
     * 是否完成确认收货，0未完成，1完成。
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean confirmReceiptFlg;

    /**
     * PICC合同投保状态
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean piccPushFlg = false;

    /**
     * PICC合同投保回传信息
     */
    private String piccMessage;
    @JsonSerialize(using = ToStringSerializer.class)

    /**
     * PICC合同回款状态
     */
    private Boolean piccReceiveFlg;

    /**
     * PICC合同回款回传信息
     */
    private String piccReceiveMessage;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount = BigDecimal.ZERO;

    /**
     * 合同结束日期(已全部收票,付款,入库,出库 的最后日期)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractEndTime;

    /**
     * 是否经过合同调整
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean adjustFlg = false;

    /**
     * 人保可用额度
     */
    private BigDecimal piccRemainCredit;

    /**
     * 是否发起合同作废申请
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean applyCancelFlg = false;

    /**
     * 背靠背赊销标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean matchCreditFlg = false;

    /**
     * 撮合合同排序号
     */
    private String pairCode;

    /**
     * 上传采购合同附件ID
     */
    private String buyContentFileId;

    /**
     * 上传销售合同附件ID
     */
    private String sellContentFileId;

    /**
     * 上传服务合同模板ID
     */
    private String serviceContentFileId;

    /**
     * 电子合同模板ID
     */
    private Long bsTemplateContractId;

    /**
     * 是否已通过盖章审核
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean sealFlg = false;

    /**
     * 服务费
     */
    private BigDecimal serviceAmount = BigDecimal.ZERO;

    /**
     * 已收服务费
     */
    private BigDecimal receiveServiceAmount = BigDecimal.ZERO;

    /**
     * 服务合同ID
     */
    private Long serviceContractId;

    /**
     * 服务费已开票金额
     */
    private BigDecimal serviceBilledAmount = BigDecimal.ZERO;

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
     * 违约标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean violateTreatyFlg;

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
     * 逾期罚息比例
     */
    private BigDecimal breachRate;

    /**
     * 逾期天数
     */
    private Long breachDays;

    /**
     * 赊销天数
     */
    private Long creditCycle;

    /**
     * 出货日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date piccHappenDate;

    /**
     * 应付款日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date piccAccrualDate;

    /**
     * 人保合同总额
     */
    private BigDecimal piccTotalAmount = BigDecimal.ZERO;

    /**
     * 审批赊销金额
     */
    private BigDecimal approveCreditAmount = BigDecimal.ZERO;

    /**
     * 含税单价
     */
    private BigDecimal dealPrice;

    /**
     * 不含税单价
     */
    private BigDecimal dealAmountNoTax;

    /**
     * 损耗
     */
    private BigDecimal lossAmount;
    /**
     * 保理收金额
     * @return
     */
    private BigDecimal factoringAmount;

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
     * 采购合同加价字段
     */
    private BigDecimal premium;

    /**
     * 使用抬头
     */
    private String companyTitle;

    /**
     * 代采赊销业务类型
     */
    private String businessTypeDcsx;

    /**
     * 合同模式
     */
    private String contractModel;

    /**
     * 运费结算标识
     */
    private String freightSettlement;

    /**
     * 出/入库费用
     */
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    /**
     * 承运商
     */
    private String carrier;

    /**
     * 原始金额
     */
    private BigDecimal originalContractAmount;
    /**
     * 抵扣金额
     */
    private BigDecimal deductibleAmount;
    /**
     * 实际金额
     */
    private BigDecimal actualContractAmount;

    /**
     * 债权凭证附件ID
     */
    private String debtCertificateFileId;

    /**
     * 记录客户预选收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date preselectionConfirmDate;
    /**
     * 物流单状态
     */
    private String deliveryStaus;

    /**
     * 客户订单号
     *
     */
    private String customerOrderCode;

    /**
     * 中标状态
     */
    private String biddinStatus = "N";

    /**
     * 履约状态
     * N-进行中，B-宽限期，D-催告期，S-逾期，P-违约
     */
    private String performanceStatus;
    /**
     * 诉讼费
     */
    private BigDecimal litigationFees;
    /**
     * 保全费
     */
    private BigDecimal securityFees;
    /**
     * 律师费
     */
    private BigDecimal legalFees;
    /**
     * 历史罚息总额
     */
    private BigDecimal sellInterestAmount;
    /**
     * 预算运输费
     */
    private BigDecimal approveTransportAmount;

    /**
     * 预算仓储费
     */
    private BigDecimal  approveWarehouseAmount;

    /**
     * 品种
     */
    private String productBrand;

    /**
     *牌号
     */
    private  String productNum;

    /**
     * 审批编号
     */
    private String approveNo;

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }

    public BigDecimal getFactoringAmount() {
        return factoringAmount;
    }

    public void setFactoringAmount(BigDecimal factoringAmount) {
        this.factoringAmount = factoringAmount;
    }

    public BigDecimal getApproveTransportAmount() {
        return approveTransportAmount;
    }

    public void setApproveTransportAmount(BigDecimal approveTransportAmount) {
        this.approveTransportAmount = approveTransportAmount;
    }

    public BigDecimal getApproveWarehouseAmount() {
        return approveWarehouseAmount;
    }

    public void setApproveWarehouseAmount(BigDecimal approveWarehouseAmount) {
        this.approveWarehouseAmount = approveWarehouseAmount;
    }


    public BigDecimal getSellInterestAmount() {
        return defaultNum(sellInterestAmount);
    }

    public void setSellInterestAmount(BigDecimal sellInterestAmount) {
        this.sellInterestAmount = sellInterestAmount;
    }

    public String getPerformanceStatus() {
        return performanceStatus;
    }

    public void setPerformanceStatus(String performanceStatus) {
        this.performanceStatus = performanceStatus;
    }

    public BigDecimal getLitigationFees() {
        return litigationFees;
    }

    public void setLitigationFees(BigDecimal litigationFees) {
        this.litigationFees = litigationFees;
    }

    public BigDecimal getSecurityFees() {
        return securityFees;
    }

    public void setSecurityFees(BigDecimal securityFees) {
        this.securityFees = securityFees;
    }

    public BigDecimal getLegalFees() {
        return legalFees;
    }

    public void setLegalFees(BigDecimal legalFees) {
        this.legalFees = legalFees;
    }

    /**
     * 来源
     *
     */
    private String applySource;

    private String outOrderNo; //	String	花旗订单编号

    public String getOutOrderNo() {
        return outOrderNo;
    }

    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public String getBiddinStatus() {
        return biddinStatus;
    }

    public void setBiddinStatus(String biddinStatus) {
        this.biddinStatus = biddinStatus;
    }

    public String getCustomerOrderCode() {
        return customerOrderCode;
    }

    public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }

    public String getDeliveryStaus() {
        return deliveryStaus;
    }

    public void setDeliveryStaus(String deliveryStaus) {
        this.deliveryStaus = deliveryStaus;
    }

    public BigDecimal getPremium() {
        return defaultNum(premium);
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getDealPrice() {
        return defaultNum(dealPrice);
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getDealAmountNoTax() {
        return defaultNum(dealAmountNoTax);
    }

    public void setDealAmountNoTax(BigDecimal dealAmountNoTax) {
        this.dealAmountNoTax = dealAmountNoTax;
    }

    public Date getPreselectionConfirmDate() {
        return preselectionConfirmDate;
    }

    public void setPreselectionConfirmDate(Date preselectionConfirmDate) {
        this.preselectionConfirmDate = preselectionConfirmDate;
    }

    public String getDebtCertificateFileId() {
        return debtCertificateFileId;
    }
    /**
     *
     * @return 保理预算 收货证明附件Id
     */
    private String goodsFileId;

    /**
     * 保理状态
     * N-待处理、Z-资料已收集、B-保证金已支付、F-银联已放款、D-已还款
     */
    private String factorStatus = BasConstants.FACTOR_STATUS_N;

    public String getFactorStatus() {
        return factorStatus;
    }

    public void setFactorStatus(String factorStatus) {
        this.factorStatus = factorStatus;
    }

    public String getGoodsFileId() {
        return goodsFileId;
    }

    public void setGoodsFileId(String goodsFileId) {
        this.goodsFileId = goodsFileId;
    }




    public void setDebtCertificateFileId(String debtCertificateFileId) {
        this.debtCertificateFileId = debtCertificateFileId;
    }

    public BigDecimal getOriginalContractAmount() {
        return originalContractAmount;
    }

    public void setOriginalContractAmount(BigDecimal originalContractAmount) {
        this.originalContractAmount = originalContractAmount;
    }

    public BigDecimal getDeductibleAmount() {
        return deductibleAmount;
    }

    public void setDeductibleAmount(BigDecimal deductibleAmount) {
        this.deductibleAmount = deductibleAmount;
    }

    public BigDecimal getActualContractAmount() {
        return actualContractAmount;
    }

    public void setActualContractAmount(BigDecimal actualContractAmount) {
        this.actualContractAmount = actualContractAmount;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public BigDecimal getDeliveryFee() {
        return defaultNum(deliveryFee);
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getFreightSettlement() {
        return freightSettlement;
    }

    public void setFreightSettlement(String freightSettlement) {
        this.freightSettlement = freightSettlement;
    }

    public String getContractModel() {
        return contractModel;
    }

    public void setContractModel(String contractModel) {
        this.contractModel = contractModel;
    }

    public String getBusinessTypeDcsx() {
        return businessTypeDcsx;
    }

    public void setBusinessTypeDcsx(String businessTypeDcsx) {
        this.businessTypeDcsx = businessTypeDcsx;
    }

    public String getContractStatusWx() {
        return contractStatusWx;
    }

    public void setContractStatusWx(String contractStatusWx) {
        this.contractStatusWx = contractStatusWx;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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


    public Boolean getViolateTreatyFlg() {
        return violateTreatyFlg;
    }

    public void setViolateTreatyFlg(Boolean violateTreatyFlg) {
        this.violateTreatyFlg = violateTreatyFlg;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryInTime;    //临时属性收货日期

    // 关联查询 ctrContractApply
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ctrContractId")
    //@JoinColumn(name = "ctrContractId")
    public List<CtrContractApply> getCtrContractApply() {
        return ctrContractApply;
    }

    public void setCtrContractApply(List<CtrContractApply> ctrContractApply) {
        this.ctrContractApply = ctrContractApply;
    }

    @Transient
    public Date getDeliveryInTime() {
        return deliveryInTime;
    }

    public void setDeliveryInTime(Date deliveryInTime) {
        this.deliveryInTime = deliveryInTime;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
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
        return defaultNum(totalAmount);
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

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getBondRate() {
        return defaultNum(bondRate);
    }

    public void setBondRate(BigDecimal bondRate) {
        this.bondRate = bondRate;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
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

    public BigDecimal getBondAmount() {
        return defaultNum(bondAmount);
    }

    public void setBondAmount(BigDecimal bondAmount) {
        this.bondAmount = bondAmount;
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
        return defaultNum(dealedAmount);
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
        return defaultNum(transportAmount);
    }

    public void setTransportAmount(BigDecimal transportAmount) {
        this.transportAmount = transportAmount;
    }

    public BigDecimal getWarehouseAmount() {
        return defaultNum(warehouseAmount);
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public Boolean getTransportFlg() {
        return transportFlg;
    }

    public void setTransportFlg(Boolean transportFlg) {
        this.transportFlg = transportFlg;
    }

    public Boolean getWarehouseFlg() {
        return warehouseFlg;
    }

    public void setWarehouseFlg(Boolean warehouseFlg) {
        this.warehouseFlg = warehouseFlg;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Boolean getBuysellFlg() {
        return buysellFlg;
    }

    public void setBuysellFlg(Boolean buysellFlg) {
        this.buysellFlg = buysellFlg;
    }

    public BigDecimal getBilledAmount() {
        return defaultNum(billedAmount);
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public BigDecimal getTotalNumber() {
        return defaultNum(totalNumber);
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public BigDecimal getWarehouseNumber() {
        return defaultNum(warehouseNumber);
    }

    public void setWarehouseNumber(BigDecimal warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
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

    public String getForeignContractNo() {
        return foreignContractNo;
    }

    public void setForeignContractNo(String foreignContractNo) {
        this.foreignContractNo = foreignContractNo;
    }

    public Boolean getOrverdurFlg() {
        return orverdurFlg;
    }

    public void setOrverdurFlg(Boolean orverdurFlg) {
        this.orverdurFlg = orverdurFlg;
    }

    public String getSaasContractNo() {
        return saasContractNo;
    }

    public void setSaasContractNo(String saasContractNo) {
        this.saasContractNo = saasContractNo;
    }

    public String getSaasOrderId() {
        return saasOrderId;
    }

    public void setSaasOrderId(String saasOrderId) {
        this.saasOrderId = saasOrderId;
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

    public Boolean getOnLineFlg() {
        return onLineFlg;
    }

    public void setOnLineFlg(Boolean onLineFlg) {
        this.onLineFlg = onLineFlg;
    }

    public Boolean getCreditFlg() {
        return creditFlg;
    }

    public void setCreditFlg(Boolean creditFlg) {
        this.creditFlg = creditFlg;
    }

    public String getInvoiceFileId() {
        return invoiceFileId;
    }

    public void setInvoiceFileId(String invoiceFileId) {
        this.invoiceFileId = invoiceFileId;
    }

    public String getAmountFileId() {
        return amountFileId;
    }

    public void setAmountFileId(String amountFileId) {
        this.amountFileId = amountFileId;
    }

    public String getWarehouseFileId() {
        return warehouseFileId;
    }

    public void setWarehouseFileId(String warehouseFileId) {
        this.warehouseFileId = warehouseFileId;
    }

    public BigDecimal getLoadingAmount() {
        return defaultNum(LoadingAmount);
    }

    public void setLoadingAmount(BigDecimal loadingAmount) {
        LoadingAmount = loadingAmount;
    }

    public BigDecimal getInterestAmount() {
        return defaultNum(interestAmount);
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public BigDecimal getConfirmReceiveNumber() {
        return defaultNum(confirmReceiveNumber);
    }

    public void setConfirmReceiveNumber(BigDecimal confirmReceiveNumber) {
        this.confirmReceiveNumber = confirmReceiveNumber;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public Boolean getPiccPushFlg() {
        return piccPushFlg;
    }

    public void setPiccPushFlg(Boolean piccPushFlg) {
        this.piccPushFlg = piccPushFlg;
    }

    public String getPiccMessage() {
        return piccMessage;
    }

    public void setPiccMessage(String piccMessage) {
        this.piccMessage = piccMessage;
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

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getRefundAmount() {
        return defaultNum(refundAmount);
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Boolean getPiccReceiveFlg() {
        return piccReceiveFlg;
    }

    public void setPiccReceiveFlg(Boolean piccReceiveFlg) {
        this.piccReceiveFlg = piccReceiveFlg;
    }

    public String getPiccReceiveMessage() {
        return piccReceiveMessage;
    }

    public void setPiccReceiveMessage(String piccReceiveMessage) {
        this.piccReceiveMessage = piccReceiveMessage;
    }

    public Date getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(Date contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public BigDecimal getRealTransportAmount() {
        return defaultNum(realTransportAmount);
    }

    public void setRealTransportAmount(BigDecimal realTransportAmount) {
        this.realTransportAmount = realTransportAmount;
    }

    public BigDecimal getRealWarehouseAmount() {
        return defaultNum(realWarehouseAmount);
    }

    public void setRealWarehouseAmount(BigDecimal realWarehouseAmount) {
        this.realWarehouseAmount = realWarehouseAmount;
    }

    public Boolean getAdjustFlg() {
        return adjustFlg;
    }

    public void setAdjustFlg(Boolean adjustFlg) {
        this.adjustFlg = adjustFlg;
    }

    public BigDecimal getPiccRemainCredit() {
        return defaultNum(piccRemainCredit);
    }

    public void setPiccRemainCredit(BigDecimal piccRemainCredit) {
        this.piccRemainCredit = piccRemainCredit;
    }

    public Boolean getApplyCancelFlg() {
        return applyCancelFlg;
    }

    public void setApplyCancelFlg(Boolean applyCancelFlg) {
        this.applyCancelFlg = applyCancelFlg;
    }

    public BigDecimal getReceiveInterestAmount() {
        return defaultNum(receiveInterestAmount);
    }

    public void setReceiveInterestAmount(BigDecimal receiveInterestAmount) {
        this.receiveInterestAmount = receiveInterestAmount;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }


    public String getPairCode() {
        return pairCode;
    }

    public void setPairCode(String pairCode) {
        this.pairCode = pairCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((approveId == null) ? 0 : approveId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CtrContractChain other = (CtrContractChain) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public BigDecimal findRealDealedAmount() {
        return this.dealedAmount.subtract(this.refundAmount);
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

    public String getServiceContentFileId() {
        return serviceContentFileId;
    }

    public void setServiceContentFileId(String serviceContentFileId) {
        this.serviceContentFileId = serviceContentFileId;
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
        return defaultNum(serviceAmount);
    }

    public void setServiceAmount(BigDecimal serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public Long getServiceContractId() {
        return serviceContractId;
    }

    public void setServiceContractId(Long serviceContractId) {
        this.serviceContractId = serviceContractId;
    }

    public BigDecimal getReceiveServiceAmount() {
        return defaultNum(receiveServiceAmount);
    }

    public void setReceiveServiceAmount(BigDecimal receiveServiceAmount) {
        this.receiveServiceAmount = receiveServiceAmount;
    }

    public Boolean getConfirmReceiptFlg() {
        return confirmReceiptFlg;
    }

    public void setConfirmReceiptFlg(Boolean confirmReceiptFlg) {
        this.confirmReceiptFlg = confirmReceiptFlg;
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

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getBreachRate() {
        return breachRate;
    }

    public void setBreachRate(BigDecimal breachRate) {
        this.breachRate = breachRate;
    }

    public BigDecimal getBreachAmount() {
        return breachAmount == null ? BigDecimal.ZERO : breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }

    public BigDecimal getReceiveBreachAmount() {
        return receiveBreachAmount == null ? BigDecimal.ZERO : receiveBreachAmount;
    }

    public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
        this.receiveBreachAmount = receiveBreachAmount;
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

    private BigDecimal defaultNum(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public Date getPiccHappenDate() {
        return piccHappenDate;
    }

    public void setPiccHappenDate(Date piccHappenDate) {
        this.piccHappenDate = piccHappenDate;
    }

    public Date getPiccAccrualDate() {
        return piccAccrualDate;
    }

    public void setPiccAccrualDate(Date piccAccrualDate) {
        this.piccAccrualDate = piccAccrualDate;
    }

    public BigDecimal getPiccTotalAmount() {
        return piccTotalAmount;
    }

    public void setPiccTotalAmount(BigDecimal piccTotalAmount) {
        this.piccTotalAmount = piccTotalAmount;
    }

    @Transient
    public BigDecimal getApproveCreditAmount() {
        return approveCreditAmount;
    }

    public void setApproveCreditAmount(BigDecimal approveCreditAmount) {
        this.approveCreditAmount = approveCreditAmount;
    }

    public BigDecimal getLossAmount() {
        return defaultNum(lossAmount);
    }

    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    public String getCompanyTitle() {
        return companyTitle;
    }

    public void setCompanyTitle(String companyTitle) {
        this.companyTitle = companyTitle;
    }

    public BigDecimal getLossNumber() {
        return defaultNum(lossNumber);
    }

    public void setLossNumber(BigDecimal lossNumber) {
        this.lossNumber = lossNumber;
    }

    public BigDecimal getLossAmountByLogistics() {
        return defaultNum(lossAmountByLogistics);
    }

    public void setLossAmountByLogistics(BigDecimal lossAmountByLogistics) {
        this.lossAmountByLogistics = lossAmountByLogistics;
    }

    public BigDecimal getLossAmountByActual() {
        return defaultNum(lossAmountByActual);
    }

    public void setLossAmountByActual(BigDecimal lossAmountByActual) {
        this.lossAmountByActual = lossAmountByActual;
    }

    public BigDecimal getLossAmountBySupplier() {
        return defaultNum(lossAmountBySupplier);
    }

    public void setLossAmountBySupplier(BigDecimal lossAmountBySupplier) {
        this.lossAmountBySupplier = lossAmountBySupplier;
    }

    public BigDecimal getLossAmountByOur() {
        return defaultNum(lossAmountByOur);
    }

    public void setLossAmountByOur(BigDecimal lossAmountByOur) {
        this.lossAmountByOur = lossAmountByOur;
    }

    public Date getAppointPayFullTime() {
        return appointPayFullTime;
    }

    public void setAppointPayFullTime(Date appointPayFullTime) {
        this.appointPayFullTime = appointPayFullTime;
    }

    public Date getRealPayFullTime() {
        return realPayFullTime;
    }

    public void setRealPayFullTime(Date realPayFullTime) {
        this.realPayFullTime = realPayFullTime;
    }
}
