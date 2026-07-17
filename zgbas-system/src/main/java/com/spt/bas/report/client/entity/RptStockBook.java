package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 库存台账
 */
@Data
public class RptStockBook {
    /**
     * 库存编号
     */
    private String stockVirtualNo;
    /**
     * 销售合同号
     */
    private String contractNo;
    /**
     * 下游我方
     */
    private String ourCompanyName;
    /**
     * 下游客户
     */
    private String companyName;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 业务类型
     */
    private String businessTypeName;
    /**
     * 赊销标识
     */
    private Boolean matchCreditFlg;
    /**
     * 货名
     */
    private String productsName;
    /**
     * 业务员
     */
    private String matchUserName;
    /**
     * 业务大区
     */
    private Long deptId;
    /**
     * 业务大区名称
     */
    private String deptName;
    /**
     * 数量
     */
    private BigDecimal totalNumber;
    /**
     * 销售指导价
     */
    private BigDecimal minSellPrice;

    /**
     * 采购价
     */
    private BigDecimal buyPrice;
    /**
     * 销售价
     */
    private BigDecimal sellPrice;
    /**
     * 指导价毛利 计算公式 =（销售价*数量）-（销售指导价*数量）
     */
    private BigDecimal minSellPriceProfit;
    /**
     * 采购价毛利 计算公式 =销售价*数量）-（采购价*数量）
     */
    private BigDecimal buyPriceProfit;
    /**
     * 在库天数(确认收货日期-上游支付日期)
     */
    private Integer warehouseDay;
    /**
     * 下游账期
     */
    private Integer creditCycle;
    /**
     *资金成本 计算公式=（采购价*数量）*下游我方年化利率*下游账期
     */
    private BigDecimal costOfFunds;
    /**
     * 仓储费
     */
    private BigDecimal warehouseAmount;
    /**
     * 运输费
     */
    private BigDecimal transportAmount;
    /**
     * 装卸费
     */
    private BigDecimal stevedorage;
    /**
     * 保费费率
     */
    private BigDecimal insuranceRate;

    /**
     * 保费(销售总价*保费费率)
     */
    private BigDecimal insuranceAmount;

    /**
     * 签约日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 出库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date realWarehoseDate;

    /**
     * 支付上游日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;
    /**
     * 确认收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmDate;

    /**
     * KUB合同采购价
     */
    private BigDecimal kubBuyPrice;

    public Boolean getMatchCreditFlg() {
        return Objects.isNull(matchCreditFlg) ? Boolean.FALSE : matchCreditFlg;
    }
    /**
     * 支付上游金额
     */
    private BigDecimal sumPayAmount;
    /**
     * 下游回款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveDate;
    /**
     * 下游付款金额
     */
    private BigDecimal sumReceiveAmount;
    /**
     * 销售总价
     */
    private BigDecimal totalAmount;
}
