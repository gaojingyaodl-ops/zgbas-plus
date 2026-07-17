package com.spt.bas.server.util;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BudgetSettlement;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 预算结算表计算工具类
 * @author shengong
 * copy from SettlementUtil
 */
@Slf4j
public class BudgetSettlementUtil {
	private static final BigDecimal PARAMETER_1 = new BigDecimal("0.0004");
	private static final BigDecimal PARAMETER_2 = new BigDecimal("0.001");
	private static final BigDecimal PARAMETER_0_0003 = new BigDecimal("0.0003");
	private static final BigDecimal PARAMETER_4 = new BigDecimal("1.13");
	private static final BigDecimal PARAMETER_5 = new BigDecimal("0.13");
	private static final BigDecimal PARAMETER_6 = new BigDecimal("1.09");
	private static final BigDecimal PARAMETER_7 = new BigDecimal("0.09");
	private static final BigDecimal PARAMETER_8 = new BigDecimal("1.06");
	private static final BigDecimal PARAMETER_9 = new BigDecimal("0.06");
	private static final BigDecimal PARAMETER_10 = new BigDecimal("0.08");
	private static final BigDecimal PARAMETER_365 = new BigDecimal("365");
	private static final BigDecimal PARAMETER_100 = new BigDecimal("100");

	private static final BigDecimal PARAMETER_1_0012 = new BigDecimal("1.0012");
	private static final BigDecimal PARAMETER_0_0012 = new BigDecimal("0.0012");
	private static final BigDecimal PARAMETER_0_0007 = new BigDecimal("0.0007");
	private static final BigDecimal PARAMETER_0_0004 = new BigDecimal("0.0004");
	private static final BigDecimal PARAMETER_0_07 = new BigDecimal("0.07");
	private static final BigDecimal PARAMETER_0_08 = new BigDecimal("0.08");
	private static final BigDecimal PARAMETER_0_1 = new BigDecimal("0.1");
	private static final BigDecimal PARAMETER_1_06 = new BigDecimal("1.06");
	private static final BigDecimal PARAMETER_1_09 = new BigDecimal("1.09");
	private static final BigDecimal PARAMETER_0_29 = new BigDecimal("0.29");
	private static final BigDecimal PARAMETER_0_54 = new BigDecimal("0.54");
	private static final BigDecimal PARAMETER_0_05 = new BigDecimal("0.05");
	private static final BigDecimal PARAMETER_0_01 = new BigDecimal("0.01");
	private static final BigDecimal PARAMETER_0_03 = new BigDecimal("0.03");
	private static final BigDecimal PARAMETER_0_41 = new BigDecimal("0.41");

