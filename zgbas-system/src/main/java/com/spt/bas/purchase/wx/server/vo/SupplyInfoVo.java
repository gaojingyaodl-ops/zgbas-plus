package com.spt.bas.purchase.wx.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  补充信息资料
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-30 11:42
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplyInfoVo {
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
