package com.spt.bas.web.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.remote.IRptIndexStatisticsClient;
import com.spt.bas.report.client.remote.IRptIndexReportClient;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.StringUtils;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @Author: gaojy
 * @create 2022/6/27 18:08
 * @version: 1.0
 * @description:
 */
public class WorkBenchCache {
    private static final AtomicBoolean IS_IN_IT = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(WorkBenchCache.class);

    // 工作台-待办统计
    private static LoadingCache<String, RptToDoStatisticsVo> toDoStatisticsCache;

    // 工作台-业务统计
    private static LoadingCache<String, List<RptIndexCommonVo>> businessStatisticsCache;
    private static LoadingCache<String, List<RptIndexCommonVo>> businessStatisticsCacheByMonth;

    // 工作台-业绩提成
    private static LoadingCache<String, RptPerformanceVo> performanceCommissionCache;

    // 工作台-业绩排名
    private static LoadingCache<String, List<RptIndexCommonVo>> performanceRankingCache;

    // 工作台-风控统计
    private static LoadingCache<String, List<RptIndexStatisticsVo>> riskStatisticsCache;

    // 工作台-财务统计
    private static LoadingCache<String, List<RptIndexStatisticsVo>> financeStatisticsCache;

    // 工作台-到期应收
    private static LoadingCache<String, List<RptIndexStatisticsVo>> unPayFinanceCache;

    // 工作台-未收款统计
    private static LoadingCache<String, List<RptIndexStatisticsVo>> unPayStatisticsCache;

    // 工作台-过去10周/月 赊销、代采、自营、合计
    private static LoadingCache<Integer, List<RptIndexStatisticsVo>> salesDataCache;

    // 工作台-过去10个月的毛利率
    private static LoadingCache<String, List<RptGrossProfitMarginVo>> grossProfitMarginCache;

    private WorkBenchCache() {
        if (!IS_IN_IT.get()) {
            init();
        }
    }

    public static void init() {
        log.info("---初始化工作台数据缓存");

        // 工作台-待办统计
        toDoStatisticsCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, RptToDoStatisticsVo>() {
                    @Override
                    public RptToDoStatisticsVo load(String query) {
                        IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                        return service.backlogStatistics(JsonUtil.json2Object(RptIndexReportQuery.class, query));
                    }
                });

