// Phase 4 stub — Phase 5 will overlay with complete source version
package com.spt.bas.purchase.wx.server.vo;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * <p>
 *  个人信息返回数据
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 11:53
 */
@Data
@Builder
//@ApiModel(value = "登录接口个人信息返回数据")
public class UserInfoVo {
    /**
     * 用户id
     */
    //@ApiModelProperty(value = "用户id", dataType = "Long", required = true)
    private Long userId;

    /**
     * 姓名
     */
    //@ApiModelProperty(value = "姓名", dataType = "String", required = true)
    private String name;

    /**
     * 手机号
     */
    //@ApiModelProperty(value = "手机号", dataType = "String", required = true)
    private String phone;

    /**
     * 登录凭证
     */
    //@ApiModelProperty(value = "登录凭证", dataType = "String", required = true)
    private String accessToken;

    /**
     * 信息完善步骤
     */
    //@ApiModelProperty(value = "信息完善步骤", dataType = "String", required = true)
    private Integer infoStep;

    /**
     * 公司名
     */
    //@ApiModelProperty(value = "公司名", dataType = "String", required = false)
    private String companyName;

    /**
     * 公司ID
     */
    //@ApiModelProperty(value = "公司ID", dataType = "String", required = false)
    private Long companyId;

    private Long sessionId;

    private String openId;

    /**
     * 是否阅读过权益知情书，1：已阅读，0：未阅读
     */
    private Integer informedConsentFlag;
}
