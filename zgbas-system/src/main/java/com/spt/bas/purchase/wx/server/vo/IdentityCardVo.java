package com.spt.bas.purchase.wx.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  身份证字段
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-20 23:18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentityCardVo {
    /**
     * 姓名
     */
    private String legalRepresent;

    /**
     * 证件号
     */
    private String identityCardNumber;

    /**
     * 证件类型 0：身份证 1：港澳通行证  2：护照 3：台胞证
     */
    private String cardType;

    /**
     * 0：正面 1：反面
     */
    private String direction;

    /**
     * 身份证反面url
     */
    private String legalPersonOppositePicUrl;

    /**
     * 身份证正面
     */
    private String legalPersonPicUrl;

}
