package com.spt.bas.purchase.wx.server.common;

/**
 * <p>
 *  首页信息步骤状态
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 21:22
 */
public enum InfoStep {
    /**
     * 默认值
     */
    DEFAULT(1,"默认值"),

    /**
     * 设置初始密码
     */
    INITIAL_PASSWORD(2,"设置初始密码"),


    QUOTA_TEST(3, "额度测试"),

    /**
     * 填写基本信息
     */
    BASE_INFO(4,"上传证件"),

    /**
     * 签署委托授权
     */
    SIGN_AUTHORIZATION(5,"委托授权签署"),

    /**
     * 入金验证
     */
    VERIFICATION_OF_DEPOSIT(6,"入金验证"),

    /**
     * 入金完成
     */
    VOD_FINISH(7,"入金完成"),

    /**
     * 补充资料审核中
     */
    SUPPLY_INFO_CHECKING(8,"补充资料审核中"),

    /**
     * 补充资料审核完成
     */
    SUPPLY_INFO_FINISH(9,"补充资料审核完成"),

    ACCESS(10, "已准入"),

    /**
     * 完成
     */
    FINISH(99,"完成");

    /**
     * 状态步骤
     */
    private Integer infoStep;

    /**
     * 描述
     */
    private String description;

    InfoStep(Integer infoStep, String description) {
        this.infoStep = infoStep;
        this.description = description;
    }

    public Integer getInfoStep() {
        return infoStep;
    }

    public void setInfoStep(Integer infoStep) {
        this.infoStep = infoStep;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Status:{infoStep=%s, description=%s}", getInfoStep(), getDescription());
    }

}
