package com.spt.bas.server.performance.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.PerformanceCommissionUser;
import com.spt.bas.client.entity.RptBaseCost;
import com.spt.bas.client.vo.performance.UserCommissionSummary;
import com.spt.bas.report.client.remote.IRptBsCompanyClient;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditQueryVo;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditVo;
import com.spt.bas.server.dao.CtrContractSettlementDao;
import com.spt.bas.server.dao.performance.PerformanceCommissionUserDao;
import com.spt.bas.server.performance.service.IPerformanceCommissionUserService;
import com.spt.bas.server.service.IRptBaseCostService;
import com.spt.bas.server.util.DeptUtils;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.constant.PmConstants;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/1/22 16:28
 */
@Component
@Transactional(readOnly = true)
public class PerformanceCommissionUserServiceImpl extends BaseService<PerformanceCommissionUser> implements IPerformanceCommissionUserService {
    @Resource
    private PerformanceCommissionUserDao performanceCommissionUserDao;
    @Resource
    private CtrContractSettlementDao ctrContractSettlementDao;
    @Resource
    private IRptBsCompanyClient bsCompanyClient;
    @Resource
    private IRptBaseCostService rptBaseCostService;
    @Resource
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private DeptUtils deptUtils;

    private final BigDecimal PERFORMANCE_COMMISSION_RATE_035 = BigDecimal.valueOf(0.35);
    private final BigDecimal PERFORMANCE_COMMISSION_MONTH_080 = BigDecimal.valueOf(0.8);
    private final BigDecimal PERFORMANCE_COMMISSION_YEAR_020 = BigDecimal.valueOf(0.2);
    @Override
    public BaseDao<PerformanceCommissionUser> getBaseDao() {
        return performanceCommissionUserDao;
    }

    @Override
    @ServerTransactional
    public void initPerformanceCommissionUser(Date commissionDate, Long userId) {
        commissionDate = Objects.isNull(commissionDate) ? new Date() : commissionDate;
        List<CtrContractSettlement> settlementList;
        if (Objects.nonNull(userId)) {
            settlementList = ctrContractSettlementDao.getCommissionList(commissionDate, userId);
        } else {
            settlementList = ctrContractSettlementDao.getCommissionList(commissionDate);
        }
        if (Collections.isEmpty(settlementList)) {
            logger.info("没有符合条件的结算单");
            return;
        }
        // 结算年月
        String yearMonth = DateTimeFormatter.ofPattern("yyyy-MM")
                .format(commissionDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate());

        // 1.查询业务员毛利
        Map<Long, BigDecimal> grossMarginMap = mergeUserCommission(settlementList);

        // 2.查询业务员人工成本
        List<RptBaseCost> rptBaseCostList = rptBaseCostService.findRptBaseCostByBaseDate(commissionDate);
        Map<Long, BigDecimal> laborCostMap = mergeLaborCost(rptBaseCostList);

        // 3.查询业务员差旅招待成本
        Map<Long, RptBaseCost> travelCostMap = mergeTravelCost(rptBaseCostList);

        // 4.查询业务员保险审批成本
        List<RptOpenCompanyCreditVo> openCreditList = bsCompanyClient.findOpenCreditList(new RptOpenCompanyCreditQueryVo(commissionDate));
        Map<Long, BigDecimal> openCreditMap = mergeUserInsuranceCost(openCreditList);

        List<SysUserSdk> sysUserList = authOpenFacade.findUserAll(new UserSearchVo());
        Map<Long, SysUserSdk> matchUserMap = sysUserList.stream().filter(Objects::nonNull).collect(Collectors.toMap(SysUserSdk::getUserId, e -> e, (a, b) ->b));

        List<PerformanceCommissionUser> initPerformanceData = new ArrayList<>();
        grossMarginMap.forEach((performanceUserId, grossMargin) -> {
            PerformanceCommissionUser user = new PerformanceCommissionUser();
            user.setPerformanceDate(yearMonth);
            user.setUserId(performanceUserId);
            user.setUserName(matchUserMap.getOrDefault(performanceUserId, new SysUserSdk()).getNickName());
            SysDeptSdk sysDept = deptUtils.getDeptByUserIdAndDeptType(performanceUserId, PmConstants.NODE_TYPE_DEPT);
            if (Objects.nonNull(sysDept)){
                user.setDeptId(sysDept.getDeptId());
                user.setOwningRegion(deptUtils.getOwningRegion(sysDept));
                user.setLeaderUserId(sysDept.getLeaderId());
                user.setLeaderUserName(matchUserMap.getOrDefault(sysDept.getLeaderId(), new SysUserSdk()).getNickName());
            }
            if (StringUtils.isBlank(user.getOwningRegion())){
                user.setOwningRegion(travelCostMap.getOrDefault(performanceUserId, new RptBaseCost()).getBranchCd());
            }
            // 管理成本
            user.setManageCost(BigDecimal.valueOf(5000L));
            // 毛利
            user.setGrossMargin(safe(grossMargin));
            // 人工成本
            user.setLaborCost(safe(laborCostMap.getOrDefault(performanceUserId, BigDecimal.ZERO)));
            // 差旅成本
            user.setTravelCost(safe(travelCostMap.getOrDefault(performanceUserId, new RptBaseCost()).getEvectionCost()));
            // 保险成本
            user.setInsuranceCost(safe(openCreditMap.getOrDefault(performanceUserId, BigDecimal.ZERO)));
            // 净利 = 毛利 - 管理成本 - 人工成本 - 差旅成本 - 保险成本
            user.setNetProfit(safe(grossMargin)
                    .subtract(safe(user.getManageCost()))
                    .subtract(safe(user.getLaborCost()))
                    .subtract(safe(user.getTravelCost()))
                    .subtract(safe(user.getInsuranceCost())));
            // 业务员提成金额 = 净利 * 35%
            user.setCommission(user.getNetProfit().multiply(PERFORMANCE_COMMISSION_RATE_035).setScale(2, RoundingMode.HALF_UP));
            // 当月结算 = 提成金额 * 80%
            user.setMonthCommission(user.getCommission().multiply(PERFORMANCE_COMMISSION_MONTH_080).setScale(2, RoundingMode.HALF_UP));
            // 当年余额 = 提成金额 * 20%
            user.setYearCommission(user.getCommission().multiply(PERFORMANCE_COMMISSION_YEAR_020).setScale(2, RoundingMode.HALF_UP));
            initPerformanceData.add(user);
        });
        if (CollectionUtils.isNotEmpty(initPerformanceData)) {
            // 删除历史数据
            performanceCommissionUserDao.deleteAllByPerformanceDate(yearMonth);
            // 保存
            performanceCommissionUserDao.saveAll(initPerformanceData);
        }
    }

