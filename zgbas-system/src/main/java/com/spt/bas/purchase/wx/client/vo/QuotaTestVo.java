package com.spt.bas.purchase.wx.client.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  额度测试保存信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-30 20:51
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuotaTestVo {
    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 营业执照号
     */
    private String licenseNumber;

    /**
     * 办公地点
     */
    private String address;

    /**
     * 企业类型
     */
    private String companyType;

    /**
     * 所属行业
     */
    private String customCompanySource;

    /**
     * 我的身份
     */
    private String customMyRole;

    /**
     * 白条额度
     */
    private String customQuota;

    /**
     * 申请还款周期
     */
    private String customRepaymentPeriod;

    /**
     * 营业执照附件id
     */
    private String businessLicenseUrl;

    /**
     * 法人
     */
    private String legalRepresent;

}
