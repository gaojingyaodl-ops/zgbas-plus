package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.BudgetSettlementOphisSearchVo;
import com.spt.bas.client.vo.BudgetSettlementOphisVo;
import com.spt.bas.client.vo.CtrCalCulateParam;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author laoren
 */
public interface ICtrContractSettlementService extends IBaseService<CtrContractSettlement> {

    /**
     * 保存结算表
     * @param buyProduct
     * @param sellProduct
     */
    void saveSettlement(CtrProduct buyProduct, CtrProduct sellProduct);

    CtrCalCulateParam getCalculateParamByUserId(CtrContractSettlement settlement);

    void calculateCommission(List<CtrContractSettlement> settlementList) throws InterruptedException, ExecutionException;

    void markSettlement(List<Long> settlementIds);

    void refreshSettlement(List<Long> settlementIds) throws ExecutionException, InterruptedException;

    void refreshAllSettlement() throws ExecutionException, InterruptedException;

    void finalAccount(CtrContractSettlement settlement) throws ExecutionException, InterruptedException;

    void updateSettlementOphis(BudgetSettlementOphisVo ophisVo);

    CtrContractSettlement sumPageSettlement(PageSearchVo searchVo);

    void updateSettleTotalFlg(List<Long> settlementId);

    Page<CtrContractSettlement> findIndexPage(BudgetSettlementOphisSearchVo searchVo);

    CtrContractSettlement sumIndexPage(BudgetSettlementOphisSearchVo searchVo);

    Page<CtrContractSettlement> findContractSettlementPage(Pageable page);

    Integer selectAllCount();

}

