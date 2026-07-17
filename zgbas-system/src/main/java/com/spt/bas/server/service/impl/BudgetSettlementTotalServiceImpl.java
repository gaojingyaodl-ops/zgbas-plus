package com.spt.bas.server.service.impl;


import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BudgetSettlementTotal;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrContractSettlementCommission;
import com.spt.bas.server.dao.BudgetSettlementTotalDao;
import com.spt.bas.server.dao.CtrContractSettlementCommissionDao;
import com.spt.bas.server.dao.CtrContractSettlementDao;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.IBudgetSettlementTotalService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Transactional(readOnly = true)
public class BudgetSettlementTotalServiceImpl extends BaseService<BudgetSettlementTotal> implements IBudgetSettlementTotalService {
    @Autowired
    private BudgetSettlementTotalDao budgetSettlementTotalDao;
    @Autowired
    private CtrContractSettlementDao settlementDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private CtrContractSettlementCommissionDao commissionDao;

    List<String> companyGradeList = new ArrayList<String>(2) {
        private static final long serialVersionUID = 8129796420515413380L;

        {
            add(BasConstants.DICT_TYPE_COMPANYCATEGORY_A);
            add(BasConstants.DICT_TYPE_COMPANYCATEGORY_B);
        }
    };

    Map<String, String> laborRelationsMap = new HashMap<String, String>(4) {
        private static final long serialVersionUID = 1757804862086701108L;

        {
            put("SH", "上海中光");
            put("QD", "青岛中光");
            put("YY", "余姚事业部");
            put("GZ", "广州事业部");
            put("HN", "河南事业部");
            put("AH", "安徽事业部");
        }
    };

    @Override
    public BaseDao<BudgetSettlementTotal> getBaseDao() {
        return budgetSettlementTotalDao;
    }

    @Override
    public Page<BudgetSettlementTotal> findSettlementPage(PageSearchVo searchVo) {
        Sort sort = Sort.by(Sort.Direction.ASC, "settleStatus");
        Specification<BudgetSettlementTotal> spe = WebUtil.buildSpecification(searchVo.getSearchParams());
        List<BudgetSettlementTotal> resultList = budgetSettlementTotalDao.findAll(spe, sort);
        int totalSize = resultList.size();
        int rows = searchVo.getRows();
        int page = searchVo.getPage();

        // 计算当前页的起始索引
        int startIdx = (page - 1) * rows;
        // 计算当前页的结束索引
        int endIdx = Math.min(startIdx + rows, totalSize);
        List<BudgetSettlementTotal> paginatedList = resultList.subList(startIdx, endIdx);
        Pageable pageable = PageRequest.of(page - 1, rows);
        return new PageImpl<>(paginatedList, pageable, totalSize);
    }

    @Override
    public BudgetSettlementTotal sumPageSettlement(PageSearchVo searchVo) {
        Specification<BudgetSettlementTotal> spe = WebUtil.buildSpecification(searchVo.getSearchParams());
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery();
        Root<BudgetSettlementTotal> root = query.from(BudgetSettlementTotal.class);
        CriteriaQuery<?> cq = query.where(spe.toPredicate(root, query, cb)).multiselect(
                cb.sum(root.get("sellCommissionAmount1")), cb.sum(root.get("buyCommissionAmount1")), cb.sum(root.get("sellDirectorCommissionAmount1")),
                cb.sum(root.get("buyDirectorCommissionAmount1")), cb.sum(root.get("sellCommissionAmount2")), cb.sum(root.get("buyCommissionAmount2")),
                cb.sum(root.get("sellDirectorCommissionAmount2")), cb.sum(root.get("buyDirectorCommissionAmount2")), cb.sum(root.get("totalAmount")));
        TypedQuery<?> tq = em.createQuery(cq);
        Object[] result = ((Object[]) tq.getSingleResult());
        BudgetSettlementTotal sum = new BudgetSettlementTotal();
        BigDecimal sellCommissionAmount1 = (BigDecimal) result[0];
        BigDecimal buyCommissionAmount1 = (BigDecimal) result[1];
        BigDecimal sellDirectorCommissionAmount1 = (BigDecimal) result[2];
        BigDecimal buyDirectorCommissionAmount1 = (BigDecimal) result[3];
        BigDecimal sellCommissionAmount2 = (BigDecimal) result[4];
        BigDecimal buyCommissionAmount2 = (BigDecimal) result[5];
        BigDecimal sellDirectorCommissionAmount2 = (BigDecimal) result[6];
        BigDecimal buyDirectorCommissionAmount2 = (BigDecimal) result[7];
        BigDecimal totalAmount = (BigDecimal) result[8];
        sum.setSellCommissionAmount1(sellCommissionAmount1);
        sum.setBuyCommissionAmount1(buyCommissionAmount1);
        sum.setSellDirectorCommissionAmount1(sellDirectorCommissionAmount1);
        sum.setBuyDirectorCommissionAmount1(buyDirectorCommissionAmount1);
        sum.setSellCommissionAmount2(sellCommissionAmount2);
        sum.setBuyCommissionAmount2(buyCommissionAmount2);
        sum.setSellDirectorCommissionAmount2(sellDirectorCommissionAmount2);
        sum.setBuyDirectorCommissionAmount2(buyDirectorCommissionAmount2);
        sum.setTotalAmount(totalAmount);
        return sum;
    }

