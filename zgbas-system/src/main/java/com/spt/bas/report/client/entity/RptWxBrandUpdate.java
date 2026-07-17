package com.spt.bas.report.client.entity;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-13 16:43
 */
public class RptWxBrandUpdate {
    /**
     * 微信用户id
     */
    private Long userId;
    /**
     * 品种
     */
    private String productCd;
    /**
     * 牌号
     */
    private List<RptWxBrandFollow> brandLists;

    public List<RptWxBrandFollow> getBrandLists() {
        return brandLists;
    }

    private Long productTypeId;

    public void setBrandLists(List<RptWxBrandFollow> brandLists) {
        this.brandLists = brandLists;
    }

    public Long getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Long productTypeId) {
        this.productTypeId = productTypeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
    }
}
