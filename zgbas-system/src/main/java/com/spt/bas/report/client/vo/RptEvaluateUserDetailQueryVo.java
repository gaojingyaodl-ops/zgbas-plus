package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/24 18:20
 */

public class RptEvaluateUserDetailQueryVo extends PageSearchVo {
    /**
     * 考核人员 id
     */
    private Long evaluateUserId;

    /**
     * 年-月
     */
    private String yearAndMonth;

    /**
     * 考核人员名字
     */
    private String evaluateName;

    /**
     * 0-考评，1-详情，2-已考评，3-审核
     */
    private String type="0";

    /**
     * 评分部门
     */
    private String evaluateDept;

    /**
     * 当前登录人 id
     */
    private Long currentUserId;

    /**
     * 考核人所属编码
     */
    private String branchCd;

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getEvaluateDept() {
        return evaluateDept;
    }

    public void setEvaluateDept(String evaluateDept) {
        this.evaluateDept = evaluateDept;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getEvaluateUserId() {
        return evaluateUserId;
    }

    public void setEvaluateUserId(Long evaluateUserId) {
        this.evaluateUserId = evaluateUserId;
    }

    public String getYearAndMonth() {
        return yearAndMonth;
    }

    public void setYearAndMonth(String yearAndMonth) {
        this.yearAndMonth = yearAndMonth;
    }

    public String getEvaluateName() {
        return evaluateName;
    }

    public void setEvaluateName(String evaluateName) {
        this.evaluateName = evaluateName;
    }
}
