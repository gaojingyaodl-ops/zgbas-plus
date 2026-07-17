package com.spt.bas.server.filter.impl;

import cn.hutool.core.convert.Convert;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.BsConfigReqVo;
import com.spt.bas.client.vo.BsConfigRespVo;
import com.spt.bas.server.dao.ApplyMatchDao;
import com.spt.bas.server.dao.BsDictDataDao;
import com.spt.bas.server.filter.IBudgetVerifyFilter;
import com.spt.bas.server.service.*;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author MoonLight
 * @Date 2024/10/10 16:10
 * @Version 1.0
 */
@Component
public class BudgetVerifyFilterImpl implements IBudgetVerifyFilter {
    private static final Logger log = LoggerFactory.getLogger(BudgetVerifyFilterImpl.class);
    @Resource
    private BsDictDataDao bsDictDataDao;
    @Resource
    private ICtrContractService ctrContractService;
    @Resource
    private IBsCompanyService companyService;
    @Resource
    private IBsConfigService bsConfigService;
    @Resource
    private ApplyMatchDao applyMatchDao;
    @Resource
    private IBsCompanyCreditService companyCreditService;
    @Resource
    private IBsCompanyDcsxService bsCompanyDcsxService;

    /**
     * 验证预算单厂商不可为空
     *
     * @param match 预算单
     * @throws ApplicationException 异常信息
     */
    @Override
    public void verifyFactoryName(ApplyMatch match) throws ApplicationException {
        if (StringUtils.isBlank(match.getFactoryName())) {
            throw new ApplicationException("厂商不可为空!");
        }
    }

    /**
     * 预算申请控制毛利率必须大于规定比率
     *
     * @param match 预算单
     * @return 业务发起控制入参Vo
     * @throws ApplicationException 异常信息
     */
    @Override
    public BsConfigRespVo judgmentStart(ApplyMatch match, List<ApplyMatchDetail> matchDetailList, Long processId) throws ApplicationException {
        BsConfigReqVo configReqVo = new BsConfigReqVo(match.getOurCompanyName(), match.getBuyOurCompanyName(),
                match.getContractModel(), BasConstants.CONFIG_TYPE_FUND_SOURCE_OUR, match.getEnterpriseId(),
                processId, match.getBuyAmount(), match, matchDetailList);
        BsConfigRespVo bsConfigRespVo = bsConfigService.judgmentStart(configReqVo);
        if (bsConfigRespVo != null && Boolean.FALSE.equals(bsConfigRespVo.getStartFlg())) {
            throw new ApplicationException(bsConfigRespVo.getMessage());
        }
        return bsConfigRespVo;
    }

    /**
     * 预算申请控制毛利率必须大于规定比率
     *
     * @param match           预算单
     * @param matchDetailList 预算明细
     * @param processId       流程ID
     * @return 业务发起控制入参Vo
     * @throws ApplicationException 异常信息
     */
    @Override
    public BsConfigRespVo judgmentMatchStart(ApplyMatch match, List<ApplyMatchDetail> matchDetailList, Long processId) throws ApplicationException {
        BsConfigReqVo configReqVo = new BsConfigReqVo(match.getOurCompanyName(), match.getContractModel(), BasConstants.CONFIG_TYPE_FUND_SOURCE_OUR,
                match.getEnterpriseId(), processId, match.getBuyAmount(), match, matchDetailList);
        BsConfigRespVo bsConfigRespVo = bsConfigService.judgmentStart(configReqVo);
        if (bsConfigRespVo != null && Boolean.FALSE.equals(bsConfigRespVo.getStartFlg())) {
            throw new ApplicationException(bsConfigRespVo.getMessage());
        }
        return bsConfigRespVo;
    }

    /**
     * 代采毛利率不可低于规定否则不可发起
     *
     * @param match           预算单
     * @param matchDetailList 预算明细
     * @param creditFlg       是否赊销标识
     * @return 预算单
     * @throws ApplicationException 异常信息
     */
    @Override
    public void judgmentMatchProfit(ApplyMatch match, List<ApplyMatchDetail> matchDetailList, Boolean creditFlg) throws ApplicationException {
        BsConfigRespVo profitConfig = bsConfigService.judgmentMatchProfit(matchDetailList, match.getEnterpriseId(), creditFlg);
        if (Objects.nonNull(profitConfig) && Boolean.FALSE.equals(profitConfig.getStartFlg())) {
            throw new ApplicationException(profitConfig.getMessage());
        }
        match.setBusinessRestrictRelieveFlg(profitConfig.getBusinessRestrictRelieveFlg());
    }

