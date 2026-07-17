package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.CtrContractSettlement;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RptContractSettlementVo {

    private Long approveId;

    /**
     * 预算编号
     */
    private String approveNo;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 赊销标识
     */
    private Boolean matchCreditFlg;

    /**
     * 采购合同ID
     */
    private Long buyContractId;

    /**
     * 采购合同号
     */
    private String buyContractNo;

    /**
     * 销售合同ID
     */
    private Long sellContractId;

    /**
     * 销售合同号
     */
    private String sellContractNo;

    /**
     * 品名
     */
    private String productsName;

    /**
     * 合同日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 收款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveDate;

    /**
     * 约定付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date appointPayDate;

    private Long deptId;

    private String deptName;

    /**
     * 采购业务员ID
     */
    private Long buyMatchUserId;

    /**
     * 采购业务员
     */
    private String buyMatchUserName;

    /**
     * 销售业务员ID
     */
    private Long sellMatchUserId;

    /**
     * 销售业务员
     */
    private String sellMatchUserName;

    /**
     * 采购我方抬头
     */
    private String buyOurCompanyName;

    /**
     * 销售我方抬头
     */
    private String sellOurCompanyName;

    /**
     * 销售企业ID
     */
    private Long sellCompanyId;

    /**
     * 销售企业名称
     */
    private String sellCompanyName;

    /**
     * 采购企业ID
     */
    private Long buyCompanyId;

    /**
     * 采购企业名称
     */
    private String buyCompanyName;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 采购单价
     */
    private BigDecimal buyPrice;

    /**
     * 销售单价
     */
    private BigDecimal sellPrice;

    /**
     * 采购合同总额
     */
    private BigDecimal buyTotalAmount;

    /**
     * 销售合同总额
     */
    private BigDecimal sellTotalAmount;

    /**
     * 合同账期
     */
    private Long creditCycle;

    /**
     * 金融服务费
     */
    private BigDecimal financialServiceAmount;

    /**
     * 保险费率
     */
    private BigDecimal insuranceRate;

    /**
     * 保费
     */
    private BigDecimal insuranceAmount;

    /**
     * 增值税税后差价
     */
    private BigDecimal vatSpreadAmount;

    /**
     * 增值税
     */
    private BigDecimal vatAmount;

    /**
     * 附加税
     */
    private BigDecimal surchargeAmount;

    /**
     * 印花税
     */
    private BigDecimal printAmount;

    /**
     * 税金及附加
     */
    private BigDecimal taxesSurchargesAmount;

    /**
     * 税后差价收入
     */
    private BigDecimal afterTaxSpreadAmount;

    /**
     * 交货日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryTime;

    /**
     * 预计结算日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;



    /**
     * 实际收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmReceiptDate;

    /**
     * 收票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveBillDate;

    /**
     * 汇总日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date summaryDate;

    /**
     * 结算日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date settlementDate;



    /**
     * 运费合计
     */
    private BigDecimal transportAmount;

    /**
     * 仓储费合计
     */
    private BigDecimal warehouseAmount;

    /**
     * 出库费用
     */
    private BigDecimal deliveryFee;

    /**
     * 装卸费
     */
    private BigDecimal stevedorage;

    /**
     * 逾期天数
     */
    private Long breachDay;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount;



    /**
     * 销售团队负责人分成
     */
    private BigDecimal sellHeadCommissionAmount;

    /**
     * 采购团队负责人分成
     */
    private BigDecimal buyHeadCommissionAmount;

    /**
     * 销售人员分成
     */
    private BigDecimal sellMatchAmount;

    /**
     * 采购人员分成
     */
    private BigDecimal buyMatchAmount;

    /**
     * 企业账号ID
     */
    private Long enterpriseId;

    /**
     * 唯一标识
     */
    private String settlementCode;

    /**
     * 全部收款标识
     */
    private Boolean receiveFlg = false;

    /**
     * 全部收货确认标识
     */
    private Boolean confirmFlg = false;

    /**
     * 全部收票标识
     */
    private Boolean billFlg = false;

    /**
     * 新：结算状态
     */
    private String settleStatus;

    /**
     * 汇总标识
     */
    private Boolean settleTotalFlg = false;

    /**
     * 是否有效
     */
    private Boolean enableFlg = false;

    /**
     * 采购业务员上级主管
     */
    private Long buyHeadUserId;

    /**
     * 销售业务员上级主管
     */
    private Long sellHeadUserId;

    /**
     * 代采赊销业务类型
     */
    private String businessTypeDcsx;



    /**
     * 合同状态
     * 采购：N-新增，A-审批中，S-已签约(已盖章)，F1-已付款，G1-已收货，V1-已收票，D-完成，B-已审批，C-作废 W-等待
     * 销售：N-新增，A-审批中，S-已签约(已盖章)，F2-已收款，G2-已发货，V2-已开票，D-完成，B-已审批，C-作废 W-等待
     */
    private String contractStatus;
    /**
     * 结算单状态	I-进行中， B-违约，D-已完成
     */
    private String status;
    private String settlementStatus;


}
