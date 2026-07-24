package com.spt.bas.purchase.wx.client.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 2.13 人脸识别认证
 */
@Data
public class AuthFaceRecognition {

    @NotBlank(message = "姓名name不能为空")
    private String name;

    @NotBlank(message = "身份证号不能为空")
    private String idNumber;

    @NotBlank(message = "视频数据不能为空")
    private String videoStr;

}
