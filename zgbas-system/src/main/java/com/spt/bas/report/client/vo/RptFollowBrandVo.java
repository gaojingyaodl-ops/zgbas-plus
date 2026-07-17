package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-13 15:46
 */
public class RptFollowBrandVo {
    /**
     * 品种
     */
    private String productCd;

    /**
     * 关注该品种的企业的家数
     */
    private int factoryNumber;

    /**
     * 企业已关注的该品种牌号的数量
     */
    private int attentedNumber;

    /**
     * 该品种下的总牌号数量
     */
    private int brandNumber;

    /**
     * 已关注牌号list
     */
    private List<RptBrandVo> attentedBrandList;

    /**
     *
     */
    private List<RptBrandVo> unAttentedBrandList;

    private Long productTypeId;

    private String productName;

    @JsonIgnore
    private Long userId;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<RptBrandVo> getAttentedBrandList() {
        return attentedBrandList;
    }

    public void setAttentedBrandList(List<RptBrandVo> attentedBrandList) {
        this.attentedBrandList = attentedBrandList;
    }

    public List<RptBrandVo> getUnAttentedBrandList() {
        return unAttentedBrandList;
    }

    public void setUnAttentedBrandList(List<RptBrandVo> unAttentedBrandList) {
        this.unAttentedBrandList = unAttentedBrandList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Long productTypeId) {
        this.productTypeId = productTypeId;
    }

    public int getFactoryNumber() {
        return factoryNumber;
    }

    public void setFactoryNumber(int factoryNumber) {
        this.factoryNumber = factoryNumber;
    }

    public int getAttentedNumber() {
        return attentedNumber;
    }

    public void setAttentedNumber(int attentedNumber) {
        this.attentedNumber = attentedNumber;
    }

    public int getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(int brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
    }
}
