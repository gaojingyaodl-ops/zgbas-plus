package com.spt.bas.purchase.wx.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-20 23:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyVo {
    /**
     * 法人代表姓名
     */
    private String legalRepresent;

    /**
     * 身份证号
     */
    private String identityCardNumber;

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
     * 邮箱
     */
    private String email;

    /**
     * 营业执照图片ID
     */
    private String businessLicenseUrl;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 证件正面图片id
     */
    private String legalPersonPicUrl;

    /**
     * 证件反面图片id
     */
    private String legalPersonOppositePicUrl;

    /**
     * 证件类型 0：身份证 1：港澳通行证 2：护照 3：台胞证
     */
    private String cardType;
}
