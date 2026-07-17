package com.spt.bas.server.service;

import com.spt.bas.client.entity.BudgetSettlement;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.BudgetSettlementVo;
import com.spt.bas.client.vo.ParamByCompanyGrade;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

/**
 * @author sg
 */
public interface IBudgetSettlementService extends IBaseService<BudgetSettlement> {

    /**
     * 保存销售结算表
     * @param buyProduct
     * @param sellProduct
     */
    void saveSettlement(CtrProduct buyProduct, CtrProduct sellProduct);

    /**
     * 更新销售结算表
     * @param budgetSettlement
     * @param buyContract
     * @param sellContract
     */
    void updateSettlement(BudgetSettlement budgetSettlement, CtrContract buyContract, CtrContract sellContract);

    /**
     * 更新已违约结算表
     */
    void doTask() throws ApplicationException;

    /**
     * 根据合同编号更新已违约结算表
     */
    void doTaskByContractNo(String contractNo) throws ApplicationException;

    /**
     * 通过销售合同id查询
     * @param sellContractId
     * @return
     */
    BudgetSettlementVo findBySellContractId(Long sellContractId);


    BudgetSettlement findBySellContractIdWithAnyStatus(Long sellContractId);

    /**
     * BudgetSettlement
     * @param sellContractId
     * @return
     */
    BudgetSettlement getBySellContractId(Long sellContractId);

    /**
     * 单预算结算
     */
    void doSettle(BudgetSettlement settlement) throws ApplicationException;

    /**
     * 更新上下游仓储费运输费
     * @param sellContractId
     * @return
     */
    BudgetSettlement updateTransformAndWarehouse(Long sellContractId);

    /**
     * 根据企业客户等级获取服务费率、违约费率
     * @param companyId
     * @return
     */
    ParamByCompanyGrade getParamByCompanyGrade(Long companyId, String productCd);

}

