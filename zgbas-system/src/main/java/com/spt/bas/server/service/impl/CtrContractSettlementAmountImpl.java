package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.CtrCalCulateParam;
import com.spt.bas.server.dao.ApplyReceiveDao;
import com.spt.bas.server.dao.CtrContractSettlementAmountDao;
import com.spt.bas.server.dao.CtrContractSettlementCommissionDao;
import com.spt.bas.server.dao.CtrContractSettlementDao;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.ICtrContractSettlementAmountService;
import com.spt.bas.server.service.ICtrContractSettlementCommissionService;
import com.spt.bas.server.service.ICtrContractSettlementService;
import com.spt.bas.server.util.CommissionCalculateUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 合同结算金额表
 *
 * @author MoonLight
 */
@Component
public class CtrContractSettlementAmountImpl extends BaseService<CtrContractSettlementAmount> implements ICtrContractSettlementAmountService {
    @Autowired
    private CtrContractSettlementAmountDao settlementAmountDao;
    @Autowired
    private CtrContractSettlementDao settlementDao;
    @Autowired
    private CtrContractSettlementCommissionDao commissionDao;
    @Autowired
    private ICtrContractSettlementCommissionService commissionService;
    @Autowired
    private ICtrContractSettlementService ctrContractSettlementService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private ApplyReceiveDao applyReceiveDao;
    @Resource
    private CommissionCalculateUtil calculateUtil;

    @Override
    public BaseDao<CtrContractSettlementAmount> getBaseDao() {
        return settlementAmountDao;
    }

    /**
     * 初始化保存结算金额数据-货款
     *
     * @param settlement
     */
    @Override
    @ServiceTransactional
    public void initSaveSettlementAmount(CtrContractSettlement settlement, CtrCalCulateParam param) {
        List<CtrContractSettlementAmount> resultList = settlementAmountDao.findBySettlementIdAndSettlementType(settlement.getId(),
                BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_TYPE_0);
        CtrContractSettlementAmount entity = new CtrContractSettlementAmount();
        BigDecimal calculateAmount = settlement.verifyBeforeAprilFlg() ? calculateDeductionBreachAmount(settlement) : settlement.getAfterTaxSpreadAmount();
        if (CollectionUtils.isNotEmpty(resultList)) {
            entity = resultList.get(0);
        } else {
            entity.setId(0L);
            entity.setSettlementId(settlement.getId());
            entity.setContractId(settlement.getSellContractId());
            entity.setContractNo(settlement.getSellContractNo());
            entity.setSettlementStatus(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_STATUS_0);
            entity.setSettlementType(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_TYPE_0);
        }
        entity.setSettlementAmount(calculateAmount);
        // 更新保存合同结算金额数据
        entity = settlementAmountDao.save(entity);
        // 更新保存合同结算提成数据
        CtrContractSettlementCommission commission = commissionService.initSaveSettlementCommission(settlement, entity, param);
        settlement.setSellCommission(commission.getSellCommission());
        settlement.setBuyCommission(commission.getBuyCommission());
        settlement.setSellHeadCommission(commission.getSellHeadCommission());
        settlement.setBuyHeadCommission(commission.getBuyHeadCommission());
        // 供应商资源负责人分成
        settlement.setSupplierManagerAmount(calculateUtil.getSupplierManagerAmount(settlement));
    }

    /**
     * 抵扣逾期罚息判断
     */
    private BigDecimal calculateDeductionBreachAmount(CtrContractSettlement settlement) {
        // 逾期罚息
        BigDecimal breachAmount = settlement.getBreachAmount();
        // 收货款提成-收货款提成负数情况下，按照0计算
        BigDecimal afterTaxSpreadAmount = settlement.getAfterTaxSpreadAmount().add(breachAmount);
        afterTaxSpreadAmount = afterTaxSpreadAmount.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : afterTaxSpreadAmount;
        // 抵扣逾期罚息
        BigDecimal deductionBreachAmount = breachAmount.compareTo(afterTaxSpreadAmount) <= 0 ? breachAmount : afterTaxSpreadAmount;
        // 实际收货款金额
         afterTaxSpreadAmount = afterTaxSpreadAmount.subtract(deductionBreachAmount);
        // 设值抵扣逾期罚息
        settlement.setDeductionBreachAmount(deductionBreachAmount);
        return afterTaxSpreadAmount;
    }

