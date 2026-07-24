package com.spt.bas.purchase.wx.server.payload;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *  量身定制请求参数
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-17 15:04
 */
@Data
//@ApiModel(value = "量身定制请求参数", description = "量身定制请求参数")
public class CustomRequest {
    /**
     * 量身定制公司类型 0：贸易商 1：终端工厂
     */
    @NotBlank(message = "公司类型不能为空")
    //@ApiModelProperty(value = "量身定制公司类型 0：贸易商 1：终端工厂", required = true)
    private String companyType;

    /**
     * 量身定制企业类型：0：基础化工 1：通用塑料
     */
    @NotBlank(message = "企业类型不能为空")
    //@ApiModelProperty(value = "量身定制企业类型：0：基础化工 1：通用塑料", required = true)
    private String customCompanySource;

    /**
     *  量身定制 我的角色 0：企业主 1：采购经理 2：业务员
     */
    @NotBlank(message = "我的角色不能为空")
    //@ApiModelProperty(value = "量身定制 我的角色 0：企业主 1：采购经理 2：业务员", required = true)
    private String customMyRole;

    /**
     * 自定义额度
     * 0：30万以内
     * 1：30到100万
     * 2：大于100万
     */
    @NotBlank(message = "自定义额度")
    //@ApiModelProperty(value = "自定义额度", required = true)
    private String customQuota;

    /**
     * 自定义还款周期
     * 0：15天
     * 1：30天
     * 2：60天
     */
    @NotBlank(message = "自定义还款周期")
    //@ApiModelProperty(value = "自定义还款周期", required = true)
    private String customRepaymentPeriod;
}