    @Override
    public PerformanceCommissionUser findPerformanceCommissionUser(PerformanceCommissionUser queryVo) {
        String performanceDate = queryVo.getPerformanceDate();
        Long userId = queryVo.getUserId();
        if (StringUtils.isBlank(performanceDate) || Objects.isNull(userId)){
            return null;
        }
        return performanceCommissionUserDao.getPerformanceCommissionUser(performanceDate, userId);
    }

    private Map<Long, BigDecimal> mergeLaborCost(List<RptBaseCost> rptBaseCostList) {
        return rptBaseCostList.stream()
                .filter(e -> e.getMatchUserId() != null)
                .collect(Collectors.toMap(
                        RptBaseCost::getMatchUserId,
                        e -> safe(e.getWages())
                                .add(safe(e.getOtherCost()))
                                .add(safe(e.getSocialSecurity()))
                                .add(safe(e.getProvidentFund())),
                        BigDecimal::add
                ));
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Map<Long, RptBaseCost> mergeTravelCost(List<RptBaseCost> rptBaseCostList) {
        return rptBaseCostList.stream()
                .filter(e -> e.getMatchUserId() != null)
                .collect(Collectors.toMap(
                        RptBaseCost::getMatchUserId,
                        e -> e,
                        (existing, replacement) -> existing
                ));
    }

    private Map<Long, BigDecimal> mergeUserInsuranceCost(List<RptOpenCompanyCreditVo> openCreditList) {
        return openCreditList.stream()
                .collect(Collectors.groupingBy(
                        RptOpenCompanyCreditVo::getMatchUserId,
                        Collectors.mapping(
                                vo -> {
                                    switch (vo.getCreditType()) {
                                        case BasConstants.CREDIT_TYPE_0:
                                            return BigDecimal.valueOf(400);
                                        case BasConstants.CREDIT_TYPE_2:
                                            return BigDecimal.valueOf(800);
                                        default:
                                            return BigDecimal.ZERO;
                                    }
                                },
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

    private Map<Long, BigDecimal> mergeUserCommission(List<CtrContractSettlement> settlementList) {
        return settlementList.stream()
                .filter(e -> e.getSellMatchUserId() != null)
                .collect(Collectors.groupingBy(
                        CtrContractSettlement::getSellMatchUserId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                e -> Optional.ofNullable(e.getAfterTaxSpreadAmount()).orElse(BigDecimal.ZERO),
                                BigDecimal::add
                        )
                ));
    }

    private static void addCommission(
            Map<Long, UserCommissionSummary> map,
            Long userId,
            BigDecimal amount,
            BiConsumer<UserCommissionSummary, BigDecimal> consumer) {

        if (userId == null || amount == null) {
            return;
        }

        UserCommissionSummary summary =
                map.computeIfAbsent(userId, id -> {
                    UserCommissionSummary u = new UserCommissionSummary();
                    u.setUserId(id);
                    return u;
                });

        consumer.accept(summary, amount);
    }
}
