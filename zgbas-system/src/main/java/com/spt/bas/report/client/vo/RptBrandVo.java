package com.spt.bas.report.client.vo;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-13 15:41
 */
public class RptBrandVo {
    // 牌号名
    private String name;
    // 关注人数
    private int factoryNumber;
    // 牌号id
    private Long brandId;

    public int getFactoryNumber() {
        return factoryNumber;
    }

    public void setFactoryNumber(int factoryNumber) {
        this.factoryNumber = factoryNumber;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
