package com.spt.bas.purchase.wx.server.payload;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *  图片上传base64请求参数
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-24 10:05
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
//@ApiModel(value = "UploadBase64Request", description = "上传接口参数")
public class UploadBase64Request {
    /**
     * 文件名
     */
    //@ApiModelProperty(value = "文件名", required = true)
    private String fileName;

    /**
     * 图片base64文件编码
     */
    //@ApiModelProperty(value = "图片base64文件编码", required = true)
    @NotBlank(message = "图片base64文件编码不能为空")
    private String base64Data;

    /**
     * 图片类型
     */
    //@ApiModelProperty(value = "图片类型")
    private String cardType;

    /**
     * 方向
     */
    //@ApiModelProperty(value = "方向")
    private String direction;

    /**
     * 是否登录
     */
    //@ApiModelProperty(value = "是否登录")
    private Boolean loginFlg = true;
}
