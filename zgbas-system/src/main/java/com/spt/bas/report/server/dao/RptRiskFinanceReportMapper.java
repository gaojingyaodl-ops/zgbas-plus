package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.payload.FinanceStatics;
import com.spt.bas.report.client.utils.PageHelper;
import com.spt.tools.mybatis.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface RptRiskFinanceReportMapper {

    /**
     * 预算财务统计表
     * @param approveId
     * @return
     */
    List<RptMarginAmountReport> getMarginAmountList(@Param("approveIds") List<Long> approveId);

    /**
     * 预算财务统计表(单表查询)
     * @param
     * @return
     */
    List<RptMarginAmountReport> getMarginAmountListNew(FinanceStatics financeStatics);

    /**
     * 代采赊销财务统计报表
     * @param approveId
     * @return
     */
    List<RptMarginAmountReport> getMarginAmountListWithDCSX(@Param("approveIds") List<Long> approveId);


    /**
     * 预算财务统计表 合计行（footer）
     * @param approveId
     * @return
     */
    RptSumMarginAmountReport sumOfMarginAmountList(@Param("approveIds") List<Long> approveId);

    /**
     * 预算财务统计表 合计行（footer 单表查询）
     * @param
     * @return
     */
    RptSumMarginAmountReport sumOfMarginAmountListNew(FinanceStatics financeStatics);



    RptSumMarginAmountReport sumOfMarginAmountListDCSX(@Param("approveIds") List<Long> approveId);

    /**
     * 预算决算统计表
     *
     * @param approveId
     * @return
     */
    List<RptFinalAccountReport> getFinalAccountsList(@Param("approveIds") List<Long> approveId);
    
   

    /**
     * 预算决算统计表代采赊销
     * @param approveId
     * @return
     */
    List<RptFinalAccountReport> getFinalAccountsListDCSX(@Param("approveIds") List<Long> approveId);

    /**
     * 预算决算统计表 合计行（footer）
     * @return
     */
    RptSumFinalAccountReport sumOfFinalAccountList(@Param("approveIds") List<Long> approveId);

    /**
     * 预算决算统计表
     *
     * @return
     */
    List<RptFinalAccountReportNew> getFinalAccountsNewList(FinanceStatics financeStatics);
    /**
     * 预算决算统计表 合计行（footer）
     * @return
     */
    RptSumFinalAccountReportNew sumOfFinalAccountNewList(FinanceStatics financeStatics);

    /**
     * 预算决算统计表代采赊销 合计行（footer）
     * @param approveId
     * @return
     */
    RptSumFinalAccountReport sumOfFinalAccountListDCSX(@Param("approveIds") List<Long> approveId);

    List<Long> getApproveIdsWithAll(FinanceStatics financeStatics);

}