    /**
     * 初始化保存结算金额数据-逾期罚息
     *
     * @param receive
     * @param invalidFlg
     */
    @Override
    @ServiceTransactional
    public void initSaveBreachSettlementAmount(ApplyReceive receive, Boolean invalidFlg) throws ApplicationException {
        CtrContract contract = ctrContractService.getEntity(receive.getContractId());
        BigDecimal receiveBreachAmount = verifyReceive(receive, contract);
        logger.info("receive applyNo:{}, receiveBreachAmount:{}", receive.getApplyNo(), receiveBreachAmount);
        if (Objects.isNull(receiveBreachAmount) || receiveBreachAmount.compareTo(BigDecimal.ZERO) <= 0){
            return;
        }
        CtrContractSettlement settlement = settlementDao.findBySellContractId(contract.getId());
        if (Objects.isNull(settlement)) {
            return;
        }
        Long settlementId = settlement.getId();
        if (Boolean.TRUE.equals(invalidFlg)) {
            CtrContractSettlementAmount amountEntity = settlementAmountDao.findByBizId(receive.getId());
            if (Objects.nonNull(amountEntity)){
                if (StringUtils.equals(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_STATUS_1, amountEntity.getSettlementStatus())){
                    throw new ApplicationException("该合同已参与结算，不允许作废!");
                }

                settlementAmountDao.deleteBySettlementIdAndBizId(settlementId, receive.getId());
                commissionDao.deleteBySettlementIdAndSettlementAmountId(settlementId, amountEntity.getId());

                CtrContractSettlementAmount deductionEntity = settlementAmountDao.findByBizIdAndSettlementType(amountEntity.getId(), BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_TYPE_2);
                if (Objects.nonNull(deductionEntity)){
                    settlementAmountDao.deleteBySettlementIdAndBizId(settlementId, amountEntity.getId());
                    commissionDao.deleteBySettlementIdAndSettlementAmountId(settlementId, deductionEntity.getId());
                }
                settlement = this.refreshSettlementAmount(settlement);
                settlement.setSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_0);
                settlementDao.save(settlement);
            }
            return;
        }

