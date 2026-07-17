package com.spt.bas.server.util;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyConfig;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrContractSettlementCommission;
import com.spt.bas.client.vo.CtrCalCulateInsuranceParam;
import com.spt.bas.client.vo.CtrCalCulateParam;
import com.spt.bas.client.vo.ParamByCompanyGrade;
import com.spt.pm.util.ResConditionParser;
import com.spt.tools.core.date.DateOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.datameta.Variable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 提成结算计算工具类
 *
 * @Author: gaojy
 * @create 2022/4/2 10:18
 * @version: 1.0
 * @description:
 */
@Component
public class CommissionCalculateUtil {
    Logger logger = LoggerFactory.getLogger(CommissionCalculateUtil.class);
    private final Long KH_DEPT_ID = 67957L;
    private final String KH_DEPT_NAME = "改性塑料事业部";
    private final BigDecimal PARAM_1_13 = BigDecimal.valueOf(1.13);
    private final BigDecimal PARAM_1_09 = BigDecimal.valueOf(1.09);
    private final BigDecimal PARAM_1_06 = BigDecimal.valueOf(1.06);
    private final BigDecimal PARAM_0_12 = BigDecimal.valueOf(0.12);
    private final BigDecimal PARAM_0_08 = BigDecimal.valueOf(0.08);
    private final BigDecimal PARAM_0_10 = BigDecimal.valueOf(0.1);
    private final BigDecimal PARAM_0_22 = BigDecimal.valueOf(0.22);
    private final BigDecimal PARAM_0_29 = BigDecimal.valueOf(0.29);
    private final BigDecimal PARAM_0_33 = BigDecimal.valueOf(0.33);
    private final BigDecimal PARAM_0_35 = BigDecimal.valueOf(0.35);
    private final BigDecimal PARAM_0_01 = BigDecimal.valueOf(0.01);
    private final BigDecimal PARAM_0_03 = BigDecimal.valueOf(0.03);
    private final BigDecimal PARAM_0_07 = BigDecimal.valueOf(0.07);
    private final BigDecimal PARAM_0_001 = BigDecimal.valueOf(0.001);
    private final BigDecimal PARAM_0_0003 = BigDecimal.valueOf(0.0003);
    private final BigDecimal PARAM_0_0004 = BigDecimal.valueOf(0.0004);
    private final BigDecimal PARAM_0_6 = BigDecimal.valueOf(0.6);
    private final BigDecimal PARAM_5 = BigDecimal.valueOf(5);
    private final BigDecimal PARAM_INSURANCE_0_001696 = BigDecimal.valueOf(0.001696);
    private final BigDecimal PARAM_INSURANCE_0_001749 = BigDecimal.valueOf(0.001749);
    private final BigDecimal PARAM_INSURANCE_0_001 = BigDecimal.valueOf(0.001);
    private final BigDecimal PARAM_INSURANCE_0_0012 = BigDecimal.valueOf(0.0012);
    private final BigDecimal PARAM_INSURANCE_0_0015 = BigDecimal.valueOf(0.0015);
    private final BigDecimal PARAM_INSURANCE_0_0020 = BigDecimal.valueOf(0.002);
    private final BigDecimal PARAM_INSURANCE_0_0019 = BigDecimal.valueOf(0.0019);
    private final BigDecimal PARAM_INSURANCE_0_0025 = BigDecimal.valueOf(0.0025);

    /**
     * 获取业务员逾期罚息费用
     *
     * @param sellContract 销售合同
     * @param settlement   结算单
     * @param param        提成配置项
     * @return 获取业务员逾期罚息费用
     */
    public BigDecimal calculateBreachAmount(CtrContract sellContract, CtrContractSettlement settlement, CtrCalCulateParam param) {
        return this.getMatchBreachAmount(sellContract.getBreachRate(), sellContract.getBreachAmount(), param, settlement.verifyBeforeAprilFlg());
    }

    /**
     * 印花税
     *
     * @param settlement 结算单
     * @param param      提成配置项
     * @return 印花税
     */
    public BigDecimal calculatePrintAmount(CtrContractSettlement settlement, CtrCalCulateParam param) {
        return this.getPrintAmount(settlement.getSellPrice(), settlement.getDealNumber(), param.getStampDutyRate());
    }

