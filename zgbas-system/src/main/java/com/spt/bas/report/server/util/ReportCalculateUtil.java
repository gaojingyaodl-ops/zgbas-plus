package com.spt.bas.report.server.util;

import com.spt.bas.report.client.vo.RptCalculateInsuranceRates;
import com.spt.bas.report.client.vo.RptCreditBusinessCommission;
import com.spt.pm.util.ResConditionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.datameta.Variable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 报表计算工具类
 *
 * @Author: gaojy
 * @create 2022/2/24 16:03
 * @version: 1.0
 * @description:
 */
@Component
public class ReportCalculateUtil {
    Logger logger = LoggerFactory.getLogger(ReportCalculateUtil.class);

    private final BigDecimal PARAM_1_13 = BigDecimal.valueOf(1.13);
    private final BigDecimal PARAM_1_09 = BigDecimal.valueOf(1.09);
    private final BigDecimal PARAM_1_06 = BigDecimal.valueOf(1.06);
    private final BigDecimal PARAM_0_12 = BigDecimal.valueOf(0.12);
    private final BigDecimal PARAM_0_08 = BigDecimal.valueOf(0.08);
    private final BigDecimal PARAM_0_29 = BigDecimal.valueOf(0.29);
    private final BigDecimal PARAM_0_01 = BigDecimal.valueOf(0.01);
    private final BigDecimal PARAM_0_03 = BigDecimal.valueOf(0.03);


