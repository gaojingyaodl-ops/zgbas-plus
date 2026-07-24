package com.spt.bas.purchase.wx.server.payload;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spt.bas.client.entity.BsCompany;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *     公司信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-18 20:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@ApiModel(value = "CompanyInfoRequest", description = "公司信息参数")
public class CompanyInfoRequest {

    /**
     * 法人代表姓名
     */
    //@ApiModelProperty(value = "法人代表姓名")
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
     * 证件正面图片id
     */
    private String legalPersonPicUrl;

    /**
     * 证件反面图片id
     */
    private String legalPersonOppositePicUrl;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 证件类型 0：身份证 1：港澳通行证 2：护照 3：台胞证
     */
    private String cardType;

    /**
     * 保存类型
     */
    @JsonIgnore
    private String saveType;

}