    /**
     * 增值税税后差价
     *
     * @param settlement 结算单
     * @return 增值税税后差价
     */
    public BigDecimal calculateVatSpreadAmount(CtrContractSettlement settlement) {
        BigDecimal buyTotalAmount = settlement.getBuyTotalAmount();
        boolean virtualFlg = settlement.getVirtualFlg();
        boolean tpFlg = StringUtils.equals("ZY-TP", settlement.getBusinessType());
        BigDecimal sellGuidePrice = settlement.getSellGuidePrice();
        if (virtualFlg && Objects.nonNull(sellGuidePrice) && sellGuidePrice.compareTo(BigDecimal.ZERO) > 0){
            buyTotalAmount = settlement.getDealNumber().multiply(sellGuidePrice).setScale(2, RoundingMode.HALF_UP);
        }
        return this.getVatSpreadAmount(settlement.getSellTotalAmount(), buyTotalAmount,
                settlement.getFinancialServiceAmount(), settlement.getTransportAmount(), settlement.getWarehouseAmount(), settlement.getSteveDorageAmount(),
                settlement.getInsuranceRate(), settlement.getOtherDeductionsAmount(), settlement.getDiscountAmount(), settlement.getBreachAmount(), settlement.verifyBeforeAprilFlg(), tpFlg);
    }

    /**
     * 增值税
     *
     * @param settlement 结算单
     * @param param      提成配置项
     * @return 增值税
     */
    public BigDecimal calculateVatAmount(CtrContractSettlement settlement, CtrCalCulateParam param) {
        return this.getVatAmount(settlement.getSellTotalAmount(), settlement.getBuyTotalAmount(), settlement.getFinancialServiceAmount(),
                settlement.getTransportAmount(), settlement.getWarehouseAmount(), settlement.getSteveDorageAmount(), settlement.getInsuranceRate(), param.getVatRate(),
                param.getTransportationRate(), param.getWarehouseRate(), param.getPremiumRate());
    }

    /**
     * 附加税
     *
     * @param settlement 结算单
     * @param param      提成配置项
     * @return 附加税
     */
    public BigDecimal calculateSurchargeAmount(CtrContractSettlement settlement, CtrCalCulateParam param) {
        return this.getSurchargeAmount(settlement.getVatAmount(), param.getSurchargeRate());
    }

    /**
     * 税金及附加
     *
     * @param settlement 结算单
     * @return 税金及附加
     */
    public BigDecimal calculateSurchargesAmount(CtrContractSettlement settlement) {
        return this.getTaxesSurchargesAmount(settlement.getVatAmount());
    }

    /**
     * 税后差价收入（利润）
     *
     * @param settlement 结算单
     * @return
     */
    public BigDecimal calculateAfterTaxSpreadAmount(CtrContractSettlement settlement) {
        boolean tpFlg = StringUtils.equals("ZY-TP", settlement.getBusinessType());
        return this.getAfterTaxSpreadAmount(settlement.getVatSpreadAmount(),
                settlement.getTaxesSurchargesAmount(), settlement.getPrintAmount(), settlement.getMatchBreachAmount(), tpFlg);
    }

    /**
     * 根据月度毛利润阶梯提成
     * 毛利润 = 销售总额-采购总额-仓储费-装卸费-出库费-运输费
     * V1.0
     * 当月结算合同毛利润 X≦50000，适用 29%；
     * 当月结算合同毛利润 50000<X≦80000，适用 33%；
     * 当月结算合同毛利润 X>80000，适用 35%
     *
     * @param grossProfit 毛利润
     * @return
     */
    public BigDecimal getMatchCommissionRate(BigDecimal grossProfit) {
        if (grossProfit.compareTo(new BigDecimal(50000)) <= 0) {
            return PARAM_0_29;
        } else if (grossProfit.compareTo(new BigDecimal(80000)) <= 0) {
            return PARAM_0_33;
        } else if (grossProfit.compareTo(new BigDecimal(80000)) > 0) {
            return PARAM_0_35;
        }
        return PARAM_0_29;
    }

    /**
     * 根据月度毛利润阶梯提成
     * 毛利润 = 销售总额-采购总额-仓储费-装卸费-出库费-运输费
     * V2.0
     * 当月结算合同毛利润 X≦40000，适用 22%；
     * 当月结算合同毛利润 X>40000，适用 33%；
     *
     * @param grossProfit 毛利润
     * @return
     */
    public BigDecimal getMatchCommissionRate2(BigDecimal grossProfit) {
        return grossProfit.compareTo(new BigDecimal(40000)) <= 0 ? PARAM_0_22 : PARAM_0_33;
    }

    /**
     * 根据月度毛利润阶梯提成
     * 毛利润 = 销售总额-采购总额-仓储费-装卸费-出库费-运输费
     * 改性塑料事业部
     * 当月结算合同毛利润 X<=40000，适用 10%；
     * 当月结算合同毛利润 X>40000，适用 33%
     *
     * @param grossProfit 毛利润
     * @return
     */
    public BigDecimal getMatchCommissionRateKH(BigDecimal grossProfit) {
        return grossProfit.compareTo(new BigDecimal(40000)) <= 0 ? PARAM_0_10 : PARAM_0_33;
    }

