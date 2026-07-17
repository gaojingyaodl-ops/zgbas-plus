package com.spt.bas.server.performance.service.impl;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.PerformanceCommissionDept;
import com.spt.bas.client.entity.PerformanceCommissionUser;
import com.spt.bas.client.entity.RptBaseCost;
import com.spt.bas.server.dao.performance.PerformanceCommissionDeptDao;
import com.spt.bas.server.dao.performance.PerformanceCommissionUserDao;
import com.spt.bas.server.performance.service.IPerformanceCommissionDeptService;
import com.spt.bas.server.service.IRptBaseCostService;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/2/28 14:36
 */
@Component
@Transactional(readOnly = true)
public class PerformanceCommissionDeptServiceImpl extends BaseService<PerformanceCommissionDept> implements IPerformanceCommissionDeptService {
    @Resource
    private PerformanceCommissionUserDao performanceCommissionUserDao;
    @Resource
    private PerformanceCommissionDeptDao performanceCommissionDeptDao;
    @Resource
    private IRptBaseCostService rptBaseCostService;

    private final BigDecimal PERFORMANCE_COMMISSION_RATE_065 = BigDecimal.valueOf(0.65);
    private final BigDecimal PERFORMANCE_COMMISSION_RATE_050 = BigDecimal.valueOf(0.5);
    private final BigDecimal PERFORMANCE_COMMISSION_MONTH_080 = BigDecimal.valueOf(0.8);
    private final BigDecimal PERFORMANCE_COMMISSION_YEAR_020 = BigDecimal.valueOf(0.2);

    @Override
    public BaseDao<PerformanceCommissionDept> getBaseDao() {
        return performanceCommissionDeptDao;
    }

