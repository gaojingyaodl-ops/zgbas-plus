package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;

import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/9/11 17:37
 * @Version 1.0
 */
public class ApplyInvalidDetailVo {
    private CtrContract buyContract;

    private CtrContract sellContract;

    private List<CtrProduct> productList;

    private List<String> invalidTypeList;

    private String tradeChain;

    public CtrContract getBuyContract() {
        return buyContract;
    }

    public void setBuyContract(CtrContract buyContract) {
        this.buyContract = buyContract;
    }

    public CtrContract getSellContract() {
        return sellContract;
    }

    public void setSellContract(CtrContract sellContract) {
        this.sellContract = sellContract;
    }

    public List<CtrProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<CtrProduct> productList) {
        this.productList = productList;
    }

    public String getTradeChain() {
        return tradeChain;
    }

    public void setTradeChain(String tradeChain) {
        this.tradeChain = tradeChain;
    }

    public List<String> getInvalidTypeList() {
        return invalidTypeList;
    }

    public void setInvalidTypeList(List<String> invalidTypeList) {
        this.invalidTypeList = invalidTypeList;
    }
}
