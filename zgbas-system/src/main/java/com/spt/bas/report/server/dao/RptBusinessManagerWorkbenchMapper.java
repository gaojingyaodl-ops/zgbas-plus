package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptBusinessManagerWorkbenchSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * 业务经理工作台 mapper
 */
@MyBatisDao
public interface RptBusinessManagerWorkbenchMapper {

    /**
     * 个人成就
     * @param searchVo
     * @return
     */
    List<RptPersonalAchievement> findLeaderBoardMatchUserGroupList(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 个人成就
     * @param searchVo
     * @return
     */
    RptPersonalAchievement findPersonalAchievement(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 过去5个月毛利润
     * @param searchVo
     * @return
     */
    List<RptPersonalAchievement> findFiveMonthGrossProfitAmount(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-执行数据详情（销售）
     * @param searchVo
     * @return
     */
    List<RptWorkbenchContract> findSellContractExecution(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询订单-执行数据详情（采购）
     * @param searchVo
     * @return
     */
    List<RptWorkbenchContract> findBuyContractExecution(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询订单-应收数据详情
     * @param searchVo
     * @return
     */
    List<RptWorkbenchContract> findSellContractReceivable(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 订单应收 合计
     * @param searchVo
     * @return
     */
    RptWorkbenchContract findSellContractReceivableSum(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询订单-审批（统计）
     * @param searchVo
     * @return
     */
    List<RptWorkbenchApproveStatist> findBudgetApproveStatist(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询订单-审批（双签）数据详情
     * @param searchVo
     * @return
     */
    RptWorkbenchApproveStatist findSealApproveStatist(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询订单-审批（付款）数据详情
     * @param searchVo
     * @return
     */
    RptWorkbenchApproveStatist findBuyPayApproveStatist(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询订单-审批（预算）数据详情
     * @param searchVo
     * @return
     */
    List<RptWorkbenchApprove> findContractApprove(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询合同信息
     * @param searchVo
     * @return
     */
    List<RptWorkbenchContract> findContractByApproveIds(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询供应商双签申请单
     * @param searchVo
     * @return
     */
    List<RptWorkbenchApprove> findBuySealApprove(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询客户双签申请单
     * @param searchVo
     * @return
     */
    List<RptWorkbenchApprove> findSellSealApprove(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询供应商付款申请单
     * @param searchVo
     * @return
     */
    List<RptWorkbenchApprove> findBuyPayApprove(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询企业新增
     * @param searchVo
     * @return
     */
    List<RptWorkbenchCompany> findNewCompanyList(RptBusinessManagerWorkbenchSearchVo searchVo);

    /**
     * 查询访厂报告信息
     * @param searchVo
     * @return
     */
    List<RptWorkbenchCompany> findCompanyVisitList(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询待人保批复信息
     * @param searchVo
     * @return
     */
    List<RptWorkbenchCompany> findPiccCompanyList(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询活跃企业信息
     * @param searchVo
     * @return
     */
    List<RptWorkbenchCompany> findHyCompanyList(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询活跃企业信息
     * @param searchVo
     * @return
     */
    List<RptWorkbenchCompanyCredit> findCompanyCreditList(RptBusinessManagerWorkbenchSearchVo searchVo);
    
    /**
     * 查询活跃企业合同信息
     * @param searchVo
     * @return
     */
    List<RptWorkbenchCompany> findContractInfoList(RptBusinessManagerWorkbenchSearchVo searchVo);
    
}