    @Override
    @ServerTransactional
    public void initPerformanceCommissionDept(Date commissionDate) {
        commissionDate = Objects.isNull(commissionDate) ? new Date() : commissionDate;
        List<PerformanceCommissionUser> performanceCommissionList = performanceCommissionUserDao.getPerformanceCommissionList(commissionDate);

        if (Collections.isEmpty(performanceCommissionList)) {
            return;
        }
        // 结算年月
        String yearMonth = DateTimeFormatter.ofPattern("yyyy-MM")
                .format(commissionDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate());

        List<PerformanceCommissionDept> performanceCommissionDeptList = new ArrayList<>();
        List<RptBaseCost> rptBaseCostList = rptBaseCostService.findRptBaseCostByBaseDate(commissionDate);
        // 人工成本 Map
        Map<Long, BigDecimal> laborCostMap = mergeLaborCost(rptBaseCostList);
        // 助理配置
        List<BsDictData> assistantConfig = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.OWN_REGION_ASSISTANT);
        // 计算助理负责部门数量（用于成本分摊）
        Map<Long, Long> assistantDeptCountMap = assistantConfig.stream()
                .filter(e -> StringUtils.isNotBlank(e.getDictName()))
                .flatMap(e -> Arrays.stream(e.getDictName().split(",")))
                .map(Long::parseLong)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 按负责人分组
        Map<Long, List<PerformanceCommissionUser>> commissionUserMap = performanceCommissionList.stream().collect(Collectors.groupingBy(PerformanceCommissionUser::getLeaderUserId));
        commissionUserMap.forEach((leaderUserId, commissionUserList) -> {
            if (CollectionUtils.isEmpty(commissionUserList)) {
                return;
            }
            PerformanceCommissionDept dept = new PerformanceCommissionDept();
            PerformanceCommissionUser first = commissionUserList.get(0);
            String owningRegion = first.getOwningRegion();
            String leaderUserName = first.getLeaderUserName();
            // 事业部净利
            BigDecimal deptNetProfit = commissionUserList.stream().map(PerformanceCommissionUser::getNetProfit).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 销售提成
            BigDecimal deptCommission = commissionUserList.stream().map(PerformanceCommissionUser::getCommission).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 负责人人工成本
            BigDecimal leaderLaborCost = laborCostMap.getOrDefault(leaderUserId, BigDecimal.ZERO);
            // 管理成本
            BigDecimal deptManageCost = commissionUserList.stream().map(PerformanceCommissionUser::getManageCost).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 部门助理人工成本
            BigDecimal assistantLaborCost = BigDecimal.ZERO;
            List<Long> deptAssistantUserIdList = getDeptAssistantUserIdList(assistantConfig, owningRegion);
            if (CollectionUtils.isNotEmpty(deptAssistantUserIdList)) {
                for (Long assistantUserId : deptAssistantUserIdList) {
                    BigDecimal laborCost = laborCostMap.getOrDefault(assistantUserId, BigDecimal.ZERO);
                    Long deptCount = assistantDeptCountMap.getOrDefault(assistantUserId, 1L);
                    BigDecimal shareCost = laborCost.divide(BigDecimal.valueOf(deptCount), 2, RoundingMode.HALF_UP);
                    assistantLaborCost = assistantLaborCost.add(shareCost);
                }
            }
            // 团队剩余净利
            BigDecimal residueNetProfit = deptNetProfit.multiply(PERFORMANCE_COMMISSION_RATE_065).setScale(2, RoundingMode.HALF_UP).subtract(leaderLaborCost).subtract(assistantLaborCost);
            // 负责人提成
            BigDecimal leaderCommission = residueNetProfit.multiply(PERFORMANCE_COMMISSION_RATE_050).setScale(2, RoundingMode.HALF_UP);
            BigDecimal nextMonthBalance = BigDecimal.ZERO;
            if (leaderCommission.compareTo(BigDecimal.ZERO) < 0) {
                nextMonthBalance = leaderCommission;
                leaderCommission = BigDecimal.ZERO;
            }
            BigDecimal leaderMonthCommission = leaderCommission.multiply(PERFORMANCE_COMMISSION_MONTH_080).setScale(2, RoundingMode.HALF_UP);
            BigDecimal leaderYearCommission = leaderCommission.multiply(PERFORMANCE_COMMISSION_YEAR_020).setScale(2, RoundingMode.HALF_UP);

            // 赋值
            dept.setLeaderUserId(leaderUserId);
            dept.setLeaderUserName(leaderUserName);
            dept.setPerformanceDate(yearMonth);
            dept.setOwningRegion(owningRegion);
            dept.setDeptNetProfit(deptNetProfit);
            dept.setDeptCommission(deptCommission);
            dept.setLeaderLaborCost(leaderLaborCost);
            dept.setDeptManageCost(deptManageCost);
            dept.setAssistantLaborCost(assistantLaborCost);
            dept.setResidueNetProfit(residueNetProfit);
            dept.setLeaderCommission(leaderCommission);
            dept.setLeaderMonthCommission(leaderMonthCommission);
            dept.setLeaderYearCommission(leaderYearCommission);
            dept.setNextMonthBalance(nextMonthBalance);
            performanceCommissionDeptList.add(dept);
        });
        if (CollectionUtils.isNotEmpty(performanceCommissionDeptList)) {
            // 删除历史数据
            performanceCommissionDeptDao.deleteAllByPerformanceDate(yearMonth);
            // 保存
            performanceCommissionDeptDao.saveAll(performanceCommissionDeptList);
        }
    }

    @Override
    public PerformanceCommissionDept findPerformanceCommissionDept(PerformanceCommissionDept queryVo) {
        String performanceDate = queryVo.getPerformanceDate();
        Long userId = queryVo.getLeaderUserId();
        if (org.apache.commons.lang.StringUtils.isBlank(performanceDate) || Objects.isNull(userId)){
            return null;
        }
        return performanceCommissionDeptDao.getPerformanceCommissionDept(performanceDate, userId);
    }

    private List<Long> getDeptAssistantUserIdList(List<BsDictData> assistantConfig, String ownRegion) {
        return assistantConfig.stream()
                .filter(e -> ownRegion.equals(e.getDictCd()))
                .map(BsDictData::getDictName)
                .filter(Objects::nonNull)
                .flatMap(name -> Arrays.stream(name.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
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
}