        CtrContractSettlementAmount breachSettlement = null;
        CtrContractSettlementAmount entity = settlementAmountDao.findByBizId(receive.getId());
        if (Objects.isNull(entity)) {
            entity = buildBreachSettlement(receive, settlementId, contract);
        }
        entity.setSettlementType(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_TYPE_1);
        entity.setSettlementAmount(receiveBreachAmount);
        logger.info("initSaveBreachSettlementAmount contractNo:{},receiveBreachAmount:{}",
                contract.getContractNo(), receiveBreachAmount);
        CtrCalCulateParam param = ctrContractSettlementService.getCalculateParamByUserId(settlement);
        if (settlement.verifyBeforeAprilFlg()) {
            // 1.返还逾期罚息
            BigDecimal totalDeductionBreachAmount = settlement.getDeductionBreachAmount();
            if (Objects.isNull(totalDeductionBreachAmount)) {
                // 确认抵扣逾期罚息数值
                totalDeductionBreachAmount = calculateDeductionBreachAmount(settlement);
                settlement.setDeductionBreachAmount(totalDeductionBreachAmount);
            }
            BigDecimal sumDeductionBreachAmount = settlementAmountDao.getSumSettlementAmountV2(settlement.getId(), BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_TYPE_2);
            sumDeductionBreachAmount = Objects.isNull(sumDeductionBreachAmount) ? BigDecimal.ZERO : sumDeductionBreachAmount;
            BigDecimal amount = totalDeductionBreachAmount.subtract(sumDeductionBreachAmount);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                logger.info("amount:{}, receiveBreachAmount:{}", amount, receiveBreachAmount);
                BigDecimal receiveBreach = receive.getReceiveBreachAmount();
                BigDecimal breachAmount = settlement.getBreachAmount();
                BigDecimal settlementAmount;
                if ((receiveBreach.add(receiveBreachAmount)).compareTo(breachAmount) >= 0) {
                    settlementAmount = amount;
                } else {
                    settlementAmount = amount.subtract(breachAmount.subtract(receiveBreachAmount.add(receiveBreach)));
                }
                settlementAmount = settlementAmount.compareTo(BigDecimal.ZERO) <= 0 ? amount : settlementAmount;
                breachSettlement = buildBreachSettlement(receive, settlementId, contract);
                breachSettlement.setSettlementAmount(settlementAmount);
                breachSettlement.setSettlementType(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_TYPE_2);
                breachSettlement = settlementAmountDao.save(breachSettlement);
                commissionService.initSaveSettlementCommission(settlement, breachSettlement, param);
            }
        }

        // 2.收逾期罚息
        entity = settlementAmountDao.save(entity);
        commissionService.initSaveSettlementCommission(settlement, entity, param);

        // 保存关联关系
        if (Objects.nonNull(breachSettlement) && settlement.verifyBeforeAprilFlg()){
            breachSettlement.setBizId(entity.getId());
            settlementAmountDao.save(breachSettlement);
        }

        settlement = this.refreshSettlementAmount(settlement);
        settlement.setSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_0);
        settlementDao.save(settlement);
    }

    private BigDecimal verifyReceive(ApplyReceive receive, CtrContract contract) {
        if (Boolean.FALSE.equals(contract.getMatchCreditFlg())) {
            return BigDecimal.ZERO;
        } else if (StringUtils.equals("M", receive.getReceiveType())) {
            return receive.getReceiveAmount();
        } else {
            return BigDecimal.ZERO;
        }
    }

    private CtrContractSettlementAmount buildBreachSettlement(ApplyReceive receive, Long settlementId, CtrContract contract){
        CtrContractSettlementAmount entity = new CtrContractSettlementAmount();
        entity.setBizId(receive.getId());
        entity.setSettlementId(settlementId);
        entity.setContractId(contract.getId());
        entity.setContractNo(contract.getContractNo());
        entity.setSettlementStatus(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_STATUS_0);
        return entity;
    }

    /**
     * 更新合同结算表，已结算金额、待结算金额
     *
     * @param settlement
     */
    @Override
    public CtrContractSettlement refreshSettlementAmount(CtrContractSettlement settlement) {
        BigDecimal hasSettlementAmount = BigDecimal.ZERO;
        BigDecimal noneSettlementAmount = BigDecimal.ZERO;
        List<CtrContractSettlementAmount> resultList = settlementAmountDao.findBySettlementId(settlement.getId());
        for (CtrContractSettlementAmount entity : resultList) {
            String settlementStatus = entity.getSettlementStatus();
            BigDecimal settlementAmount = entity.getSettlementAmount();
            if (StringUtils.equals(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_STATUS_0, settlementStatus)) {
                noneSettlementAmount = noneSettlementAmount.add(settlementAmount);
            } else {
                hasSettlementAmount = hasSettlementAmount.add(settlementAmount);
            }
        }
        settlement.setHasSettlementAmount(hasSettlementAmount);
        settlement.setNoneSettlementAmount(noneSettlementAmount);

        settlement = commissionService.refreshSettlementAmount(settlement);
        return settlement;
    }

    /**
     * 结算 结算单
     *
     * @param settlementIds
     */
    @Override
    @ServiceTransactional
    public void makeComplete(List<Long> settlementIds) {
        if (CollectionUtils.isEmpty(settlementIds)){
            return;
        }
        settlementAmountDao.updateSettlementStatus(settlementIds, BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_STATUS_1);
        commissionDao.updateSettlementStatus(settlementIds, BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_STATUS_1);
    }

    /**
     * 数据处理
     * 刷新初始化收逾期罚息提成明细数据
     *
     * @param contractNo
     */
    @Override
    @ServiceTransactional
    public void refreshBreachCommission(String contractNo) {
        if (StringUtils.isEmpty(contractNo)) {
            return;
        }
        List<ApplyReceive> receiveList = applyReceiveDao.findByContractNo(contractNo);
        List<ApplyReceive> resultList = receiveList.stream().filter(r -> StringUtils.equals("M", r.getReceiveType())).collect(Collectors.toList());
        resultList.forEach(entity -> {
            try {
                initSaveBreachSettlementAmount(entity, !StringUtils.equals(BasConstants.APPROVE_STATUS_D, entity.getStatus()));
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
