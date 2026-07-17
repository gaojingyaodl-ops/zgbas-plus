package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/4/25 15:32
 */

public class RptAssessmentSearch extends PageSearchVo {

    /**
     * 月度数据
     */
    private String yearAndMonth;
    /**
     * 季度:1:一季度，2：二季度，3：三季度：4：4:季度
     */
    private Integer quarter;

    /**
     * 开始月份
     */
    private String startMonth;

    /**
     * 结束月份
     */
    private String endMonth;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 事业部 id
     */
    private Long deptId;

    /**
     * 业务员 id
     */
    private Long userId;

    private List<Long> userIds;

    public String getYearAndMonth() {
        return yearAndMonth;
    }

    public void setYearAndMonth(String yearAndMonth) {
        this.yearAndMonth = yearAndMonth;
    }

    public String getStartMonth() {
        String m = null;
        if (quarter != null) {
            switch (quarter) {
                case 1:
                    m = "01";
                    break;
                case 2:
                    m = "04";
                    break;
                case 3:
                    m = "07";
                    break;
                case 4:
                    m = "10";
                    break;
            }
        }
        this.startMonth = m;
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getEndMonth() {
        String m = null;
        if (quarter != null) {
            switch (quarter) {
                case 1:
                    m = "03";
                    break;
                case 2:
                    m = "06";
                    break;
                case 3:
                    m = "09";
                    break;
                case 4:
                    m = "12";
                    break;
            }
        }
        return m;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getQuarter() {
        return quarter;
    }

    public void setQuarter(Integer quarter) {
        this.quarter = quarter;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
