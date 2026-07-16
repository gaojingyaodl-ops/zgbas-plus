package com.spt.bas.client.vo;

import lombok.Data;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/11 14:53
 */
@Data
public class SupplierApplyVo {
    /**
     * 备注
     */
    private String supplierSecondRemark;

    /**
     * 备注
     */
    private String supplierRemark;

    /**
     * 供应商准入审批
     */
    private String supplierRating;

    /**
     * 采购额度（元）
     */
    private Long supplierPurchaseAmount;

    /**
     * 预付款额度（元）
     */
    private Long supplierPrepayAmount;

    /**
     * 供应商级别
     */
    private String supplierLevel;

    /**
     * 供应商期货
     */
    private String supplierFutureOne;

    /**
     * 供应商配送
     */
    private String supplierDeliveryOne;

    /**
     * 供应商配送
     */
    private String supplierDelivery;

    /**
     * 企业性质
     */
    private String companyCategory;

    private String supplierCategory;

    /**
     * 成立日期
     */
    private String startDate;

    /**
     * 股东背景
     */
    private String shareholder;

    /**
     * 注册资本（万）
     */
    private String registerCapital;

    /**
     * 办公场地照片(张)
     */
    private String officeImageNumber;

    /**
     * 参保人员
     */
    private String insuredNumber;

    /**
     * 是否存在欠税记录
     */
    private String existOverDueTax;

    /**
     * 是否存在执行冻结案件
     */
    private String existJudicialFreeze;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 实际办公地址
     */
    private String businessAddress;

    /**
     * 与下游的开票照片(张)
     */
    private String billImageNumber;

    /**
     * 用户id
     */
    private Long applyUserId;

    /**
     * 用户昵称
     */
    private String applyUserName;

    /**
     * 审批id
     */
    private Long approveId;

    /**
     * 企业id
     */
    private Long enterpriseId;

    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 供应商配送备注
     */
    private String supplierDeliveryRemark;
}
