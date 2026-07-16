package com.spt.bas.client.vo;

public class ApproveWaitDealVo {
    //企业账套ID
    private Long enterpriseId;
    //责任人ID
    private String relaUserId;
    //责任人所在部门id
    private Long relaDeptId;
    //事项类型
    private String dealType;
    //摘要
    private String subject;
    //创建人id
    private Long createdUserId;
    //已读状态
    private String readFlg;
    //完成状态
    private String completeFlg;
    //关联业务表id
    private Long relationId;
    //关联业务表
    private String relationTable;
    //来源
    private String source;

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getRelaUserId() {
        return relaUserId;
    }

    public void setRelaUserId(String relaUserId) {
        this.relaUserId = relaUserId;
    }

    public Long getRelaDeptId() {
        return relaDeptId;
    }

    public void setRelaDeptId(Long relaDeptId) {
        this.relaDeptId = relaDeptId;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(Long createdUserId) {
        this.createdUserId = createdUserId;
    }

    public String getReadFlg() {
        return readFlg;
    }

    public void setReadFlg(String readFlg) {
        this.readFlg = readFlg;
    }

    public String getCompleteFlg() {
        return completeFlg;
    }

    public void setCompleteFlg(String completeFlg) {
        this.completeFlg = completeFlg;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public String getRelationTable() {
        return relationTable;
    }

    public void setRelationTable(String relationTable) {
        this.relationTable = relationTable;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
