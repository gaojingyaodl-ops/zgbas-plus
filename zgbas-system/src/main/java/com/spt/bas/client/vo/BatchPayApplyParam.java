package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 批量发起付款申请配置项
 * @Author MoonLight
 * @Date 2023/3/2 14:28
 * @Version 1.0
 */
public class BatchPayApplyParam {
    /**
     * 每批次对应货物吨数
     */
    private BigDecimal batchNumber;

    /**
     * 可批量发起付款的货物类型
     */
    private List<String> batchProductCd;

    public BigDecimal getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(BigDecimal batchNumber) {
        this.batchNumber = batchNumber;
    }

    public List<String> getBatchProductCd() {
        return batchProductCd;
    }

    public void setBatchProductCd(List<String> batchProductCd) {
        this.batchProductCd = batchProductCd;
    }
}
