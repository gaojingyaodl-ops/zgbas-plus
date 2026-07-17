package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyConfig;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrContractSettlementAmount;
import com.spt.bas.client.entity.CtrContractSettlementCommission;
import com.spt.bas.client.vo.CtrCalCulateParam;
import com.spt.bas.server.dao.CtrContractSettlementCommissionDao;
import com.spt.bas.server.service.IBsCompanyConfigService;
import com.spt.bas.server.service.ICtrContractSettlementCommissionService;
import com.spt.bas.server.service.ICtrContractSettlementService;
import com.spt.bas.server.util.CommissionCalculateUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 合同计算提成表
 *
 * @author MoonLight
 */
@Component
public class CtrContractSettlementCommissionImpl extends BaseService<CtrContractSettlementCommission> implements ICtrContractSettlementCommissionService {
    @Resource
    private CtrContractSettlementCommissionDao settlementCommissionDao;
    @Resource
    private ICtrContractSettlementService settlementService;
    @Resource
    private CommissionCalculateUtil calculateUtil;
    @Resource
    private IBsCompanyConfigService bsCompanyConfigService;

    @Override
    public BaseDao<CtrContractSettlementCommission> getBaseDao() {
        return settlementCommissionDao;
    }

    /**
     * 初始化保存结算提成数据-货款
     *
     * @param settlementAmount
     */
    @Override
    @ServiceTransactional
    public CtrContractSettlementCommission initSaveSettlementCommission(CtrContractSettlement settlement, CtrContractSettlementAmount settlementAmount, CtrCalCulateParam param) {
        CtrContractSettlementCommission entity = settlementCommissionDao.findBySettlementAmountId(settlementAmount.getId());
        if (Objects.isNull(entity)) {
            entity = new CtrContractSettlementCommission();
            BeanUtils.copyProperties(settlement, entity);
            entity.setId(0L);
            entity.setContractId(settlement.getSellContractId());
            entity.setContractNo(settlement.getSellContractNo());
            entity.setSettlementId(settlement.getId());
            entity.setSettlementAmountId(settlementAmount.getId());
        }
        entity.setAfterTaxSpreadAmount(settlementAmount.getSettlementAmount());
        entity.setSettlementStatus(settlementAmount.getSettlementStatus());
        entity.setSettlementType(settlementAmount.getSettlementType());
        if (Objects.isNull(param)){
            param = settlementService.getCalculateParamByUserId(settlement);
        }
        // 销售人员分成
        BsCompanyConfig companyConfig = bsCompanyConfigService.findByBsCompanyIdAndMatchUserId(settlement.getSellCompanyId(), settlement.getSellMatchUserId());
        entity.setSellMatchAmount(calculateUtil.getSellMatchAmount(param, settlement, entity, companyConfig));
        // 采购人员分成
        entity.setBuyMatchAmount(calculateUtil.getBuyMatchAmount(param, settlement, entity));
        // 销售团队负责人分成
        entity.setSellHeadCommissionAmount(calculateUtil.getSaleTeamLeaderAmount(param, settlement, entity));
        // 采购人员负责人分成
        entity.setBuyHeadCommissionAmount(calculateUtil.getBuyHeadTeamLeaderAmount(param, settlement, entity));
        settlementCommissionDao.save(entity);
        return entity;
    }

    /**
     * 更新合计结算提成数据
     *
     * @param settlement
     */
    @Override
    public CtrContractSettlement refreshSettlementAmount(CtrContractSettlement settlement) {
        List<CtrContractSettlementCommission> resultList = settlementCommissionDao.findBySettlementId(settlement.getId());
        List<CtrContractSettlementCommission> resultNoneSettlementList = resultList.stream().filter(c -> StringUtils.equals(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_STATUS_0, c.getSettlementStatus())).collect(Collectors.toList());
        BigDecimal afterTaxSpreadAmount = BigDecimal.ZERO;
        BigDecimal sellMatchAmount = BigDecimal.ZERO;
        BigDecimal buyMatchAmount = BigDecimal.ZERO;
        BigDecimal buyHeadCommissionAmount = BigDecimal.ZERO;
        BigDecimal sellHeadCommissionAmount = BigDecimal.ZERO;
        for (CtrContractSettlementCommission entity : resultNoneSettlementList) {
            afterTaxSpreadAmount = afterTaxSpreadAmount.add(entity.getAfterTaxSpreadAmount());
            sellMatchAmount = sellMatchAmount.add(entity.getSellMatchAmount());
            buyMatchAmount = buyMatchAmount.add(entity.getBuyMatchAmount());
            buyHeadCommissionAmount = buyHeadCommissionAmount.add(entity.getBuyHeadCommissionAmount());
            sellHeadCommissionAmount = sellHeadCommissionAmount.add(entity.getSellHeadCommissionAmount());
        }

        BigDecimal sellMatchTotalAmount = BigDecimal.ZERO;
        BigDecimal buyMatchTotalAmount = BigDecimal.ZERO;
        BigDecimal buyHeadCommissionTotalAmount = BigDecimal.ZERO;
        BigDecimal sellHeadCommissionTotalAmount = BigDecimal.ZERO;
        for (CtrContractSettlementCommission entity : resultList) {
            sellMatchTotalAmount = sellMatchTotalAmount.add(entity.getSellMatchAmount());
            buyMatchTotalAmount = buyMatchTotalAmount.add(entity.getBuyMatchAmount());
            buyHeadCommissionTotalAmount = buyHeadCommissionTotalAmount.add(entity.getBuyHeadCommissionAmount());
            sellHeadCommissionTotalAmount = sellHeadCommissionTotalAmount.add(entity.getSellHeadCommissionAmount());
        }
        settlement.setSellMatchAmount(sellMatchAmount);
        settlement.setBuyMatchAmount(buyMatchAmount.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : buyMatchAmount);
        settlement.setBuyHeadCommissionAmount(buyHeadCommissionAmount.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : buyHeadCommissionAmount);
        settlement.setSellHeadCommissionAmount(sellHeadCommissionAmount);

        settlement.setSellMatchTotalAmount(sellMatchTotalAmount);
        settlement.setBuyMatchTotalAmount(buyMatchTotalAmount.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : buyMatchTotalAmount);
        settlement.setBuyHeadCommissionTotalAmount(buyHeadCommissionTotalAmount.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : buyHeadCommissionTotalAmount);
        settlement.setSellHeadCommissionTotalAmount(sellHeadCommissionTotalAmount);
        return settlement;
    }

    /**
     * 查询合同结算提成明细
     *
     * @param settlementId
     * @return
     */
    @Override
    public List<CtrContractSettlementCommission> findSettlementCommissionList(Long settlementId) {
        return settlementCommissionDao.findBySettlementId(settlementId);
    }
}
