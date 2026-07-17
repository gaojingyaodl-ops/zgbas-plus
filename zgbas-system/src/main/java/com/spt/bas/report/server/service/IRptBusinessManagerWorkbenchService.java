package com.spt.bas.report.server.service;


import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptBusinessManagerWorkbenchSearchVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 业务经理工作台service
 *
 * @author lsj
 * @version 1.0.0
 * @date 2024/12/11 16:11
 */

public interface IRptBusinessManagerWorkbenchService {

    /**
     * 个人成就
     *
     * @param searchVo
     * @return
     */
    RptPersonalAchievement findPersonalAchievement(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 过去5个月毛利润（万元）
     *
     * @param searchVo
     * @return
     */
    List<RptPersonalAchievement> findFiveMonthGrossProfitAmount(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 订单-执行（统计）
     *
     * @param searchVo
     * @return
     */
    List<RptWorkbenchContractStatist> findContractExecutionStatistList(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-执行数据详情 （待出库，待收款，待开票）
     *
     * @param searchVo
     * @return
     */
    Page<RptWorkbenchContract> findSellContractExecutionPage(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-执行数据详情 （待收票）
     *
     * @param searchVo
     * @return
     */
    Page<RptWorkbenchContract> findBuyContractExecutionPage(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 订单-应收（统计）
     *
     * @param searchVo
     * @return
     */
    List<RptWorkbenchContractStatist> findContractReceivableStatistList(RptBusinessManagerWorkbenchSearchVo searchVo);


    /**
     * 查询订单-应收数据详情
     *
     * @param searchVo
     * @return
     */
    Page<RptWorkbenchContract> findSellContractReceivablePage(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 订单应收 合计
     * @param searchVo
     * @return
     */
    RptWorkbenchContract findSellContractReceivableSum(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-审批 统计
     * @param searchVo
     * @return
     */
    Page<RptWorkbenchApproveStatist> findContractApproveStatistList(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-审批 数据详情
     * @param searchVo
     * @return
     */
    Page<RptWorkbenchApprove> findContractApprovePage(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询企业 统计
     * @param searchVo
     * @return
     */
    List<RptWorkbenchContractStatist> findCompanyStatistList(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询供应商 统计
     * @param searchVo
     * @return
     */
    List<RptWorkbenchContractStatist> findSupplierStatistList(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询企业新增 数据详情
     * @param searchVo
     * @return
     */
    Page<RptWorkbenchCompany> findNewCompanyPage(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询人保待批复 数据详情
     * @param searchVo
     * @return
     */
    Page<RptWorkbenchCompany> findPiccCompanyPage(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询活跃企业信息
     * @param searchVo
     * @return
     */
    Page<RptWorkbenchCompany> findHyCompanyPage(RptBusinessManagerWorkbenchSearchVo searchVo);

    
    
}
