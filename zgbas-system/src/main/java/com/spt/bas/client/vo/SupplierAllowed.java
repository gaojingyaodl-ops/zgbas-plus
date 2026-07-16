package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplySupplierAllowed;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
public class SupplierAllowed extends ApplySupplierAllowed {
    private String supplierRemark;//供应商准入备注
    private String supplierCategory;// 供应商性质

    private String shareholder; // 股东背景
    private String registerCapital; // 注册资本（万）
    private String lastYearTaxableSales; // 去年纳税销售额
    private String unallocatedStock; // 常备库存
    private String supplierLevel; // 供应商级别
    private BigDecimal supplierPurchaseAmount = BigDecimal.ZERO; // 采购额度（元）
    private BigDecimal supplierPrepayAmount = BigDecimal.ZERO; // 预付款额度（元）
    private String supplierSecondRemark;//额度备注

    private String supplierFuture; // 是否允许供应商远期合同
    private String supplierDelivery; // 是否允许供应商配送



}