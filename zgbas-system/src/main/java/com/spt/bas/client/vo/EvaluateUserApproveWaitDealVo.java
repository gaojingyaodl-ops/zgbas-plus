package com.spt.bas.client.vo;

import com.spt.bas.client.entity.EvaluateUser;

import java.util.Date;
import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/25 19:10
 */

public class EvaluateUserApproveWaitDealVo {
    /**
     * id集合
     */
    private String evaluateUserIds;

    /**
     * 代办事项通知人 id
     */
    private List<String> userIds;
    /**
     * 年-月
     */
    private String yearAndMonth;

    /**
     * 审核日期
     */
    private Date evaluateDate;
    /**
     * 企业账号 id
     */
    private Long enterpriseId;

    /**
     * 申诉邮件发送人 id 集合
     */
    private List<String> enterpriseAppealEmail;

    /**
     * 考核人
     */
    private EvaluateUser evaluateUser;

    /**
     * 考核明细
     */
    private String appealRemark;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public EvaluateUser getEvaluateUser() {
        return evaluateUser;
    }

    public void setEvaluateUser(EvaluateUser evaluateUser) {
        this.evaluateUser = evaluateUser;
    }

    public String getAppealRemark() {
        return appealRemark;
    }

    public void setAppealRemark(String appealRemark) {
        this.appealRemark = appealRemark;
    }

    public List<String> getEnterpriseAppealEmail() {
        return enterpriseAppealEmail;
    }

    public void setEnterpriseAppealEmail(List<String> enterpriseAppealEmail) {
        this.enterpriseAppealEmail = enterpriseAppealEmail;
    }

    public String getEvaluateUserIds() {
        return evaluateUserIds;
    }

    public void setEvaluateUserIds(String evaluateUserIds) {
        this.evaluateUserIds = evaluateUserIds;
    }

    public String getYearAndMonth() {
        return yearAndMonth;
    }

    public void setYearAndMonth(String yearAndMonth) {
        this.yearAndMonth = yearAndMonth;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Date getEvaluateDate() {
        return evaluateDate;
    }

    public void setEvaluateDate(Date evaluateDate) {
        this.evaluateDate = evaluateDate;
    }
}
