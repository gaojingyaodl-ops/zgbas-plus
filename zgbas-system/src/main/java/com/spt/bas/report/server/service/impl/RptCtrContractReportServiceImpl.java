package com.spt.bas.report.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Splitter;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.client.vo.DcsxShowVo;
import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrContractReport;
import com.spt.bas.report.client.entity.RptCtrContractWarnReport;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.dao.RptBsCompanyMapper;
import com.spt.bas.report.server.dao.RptCtrContractReportMapper;
import com.spt.bas.report.server.service.IRptCtrContractReportService;
import com.spt.bas.report.server.service.IRptSummaryRoiService;
import com.spt.bas.report.server.service.IRptUserRoiService;
import com.spt.bas.report.server.util.ReportCalculateUtil;
import com.spt.tools.core.json.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RptCtrContractReportServiceImpl implements IRptCtrContractReportService {
    private static final Logger log = LoggerFactory.getLogger(RptCtrContractReportServiceImpl.class);

    @Autowired
    private RptCtrContractReportMapper ctrContractReportMapper;
    @Autowired
    private ReportCalculateUtil reportCalculateUtil;
    @Value("${file.show.url}")
    private String fileShowUrl;
    @Value("${file.download.url}")
    private String fileDownLoadUrl;
    @Autowired
    private IRptUserRoiService userRoiService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IRptSummaryRoiService summaryRoiService;
    @Autowired
    private RptBsCompanyMapper bsCompanyDao;
    
    @Override
    public Page<RptCtrContractReport> findNotDeliveryInPage(RptCtrContractReportSearchVo searchVo) {
        String searchType = searchVo.getSearchType();
        List<RptCtrContractReport> list = new ArrayList<RptCtrContractReport>();
        if (StringUtils.equals("WI", searchType)) {
            list = ctrContractReportMapper.findWasDeliveryInPage(searchVo);
        } else if (StringUtils.equals("RO", searchType)) {
            list = ctrContractReportMapper.findReceiveAndNotOutPage(searchVo);
        } else {
            list = ctrContractReportMapper.findNotDeliveryInPage(searchVo);
        }
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptCtrContractReport> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }

    @Override
    public RptCtrContractReport findNotDeliveryInPageSum(RptCtrContractReportSearchVo searchVo) {
        searchVo.setCount(-1);
        String searchType = searchVo.getSearchType();
        RptCtrContractReport reportSum = null;
        if (StringUtils.equals("WI", searchType)) {
            reportSum = ctrContractReportMapper.findWasDeliveryInPageSum(searchVo);
        } else if (StringUtils.equals("RO", searchType)) {
            reportSum = ctrContractReportMapper.findReceiveAndNotOutPageSum(searchVo);
        } else {
            reportSum = ctrContractReportMapper.findNotDeliveryInPageSum(searchVo);
        }
        if (reportSum == null) {
            reportSum = new RptCtrContractReport();
        }
        return reportSum;
    }

    @Override
    public Page<RptCtrContractReport> findPreSellPage(RptCtrContractReportSearchVo searchVo) {
        List<RptCtrContractReport> list = ctrContractReportMapper.findPreSellPage(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptCtrContractReport> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }

    @Override
    public Page<RptCtrContractReport> findSXReceivePage(RptCtrContractReportSearchVo searchVo) {
        List<RptCtrContractReport> list = ctrContractReportMapper.findSXReceivePage(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptCtrContractReport> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }

    @Override
    public Page<RptCtrProfitVo> findProfitPage(RptCtrProfitSearchVo ctrProfitSearchVo) {
        List<RptCtrProfitVo> profitPage = ctrContractReportMapper.findProfitPage(ctrProfitSearchVo);

        Date contractTimeStart = ctrProfitSearchVo.getContractTimeStart();
        Date contractTimeEnd = ctrProfitSearchVo.getContractTimeEnd();
        String noBusinessType = ctrProfitSearchVo.getNoBusinessType();

        String baseDateStart = "";
        if(contractTimeStart != null ) {
            baseDateStart = DateUtil.format(contractTimeStart, "YYYY-MM");
        }
        String baseDateEnd = "";
        if(contractTimeEnd != null) {
            baseDateEnd = DateUtil.format(contractTimeEnd, "YYYY-MM");
        }

        Boolean roiDataFlg = false;
        Date nowDate = new Date();
        String nowDateStr = DateUtil.format(nowDate, "YYYY-MM");
        
        if(StringUtils.isNotBlank(baseDateStart) && StringUtils.equals("Y",noBusinessType)) {
            if(StringUtils.equals(baseDateStart,nowDateStr)) {
                roiDataFlg = true;
            } else if (StringUtils.equals(baseDateStart,baseDateEnd)) {
                roiDataFlg = true;
            }
        }


        // 添加roi数据
        if(roiDataFlg){
            // 【合同日期开始、结束日期在同一个月之内或开始日期为当前月】 并且 【勾选不区分业务类型】
            RptBaseCostSearchVo searchVo = new RptBaseCostSearchVo();
            searchVo.setBaseDate(baseDateStart);
            List<RptBaseCostVo> rptBaseCostList = ctrContractReportMapper.findRptBaseCostList(searchVo);
            if(CollectionUtils.isNotEmpty(rptBaseCostList)) {
                List<Long> userIdList = rptBaseCostList.stream().map(RptBaseCostVo::getMatchUserId).distinct().collect(Collectors.toList());
                RptUserRoiVo userRoiVo = new RptUserRoiVo();
                userRoiVo.setUserList(userIdList);
                userRoiVo.setBaseStartDate(baseDateStart);
                userRoiVo.setBaseEndDate(baseDateStart);
                List<RptUserRoiResultVo> roiResultVoList = userRoiService.getUserRoiResultVoList(userRoiVo);
                if(CollectionUtils.isNotEmpty(roiResultVoList)) {
                    Map<Long, RptUserRoiResultVo> userRoiMap = roiResultVoList.stream()
                            .collect(Collectors.toMap(RptUserRoiResultVo::getMatchUserId, vo -> vo));

                    if(CollectionUtils.isNotEmpty(profitPage)) {
                        for (RptCtrProfitVo ctrProfitVo : profitPage) {
                            RptUserRoiResultVo userRoiResultVo = userRoiMap.get(ctrProfitVo.getMatchUserId());
                            if(Objects.nonNull(userRoiResultVo)) {
                                BeanUtils.copyProperties(userRoiResultVo,ctrProfitVo);
                            }
                        }
                    }
                }
                
            }
            // 添加业务成本统计数据
            if(CollectionUtils.isNotEmpty(profitPage)&&noBusinessType.equals("Y")){
                RptBaseCostSearchVo rptCostsearchVo = new RptBaseCostSearchVo();
                rptCostsearchVo.setBaseDateStart(baseDateStart);
                rptCostsearchVo.setBaseDateEnd(baseDateEnd);
                List<RptBaseCostVo> costGroupbList = ctrContractReportMapper.findBaseCostGroupByDate(rptCostsearchVo);
                if(CollectionUtils.isNotEmpty(costGroupbList)){
                    Map<Long, BigDecimal> rptBaseCostMap = costGroupbList.stream().collect(Collectors.toMap(RptBaseCostVo::getMatchUserId, RptBaseCostVo::getTotalCost));
                    profitPage.forEach(it->{
                        it.setTotalCost(rptBaseCostMap.get(it.getMatchUserId()));
                    });
                }
            }
        }
        Pageable pageable = PageRequest.of(ctrProfitSearchVo.getPage() - 1, ctrProfitSearchVo.getRows());
        Page<RptCtrProfitVo> pageVo = new PageImpl<>(profitPage, pageable, ctrProfitSearchVo.getCount());
        return pageVo;
    }

//    @Override
//    public CtrProfitVo findProfitSum(CtrProfitSearchVo vo) {
//        vo.setCount(-1);
//        CtrProfitVo profitSum = ctrContractReportMapper.findProfitSum(vo);
//        if (profitSum == null) {
//            profitSum = new CtrProfitVo();
//        }
//        return profitSum;
//    }
//    
    @Override
    public RptCtrProfitVo findProfitSum(RptCtrProfitSearchVo ctrProfitSearchVo) {
        ctrProfitSearchVo.setPage(1);
        ctrProfitSearchVo.setRows(999);
        List<RptCtrProfitVo> profitPage = ctrContractReportMapper.findProfitPage(ctrProfitSearchVo);
        Date contractTimeStart = ctrProfitSearchVo.getContractTimeStart();
        Date contractTimeEnd = ctrProfitSearchVo.getContractTimeEnd();
        String noBusinessType = ctrProfitSearchVo.getNoBusinessType();
        String baseDateStart = "";
        if(contractTimeStart != null ) {
            baseDateStart = DateUtil.format(contractTimeStart, "YYYY-MM");
        }
        String baseDateEnd = "";
        if(contractTimeEnd != null) {
            baseDateEnd = DateUtil.format(contractTimeEnd, "YYYY-MM");
        }
        Boolean roiDataFlg = false;
        Date nowDate = new Date();
        String nowDateStr = DateUtil.format(nowDate, "YYYY-MM");

        if(StringUtils.isNotBlank(baseDateStart) && StringUtils.equals("Y",noBusinessType)) {
            if(StringUtils.equals(baseDateStart,nowDateStr)) {
                roiDataFlg = true;
            } else if (StringUtils.equals(baseDateStart,baseDateEnd)) {
                roiDataFlg = true;
            }
        }
        // 添加roi数据
        if(roiDataFlg){
            // 【合同日期开始、结束日期在同一个月之内或开始日期为当前月】 并且 【勾选不区分业务类型】
            RptBaseCostSearchVo searchVo = new RptBaseCostSearchVo();
            searchVo.setBaseDate(baseDateStart);
            List<RptBaseCostVo> rptBaseCostList = ctrContractReportMapper.findRptBaseCostList(searchVo);

            if(CollectionUtils.isNotEmpty(rptBaseCostList)) {
                List<Long> userIdList = rptBaseCostList.stream().map(RptBaseCostVo::getMatchUserId).distinct().collect(Collectors.toList());
                RptUserRoiVo userRoiVo = new RptUserRoiVo();
                userRoiVo.setUserList(userIdList);
                userRoiVo.setBaseStartDate(baseDateStart);
                userRoiVo.setBaseEndDate(baseDateStart);
                List<RptUserRoiResultVo> roiResultVoList = userRoiService.getUserRoiResultVoList(userRoiVo);
                if(CollectionUtils.isNotEmpty(roiResultVoList)) {
                    Map<Long, RptUserRoiResultVo> userRoiMap = roiResultVoList.stream()
                            .collect(Collectors.toMap(RptUserRoiResultVo::getMatchUserId, vo -> vo));

                    if(CollectionUtils.isNotEmpty(profitPage)) {
                        for (RptCtrProfitVo ctrProfitVo : profitPage) {
                            RptUserRoiResultVo userRoiResultVo = userRoiMap.get(ctrProfitVo.getMatchUserId());
                            if(Objects.nonNull(userRoiResultVo)) {
                                BeanUtils.copyProperties(userRoiResultVo,ctrProfitVo);
                            }
                        }
                    }
                }

            }
        }

        RptCtrProfitVo profitSum = new RptCtrProfitVo();

        Integer sumOrderCount = 0;// 订单合计
        BigDecimal sumTunnage = BigDecimal.ZERO;// 吨数合计
        BigDecimal sumSellMoney = BigDecimal.ZERO;// 销售额合计
        BigDecimal sumGross = BigDecimal.ZERO;// 毛利合计
        BigDecimal sumGrossAvg = BigDecimal.ZERO;// 毛利均值合计
        BigDecimal sumTotalFinancing = BigDecimal.ZERO;// 总投入合计
        BigDecimal sumCommission = BigDecimal.ZERO;// 提成合计
        BigDecimal sumEvectionCost = BigDecimal.ZERO;// 出差报销费用合计
        BigDecimal sumRoi = BigDecimal.ZERO;// roi合计
        
        
        BigDecimal sumSellTotalAmount = BigDecimal.ZERO;// 销售额
        BigDecimal sumBuyTotalAmount = BigDecimal.ZERO;// 销售额
        BigDecimal sumProfit = BigDecimal.ZERO;// 毛利
        BigDecimal sumCost = BigDecimal.ZERO;// 费用
        BigDecimal sumMargin = BigDecimal.ZERO;// 净毛利
        
        if(CollectionUtils.isNotEmpty(profitPage)) {
            for (RptCtrProfitVo ctrProfitVo : profitPage) {
                if(ctrProfitVo.getSellTotalAmount() != null) {
                    sumSellTotalAmount = sumSellTotalAmount.add(ctrProfitVo.getSellTotalAmount());
                }
                if(ctrProfitVo.getBuyTotalAmount() != null) {
                    sumBuyTotalAmount = sumBuyTotalAmount.add(ctrProfitVo.getBuyTotalAmount());
                }
                if(ctrProfitVo.getProfit() != null) {
                    sumProfit = sumProfit.add(ctrProfitVo.getProfit());
                }
                if(ctrProfitVo.getCost() != null) {
                    sumCost = sumCost.add(ctrProfitVo.getCost());
                }
                if(ctrProfitVo.getMargin() != null) {
                    sumMargin = sumMargin.add(ctrProfitVo.getMargin());
                }
                if(ctrProfitVo.getOrderCount() != null){
                    sumOrderCount += ctrProfitVo.getOrderCount();
                }
                if(ctrProfitVo.getTunnage() != null) {
                    sumTunnage = sumTunnage.add(ctrProfitVo.getTunnage());
                }
                if(ctrProfitVo.getSellMoney() != null) {
                    sumSellMoney = sumSellMoney.add(ctrProfitVo.getSellMoney());
                }
                if(ctrProfitVo.getGross() != null) {
                    sumGross = sumGross.add(ctrProfitVo.getGross());
                }
                if(ctrProfitVo.getGrossAvg() != null) {
                    sumGrossAvg = sumGrossAvg.add(ctrProfitVo.getGrossAvg());
                }
                if(ctrProfitVo.getTotalFinancing() != null) {
                    sumTotalFinancing = sumTotalFinancing.add(ctrProfitVo.getTotalFinancing());
                }
                if(ctrProfitVo.getCommission() != null) {
                    sumCommission = sumCommission.add(ctrProfitVo.getCommission());
                }
                if(ctrProfitVo.getEvectionCost() != null) {
                    sumEvectionCost = sumEvectionCost.add(ctrProfitVo.getEvectionCost());
                }
            }  
        }
        if (sumTotalFinancing.compareTo(BigDecimal.ZERO) > 0) {
            sumRoi = sumGross.subtract(sumTotalFinancing).divide(sumTotalFinancing,2, RoundingMode.HALF_UP);
        }
        // 添加业务成本统计数据
        if(CollectionUtils.isNotEmpty(profitPage)&&roiDataFlg){
            RptBaseCostSearchVo rptSearchVo = new RptBaseCostSearchVo();
            rptSearchVo.setBaseDateStart(baseDateStart);
            rptSearchVo.setBaseDateEnd(baseDateEnd);
            List<RptBaseCostVo> costGroupByDateList = ctrContractReportMapper.findBaseCostGroupByDate(rptSearchVo);
            BigDecimal totalCost =new BigDecimal(0);
            if(CollectionUtils.isNotEmpty(costGroupByDateList)){
                Map<Long, BigDecimal> rptBaseCostMap = costGroupByDateList.stream().collect(Collectors.toMap(RptBaseCostVo::getMatchUserId, RptBaseCostVo::getTotalCost));
                for (RptCtrProfitVo ctrProfitVo : profitPage) {
                    BigDecimal cost = rptBaseCostMap.get(ctrProfitVo.getMatchUserId());
                    if(cost!=null){
                        totalCost= totalCost.add(cost);
                    }
                }
                profitSum.setTotalCost(totalCost);
            }
        }
        profitSum.setSellTotalAmount(sumSellTotalAmount);
        profitSum.setBuyTotalAmount(sumBuyTotalAmount);
        profitSum.setProfit(sumProfit);
        profitSum.setCost(sumCost);
        profitSum.setMargin(sumMargin);
        profitSum.setOrderCount(sumOrderCount);
        profitSum.setTunnage(sumTunnage);
        profitSum.setSellMoney(sumSellMoney);
        profitSum.setGross(sumGross);
        profitSum.setGrossAvg(sumGrossAvg);
        profitSum.setTotalFinancing(sumTotalFinancing);
        profitSum.setCommission(sumCommission);
        profitSum.setEvectionCost(sumEvectionCost);
        profitSum.setRoi(sumRoi);
        return profitSum;
    }

    @Override
    public Page<RptCtrTypeProfitVo> findTypeProfitPage(RptCtrProfitSearchVo ctrProfitSearchVo) {
        List<RptCtrTypeProfitVo> dcsxCapitalCostList = ctrContractReportMapper.getDcsxCapitalCost(ctrProfitSearchVo);
        List<RptCtrTypeProfitVo> capitalCostList = ctrContractReportMapper.getCapitalCost(ctrProfitSearchVo);
        List<RptCtrTypeProfitVo> profitPage = ctrContractReportMapper.findTypeProfitPage(ctrProfitSearchVo);
        if(CollUtil.isNotEmpty(profitPage)) {
            Map<String, RptCtrTypeProfitVo> dcsxCapitalCostMap = new HashMap<>();
            Map<String, RptCtrTypeProfitVo> capitalCostMap = new HashMap<>();
            if (CollUtil.isNotEmpty(dcsxCapitalCostList)) {
                dcsxCapitalCostMap = dcsxCapitalCostList.stream()
                        .collect(Collectors.toMap(
                                item -> item.getDeptId() + item.getBusinessName(),  // 键的映射
                                item -> item,                                       // 值的映射
                                (existing, replacement) -> existing                // 合并函数（处理重复键的情况）
                        ));
            }
            if (CollUtil.isNotEmpty(capitalCostList)) {
                capitalCostMap = capitalCostList.stream()
                        .collect(Collectors.toMap(
                                item -> item.getDeptId() + item.getBusinessName(),  // 键的映射
                                item -> item,                                       // 值的映射
                                (existing, replacement) -> existing                // 合并函数（处理重复键的情况）
                        ));
            }
            for (RptCtrTypeProfitVo profitVo : profitPage) {
                RptCtrTypeProfitVo dcsxCapitalCost = dcsxCapitalCostMap.get(profitVo.getDeptId() + profitVo.getBusinessName());
                RptCtrTypeProfitVo capitalCost = capitalCostMap.get(profitVo.getDeptId() + profitVo.getBusinessName());
                profitVo.setMargin(profitVo.getMargin().subtract(Objects.isNull(dcsxCapitalCost)?BigDecimal.ZERO:dcsxCapitalCost.getDcsxCapitalCost())
                        .subtract(Objects.isNull(capitalCost)?BigDecimal.ZERO:capitalCost.getCapitalCost()).setScale(2, RoundingMode.HALF_UP));
            }
        }
        
        // 查询部门CD
        DeptSearchVo deptSearchVo = new DeptSearchVo(BasConstants.ZG_ENTERPRISE_ID);
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        Map<Long,String> deptCdMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(deptList)) {
            for (SysDeptSdk deptSdk : deptList) {
                deptCdMap.put(deptSdk.getDeptId(),deptSdk.getDeptCd());
            }
        }
        
        Date contractTimeStart = ctrProfitSearchVo.getContractTimeStart();
        Date contractTimeEnd = ctrProfitSearchVo.getContractTimeEnd();
        String noBusinessType = ctrProfitSearchVo.getNoBusinessType();

        String baseDateStart = "";
        if(contractTimeStart != null ) {
            baseDateStart = DateUtil.format(contractTimeStart, "YYYY-MM");
        }
        String baseDateEnd = "";
        if(contractTimeEnd != null) {
            baseDateEnd = DateUtil.format(contractTimeEnd, "YYYY-MM");
        }

        Boolean roiDataFlg = false;
        Date nowDate = new Date();
        String nowDateStr = DateUtil.format(nowDate, "YYYY-MM");

        if(StringUtils.isNotBlank(baseDateStart) && StringUtils.equals("Y",noBusinessType)) {
            if(StringUtils.equals(baseDateStart,nowDateStr)) {
                roiDataFlg = true;
            } else if (StringUtils.equals(baseDateStart,baseDateEnd)) {
                roiDataFlg = true;
            }
        }


        // 添加roi汇总数据
        if(roiDataFlg){
            // 【合同日期开始、结束日期在同一个月之内或开始日期为当前月】 并且 【勾选不区分业务类型】
            RptUserRoiVo userRoiVo = new RptUserRoiVo();
            userRoiVo.setBaseDate(baseDateStart);
            List<RptSummaryRoiResultVo> summaryRoiList = summaryRoiService.getSummaryRoiResult(userRoiVo);
            if(CollectionUtils.isNotEmpty(summaryRoiList)) {
                Map<String, RptSummaryRoiResultVo> baseCostVoMap = summaryRoiList.stream()
                        .collect(Collectors.toMap(
                                RptSummaryRoiResultVo::getBranchCd, // Key: BranchCd
                                vo -> {
                                    // Value: RptBaseCostVo with summed fields
                                    Integer businessUserCount = 0;
                                    Integer orderCount = 0;
                                    BigDecimal tonnes = BigDecimal.ZERO;
                                    BigDecimal sellMoney = BigDecimal.ZERO;
                                    BigDecimal sellLabor = BigDecimal.ZERO;
                                    BigDecimal gross = BigDecimal.ZERO;
                                    BigDecimal grossLabor = BigDecimal.ZERO;
                                    BigDecimal grossAvg = BigDecimal.ZERO;

                                    for (RptSummaryRoiResultVo roiResultVo : summaryRoiList) {
                                        if (roiResultVo.getBranchCd().equals(vo.getBranchCd())) {
                                            businessUserCount += roiResultVo.getBusinessUserCount();
                                            orderCount += roiResultVo.getOrderCount();
                                            tonnes = tonnes.add(roiResultVo.getTonnes());
                                            sellMoney = sellMoney.add(roiResultVo.getSellMoney());
                                            sellLabor = sellLabor.add(roiResultVo.getSellLabor());
                                            gross = gross.add(roiResultVo.getGross());
                                            grossLabor = grossLabor.add(roiResultVo.getGrossLabor());
                                            grossAvg = grossAvg.add(roiResultVo.getGrossAvg());
                                        }
                                    }

                                    RptSummaryRoiResultVo resultVo = new RptSummaryRoiResultVo();
                                    resultVo.setBranchCd(vo.getBranchCd());
                                    resultVo.setBusinessUserCount(businessUserCount);
                                    resultVo.setOrderCount(orderCount);
                                    resultVo.setTonnes(tonnes);
                                    resultVo.setSellMoney(sellMoney);
                                    resultVo.setSellLabor(sellLabor);
                                    resultVo.setGross(gross);
                                    resultVo.setGrossLabor(grossLabor);
                                    resultVo.setGrossAvg(grossAvg);
                                    return resultVo;
                                },
                                (existing, replacement) -> replacement));

                if(CollectionUtils.isNotEmpty(profitPage)) {
                    for (RptCtrTypeProfitVo ctrTypeProfitVo : profitPage) {
                        String deptCd = deptCdMap.get(ctrTypeProfitVo.getDeptId());

                        RptSummaryRoiResultVo roiResultVo = baseCostVoMap.get(deptCd);
                        if(Objects.nonNull(roiResultVo)) {
                            BeanUtils.copyProperties(roiResultVo,ctrTypeProfitVo);
                        }
                    }
                }
               
            }
            
        }

        Pageable pageable = PageRequest.of(ctrProfitSearchVo.getPage() - 1, ctrProfitSearchVo.getRows());
        Page<RptCtrTypeProfitVo> pageVo = new PageImpl<>(profitPage, pageable, ctrProfitSearchVo.getCount());
        return pageVo;
    }

    @Override
    public RptCtrTypeProfitVo findTypeProfitSum(RptCtrProfitSearchVo ctrProfitSearchVo) {
        ctrProfitSearchVo.setPage(1);
        ctrProfitSearchVo.setRows(999);
        List<RptCtrTypeProfitVo> dcsxCapitalCostList = ctrContractReportMapper.getDcsxCapitalCost(ctrProfitSearchVo);
        List<RptCtrTypeProfitVo> capitalCostList = ctrContractReportMapper.getCapitalCost(ctrProfitSearchVo);
        List<RptCtrTypeProfitVo> profitPage = ctrContractReportMapper.findTypeProfitPage(ctrProfitSearchVo);
        if(CollUtil.isNotEmpty(profitPage)) {
            Map<String, RptCtrTypeProfitVo> dcsxCapitalCostMap = new HashMap<>();
            Map<String, RptCtrTypeProfitVo> capitalCostMap = new HashMap<>();
            if (CollUtil.isNotEmpty(dcsxCapitalCostList)) {
                dcsxCapitalCostMap = dcsxCapitalCostList.stream()
                        .collect(Collectors.toMap(
                                item -> item.getDeptId() + item.getBusinessName(),  // 键的映射
                                item -> item,                                       // 值的映射
                                (existing, replacement) -> existing                // 合并函数（处理重复键的情况）
                        ));
            }
            if (CollUtil.isNotEmpty(capitalCostList)) {
                capitalCostMap = capitalCostList.stream()
                        .collect(Collectors.toMap(
                                item -> item.getDeptId() + item.getBusinessName(),  // 键的映射
                                item -> item,                                       // 值的映射
                                (existing, replacement) -> existing                // 合并函数（处理重复键的情况）
                        ));
            }
            for (RptCtrTypeProfitVo profitVo : profitPage) {
                RptCtrTypeProfitVo dcsxCapitalCost = dcsxCapitalCostMap.get(profitVo.getDeptId() + profitVo.getBusinessName());
                RptCtrTypeProfitVo capitalCost = capitalCostMap.get(profitVo.getDeptId() + profitVo.getBusinessName());
                profitVo.setMargin(profitVo.getMargin().subtract(Objects.isNull(dcsxCapitalCost)?BigDecimal.ZERO:dcsxCapitalCost.getDcsxCapitalCost())
                        .subtract(Objects.isNull(capitalCost)?BigDecimal.ZERO:capitalCost.getCapitalCost()).setScale(2, RoundingMode.HALF_UP));
            }
        }
        // 查询部门CD
        DeptSearchVo deptSearchVo = new DeptSearchVo(BasConstants.ZG_ENTERPRISE_ID);
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        Map<Long,String> deptCdMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(deptList)) {
            for (SysDeptSdk deptSdk : deptList) {
                deptCdMap.put(deptSdk.getDeptId(),deptSdk.getDeptCd());
            }
        }

        Date contractTimeStart = ctrProfitSearchVo.getContractTimeStart();
        Date contractTimeEnd = ctrProfitSearchVo.getContractTimeEnd();
        String noBusinessType = ctrProfitSearchVo.getNoBusinessType();

        String baseDateStart = "";
        if(contractTimeStart != null ) {
            baseDateStart = DateUtil.format(contractTimeStart, "YYYY-MM");
        }
        String baseDateEnd = "";
        if(contractTimeEnd != null) {
            baseDateEnd = DateUtil.format(contractTimeEnd, "YYYY-MM");
        }

        Boolean roiDataFlg = false;
        Date nowDate = new Date();
        String nowDateStr = DateUtil.format(nowDate, "YYYY-MM");

        if(StringUtils.isNotBlank(baseDateStart) && StringUtils.equals("Y",noBusinessType)) {
            if(StringUtils.equals(baseDateStart,nowDateStr)) {
                roiDataFlg = true;
            } else if (StringUtils.equals(baseDateStart,baseDateEnd)) {
                roiDataFlg = true;
            }
        }


        // 添加roi汇总数据
        if(roiDataFlg){
            // 【合同日期开始、结束日期在同一个月之内或开始日期为当前月】 并且 【勾选不区分业务类型】
            RptUserRoiVo userRoiVo = new RptUserRoiVo();
            userRoiVo.setBaseDate(baseDateStart);
            List<RptSummaryRoiResultVo> summaryRoiList = summaryRoiService.getSummaryRoiResult(userRoiVo);
            if(CollectionUtils.isNotEmpty(summaryRoiList)) {
                Map<String, RptSummaryRoiResultVo> baseCostVoMap = summaryRoiList.stream()
                        .collect(Collectors.toMap(
                                RptSummaryRoiResultVo::getBranchCd, // Key: BranchCd
                                vo -> {
                                    // Value: RptBaseCostVo with summed fields
                                    Integer businessUserCount = 0;
                                    Integer orderCount = 0;
                                    BigDecimal tonnes = BigDecimal.ZERO;
                                    BigDecimal sellMoney = BigDecimal.ZERO;
                                    BigDecimal sellLabor = BigDecimal.ZERO;
                                    BigDecimal gross = BigDecimal.ZERO;
                                    BigDecimal grossLabor = BigDecimal.ZERO;
                                    BigDecimal grossAvg = BigDecimal.ZERO;

                                    for (RptSummaryRoiResultVo roiResultVo : summaryRoiList) {
                                        if (roiResultVo.getBranchCd().equals(vo.getBranchCd())) {
                                            businessUserCount += roiResultVo.getBusinessUserCount();
                                            orderCount += roiResultVo.getOrderCount();
                                            tonnes = tonnes.add(roiResultVo.getTonnes());
                                            sellMoney = sellMoney.add(roiResultVo.getSellMoney());
                                            sellLabor = sellLabor.add(roiResultVo.getSellLabor());
                                            gross = gross.add(roiResultVo.getGross());
                                            grossLabor = grossLabor.add(roiResultVo.getGrossLabor());
                                            grossAvg = grossAvg.add(roiResultVo.getGrossAvg());
                                        }
                                    }

                                    RptSummaryRoiResultVo resultVo = new RptSummaryRoiResultVo();
                                    resultVo.setBranchCd(vo.getBranchCd());
                                    resultVo.setBusinessUserCount(businessUserCount);
                                    resultVo.setOrderCount(orderCount);
                                    resultVo.setTonnes(tonnes);
                                    resultVo.setSellMoney(sellMoney);
                                    resultVo.setSellLabor(sellLabor);
                                    resultVo.setGross(gross);
                                    resultVo.setGrossLabor(grossLabor);
                                    resultVo.setGrossAvg(grossAvg);
                                    return resultVo;
                                },
                                (existing, replacement) -> replacement));

                if(CollectionUtils.isNotEmpty(profitPage)) {
                    for (RptCtrTypeProfitVo ctrTypeProfitVo : profitPage) {
                        String deptCd = deptCdMap.get(ctrTypeProfitVo.getDeptId());

                        RptSummaryRoiResultVo roiResultVo = baseCostVoMap.get(deptCd);
                        if(Objects.nonNull(roiResultVo)) {
                            BeanUtils.copyProperties(roiResultVo,ctrTypeProfitVo);
                        }
                    }
                }

            }

        }

        RptCtrTypeProfitVo profitSum = new RptCtrTypeProfitVo();

        Integer sumBusinessUserCount = 0;// 业务人数
        Integer sumOrderCount = 0;// 订单合计
        BigDecimal sumTonnes = BigDecimal.ZERO;// 吨数
        BigDecimal sumSellMoney = BigDecimal.ZERO;// 销售额
        BigDecimal sumSellLabor = BigDecimal.ZERO;// 销售额人效
        BigDecimal sumGross = BigDecimal.ZERO;// 毛利合计
        BigDecimal sumGrossLabor = BigDecimal.ZERO;// 毛利人效
        BigDecimal sumGrossAvg = BigDecimal.ZERO;// 毛利率

        BigDecimal sumSellTotalAmount = BigDecimal.ZERO;// 销售额
        BigDecimal sumBuyTotalAmount = BigDecimal.ZERO;// 销售额
        BigDecimal sumProfit = BigDecimal.ZERO;// 毛利
        BigDecimal sumCost = BigDecimal.ZERO;// 费用
        BigDecimal sumMargin = BigDecimal.ZERO;// 净毛利

        if(CollectionUtils.isNotEmpty(profitPage)) {
            for (RptCtrTypeProfitVo ctrTypeProfitVo : profitPage) {
                if(ctrTypeProfitVo.getSellTotalAmount() != null) {
                    sumSellTotalAmount = sumSellTotalAmount.add(ctrTypeProfitVo.getSellTotalAmount());
                }
                if(ctrTypeProfitVo.getBuyTotalAmount() != null) {
                    sumBuyTotalAmount = sumBuyTotalAmount.add(ctrTypeProfitVo.getBuyTotalAmount());
                }
                if(ctrTypeProfitVo.getProfit() != null) {
                    sumProfit = sumProfit.add(ctrTypeProfitVo.getProfit());
                }
                if(ctrTypeProfitVo.getCost() != null) {
                    sumCost = sumCost.add(ctrTypeProfitVo.getCost());
                }
                if(ctrTypeProfitVo.getMargin() != null) {
                    sumMargin = sumMargin.add(ctrTypeProfitVo.getMargin());
                }
                
                if(ctrTypeProfitVo.getBusinessUserCount() != null){
                    sumBusinessUserCount += ctrTypeProfitVo.getBusinessUserCount();
                }

                if(ctrTypeProfitVo.getOrderCount() != null){
                    sumOrderCount += ctrTypeProfitVo.getOrderCount();
                }
                
                if(ctrTypeProfitVo.getTonnes() != null) {
                    sumTonnes = sumTonnes.add(ctrTypeProfitVo.getTonnes());
                }
                if(ctrTypeProfitVo.getSellMoney() != null) {
                    sumSellMoney = sumSellMoney.add(ctrTypeProfitVo.getSellMoney());
                }
                if(ctrTypeProfitVo.getSellLabor() != null) {
                    sumSellLabor = sumSellLabor.add(ctrTypeProfitVo.getSellLabor());
                }
                if(ctrTypeProfitVo.getGross() != null) {
                    sumGross = sumGross.add(ctrTypeProfitVo.getGross());
                }
                if(ctrTypeProfitVo.getGrossLabor() != null) {
                    sumGrossLabor = sumGrossLabor.add(ctrTypeProfitVo.getGrossLabor());
                }
                if(ctrTypeProfitVo.getGrossAvg() != null) {
                    sumGrossAvg = sumGrossAvg.add(ctrTypeProfitVo.getGrossAvg());
                }
            }
        }
        
        profitSum.setSellTotalAmount(sumSellTotalAmount);
        profitSum.setBuyTotalAmount(sumBuyTotalAmount);
        profitSum.setProfit(sumProfit);
        profitSum.setCost(sumCost);
        profitSum.setMargin(sumMargin);

        profitSum.setBusinessUserCount(sumBusinessUserCount);
        profitSum.setOrderCount(sumOrderCount);
        profitSum.setTonnes(sumTonnes);
        profitSum.setSellMoney(sumSellMoney);
        profitSum.setSellLabor(sumSellLabor);
        profitSum.setGross(sumGross);
        profitSum.setGrossLabor(sumGrossLabor);
        profitSum.setGrossAvg(sumGrossAvg);
        return profitSum;
    }

    @Override
    public List<Long> findProfitByDeptId(RptCtrProfitSearchVo searchVo) {
        List<Long> byDeptId = ctrContractReportMapper.findProfitByDeptId(searchVo);
        return byDeptId;
    }

    /**
     * 查询提成计算默认参数
     *
     * @param configKey
     * @return
     */
    @Override
    public RptCalCulateParam findCalculateParam(String configKey) {
        String calculateParam = ctrContractReportMapper.findCalculateParam(configKey);
        if (StringUtils.isNotBlank(calculateParam)) {
            TypeReference<RptCalCulateParam> clazz = new TypeReference<RptCalCulateParam>() {
            };
            return JsonUtil.json2Object(clazz, calculateParam);
        }
        return null;
    }

    /**
     * 查询保费费率计算参数
     * @param configKey
     * @return
     */
    @Override
    public List<RptCalculateInsuranceRates> findCalculateInsuranceRates(String configKey) {
        String calculateParam = ctrContractReportMapper.findCalculateParam(configKey);
        if (StringUtils.isNotBlank(calculateParam)) {
            TypeReference<List<RptCalculateInsuranceRates>> clazz = new TypeReference<List<RptCalculateInsuranceRates>>() {
            };
            return JsonUtil.json2Object(clazz, calculateParam);
        }
        return null;
    }

    /**
     * 业务员利润计算配置表
     *
     * @return
     */
    @Override
    public List<RptMatchProfitConfig> findMatchProfitConfig() {
        return ctrContractReportMapper.findMatchProfitConfig();
    }

    /**
     * 赊销业务提成计算明细表
     *
     * @param searchVo
     * @return
     */
    @Override
    public Page<RptCreditBusinessCommission> findCreditBusinessCommissionPage(RptCreditBusinessCommissionSearchVo searchVo) {
        // 默认利润计算参数
        RptCalCulateParam calculateParam = findCalculateParam(ReportConstant.CALCULATE_CONFIG_KEY);
        // 保费费率取值表达式
        List<RptCalculateInsuranceRates> insuranceRates = findCalculateInsuranceRates(ReportConstant.CALCUALTE_INSURANCE_RATE_KEY);

        // 业务员利润计算配置表
        List<RptMatchProfitConfig> matchProfitConfig = findMatchProfitConfig();
		Map<Long, RptMatchProfitConfig> matchConfigMap = matchProfitConfig.stream().collect(Collectors.toMap(RptMatchProfitConfig::getMatchUserId, MatchProfitConfig -> MatchProfitConfig, (v1, v2) -> v2));

        RptCalCulateParam calCulateParam = compositeConfig(calculateParam, matchConfigMap, searchVo.getMatchUserId());

        List<RptCreditBusinessCommission> commissionPage = ctrContractReportMapper.findCreditBusinessCommissionPage(searchVo);
        commissionPage.forEach(commission -> {
            //保费取值根据配置项动态取值
            commission.setInsuranceRate(reportCalculateUtil.getInsuranceRate(commission, insuranceRates));
            //金融服务费
            commission.setFinancialServiceAmount(reportCalculateUtil.getFinancialServiceAmount(commission.getBuyTotalAmount(),calCulateParam.getServeRate(),commission.getCreditCycle()));
            //印花税
            commission.setPrintAmount(reportCalculateUtil.getPrintAmount(commission.getSellPrice(),commission.getTotalNumber(),calCulateParam.getStampDutyRate()));
            //增值税税后差价
            commission.setVatSpreadAmount(reportCalculateUtil.getVatSpreadAmount(commission.getSellTotalAmount(),commission.getBuyTotalAmount(),commission.getFinancialServiceAmount(),commission.getTransportAmount(),commission.getWarehouseAmount(),commission.getBreachAmount(),commission.getInsuranceRate(),commission.getDeliveryFee()));
            //增值税
            commission.setVatAmount(reportCalculateUtil.getVatAmount(commission.getSellPrice(),commission.getBuyPrice(),commission.getTransportAmount(),commission.getWarehouseAmount(),commission.getInsuranceRate(),commission.getTotalNumber(),calCulateParam.getVatRate(),calCulateParam.getTransportationRate(),calCulateParam.getWarehouseRate(),commission.getInsuranceRate(),commission.getDeliveryFee()));
            //附加税
            commission.setSurchargeAmount(reportCalculateUtil.getSurchargeAmount(commission.getVatAmount(),calCulateParam.getSurchargeRate()));
            //税金及附加
            commission.setTaxesSurchargesAmount(reportCalculateUtil.getTaxesSurchargesAmount(commission.getVatAmount()));
            //税后差价收入（利润）
            commission.setAfterTaxSpreadAmount(reportCalculateUtil.getAfterTaxSpreadAmount(commission.getVatSpreadAmount(),commission.getTaxesSurchargesAmount(),commission.getPrintAmount()));
            //销售人员分成
            commission.setSellMatchAmount(reportCalculateUtil.getSellMatchAmount(commission.getAfterTaxSpreadAmount()));
            //采购人员分成
            commission.setBuyMatchAmount(reportCalculateUtil.getBuyMatchAmount(commission.getAfterTaxSpreadAmount()));
            //销售团队负责人分成
            commission.setSellHeadCommissionAmount(reportCalculateUtil.getSaleTeamLeaderAmount(commission.getAfterTaxSpreadAmount()));
            //采购人员负责人分成
            commission.setBuyHeadCommissionAmount(reportCalculateUtil.getBuyHeadTeamLeaderAmount(commission.getAfterTaxSpreadAmount()));
        });

        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptCreditBusinessCommission> pageVo = new PageImpl<>(commissionPage, pageable, searchVo.getCount());
        return pageVo;
    }

    /**
     * 计算营业员的比率
     * @param calculateParam
     * @param matchConfigMap
     * @param matchUserId
     * @return
     */
	private RptCalCulateParam compositeConfig(RptCalCulateParam calculateParam, Map<Long, RptMatchProfitConfig> matchConfigMap, Long matchUserId){
        RptCalCulateParam calCulateParam1 = new RptCalCulateParam();
        BeanUtils.copyProperties(calculateParam,calCulateParam1);
        matchConfigMap.forEach((k,v)->{
            if (k.equals(matchUserId)){
                calCulateParam1.setBuyCommissionRate(v.getBuyCommissionRate());
                calCulateParam1.setSellCommissionRate(v.getSellCommissionRate());
                calCulateParam1.setMarketingRate(v.getMarketingRate());
                calCulateParam1.setCompanyRate(v.getCompanyRate());
                calCulateParam1.setBuyCommissionRate(v.getBuyCommissionRate());
                calCulateParam1.setSellCommissionRate(v.getSellCommissionRate());
            }
        });
        return calCulateParam1;
	}

    @Override
    public Page<RptCtrContractRptVo> findRptContractPage(ContractSearchVo searchVo) {
        List<RptCtrContractRptVo> resultList = ctrContractReportMapper.findRptContractPage(searchVo);
        makePairCode(resultList);
        resultList.forEach(vo->{
            // 逾期金额 ：在定金时间点未收到定金的显示定金金额
            if (vo.getPayBondTime() != null && new Date().after(vo.getPayBondTime())) {
                vo.setOrverdurAmount(vo.getBondAmount());
            }
            // 逾期金额 ：在收全款时间点未收到全款的显示余款
            if (vo.getPayFullTime() != null && new Date().after(vo.getPayFullTime())) {
                vo.setOrverdurAmount(vo.getTotalAmount().subtract(vo.getDealedAmount()).subtract(vo.getRefundAmount()));
            }
            boolean existContractTextFileId = false;
            String contractFileUrl = "";
            if (StringUtils.isNotBlank(vo.getBuyContentFileId())){
                existContractTextFileId = true;
                List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getBuyContentFileId());
                if (CollectionUtils.isNotEmpty(idList)){
                    contractFileUrl = fileShowUrl  + idList.get(idList.size()-1);
                }
            }else if(StringUtils.isNotBlank(vo.getSellContentFileId())){
                existContractTextFileId = true;
                List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getSellContentFileId());
                if (CollectionUtils.isNotEmpty(idList)){
                    contractFileUrl = fileShowUrl  + idList.get(idList.size() - 1);
                }
            }
            vo.setExistContractTextFileId(existContractTextFileId);
            vo.setContractFileUrl(contractFileUrl);
            String shippingFileUrl = "";
            if (StringUtils.isNotBlank(vo.getShippingFileId())){
                List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getShippingFileId());
                if (CollectionUtils.isNotEmpty(idList)){
                    shippingFileUrl = fileDownLoadUrl + "/view/download/" + idList.get(idList.size()-1);
                }
            }
            vo.setShippingFileUrl(shippingFileUrl);
        });
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        return new PageImpl<>(resultList, pageable, searchVo.getCount());
    } 
    
    @Override
    public Page<ContractShowVo> findIndexRptContractPage(ContractSearchVo searchVo) {
        List<ContractShowVo> resultList = ctrContractReportMapper.findIndexRptContractPage(searchVo);
        List<RptPartBsCompanyVo> companyList = bsCompanyDao.findAllCompany();
        
        // 使用Stream将List转换为Map
        Map<Long, RptPartBsCompanyVo> companyMap = companyList.stream()
                .collect(Collectors.toMap(RptPartBsCompanyVo::getId, company -> company));
        List<ContractShowVo> contractShowVos = makePairCode2(resultList);
        contractShowVos.forEach(vo->{
            // 逾期金额 ：在定金时间点未收到定金的显示定金金额
            if (vo.getPayBondTime() != null && new Date().after(vo.getPayBondTime())) {
                vo.setOrverdurAmount(vo.getBondAmount());
            }
            // 逾期金额 ：在收全款时间点未收到全款的显示余款
            if (vo.getPayFullTime() != null && new Date().after(vo.getPayFullTime())) {
                vo.setOrverdurAmount(vo.getTotalAmount().subtract(vo.getDealedAmount()).subtract(vo.getRefundAmount()));
            }
            boolean existContractTextFileId = false;
            String contractFileUrl = "";
            if (StringUtils.isNotBlank(vo.getBuyContentFileId())){
                existContractTextFileId = true;
                List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getBuyContentFileId());
                if (CollectionUtils.isNotEmpty(idList)){
                    contractFileUrl = fileShowUrl + "/view/show/" + idList.get(idList.size()-1);
                }
            }else if(StringUtils.isNotBlank(vo.getSellContentFileId())){
                existContractTextFileId = true;
                List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getSellContentFileId());
                if (CollectionUtils.isNotEmpty(idList)){
                    contractFileUrl = fileShowUrl + "/view/show/" + idList.get(idList.size() - 1);
                }
            }
            vo.setExistContractTextFileId(existContractTextFileId);
            vo.setContractFileUrl(contractFileUrl);
            String protocolFileUrl = "";
            if (StringUtils.isNotBlank(vo.getProtocolFileId())) {
                List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getProtocolFileId());
                protocolFileUrl = fileShowUrl + "/view/show/" + idList.get(idList.size() - 1);
            }
            vo.setProtocolFileUrl(protocolFileUrl);
            vo.setInsuranceAmount(vo.parseInsuranceAmount());
            BigDecimal dealedAmount = getDefaultBigDecimal(vo.getDealedAmount());
            BigDecimal totalAmount = vo.getTotalAmount();
            vo.setReceivablePrincipal(totalAmount.subtract(dealedAmount));
            RptPartBsCompanyVo bsCompany = companyMap.get(vo.getCompanyId());
            if(Objects.nonNull(bsCompany)) {
                Boolean accessReportFlg = bsCompany.getAccessReportFlg();
                if(Boolean.TRUE.equals(accessReportFlg)) {
                    vo.setAccessReportFlg("是");
                } else {
                    vo.setAccessReportFlg("否");
                }
                Boolean actualGuaranteeFlg = bsCompany.getActualGuaranteeFlg();
                if(Boolean.TRUE.equals(actualGuaranteeFlg)) {
                    vo.setLiabilityFlg("是");
                } else {
                    vo.setLiabilityFlg("否");
                }
            }
            String shippingFileUrl = "";
            if (StringUtils.isNotBlank(vo.getShippingFileId())){
                List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getShippingFileId());
                if (CollectionUtils.isNotEmpty(idList)){
                    shippingFileUrl = fileDownLoadUrl + "/view/download/" + idList.get(idList.size()-1);
                }
            }
            vo.setShippingFileUrl(shippingFileUrl);
        });
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        return new PageImpl<>(contractShowVos, pageable, searchVo.getCount());
    }

    private BigDecimal getDefaultBigDecimal(BigDecimal value){
        return (Objects.isNull(value) || value.compareTo(BigDecimal.ZERO) < 0) ? BigDecimal.ZERO : value;
    }
    
    @Override
    public RptCtrContractRptVo findRptSumPageContract(ContractSearchVo searchVo) {
        return ctrContractReportMapper.findRptSumPageContract(searchVo);
    }
    
    @Override
    public RptCtrContractRptVo findIndexRptSumPageContract(ContractSearchVo searchVo) {
        return ctrContractReportMapper.findIndexRptSumPageContract(searchVo);
    }

    /**
     * 逾期预警合同列表查询
     *
     * @param searchVo
     * @return
     */
    @Override
    public Page<RptCtrContractWarnReport> findRptContractWarnPage(RptCtrContractWarnSearchVo searchVo) {
        List<RptCtrContractWarnReport> rptContractWarn = ctrContractReportMapper.findRptContractWarnPage(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        return new PageImpl<>(rptContractWarn, pageable, searchVo.getCount());
    }

    /**
     * 逾期预警合同列表合计
     *
     * @param searchVo
     * @return
     */
    @Override
    public RptCtrContractWarnReport findRptContractWarnSum(RptCtrContractWarnSearchVo searchVo) {
        searchVo.setCount(-1);
        return ctrContractReportMapper.findRptContractWarnSum(searchVo);
    }

    @Override
    public List<RptExportChainVo> mergeChainExport(List<DcsxShowVo> dcsxShowVoList) {
        if (CollectionUtils.isEmpty(dcsxShowVoList)){
            return new ArrayList<>();
        }
        log.info("mergeChainExport dcsxShowVoList.size:{}", dcsxShowVoList.size());
        List<Long> contractIdList = dcsxShowVoList.stream().map(DcsxShowVo::getId).collect(Collectors.toList());
        List<Long> approveIdList = dcsxShowVoList.stream().map(DcsxShowVo::getApproveId).collect(Collectors.toList());
        log.info("mergeChainExport contractIdList.size:{}", contractIdList.size());
        log.info("mergeChainExport approveIdList.size:{}", approveIdList.size());
        RptExportSearchVo searchVo = new RptExportSearchVo();
        searchVo.setContractIdList(contractIdList);
        searchVo.setApproveIdList(approveIdList);
        List<RptExportChainVo> resultList = ctrContractReportMapper.mergeChainExport(searchVo);
        log.info("mergeChainExport resultList.size:{}", resultList.size());
        List<RptExportChainVo> filteredContracts = resultList.stream()
                .filter(contract -> "S".equals(contract.getContractType()))
                .collect(Collectors.toList());
        log.info("mergeChainExport filteredContracts.size:{}", filteredContracts.size());
        Map<Long, RptExportChainVo> productMap = filteredContracts.stream().collect(Collectors.toMap(RptExportChainVo::getApproveId, c -> c));
        log.info("mergeChainExport productMap.size:{}", productMap.size());
        if (productMap.isEmpty()){
            return resultList;
        }
        // 获取所有人保授信
        List<BsCompanyCredit> creditList = bsCompanyDao.findAllCompanyCredit0();
        Map<Long, BsCompanyCredit> creditMap=new HashMap<>();
        if(CollectionUtils.isNotEmpty(creditList)){
            creditMap = creditList.stream().collect(Collectors.toMap(BsCompanyCredit::getCompanyId, item -> item));
        }
        for (RptExportChainVo exportChainVo : resultList) {
            RptExportChainVo vo = productMap.get(exportChainVo.getApproveId());
            if (Objects.nonNull(vo)){
                exportChainVo.setProductNames(vo.getProductNames());
//                exportChainVo.setAppointPayFullTime(vo.getAppointPayFullTime());
            }
            if(exportChainVo.getContractType().equals("S")){
                BsCompanyCredit bsCompanyCredit = creditMap.get(exportChainVo.getCompanyId());
                if(bsCompanyCredit!=null){
                    exportChainVo.setUsedCreditAmount(bsCompanyCredit.getUsedCreditAmount());
                    exportChainVo.setAvailableCreditAmount(bsCompanyCredit.getAvailableCreditAmount());
                }
            }
        }
        log.info("mergeChainExport resultList.size:{}", resultList.size());
        return resultList;
    }

    private void makePairCode(List<RptCtrContractRptVo> rptVoList) {
        Long pairId = 1L;
        for (int i = 0; i < rptVoList.size(); i++) {
            RptCtrContractRptVo ctrContract = rptVoList.get(i);
            Long viewPairId = ctrContract.getPairId();
            String pairCode = ctrContract.getPairCode();
            if (StringUtils.isNotBlank(pairCode) && viewPairId == null) {
                rptVoList.get(i).setPairId(pairId);
                for (int j = i + 1; j < rptVoList.size(); j++) {
                    if (StringUtils.equals(pairCode, rptVoList.get(j).getPairCode())) {
                        rptVoList.get(j).setPairId(pairId);
                    }
                }
                pairId++;
            }
        }
    }
    private List<ContractShowVo> makePairCode2(List<ContractShowVo> content) {
        Long pairId = 1L;
        for (int i = 0; i < content.size(); i++) {
            ContractShowVo ctrContract = content.get(i);
            Long pair_id = ctrContract.getPairId();
            String pairCode = ctrContract.getPairCode();
            if (StringUtils.isNotBlank(pairCode) && pair_id == null) {
                content.get(i).setPairId(pairId);
                for (int j = i + 1; j < content.size(); j++) {
                    if (j < content.size() && StringUtils.equals(pairCode, content.get(j).getPairCode())) {
                        content.get(j).setPairId(pairId);
                    }
                }
                pairId++;
            }
        }
        return content;
    }
}
