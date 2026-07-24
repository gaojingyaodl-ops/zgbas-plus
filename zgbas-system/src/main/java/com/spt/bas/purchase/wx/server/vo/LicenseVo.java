package com.spt.bas.purchase.wx.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *     营业执照信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-20 14:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LicenseVo {
    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 营业执照编号
     */
    private String licenseNumber;

    /**
     * 地址
     */
    private String address;

    /**
     * 营业执照保存地址
     */
    private String businessLicenseUrl;

    /**
     * 法人
     */
    private String legalRepresent;

}
