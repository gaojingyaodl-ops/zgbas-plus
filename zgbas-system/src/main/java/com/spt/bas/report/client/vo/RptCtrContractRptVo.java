package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: gaojy
 * @create 2022/8/31 9:34
 * @version: 1.0
 * @description:
 */
public class RptCtrContractRptVo {
    /**
     * 合同ID
     */
    private Long id;

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
     * 合同逾期标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean orverdurFlg;

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
     * 业务类型 
     *
     * SXDC - 赊销代采
     * SXDCHDFK - 赊销代采货到付款
     * DCSX - 代采赊销
     * DCSXHDFK - 代采赊销货到付款
     * DCSXBL - 代采赊销保理
     * DC - 代采
     * SX - 赊销
     * SXHDFK -赊销货到付款
     * SXBL - 赊销保理
     * ZYCG - 自营采购
     * ZYXS - 自营销售
     */
    private String businessKind;

    /**
     * 确认收货数量
     */
    private BigDecimal confirmReceiveNumber = BigDecimal.ZERO;

    /**
     * 确认收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
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
     * 双签日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date sealDate;

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
     *
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
    private BigDecimal approveWarehouseAmount;

    /**
     * 装卸费
     */
    private BigDecimal stevedorage;

    /**
     * 应收本金
     */
    private BigDecimal receivablePrincipal;

    /**
     * 保费费率
     */
    private BigDecimal insuranceRate;

    /**
     * 保费
     */
    private BigDecimal insuranceAmount;

    private Boolean canStartBuy = false;
    private String replyStatus;//回复状态
    private String contractTimeStr;//合同时间
    private BigDecimal orverdurAmount;//逾期金额
    private String deptName;//事业部
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date lastPayDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date lastDeliveryDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date lastBillDate;
    private String piccPushFlgStr;
    private String piccReceiveFlgStr;
    private Boolean existServiceText = false;
    private Boolean existServiceTextFileId = false;
    private String serviceFileUrl;
    private Boolean existContractText;
    private Boolean existContractTextFileId = false;
    private String contractFileUrl;
    private Integer contractDifTime;
    private Long pairId;
    private BigDecimal deliveryOutFee;
    private Boolean existWarehouse = false;// 存在入库
    private Boolean allWarehouse = false;// 全部出库
    private String violateFlg;//标识
    private BigDecimal receivableBalance;// 应收余额
    /**
     * 实际确认收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date realConfirmReceiptDate;

    /**
     * 虚拟类型
     * KC-库存采购
     * XY-协议采购
     */
    private String virtualType;

    /**
     * 虚拟单ID
     */
    private Long virtualId;

    /**
     * 虚拟库存采购ID
     */
    private Long virtualContractId;

    /**
     * 虚拟库存采购No
     */
    private String virtualContractNo;

    /**
     * 贴现费用承担方
     * K-客户承担
     * Y-业务员承担
     * W-我方承担
     */
    private String discountChargeTarget;

    /**
     * 贴现费用
     */
    private BigDecimal discountChargeAmount = BigDecimal.ZERO;

    /**
     * 已收贴现费用
     */
    private BigDecimal discountReceiveAmount = BigDecimal.ZERO;

    /**
     * 发货附件ID
     */
    private String shippingFileId;
    
    /**
     * 发货单url
     */
    private String shippingFileUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

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

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
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

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public String getDoubleCheckFileId() {
        return doubleCheckFileId;
    }

    public void setDoubleCheckFileId(String doubleCheckFileId) {
        this.doubleCheckFileId = doubleCheckFileId;
    }

