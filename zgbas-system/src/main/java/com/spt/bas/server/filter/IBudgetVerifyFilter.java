package com.spt.bas.server.filter;

import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.BsConfigRespVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;

import java.util.List;

/**
 * @Author MoonLight
 * @Date 2024/10/10 16:09
 * @Version 1.0
 */
public interface IBudgetVerifyFilter {

    /**
     * 验证预算单厂商不可为空
     *
     * @param match 预算单
     * @throws ApplicationException 异常信息
     */
    void verifyFactoryName(ApplyMatch match) throws ApplicationException;

    /**
     * 预算申请控制毛利率必须大于规定比率
     *
     * @param match           预算单
     * @param matchDetailList 预算明细
     * @param processId       流程ID
     * @return 业务发起控制入参Vo
     * @throws ApplicationException 异常信息
     */
    BsConfigRespVo judgmentStart(ApplyMatch match, List<ApplyMatchDetail> matchDetailList, Long processId) throws ApplicationException;

    /**
     * 预算申请控制毛利率必须大于规定比率
     *
     * @param match           预算单
     * @param matchDetailList 预算明细
     * @param processId       流程ID
     * @return 业务发起控制入参Vo
     * @throws ApplicationException 异常信息
     */
    BsConfigRespVo judgmentMatchStart(ApplyMatch match, List<ApplyMatchDetail> matchDetailList, Long processId) throws ApplicationException;

    /**
     * 代采毛利率不可低于规定否则不可发起
     *
     * @param match           预算单
     * @param matchDetailList 预算明细
     * @param creditFlg       是否赊销标识
     * @throws ApplicationException 异常信息
     */
    void judgmentMatchProfit(ApplyMatch match, List<ApplyMatchDetail> matchDetailList, Boolean creditFlg) throws ApplicationException;

    /**
     * 在赊销预算发起的时候做拦截：如果同一个供应商前面的采购合同还没发货，预算不让申请
     *
     * @param match 预算单
     * @throws ApplicationException 发货预警异常信息
     */
    void deliveryWarning(ApplyMatch match) throws ApplicationException;

    /**
     * 验证剩余授信额度是否可用
     *
     * @param match          预算单
     * @param bsConfigRespVo 业务发起控制入参Vo
     */
    void verifyCreditAmount(ApplyMatch match, BsConfigRespVo bsConfigRespVo) throws ApplicationException;

    /**
     * 更新业务配置额度
     *
     * @param bsConfigRespVo 业务发起控制入参Vo
     * @param approve        审批单
     * @param applyMatch     预算单
     */
    void refreshBalance(BsConfigRespVo bsConfigRespVo, PmApprove approve, ApplyMatch applyMatch);

    /**
     * 预算单驳回更新业务配置额度
     *
     * @param approve    审批单
     * @param applyMatch 预算单
     */
    void rollBackBalance(PmApprove approve, ApplyMatch applyMatch);

    /**
     * 维护合同授信类别
     * @param entity
     * @param applyMatch
     */
    void maintainCreditType(CtrContract entity, ApplyMatch applyMatch);

    /**
     * 链条合规判断
     * @param applyMatch
     * @throws ApplicationException
     */
    void verifyMiningAgent(ApplyMatch applyMatch) throws ApplicationException;
}
