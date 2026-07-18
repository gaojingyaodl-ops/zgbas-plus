package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptBusinessManagerWorkbenchSearchVo;
import com.spt.bas.report.server.service.IRptBusinessManagerWorkbenchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 业务经理工作台Api
 *
 * @author lsj
 * @version 1.0.0
 * @date 2024/12/11 16:04
 */

@RestController
@RequestMapping(value = "/business/manager/workbench")
public class RptBusinessManagerWorkbenchApi {

    @Autowired
    IRptBusinessManagerWorkbenchService businessManagerWorkbenchService;

    /**
     * 个人成就
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findPersonalAchievement")
    public RptPersonalAchievement findPersonalAchievement(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findPersonalAchievement(searchVo);
    }

    /**
     * 过去5个月毛利润（万元）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findFiveMonthGrossProfitAmount")
    public List<RptPersonalAchievement> findFiveMonthGrossProfitAmount(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findFiveMonthGrossProfitAmount(searchVo);
    }

    /**
     * 订单-执行（统计）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractExecutionStatistList")
    public List<RptWorkbenchContractStatist> findContractExecutionStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findContractExecutionStatistList(searchVo);
    }

    /**
     * 查询订单-执行数据详情 （待出库，待收款，待开票）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findSellContractExecutionPage")
    public Page<RptWorkbenchContract> findSellContractExecutionPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findSellContractExecutionPage(searchVo);
    }

    /**
     * 查询订单-执行数据详情 （待收票）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findBuyContractExecutionPage")
    public Page<RptWorkbenchContract> findBuyContractExecutionPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findBuyContractExecutionPage(searchVo);
    }

    /**
     * 订单-应收（统计）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractReceivableStatistList")
    public List<RptWorkbenchContractStatist> findContractReceivableStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findContractReceivableStatistList(searchVo);
    }

    /**
     * 查询订单-执行数据详情 （待收票）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findSellContractReceivablePage")
    public Page<RptWorkbenchContract> findSellContractReceivablePage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findSellContractReceivablePage(searchVo);
    }

    /**
     * 订单应收 合计
     * @param searchVo
     * @return
     */
    @PostMapping("findSellContractReceivableSum")
    public RptWorkbenchContract findSellContractReceivableSum(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo){
        return businessManagerWorkbenchService.findSellContractReceivableSum(searchVo);
    }

    /**
     * 订单-审批（统计）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractApproveStatistList")
    public Page<RptWorkbenchApproveStatist> findContractApproveStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findContractApproveStatistList(searchVo);
    }

    /**
     * 查询订单-审批数据详情
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractApprovePage")
    public Page<RptWorkbenchApprove> findContractApprovePage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findContractApprovePage(searchVo);
    }
    
    /**
     * 查询客户 统计
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findCompanyStatistList")
    public List<RptWorkbenchContractStatist> findCompanyStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findCompanyStatistList(searchVo);
    }
    
    /**
     * 查询供应商 统计
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findSupplierStatistList")
    public List<RptWorkbenchContractStatist> findSupplierStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findSupplierStatistList(searchVo);
    }
    
    /**
     * 查询新增企业 数据详情
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findNewCompanyPage")
    public Page<RptWorkbenchCompany> findNewCompanyPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findNewCompanyPage(searchVo);
    }
    
    /**
     * 查询人保待批复 数据详情
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findPiccCompanyPage")
    public Page<RptWorkbenchCompany> findPiccCompanyPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findPiccCompanyPage(searchVo);
    }
    
    /**
     * 查询活跃企业信息 数据详情
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findHyCompanyPage")
    public Page<RptWorkbenchCompany> findHyCompanyPage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchService.findHyCompanyPage(searchVo);
    }

}
