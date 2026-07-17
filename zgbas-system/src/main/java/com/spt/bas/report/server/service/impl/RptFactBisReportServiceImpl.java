package com.spt.bas.report.server.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.utils.DateUtils;
import com.spt.bas.report.client.vo.RptFactBisBusinessAnalysisSearchVo;
import com.spt.bas.report.client.vo.RptFactBisHumanResourceCostsAnalysisSearchVo;
import com.spt.bas.report.server.dao.RptFactBisReportMapper;
import com.spt.bas.report.server.service.IRptFactBisReportService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RptFactBisReportServiceImpl implements IRptFactBisReportService {

    @Autowired
    private RptFactBisReportMapper factBisReportMapper;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public RptFactBisModifiedBusinessVo findModifiedBusiness(RptFactBisBusinessAnalysisSearchVo vo) {
        // 月份查询参数 ：2026-03
        String month = vo.getMonth();
        //查询参数获取上查询参数获取上月开始时间上月结束时间
        if (StringUtils.isNotBlank(month)) {
            Date monthDate = DateUtils.parseDate(month + "-01");
            vo.setSealStartDate(DateUtils.getBeginDayOfMonth(monthDate));
            vo.setSealEndDate(DateUtils.getEndDayOfMonth(monthDate));
        }
        // 查询月数据
        RptFactBisModifiedBusinessVo modifiedBusiness = factBisReportMapper.findModifiedBusiness(vo);
        if (Objects.isNull(modifiedBusiness)) {
            modifiedBusiness = new RptFactBisModifiedBusinessVo();
        }
        RptFactBisModifiedBusinessVo modifiedBusinessPay = factBisReportMapper.findModifiedBusinessPay(vo);
        if (Objects.isNull(modifiedBusinessPay)) {
            modifiedBusinessPay = new RptFactBisModifiedBusinessVo();
        }

        if (StringUtils.isNotBlank(month)) {
            Date monthDate = DateUtils.parseDate(month + "-01");
            vo.setSealStartDate(DateUtils.getBeginDayOfLastMonth(monthDate, -1));
            vo.setSealEndDate(DateUtils.getEndDayOfLastMonth(monthDate, -1));
        }
        // 上月数据
        RptFactBisModifiedBusinessVo lastModifiedBusiness = factBisReportMapper.findModifiedBusiness(vo);
        if (Objects.isNull(lastModifiedBusiness)) {
            lastModifiedBusiness = new RptFactBisModifiedBusinessVo();
        }
        RptFactBisModifiedBusinessVo lastModifiedBusinessPay = factBisReportMapper.findModifiedBusinessPay(vo);
        if (Objects.isNull(lastModifiedBusinessPay)) {
            lastModifiedBusinessPay = new RptFactBisModifiedBusinessVo();
        }


        modifiedBusiness.setLastTotalAmount(lastModifiedBusiness.getTotalAmount());
        modifiedBusiness.setLastTotalNumber(lastModifiedBusiness.getTotalNumber());
        modifiedBusiness.setLastGrossProfit(lastModifiedBusiness.getGrossProfit());
        modifiedBusiness.setLastCapitalCost(lastModifiedBusiness.getCapitalCost());
        modifiedBusiness.setLastNetMargin(lastModifiedBusiness.getNetMargin());
        
        // 同周期上年数据
        if (StringUtils.isNotBlank(month)) {
            Date monthDate = DateUtils.parseDate(month + "-01");
            vo.setSealStartDate(DateUtils.getBeginDayOfLastMonth(monthDate, -12));
            vo.setSealEndDate(DateUtils.getEndDayOfLastMonth(monthDate, -12));
        }

        // 同周期上年数据
        RptFactBisModifiedBusinessVo lastYearModifiedBusiness = factBisReportMapper.findModifiedBusiness(vo);
        if (Objects.isNull(lastYearModifiedBusiness)) {
            lastYearModifiedBusiness = new RptFactBisModifiedBusinessVo();
        }
        RptFactBisModifiedBusinessVo lastYearModifiedBusinessPay = factBisReportMapper.findModifiedBusinessPay(vo);
        if (Objects.isNull(lastYearModifiedBusinessPay)) {
            lastYearModifiedBusinessPay = new RptFactBisModifiedBusinessVo();
        }

        modifiedBusiness.setLastYearTotalAmount(lastYearModifiedBusiness.getTotalAmount());
        modifiedBusiness.setLastYearTotalNumber(lastYearModifiedBusiness.getTotalNumber());
        modifiedBusiness.setLastYearGrossProfit(lastYearModifiedBusiness.getGrossProfit());
        modifiedBusiness.setLastYearCapitalCost(lastYearModifiedBusiness.getCapitalCost());
        modifiedBusiness.setLastYearNetMargin(lastYearModifiedBusiness.getNetMargin());

        // 查询成本项
        handelBaseCost(modifiedBusiness, modifiedBusinessPay, lastModifiedBusinessPay, lastYearModifiedBusinessPay, vo);

        // 业务指标配置查询
        List<String> monthListByMonth = getMonthListByMonth(month);
        vo.setMonthList(monthListByMonth);

        vo.setTargetType("1");
        RptFactBisBusinessWorkTargetResultVo workTargetResultVo = factBisReportMapper.findWorkTarget(vo);
        if (Objects.isNull(workTargetResultVo)) {
            workTargetResultVo = new RptFactBisBusinessWorkTargetResultVo();
        }
        modifiedBusiness.setSellWorkTarget(workTargetResultVo.getTargetTotalAmount());

        vo.setTargetType("2");
        RptFactBisBusinessWorkTargetResultVo grossProfitWorkTargetResultVo = factBisReportMapper.findWorkTarget(vo);
        if (Objects.isNull(grossProfitWorkTargetResultVo)) {
            grossProfitWorkTargetResultVo = new RptFactBisBusinessWorkTargetResultVo();
        }
        modifiedBusiness.setGrossProfitWorkTarget(grossProfitWorkTargetResultVo.getTargetTotalAmount());

        // 当季销售额 毛利润
        Date quarterStartDate = DateUtils.getQuarterStartDate(month);
        Date quarterEndDate = DateUtils.getQuarterEndDate(month);
        vo.setSealStartDate(quarterStartDate);
        vo.setSealEndDate(quarterEndDate);

        RptFactBisModifiedBusinessVo quarterModifiedBusiness = factBisReportMapper.findModifiedBusiness(vo);
        if (Objects.isNull(quarterModifiedBusiness)) {
            quarterModifiedBusiness = new RptFactBisModifiedBusinessVo();
        }
        modifiedBusiness.setQuarterlySellAmount(quarterModifiedBusiness.getTotalAmount());
        modifiedBusiness.setQuarterlyGrossProfit(quarterModifiedBusiness.getGrossProfit());


        return modifiedBusiness;
    }

    /**
     * 处理成本项数据
     *
     * @param modifiedBusiness
     * @param vo
     */
    public void handelBaseCost(RptFactBisModifiedBusinessVo modifiedBusiness, RptFactBisModifiedBusinessVo modifiedBusinessPay,
                               RptFactBisModifiedBusinessVo lastModifiedBusinessPay, RptFactBisModifiedBusinessVo lastYearModifiedBusinessPay, 
                               RptFactBisBusinessAnalysisSearchVo vo) {
        // 查询成本项
        List deptIdList = vo.getDeptIdList();
        if (CollectionUtils.isNotEmpty(deptIdList)) {
            List<SysUserSdk> userList = authOpenFacade.findByDeptIds(deptIdList);
            if (CollectionUtils.isNotEmpty(userList)) {
				List<Long> userIdList = userList.stream().map(SysUserSdk::getUserId).collect(Collectors.toList());
                vo.setUserIdList(userIdList);
            }

        }
        String month = vo.getMonth();
        if (StringUtils.isNotBlank(month)) {
            vo.setMonthList(Collections.singletonList(vo.getMonth()));
        }
        RptFactBisModifiedBusinessVo modifiedBusinessCost = factBisReportMapper.findModifiedBusinessCost(vo);
		if (Objects.isNull(modifiedBusinessCost)) {
			modifiedBusinessCost = new RptFactBisModifiedBusinessVo();
		}
		
		modifiedBusiness.setSocialSecurity(modifiedBusinessCost.getSocialSecurity());
		modifiedBusiness.setEvectionAmount(modifiedBusinessCost.getEvectionAmount());
		modifiedBusiness.setPersonnelSalary(modifiedBusinessCost.getPersonnelSalary());
		modifiedBusiness.setOperatingCosts(NumberUtil.add(modifiedBusinessCost.getOperatingCosts(), modifiedBusinessPay.getOperatingCosts()));
		modifiedBusiness.setSurplus(NumberUtil.sub(modifiedBusiness.getNetMargin(), modifiedBusiness.getOperatingCosts()));

        if (StringUtils.isNotBlank(month)) {
            String lastMonth = getLastMonth(month, 1);
            vo.setMonthList(Collections.singletonList(lastMonth));
        }
        RptFactBisModifiedBusinessVo lastModifiedBusinessCost = factBisReportMapper.findModifiedBusinessCost(vo);
		if (Objects.isNull(lastModifiedBusinessCost)) {
			lastModifiedBusinessCost = new RptFactBisModifiedBusinessVo();
		}
		
		modifiedBusiness.setLastSocialSecurity(lastModifiedBusinessCost.getSocialSecurity());
		modifiedBusiness.setLastEvectionAmount(lastModifiedBusinessCost.getEvectionAmount());
        modifiedBusiness.setLastPersonnelSalary(lastModifiedBusinessCost.getPersonnelSalary());
		modifiedBusiness.setLastOperatingCosts(NumberUtil.add(lastModifiedBusinessCost.getOperatingCosts(), lastModifiedBusinessPay.getOperatingCosts()));
		modifiedBusiness.setLastSurplus(NumberUtil.sub(modifiedBusiness.getLastNetMargin(), modifiedBusiness.getLastOperatingCosts()));

        if (StringUtils.isNotBlank(month)) {
            String lastMonth = getLastMonth(month, 12);
            vo.setMonthList(Collections.singletonList(lastMonth));
        }
        RptFactBisModifiedBusinessVo lastYearModifiedBusinessCost = factBisReportMapper.findModifiedBusinessCost(vo);
        if (Objects.isNull(lastYearModifiedBusinessCost)) {
            lastYearModifiedBusinessCost = new RptFactBisModifiedBusinessVo();
        }

        modifiedBusiness.setLastYearSocialSecurity(lastYearModifiedBusinessCost.getSocialSecurity());
        modifiedBusiness.setLastYearEvectionAmount(lastYearModifiedBusinessCost.getEvectionAmount());
        modifiedBusiness.setLastYearPersonnelSalary(lastYearModifiedBusinessCost.getPersonnelSalary());
        modifiedBusiness.setLastYearOperatingCosts(NumberUtil.add(lastYearModifiedBusinessCost.getOperatingCosts(), lastYearModifiedBusinessPay.getOperatingCosts()));
        modifiedBusiness.setLastYearSurplus(NumberUtil.sub(modifiedBusiness.getLastYearNetMargin(), modifiedBusiness.getLastYearOperatingCosts()));


    }

    public static String getLastMonth(String month, int num) {
        YearMonth yearMonth = YearMonth.parse(month);
        return yearMonth.minusMonths(num).toString();
    }


    @Override
    public RptFactBisBusinessAnalysisVo findBusinessAnalysis(RptFactBisBusinessAnalysisSearchVo vo) {
        RptFactBisBusinessAnalysisVo businessAnalysisVo = new RptFactBisBusinessAnalysisVo();
        // 月份查询参数 ：2026-03
        String month = vo.getMonth();
        //查询参数获取上查询参数获取上月开始时间上月结束时间
        if (StringUtils.isNotBlank(month)) {
            Date monthDate = DateUtils.parseDate(month + "-01");
            vo.setSealStartDate(DateUtils.getBeginDayOfMonth(monthDate));
            vo.setSealEndDate(DateUtils.getEndDayOfMonth(monthDate));
        }
        vo.setMatchCreditFlg(true);
        RptFactBisBusinessAnalysisResultVo sxBusinessAnalysisResultVo = factBisReportMapper.findBusinessAnalysis(vo);
        if (Objects.isNull(sxBusinessAnalysisResultVo)) {
            sxBusinessAnalysisResultVo = new RptFactBisBusinessAnalysisResultVo();
        }
        fillResult(businessAnalysisVo, sxBusinessAnalysisResultVo, "sx");
        
        
        vo.setMatchCreditFlg(false);
        RptFactBisBusinessAnalysisResultVo dcBusinessAnalysisResultVo = factBisReportMapper.findBusinessAnalysis(vo);
        if (Objects.isNull(dcBusinessAnalysisResultVo)) {
            dcBusinessAnalysisResultVo = new RptFactBisBusinessAnalysisResultVo();
        }
        fillResult(businessAnalysisVo, dcBusinessAnalysisResultVo, "dc");

        if (StringUtils.isNotBlank(month)) {
            vo.setMonthList(Collections.singletonList(vo.getMonth()));
        }
        RptFactBisModifiedBusinessVo modifiedBusinessCost = factBisReportMapper.findModifiedBusinessCost(vo);
        if (Objects.isNull(modifiedBusinessCost)) {
            modifiedBusinessCost = new RptFactBisModifiedBusinessVo();
        }
        businessAnalysisVo.setSocialSecurity(modifiedBusinessCost.getSocialSecurity());
        businessAnalysisVo.setEvectionAmount(modifiedBusinessCost.getEvectionAmount());
        businessAnalysisVo.setPersonnelSalary(modifiedBusinessCost.getPersonnelSalary());


        // 上月数据查询
        if (StringUtils.isNotBlank(month)) {
            Date monthDate = DateUtils.parseDate(month + "-01");
            vo.setSealStartDate(DateUtils.getBeginDayOfLastMonth(monthDate, -1));
            vo.setSealEndDate(DateUtils.getEndDayOfLastMonth(monthDate, -1));
        }
        vo.setMatchCreditFlg(true);
        RptFactBisBusinessAnalysisResultVo lastSxBusinessAnalysisResultVo = factBisReportMapper.findBusinessAnalysis(vo);
        if (Objects.isNull(lastSxBusinessAnalysisResultVo)) {
            lastSxBusinessAnalysisResultVo = new RptFactBisBusinessAnalysisResultVo();
        }
        fillResult(businessAnalysisVo, lastSxBusinessAnalysisResultVo, "lastSx");
        
        vo.setMatchCreditFlg(false);
        RptFactBisBusinessAnalysisResultVo lastDcBusinessAnalysisResultVo = factBisReportMapper.findBusinessAnalysis(vo);
        if (Objects.isNull(lastDcBusinessAnalysisResultVo)) {
            lastDcBusinessAnalysisResultVo = new RptFactBisBusinessAnalysisResultVo();
        }
        fillResult(businessAnalysisVo, lastDcBusinessAnalysisResultVo, "lastDc");

        if (StringUtils.isNotBlank(month)) {
            String lastMonth = getLastMonth(month, 1);
            vo.setMonthList(Collections.singletonList(lastMonth));
        }
        RptFactBisModifiedBusinessVo lastModifiedBusinessCost = factBisReportMapper.findModifiedBusinessCost(vo);
        if (Objects.isNull(lastModifiedBusinessCost)) {
            lastModifiedBusinessCost = new RptFactBisModifiedBusinessVo();
        }
        businessAnalysisVo.setLastSocialSecurity(lastModifiedBusinessCost.getSocialSecurity());
        businessAnalysisVo.setLastEvectionAmount(lastModifiedBusinessCost.getEvectionAmount());
        businessAnalysisVo.setLastPersonnelSalary(lastModifiedBusinessCost.getPersonnelSalary());


        // 同期上年数据查询
        if (StringUtils.isNotBlank(month)) {
            Date monthDate = DateUtils.parseDate(month + "-01");
            vo.setSealStartDate(DateUtils.getBeginDayOfLastMonth(monthDate, -12));
            vo.setSealEndDate(DateUtils.getEndDayOfLastMonth(monthDate, -12));
        }
        vo.setMatchCreditFlg(true);
        RptFactBisBusinessAnalysisResultVo lastYearSxBusinessAnalysisResultVo = factBisReportMapper.findBusinessAnalysis(vo);
        if (Objects.isNull(lastYearSxBusinessAnalysisResultVo)) {
            lastYearSxBusinessAnalysisResultVo = new RptFactBisBusinessAnalysisResultVo();
        }
        fillResult(businessAnalysisVo, lastYearSxBusinessAnalysisResultVo, "lastYearSx");

        vo.setMatchCreditFlg(false);
        RptFactBisBusinessAnalysisResultVo lastYearDcBusinessAnalysisResultVo = factBisReportMapper.findBusinessAnalysis(vo);
        if (Objects.isNull(lastYearDcBusinessAnalysisResultVo)) {
            lastYearDcBusinessAnalysisResultVo = new RptFactBisBusinessAnalysisResultVo();
        }
        fillResult(businessAnalysisVo, lastYearDcBusinessAnalysisResultVo, "lastYearDc");

        if (StringUtils.isNotBlank(month)) {
            String lastMonth = getLastMonth(month, 12);
            vo.setMonthList(Collections.singletonList(lastMonth));
        }
        RptFactBisModifiedBusinessVo lastYearModifiedBusinessCost = factBisReportMapper.findModifiedBusinessCost(vo);
        if (Objects.isNull(lastYearModifiedBusinessCost)) {
            lastYearModifiedBusinessCost = new RptFactBisModifiedBusinessVo();
        }
        businessAnalysisVo.setLastYearSocialSecurity(lastYearModifiedBusinessCost.getSocialSecurity());
        businessAnalysisVo.setLastYearEvectionAmount(lastYearModifiedBusinessCost.getEvectionAmount());
        businessAnalysisVo.setLastYearPersonnelSalary(lastYearModifiedBusinessCost.getPersonnelSalary());
        
        // 业务指标配置查询
        List<String> monthListByMonth = getMonthListByMonth(month);
        vo.setMonthList(monthListByMonth);
        
        vo.setTargetType("1");
        RptFactBisBusinessWorkTargetResultVo workTargetResultVo = factBisReportMapper.findWorkTarget(vo);
        if (Objects.isNull(workTargetResultVo)) {
            workTargetResultVo = new RptFactBisBusinessWorkTargetResultVo();
        }
        businessAnalysisVo.setSellWorkTarget(workTargetResultVo.getTargetTotalAmount());
        
        vo.setTargetType("2");
        RptFactBisBusinessWorkTargetResultVo grossProfitWorkTargetResultVo = factBisReportMapper.findWorkTarget(vo);
        if (Objects.isNull(grossProfitWorkTargetResultVo)) {
            grossProfitWorkTargetResultVo = new RptFactBisBusinessWorkTargetResultVo();
        }
        businessAnalysisVo.setGrossProfitWorkTarget(grossProfitWorkTargetResultVo.getTargetTotalAmount());
        
        // 当季销售额 毛利润
        Date quarterStartDate = DateUtils.getQuarterStartDate(month);
        Date quarterEndDate = DateUtils.getQuarterEndDate(month);
        vo.setMatchCreditFlg(null);
        vo.setSealStartDate(quarterStartDate);
        vo.setSealEndDate(quarterEndDate);
        RptFactBisBusinessAnalysisResultVo quarterBusinessAnalysisResultVo = factBisReportMapper.findBusinessAnalysis(vo);
        if (Objects.isNull(quarterBusinessAnalysisResultVo)) {
            quarterBusinessAnalysisResultVo = new RptFactBisBusinessAnalysisResultVo();
        }
        businessAnalysisVo.setQuarterlySellAmount(quarterBusinessAnalysisResultVo.getTotalAmount());
        businessAnalysisVo.setQuarterlyGrossProfit(quarterBusinessAnalysisResultVo.getGrossProfit());
        
        
        return businessAnalysisVo;
    }

    /**
     * 根据月份例如：2026-04 获取所在季度全部月份list 例如：2026-04 2026-05 2026-06
     */
    private List<String> getMonthListByMonth(String month) {
        if (StringUtils.isBlank(month)) {
            return Collections.emptyList();
        }
        YearMonth ym = YearMonth.parse(month);

        int currentMonth = ym.getMonthValue();

        // 计算季度起始月
        int startMonth = ((currentMonth - 1) / 3) * 3 + 1;

        List<String> result = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            YearMonth m = YearMonth.of(ym.getYear(), startMonth + i);
            result.add(m.toString()); // 默认 yyyy-MM
        }

        return result;
        
    }
    

    private void fillResult(RptFactBisBusinessAnalysisVo result, RptFactBisBusinessAnalysisResultVo source, String prefix) {

        BigDecimal capitalCost = result.getCapitalCost();
        BigDecimal lastCapitalCost = result.getLastCapitalCost();
        BigDecimal lastYearCapitalCost = result.getLastYearCapitalCost();

        if ("sx".equals(prefix)) {
            result.setSxTotalAmount(source.getTotalAmount());
            result.setSxTotalNumber(source.getTotalNumber());
            result.setSxGrossProfit(source.getGrossProfit());
            capitalCost = NumberUtil.add(capitalCost, source.getCapitalCost());
        } else if ("dc".equals(prefix)) {
            result.setDcTotalAmount(source.getTotalAmount());
            result.setDcTotalNumber(source.getTotalNumber());
            result.setDcGrossProfit(source.getGrossProfit());
            capitalCost = NumberUtil.add(capitalCost, source.getCapitalCost());
        } else if ("lastSx".equals(prefix)) {
            result.setLastSxTotalAmount(source.getTotalAmount());
            result.setLastSxTotalNumber(source.getTotalNumber());
            result.setLastSxGrossProfit(source.getGrossProfit());
            lastCapitalCost = NumberUtil.add(lastCapitalCost, source.getCapitalCost());
        } else if ("lastDc".equals(prefix)) {
            result.setLastDcTotalAmount(source.getTotalAmount());
            result.setLastDcTotalNumber(source.getTotalNumber());
            result.setLastDcGrossProfit(source.getGrossProfit());
            lastCapitalCost = NumberUtil.add(lastCapitalCost, source.getCapitalCost());
        } else if ("lastYearSx".equals(prefix)) {
            result.setLastYearSxTotalAmount(source.getTotalAmount());
            result.setLastYearSxTotalNumber(source.getTotalNumber());
            result.setLastYearSxGrossProfit(source.getGrossProfit());
            lastYearCapitalCost = NumberUtil.add(lastYearCapitalCost, source.getCapitalCost());
        } else if ("lastYearDc".equals(prefix)) {
            result.setLastYearDcTotalAmount(source.getTotalAmount());
            result.setLastYearDcTotalNumber(source.getTotalNumber());
            result.setLastYearDcGrossProfit(source.getGrossProfit());
            lastYearCapitalCost = NumberUtil.add(lastYearCapitalCost, source.getCapitalCost());
        }
        
        result.setCapitalCost(capitalCost);
        result.setLastCapitalCost(lastCapitalCost);
        result.setLastYearCapitalCost(lastYearCapitalCost);
    }
    
    @Override
    public RptFactBisHumanResourceCostsAnalysisVo findHumanResourceCostsAnalysis(RptFactBisHumanResourceCostsAnalysisSearchVo vo) {
        String month = vo.getMonth();
        // 查询月
        if (StringUtils.isNotBlank(month)) {
            Date monthDate = DateUtils.parseDate(month + "-01");
            vo.setStartDate(DateUtils.getBeginDayOfMonth(monthDate));
            vo.setEndDate(DateUtils.getEndDayOfMonth(monthDate));
        }
        List<Long> deptIdList = vo.getDeptIdList();
        List<Long> userIdList = vo.getUserIdList();
        if (CollectionUtils.isEmpty(userIdList)) {
            userIdList = new ArrayList<>();
        }
        if (CollectionUtils.isNotEmpty(deptIdList)) {
            List<SysUserSdk> userSdkList = authOpenFacade.findByDeptIds(deptIdList);
            if (CollectionUtils.isNotEmpty(userSdkList)) {
                for (SysUserSdk sysUserSdk : userSdkList) {
                    userIdList.add(sysUserSdk.getUserId());
                }
            }
            vo.setUserIdList(userIdList);
        }


        RptFactBisHumanResourceCostsAnalysisVo humanResourceCostsAnalysis = factBisReportMapper.findHumanResourceCostsAnalysis(vo);
        
        // 上月
        if (StringUtils.isNotBlank(month)) {
            Date monthDate = DateUtils.parseDate(month + "-01");
            vo.setStartDate(DateUtils.getBeginDayOfLastMonth(monthDate, -1));
            vo.setEndDate(DateUtils.getEndDayOfLastMonth(monthDate, -1));
        }
        RptFactBisHumanResourceCostsAnalysisVo lastHumanResourceCostsAnalysis = factBisReportMapper.findHumanResourceCostsAnalysis(vo);
        humanResourceCostsAnalysis.setLastRecruitmentCosts(lastHumanResourceCostsAnalysis.getRecruitmentCosts());
        humanResourceCostsAnalysis.setLastTrainingCosts(lastHumanResourceCostsAnalysis.getTrainingCosts());
        humanResourceCostsAnalysis.setLastRentPropertyCosts(lastHumanResourceCostsAnalysis.getRentPropertyCosts());
        humanResourceCostsAnalysis.setLastOfficeCosts(lastHumanResourceCostsAnalysis.getOfficeCosts());
        humanResourceCostsAnalysis.setLastBusinessEntertainmentCosts(lastHumanResourceCostsAnalysis.getBusinessEntertainmentCosts());

        // 同周期上年数据
        if (StringUtils.isNotBlank(month)) {
            Date monthDate = DateUtils.parseDate(month + "-01");
            vo.setStartDate(DateUtils.getBeginDayOfLastMonth(monthDate, -12));
            vo.setEndDate(DateUtils.getEndDayOfLastMonth(monthDate, -12));
        }
        RptFactBisHumanResourceCostsAnalysisVo lastYearHumanResourceCostsAnalysis = factBisReportMapper.findHumanResourceCostsAnalysis(vo);
        humanResourceCostsAnalysis.setLastYearRecruitmentCosts(lastYearHumanResourceCostsAnalysis.getRecruitmentCosts());
        humanResourceCostsAnalysis.setLastYearTrainingCosts(lastYearHumanResourceCostsAnalysis.getTrainingCosts());
        humanResourceCostsAnalysis.setLastYearRentPropertyCosts(lastYearHumanResourceCostsAnalysis.getRentPropertyCosts());
        humanResourceCostsAnalysis.setLastYearOfficeCosts(lastYearHumanResourceCostsAnalysis.getOfficeCosts());
        humanResourceCostsAnalysis.setLastYearBusinessEntertainmentCosts(lastYearHumanResourceCostsAnalysis.getBusinessEntertainmentCosts());
        
        
        // 获取在编人数-前台
        List<Long> frontDeptIdList = vo.getFrontDeptIdList();
        if (CollectionUtils.isNotEmpty(frontDeptIdList)) {
            List<SysUserSdk> userList = authOpenFacade.findByDeptIds(vo.getFrontDeptIdList());
            if (CollectionUtils.isNotEmpty(userList)) {
                humanResourceCostsAnalysis.setFrontPersonCount(userList.size());
            }
        }
        
        // 获取在编人数-中台
        List<Long> middleDeptIdList = vo.getMiddleDeptIdList();
        if (CollectionUtils.isNotEmpty(middleDeptIdList)) {
            List<SysUserSdk> userList = authOpenFacade.findByDeptIds(vo.getMiddleDeptIdList());
            if (CollectionUtils.isNotEmpty(userList)) {
                humanResourceCostsAnalysis.setMiddlePersonCount(userList.size());
            }
        }
        
        // 获取在编人数-后台
        List<Long> backDeptIdList = vo.getBackDeptIdList();
        if (CollectionUtils.isNotEmpty(backDeptIdList)) {
            List<SysUserSdk> userList = authOpenFacade.findByDeptIds(vo.getBackDeptIdList());
            if (CollectionUtils.isNotEmpty(userList)) {
                humanResourceCostsAnalysis.setBackPersonCount(userList.size());
            }
        }
        

        return humanResourceCostsAnalysis;
    }
    
    
}