    /**
     * 保险费率：(31,45] 0.0023，(16,30] 0.0017，(8,15] 0.0012，(0,7] 0.001
     *
     * @param mapDefault
     * @param paramByCompanyGradeList
     * @return 保险费率
     */
    public ParamByCompanyGrade getParamByCompanyGrand(Map<String, Object> mapDefault, List<ParamByCompanyGrade> paramByCompanyGradeList) {
        ParamByCompanyGrade defaultParam = new ParamByCompanyGrade();
        try {
            for (ParamByCompanyGrade paramByCompanyGrade : paramByCompanyGradeList) {
                String conditionValue = paramByCompanyGrade.getCondition();
                List<ExpressionToken> expressionTokenList = ResConditionParser.getVars(conditionValue);
                Map<String, Object> param = new HashMap<>();
                expressionTokenList.forEach(t -> {
                    Variable var = t.getVariable();
                    Object varVal = ResConditionParser.getVarValue(var.getVariableName(), null, mapDefault);
                    param.put(var.getVariableName(), varVal);
                });
                if (ResConditionParser.validCondition(conditionValue, param)) {
                    logger.info("companyGrand:{},breachRate:{},serveRate:{}", mapDefault.get("companyGrade"), paramByCompanyGrade.getBreachRate(), paramByCompanyGrade.getServeRate());
                    return paramByCompanyGrade;
                }
            }
        } catch (Exception e) {
            logger.error("getParamByCompanyGrand error", e);
        }
        // 默认违约费率
        defaultParam.setBreachRate(PARAM_0_001);
        // 默认服务费费率
        defaultParam.setServeRate(PARAM_0_0003);
        return defaultParam;
    }