    /**
     * 在赊销预算发起的时候做拦截：如果同一个供应商前面的采购合同还没发货，预算不让申请
     *
     * @param match 预算单
     * @throws ApplicationException 发货预警异常信息
     */
    @Override
    public void deliveryWarning(ApplyMatch match) throws ApplicationException {
        // 查询发货预警开关
        BsDictData unDeliverySwitch = bsDictDataDao.loadDictDataByCd(BasConstants.DEAL_UN_DELIVRY_PARTY, "switch", match.getEnterpriseId());

        // 启用发货预警判断
        if (Objects.nonNull(unDeliverySwitch) && Convert.toBool(unDeliverySwitch.getDictName(), false)) {
            List<CtrContract> unDeliveryList = ctrContractService.findUnDelivery(match.getBuyCompanyId());
            BsCompany buyCompany = companyService.getEntity(match.getBuyCompanyId());
            if (CollectionUtils.isNotEmpty(unDeliveryList)) {
                String contractNoMessage = unDeliveryList.stream().map(CtrContract::getContractNo).collect(Collectors.joining(BasConstants.COMMA));
                throw new ApplicationException(String.format("供应商：%s还有已付全款未发货的采购合同%s，请先发货！", buyCompany.getCompanyName(), contractNoMessage));
            }
        }
    }

    /**
     * 验证剩余授信额度是否可用
     *
     * @param match          预算单
     * @param bsConfigRespVo 业务发起控制入参Vo
     */
    @Override
    public void verifyCreditAmount(ApplyMatch match, BsConfigRespVo bsConfigRespVo) throws ApplicationException {
        // 货到付款模式 不使用授信额度
        String contractModel = match.getContractModel();
        if (StringUtils.isNotBlank(contractModel) && contractModel.contains("HDFK")){
            return;
        }

        BsCompany bsCompany = companyService.getEntity(match.getSellCompanyId());
        log.info("下游客户:{} 下游客户ID:{}", bsCompany.getCompanyName(), bsCompany.getId());
        if (Objects.isNull(bsConfigRespVo) || Objects.isNull(bsConfigRespVo.getBsConfig()) || Objects.isNull(bsConfigRespVo.getBsConfig().getCreditType())) {
            throw new ApplicationException(String.format("%s 配置项缺失!", match.getOurCompanyName()));
        }
        String creditType = bsConfigRespVo.getBsConfig().getCreditType();
        BsCompanyCredit companyCredit = companyCreditService.findByCompanyIdAndCreditTypeAndEnableFlg(bsCompany.getId(), creditType, true);
        if (Objects.isNull(companyCredit)) {
            throw new ApplicationException(String.format("%s 授信额度缺失!", bsCompany.getCompanyName()));
        }
        log.info("授信额度类别:{},授信额度ID:{}", creditType, companyCredit.getId());
        // 本次申请额度
        BigDecimal targetAmount = match.getSellAmount();

        // 风控额度
        BigDecimal riskAmount = companyCredit.getRiskAmount();

        // 授信额度
        BigDecimal creditAmount = companyCredit.getCreditAmount();

        creditAmount = Objects.nonNull(riskAmount) ? riskAmount : creditAmount;

        // 已使用额度
        BigDecimal usedCreditAmount = companyCredit.getUsedCreditAmount();

        // 临时额度
        BigDecimal temporaryAmount = companyCredit.getTemporaryAmount();

        // 审批占用额度
        BigDecimal approveCreditAmount = applyMatchDao.getApproveCreditAmount(companyCredit.getId());
        approveCreditAmount = Objects.isNull(approveCreditAmount) ? BigDecimal.ZERO : approveCreditAmount;
        BigDecimal resultCreditAmount = creditAmount.add(temporaryAmount).subtract(usedCreditAmount).subtract(approveCreditAmount).subtract(targetAmount);
        log.info("本次申请额度:{},风控额度:{} 授信额度:{}, 已使用额度:{}, 临时额度:{}, 审批占用额度:{}, 申请后剩余额度:{}", targetAmount,
                riskAmount, creditAmount, usedCreditAmount, temporaryAmount, approveCreditAmount, resultCreditAmount);
        if (resultCreditAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApplicationException(String.format("%s 授信额度不足，请联系风控!", bsCompany.getCompanyName()));
        }
        match.setCompanyCreditId(companyCredit.getId());
    }