    /**
     * 生成提成汇总
     *
     * @param summaryDate
     */
    @Override
    @ServiceTransactional
    public void createSettleTotal(String summaryDate) {
        if (StringUtils.isBlank(summaryDate)) {
            logger.error("summaryDate is null");
            return;
        }

        // 查询结算月份下的已审批的提成明细
        String settlementId = summaryDate.replace("-", "");
        List<CtrContractSettlement> summaryList = settlementDao.getSummaryList(settlementId);
        if (CollectionUtils.isEmpty(summaryList)) {
            logger.info("summaryList is Empty");
            return;
        }
        List<Long> settlementIdList = summaryList.stream().map(CtrContractSettlement::getId).distinct().collect(Collectors.toList());
        List<CtrContractSettlementCommission> commissionList = commissionDao.getCommissionList(settlementIdList);
        if (CollectionUtils.isEmpty(commissionList)) {
            logger.info("commissionList is Empty");
            return;
        }
        Map<Long, List<CtrContractSettlementCommission>> commissionMap = commissionList.stream().collect(Collectors.groupingBy(CtrContractSettlementCommission::getSettlementId));
        List<BudgetSettlementTotal> resultList = new ArrayList<>();
        List<Long> budgetUserIdList = getSettlementUserIdList(summaryList);
        for (Long matchUserId : budgetUserIdList) {
            resultList.add(getSummaryData(matchUserId, settlementId, summaryList, commissionMap));
        }
        // 删除本结算月份历史数据
        budgetSettlementTotalDao.deleteByBudgetSettlementId(settlementId);

        // 保存本结算月份提成汇总数据
        budgetSettlementTotalDao.saveAll(resultList);

//        // 添加标识-提成明细已计入汇总防止重复参与汇总
//        summaryList.forEach(s -> s.setSettleTotalFlg(true));
//        settlementDao.saveAll(summaryList);
    }

