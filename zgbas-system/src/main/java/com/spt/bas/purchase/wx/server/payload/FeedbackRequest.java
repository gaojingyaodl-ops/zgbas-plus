package com.spt.bas.purchase.wx.server.payload;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 *  意见反馈
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-14 18:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@ApiModel(value = "意见反馈")
public class FeedbackRequest {

    //@ApiModelProperty(value = "意见反馈的类型", required = true)
    @NotBlank(message = "意见反馈类型不能为空")
    private String feedbackType;

    @NotBlank(message = "意见反馈内容不能为空")
    //@ApiModelProperty(value = "意见反馈的内容", required = true)
    private String feedbackContent;

    //@ApiModelProperty(value = "截图的ID列表")
    private String attachIds;

    @NotBlank(message = "联系方式不能为空")
    //@ApiModelProperty(value = "联系方式", required = true)
    private String contact;

    //@ApiModelProperty(value = "是否允许48小时与企业联系，0为是，1为否", required = true)
    @NotBlank(message = "canContact不能为空")
    private String canContact;

}