    /**
     * 更新业务配置额度
     *
     * @param bsConfigRespVo 业务发起控制入参Vo
     * @param approve        审批单
     * @param applyMatch     预算单
     */
    @Override
    public void refreshBalance(BsConfigRespVo bsConfigRespVo, PmApprove approve, ApplyMatch applyMatch) {
        if (Objects.nonNull(bsConfigRespVo) && Objects.nonNull(bsConfigRespVo.getBsConfig())) {
            BsConfig bsConfig = bsConfigRespVo.getBsConfig();
            log.info("refreshBalance 更新业务配置可用额度 approveNo:{},bsConfigId:{},contractAmount:{}",
                    approve.getApproveNo(), bsConfig.getId(), applyMatch.getBuyAmount().negate());
            applyMatch.setBsConfigId(bsConfig.getId());
            applyMatch.setApproveNo(approve.getApproveNo());
            bsConfigService.refreshBalance(approve.getApproveNo(), bsConfig.getId(), applyMatch.getBuyAmount().negate());
        }
    }

    /**
     * 预算单驳回更新业务配置额度
     *
     * @param applyMatch 预算单
     */
    @Override
    public void rollBackBalance(PmApprove approve, ApplyMatch applyMatch) {
        if (Objects.nonNull(applyMatch.getBsConfigId())) {
            log.info("rollBackBalance 更新业务配置可用额度 approveNo:{},bsConfigId:{},contractAmount:{}",
                    approve.getApproveNo(), applyMatch.getBsConfigId(), applyMatch.getBuyAmount());
            bsConfigService.refreshBalance(approve.getApproveNo(), applyMatch.getBsConfigId(), applyMatch.getBuyAmount());
        }
    }

    /**
     * 维护合同授信类别
     *
     * @param entity
     * @param applyMatch
     */
    @Override
    public void maintainCreditType(CtrContract entity, ApplyMatch applyMatch) {
        Long companyCreditId = applyMatch.getCompanyCreditId();
        if (Objects.isNull(companyCreditId)) {
            return;
        }
        BsCompanyCredit companyCredit = companyCreditService.getEntity(companyCreditId);
        if (Objects.isNull(companyCredit)) {
            return;
        }
        entity.setCreditType(companyCredit.getCreditType());
    }

    /**
     * 链条合规判断
     *
     * @param applyMatch
     * @throws ApplicationException
     */
    @Override
    public void verifyMiningAgent(ApplyMatch applyMatch) throws ApplicationException {
        String buyOurCompanyName = applyMatch.getBuyOurCompanyName();
        String sellOurCompanyName = applyMatch.getSellOurCompanyName();

        Map<String, BsCompanyDcsx> companyMap = bsCompanyDcsxService.getCompanyConfigMap();
        BsCompanyDcsx buyCompanyConfig = companyMap.get(buyOurCompanyName);
        BsCompanyDcsx sellCompanyConfig = companyMap.get(sellOurCompanyName);

        if (Objects.isNull(buyCompanyConfig) || Objects.isNull(sellCompanyConfig)) {
            return;
        }
        Boolean buyOurCompanyFlag = buyCompanyConfig.getOurCompanyFlag();
        Boolean sellOurCompanyFlag = sellCompanyConfig.getOurCompanyFlag();
        log.info("buyOurCompanyName：{}, buyOurCompanyFlag：{}", buyOurCompanyName, buyOurCompanyFlag);
        log.info("sellOurCompanyName：{}, sellOurCompanyFlag：{}", sellOurCompanyName, sellOurCompanyFlag);

        boolean sameFlag = !StringUtils.equals(buyOurCompanyName, sellOurCompanyName);
        boolean allOurFlag = Boolean.TRUE.equals(buyOurCompanyFlag) && Boolean.TRUE.equals(sellOurCompanyFlag);
        log.info("sameFlag:{}, allOurFlag:{}", sameFlag, allOurFlag);
        if (sameFlag && allOurFlag) {
            throw new ApplicationException("代采方或中游代采方选择有误，该链条不存在!");
        }
    }
}