    private List<Long> getSettlementUserIdList(List<CtrContractSettlement> summaryList) {
        List<Long> sellMatchUserIdList = summaryList.stream().map(CtrContractSettlement::getSellMatchUserId).distinct().collect(Collectors.toList());
        List<Long> buyMatchUserIdList = summaryList.stream().map(CtrContractSettlement::getBuyMatchUserId).distinct().collect(Collectors.toList());

        sellMatchUserIdList.addAll(buyMatchUserIdList);
        return sellMatchUserIdList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 生成业务员提成汇总数据
     *
     * @param matchUserId
     * @param settlementId
     * @param summaryList
     * @return
     */
    private BudgetSettlementTotal getSummaryData(Long matchUserId, String settlementId, List<CtrContractSettlement> summaryList, Map<Long, List<CtrContractSettlementCommission>> commissionMap) {
        BudgetSettlementTotal entity = new BudgetSettlementTotal();
        List<CtrContractSettlement> matchUserSxList = summaryList.stream()
                .filter(s -> (matchUserId.equals(s.getSellMatchUserId()) || matchUserId.equals(s.getBuyMatchUserId())))
                .filter(s -> Boolean.TRUE.equals(s.getMatchCreditFlg()))
                .collect(Collectors.toList());

        List<CtrContractSettlement> matchUserDcList = summaryList.stream()
                .filter(s -> (matchUserId.equals(s.getSellMatchUserId()) || matchUserId.equals(s.getBuyMatchUserId())))
                .filter(s -> Boolean.FALSE.equals(s.getMatchCreditFlg()))
                .collect(Collectors.toList());
        for (CtrContractSettlement settlement : matchUserSxList) {
            if (verifySpecial(settlement)) {
                logger.info("sellContractNo:{}，{}，{}，因罚息导致赊销提成为负数，不计入汇总", settlement.getSellContractNo(), settlement.getSellCompanyName(), settlement.getSellMatchUserName());
                continue;
            }
            CtrContractSettlementCommission commission = getTotalCommission(settlement, commissionMap, matchUserId);
            entity.setSellCommissionAmount1(entity.getSellCommissionAmount1().add(commission.getSellMatchAmount()));
            entity.setBuyCommissionAmount1(entity.getBuyCommissionAmount1().add(commission.getBuyMatchAmount()));
            entity.setSellDirectorCommissionAmount1(entity.getSellDirectorCommissionAmount1().add(commission.getSellHeadCommissionAmount()));
            entity.setBuyDirectorCommissionAmount1(entity.getBuyDirectorCommissionAmount1().add(commission.getBuyHeadCommissionAmount()));
        }
        for (CtrContractSettlement settlement : matchUserDcList) {
            if (verifySpecial(settlement)) {
                logger.info("sellContractNo:{}，{}，{}，因罚息导致代采提成为负数，不计入汇总", settlement.getSellContractNo(), settlement.getSellCompanyName(), settlement.getSellMatchUserName());
                continue;
            }
            CtrContractSettlementCommission commission = getTotalCommission(settlement, commissionMap, matchUserId);
            entity.setSellCommissionAmount2(entity.getSellCommissionAmount2().add(commission.getSellMatchAmount()));
            entity.setBuyCommissionAmount2(entity.getBuyCommissionAmount2().add(commission.getBuyMatchAmount()));
            entity.setSellDirectorCommissionAmount2(entity.getSellDirectorCommissionAmount2().add(commission.getSellHeadCommissionAmount()));
            entity.setBuyDirectorCommissionAmount2(entity.getBuyDirectorCommissionAmount2().add(commission.getBuyHeadCommissionAmount()));
        }
        entity.setMatchUserId(matchUserId);
        entity.setBudgetSettlementId(settlementId);
        entity.setSettleStatus(setLaborRelations(matchUserId));
        entity.setTotalAmount(calculateTotal(entity));
        return entity;
    }

    private CtrContractSettlementCommission getTotalCommission(CtrContractSettlement settlement, Map<Long, List<CtrContractSettlementCommission>> commissionMap, Long matchUserId) {
        CtrContractSettlementCommission entity = new CtrContractSettlementCommission();
        List<CtrContractSettlementCommission> resultList = commissionMap.get(settlement.getId());
        if (CollectionUtils.isNotEmpty(resultList)) {
            resultList.forEach(c -> {
                Long sellMatchUserId = c.getSellMatchUserId();
                Long buyMatchUserId = c.getBuyMatchUserId();

                BigDecimal sellMatchAmount = Objects.equals(matchUserId, sellMatchUserId) ? c.getSellMatchAmount() : BigDecimal.ZERO;
                BigDecimal sellHeadCommissionAmount = Objects.equals(matchUserId, sellMatchUserId) ? c.getSellHeadCommissionAmount() : BigDecimal.ZERO;

                BigDecimal buyMatchAmount = Objects.equals(matchUserId, buyMatchUserId) ? c.getBuyMatchAmount() : BigDecimal.ZERO;
                BigDecimal buyHeadCommissionAmount = Objects.equals(matchUserId, buyMatchUserId) ? c.getBuyHeadCommissionAmount() : BigDecimal.ZERO;

                entity.setSellMatchAmount(entity.getSellMatchAmount().add(sellMatchAmount));
                entity.setSellHeadCommissionAmount(entity.getSellHeadCommissionAmount().add(sellHeadCommissionAmount));

                entity.setBuyMatchAmount(entity.getBuyMatchAmount().add(buyMatchAmount));
                entity.setBuyHeadCommissionAmount(entity.getBuyHeadCommissionAmount().add(buyHeadCommissionAmount));
            });
        }
        return entity;
    }

    /**
     * A、B类客户因逾期罚息导致提成为负数，不计入汇总
     *
     * @param settlement
     * @return
     */
    private Boolean verifySpecial(CtrContractSettlement settlement) {
        boolean flg = false;
        Date contractTime = settlement.getContractTime();
        if (Objects.nonNull(contractTime) && contractTime.before(DateOperator.parse("2024-04-01"))) {
            BigDecimal sellMatchAmount = settlement.getSellMatchAmount();
            BigDecimal breachAmount = settlement.getBreachAmount();
            Long sellCompanyId = settlement.getSellCompanyId();
            if (lessThanZero(sellMatchAmount) && greaterThanZero(breachAmount)) {
                BsCompany company = bsCompanyService.getEntity(sellCompanyId);
                if (Objects.nonNull(company) && StringUtils.isNotBlank(company.getCompanyGrade())) {
                    String companyGrade = company.getCompanyGrade();
                    return companyGradeList.contains(companyGrade);
                }
            }
        }
        return flg;
    }

    /**
     * 设置业务员所属劳务关系
     *
     * @param matchUserId
     * @return
     */
    private String setLaborRelations(Long matchUserId) {
        String laborRelations = "";
        SysUserSdk sysUser = authOpenFacade.findUserById(matchUserId);
        if (Objects.nonNull(sysUser) && StringUtils.isNotBlank(sysUser.getBranchCd())) {
            laborRelations = laborRelationsMap.get(sysUser.getBranchCd());
        }
        return laborRelations;
    }

    /**
     * 计算提成汇总业务员合计金额
     *
     * @param entity
     * @return
     */
    private BigDecimal calculateTotal(BudgetSettlementTotal entity) {
        return entity.getBuyCommissionAmount1().add(entity.getSellCommissionAmount1()).add(entity.getBuyDirectorCommissionAmount1()).add(entity.getSellDirectorCommissionAmount1()).
                add(entity.getBuyCommissionAmount2()).add(entity.getSellCommissionAmount2()).add(entity.getBuyDirectorCommissionAmount2()).add(entity.getSellDirectorCommissionAmount2());
    }

    private Boolean greaterThanZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    private Boolean lessThanZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }
}

