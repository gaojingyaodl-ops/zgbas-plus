package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.vo.RptIndexStatisticsVo;
import com.spt.bas.report.client.vo.RptIndexStatisticsReqVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptIndexStatisticsMapper {

    /**
     * 首页风控统计
     * @param performanceStatus
     * @return
     */
    RptIndexStatisticsVo findIndexRiskStatistics(RptIndexStatisticsReqVo vo);

    /**
     * 首页风控统计-未发货
     */
    RptIndexStatisticsVo findIndexRiskStatisticsNoDeliverDoods(RptIndexStatisticsReqVo vo);

    /**
     * 财务统计  待收进项发票	合同签订、上游采购发票未收
     * @return
     */
    RptIndexStatisticsVo findBuyUnReceivedFinanceInvoice(RptIndexStatisticsReqVo vo);

    /**
     * 财务统计 - 已收待开销项发票 上游采购发票已收，下游销售发票未开
     * @return
     */
    RptIndexStatisticsVo findBuyReceivedSellUnReceivedFinanceInvoice(RptIndexStatisticsReqVo vo);

    /**
     * 财务统计 - 未收已开销项发票 上游采购发票未收，下游销售发票已开
     * @return
     */
    RptIndexStatisticsVo findBuyUnReceivedSellReceivedFinanceInvoice(RptIndexStatisticsReqVo vo);

    /**
     * 财务统计  待开销项发票	合同签订、下游发票未开
     * @return
     */
    RptIndexStatisticsVo findSealFlgSellUnReceivedFinanceInvoice(RptIndexStatisticsReqVo vo);

    /**
     * 财务统计 - 赊销应付货款 合同签订、上游采购货款未付
     * @return
     */
    RptIndexStatisticsVo findSxBuyUnReceivedFinancePay(RptIndexStatisticsReqVo vo);

    /**
     * 代采应付货款  合同签订、上游采购货款未付
     * @return
     */
    RptIndexStatisticsVo findDcBuyUnReceivedFinancePay(RptIndexStatisticsReqVo vo);

    /**
     * 未来7天应收未收的销售货款 已出库，且还未付款
     * @return
     */
    RptIndexStatisticsVo findSellUnPayFinance(RptIndexStatisticsReqVo vo);

    /**
     * 未收款统计	已付款未发货 :已付款未发货	赊销，上游已付款，下游未发货，未收款
     * @return
     */
    RptIndexStatisticsVo findBuyPaySellUnPayFinance(RptIndexStatisticsReqVo vo);

    /**
     * 未收款统计	已发货未到还款日 :已发货未到还款日	赊销，下游已发货，未到还款日，未收款
     * @return
     */
    RptIndexStatisticsVo findNotArrivedSellUnPayFinance();

    /**
     * 未收款统计	已发货已过还款日 :已发货已过还款日	赊销，下游已发货，已过还款日，未收款 
     * @return
     */
    RptIndexStatisticsVo findArrivedSellUnPayFinance();
    
    /**
     * 未收款统计	未发货未过还款日 :未发货未过还款日	赊销，下游未发货，未到还款日，未收款
     * @return
     */
    RptIndexStatisticsVo findNotUnArrivedSellUnPayFinance();

    /**
     * 未收款统计合计
     * @return
     */
    RptIndexStatisticsVo findUnPayFinanceSum();

    /**
     * 未收款统计	未发货已过还款日 :未发货已过还款日	赊销，下游未发货，已过还款日，未收款 
     * @return
     */
    RptIndexStatisticsVo findUnArrivedSellUnPayFinance();

    /**
     * 过去10周业务金额统计
     * @param vo
     * @return
     */
    RptIndexStatisticsVo findSalesWeekData(RptIndexStatisticsReqVo vo);

    /**
     * 过去10周业务金额统计(赊销、代采、自营)合计
     * @param vo
     * @return
     */
    RptIndexStatisticsVo findTotalSalesWeekData(RptIndexStatisticsReqVo vo);
    
    /**
     * 过去10月业务金额统计
     * @param vo
     * @return
     */
    RptIndexStatisticsVo findSalesMonthData(RptIndexStatisticsReqVo vo);

    /**
     * 过去10月业务金额统计(赊销、代采、自营)合计
     * @param vo
     * @return
     */
    RptIndexStatisticsVo findTotalSalesMonthData(RptIndexStatisticsReqVo vo);
    
    
    
    
    
}
