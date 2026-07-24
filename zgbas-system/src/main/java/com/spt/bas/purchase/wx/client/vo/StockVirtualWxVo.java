package com.spt.bas.purchase.wx.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/8/6 10:04
 */
public class StockVirtualWxVo extends PageSearchVo {

    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "StockVirtualVo{" +
                "productName='" + productName + '\'' +
                '}';
    }
}
