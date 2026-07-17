package com.spt.bas.report.server.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.spt.bas.report.client.utils.DateUtils;
import com.spt.bas.report.client.vo.RptIndexStatisticsVo;
import com.spt.bas.report.client.vo.RptIndexStatisticsReqVo;
import com.spt.bas.report.server.dao.RptIndexStatisticsMapper;
import com.spt.bas.report.server.service.IRptIndexStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class RptIndexStatisticsServiceImpl implements IRptIndexStatisticsService {
    @Autowired
    private RptIndexStatisticsMapper indexStatisticsMapper;

    /**
     * 首页风控统计
     * @param vo
     * @return
     */
    @Override
    public List<RptIndexStatisticsVo> findIndexRiskStatistics(RptIndexStatisticsReqVo vo) {
        List<RptIndexStatisticsVo> list = new ArrayList<>();
//        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_INDEX_RISK_STATISTICS);
        List<String> statisticsTypeList = new ArrayList<>();
        statisticsTypeList.add("B");
        statisticsTypeList.add("D");
        statisticsTypeList.add("S");
        statisticsTypeList.add("P");
        for (String statisticsType:statisticsTypeList) {
            vo.setStatisticsType(statisticsType);
            RptIndexStatisticsVo homeRiskStatistics = indexStatisticsMapper.findIndexRiskStatistics(vo);
            if(StringUtils.equals("B",statisticsType)){
                homeRiskStatistics.setStatisticsTypeName("宽期限");
            }
            if(StringUtils.equals("D",statisticsType)){
                homeRiskStatistics.setStatisticsTypeName("催告期");
            }
            if(StringUtils.equals("S",statisticsType)){
                homeRiskStatistics.setStatisticsTypeName("逾期");
            }
            if(StringUtils.equals("P",statisticsType)){
                homeRiskStatistics.setStatisticsTypeName("诉讼");
            }
            list.add(homeRiskStatistics);
        }
//        if(listByCategory != null && listByCategory.size() > 0){
//            for (BsDictData dictData:listByCategory) {
//                IndexStatisticsVo homeRiskStatistics = indexStatisticsMapper.findIndexRiskStatistics(dictData.getDictCd());
//                list.add(homeRiskStatistics);
//            }
//        }
        RptIndexStatisticsVo homeRiskStatisticsNoDeliverDoods = indexStatisticsMapper.findIndexRiskStatisticsNoDeliverDoods(vo);
        homeRiskStatisticsNoDeliverDoods.setStatisticsTypeName("未发货");
        list.add(homeRiskStatisticsNoDeliverDoods);
        
        return list;
    }

    /**
     * 首页-财务统计 
     * @return
     */
    @Override
    public List<RptIndexStatisticsVo> findIndexFinanceStatistics(RptIndexStatisticsReqVo vo) {
        List<RptIndexStatisticsVo> list = new ArrayList<>();
        //待收进项发票
        RptIndexStatisticsVo financeInvoice_A = indexStatisticsMapper.findBuyUnReceivedFinanceInvoice(vo);
        list.add(financeInvoice_A);
        //待开销项发票
        RptIndexStatisticsVo financeInvoice_D = indexStatisticsMapper.findSealFlgSellUnReceivedFinanceInvoice(vo);
        list.add(financeInvoice_D);
        //已收待开销项发票
        RptIndexStatisticsVo financeInvoice_B = indexStatisticsMapper.findBuyReceivedSellUnReceivedFinanceInvoice(vo);
        list.add(financeInvoice_B);
        //未收已开销项发票
        RptIndexStatisticsVo financeInvoice_C = indexStatisticsMapper.findBuyUnReceivedSellReceivedFinanceInvoice(vo);
        list.add(financeInvoice_C);
        //赊销应付货款
        RptIndexStatisticsVo financeInvoice_E = indexStatisticsMapper.findSxBuyUnReceivedFinancePay(vo);
        list.add(financeInvoice_E);
        //代采应付货款
        RptIndexStatisticsVo financeInvoice_F = indexStatisticsMapper.findDcBuyUnReceivedFinancePay(vo);
        list.add(financeInvoice_F);
        //未收款统计	已付款未发货 :已付款未发货	赊销，上游已付款，下游未发货，未收款
        RptIndexStatisticsVo unPayStatistics_G = indexStatisticsMapper.findBuyPaySellUnPayFinance(vo);
        list.add(unPayStatistics_G);
        //应付费用合计
        
        return list;
    }

    /**
     * 首页-到期应收   未来7天应收未收的销售货款
     * @param vo
     * @return
     */
    @Override
    public List<RptIndexStatisticsVo> findSellUnPayFinance() {
        List<RptIndexStatisticsVo> list = new ArrayList<>();
        Date beginDate = DateUtils.getDayBegin();
        Date endDate = DateUtils.getDayEnd();
        int count = 7;
        RptIndexStatisticsReqVo vo = new RptIndexStatisticsReqVo();
        for (int i = 1; i <= count; i++){
            vo.setStartDate(DateUtils.getDayAfter(beginDate,i));
            vo.setEndDate(DateUtils.getDayAfter(endDate,i));
            RptIndexStatisticsVo sellUnPayFinance = indexStatisticsMapper.findSellUnPayFinance(vo);
            list.add(sellUnPayFinance);
        }
        return list;
    }

    /**
     * 首页-未收款统计
     * @return
     */
    @Override
    public List<RptIndexStatisticsVo> findUnPayStatistics() {
        List<RptIndexStatisticsVo> list = new ArrayList<>();
        //未收款统计	已发货未到还款日 :已发货未到还款日	赊销，下游已发货，未到还款日，未收款
        RptIndexStatisticsVo unPayStatistics_B = indexStatisticsMapper.findNotArrivedSellUnPayFinance();
        list.add(unPayStatistics_B);
        //未收款统计	已发货已过还款日 :已发货已过还款日	赊销，下游已发货，已过还款日，未收款 
        RptIndexStatisticsVo unPayStatistics_C = indexStatisticsMapper.findArrivedSellUnPayFinance();
        list.add(unPayStatistics_C);
        //未收款统计	未发货已过还款日 :未发货已过还款日	赊销，下游未发货，已过还款日，未收款 
        RptIndexStatisticsVo unPayStatistics_E = indexStatisticsMapper.findUnArrivedSellUnPayFinance();
        list.add(unPayStatistics_E);
        //未收款统计	未发货未过还款日 :未发货未过还款日	赊销，下游未发货，未到还款日，未收款
        RptIndexStatisticsVo unPayStatistics_D = indexStatisticsMapper.findNotUnArrivedSellUnPayFinance();
        list.add(unPayStatistics_D);
        // 合计
        RptIndexStatisticsVo unPayStatistics_Sum = indexStatisticsMapper.findUnPayFinanceSum();
        list.add(unPayStatistics_Sum);
        
        return list;
    }

    /**
     * 过去10周业务金额统计
     * @return
     */
    @Override
    public List<RptIndexStatisticsVo> findSalesTenWeekData(RptIndexStatisticsReqVo vo) {
        List<RptIndexStatisticsVo> list = new ArrayList<>();
        int count = 10;
        for (int i = 0; i < count; i++){
            Date beginDayOfLastWeek = DateUtils.getBeginDayOfLastWeek(i);
            Date endDayOfLastWeek = DateUtils.getEndDayOfLastWeek(i);
            vo.setStartDate(beginDayOfLastWeek);
            vo.setEndDate(endDayOfLastWeek);
            list.add(indexStatisticsMapper.findSalesWeekData(vo));
        }
        Collections.reverse(list);
        return list;
    }
    /**
     * 过去10周业务金额统计(赊销、代采、自营)合计
     * @return
     */
    @Override
    public List<RptIndexStatisticsVo> findTotalSalesWeekData(RptIndexStatisticsReqVo vo) {
        List<RptIndexStatisticsVo> list = new ArrayList<>();
        int count = 10;
        for (int i = 0; i < count; i++){
            Date beginDayOfLastWeek = DateUtils.getBeginDayOfLastWeek(i);
            Date endDayOfLastWeek = DateUtils.getEndDayOfLastWeek(i);
            vo.setStartDate(beginDayOfLastWeek);
            vo.setEndDate(endDayOfLastWeek);
            list.add(indexStatisticsMapper.findTotalSalesWeekData(vo));
        }
        Collections.reverse(list);
        return list;
    } 
    
    /**
     * 过去10月业务金额统计
     * @return
     */
    @Override
    public List<RptIndexStatisticsVo> findSalesTenMonthData(RptIndexStatisticsReqVo vo) {
        List<RptIndexStatisticsVo> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter patten = DateTimeFormatter.ofPattern("yyyy-MM");
        for(int i = 0 ; i < 10 ; i++){
            String month = now.plusMonths(i * (-1)).format(patten);
            vo.setMonth(month);
            list.add(indexStatisticsMapper.findSalesMonthData(vo));
        }
        Collections.reverse(list);
        return list;
    }
    /**
     * 过去10月业务金额统计(赊销、代采、自营)合计
     * @return
     */
    @Override
    public List<RptIndexStatisticsVo> findTotalSalesMonthData(RptIndexStatisticsReqVo vo) {
        List<RptIndexStatisticsVo> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter patten = DateTimeFormatter.ofPattern("yyyy-MM");
        for(int i = 0 ; i < 10 ; i++){
            String month = now.plusMonths(i * (-1)).format(patten);
            vo.setMonth(month);
            list.add(indexStatisticsMapper.findTotalSalesMonthData(vo));
        }
        Collections.reverse(list);
        return list;
    }

}
