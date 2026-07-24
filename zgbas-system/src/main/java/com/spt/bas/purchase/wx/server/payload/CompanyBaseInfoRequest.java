package com.spt.bas.purchase.wx.server.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  基本信息-上传证件步骤
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-30 11:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyBaseInfoRequest {


    /**
     * 法人代表姓名
     */
    private String legalRepresent;

    /**
     * 身份证号
     */
    private String identityCardNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 营业执照副本加盖公章附件ID
     */
    private String businessLicenseWithSealUrl;

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

    private Long companyId;
}