        // 工作台-业务统计
        businessStatisticsCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, List<RptIndexCommonVo>>() {
                    @Override
                    public List<RptIndexCommonVo> load(String query) {
                        IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                        return service.businessStatistics(JsonUtil.json2Object(RptIndexReportQuery.class, query));
                    }
                });

        businessStatisticsCacheByMonth = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, List<RptIndexCommonVo>>() {
                    @Override
                    public List<RptIndexCommonVo> load(String query) {
                        IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                        return service.businessStatisticsByMonth(JsonUtil.json2Object(RptIndexReportQuery.class, query));
                    }
                });

        // 工作台-业绩提成
        performanceCommissionCache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)
                .build(new CacheLoader<String, RptPerformanceVo>() {
                    @Override
                    public RptPerformanceVo load(String query) {
                        IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                        return service.getPerformanceCommission(JsonUtil.json2Object(RptIndexReportQuery.class, query));
                    }
                });

        // 工作台-业绩排名
        performanceRankingCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, List<RptIndexCommonVo>>() {
                    @Override
                    public List<RptIndexCommonVo> load(String query) {
                        IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                        return service.performanceRanking(JsonUtil.json2Object(RptIndexReportQuery.class, query));
                    }
                });

        // 工作台-风控统计
        riskStatisticsCache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)
                .build(new CacheLoader<String, List<RptIndexStatisticsVo>>() {
                    @Override
                    public List<RptIndexStatisticsVo> load(String key) {
                        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
                        List<Long> hgMatchUserIdList = new ArrayList<>();
                        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listByCategory)) {
                            for (BsDictData bsDictData : listByCategory) {
                                try {
                                    String dictCd = bsDictData.getDictCd();
                                    Long matchUserId = Long.valueOf(dictCd);
                                    hgMatchUserIdList.add(matchUserId);
                                } catch (Exception e) {
                                }
                            }
                        }
                        IRptIndexStatisticsClient service = SpringContextHolder.getBean(IRptIndexStatisticsClient.class);
                        RptIndexStatisticsReqVo vo = new RptIndexStatisticsReqVo();
                        vo.setHgMatchUserIdList(hgMatchUserIdList);
                        return service.findIndexRiskStatistics(vo);
                    }
                });

        // 工作台-财务统计
        financeStatisticsCache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)
                .build(new CacheLoader<String, List<RptIndexStatisticsVo>>() {
                    @Override
                    public List<RptIndexStatisticsVo> load(String key) {
                        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
                        List<Long> hgMatchUserIdList = new ArrayList<>();
                        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listByCategory)) {
                            for (BsDictData bsDictData : listByCategory) {
                                try {
                                    String dictCd = bsDictData.getDictCd();
                                    Long matchUserId = Long.valueOf(dictCd);
                                    hgMatchUserIdList.add(matchUserId);
                                } catch (Exception e) {
                                }
                            }
                        }
                        IRptIndexStatisticsClient service = SpringContextHolder.getBean(IRptIndexStatisticsClient.class);
                        RptIndexStatisticsReqVo vo = new RptIndexStatisticsReqVo(key);
                        List<BsDictData> companyOurFalagList = BsCompanyOurUtil.getCompanyOurFlagToBsDictDataList();
                        // 财务统计
                        List<String> ourCompanyNames = companyOurFalagList.stream()
                                .map(data -> data.getDictName().trim()) // 去除每个 dictName 两端的空格
                                .collect(Collectors.toList());
                        vo.setOurCompanyNameList(ourCompanyNames);
                        vo.setHgMatchUserIdList(hgMatchUserIdList);
                        return service.findIndexFinanceStatistics(vo);
                    }
                });

        // 工作台-到期应收
        unPayFinanceCache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)
                .build(new CacheLoader<String, List<RptIndexStatisticsVo>>() {
                    @Override
                    public List<RptIndexStatisticsVo> load(String key) {
                        IRptIndexStatisticsClient service = SpringContextHolder.getBean(IRptIndexStatisticsClient.class);
                        return service.findSellUnPayFinance();
                    }
                });

        // 工作台-未收款统计
        unPayStatisticsCache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)
                .build(new CacheLoader<String, List<RptIndexStatisticsVo>>() {
                    @Override
                    public List<RptIndexStatisticsVo> load(String key) {
                        IRptIndexStatisticsClient service = SpringContextHolder.getBean(IRptIndexStatisticsClient.class);
                        return service.findUnPayStatistics();
                    }
                });

        // 工作台-过去10周/月 赊销、代采、自营、合计
        salesDataCache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)
                .build(new CacheLoader<Integer, List<RptIndexStatisticsVo>>() {
                    @Override
                    public List<RptIndexStatisticsVo> load(Integer key) {
                        return getSalesData(key);
                    }
                });
        
        // 工作台-过去10个月的毛利率
        grossProfitMarginCache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)
                .build(new CacheLoader<String, List<RptGrossProfitMarginVo>>() {
                    @Override
                    public List<RptGrossProfitMarginVo> load(String key) {
                        IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                        RptGrossProfitMarginSearchVo searchVo = new RptGrossProfitMarginSearchVo();
                        // 获取查询部门列表
                        String deptIdStr = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_INDEX_CONFIG, BasConstants.DICT_TYPE_INDEX_CONFIG_DEPTIDS);
                        if(com.spt.bas.web.util.StringUtils.isNotEmpty(deptIdStr)) {
                            List<Long> deptIds = new ArrayList<>();
                            String[] deptIdArr = deptIdStr.split(",");
                            for (String idStr : deptIdArr) {
                                try {
                                    deptIds.add(Long.parseLong(idStr));
                                } catch (Exception e) {
                                    log.error("数据字典【indexConfig-deptIds】部门ID：String转Long失败：", e);
                                }
                            }
                            searchVo.setDeptIdList(deptIds);
                        }
                        return service.grossProfitMargin(searchVo);
                    }
                });

        LocalCacheManager.register(toDoStatisticsCache);
        LocalCacheManager.register(businessStatisticsCache);
        LocalCacheManager.register(businessStatisticsCacheByMonth);
        LocalCacheManager.register(performanceCommissionCache);
        LocalCacheManager.register(performanceRankingCache);
        LocalCacheManager.register(riskStatisticsCache);
        LocalCacheManager.register(financeStatisticsCache);
        LocalCacheManager.register(unPayFinanceCache);
        LocalCacheManager.register(unPayStatisticsCache);
        LocalCacheManager.register(salesDataCache);
        LocalCacheManager.register(grossProfitMarginCache);
        IS_IN_IT.set(true);
    }

    /**
     * 待办统计-数据缓存查询
     *
     * @return
     */
    public static RptToDoStatisticsVo getToDoStatistics(RptIndexReportQuery query) {
        RptToDoStatisticsVo result = new RptToDoStatisticsVo();
        try {
            if (toDoStatisticsCache != null) {
                result = toDoStatisticsCache.get(JsonUtil.obj2Json(query));
            } else {
                IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                return service.backlogStatistics(query);
            }
        } catch (Exception e) {
            log.error("getToDoStatistics error:{}", e);
        }
        return result;
    }

    /**
     * 业务统计-数据缓存查询
     *
     * @return
     */
    public static List<RptIndexCommonVo> getBusinessStatistics(RptIndexReportQuery query) {
        List<RptIndexCommonVo> resultList = new ArrayList<>();
        try {
            if (businessStatisticsCache != null) {
                resultList = businessStatisticsCache.get(JsonUtil.obj2Json(query));
            } else {
                IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                return service.businessStatistics(query);
            }
        } catch (Exception e) {
            log.error("getBusinessStatistics error:{}", e);
        }
        return resultList;
    }

    /**
     * 业务统计-数据缓存查询 历史月份
     *
     * @return
     */
    public static List<RptIndexCommonVo> getBusinessStatisticsByMonth(RptIndexReportQuery query) {
        List<RptIndexCommonVo> resultList = new ArrayList<>();
        try {
            if (businessStatisticsCacheByMonth != null) {
                resultList = businessStatisticsCacheByMonth.get(JsonUtil.obj2Json(query));
            } else {
                IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                return service.businessStatisticsByMonth(query);
            }
        } catch (Exception e) {
            log.error("getBusinessStatistics error:{}", e);
        }
        return resultList;
    }

    /**
     * 业绩提成-数据缓存查询
     *
     * @return
     */
    public static RptPerformanceVo getPerformanceCommissionCache(RptIndexReportQuery query) {
        RptPerformanceVo result = new RptPerformanceVo();
        try {
            if (performanceCommissionCache != null) {
                result = performanceCommissionCache.get(JsonUtil.obj2Json(query));
            } else {
                IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                return service.getPerformanceCommission(query);
            }
        } catch (Exception e) {
            log.error("getToDoStatistics error:{}", e);
        }
        return result;
    }

    /**
     * 业绩排名-数据缓存查询
     *
     * @return
     */
    public static List<RptIndexCommonVo> getPerformanceRanking(RptIndexReportQuery query) {
        List<RptIndexCommonVo> resultList = new ArrayList<>();
        try {
            if (performanceRankingCache != null) {
                resultList = performanceRankingCache.get(JsonUtil.obj2Json(query));
            } else {
                IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                return service.performanceRanking(query);
            }
        } catch (Exception e) {
            log.error("getPerformanceRanking error:{}", e);
        }
        return resultList;
    }

    /**
     * 风控统计-数据缓存查询
     *
     * @return
     */
    public static List<RptIndexStatisticsVo> getRiskStatistics(RptIndexStatisticsReqVo vo) {
        List<RptIndexStatisticsVo> resultList = new ArrayList<>();
        try {
            if (riskStatisticsCache != null) {
                resultList = riskStatisticsCache.get("ALL");
            } else {
                IRptIndexStatisticsClient service = SpringContextHolder.getBean(IRptIndexStatisticsClient.class);
                return service.findIndexRiskStatistics(vo);
            }
        } catch (Exception e) {
            log.error("getRiskStatistics error:{}", e);
        }
        return resultList;
    }

    /**
     * 财务统计-数据缓存查询
     *
     * @return
     */
    public static List<RptIndexStatisticsVo> getFinanceStatistics(RptIndexStatisticsReqVo vo) {
        List<RptIndexStatisticsVo> resultList = new ArrayList<>();
        try {
            if (financeStatisticsCache != null) {
                String ourCompanyName = StringUtils.isEmpty(vo.getOurCompanyName()) ? "" : vo.getOurCompanyName();
                String businessType = StringUtils.isEmpty(vo.getBusinessType()) ? "" : vo.getBusinessType();
                resultList = financeStatisticsCache.get(ourCompanyName+","+businessType);
            } else {
                IRptIndexStatisticsClient service = SpringContextHolder.getBean(IRptIndexStatisticsClient.class);
                return service.findIndexFinanceStatistics(vo);
            }
        } catch (Exception e) {
            log.error("getFinanceStatistics error:{}", e);
        }
        return resultList;
    }

    /**
     * 到期应收-数据缓存查询
     *
     * @return
     */
    public static List<RptIndexStatisticsVo> getUnPayFinance() {
        List<RptIndexStatisticsVo> resultList = new ArrayList<>();
        try {
            if (unPayFinanceCache != null) {
                resultList = unPayFinanceCache.get("ALL");
            } else {
                IRptIndexStatisticsClient service = SpringContextHolder.getBean(IRptIndexStatisticsClient.class);
                return service.findSellUnPayFinance();
            }
        } catch (Exception e) {
            log.error("getUnPayFinance error:{}", e);
        }
        return resultList;
    }

    /**
     * 未收款统计-数据缓存查询
     *
     * @return
     */
    public static List<RptIndexStatisticsVo> getUnPayStatistics() {
        List<RptIndexStatisticsVo> resultList = new ArrayList<>();
        try {
            if (unPayStatisticsCache != null) {
                resultList = unPayStatisticsCache.get("ALL");
            } else {
                IRptIndexStatisticsClient service = SpringContextHolder.getBean(IRptIndexStatisticsClient.class);
                return service.findUnPayStatistics();
            }
        } catch (Exception e) {
            log.error("getUnPayStatistics error:{}", e);
        }
        return resultList;
    }

    /**
     * 销售周-过去10周 赊销、代采、自营、合计 数据
     * 销售月-过去10月 赊销、代采、自营、合计 数据
     * type = 1 赊销(销售周)
     * type = 2 代采(销售周)
     * type = 3 自营(销售周)
     * type = 4 合计(销售周)
     * <p>
     * type = 5 赊销(销售月)
     * type = 6 代采(销售月)
     * type = 7 自营(销售月)
     * type = 8 合计(销售月)
     *
     * @param type
     * @return
     */
    public static List<RptIndexStatisticsVo> getSalesList(Integer type) {
        List<RptIndexStatisticsVo> resultList = new ArrayList<>();
        try {
            if (salesDataCache != null) {
                resultList = salesDataCache.get(type);
            } else {
                return getSalesData(type);
            }
        } catch (Exception e) {
            log.error("getSalesList error:{}", e);
        }
        return resultList;
    }

    /**
     * 获取过去10个月的毛利率
     *
     * @return
     */
    public static List<RptGrossProfitMarginVo> getGrossProfitMargin() {
        List<RptGrossProfitMarginVo> resultList = new ArrayList<>();
        RptGrossProfitMarginSearchVo searchVo = new RptGrossProfitMarginSearchVo();
        // 获取查询部门列表
        String deptIdStr = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_INDEX_CONFIG, BasConstants.DICT_TYPE_INDEX_CONFIG_DEPTIDS);
        if(com.spt.bas.web.util.StringUtils.isNotEmpty(deptIdStr)) {
            List<Long> deptIds = new ArrayList<>();
            String[] deptIdArr = deptIdStr.split(",");
            for (String idStr : deptIdArr) {
                try {
                    deptIds.add(Long.parseLong(idStr));
                } catch (Exception e) {
                    log.error("数据字典【indexConfig-deptIds】部门ID：String转Long失败：", e);
                }
            }
            searchVo.setDeptIdList(deptIds);
        }
        try {
            if (grossProfitMarginCache != null) {
                resultList = grossProfitMarginCache.get("ALL");
            } else {
                IRptIndexReportClient service = SpringContextHolder.getBean(IRptIndexReportClient.class);
                return service.grossProfitMargin(searchVo);
            }
        } catch (Exception e) {
            log.error("getGrossProfitMargin error:{}", e);
        }
        return resultList;
    }


    /**
     * 查询 销售周、销售月相关 数据
     *
     * @param type
     * @return
     */
    public static List<RptIndexStatisticsVo> getSalesData(Integer type) {
        List<RptIndexStatisticsVo> resultData = new ArrayList<>();
        RptIndexStatisticsReqVo reqVo = getReqVo(type);
        IRptIndexStatisticsClient service = SpringContextHolder.getBean(IRptIndexStatisticsClient.class);
        if (ReportConstant.SALES_WEEK_TYPE.contains(type)) {
            resultData = service.findSalesTenWeekData(reqVo);
        } else if (ReportConstant.SALES_WEEK_TYPE_4.equals(type)) {
            resultData = service.findTotalSalesWeekData(reqVo);
        } else if (ReportConstant.SALES_MONTH_TYPE.contains(type)) {
            resultData = service.findSalesTenMonthData(reqVo);
        } else if (ReportConstant.SALES_MONTH_TYPE_8.equals(type)) {
            resultData = service.findTotalSalesMonthData(reqVo);
        }
        return resultData;
    }

    public static RptIndexStatisticsReqVo getReqVo(Integer type) {
        RptIndexStatisticsReqVo reqVo = new RptIndexStatisticsReqVo();
        if (ReportConstant.SALES_WEEK_TYPE_1.equals(type) || ReportConstant.SALES_MONTH_TYPE_5.equals(type)) {
            // 赊销业务金额统计
            reqVo.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB);
            reqVo.setMatchCreditFlg(true);
        } else if (ReportConstant.SALES_WEEK_TYPE_2.equals(type) || ReportConstant.SALES_MONTH_TYPE_6.equals(type)) {
            // 代采业务金额统计
            reqVo.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB);
            reqVo.setMatchCreditFlg(false);
        } else if (ReportConstant.SALES_WEEK_TYPE_3.equals(type) || ReportConstant.SALES_MONTH_TYPE_7.equals(type)) {
            // 自营业务金额统计
            reqVo.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_XS);
        }
        return reqVo;
    }
}