    /**
     * 保险费率：(31,45] 0.0023，(16,30] 0.0017，(8,15] 0.0012，(0,7] 0.001
     * @param commission
     * @param insuranceRates
     * @return
     */
    public BigDecimal getInsuranceRate(RptCreditBusinessCommission commission, List<RptCalculateInsuranceRates> insuranceRates) {
        try {
            for (RptCalculateInsuranceRates insuranceRate : insuranceRates) {
                String conditionValue = insuranceRate.getCondition();
                List<ExpressionToken> expressionTokenList = ResConditionParser.getVars(conditionValue);
                Map<String, Object> param = new HashMap<>();
                expressionTokenList.forEach(t -> {
                    Variable var = t.getVariable();
                    Object varVal = ResConditionParser.getVarValue(var.getVariableName(), commission, null);
                    param.put(var.getVariableName(), varVal);
                });
                if (ResConditionParser.validCondition(conditionValue, param)) {
                    logger.info("creditCycle:{},insuranceRate:{}", commission.getCreditCycle(), insuranceRate.getInsuranceRate());
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
     * <p>
     * 金融服务费 = 采购总额 * 资金服务费率（0.0003） * 天数
     *
     * @param buyTotalAmount 采购总额
     * @param financialRate  资金服务费率
     * @param creditCycle    账期天数
     * @return
     */
    public BigDecimal getFinancialServiceAmount(BigDecimal buyTotalAmount, BigDecimal financialRate, Long creditCycle) {
        if (numbersIsNullOrZero(buyTotalAmount, financialRate, creditCycle)) {
            return BigDecimal.ZERO;
        }
        return buyTotalAmount.multiply(financialRate).multiply(BigDecimal.valueOf(creditCycle)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 增值税税后差价
     * <p>
     * 增值税税后差价 = (销售总额-采购总额-金融服务费)/1.13-运费/1.09-（仓储费+出库费）/1.06-逾期罚息-销售总额*保险费率/1.06
     *
     * @param sellTotalAmount        销售总额
     * @param buyTotalAmount         采购总额
     * @param financialServiceAmount 金融服务费
     * @param transportAmount        运费
     * @param warehouseAmount        仓储费
     * @param breachAmount           逾期罚息
     * @param insuranceRate          保险费率
     * @return
     */
    public BigDecimal getVatSpreadAmount(BigDecimal sellTotalAmount, BigDecimal buyTotalAmount, BigDecimal financialServiceAmount,
                                          BigDecimal transportAmount, BigDecimal warehouseAmount, BigDecimal breachAmount, BigDecimal insuranceRate,BigDecimal deliveryFee) {
        if (breachAmount == null){
            breachAmount = BigDecimal.valueOf(0.00);
        }
        if (deliveryFee == null){
            deliveryFee = BigDecimal.valueOf(0.00);
        }
        // (销售总额-采购总额-金融服务费)/1.13
        BigDecimal value1 = (sellTotalAmount.subtract(buyTotalAmount).subtract(financialServiceAmount)).divide(PARAM_1_13, 4, RoundingMode.HALF_UP);

        // 运费/1.09
        BigDecimal value2 = transportAmount.divide(PARAM_1_09, 4, RoundingMode.HALF_UP);

        // （仓储费+出库费）/1.06
        BigDecimal value3 = (warehouseAmount.add(deliveryFee)).divide(PARAM_1_06, 4, RoundingMode.HALF_UP);

        // 销售总额*保险费率/1.06
        BigDecimal value4 = sellTotalAmount.multiply(insuranceRate).divide(PARAM_1_06, 4, RoundingMode.HALF_UP);

        // (销售总额-采购总额-金融服务费)/1.13-运费/1.09-仓储费/1.06-逾期罚息-销售总额*保险费率/1.06
        BigDecimal result = value1.subtract(value2).subtract(value3).subtract(breachAmount).subtract(value4);
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 增值税
     * <p>
     * 增值税 = ((销售价-采购价)/1.13*增值税税率-运费/1.09*运费税率-（仓储费+出库费）/1.06*仓储费税率-销售价*保费比率/1.06*保费税率)*数量
     *
     * @param sellPrice          销售价
     * @param buyPrice           采购价
     * @param transportAmount    运费
     * @param warehouseAmount    仓储费
     * @param insuranceRate      保费比率
     * @param number             数量
     * @param vatRate            增值税税率-0.13
     * @param transportationRate 运费税率-0.09
     * @param warehouseRate      仓储费税率-0.06
     * @param premiumRate        保费税率-0.06
     * @return
     */
    public BigDecimal getVatAmount(BigDecimal sellPrice, BigDecimal buyPrice, BigDecimal transportAmount, BigDecimal warehouseAmount,
                                    BigDecimal insuranceRate, BigDecimal number, BigDecimal vatRate, BigDecimal transportationRate,
                                    BigDecimal warehouseRate, BigDecimal premiumRate,BigDecimal deliveryFee) {
        if (deliveryFee == null){
            deliveryFee = BigDecimal.valueOf(0.00);
        }
        // (销售价-采购价)/1.13*增值税税率
        BigDecimal param1 = (sellPrice.subtract(buyPrice)).divide(PARAM_1_13, 4, RoundingMode.HALF_UP).multiply(vatRate);

        // 运费总额/1.09*运费税率*天数
        BigDecimal param2 = transportAmount.divide(PARAM_1_09, 4, RoundingMode.HALF_UP).multiply(transportationRate).divide(number,4, RoundingMode.HALF_UP);

        // (仓储费+出库费）/1.06*仓储费税率
        BigDecimal param3 = (warehouseAmount.add(deliveryFee)).divide(PARAM_1_06, 4, RoundingMode.HALF_UP).multiply(warehouseRate).divide(number,4, RoundingMode.HALF_UP);

        // 销售价*保费比率/1.06*保费税率
        BigDecimal param4 = sellPrice.multiply(insuranceRate).divide(PARAM_1_06, 4, RoundingMode.HALF_UP).multiply(premiumRate);

        // ((销售价-采购价)/1.13*增值税税率-运费/1.09*运费税率-仓储费/1.06*仓储费税率-销售价*保费比率/1.06*保费税率)*数量
        BigDecimal result = (param1.subtract(param2).subtract(param3).subtract(param4)).multiply(number);
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
     * @return
     */
    public BigDecimal getPrintAmount(BigDecimal sellPrice, BigDecimal number, BigDecimal stampDutyRate) {
        if (numbersIsNullOrZero(sellPrice, number, stampDutyRate)) {
            return BigDecimal.ZERO;
        }
        // 印花税 = 销售价*数量/1.13 *印花税税率
        BigDecimal result = sellPrice.multiply(number).divide(PARAM_1_13, 4, RoundingMode.HALF_UP).multiply(stampDutyRate);
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 附加税
     * <p>
     * 附加税 = 增值税 * 附加税税率
     *
     * @param vatAmount     增值税
     * @param surchargeRate 附加税税率
     * @return
     */
    public BigDecimal getSurchargeAmount(BigDecimal vatAmount, BigDecimal surchargeRate) {
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
     * @return
     */
    public BigDecimal getTaxesSurchargesAmount(BigDecimal vatAmount) {
        if (numberIsNullOrZero(vatAmount)) {
            return BigDecimal.ZERO;
        }
        // 税金及附加 = 增值税 * 0.12
        return vatAmount.multiply(PARAM_0_12).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 税后差价收入（利润）
     * <p>
     * 税后差价收入（利润） = 增值税税后差价 - 附加税 - 印花税
     *
     * @param vatSpreadAmount vatSpreadAmount
     * @param surchargeAmount surchargeAmount
     * @param printAmount     印花税
     * @return
     */
    public BigDecimal getAfterTaxSpreadAmount(BigDecimal vatSpreadAmount, BigDecimal surchargeAmount, BigDecimal printAmount) {
        // 利润 = 增值税税后差价 - 附加税 - 印花税
        return vatSpreadAmount.subtract(surchargeAmount).subtract(printAmount);
    }

    /**
     * 采购团队负责人分成
     * 采购团队负责人分成 = 利润 * 0.01
     * @param afterTaxSpreadAmount 利润
     * @return
     */
    public BigDecimal getBuyHeadTeamLeaderAmount(BigDecimal afterTaxSpreadAmount){
        if (numberIsNullOrZero(afterTaxSpreadAmount)) {
            return BigDecimal.ZERO;
        }
        return afterTaxSpreadAmount.multiply(PARAM_0_01).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 销售团队负责人分成
     * 销售团队负责人分成 = 利润 * 0.03
     * @param afterTaxSpreadAmount 利润
     * @return
     */
    public BigDecimal getSaleTeamLeaderAmount(BigDecimal afterTaxSpreadAmount){
        if (numberIsNullOrZero(afterTaxSpreadAmount)) {
            return BigDecimal.ZERO;
        }
        return afterTaxSpreadAmount.multiply(PARAM_0_03).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 销售人员分成
     * 销售人员分成 = 利润 * 0.29
     * @param afterTaxSpreadAmount 利润
     * @return
     */
    public BigDecimal getSellMatchAmount(BigDecimal afterTaxSpreadAmount){
        if (numberIsNullOrZero(afterTaxSpreadAmount)) {
            return BigDecimal.ZERO;
        }
        return afterTaxSpreadAmount.multiply(PARAM_0_29).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 采购人员分成
     * 采购人员分成 = 利润 * 0.08
     * @param afterTaxSpreadAmount 利润
     * @return
     */
    public BigDecimal getBuyMatchAmount(BigDecimal afterTaxSpreadAmount){
        if (numberIsNullOrZero(afterTaxSpreadAmount)) {
            return BigDecimal.ZERO;
        }
        return afterTaxSpreadAmount.multiply(PARAM_0_08).setScale(2, RoundingMode.HALF_UP);
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
