package com.spt.bas.server.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.spt.bas.client.entity.BsCompany;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  基础额度档位(来源于风控系统)
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-20 10:03
 */
@Slf4j
public class BasInitialDataScoreUtil {
    private static final Integer CREDIT_DAYS_30 = 30;
    private static final Integer CREDIT_DAYS_15 = 15;
    private static final BigDecimal QUOTA_500W = new BigDecimal("5000000");
    private static final BigDecimal QUOTA_200W = new BigDecimal("2000000");
    private static final BigDecimal QUOTA_100W = new BigDecimal("1000000");
    private static final BigDecimal QUOTA_50W = new BigDecimal("500000");
    private static final BigDecimal QUOTA_30W = new BigDecimal("300000");
    private static final BigDecimal SCORE_160 = new BigDecimal("160");
    private static final BigDecimal SCORE_135 = new BigDecimal("135");
    private static final BigDecimal SCORE_115 = new BigDecimal("115");
    private static final BigDecimal SCORE_70 = new BigDecimal("70");

    private static final Map<String, Grade> GRADE_MAP = new HashMap<>();

    private static final String KEYWORD_1 = "制造";
    private static final String KEYWORD_2 = "加工";
    private static final String KEYWORD_3 = "生产";

    public static class Grade{
        private Integer days;
        private BigDecimal quota;

        public Grade(Integer days, BigDecimal quota) {
            this.days = days;
            this.quota = quota;
        }

        public Integer getDays() {
            return days;
        }

        public BigDecimal getQuota() {
            return quota;
        }
    }

    static {
        GRADE_MAP.put("A", new Grade(CREDIT_DAYS_30, QUOTA_500W));
        GRADE_MAP.put("B", new Grade(CREDIT_DAYS_30, QUOTA_200W));
        GRADE_MAP.put("C", new Grade(CREDIT_DAYS_30, QUOTA_100W));
        GRADE_MAP.put("D", new Grade(CREDIT_DAYS_30, QUOTA_50W));
        GRADE_MAP.put("E", new Grade(CREDIT_DAYS_15, QUOTA_30W));
        log.info("InitialDataScoreUtil init");
    }

    public static Grade getQuota(BigDecimal score, BsCompany company) {
        if (score.compareTo(SCORE_160) > 0) {
            return GRADE_MAP.get("A");
        }
        if (score.compareTo(SCORE_135) > 0) {
            return GRADE_MAP.get("B");
        }
        if (score.compareTo(SCORE_115) > 0) {
            return GRADE_MAP.get("C");
        }
        if (score.compareTo(SCORE_70) > 0 || checkExtraForD(company)) {
            return GRADE_MAP.get("D");
        }
        return GRADE_MAP.get("E");
    }

    /**
     * 等级D额外判断条件
     * @param company
     * @return
     */
    public static Boolean checkExtraForD(BsCompany company) {
        if (company == null) {
            return false;
        }
        // 成立日期
        String startDate = company.getStartDate();
        log.info("checkExtraForD startDate:{}", startDate);
        try{
            DateTime startTime = DateUtil.parse(startDate, "yyyy-MM-dd");
            // 成立3年后的时间
            DateTime afterThreeYear = DateUtil.offset(startTime, DateField.YEAR, 3);
            log.info("checkExtraForD afterThreeYear:{}", afterThreeYear);
            // 比较'成立3年后的时间'和当前时间
            if (DateUtil.compare(afterThreeYear, DateUtil.date()) > 0) {
                return false;
            }
        }catch (Exception e){
            log.error("checkExtraForD parseTime error", e);
            return false;
        }

        String scope = company.getScope();
        log.info("checkExtraForD scope:{}", scope);
        // 判断经营范围内关键词
        if (!StrUtil.contains(scope, KEYWORD_1) && !StrUtil.contains(scope, KEYWORD_2) && !StrUtil.contains(scope, KEYWORD_3)) {
            return false;
        }
        return true;
    }
}
