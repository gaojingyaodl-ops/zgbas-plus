//package com.spt.bas.server.util;
///**
// * 销售结算表计算工具类
// *
// */
//
//import java.math.BigDecimal;
//import java.util.Date;
//
//import com.spt.bas.client.entity.CtrContractSettlement;
//import com.spt.tools.core.date.DateOperator;
//
//public class SettlementUtil {
//	private static final BigDecimal PARAMETER_1 = new BigDecimal("0.0004");
//	private static final BigDecimal PARAMETER_2 = new BigDecimal("0.001");
//	private static final BigDecimal PARAMETER_3 = new BigDecimal("0.0003");
//	private static final BigDecimal PARAMETER_4 = new BigDecimal("1.13");
//	private static final BigDecimal PARAMETER_5 = new BigDecimal("0.13");
//	private static final BigDecimal PARAMETER_6 = new BigDecimal("1.09");
//	private static final BigDecimal PARAMETER_7 = new BigDecimal("0.09");
//	private static final BigDecimal PARAMETER_8 = new BigDecimal("1.06");
//	private static final BigDecimal PARAMETER_9 = new BigDecimal("0.06");
//
//	/**
//	 * 销售价 =(采购价+运费+仓储费+(约定结算日-送货日)*采购价*0.0004+加价)*(1+保费比率)
//	 *
//	 * @param settlement
//	 * @return
//	 */
//	public static BigDecimal getSettlementSellPrice(CtrContractSettlement settlement) {
//		BigDecimal settlementSellPrice = BigDecimal.ZERO;
//		Long compareDays = DateOperator.compareDays(settlement.getDeliveryTime(), settlement.getPayFullTime());
//		settlementSellPrice = (new BigDecimal(compareDays).multiply(settlement.getBuyPrice()).multiply(PARAMETER_1)
//				.add(settlement.getBuyPrice()).add(settlement.getTransportPrice()).add(settlement.getWarehousePrice()))
//						.multiply((BigDecimal.ONE.add(settlement.getInsuranceRate())));
//		return settlementSellPrice.setScale(2,BigDecimal.ROUND_HALF_UP);
//	}
//
//	/**
//	 * 逾期罚息 =销售价*数量*0.001*(实际付款日-约定结算日)
//	 *
//	 * @param settlement
//	 * @return
//	 */
//	public static BigDecimal getBreachAmount(CtrContractSettlement settlement) {
//		BigDecimal breachAmount = BigDecimal.ZERO;
//		Date realPayFullTime = settlement.getRealPayFullTime();
//		Date payFullTime = settlement.getPayFullTime();
//		if (realPayFullTime == null) {
//			realPayFullTime = new Date();
//		}
//		if (realPayFullTime.after(payFullTime)) {
//			Long compareDays = DateOperator.compareDays(SptDateUtils.formatterDate(payFullTime), SptDateUtils.formatterDate(realPayFullTime));
//			breachAmount = settlement.getSellPrice().multiply(settlement.getDealNumber()).multiply(PARAMETER_2)
//					.multiply(new BigDecimal(compareDays));
//		}
//		return breachAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
//	}
//
//	/**
//	 * 印花税 = 销售价*数量*0.0003
//	 *
//	 * @param settlement
//	 * @return
//	 */
//	public static BigDecimal getPrintAmount(CtrContractSettlement settlement) {
//		BigDecimal printAmount = BigDecimal.ZERO;
//		printAmount = settlement.getSellPrice().multiply(settlement.getDealNumber()).multiply(PARAMETER_3);
//		return printAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
//	}
//
//	/**
//	 * 增值税 = ((销售价-采购价)/1.13*0.13-运费/1.09*0.09-仓储费/1.06*0.06-销售价*保费比率/1.06*0.06)*数量
//	 *
//	 * @param settlement
//	 * @return
//	 */
//	public static BigDecimal getVatAmount(CtrContractSettlement settlement) {
//		BigDecimal vatAmount = BigDecimal.ZERO;
//		vatAmount = (((settlement.getSellPrice().subtract(settlement.getBuyPrice()))
//				.divide(PARAMETER_4, 4, BigDecimal.ROUND_HALF_UP).multiply(PARAMETER_5))
//						.subtract(settlement.getTransportPrice().divide(PARAMETER_6, 4, BigDecimal.ROUND_HALF_UP)
//								.multiply(PARAMETER_7))
//						.subtract(settlement.getWarehousePrice().divide(PARAMETER_8, 4, BigDecimal.ROUND_HALF_UP)
//								.multiply(PARAMETER_9))
//						.subtract(settlement.getSellPrice().multiply(settlement.getInsuranceRate())
//								.divide(PARAMETER_8, 4, BigDecimal.ROUND_HALF_UP).multiply(PARAMETER_9)))
//										.multiply(settlement.getDealNumber());
//		return vatAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
//	}
//
//	/**
//	 * 毛利 =(销售价/1.13-采购价/1.13-运费/1.09-仓储费/1.06)*数量-逾期罚息-印花税-销售价*数量*保费比率/1.06
//	 *
//	 * @param settlement
//	 * @return
//	 */
//	public static BigDecimal getMarginAmount(CtrContractSettlement settlement) {
//		BigDecimal marginAmount = BigDecimal.ZERO;
//		BigDecimal breachAmount = getBreachAmount(settlement);
//		BigDecimal printAmount = getPrintAmount(settlement);
//		marginAmount = ((settlement.getSellPrice().divide(PARAMETER_4, 4, BigDecimal.ROUND_HALF_UP)
//				.subtract(settlement.getBuyPrice().divide(PARAMETER_4, 4, BigDecimal.ROUND_HALF_UP))
//				.subtract(settlement.getTransportPrice().divide(PARAMETER_6, 4, BigDecimal.ROUND_HALF_UP))
//				.subtract(settlement.getWarehousePrice().divide(PARAMETER_8, 4, BigDecimal.ROUND_HALF_UP))))
//						.multiply(settlement.getDealNumber()).subtract(breachAmount).subtract(printAmount)
//						.subtract(settlement.getSellPrice().multiply(settlement.getDealNumber().multiply(
//								settlement.getInsuranceRate().divide(PARAMETER_8, 4, BigDecimal.ROUND_HALF_UP))));
//
//		return marginAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
//	}
//
//	/**
//	 * 采购提成 = 毛利*业务提成比率*采购提成比率
//	 *
//	 * @param settlement
//	 * @return
//	 */
//	public static BigDecimal getBuyCommissionAmount(CtrContractSettlement settlement) {
//		BigDecimal buyCommissionAmount = BigDecimal.ZERO;
//		buyCommissionAmount = settlement.getMarginAmount().multiply(settlement.getBusinessCommissionRate())
//				.multiply(settlement.getBuyCommissionRate());
//		return buyCommissionAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
//	}
//
//	/**
//	 * 销售提成 = 毛利*业务提成比率*销售提成比率
//	 *
//	 * @param settlement
//	 * @return
//	 */
//	public static BigDecimal getSellCommissionAmount(CtrContractSettlement settlement) {
//		BigDecimal sellCommissionAmount = BigDecimal.ZERO;
//		sellCommissionAmount = settlement.getMarginAmount().multiply(settlement.getBusinessCommissionRate())
//				.multiply(settlement.getSellCommissionRate());
//		return sellCommissionAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
//	}
//
//	/**
//	 * 管理提成 = 毛利*业务提成比率*管理提成比率
//	 *
//	 * @param settlement
//	 * @return
//	 */
//	public static BigDecimal getManageCommissionAmount(CtrContractSettlement settlement) {
//		BigDecimal manageCommissionAmount = BigDecimal.ZERO;
//		manageCommissionAmount = settlement.getMarginAmount().multiply(settlement.getBusinessCommissionRate())
//				.multiply(settlement.getManageCommissionRate());
//		return manageCommissionAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
//	}
//
//	/**
//	 * 服务费 = 毛利*服务费率
//	 *
//	 * @param settlement
//	 * @return
//	 */
//	public static BigDecimal getServeAmount(CtrContractSettlement settlement) {
//		BigDecimal serveAmount = BigDecimal.ZERO;
//		BigDecimal marginAmount = getMarginAmount(settlement);
//		serveAmount = marginAmount.multiply(settlement.getServeRate());
//		return serveAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
//	}
//}