	/**
	 * 逾期罚息 =销售价*数量*0.001*(实际付款日-约定结算日) ---代采&白条
	 * 逾期罚息 =销售价*数量*0.001*(实际付款日-约定结算日) ---托盘
	 * 直接取ctrContract中的breachAmount，这个算的最准确
	 *
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getBreachAmount(BudgetSettlement settlement) {
		ICtrContractService contractService = SpringContextHolder.getBean(ICtrContractService.class);
		CtrContract entity = contractService.getEntity(settlement.getSellContractId());
		BigDecimal breachAmount = entity.getBreachAmount();
		return breachAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 印花税 = 销售价*数量/1.13*0.0003
	 *
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getPrintAmount(BudgetSettlement settlement) {
		BigDecimal printAmount;
		printAmount = settlement.getSellPrice().multiply(settlement.getDealNumber()).divide(PARAMETER_4, 4, BigDecimal.ROUND_HALF_UP).multiply(PARAMETER_0_0003);
		return printAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 增值税 = ((销售价-采购价)/1.13*0.13-上下游运费单价/1.09*0.09-上下游仓储单价费1.06*0.06-销售价*保费比率/1.06*0.06)*数量
	 *
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getVatAmount(BudgetSettlement settlement) {
		BigDecimal vatAmount;
		vatAmount = (((settlement.getSellPrice().subtract(settlement.getBuyPrice()))
				.divide(PARAMETER_4, 4, BigDecimal.ROUND_HALF_UP).multiply(PARAMETER_5))
						.subtract(settlement.getTransportPrice().divide(PARAMETER_6, 4, BigDecimal.ROUND_HALF_UP)
								.multiply(PARAMETER_7))
						.subtract(settlement.getWarehousePrice().divide(PARAMETER_8, 4, BigDecimal.ROUND_HALF_UP)
								.multiply(PARAMETER_9))
						.subtract(settlement.getSellPrice().multiply(settlement.getInsuranceRate())
								.divide(PARAMETER_8, 4, BigDecimal.ROUND_HALF_UP).multiply(PARAMETER_9)))
										.multiply(settlement.getDealNumber());
		return vatAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 附加税 = 增值税 * 0.1
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getSurtax(BudgetSettlement settlement) {
		BigDecimal surtax;
		BigDecimal vatAmount = getVatAmount(settlement);
		surtax = vatAmount.multiply(PARAMETER_0_1);
		return surtax.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * (v1版本)毛利 =(销售价/1.13-采购价/1.13-运费/1.09-仓储费/1.06)*数量-逾期罚息-印花税-销售价*数量*保费比率/1.06
	 * （V2版本）毛利 = 销售合同总价 + 服务合同总价 - 采购合同总价 - 采购仓储费/1.06 - 采购运输费/1.09 - 增值税 - 附加税 - 印花税 - 保险费用（不含险销售单价*0.0012*数量）- 异常损耗
	 * （V3版本）两票制 毛利 = 销售合同总价 - 采购合同总价 - 采购仓储费/1.06 - 采购运输费/1.09 - 增值税 - 附加税 - 印花税 - 保险费用（不含险销售单价*0.0012*数量）- 异常损耗
	 *			一票制 毛利 = 销售合同总价 - 最低价*0.0003*赊销时长*数量 - 采购合同总价 - 采购仓储费/1.06 - 采购运输费/1.09 - 增值税 - 附加税 - 印花税 - 保险费用（不含险销售单价*0.0012*数量）- 异常损耗
	 *
	 *  利润（中间值）
	 *
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getMarginAmount(BudgetSettlement settlement) {
		// 损耗
		// 计入供应商部分  损耗项=对下游的退款金额-对上游的收退款金额
		// 计入物流方部分  对下游的退款金额-补偿款金额
		// 计入我方部分  对下游的退款金额
		BigDecimal lossAmount = settlement.getSellRefund().subtract(settlement.getBuyRefund()).subtract(settlement.getLogisticsRefund());
		log.info("损耗:{}", lossAmount);

		BigDecimal marginAmount;
		// 逾期罚息
		BigDecimal breachAmount = getBreachAmount(settlement);
		// 印花税
		BigDecimal printAmount = getPrintAmount(settlement);
		// 销售合同总价
		BigDecimal sellTotalAmount = settlement.getSellPrice().multiply(settlement.getDealNumber());
		// 采购销售总价
		BigDecimal buyTotalAmount = settlement.getBuyPrice().multiply(settlement.getDealNumber());
		// 服务合同总价
//		BigDecimal serveAmount = settlement.getServeAmount();
		BigDecimal serveAmount = BigDecimal.ZERO;
		// 采购仓储费/1.06
		BigDecimal buyWarehouseAmount = settlement.getBuyWarehouseAmount();
		buyWarehouseAmount = buyWarehouseAmount == null ? BigDecimal.ZERO : buyWarehouseAmount;
		buyWarehouseAmount = buyWarehouseAmount.divide(PARAMETER_1_06, 4, BigDecimal.ROUND_HALF_UP);
		// 采购运输费/1.09
		BigDecimal buyTransportAmount = settlement.getBuyTransportAmount();
		buyTransportAmount = buyTransportAmount == null ? BigDecimal.ZERO : buyTransportAmount;
		buyTransportAmount = buyTransportAmount.divide(PARAMETER_1_09, 4, BigDecimal.ROUND_HALF_UP);
		// 增值税
		BigDecimal vatAmount = getVatAmount(settlement);
		// 附加税
		BigDecimal surtax = getSurtax(settlement);

		// 保险费用 (代采没有保险)(保险成本)
		BigDecimal insuranceAmount = getInsuranceCost(settlement);

		// 异常费用（暂未使用）
		BigDecimal abnormalAmount = BigDecimal.ZERO;

		// 计算 最低价*0.0003*赊销时长*数量
		BigDecimal ser = BigDecimal.ZERO;
		BigDecimal cyc = BigDecimal.ZERO;
		BigDecimal min = BigDecimal.ZERO;
		// 一票制赊销
		if (BasConstants.SETTLEMENT_TYPE_ONE.equals(settlement.getSettlementType())) {
			try{
				cyc = new BigDecimal(Objects.isNull(settlement.getCreditCycle()) ? 0 : settlement.getCreditCycle());
				BigDecimal serveRate = Objects.isNull(settlement.getServeRate()) ? BigDecimal.ZERO : settlement.getServeRate();
				BigDecimal _rate = PARAMETER_0_0004.add(serveRate);
				min = settlement.getSellPrice()
						.divide(BigDecimal.ONE.add(_rate.multiply(cyc)),
								4, BigDecimal.ROUND_HALF_UP);

				ser = min.multiply(serveRate)
						.multiply(cyc)
						.multiply(settlement.getDealNumber());
			}catch (Exception e){
				log.error("计算 最低价*0.0003*赊销时长*数量 出错", e);
			}
		}
		marginAmount = sellTotalAmount.add(serveAmount)
				.subtract(buyTotalAmount)
				.subtract(buyWarehouseAmount)
				.subtract(buyTransportAmount)
				.subtract(vatAmount)
				.subtract(surtax)
				.subtract(printAmount)
				.subtract(insuranceAmount)
				.subtract(abnormalAmount)
				.subtract(ser)
				.subtract(lossAmount);
		log.info("marginAmount:{}", marginAmount);
		return marginAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 毛利（决算）  利润 + 实收罚金
	 *
	 * @return
	 */
	public static BigDecimal getGrossProfit(BudgetSettlement settlement) {
		BigDecimal grossProfit = BigDecimal.ZERO;
		if(settlement.getGrossProfit()==null){
			settlement.setGrossProfit(BigDecimal.ZERO);
		}
		grossProfit = settlement.getGrossProfit();
		grossProfit = grossProfit.add(getBreachAmount(settlement));
		return grossProfit.setScale(2, BigDecimal.ROUND_HALF_UP);
	}


