package com.spt.bas.report.server.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.cache.UserCache;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.report.client.constant.WorkbenchLabelEnum;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptBusinessManagerWorkbenchSearchVo;
import com.spt.bas.report.server.dao.RptBusinessManagerWorkbenchMapper;
import com.spt.bas.report.server.service.IRptBusinessManagerWorkbenchService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 业务经理工作台serviceImpl
 *
 * @author lsj
 * @version 1.0.0
 * @date 2024/12/11 16:11
 */

@Service
public class RptBusinessManagerWorkbenchServiceImpl implements IRptBusinessManagerWorkbenchService {

    @Autowired
    private RptBusinessManagerWorkbenchMapper businessManagerWorkbenchMapper;


    /**
     * 个人成就
     *
     * @param searchVo
     * @return
     */
    @Override
    public RptPersonalAchievement findPersonalAchievement(RptBusinessManagerWorkbenchSearchVo searchVo) {
        searchVo.setRows(1000);
        if (searchVo.getMonthFlag()) {
            String month = DateUtil.format(new Date(), "yyyy-MM");
            searchVo.setMonth(month);
        } else if (searchVo.getYearFlag()) {
            String year = DateUtil.format(new Date(), "yyyy");
            searchVo.setYear(year);
        }
//        RptPersonalAchievement personalAchievement = businessManagerWorkbenchMapper.findPersonalAchievement(searchVo);
        List<RptPersonalAchievement> leaderBoardMatchUserGroupList = businessManagerWorkbenchMapper.findLeaderBoardMatchUserGroupList(searchVo);
        // 初始化 BigDecimal 类型的合计值
        String Ranking = "";
        BigDecimal totalSellAmount = BigDecimal.ZERO;
        BigDecimal totalSellNumber = BigDecimal.ZERO;
        BigDecimal totalGrossProfitAmount = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(leaderBoardMatchUserGroupList)) {
            for (int i=0; i < leaderBoardMatchUserGroupList.size(); i++) {
                RptPersonalAchievement personalAchievement = leaderBoardMatchUserGroupList.get(i);
                personalAchievement.setRanking(i+1+"");
            }
            
            List<Long> matchUserIdList = searchVo.getMatchUserIdList();
            List<RptPersonalAchievement> filteredList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(matchUserIdList) && matchUserIdList.size() > 0) {
                // 使用 Stream 进行过滤，筛选出 matchUserId 在 matchUserIdList 中的记录
                filteredList = leaderBoardMatchUserGroupList.stream()
                        .filter(achievement -> matchUserIdList.contains(achievement.getMatchUserId())) // 过滤条件
                        .collect(Collectors.toList());
            } else {
                filteredList.addAll(leaderBoardMatchUserGroupList);
            }
            if (CollectionUtils.isNotEmpty(filteredList) && filteredList.size() > 0) {
                for (RptPersonalAchievement item : filteredList) {
                    Ranking = item.getRanking();
                    totalSellAmount = totalSellAmount.add(item.getSellTotalAmount());
                    totalSellNumber = totalSellNumber.add(item.getSellTotalNumber());
                    totalGrossProfitAmount = totalGrossProfitAmount.add(item.getGrossProfitAmount());
                }
            }
        }

        
        RptPersonalAchievement personalAchievement = new RptPersonalAchievement();
        personalAchievement.setRanking(Ranking);
        personalAchievement.setSellTotalAmount(totalSellAmount);
        personalAchievement.setSellTotalNumber(totalSellNumber);
        personalAchievement.setGrossProfitAmount(totalGrossProfitAmount);
        if (Objects.nonNull(personalAchievement)) {
            BigDecimal grossProfitAmount = personalAchievement.getGrossProfitAmount();
            if (Objects.nonNull(grossProfitAmount)) {
                personalAchievement.setGrossProfitAmount(grossProfitAmount.divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP));
            }
            BigDecimal sellTotalAmount = personalAchievement.getSellTotalAmount();
            if (Objects.nonNull(sellTotalAmount)) {
                personalAchievement.setSellTotalAmount(sellTotalAmount.divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP));
            }
        }
        return personalAchievement;
    }

    /**
     * 过去5个月毛利润（万元）
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<RptPersonalAchievement> findFiveMonthGrossProfitAmount(RptBusinessManagerWorkbenchSearchVo searchVo) {
        List<String> fiveMonths = getPreviousFiveMonths();
        searchVo.setFiveMonthList(fiveMonths);
        List<RptPersonalAchievement> personalAchievementList = businessManagerWorkbenchMapper.findFiveMonthGrossProfitAmount(searchVo);

        if (CollectionUtil.isNotEmpty(personalAchievementList) && personalAchievementList.size() < 18) {
            personalAchievementList = getFiveGrossProfit(personalAchievementList, fiveMonths);
        }
        for (RptPersonalAchievement personalAchievement : personalAchievementList) {
            BigDecimal grossProfitAmount = personalAchievement.getGrossProfitAmount();
            if (Objects.nonNull(grossProfitAmount)) {
                personalAchievement.setGrossProfitAmount(grossProfitAmount.divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP));
            }
        }

        return personalAchievementList;
    }

    /**
     * 订单-执行（统计）
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<RptWorkbenchContractStatist> findContractExecutionStatistList(RptBusinessManagerWorkbenchSearchVo searchVo) {

        List<RptWorkbenchContractStatist> list = new ArrayList<>();
        // 待出库
        searchVo.setLabelCode(WorkbenchLabelEnum.NCK.getLabelCode());
        Page<RptWorkbenchContract> nckPage = findSellContractExecutionPage(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.NCK.getLabelCode(), WorkbenchLabelEnum.NCK.getLabelName(), nckPage.getTotalElements()));
        // 待收款
        searchVo.setLabelCode(WorkbenchLabelEnum.NSK.getLabelCode());
        Page<RptWorkbenchContract> nskPage = findSellContractExecutionPage(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.NSK.getLabelCode(), WorkbenchLabelEnum.NSK.getLabelName(), nskPage.getTotalElements()));
        // 待开票
        searchVo.setLabelCode(WorkbenchLabelEnum.NKP.getLabelCode());
        Page<RptWorkbenchContract> nkpPage = findSellContractExecutionPage(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.NKP.getLabelCode(), WorkbenchLabelEnum.NKP.getLabelName(), nkpPage.getTotalElements()));
        // 待收票
        searchVo.setLabelCode(WorkbenchLabelEnum.NSP.getLabelCode());
        Page<RptWorkbenchContract> nspPage = findBuyContractExecutionPage(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.NSP.getLabelCode(), WorkbenchLabelEnum.NSP.getLabelName(), nspPage.getTotalElements()));

        return list;
    }

    /**
     * 查询订单-执行数据详情 （待出库，待收款，待开票）
     *
     * @param searchVo
     * @return
     */
    @Override
    public Page<RptWorkbenchContract> findSellContractExecutionPage(RptBusinessManagerWorkbenchSearchVo searchVo) {
        List<RptWorkbenchContract> sellContractExecutionList = businessManagerWorkbenchMapper.findSellContractExecution(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptWorkbenchContract> pageVo = new PageImpl<>(sellContractExecutionList, pageable, searchVo.getCount());
        return pageVo;
    }

    /**
     * 查询订单-执行数据详情 （待收票）
     *
     * @param searchVo
     * @return
     */
    @Override
    public Page<RptWorkbenchContract> findBuyContractExecutionPage(RptBusinessManagerWorkbenchSearchVo searchVo) {
        List<RptWorkbenchContract> buyContractExecutionList = businessManagerWorkbenchMapper.findBuyContractExecution(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptWorkbenchContract> pageVo = new PageImpl<>(buyContractExecutionList, pageable, searchVo.getCount());
        return pageVo;
    }

    /**
     * 订单-应收（统计）
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<RptWorkbenchContractStatist> findContractReceivableStatistList(RptBusinessManagerWorkbenchSearchVo searchVo) {
        List<RptWorkbenchContractStatist> list = new ArrayList<>();
        // 即将到期
        searchVo.setLabelCode(WorkbenchLabelEnum.N.getLabelCode());
        Page<RptWorkbenchContract> nReceivable = findSellContractReceivablePage(searchVo);
        RptWorkbenchContract nSum = businessManagerWorkbenchMapper.findSellContractReceivableSum(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.N.getLabelCode(), WorkbenchLabelEnum.N.getLabelName(), nReceivable.getTotalElements(), Objects.nonNull(nSum) ? nSum.getNoReceiveTotalAmount() : BigDecimal.ZERO ));
        
        // 宽限期
        searchVo.setLabelCode(WorkbenchLabelEnum.B.getLabelCode());
        Page<RptWorkbenchContract> bReceivable = findSellContractReceivablePage(searchVo);
        RptWorkbenchContract bSum = businessManagerWorkbenchMapper.findSellContractReceivableSum(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.B.getLabelCode(), WorkbenchLabelEnum.B.getLabelName(), bReceivable.getTotalElements(), Objects.nonNull(bSum) ? bSum.getNoReceiveTotalAmount() : BigDecimal.ZERO));
        
        // 催告期
        searchVo.setLabelCode(WorkbenchLabelEnum.D.getLabelCode());
        Page<RptWorkbenchContract> dReceivable = findSellContractReceivablePage(searchVo);
        RptWorkbenchContract dSum = businessManagerWorkbenchMapper.findSellContractReceivableSum(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.D.getLabelCode(), WorkbenchLabelEnum.D.getLabelName(), dReceivable.getTotalElements(), Objects.nonNull(dSum) ? dSum.getNoReceiveTotalAmount() : BigDecimal.ZERO));
       
        // 逾期
        searchVo.setLabelCode(WorkbenchLabelEnum.S.getLabelCode());
        Page<RptWorkbenchContract> sReceivable = findSellContractReceivablePage(searchVo);
        RptWorkbenchContract sSum = businessManagerWorkbenchMapper.findSellContractReceivableSum(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.S.getLabelCode(), WorkbenchLabelEnum.S.getLabelName(), sReceivable.getTotalElements(), Objects.nonNull(sSum) ? sSum.getNoReceiveTotalAmount() : BigDecimal.ZERO));
      
        // 诉讼
        searchVo.setLabelCode(WorkbenchLabelEnum.P.getLabelCode());
        Page<RptWorkbenchContract> pReceivable = findSellContractReceivablePage(searchVo);
        RptWorkbenchContract pSum = businessManagerWorkbenchMapper.findSellContractReceivableSum(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.P.getLabelCode(), WorkbenchLabelEnum.P.getLabelName(), pReceivable.getTotalElements(), Objects.nonNull(pSum) ? pSum.getNoReceiveTotalAmount() : BigDecimal.ZERO));

        return list;
    }

    /**
     * 查询订单-应收数据详情
     *
     * @param searchVo
     * @return
     */
    @Override
    public Page<RptWorkbenchContract> findSellContractReceivablePage(RptBusinessManagerWorkbenchSearchVo searchVo) {
        List<RptWorkbenchContract> sellContractReceivableList = businessManagerWorkbenchMapper.findSellContractReceivable(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptWorkbenchContract> pageVo = new PageImpl<>(sellContractReceivableList, pageable, searchVo.getCount());
        return pageVo;
    }

    /**
     * 订单应收 合计
     * @param searchVo
     * @return
     */
    @Override
    public RptWorkbenchContract findSellContractReceivableSum(RptBusinessManagerWorkbenchSearchVo searchVo) {
        return businessManagerWorkbenchMapper.findSellContractReceivableSum(searchVo);
    }

    /**
     * 查询订单-审批 统计
     * @param searchVo
     * @return
     */
    public Page<RptWorkbenchApproveStatist> findContractApproveStatistList(RptBusinessManagerWorkbenchSearchVo searchVo){
        List<RptWorkbenchApproveStatist> budgetApproveStatist = businessManagerWorkbenchMapper.findBudgetApproveStatist(searchVo);
        if (CollectionUtils.isNotEmpty(budgetApproveStatist)) {
            boolean containsDCSX = budgetApproveStatist.stream()
                    .anyMatch(statist -> statist.getLabelName() != null && statist.getLabelName().contains(WorkbenchLabelEnum.DCSX.getLabelName()));
            if (!containsDCSX) {
                budgetApproveStatist.add(new RptWorkbenchApproveStatist(WorkbenchLabelEnum.DCSX.getLabelCode(),WorkbenchLabelEnum.DCSX.getLabelName(), 0L));
            }
            boolean containsDC = budgetApproveStatist.stream()
                    .anyMatch(statist -> statist.getLabelName() != null && statist.getLabelName().contains(WorkbenchLabelEnum.DC.getLabelName()));
            if (!containsDC) {
                budgetApproveStatist.add(new RptWorkbenchApproveStatist(WorkbenchLabelEnum.DC.getLabelCode(),WorkbenchLabelEnum.DC.getLabelName(), 0L));
            }
            boolean containsSX = budgetApproveStatist.stream()
                    .anyMatch(statist -> statist.getLabelName() != null && statist.getLabelName().contains(WorkbenchLabelEnum.SX.getLabelName()));
            if (!containsSX) {
                budgetApproveStatist.add(new RptWorkbenchApproveStatist(WorkbenchLabelEnum.SX.getLabelCode(),WorkbenchLabelEnum.SX.getLabelName(), 0L));
            }

            for (RptWorkbenchApproveStatist approveStatist : budgetApproveStatist) {
                if (StringUtils.equals(WorkbenchLabelEnum.DCSX.getLabelName(), approveStatist.getLabelName())) {
                    approveStatist.setLabelCode(WorkbenchLabelEnum.DCSX.getLabelCode());
                    
                    searchVo.setContractType(BasConstants.CONTRACT_TYPE_B);
                    searchVo.setMatchCreditFlg(true);
                    searchVo.setBudgetType(WorkbenchLabelEnum.DCSX.getLabelCode());
                    // 代采赊销供应商双签
                    RptWorkbenchApproveStatist buyApproveStatist = businessManagerWorkbenchMapper.findSealApproveStatist(searchVo);
                    approveStatist.setBuySealCount(buyApproveStatist.getCount());
                    
                    // 代采赊销供应商付款
                    RptWorkbenchApproveStatist buyPayApproveStatist = businessManagerWorkbenchMapper.findBuyPayApproveStatist(searchVo);
                    approveStatist.setBuyPayCount(buyPayApproveStatist.getCount());
                    
                    searchVo.setContractType(BasConstants.CONTRACT_TYPE_S);
                    // 代采赊销客户双签
                    RptWorkbenchApproveStatist sellApproveStatist = businessManagerWorkbenchMapper.findSealApproveStatist(searchVo);
                    approveStatist.setSellSealCount(sellApproveStatist.getCount());
                   
                } else if (StringUtils.equals(WorkbenchLabelEnum.DC.getLabelName(), approveStatist.getLabelName())) {
                    approveStatist.setLabelCode(WorkbenchLabelEnum.DC.getLabelCode());
                    
                    searchVo.setContractType(BasConstants.CONTRACT_TYPE_B);
                    searchVo.setMatchCreditFlg(false);
                    searchVo.setBudgetType(null);
                    // 代采供应商双签
                    RptWorkbenchApproveStatist buyApproveStatist = businessManagerWorkbenchMapper.findSealApproveStatist(searchVo);
                    approveStatist.setBuySealCount(buyApproveStatist.getCount());

                    // 代采供应商付款
                    RptWorkbenchApproveStatist buyPayApproveStatist = businessManagerWorkbenchMapper.findBuyPayApproveStatist(searchVo);
                    approveStatist.setBuyPayCount(buyPayApproveStatist.getCount());
                    
                    searchVo.setContractType(BasConstants.CONTRACT_TYPE_S);
                    // 代采客户双签
                    RptWorkbenchApproveStatist sellApproveStatist = businessManagerWorkbenchMapper.findSealApproveStatist(searchVo);
                    approveStatist.setSellSealCount(sellApproveStatist.getCount());

                } else if (StringUtils.equals(WorkbenchLabelEnum.SX.getLabelName(), approveStatist.getLabelName())) {
                    approveStatist.setLabelCode(WorkbenchLabelEnum.SX.getLabelCode());
                    
                    searchVo.setContractType(BasConstants.CONTRACT_TYPE_B);
                    searchVo.setMatchCreditFlg(true);
                    searchVo.setBudgetType(WorkbenchLabelEnum.SX.getLabelCode());
                    // 赊销供应商双签
                    RptWorkbenchApproveStatist buyApproveStatist = businessManagerWorkbenchMapper.findSealApproveStatist(searchVo);
                    approveStatist.setBuySealCount(buyApproveStatist.getCount());
                    
                    // 赊销供应商付款
                    RptWorkbenchApproveStatist buyPayApproveStatist = businessManagerWorkbenchMapper.findBuyPayApproveStatist(searchVo);
                    approveStatist.setBuyPayCount(buyPayApproveStatist.getCount());
                    
                    searchVo.setContractType(BasConstants.CONTRACT_TYPE_S);
                    // 赊销客户双签
                    RptWorkbenchApproveStatist sellApproveStatist = businessManagerWorkbenchMapper.findSealApproveStatist(searchVo);
                    approveStatist.setSellSealCount(sellApproveStatist.getCount());

                }
            }
            
        } else {
            budgetApproveStatist = new ArrayList<>();
            budgetApproveStatist.add(new RptWorkbenchApproveStatist(WorkbenchLabelEnum.DCSX.getLabelCode(),WorkbenchLabelEnum.DCSX.getLabelName(), 0L));
            budgetApproveStatist.add(new RptWorkbenchApproveStatist(WorkbenchLabelEnum.DC.getLabelCode(),WorkbenchLabelEnum.DC.getLabelName(), 0L));
            budgetApproveStatist.add(new RptWorkbenchApproveStatist(WorkbenchLabelEnum.SX.getLabelCode(),WorkbenchLabelEnum.SX.getLabelName(), 0L));
        }
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptWorkbenchApproveStatist> pageVo = new PageImpl<>(budgetApproveStatist, pageable, searchVo.getCount());
        return pageVo;
    }

    /**
     * 查询订单-审批（预算）数据详情
     * @param searchVo
     * @return
     */
    @Override
    public Page<RptWorkbenchApprove> findContractApprovePage(RptBusinessManagerWorkbenchSearchVo searchVo) {
        
        // 查询 未双签或供应商未付款的预算申请单
        List<RptWorkbenchApprove> contractApproveList = businessManagerWorkbenchMapper.findContractApprove(searchVo);
        // 预算申请单审批ID
        List<Long> approveIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(contractApproveList)) {
            for (RptWorkbenchApprove workbenchApprove : contractApproveList) {
                String budgetStatus = workbenchApprove.getBudgetStatus();
                if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, budgetStatus)) {
                    approveIdList.add(workbenchApprove.getApproveId());
                } else {
                    workbenchApprove.setBudgetApproveUserName(UserCache.getUserName(workbenchApprove.getCurrApproveUserId()));
                }
            }
        } else {
            Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
            Page<RptWorkbenchApprove> pageVo = new PageImpl<>(contractApproveList, pageable, searchVo.getCount());
            return pageVo;
        }
        if (CollectionUtils.isNotEmpty(approveIdList)) {
            searchVo.setApproveIdList(approveIdList);
            RptBusinessManagerWorkbenchSearchVo newSearchVo = new RptBusinessManagerWorkbenchSearchVo();
            BeanUtils.copyProperties(searchVo, newSearchVo);
            newSearchVo.setPage(1);
            newSearchVo.setRows(100);
            // 根据预算审批ID查询对应审批通过的合同
            List<RptWorkbenchContract> contractList = businessManagerWorkbenchMapper.findContractByApproveIds(newSearchVo);
            List<Long> buyIdList = new ArrayList<>();
            List<Long> sellIdList = new ArrayList<>();
            
            List<String> buyContractNoList = new ArrayList<>();
            List<String> sellContractNoList = new ArrayList<>();
            
            for (RptWorkbenchContract contract : contractList) {
                if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B,contract.getContractType())) {
                    Boolean specialChainFlag = contract.getSpecialChainFlag();
                    if (!specialChainFlag) {
                        Long virtualContractId = contract.getVirtualContractId();
                        if (virtualContractId != null) {
                            buyIdList.add(contract.getVirtualContractId());
                        } else {
                            buyIdList.add(contract.getId());
                        }
                        String virtualContractNo = contract.getVirtualContractNo();
                        if (StringUtils.isNotBlank(virtualContractNo)) {
                            buyContractNoList.add(virtualContractNo);
                        } else {
                            buyContractNoList.add(contract.getContractNo());
                        }
                    }
                } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S,contract.getContractType())) {
                    sellIdList.add(contract.getId());
                    sellContractNoList.add(contract.getContractNo());
                }
            }
            Map<Long, List<RptWorkbenchContract>> contractApproveIdMap = contractList.stream().collect(Collectors.groupingBy(RptWorkbenchContract::getApproveId));

            newSearchVo.setContractIdList(buyIdList);

            Map<Long, RptWorkbenchApprove> buySealMap = new HashMap<>();
            Map<Long, RptWorkbenchApprove> buyPayMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(buyIdList) && buyIdList.size() > 0) {
                // 供应商双签
                List<RptWorkbenchApprove> buySealApproveList = businessManagerWorkbenchMapper.findBuySealApprove(newSearchVo);
                if (CollectionUtils.isNotEmpty(buySealApproveList)) {
                    for (RptWorkbenchApprove workbenchApprove : buySealApproveList) {
                        if (StringUtils.equals(BasConstants.APPROVE_STATUS_A, workbenchApprove.getBuySealStatus())) {
                            workbenchApprove.setBuySealApproveUserName(UserCache.getUserName(workbenchApprove.getCurrApproveUserId()));
                        }
                    }
                    buySealMap = buySealApproveList.stream().collect(Collectors.toMap(RptWorkbenchApprove::getContractId, approve -> approve, (existing, replacement) -> replacement));
                }
                // 供应商付款
                List<RptWorkbenchApprove> buyPayApproveList = businessManagerWorkbenchMapper.findBuyPayApprove(newSearchVo);
                if (CollectionUtils.isNotEmpty(buySealApproveList)) {
                    for (RptWorkbenchApprove workbenchApprove : buyPayApproveList) {
                        if (StringUtils.equals(BasConstants.APPROVE_STATUS_A, workbenchApprove.getBuyPayStatus())) {
                            workbenchApprove.setBuyPayApproveUserName(UserCache.getUserName(workbenchApprove.getCurrApproveUserId()));
                        }
                    }
                    buyPayMap = buyPayApproveList.stream().collect(Collectors.toMap(
                            RptWorkbenchApprove::getContractId,  // 使用 contractId 作为键
                            approve -> approve,  // 使用 RptWorkbenchApprove 对象作为值
                            (existing, replacement) -> {
                                // 如果 status 为 'A' 且 created_date 更晚，则选择 replacement
                                if (BasConstants.APPROVE_STATUS_A.equals(replacement.getBuyPayStatus()) &&
                                        (existing.getBuyPayStatus() == null || !existing.getBuyPayStatus().equals("A") ||
                                                replacement.getCreatedDate().after(existing.getCreatedDate()))) {
                                    return replacement;
                                }
                                // 否则保留 existing
                                return existing;
                            }
                    ));

//                    buyPayMap = buyPayApproveList.stream().collect(Collectors.toMap(RptWorkbenchApprove::getContractId, approve -> approve));
                }
            }

            newSearchVo.setContractIdList(sellIdList);
            Map<Long, RptWorkbenchApprove> sellSealMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(sellIdList)) {
                // 客户双签
                List<RptWorkbenchApprove> sellSealApproveList = businessManagerWorkbenchMapper.findSellSealApprove(newSearchVo);
                if (CollectionUtils.isNotEmpty(sellSealApproveList)) {
                    for (RptWorkbenchApprove workbenchApprove : sellSealApproveList) {
                        if (StringUtils.equals(BasConstants.APPROVE_STATUS_A, workbenchApprove.getSellSealStatus())) {
                            workbenchApprove.setSellSealApproveUserName(UserCache.getUserName(workbenchApprove.getCurrApproveUserId()));
                        }
                    }
                    sellSealMap = sellSealApproveList.stream().collect(Collectors.toMap(RptWorkbenchApprove::getContractId, approve -> approve, (existing, replacement) -> replacement));
                }
            }

            for (RptWorkbenchApprove workbenchApprove : contractApproveList) {
                List<RptWorkbenchContract> conList = contractApproveIdMap.get(workbenchApprove.getApproveId());
                if (CollectionUtils.isNotEmpty(conList)) {
                    for (RptWorkbenchContract contract : conList) {
                        if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contract.getContractType())) {
                            Long contractId = contract.getId();
                            Long virtualContractId = contract.getVirtualContractId();
                            if (virtualContractId != null) {
                                contractId = virtualContractId;
                            }
                            RptWorkbenchApprove buySealApprove = buySealMap.get(contractId);
                            if (Objects.nonNull(buySealApprove)) {
                                workbenchApprove.setBuySealStatus(buySealApprove.getBuySealStatus());
                                workbenchApprove.setBuySealApproveUserName(buySealApprove.getBuySealApproveUserName());
                                workbenchApprove.setBuySealApproveLastTime(buySealApprove.getBuySealApproveLastTime());
                                workbenchApprove.setBuySealApproveId(buySealApprove.getApproveId());
                                workbenchApprove.setBuySealBudgetName(buySealApprove.getBudgetName());
                            }
                            RptWorkbenchApprove buyPayApprove = buyPayMap.get(contractId);
                            if (Objects.nonNull(buyPayApprove)) {
                                workbenchApprove.setBuyPayStatus(buyPayApprove.getBuyPayStatus());
                                workbenchApprove.setBuyPayApproveUserName(buyPayApprove.getBuyPayApproveUserName());
                                workbenchApprove.setBuyPayApproveLastTime(buyPayApprove.getBuyPayApproveLastTime());
                                workbenchApprove.setBuyPayApproveId(buyPayApprove.getApproveId());
                                workbenchApprove.setBuyPayBudgetName(buyPayApprove.getBudgetName());
                            }
                        } else {
                            RptWorkbenchApprove sellSealApprove = sellSealMap.get(contract.getId());
                            if (Objects .nonNull(sellSealApprove)) {
                                workbenchApprove.setSellSealStatus(sellSealApprove.getSellSealStatus());
                                workbenchApprove.setSellSealApproveUserName(sellSealApprove.getSellSealApproveUserName());
                                workbenchApprove.setSellSealApproveLastTime(sellSealApprove.getSellSealApproveLastTime());
                                workbenchApprove.setSellSealApproveId(sellSealApprove.getApproveId());
                                workbenchApprove.setSellSealBudgetName(sellSealApprove.getBudgetName());
                            }
                        }
                    }
                }
            }

        }
        
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptWorkbenchApprove> pageVo = new PageImpl<>(contractApproveList, pageable, searchVo.getCount());
        return pageVo;
    }

    /**
     * 查询企业 统计
     * @param searchVo
     * @return
     */
    @Override
    public List<RptWorkbenchContractStatist> findCompanyStatistList(RptBusinessManagerWorkbenchSearchVo searchVo) {
        List<RptWorkbenchContractStatist> list = new ArrayList<>();
        // 本月新增
        Page<RptWorkbenchCompany> newCompanyPage = findNewCompanyPage(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.XZ.getLabelCode(), WorkbenchLabelEnum.XZ.getLabelName(), newCompanyPage.getTotalElements()));
        // 待保险批复
        Page<RptWorkbenchCompany> piccCompanyPage = findPiccCompanyPage(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.BX.getLabelCode(), WorkbenchLabelEnum.BX.getLabelName(), piccCompanyPage.getTotalElements()));
        // 活跃企业
        Page<RptWorkbenchCompany> hyCompanyPage = findHyCompanyPage(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.HY.getLabelCode(), WorkbenchLabelEnum.HY.getLabelName(), hyCompanyPage.getTotalElements()));
        return list;
    }

    /**
     * 查询供应商 统计
     * @param searchVo
     * @return
     */
    @Override
    public List<RptWorkbenchContractStatist> findSupplierStatistList(RptBusinessManagerWorkbenchSearchVo searchVo) {
        List<RptWorkbenchContractStatist> list = new ArrayList<>();
        // 本月新增
        Page<RptWorkbenchCompany> newCompanyPage = findNewCompanyPage(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.XZ.getLabelCode(), WorkbenchLabelEnum.XZ.getLabelName(), newCompanyPage.getTotalElements()));
        // 活跃企业
        Page<RptWorkbenchCompany> hyCompanyPage = findHyCompanyPage(searchVo);
        list.add(new RptWorkbenchContractStatist(WorkbenchLabelEnum.HY.getLabelCode(), WorkbenchLabelEnum.HY.getLabelName(), hyCompanyPage.getTotalElements()));
        return list;
    }

    /**
     * 查询企业新增 数据详情
     * @param searchVo
     * @return
     */
    @Override
    public Page<RptWorkbenchCompany> findNewCompanyPage(RptBusinessManagerWorkbenchSearchVo searchVo) {
        
        if (searchVo.getMonthFlag()) {
            String month = DateUtil.format(new Date(), "yyyy-MM");
            searchVo.setMonth(month);
        } else if (searchVo.getYearFlag()) {
            String year = DateUtil.format(new Date(), "yyyy");
            searchVo.setYear(year);
        }
        List<RptWorkbenchCompany> list = businessManagerWorkbenchMapper.findNewCompanyList(searchVo);
        if (StringUtils.equals(BasConstants.DICT_TYPE_COMPANYTYPE_I, searchVo.getCompanyType())) {
            if (CollectionUtils.isNotEmpty(list)) {
                // 提取 ID 列表
                List<Long> idList = list.stream().map(RptWorkbenchCompany::getId).collect(Collectors.toList());
                searchVo.setCompanyIdList(idList);
                RptBusinessManagerWorkbenchSearchVo newSearchVo = new RptBusinessManagerWorkbenchSearchVo();
                BeanUtils.copyProperties(searchVo, newSearchVo);
                newSearchVo.setPage(1);
                newSearchVo.setRows(100);
                if (CollectionUtils.isNotEmpty(idList) && idList.size() > 0) {
                    List<RptWorkbenchCompany> companyVisitList = businessManagerWorkbenchMapper.findCompanyVisitList(newSearchVo);
                    if (CollectionUtils.isNotEmpty(companyVisitList)) {
                        Map<Long, RptWorkbenchCompany> companyMap = companyVisitList.stream().collect(Collectors.toMap(RptWorkbenchCompany::getId, company -> company));
                        for (RptWorkbenchCompany company : list) {
                            RptWorkbenchCompany workbenchCompany = companyMap.get(company.getId());
                            if (Objects.nonNull(workbenchCompany)) {
                                company.setRecommendedAmount(workbenchCompany.getRecommendedAmount());
                            }
                        }
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptWorkbenchCompany> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }

    /**
     * 查询人保待批复 数据详情
     * @param searchVo
     * @return
     */
    @Override
    public Page<RptWorkbenchCompany> findPiccCompanyPage(RptBusinessManagerWorkbenchSearchVo searchVo) {
        List<RptWorkbenchCompany> list = businessManagerWorkbenchMapper.findPiccCompanyList(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptWorkbenchCompany> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }

    /**
     * 查询活跃企业信息
     * @param searchVo
     * @return
     */
    @Override
    public Page<RptWorkbenchCompany> findHyCompanyPage(RptBusinessManagerWorkbenchSearchVo searchVo) {
        List<RptWorkbenchCompany> list = businessManagerWorkbenchMapper.findHyCompanyList(searchVo);

        if (CollectionUtils.isNotEmpty(list)) {
            RptBusinessManagerWorkbenchSearchVo newSearchVo = new RptBusinessManagerWorkbenchSearchVo();
            BeanUtils.copyProperties(searchVo, newSearchVo);
            newSearchVo.setPage(1);
            newSearchVo.setRows(100);
            List<Long> idList = list.stream().map(RptWorkbenchCompany::getId).collect(Collectors.toList());
            newSearchVo.setCompanyIdList(idList);
            
            if (CollectionUtils.isNotEmpty(idList) && idList.size() > 0) {
                // 授信信息
                List<RptWorkbenchCompanyCredit> companyCreditList = businessManagerWorkbenchMapper.findCompanyCreditList(newSearchVo);
                Map<Long, List<RptWorkbenchCompanyCredit>> creditMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(companyCreditList)) {
                    creditMap = companyCreditList.stream().collect(Collectors.groupingBy(RptWorkbenchCompanyCredit::getCompanyId));
                }

                List<RptWorkbenchCompany> contractInfoList = businessManagerWorkbenchMapper.findContractInfoList(newSearchVo);
                Map<Long, RptWorkbenchCompany> contractMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(contractInfoList)) {
                    contractMap = contractInfoList.stream().collect(Collectors.toMap(RptWorkbenchCompany::getId, contract -> contract));
                }
                for (RptWorkbenchCompany company : list) {
                    List<RptWorkbenchCompanyCredit> creditList = creditMap.get(company.getId());
                    company.setCreditInfo(handelCreditInfo(creditList));
                    RptWorkbenchCompany contract = contractMap.get(company.getId());
                    if (Objects.nonNull(contract)) {
                        company.setFirstContractTime(contract.getFirstContractTime());
                        company.setLastContractTime(contract.getLastContractTime());
                        company.setTotalContractAmount(contract.getTotalContractAmount());
                    }
                }
            }
        }
        
        
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptWorkbenchCompany> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }

    public String handelCreditInfo(List<RptWorkbenchCompanyCredit> companyCreditList){
        StringBuilder sb = new StringBuilder();
        // 查询授信额度表
        if (CollectionUtils.isNotEmpty(companyCreditList)) {
            Map<String, RptWorkbenchCompanyCredit> companyCreditMap = companyCreditList.stream()
                    .collect(Collectors.toMap(RptWorkbenchCompanyCredit::getCreditType, m -> m, (a, b) -> b));
            // 人保额度
            RptWorkbenchCompanyCredit piccCompanyCredit = companyCreditMap.get(BasConstants.CREDIT_TYPE_0);
            // 大地额度
            RptWorkbenchCompanyCredit daDiCompanyCredit = companyCreditMap.get(BasConstants.CREDIT_TYPE_1);
            // 自主额度
            RptWorkbenchCompanyCredit ziZhuCompanyCredit = companyCreditMap.get(BasConstants.CREDIT_TYPE_9);
            boolean piccFlg = false;

            if (Objects.nonNull(piccCompanyCredit)) {
                piccFlg = true;
                sb.append(spliceCreditInfo(BasConstants.CREDIT_TYPE_NAME_0, piccCompanyCredit));
            }
            boolean daDiFlg = false;
            if (Objects.nonNull(daDiCompanyCredit)) {
                daDiFlg = true;
                if (piccFlg) {
                    sb.append("<br>");
                }
                sb.append(spliceCreditInfo(BasConstants.CREDIT_TYPE_NAME_1, daDiCompanyCredit));
            }
            if (Objects.nonNull(ziZhuCompanyCredit)) {
                if (daDiFlg || piccFlg) {
                    sb.append("<br>");
                }
                sb.append(spliceCreditInfo(BasConstants.CREDIT_TYPE_NAME_9, ziZhuCompanyCredit));
            }
        }
        return sb.toString();
    }
    public String spliceCreditInfo(String creditTYpe, RptWorkbenchCompanyCredit companyCredit){
        StringBuilder sb = new StringBuilder();
        // 授信额度
        BigDecimal creditAmount = companyCredit.getRiskAmount();
        BigDecimal creditAmountW = companyCredit.getRiskAmount().divide(new BigDecimal(10000));
        // 已用额度
        BigDecimal usedCreditAmount = companyCredit.getUsedCreditAmount();
        BigDecimal usedCreditAmountW = companyCredit.getUsedCreditAmount().divide(new BigDecimal(10000));
        // 临时额度
        BigDecimal temporaryAmount = companyCredit.getTemporaryAmount();
        BigDecimal temporaryAmountW = companyCredit.getTemporaryAmount().divide(new BigDecimal(10000));
        // 剩余额度
        BigDecimal availableCreditAmountW = (creditAmount.add(temporaryAmount).subtract(usedCreditAmount)).divide(new BigDecimal(10000));

        sb.append(creditTYpe).append(" ").append(creditAmountW).append(" 万");
        sb.append("，已用 ").append(usedCreditAmountW).append(" 万");
        sb.append("，剩余 ").append(availableCreditAmountW).append(" 万");
        sb.append("，临时 ").append(temporaryAmountW).append(" 万");
        return sb.toString();
    }


    /**
     * 获取过去5个月的月份数据
     *
     * @return 过去5个月的月份数据
     */
    private List<String> getPreviousFiveMonths() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter patten = DateTimeFormatter.ofPattern("yyyy-MM");
        List<String> result = new ArrayList<>();
        for (int i = 4; i >= 0; i--) {
            result.add(now.plusMonths(i * (-1)).format(patten));
        }
        return result;
    }

    /**
     * 拼凑5个月的数据，没有的直接置为0
     *
     * @param personalAchievementList 数据
     * @param fiveMonths              5个月
     * @return
     */
    private List<RptPersonalAchievement> getFiveGrossProfit(List<RptPersonalAchievement> personalAchievementList, List<String> fiveMonths) {
        Map<String, BigDecimal> fiveGrossProfitMap = personalAchievementList.stream().collect(Collectors.toMap(RptPersonalAchievement::getMonth, RptPersonalAchievement::getGrossProfitAmount, (a, b) -> b));
        return fiveMonths.stream().map(covertGrossEntity(fiveGrossProfitMap)).collect(Collectors.toList());
    }

    /**
     * 转化为RptPersonalAchievement
     *
     * @param fiveGrossProfitMap 月份数据集
     * @return 5个月数据集
     */
    private Function<String, RptPersonalAchievement> covertGrossEntity(Map<String, BigDecimal> fiveGrossProfitMap) {
        return e -> {
            RptPersonalAchievement personalAchievement = new RptPersonalAchievement();
            personalAchievement.setMonth(e);
            personalAchievement.setGrossProfitAmount(fiveGrossProfitMap.getOrDefault(e, BigDecimal.ZERO));
            return personalAchievement;
        };
    }

}
