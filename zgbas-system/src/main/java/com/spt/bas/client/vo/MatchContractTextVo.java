package com.spt.bas.client.vo;

/**
 * 代采赊销预算合同预览Vo
 * @Author: gaojy
 * @create 2021/12/3 10:26
 * @version: 1.0
 * @description:
 */
public class MatchContractTextVo {
    private Long templateId;
    private Long matchApplyId;
    private Long enterpriseId;
    private Boolean specialFlag = false;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getMatchApplyId() {
        return matchApplyId;
    }

    public void setMatchApplyId(Long matchApplyId) {
        this.matchApplyId = matchApplyId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Boolean getSpecialFlag() {
        return specialFlag;
    }

    public void setSpecialFlag(Boolean specialFlag) {
        this.specialFlag = specialFlag;
    }

    public MatchContractTextVo() {
    }

    public MatchContractTextVo(Long templateId, Long matchApplyId, Long enterpriseId) {
        this.templateId = templateId;
        this.matchApplyId = matchApplyId;
        this.enterpriseId = enterpriseId;
    }

    public MatchContractTextVo(Long matchApplyId, Long enterpriseId, Boolean specialFlag) {
        this.matchApplyId = matchApplyId;
        this.enterpriseId = enterpriseId;
        this.specialFlag = specialFlag;
    }
}