	/**
	 * 保险费用 = 不含险销售价 * 0.0012 * 数量
	 *
	 * @return
	 */
	public static BigDecimal getInsuranceAmount(BudgetSettlement settlement) {
		BigDecimal insuranceAmount;
		// 含险
		BigDecimal sellPrice = settlement.getSellPrice();
		insuranceAmount = sellPrice
				.multiply(settlement.getInsuranceRate())
				.multiply(settlement.getDealNumber());
		return insuranceAmount.setScale(2,BigDecimal.ROUND_HALF_UP);

	}

	/**
	 * 营销留存 = 利润 * 0.05
	 *
	 * @return
	 */
	public static BigDecimal getMarketingRetention(BudgetSettlement settlement) {
		BigDecimal marketingRetention;
		BigDecimal marginAmount = getMarginAmount(settlement);
		marketingRetention = marginAmount.multiply(PARAMETER_0_05);
		return marketingRetention.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 采购提成 = 利润*0.08
	 *
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getBuyCommissionAmount(BudgetSettlement settlement) {
		BigDecimal buyCommissionAmount = BigDecimal.ZERO;
		buyCommissionAmount = settlement.getMarginAmount().multiply(PARAMETER_0_08);
		return buyCommissionAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 销售提成 = 利润*0.29
	 *
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getSellCommissionAmount(BudgetSettlement settlement) {
		BigDecimal sellCommissionAmount = BigDecimal.ZERO;
		sellCommissionAmount = settlement.getMarginAmount().multiply(PARAMETER_0_29);
		return sellCommissionAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 管理提成 = 毛利*业务提成比率*管理提成比率
	 *
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getManageCommissionAmount(BudgetSettlement settlement) {
		BigDecimal manageCommissionAmount;
		manageCommissionAmount = settlement.getMarginAmount().multiply(settlement.getBusinessCommissionRate())
				.multiply(settlement.getManageCommissionRate());
		return manageCommissionAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
	}


	/**
	 * 公司提成 = 利润 * 0.54
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getCompanyCommissionAmount(BudgetSettlement settlement) {
		BigDecimal companyCommissionAmount;
		companyCommissionAmount = settlement.getMarginAmount()
				.multiply(PARAMETER_0_54);
		return companyCommissionAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 采购团队负责人提成 = 利润 * 0，01
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getBuyDirectorCommissionAmount(BudgetSettlement settlement) {
		BigDecimal buyDirectorCommissionAmount;
		buyDirectorCommissionAmount = settlement.getMarginAmount().multiply(PARAMETER_0_01);
		return buyDirectorCommissionAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 销售团队负责人提成 = 利润 * 0，03
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getSellDirectorCommissionAmount(BudgetSettlement settlement) {
		BigDecimal sellDirectorCommissionAmount;
		sellDirectorCommissionAmount = settlement.getMarginAmount().multiply(PARAMETER_0_03);
		return sellDirectorCommissionAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 采购团队负责人提成 = 利润 * 0，01
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getBlbuyDirectorCommissionAmount(BudgetSettlement settlement) {
		BigDecimal buyDirectorCommissionAmount;
		buyDirectorCommissionAmount = settlement.getMarginAmount().multiply(PARAMETER_0_41).multiply(PARAMETER_0_01);
		return buyDirectorCommissionAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 销售团队负责人提成 = 利润 * 0，03
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getBlsellDirectorCommissionAmount(BudgetSettlement settlement) {
		BigDecimal sellDirectorCommissionAmount;
		sellDirectorCommissionAmount = settlement.getMarginAmount().multiply(PARAMETER_0_41).multiply(PARAMETER_0_03);
		return sellDirectorCommissionAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}


	/**
	 * 资金成本 = 采购合同总价*8%/365*（赊销时长 + 超期时长)
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getCapitalCost(BudgetSettlement settlement) {
		BigDecimal capitalCost;
		capitalCost = settlement.getBuyPrice()
				.multiply(settlement.getDealNumber())
				.multiply(PARAMETER_0_08)
				.divide(PARAMETER_365, 4, BigDecimal.ROUND_HALF_UP);

		Long compareDays = 0L;
		Date realPayFullTime = settlement.getRealPayFullTime();
		Date payFullTime = settlement.getPayFullTime();
		if (realPayFullTime == null) {
			realPayFullTime = new Date();
		}
		if (realPayFullTime.after(payFullTime)) {
			if (BasConstants.PROCESS_APPLY_MATCH_PALLET.equals(settlement.getProcessCode())) {
				compareDays = DateOperator.compareDays(SptDateUtils.formatterDate(settlement.getBuyPayFullTime()), SptDateUtils.formatterDate(realPayFullTime));
			}else {
				compareDays = DateOperator.compareDays(SptDateUtils.formatterDate(payFullTime), SptDateUtils.formatterDate(realPayFullTime));
			}
		}
		BigDecimal creditCycle = BigDecimal.ZERO;
		capitalCost = capitalCost.multiply(creditCycle.add(new BigDecimal(compareDays)));
		return capitalCost.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 保险成本 （不含险销售单价 * 0.0012 * 数量）
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getInsuranceCost(BudgetSettlement settlement) {
		// 保险费用 (代采没有保险)
		BigDecimal insuranceAmount = BigDecimal.ZERO;
		if (settlement.getSettlementType() != null) {
			insuranceAmount = getInsuranceAmount(settlement);
		}
		return insuranceAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}


	/**
	 * 毛利率 = 毛利/销售总价*100
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getGrossProfitRate(BudgetSettlement settlement) {
		BigDecimal grossProfitRate;
		grossProfitRate = settlement.getGrossProfit().divide(settlement.getBuyPrice().multiply(settlement.getDealNumber()), 4, BigDecimal.ROUND_HALF_UP).multiply(PARAMETER_100);
		settlement.setGrossProfitRate(grossProfitRate);
		return grossProfitRate.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 利润率 = 利润/销售总价*100
	 * @param settlement
	 * @return
	 */
	public static BigDecimal getMarginRate(BudgetSettlement settlement) {
		BigDecimal grossProfitRate;
		grossProfitRate = settlement.getMarginAmount().divide(settlement.getBuyPrice().multiply(settlement.getDealNumber()), 4, BigDecimal.ROUND_HALF_UP).multiply(PARAMETER_100);
		settlement.setGrossProfitRate(grossProfitRate);
		return grossProfitRate.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
}
