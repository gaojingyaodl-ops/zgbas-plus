package com.spt.bas.client.riskScore;

/**
 * 评分模型分类
 */
public enum RiskModelScoreType {
    insurance("insurance","人保意见"),
    creditData("creditData","信用数据"),
    historyData("historyData","历史数据"),
    taxData("taxData","税务数据"),
    otherData("otherData","其他数据"),
    oneVeto("oneVeto","一票否决");
//    business("工商信息"), trade("交易信息"), insurance("人保信息"), risk("企业风险"), research("企业调研");
    
    private String code;
    private String desc;

    RiskModelScoreType(String code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
    public String getCode() {
        return code;
    }
}
