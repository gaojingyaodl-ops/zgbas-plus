package com.spt.bas.report.server.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptBusinessAccountReport;
import com.spt.bas.report.client.vo.RptBusinessSearchVo;
import com.spt.bas.report.server.dao.RptBusinessAccountMapper;
import com.spt.bas.report.server.service.IRptBusinessAccountService;
import com.spt.tools.core.date.DateOperator;
@Component
public class RptBusinessAccountServiceImpl implements IRptBusinessAccountService {
	private static final BigDecimal PARAMETER_1 = new BigDecimal(1.13);
	private static final BigDecimal PARAMETER_2 = new BigDecimal(0.13);
	private static final BigDecimal PARAMETER_3 = new BigDecimal(0.0002);
	private static final BigDecimal PARAMETER_4 = new BigDecimal(0.12);
	private static final BigDecimal PARAMETER_5 = new BigDecimal(0.0003);
	//private static final BigDecimal PARAMETER_6 = new BigDecimal(1.09);
	//private static final BigDecimal PARAMETER_7 = new BigDecimal(1.06);
	private static final BigDecimal PARAMETER_8 = new BigDecimal(365);
	private static final BigDecimal PARAMETER_9 = new BigDecimal(0.00012);
	private static final BigDecimal PARAMETER_10 = new BigDecimal(0.00027);
	@Autowired
	private RptBusinessAccountMapper businessAccountMapper;
	/**
	 * 动态业务合同表查询
	 */
	@Override
	public Page<RptBusinessAccountReport> findPage(RptBusinessSearchVo vo) {
		List<RptBusinessAccountReport> findPageList = businessAccountMapper.findPage(vo);
		for (RptBusinessAccountReport report : findPageList) {
			BigDecimal buyAmount = report.getBuyAmount();//采购总额
			BigDecimal sellAmount = report.getSellAmount();//销售总额
			BigDecimal profit = report.getProfit();//毛利
			BigDecimal dealNumber = report.getDealNumber();//数量
			Date lastPayTime = report.getLastPayTime();//付款日期
			Date lastReceiveTime = report.getLastReceiveTime();//收款日期
			BigDecimal buyTransportAmount = report.getBuyTransportAmount();//采购物流费
			BigDecimal buyWarehouseAmount = report.getBuyWarehouseAmount();//采购仓储费
			BigDecimal sellTransportAmount = report.getSellTransportAmount();//销售物流费
			BigDecimal sellWarehouseAmount = report.getSellWarehouseAmount();//销售仓储费
			BigDecimal buyTotalNumber = report.getBuyTotalNumber();//采购总数量
			BigDecimal sellTotalNumber = report.getSellTotalNumber();//销售总数量
			//Date deliveryInTime = report.getDeliveryInTime();//入库时间
			//BigDecimal warehouseNoOutNum = report.getWarehouseNoOutNum();//未出库数量
			//BigDecimal warehousePrice = report.getWarehousePrice();//仓储费单价
			//BigDecimal sellDealPrice = report.getSellDealPrice();//销售单件
			Date buyPayFullTime = report.getBuyPayFullTime();//采购付全款时间
			Date sellPayFullTime = report.getSellPayFullTime();//销售付全款时间
			//BigDecimal feeAmount = report.getFeeAmount();//出库系统仓储费
			BigDecimal notReceive = report.getNotReceive();//未收货款
			Integer orverdurDay = report.getOrverdurDay();//逾期天数
			BigDecimal sellInterestAmount = report.getSellInterestAmount();//已收罚息
			BigDecimal realTranAndWarehouseAmount = report.getRealTranAndWarehouseAmount();//实际不含税运输仓储
			BigDecimal buyInterest = report.getBuyInterest();
			BigDecimal sellInterest = report.getSellInterest();
			//明细数量所占采购数量百分比
			BigDecimal ban = dealNumber.divide(buyTotalNumber,6,BigDecimal.ROUND_HALF_EVEN);
			buyTransportAmount = buyTransportAmount.multiply(ban);
			buyWarehouseAmount = buyWarehouseAmount.multiply(ban);
			//明细数量所占销售数量百分比
			BigDecimal bane = dealNumber.divide(sellTotalNumber,6,BigDecimal.ROUND_HALF_EVEN);
			//feeAmount = feeAmount.multiply(bane);
			notReceive = notReceive.multiply(bane);
			sellInterestAmount = sellInterestAmount.multiply(bane);
			sellTransportAmount = sellTransportAmount.multiply(bane);
			sellWarehouseAmount = sellWarehouseAmount.multiply(bane);
			realTranAndWarehouseAmount = realTranAndWarehouseAmount.multiply(bane);
			if (orverdurDay == null || orverdurDay < 0) {
				report.setOrverdurDay(0);
			}
//			if (warehouseNoOutNum.compareTo(BigDecimal.ZERO) < 0) {
//				warehouseNoOutNum = BigDecimal.ZERO;
//			}
			//实际天数
			BigDecimal realBankDays = getRealBankDays(lastPayTime,lastReceiveTime);
			report.setRealContractDays(realBankDays);
			//合同天数
			BigDecimal bankDays = getBankDays(sellPayFullTime, buyPayFullTime);
			report.setContractDays(bankDays);
			realBankDays = realBankDays == null ? BigDecimal.ZERO : realBankDays;
			bankDays = bankDays == null ? BigDecimal.ZERO : bankDays;
			//在库时长
			//BigDecimal inWarehouseDays = getInWarehouseDays(deliveryInTime);
			//税费
			BigDecimal taxAmount = getTaxAmount(profit,sellAmount,buyAmount,report);
			report.setTaxAmount(taxAmount);
			//资金成本
			BigDecimal costAmount = getCostAmount(buyAmount,bankDays);
			report.setCostAmount(costAmount);
			//采购仓储运输费
			BigDecimal buyOtherWarehouseAmount = getBuyOtherWarehouseAmount(buyTransportAmount, buyWarehouseAmount, ban);
			report.setBuyOtherWarehouseAmount(buyOtherWarehouseAmount);
			//销售仓储运输费
			BigDecimal sellOtherWarehouseAmount = getSellOtherWarehouseAmount(sellTransportAmount,sellWarehouseAmount,bane);
			report.setSellOtherWarehouseAmount(sellOtherWarehouseAmount);
			//毛利
			BigDecimal grossMargin = getGrossMargin(profit,buyOtherWarehouseAmount,sellOtherWarehouseAmount);
			//净毛利
			BigDecimal margin = getMargin(profit, taxAmount, costAmount, sellOtherWarehouseAmount,
					buyOtherWarehouseAmount);
			//实际资金成本
			BigDecimal realCostAmount = getRealCostAmount(sellInterest, buyInterest, bane, ban);
			//实际净毛利
			BigDecimal realMargin = getRealMargin(profit, report.getVatAmount(), report.getExtraAmount(), report.getPrintAmount(),
					realCostAmount, realTranAndWarehouseAmount);
			//实际收益率
			BigDecimal realEarning = getRealEarning(buyAmount, realBankDays, realMargin);
			//罚息
			//BigDecimal interestAmount = getInterestAmount(notReceive, sellDealPrice, orverdurDay);
			//合同收益率
			BigDecimal earnings = getEarnings(buyAmount, bankDays, grossMargin);
			report.setMargin(margin);//净毛利
			report.setEarnings(earnings);//合同收益率
			report.setRealEarnings(realEarning);//实际合同收益率
			//report.setInterestAmount(interestAmount);//罚息
			report.setReceiveInterest(sellInterestAmount);//已收罚息
			report.setGrossMargin(grossMargin);//毛利
			report.setRealMargin(realMargin);//实际净毛利
			
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptBusinessAccountReport> page = new PageImpl<>(findPageList, pageable, vo.getCount());
		return page;
	}
	
	
	
	/**
	 *  税费=增值税+附加税+印花税
	 * @param profit 毛利
	 * @param sellAmount 含税销售收入
	 * @return
	 */
	private BigDecimal getTaxAmount(BigDecimal profit,BigDecimal sellAmount,BigDecimal buyAmount,RptBusinessAccountReport report) {
		String ourCompanyName = report.getOurCompanyName();
		String businessType = report.getBusinessType();
		BigDecimal taxAmount = BigDecimal.ZERO;
		//增值税=毛利/1.13*0.13
		BigDecimal vatAmount = profit.divide(PARAMETER_1,4, BigDecimal.ROUND_HALF_EVEN).multiply(PARAMETER_2);
		//附加税=增值税*0.12
		BigDecimal extraAmount = vatAmount.multiply(PARAMETER_4);
		if (profit.compareTo(BigDecimal.ZERO)<=0) {
			vatAmount = BigDecimal.ZERO;
			extraAmount = BigDecimal.ZERO;
		}
		report.setVatAmount(vatAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
		report.setExtraAmount(extraAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
		//印花费=含税销售收入*3/10000
		BigDecimal printAmount = BigDecimal.ZERO;
		if (businessType.indexOf(ReportConstant.DICT_TYPE_BUSINESS_ZY) >= 0
				|| businessType.indexOf(ReportConstant.DICT_TYPE_BUSINESS_SX) >= 0) {
			if (StringUtils.equals(ourCompanyName, ReportConstant.OUR_COMPANY_NAME_SHWS)) {
				BigDecimal totalAmount = sellAmount.add(buyAmount);
				printAmount = totalAmount.divide(PARAMETER_1,2,BigDecimal.ROUND_HALF_EVEN).multiply(PARAMETER_10);
			} else {
				printAmount = sellAmount.divide(PARAMETER_1,2,BigDecimal.ROUND_HALF_EVEN).multiply(PARAMETER_9);
			}
		} else {
			printAmount = sellAmount.multiply(PARAMETER_5);
		}
		report.setPrintAmount(printAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN));
		//当增值税+附加税小于0时，税费等于印花税；当大于等于0时，税费等于增值税+附加税+印花税
		if (vatAmount.add(extraAmount).compareTo(BigDecimal.ZERO) < 0) {
			taxAmount = printAmount;
		}else {
			taxAmount = vatAmount.add(extraAmount).add(printAmount);
		}
		taxAmount = taxAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return taxAmount;
	}
	
	/**
	 *  采购仓储运输费=物流费/1.09+仓储费/1.06
	 *  （采购合同预估仓储费＋采购合同预估运输费）×（成交数量÷采购合同数量）【注：最新计算公式】
	 * @param buyTransportAmount 采购运输费
	 * @param buyWarehouseAmount 采购仓储费
	 * @return
	 */
	private BigDecimal getBuyOtherWarehouseAmount(BigDecimal buyTransportAmount,BigDecimal buyWarehouseAmount,BigDecimal ban) {
		BigDecimal amount = buyTransportAmount.add(buyWarehouseAmount);
		BigDecimal buyOtherWarehouseAmount = amount.multiply(ban);
		buyOtherWarehouseAmount = buyOtherWarehouseAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return buyOtherWarehouseAmount;
	}
	
	/**
	 *  销售仓储运输费=物流费/1.09+仓储费/1.06
	 *  销售仓储费 = 已出库部分系统仓储费+未出库部分仓储费
	 *  （销售合同预估仓储费＋销售合同预估运输费）×（成交数量÷销售合同数量）【注：最新计算公式】
	 * @param inWarehouseDays 在库天数
	 * @param feeAmount	已出库数量系统仓储费
	 * @param warehousePrice 仓储费单价
	 * @param warehouseNoOutNum	未出库数量
	 * @param sellTransportAmount	销售运输费
	 * @return
	 */
	private BigDecimal getSellOtherWarehouseAmount(BigDecimal sellTransportAmount,BigDecimal sellWarehouseAmount,BigDecimal bane) {
		BigDecimal amount = sellTransportAmount.add(sellWarehouseAmount);
		BigDecimal sellOtherWarehouseAmount = amount.multiply(bane);
		sellOtherWarehouseAmount = sellOtherWarehouseAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return sellOtherWarehouseAmount;
	}
	
	/**
	 *  预估净毛利=贸易差价-税费-预估资金成本-预估采购仓储运输费-预估销售仓储运输费
	 * @param profit 贸易差价
	 * @param taxAmount 税费
	 * @param costAmount 资金成本
	 * @param sellOtherWarehouseAmount 销售运输仓储费
	 * @param buyOtherWarehouseAmount 采购运输仓储费
	 * @return
	 */
	private BigDecimal getMargin(BigDecimal profit, BigDecimal taxAmount, BigDecimal costAmount,
			BigDecimal sellOtherWarehouseAmount, BigDecimal buyOtherWarehouseAmount) {
		BigDecimal margin = profit.subtract(taxAmount).subtract(costAmount).subtract(sellOtherWarehouseAmount)
				.subtract(buyOtherWarehouseAmount);
		margin = margin.setScale(2,BigDecimal.ROUND_HALF_EVEN);
		return margin;
	}
	
	/**
	 *  资金成本=含税采购成本*（销售约定回款时间-采购约定回款时间）*0.0002
	 * @param buyAmount 含税采购成本
	 * @param bankDays	合同天数
	 * @return
	 */
	private BigDecimal getCostAmount(BigDecimal buyAmount,BigDecimal bankDays) {
		BigDecimal costAmount = buyAmount.multiply(bankDays).multiply(PARAMETER_3);
		costAmount = costAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return costAmount;
	}
	
	/**
	 *  合同收益率=净毛利/含税采购成本*365/合同天数
	 *  合同收益率＝毛利÷采购货物总额×365÷合同天数【注：最新计算公式】
	 * @param buyAmount 含税采购成本
	 * @param bankDays 合同天数
	 * @param margin 净毛利
	 * @return
	 */
	private BigDecimal getEarnings(BigDecimal buyAmount, BigDecimal bankDays, BigDecimal grossMargin) {
		BigDecimal earnings = BigDecimal.ZERO;
		// 合同收益率
		if (bankDays.compareTo(BigDecimal.ZERO) > 0) {
			earnings = grossMargin.divide(buyAmount, 4, BigDecimal.ROUND_HALF_EVEN).multiply(PARAMETER_8)
					.divide(bankDays, 4, BigDecimal.ROUND_HALF_EVEN);
		}
		return earnings;
	}
	
	/**
	 *  实际收益率=净毛利/含税采购成本*365/实际天数
	 *  实际收益率＝实际净毛利÷采购货物总额×365÷实际天数 【注：最新计算公式】
	 * @param buyAmount 含税采购成本
	 * @param realBankDays 实际天数
	 * @param margin 净毛利
	 * @return
	 */
	private BigDecimal getRealEarning(BigDecimal buyAmount,BigDecimal realBankDays,BigDecimal realMargin) {
		BigDecimal realEarnings = BigDecimal.ZERO;
		if (realBankDays.compareTo(BigDecimal.ZERO) > 0) {
			realEarnings = realMargin.divide(buyAmount, 4, BigDecimal.ROUND_HALF_EVEN).multiply(PARAMETER_8)
					.divide(realBankDays, 4, BigDecimal.ROUND_HALF_EVEN);
		}
		return realEarnings;
	}
	
	/**
	 *  罚息=未收货款/单价*10元*逾期天数
	 * @param notReceive 未收货款
	 * @param sellDealPrice 销售单价
	 * @param orverdurDay 逾期天数
	 * @return
	 */
//	private BigDecimal getInterestAmount(BigDecimal notReceive,BigDecimal sellDealPrice,Integer orverdurDay) {
//		BigDecimal interest = BigDecimal.ZERO;
//		BigDecimal orverdur = BigDecimal.ZERO;
//		if (orverdurDay != null && orverdurDay >= 0) {
//			orverdur = new BigDecimal(orverdurDay);
//		}
//		//罚息计算=未收货款/单价*10元*逾期天数
//		if (notReceive.compareTo(BigDecimal.ZERO) > 0) {
//			interest = notReceive.divide(sellDealPrice,2,BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.TEN).multiply(orverdur);
//		}
//		return interest;
//	}
	/**
	 *    在库时长 = 当前日期-入库日期
	 * @param deliveryInTime 入库日期
	 * @return
	 */
//	private BigDecimal getInWarehouseDays(Date deliveryInTime) {
//		BigDecimal inWarehouseDays = BigDecimal.ZERO;
//		if (deliveryInTime == null) {
//			return BigDecimal.ZERO;
//		}
//		Long compareDays = DateOperator.compareDays(DateOperator.truncDate(deliveryInTime), DateOperator.truncDate(new Date()));
//		if (compareDays != null) {
//			inWarehouseDays = new BigDecimal(compareDays);
//		}
//		return inWarehouseDays;
//	}
	
	/**
	 * 实际合同天数=销售实际收全款日期-采购实际付全款日期
	 * @param lastPayTime 最后一次采购付款日期（若不存在取当天）
	 * @param lastReceiveTime 最后一次销售收款日期（若不存在取当天）
	 * @return
	 */
	private BigDecimal getRealBankDays(Date lastPayTime, Date lastReceiveTime) {
		//lastPayTime = lastPayTime == null ? new Date() : lastPayTime;
		//lastReceiveTime = lastReceiveTime == null ? new Date() : lastReceiveTime;
		if (lastPayTime == null) {
			return null;
		}
		if (lastReceiveTime == null) {
			return null;
		}
		Long compareDays = DateOperator.compareDays(lastPayTime,lastReceiveTime);
		//销售回款时间-采购回款时间 不可小于0 否则为0
		//compareDays = compareDays < 0L ? 0L : compareDays;
		BigDecimal realBankDays = new BigDecimal(compareDays);
		return realBankDays;
	}
	
	/**
	 * 合同天数=销售合同收全款日期-采购合同付全款日期
	 * @param sellPayFullTime 销售合同收全款日期
	 * @param buyPayFullTime 采购合同付全款日期
	 * @return
	 */
	private BigDecimal getBankDays(Date sellPayFullTime, Date buyPayFullTime) {
		if (sellPayFullTime == null) {
			return BigDecimal.ZERO;
		}
		if (buyPayFullTime == null) {
			return BigDecimal.ZERO;
		}
		Long compareDays = DateOperator.compareDays(buyPayFullTime,sellPayFullTime);
		BigDecimal bankDays = new BigDecimal(compareDays);
		return bankDays;
	}
	
	/**
	 * 毛利＝贸易差价－预估采购仓储运输费－预估销售仓储运输费
	 * @param profit 贸易差价
	 * @param buyOtherWarehouseAmount 预估采购仓储运输费
	 * @param sellOtherWarehouseAmount 预估销售仓储运输费
	 * @return
	 */
	private BigDecimal getGrossMargin(BigDecimal profit,BigDecimal buyOtherWarehouseAmount,BigDecimal sellOtherWarehouseAmount) {
		BigDecimal grossMargin = profit.subtract(buyOtherWarehouseAmount).subtract(sellOtherWarehouseAmount);
		grossMargin = grossMargin.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return grossMargin;
	}
	
	/**
	 * 实际净毛利＝贸易差价－增值税－附加税－印花税－实际资金成本－实际不含税采购仓储运输费－实际不含税销售仓储运输费
	 * @param profig                     贸易差价
	 * @param vatAmount                  增值税
	 * @param extraAmount                附加税
	 * @param printAmount                印花税
	 * @param costAmount                 实际资金成本
	 * @param realTranAndWarehouseAmount 实际不含税采购销售仓储运输费
	 * @return
	 */
	private BigDecimal getRealMargin(BigDecimal profig, BigDecimal vatAmount, BigDecimal extraAmount,
			BigDecimal printAmount, BigDecimal realCostAmount, BigDecimal realTranAndWarehouseAmount) {
		BigDecimal realMargin = profig.subtract(vatAmount).subtract(extraAmount).subtract(printAmount)
				.subtract(realCostAmount).subtract(realTranAndWarehouseAmount);
		return realMargin;
	}
	/**
	 * 实际资金成本＝C－B
	 * 对比销售数据所在合同的最后一次收款时间和采购数据所在合同的最后一次付款时间，取大值，记A
	 * 计算销售数据应付资金利息，（A－收款时间）×当次金额×货物价值÷合同总价×0.0002，合计后记B
	 * 计算采购数据应收资金利息，（A－付款时间）×当次金额×货物价值÷合同总价×0.0002，合计后记C
	 * @param sellInterest
	 * @param buyInterest
	 * @param bane
	 * @param ban
	 * @return
	 */
	private BigDecimal getRealCostAmount(BigDecimal sellInterest,BigDecimal buyInterest,BigDecimal bane,BigDecimal ban) {
		BigDecimal realCostAmount = BigDecimal.ZERO;
		BigDecimal sell_interest = sellInterest.multiply(bane);
		BigDecimal buy_interest = buyInterest.multiply(ban);
		realCostAmount = (buy_interest.subtract(sell_interest)).multiply(PARAMETER_3);
		realCostAmount = realCostAmount.setScale(4, BigDecimal.ROUND_HALF_EVEN);
		return realCostAmount;
	}
	
	@Override
	public RptBusinessAccountReport findPageSum(RptBusinessSearchVo vo) {
		vo.setCount(-1);
		Page<RptBusinessAccountReport> findPage = findPage(vo);
		List<RptBusinessAccountReport> list = findPage.getContent();
		BigDecimal sellAmount = BigDecimal.ZERO;
		BigDecimal profit = BigDecimal.ZERO;
		BigDecimal margin = BigDecimal.ZERO;
		BigDecimal vatAmount = BigDecimal.ZERO;
		BigDecimal extraAmount = BigDecimal.ZERO;
		BigDecimal printAmount = BigDecimal.ZERO;
		BigDecimal taxAmount = BigDecimal.ZERO;
		BigDecimal costAmount = BigDecimal.ZERO;
		BigDecimal buyOtherWarehouseAmount = BigDecimal.ZERO;
		BigDecimal sellOtherWarehouseAmount = BigDecimal.ZERO;
		BigDecimal buyAmount = BigDecimal.ZERO;
		BigDecimal dealedAmount = BigDecimal.ZERO;
		BigDecimal notReceive = BigDecimal.ZERO;
		BigDecimal grossMargin = BigDecimal.ZERO;
		BigDecimal realMargin = BigDecimal.ZERO;
		for (RptBusinessAccountReport report : list) {
			BigDecimal sell_amount = report.getSellAmount();
			BigDecimal pro_fit = report.getProfit();
			BigDecimal mar_gin = report.getMargin();
			BigDecimal vat_amount = report.getVatAmount();
			BigDecimal extra_amount = report.getExtraAmount();
			BigDecimal print_amount = report.getPrintAmount();
			BigDecimal tax_amount = report.getTaxAmount();
			BigDecimal cost_amount = report.getCostAmount();
			BigDecimal buyOther_warehouseAmount = report.getBuyOtherWarehouseAmount();
			BigDecimal sellOther_warehouseAmount = report.getSellOtherWarehouseAmount();
			BigDecimal buy_amount = report.getBuyAmount();
			BigDecimal dealed_amount = report.getDealedAmount();
			BigDecimal not_receive = report.getNotReceive();
			BigDecimal gross_margin = report.getGrossMargin();
			BigDecimal real_margin = report.getRealMargin();
			sellAmount = sellAmount.add(sell_amount);
			profit = profit.add(pro_fit);
			margin = margin.add(mar_gin);
			vatAmount = vatAmount.add(vat_amount);
			extraAmount = extraAmount.add(extra_amount);
			printAmount = printAmount.add(print_amount);
			taxAmount = taxAmount.add(tax_amount);
			costAmount = costAmount.add(cost_amount);
			buyOtherWarehouseAmount = buyOtherWarehouseAmount.add(buyOther_warehouseAmount);
			sellOtherWarehouseAmount = sellOtherWarehouseAmount.add(sellOther_warehouseAmount);
			buyAmount = buyAmount.add(buy_amount);
			dealedAmount = dealedAmount.add(dealed_amount);
			notReceive = notReceive.add(not_receive);
			grossMargin = grossMargin.add(gross_margin);
			realMargin = realMargin.add(real_margin);
			
		}
		RptBusinessAccountReport total = new RptBusinessAccountReport();
		total.setSellAmount(sellAmount);
		total.setProfit(profit);
		total.setMargin(margin);
		total.setVatAmount(vatAmount);
		total.setExtraAmount(extraAmount);
		total.setPrintAmount(printAmount);
		total.setTaxAmount(taxAmount);
		total.setCostAmount(costAmount);
		total.setBuyOtherWarehouseAmount(buyOtherWarehouseAmount);
		total.setSellOtherWarehouseAmount(sellOtherWarehouseAmount);
		total.setBuyAmount(buyAmount);
		total.setDealedAmount(dealedAmount);
		total.setNotReceive(notReceive);
		total.setGrossMargin(grossMargin);
		total.setRealMargin(realMargin);
		return total;
	}
}
