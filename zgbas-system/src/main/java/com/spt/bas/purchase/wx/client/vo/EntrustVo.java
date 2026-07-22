package com.spt.bas.purchase.wx.client.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  委托授权信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-22 17:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntrustVo {
    /**
     * 委托人
     */
    private String clientCompany;

    /**
     * 法定代表人
     */
    private String legalRepresent;

    /**
     * 营业执照
     */
    private String licenseNumber;

    /**
     * 受托人姓名
     */
    private String trusteeName;

    /**
     * 性别 0:男 1:女
     */
    private String trusteeGender;

    /**
     * 受托人手机号
     */
    private String trusteePhone;


    /**
     * 受托人身份证号
     */
    private String identityCardNumber;


    /**
     * 与受托人关系 ；0:员工 1:法人 2:其他
     */
    private String relationShip;

    /**
     * 委托授权书附件id
     */
    private String powerOfAttorneyFileId;
    
    private String trusteePersonPicUrl;

    private String trusteePersonOppositePicUrl;
}
