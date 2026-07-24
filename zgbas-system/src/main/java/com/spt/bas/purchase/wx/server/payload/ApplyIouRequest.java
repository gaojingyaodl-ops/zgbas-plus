package com.spt.bas.purchase.wx.server.payload;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 *  白条申请
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-23 17:35
 */
@Data
@Builder
public class ApplyIouRequest {
    private String userId;

    /**
     *客户申请额度
     */
    private BigDecimal applyCreditAmount;

    /**
     *回款周期
     */
    private int creditDays;

    /**
     *企业征信授权书
     */
    private String corporateCreditId;

    /**
     *个人征信授权书
     */
    private String personalCreditId;

    /**
     *商标注册证
     */
    private String trademarkId;

    /**
     *专利说明书
     */
    private String patentId;

    /**
     *个人担保函
     */
    private String personalGuaranteeId;

    /**
     *资产担保函
     */
    private String assetGuaranteeId;

    /**
     *资产负债表
     */
    private String assetsId;

    /**
     *现金流量表
     */
    private String cashFlowId;

    /**
     *利润表
     */
    private String profitId;

    /**
     *审计报告表
     */
    private String auditReportId;

    /**
     *土地证明
     */
    private String landId;

    /**
     *土地类型
     */
    private String landType;

    /**
     *厂房证明
     */
    private String plantId;

    /**
     *厂房类型
     */
    private String plantType;

    /**
     *机械设备证明
     */
    private String equipmentId;

    /**
     *机械设备类型
     */
    private String equipmentType;

}