    public Long getApproveId() {
        return approveId;
    }

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
        return bondRate;
    }

    public void setBondRate(BigDecimal bondRate) {
        this.bondRate = bondRate;
    }

    public BigDecimal getBondAmount() {
        return bondAmount;
    }

    public void setBondAmount(BigDecimal bondAmount) {
        this.bondAmount = bondAmount;
    }

    public String getStatus() {
        return status;
    }

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

    public BigDecimal getLoadingAmount() {
        return LoadingAmount;
    }

    public void setLoadingAmount(BigDecimal loadingAmount) {
        LoadingAmount = loadingAmount;
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

    public BigDecimal getBreachAmount() {
        return breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }

    public BigDecimal getReceiveBreachAmount() {
        return receiveBreachAmount;
    }

    public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
        this.receiveBreachAmount = receiveBreachAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
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

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public BigDecimal getDealedAmount() {
        return dealedAmount;
    }

    public void setDealedAmount(BigDecimal dealedAmount) {
        this.dealedAmount = dealedAmount;
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

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public BigDecimal getConfirmReceiveNumber() {
        return confirmReceiveNumber;
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

    public Boolean getConfirmReceiptFlg() {
        return confirmReceiptFlg;
    }

    public void setConfirmReceiptFlg(Boolean confirmReceiptFlg) {
        this.confirmReceiptFlg = confirmReceiptFlg;
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

    public BigDecimal getPiccRemainCredit() {
        return piccRemainCredit;
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

    public Date getSealDate() {
        return sealDate;
    }

    public void setSealDate(Date sealDate) {
        this.sealDate = sealDate;
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

    public BigDecimal getServiceBilledAmount() {
        return serviceBilledAmount;
    }

    public void setServiceBilledAmount(BigDecimal serviceBilledAmount) {
        this.serviceBilledAmount = serviceBilledAmount;
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

    public BigDecimal getBreachRate() {
        return breachRate;
    }

    public void setBreachRate(BigDecimal breachRate) {
        this.breachRate = breachRate;
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

    public BigDecimal getApproveCreditAmount() {
        return approveCreditAmount;
    }

    public void setApproveCreditAmount(BigDecimal approveCreditAmount) {
        this.approveCreditAmount = approveCreditAmount;
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

    public BigDecimal getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    public BigDecimal getFactoringAmount() {
        return factoringAmount;
    }

    public void setFactoringAmount(BigDecimal factoringAmount) {
        this.factoringAmount = factoringAmount;
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

    public String getBusinessTypeDcsx() {
        return businessTypeDcsx;
    }

    public void setBusinessTypeDcsx(String businessTypeDcsx) {
        this.businessTypeDcsx = businessTypeDcsx;
    }

    public String getContractModel() {
        return contractModel;
    }

    public void setContractModel(String contractModel) {
        this.contractModel = contractModel;
    }

    public String getFreightSettlement() {
        return freightSettlement;
    }

    public void setFreightSettlement(String freightSettlement) {
        this.freightSettlement = freightSettlement;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
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

    public String getDebtCertificateFileId() {
        return debtCertificateFileId;
    }

    public void setDebtCertificateFileId(String debtCertificateFileId) {
        this.debtCertificateFileId = debtCertificateFileId;
    }

    public Date getPreselectionConfirmDate() {
        return preselectionConfirmDate;
    }

    public void setPreselectionConfirmDate(Date preselectionConfirmDate) {
        this.preselectionConfirmDate = preselectionConfirmDate;
    }

    public String getDeliveryStaus() {
        return deliveryStaus;
    }

    public void setDeliveryStaus(String deliveryStaus) {
        this.deliveryStaus = deliveryStaus;
    }

    public String getCustomerOrderCode() {
        return customerOrderCode;
    }

    public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }

    public String getBiddinStatus() {
        return biddinStatus;
    }

    public void setBiddinStatus(String biddinStatus) {
        this.biddinStatus = biddinStatus;
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

    public BigDecimal getSellInterestAmount() {
        return sellInterestAmount;
    }

    public void setSellInterestAmount(BigDecimal sellInterestAmount) {
        this.sellInterestAmount = sellInterestAmount;
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

    public Boolean getCanStartBuy() {
        return canStartBuy;
    }

    public void setCanStartBuy(Boolean canStartBuy) {
        this.canStartBuy = canStartBuy;
    }

    public String getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }

    public String getContractTimeStr() {
        return contractTimeStr;
    }

    public void setContractTimeStr(String contractTimeStr) {
        this.contractTimeStr = contractTimeStr;
    }

    public BigDecimal getOrverdurAmount() {
        return orverdurAmount;
    }

    public void setOrverdurAmount(BigDecimal orverdurAmount) {
        this.orverdurAmount = orverdurAmount;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Date getLastPayDate() {
        return lastPayDate;
    }

    public void setLastPayDate(Date lastPayDate) {
        this.lastPayDate = lastPayDate;
    }

    public Date getLastDeliveryDate() {
        return lastDeliveryDate;
    }

    public void setLastDeliveryDate(Date lastDeliveryDate) {
        this.lastDeliveryDate = lastDeliveryDate;
    }

    public Date getLastBillDate() {
        return lastBillDate;
    }

    public void setLastBillDate(Date lastBillDate) {
        this.lastBillDate = lastBillDate;
    }

    public String getPiccPushFlgStr() {
        return piccPushFlgStr;
    }

    public void setPiccPushFlgStr(String piccPushFlgStr) {
        this.piccPushFlgStr = piccPushFlgStr;
    }

    public String getPiccReceiveFlgStr() {
        return piccReceiveFlgStr;
    }

    public void setPiccReceiveFlgStr(String piccReceiveFlgStr) {
        this.piccReceiveFlgStr = piccReceiveFlgStr;
    }

    public Boolean getExistServiceText() {
        return existServiceText;
    }

    public void setExistServiceText(Boolean existServiceText) {
        this.existServiceText = existServiceText;
    }

    public Boolean getExistServiceTextFileId() {
        return existServiceTextFileId;
    }

    public void setExistServiceTextFileId(Boolean existServiceTextFileId) {
        this.existServiceTextFileId = existServiceTextFileId;
    }

    public String getServiceFileUrl() {
        return serviceFileUrl;
    }

    public void setServiceFileUrl(String serviceFileUrl) {
        this.serviceFileUrl = serviceFileUrl;
    }

    public Boolean getExistContractText() {
        return existContractText;
    }

    public void setExistContractText(Boolean existContractText) {
        this.existContractText = existContractText;
    }

    public Boolean getExistContractTextFileId() {
        return existContractTextFileId;
    }

    public void setExistContractTextFileId(Boolean existContractTextFileId) {
        this.existContractTextFileId = existContractTextFileId;
    }

    public String getContractFileUrl() {
        return contractFileUrl;
    }

    public void setContractFileUrl(String contractFileUrl) {
        this.contractFileUrl = contractFileUrl;
    }

    public Integer getContractDifTime() {
        return contractDifTime;
    }

    public void setContractDifTime(Integer contractDifTime) {
        this.contractDifTime = contractDifTime;
    }

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    public BigDecimal getDeliveryOutFee() {
        return deliveryOutFee;
    }

    public void setDeliveryOutFee(BigDecimal deliveryOutFee) {
        this.deliveryOutFee = deliveryOutFee;
    }

    public Boolean getExistWarehouse() {
        return existWarehouse;
    }

    public void setExistWarehouse(Boolean existWarehouse) {
        this.existWarehouse = existWarehouse;
    }

    public Boolean getAllWarehouse() {
        return allWarehouse;
    }

    public void setAllWarehouse(Boolean allWarehouse) {
        this.allWarehouse = allWarehouse;
    }

    public String getViolateFlg() {
        return violateFlg;
    }

    public void setViolateFlg(String violateFlg) {
        this.violateFlg = violateFlg;
    }

    public BigDecimal getReceivableBalance() {
        return receivableBalance;
    }

    public void setReceivableBalance(BigDecimal receivableBalance) {
        this.receivableBalance = receivableBalance;
    }

    public Date getRealConfirmReceiptDate() {
        return realConfirmReceiptDate;
    }

    public String getBusinessKind() {
        return businessKind;
    }

    public void setBusinessKind(String businessKind) {
        this.businessKind = businessKind;
    }

    public void setRealConfirmReceiptDate(Date realConfirmReceiptDate) {
        this.realConfirmReceiptDate = realConfirmReceiptDate;
    }

    public BigDecimal getStevedorage() {
        return stevedorage;
    }

    public void setStevedorage(BigDecimal stevedorage) {
        this.stevedorage = stevedorage;
    }

    public String getVirtualType() {
        return virtualType;
    }

    public void setVirtualType(String virtualType) {
        this.virtualType = virtualType;
    }

    public Long getVirtualId() {
        return virtualId;
    }

    public void setVirtualId(Long virtualId) {
        this.virtualId = virtualId;
    }

    public Long getVirtualContractId() {
        return virtualContractId;
    }

    public void setVirtualContractId(Long virtualContractId) {
        this.virtualContractId = virtualContractId;
    }

    public String getVirtualContractNo() {
        return virtualContractNo;
    }

    public void setVirtualContractNo(String virtualContractNo) {
        this.virtualContractNo = virtualContractNo;
    }

    public BigDecimal getReceivablePrincipal() {
        return receivablePrincipal;
    }

    public void setReceivablePrincipal(BigDecimal receivablePrincipal) {
        this.receivablePrincipal = receivablePrincipal;
    }

    public BigDecimal getInsuranceRate() {
        return insuranceRate;
    }

    public void setInsuranceRate(BigDecimal insuranceRate) {
        this.insuranceRate = insuranceRate;
    }

    public BigDecimal getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(BigDecimal insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    public String getDiscountChargeTarget() {
        return discountChargeTarget;
    }

    public void setDiscountChargeTarget(String discountChargeTarget) {
        this.discountChargeTarget = discountChargeTarget;
    }

    public BigDecimal getDiscountChargeAmount() {
        return discountChargeAmount;
    }

    public void setDiscountChargeAmount(BigDecimal discountChargeAmount) {
        this.discountChargeAmount = discountChargeAmount;
    }

    public BigDecimal getDiscountReceiveAmount() {
        return discountReceiveAmount;
    }

    public void setDiscountReceiveAmount(BigDecimal discountReceiveAmount) {
        this.discountReceiveAmount = discountReceiveAmount;
    }

    public RptCtrContractRptVo() {
    }

    public String getShippingFileId() {
        return shippingFileId;
    }

    public void setShippingFileId(String shippingFileId) {
        this.shippingFileId = shippingFileId;
    }

    public String getShippingFileUrl() {
        return shippingFileUrl;
    }

    public void setShippingFileUrl(String shippingFileUrl) {
        this.shippingFileUrl = shippingFileUrl;
    }
}