    /**
     * 山东能化抬头统一使用大地额度：0.001696
     * v2.0 保险费率：(60,46] 0.0019，(45,16] 0.0015，(0,15] 0.001
     * v1.0 保险费率：(31,45] 0.0023，(16,30] 0.0017，(8,15] 0.0012，(0,7] 0.001
     *
     * @param sellContract
     * @param mapDefault
     * @param insuranceRates
     * @return 保险费率
     */
    public BigDecimal getInsuranceRate(CtrContract sellContract, Map<String, Object> mapDefault, List<CtrCalCulateInsuranceParam> insuranceRates) {
        try {
            Long creditCycle = sellContract.getCreditCycle();
            String ourCompanyName = sellContract.getOurCompanyName();
            Date contractTime = sellContract.getContractTime();
            Boolean matchCreditFlg = sellContract.getMatchCreditFlg();
            if (Boolean.FALSE.equals(matchCreditFlg)) {
                return BigDecimal.ZERO;
            }
            // 大地固定保费费率
            if (StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, ourCompanyName)) {
                return PARAM_INSURANCE_0_001696;
            }
            // 中银固定保费费率
            if (StringUtils.equals(BasConstants.CREDIT_TYPE_2, sellContract.getCreditType())){
                return PARAM_INSURANCE_0_001749;
            }

            if (Objects.nonNull(contractTime) && Objects.nonNull(creditCycle) && contractTime.after(DateOperator.parse("2025-08-01"))) {
                if (creditCycle <= 15) {
                    return PARAM_INSURANCE_0_0012;
                } else if (creditCycle <= 45) {
                    return PARAM_INSURANCE_0_0020;
                } else {
                    return PARAM_INSURANCE_0_0025;
                }
            }

            if (Objects.nonNull(contractTime) && Objects.nonNull(creditCycle) && contractTime.after(DateOperator.parse("2024-07-01"))) {
                if (creditCycle <= 15) {
                    return PARAM_INSURANCE_0_001;
                } else if (creditCycle <= 45) {
                    return PARAM_INSURANCE_0_0015;
                } else {
                    return PARAM_INSURANCE_0_0019;
                }
            }

            for (CtrCalCulateInsuranceParam insuranceRate : insuranceRates) {
                String conditionValue = insuranceRate.getCondition();
                List<ExpressionToken> expressionTokenList = ResConditionParser.getVars(conditionValue);
                Map<String, Object> param = new HashMap<>();
                expressionTokenList.forEach(t -> {
                    Variable var = t.getVariable();
                    Object varVal = ResConditionParser.getVarValue(var.getVariableName(), null, mapDefault);
                    param.put(var.getVariableName(), varVal);
                });
                if (ResConditionParser.validCondition(conditionValue, param)) {
                    logger.info("creditCycle:{},insuranceRate:{}", mapDefault.get("creditCycle"), insuranceRate.getInsuranceRate());
                    return insuranceRate.getInsuranceRate();
                }
            }
        } catch (Exception e) {
            logger.error("getInsuranceRate error", e);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 金融服务费
     * 保理业务资金服务费率：万2
     * 其它业务资金服务费率根据客户等级动态取值
     * <p>
     * version2.0 金融服务费 = 本批次回款金额对应的采购-付款金额 * 资金服务费率 * 本批次回款天数
     * version1.0 金融服务费 = 采购总额 * 资金服务费率 * 天数
     *
     * @param receiveAmount 本批次回款金额
     * @param financialRate 资金服务费率
     * @param creditDays    本批次回款天数
     * @return 金融服务费
     */
    public BigDecimal getFinancialServiceAmount2(BigDecimal receiveAmount, BigDecimal financialRate, Long creditDays) {
        if (numbersIsNullOrZero(receiveAmount, financialRate, creditDays)) {
            return BigDecimal.ZERO;
        }
        return receiveAmount.multiply(financialRate).multiply(BigDecimal.valueOf(creditDays)).setScale(2, RoundingMode.HALF_UP);
    }

//    /**
//     * 金融服务费
//     * <p>
//     * 金融服务费 = 采购总额 * 资金服务费率（0.0003） * 天数
//     *
//     * @param buyTotalAmount 采购总额
//     * @param financialRate  资金服务费率
//     * @param creditCycle    账期天数
//     * @return
//     */
//    public BigDecimal getFinancialServiceAmount(BigDecimal buyTotalAmount, BigDecimal financialRate, Long creditCycle) {
//        if (numbersIsNullOrZero(buyTotalAmount, financialRate, creditCycle)) {
//            return BigDecimal.ZERO;
//        }
//        return buyTotalAmount.multiply(financialRate).multiply(BigDecimal.valueOf(creditCycle)).setScale(2, RoundingMode.HALF_UP);
//    }

    /**
     * 增值税税后差价
     * <p>
     * version3.2 增值税税后差价[托盘] = (销售额-采购额-金融服务费)/1.13-印花税-增值税*0.12
     * version3.1 增值税税后差价[库存赊销] = (销售总额-销售指导价*数量-金融服务费)/1.13 - 运费/1.09 -仓储费+装卸费）/1.06 - 销售总额*保险费率/1.06 - 其他扣除项 - 贴现费用
     * version3.0 增值税税后差价 = (销售总额-采购总额-金融服务费)/1.13-运费/1.09-（仓储费+装卸费）/1.06-销售总额*保险费率/1.06 - 其他扣除项
     * version2.0 增值税税后差价 = (销售总额-采购总额-金融服务费)/1.13-运费/1.09-（仓储费+装卸费）/1.06-逾期罚息-销售总额*保险费率/1.06 - 其他扣除项
     * version1.0 增值税税后差价 = (销售总额-采购总额-金融服务费)/1.13-运费/1.09-（仓储费+出库费+装卸费）/1.06-逾期罚息-销售总额*保险费率/1.06 - 其他扣除项
     *
     * @param sellTotalAmount        销售总额
     * @param buyTotalAmount         采购总额
     * @param financialServiceAmount 金融服务费
     * @param transportAmount        运费
     * @param warehouseAmount        仓储费
     * @param steveDorageAmount      装卸费
     * @param insuranceRate          保险费率
     * @return 增值税税后差价
     */
    private BigDecimal getVatSpreadAmount(BigDecimal sellTotalAmount, BigDecimal buyTotalAmount, BigDecimal financialServiceAmount,
                                          BigDecimal transportAmount, BigDecimal warehouseAmount, BigDecimal steveDorageAmount,
                                          BigDecimal insuranceRate, BigDecimal otherDeductionsAmount, BigDecimal discountAmount, BigDecimal breachAmount, boolean verifyAprilFlg, boolean tpFlg) {
        // (销售总额-采购总额-金融服务费)/1.13
        BigDecimal value1 = (sellTotalAmount.subtract(buyTotalAmount).subtract(financialServiceAmount)).divide(PARAM_1_13, 4, RoundingMode.HALF_UP);

        if (Boolean.TRUE.equals(tpFlg)){
            return value1.setScale(2, RoundingMode.HALF_UP);
        }

        // 运费/1.09
        BigDecimal value2 = transportAmount.divide(PARAM_1_09, 4, RoundingMode.HALF_UP);

        // （仓储费+出库费）/1.06
        BigDecimal value3 = (warehouseAmount.add(steveDorageAmount)).divide(PARAM_1_06, 4, RoundingMode.HALF_UP);

        // 销售总额*保险费率/1.06
        BigDecimal value4 = sellTotalAmount.multiply(getDefaultParam(insuranceRate)).divide(PARAM_1_06, 4, RoundingMode.HALF_UP);

        // (销售总额-采购总额-金融服务费)/1.13-运费/1.09-仓储费/1.06-销售总额*保险费率/1.06- 其他扣除项
        BigDecimal result = value1.subtract(value2).subtract(value3).subtract(value4).subtract(otherDeductionsAmount).subtract(discountAmount);
        if (Boolean.TRUE.equals(verifyAprilFlg)) {
            result = result.subtract(breachAmount);
        }
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 增值税
     * <p>
     * version5.0 增值税 = (销售总额-采购总额-金融服务费)/1.13*增值税税率-运费/1.09*运费税率-（仓储费+装卸费)/1.06*仓储费税率-销售总额*保险费率/1.06*保费税率
     * version4.0 增值税 = (销售总额-采购总额)/1.13*增值税税率-运费/1.09*运费税率-（仓储费+装卸费)/1.06*仓储费税率-销售总额*保险费率/1.06*保费税率
     * version3.0 增值税 = (销售总额-采购总额)/1.13*增值税税率-运费/1.09*运费税率-（仓储费+出库费+装卸费)/1.06*仓储费税率-销售总额*保险费率/1.06*保费税率
     * version2.0 增值税 = (销售总额-采购总额)/1.13*增值税税率-运费/1.09*运费税率-（仓储费+出库费)/1.06*仓储费税率-销售总额*保险费率/1.06*保费税率
     * version1.0 增值税 = ((销售价-采购价)/1.13*增值税税率-运费/1.09*运费税率-（仓储费+出库费）/1.06*仓储费税率-销售价*保费比率/1.06*保费税率)*数量
     *
     * @param sellTotalAmount        销售总额
     * @param buyTotalAmount         采购总额
     * @param financialServiceAmount 金融服务费
     * @param transportAmount        运费
     * @param warehouseAmount        仓储费
     * @param steveDorageAmount      装卸费
     * @param insuranceRate          保费比率
     * @param vatRate                增值税税率-0.13
     * @param transportationRate     运费税率-0.09
     * @param warehouseRate          仓储费税率-0.06
     * @param premiumRate            保费税率-0.06
     * @return 增值税
     */
    private BigDecimal getVatAmount(BigDecimal sellTotalAmount, BigDecimal buyTotalAmount, BigDecimal financialServiceAmount, BigDecimal transportAmount, BigDecimal warehouseAmount,
                                    BigDecimal steveDorageAmount, BigDecimal insuranceRate, BigDecimal vatRate, BigDecimal transportationRate,
                                    BigDecimal warehouseRate, BigDecimal premiumRate) {
        // (销售总额-采购总额-金融服务费)/1.13*增值税税率
        BigDecimal param1 = (sellTotalAmount.subtract(buyTotalAmount).subtract(financialServiceAmount)).divide(PARAM_1_13, 4, RoundingMode.HALF_UP).multiply(vatRate);

        // 运费/1.09*运费税率
        BigDecimal param2 = transportAmount.divide(PARAM_1_09, 4, RoundingMode.HALF_UP).multiply(transportationRate);

        // (仓储费+出库费+装卸费）/1.06*仓储费税率
        BigDecimal param3 = (warehouseAmount.add(steveDorageAmount)).divide(PARAM_1_06, 4, RoundingMode.HALF_UP).multiply(warehouseRate);

        // 销售价*保费比率/1.06*保费税率
        BigDecimal param4 = sellTotalAmount.multiply(getDefaultParam(insuranceRate)).divide(PARAM_1_06, 4, RoundingMode.HALF_UP).multiply(premiumRate);

        // (销售总额-采购总额)/1.13*增值税税率-运费/1.09*运费税率-（仓储费+出库费)/1.06*仓储费税率-销售总额*保险费率/1.06*保费税率
        BigDecimal result = param1.subtract(param2).subtract(param3).subtract(param4);
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 印花税
     * <p>
     * 印花税 = 销售价*数量/1.13 *印花税税率
     *
     * @param sellPrice     销售价
     * @param number        数量
     * @param stampDutyRate 印花税税率-0.0003
     * @return 印花税
     */
    private BigDecimal getPrintAmount(BigDecimal sellPrice, BigDecimal number, BigDecimal stampDutyRate) {
        if (numbersIsNullOrZero(sellPrice, number, stampDutyRate)) {
            return BigDecimal.ZERO;
        }
        // 印花税 = 销售价*数量/1.13 *印花税税率
        BigDecimal result = sellPrice.multiply(number).divide(PARAM_1_13, 4, RoundingMode.HALF_UP).multiply(stampDutyRate);
        return result.setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * 附加税
     * <p>
     * 附加税 = 增值税 * 附加税税率
     *
     * @param vatAmount     增值税
     * @param surchargeRate 附加税税率
     * @return 附加税
     */
    private BigDecimal getSurchargeAmount(BigDecimal vatAmount, BigDecimal surchargeRate) {
        if (numbersIsNullOrZero(vatAmount, surchargeRate)) {
            return BigDecimal.ZERO;
        }
        // 附加税 = 增值税 * 附加税税率
        return vatAmount.multiply(surchargeRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 税金及附加
     * <p>
     * 税金及附加 = 增值税 * 0.12
     *
     * @param vatAmount 增值税
     * @return 税金及附加
     */
    private BigDecimal getTaxesSurchargesAmount(BigDecimal vatAmount) {
        if (numberIsNullOrZero(vatAmount)) {
            return BigDecimal.ZERO;
        }
        // 税金及附加 = 增值税 * 0.12
        return vatAmount.multiply(PARAM_0_12).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 税后差价收入（利润）
     * <p>
     * v4.0 税后差价收入（利润） = 增值税税后差价 - 附加税 - 印花税 - 业务员逾期罚息
     * v3.0 税后差价收入（利润） = 增值税税后差价 - 附加税 - 印花税
     * v2.0 税后差价收入（利润） = 增值税税后差价 - 附加税 - 印花税 - 业务员逾期罚息
     * v1.0 税后差价收入（利润） = 增值税税后差价 - 附加税 - 印花税
     *
     * @param vatSpreadAmount 增值税税后差价
     * @param surchargeAmount 税金及附加
     * @param printAmount     印花税
     * @param tpFlg           托盘标识
     * @return 税后差价收入
     */
    public BigDecimal getAfterTaxSpreadAmount(BigDecimal vatSpreadAmount, BigDecimal surchargeAmount, BigDecimal printAmount, BigDecimal matchBreachAmount, boolean tpFlg) {
        // 利润 = 增值税税后差价 - 附加税 - 印花税
        if (Boolean.TRUE.equals(tpFlg)){
            return vatSpreadAmount.subtract(surchargeAmount).subtract(printAmount);
        }
        return vatSpreadAmount.subtract(surchargeAmount).subtract(printAmount).subtract(matchBreachAmount);
    }


    /**
     * 供应商资源负责人分成
     * 供应商资源负责人分成 = 利润 * 0.03
     * @param settlement
     * @return
     */
    public BigDecimal getSupplierManagerAmount(CtrContractSettlement settlement){
        // 利润
        BigDecimal afterTaxSpreadAmount = settlement.getAfterTaxSpreadAmount();

        // 若利润为NULL 或 利润小于0 或 结算类型为收逾期罚息，则采购团队负责人分成0
        if (numberIsNullOrZero(afterTaxSpreadAmount)
                || afterTaxSpreadAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (Boolean.TRUE.equals(settlement.getMarkSupplierFlag())){
            BigDecimal supplierManagerCommission = Objects.isNull(settlement.getSupplierManagerCommission()) ? PARAM_0_03 : settlement.getSupplierManagerCommission();
            settlement.setSupplierManagerCommission(supplierManagerCommission);
            return afterTaxSpreadAmount.multiply(supplierManagerCommission).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 采购团队负责人分成
     * 采购团队负责人分成 = 利润 * 0.01
     *
     * @param param      提成配置项
     * @param settlement 结算单
     * @param commission 提成明细
     * @return 采购团队负责人分成
     */
    public BigDecimal getBuyHeadTeamLeaderAmount(CtrCalCulateParam param, CtrContractSettlement settlement, CtrContractSettlementCommission commission) {
        // 利润
        BigDecimal afterTaxSpreadAmount = commission.getAfterTaxSpreadAmount();

        // 若利润为NULL 或 利润小于0 或 结算类型为收逾期罚息，则采购团队负责人分成0
        if (numberIsNullOrZero(afterTaxSpreadAmount)
                || afterTaxSpreadAmount.compareTo(BigDecimal.ZERO) <= 0
                || StringUtils.equals(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_TYPE_1, commission.getSettlementType())) {
            return BigDecimal.ZERO;
        }

        // 若该结算单属于代采，且签订日期在2024-07-01之前，采购团队负责人不参与提成，故提成金额0
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg()) && settlement.verifyBeforeJulyFlg()) {
            return BigDecimal.ZERO;
        }

        // 若该结算单属于库存采购代采，且签订日期在2024-07-01之后，采购团队负责人不参与提成，故提成金额0
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg()) && settlement.verifyAfterJulyFlg() && settlement.getVirtualFlg()) {
            return BigDecimal.ZERO;
        }

        // 取值配置项-采购团队负责人提成比例，为空则默认0.01
        BigDecimal buyHeadCommission = Objects.isNull(param.getBuyHeadCommission()) ? PARAM_0_01 : param.getBuyHeadCommission();

        // 若该结算单属于普通代采，取值配置项中的 代采采购团队负责人提成比例，为空则默认0.01
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg())) {
            buyHeadCommission = Objects.isNull(param.getBuyHeadCommissionDc()) ? PARAM_0_01 : param.getBuyHeadCommissionDc();
        }

        if (Boolean.FALSE.equals(param.getBuyCommissionFlag())){
            buyHeadCommission = BigDecimal.ZERO;
        }

        // 采购团队负责人分成 = 利润 * 采购团队负责人提成比例
        commission.setBuyHeadCommission(buyHeadCommission);
        return afterTaxSpreadAmount.multiply(buyHeadCommission).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 销售团队负责人分成
     * 销售团队负责人分成 = 利润 * 0.03
     *
     * @param param      提成配置项
     * @param settlement 结算单
     * @param commission 提成明细
     * @return 销售团队负责人分成
     */
    public BigDecimal getSaleTeamLeaderAmount(CtrCalCulateParam param, CtrContractSettlement settlement, CtrContractSettlementCommission commission) {
        // 利润
        BigDecimal afterTaxSpreadAmount = commission.getAfterTaxSpreadAmount();

        // 若利润NUll 则销售团队负责人分成为0
        if (numberIsNullOrZero(afterTaxSpreadAmount)) {
            return BigDecimal.ZERO;
        }

        // 若该结算单属于代采，且签订日期在2024-07-01之前，销售团队负责人不参与提成，故提成金额0
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg()) && settlement.verifyBeforeJulyFlg()) {
            return BigDecimal.ZERO;
        }

        // 若该结算单属于库存采购代采，且签订日期在2024-07-01之后，销售团队负责人不参与提成，故提成金额0
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg()) && settlement.verifyAfterJulyFlg() && settlement.getVirtualFlg()) {
            return BigDecimal.ZERO;
        }

        // 取值配置项-销售团队负责人提成比例，为空则默认0.03
        BigDecimal sellHeadCommission = Objects.isNull(param.getSellHeadCommission()) ? PARAM_0_03 : param.getSellHeadCommission();

        // 若该结算单属于普通代采，取值配置项中的 代采销售团队负责人提成比例，为空则默认0.03
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg())) {
            sellHeadCommission = Objects.isNull(param.getSellHeadCommissionDc()) ? PARAM_0_03 : param.getSellHeadCommissionDc();
        }

        if (Boolean.TRUE.equals(settlement.getMarkSupplierFlag())){
            sellHeadCommission = Objects.isNull(settlement.getSellHeadCommission()) ? PARAM_0_07 : settlement.getSellHeadCommission();
        }

        // 销售团队负责人分成 = 利润 * 销售团队负责人提成比例
        commission.setSellHeadCommission(sellHeadCommission);
        return afterTaxSpreadAmount.multiply(sellHeadCommission).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 销售人员分成
     * v2.0 销售人员分成 = 特户计划中配置的销售提成比例(<a href="https://www.tapd.cn/tapd_fe/my/work?dialog_preview_id=story_1157789978001001783">...</a>)
     * v1.0 销售人员分成 = 利润 * 0.29
     *
     * @param param      提成配置项
     * @param settlement 结算单
     * @param commission 提成明细
     * @return 销售人员分成
     */
    public BigDecimal getSellMatchAmount(CtrCalCulateParam param, CtrContractSettlement settlement, CtrContractSettlementCommission commission, BsCompanyConfig companyConfig) {
        // 利润
        BigDecimal afterTaxSpreadAmount = commission.getAfterTaxSpreadAmount();

        // 结算单合同吨位数量
        BigDecimal dealNumber = settlement.getDealNumber();

        // 若利润NUll 则销售业务员分成为0
        if (numberIsNullOrZero(afterTaxSpreadAmount)) {
            return BigDecimal.ZERO;
        }

        // 取值配置项-销售业务员提成比例，为空则默认0.29
        BigDecimal sellCommissionRate = Objects.isNull(param.getSellCommissionRate()) ? PARAM_0_29 : param.getSellCommissionRate();

        if (Boolean.TRUE.equals(settlement.getMarkSupplierFlag())){
            // 判断是否为标记供应商订单
            sellCommissionRate = Objects.isNull(settlement.getSellCommission()) ? PARAM_0_22 : settlement.getSellCommission();
        } else if (Boolean.FALSE.equals(settlement.getMatchCreditFlg())) {
            // 判断是否为代采结算单
            // 若为代采结算单，取值配置项中的 代采销售业务员提成比例，为空则默认0.29
            sellCommissionRate = Objects.isNull(param.getSellCommissionRateDc()) ? PARAM_0_29 : param.getSellCommissionRateDc();

            // 若该结算单签订日期在2024-07-01之前，则该代采销售业务员提示比例采用历史规则中的0.6
            if (settlement.verifyBeforeJulyFlg()) {
                sellCommissionRate = PARAM_0_6;
            }
            // 若该结算单签订日期在2024-07-01之后，且为代采库存采购业务，则该代采销售业务员提成金额为：合同数量 * 5
            if (settlement.verifyAfterJulyFlg() && settlement.getVirtualFlg()){
                return dealNumber.multiply(PARAM_5).setScale(2, RoundingMode.HALF_UP);
            }
            if (Objects.equals(KH_DEPT_ID, settlement.getDeptId()) || StringUtils.equals(KH_DEPT_NAME, settlement.getDeptName())){
                sellCommissionRate = Objects.nonNull(settlement.getSellCommission()) ? settlement.getSellCommission() : PARAM_0_10;
            }
        } else if (Objects.nonNull(companyConfig) && Objects.nonNull(companyConfig.getSellCommissionRate()) && companyConfig.getSellCommissionRate().compareTo(BigDecimal.ZERO) > 0){
            sellCommissionRate = companyConfig.getSellCommissionRate();
        }
        // 销售人员分成 = 利润 * 销售业务员提成比例
        commission.setSellCommission(sellCommissionRate);
        return afterTaxSpreadAmount.multiply(sellCommissionRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 采购人员分成
     * 采购人员分成 = 利润 * 0.08
     *
     * @param param      提成配置项
     * @param settlement 结算单
     * @param commission 提成明细
     * @return 采购人员分成
     */
    public BigDecimal getBuyMatchAmount(CtrCalCulateParam param, CtrContractSettlement settlement, CtrContractSettlementCommission commission) {
        // 利润
        BigDecimal afterTaxSpreadAmount = commission.getAfterTaxSpreadAmount();

        // 若利润为NULL 或 利润小于0 或 结算类型为收逾期罚息，则采购业务员分成0
        if (numberIsNullOrZero(afterTaxSpreadAmount)
                || afterTaxSpreadAmount.compareTo(BigDecimal.ZERO) <= 0
                || StringUtils.equals(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_TYPE_1, commission.getSettlementType())) {
            return BigDecimal.ZERO;
        }
        // 若该结算单属于代采，且签订日期在2024-07-01之前，采购业务员不参与提成，故提成金额0
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg()) && settlement.verifyBeforeJulyFlg()) {
            return BigDecimal.ZERO;
        }

        // 若该结算单属于库存采购代采，且签订日期在2024-07-01之后，采购业务员不参与提成，故提成金额0
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg()) && settlement.verifyAfterJulyFlg() && settlement.getVirtualFlg()) {
            return BigDecimal.ZERO;
        }

        // 取值配置项-采购业务员提成比例，为空则默认0.08
        BigDecimal buyCommissionRate = Objects.nonNull(param.getBuyCommissionRate()) ? PARAM_0_08 : param.getBuyCommissionRate();

        // 若该结算单属于普通代采，取值配置项中的 代采采购业务员提成比例，为空则默认0.08
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg())) {
            buyCommissionRate = Objects.isNull(param.getBuyCommissionRateDc()) ? PARAM_0_08 : param.getBuyCommissionRateDc();
        }

        // 2025年6月1日成交之后的订单,赊销业务，业务经理提成按照3%，对应采购负责人为【吴凡】
        if (Boolean.TRUE.equals(settlement.getMatchCreditFlg()) && settlement.verifyAfterJuneFlg()){
            buyCommissionRate = PARAM_0_03;
            commission.setBuyHeadUserId(BasConstants.WU_FAN_USER_ID);
        }

        if (Boolean.FALSE.equals(param.getBuyCommissionFlag())){
            buyCommissionRate = BigDecimal.ZERO;
        }

        // 采购人员分成 = 利润 * 采购业务员提成比例
        commission.setBuyCommission(buyCommissionRate);
        return afterTaxSpreadAmount.multiply(buyCommissionRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取业务员逾期罚息费用
     *
     * @param breachRate   违约金费率
     * @param breachAmount 违约金金额
     * @param param        提成计算配置项
     * @return 业务员逾期罚息费用
     */
    private BigDecimal getMatchBreachAmount(BigDecimal breachRate, BigDecimal breachAmount, CtrCalCulateParam param, Boolean verifyAprilFlg) {
        if (numberIsNullOrZero(breachRate) || numberIsNullOrZero(breachAmount) || verifyAprilFlg) {
            return BigDecimal.ZERO;
        }
        BigDecimal matchBreachRate = Objects.isNull(param.getMatchBreachRate()) ? PARAM_0_0004 : param.getMatchBreachRate();
        return breachAmount.divide(breachRate, 4, RoundingMode.HALF_UP).multiply(matchBreachRate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getDefaultParam(BigDecimal insuranceRate) {
        return Objects.isNull(insuranceRate) ? BigDecimal.ZERO : insuranceRate;
    }


    private boolean numbersIsNullOrZero(Object... numbers) {
        for (Object number : numbers) {
            if (numberIsNullOrZero(number)) {
                return true;
            }
        }
        return false;
    }

    private boolean numberIsNullOrZero(Object value) {
        if (Objects.isNull(value)) {
            return true;
        }
        if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        if (value instanceof Long && (Long) value == 0L) {
            return true;
        }
        if (value instanceof Integer && (Integer) value == 0) {
            return true;
        }
        return false;
    }
}
