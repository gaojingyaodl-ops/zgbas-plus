package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.CtrContractProfit;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 风控合同利润统计表逻辑处理类
 *
 * @author MoonLight
 */
public interface ICtrContractProfitService extends IBaseService<CtrContractProfit> {

    /**
     * 初始化保存合同利润统计数据
     *
     * @param approve
     * @param applyMatch
     */
    void initContractProfit(PmApprove approve, ApplyMatch applyMatch);

    /**
     * 历史数据处理入库
     */
    void initHistoryProfit() throws InterruptedException, ExecutionException;
    List<CtrContractProfit> findByApproveIdAndProfitTypeAndLevel(Long approId, String profitType, Long level);

    List<CtrContractProfit> findByAndApproveId(Long approId);

    CtrContractProfit findBySellContractNo(String  contractNo);

    CtrContractProfit findByBuyContractNo(String  ContractNo);


    /**
     * 刷新利润统计数据
     */
    void refreshProfitData(String approveNo) throws Exception;

    /**
     * 刷新利润统计数据,多个审批编号
     */
    void refreshProfitData(List<String>approveList) throws Exception;
}
