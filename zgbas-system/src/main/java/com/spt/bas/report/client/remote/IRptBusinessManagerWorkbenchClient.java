package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.*;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 业务经理工作台 Client
 *
 * @author lsj
 * @version 1.0.0
 * @date 2024/12/11
 */
@FeignClient(name = ReportConstant.SERVER_NAME, path = ReportConstant.SERVER_NAME + "/business/manager/workbench", url = ReportConstant.SERVER_URL, configuration = FeignConfig.class)
public interface IRptBusinessManagerWorkbenchClient {

    /**
     * 个人成就
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findPersonalAchievement")
    RptPersonalAchievement findPersonalAchievement(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 过去5个月毛利润（万元）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findFiveMonthGrossProfitAmount")
    List<RptPersonalAchievement> findFiveMonthGrossProfitAmount(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 订单-执行（统计）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractExecutionStatistList")
    List<RptWorkbenchContractStatist> findContractExecutionStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-执行数据详情 （待出库，待收款，待开票）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findSellContractExecutionPage")
    PageDown<RptWorkbenchContract> findSellContractExecutionPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-执行数据详情 （待收票）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findBuyContractExecutionPage")
    PageDown<RptWorkbenchContract> findBuyContractExecutionPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 订单-应收（统计）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractReceivableStatistList")
    List<RptWorkbenchContractStatist> findContractReceivableStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-应收数据详情 （待收票）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findSellContractReceivablePage")
    PageDown<RptWorkbenchContract> findSellContractReceivablePage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 订单应收 合计
     * @param searchVo
     * @return
     */
    @PostMapping("findSellContractReceivableSum")
    RptWorkbenchContract findSellContractReceivableSum(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 订单-审批（统计）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractApproveStatistList")
    PageDown<RptWorkbenchApproveStatist> findContractApproveStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-审批数据详情
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractApprovePage")
    PageDown<RptWorkbenchApprove> findContractApprovePage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询客户 统计
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findCompanyStatistList")
    List<RptWorkbenchContractStatist> findCompanyStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询供应商 统计
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findSupplierStatistList")
    List<RptWorkbenchContractStatist> findSupplierStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-审批数据详情
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findNewCompanyPage")
    PageDown<RptWorkbenchCompany> findNewCompanyPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询人保待批复 数据详情
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findPiccCompanyPage")
    PageDown<RptWorkbenchCompany> findPiccCompanyPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询活跃企业信息 数据详情
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findHyCompanyPage")
    PageDown<RptWorkbenchCompany> findHyCompanyPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo);
    
}
