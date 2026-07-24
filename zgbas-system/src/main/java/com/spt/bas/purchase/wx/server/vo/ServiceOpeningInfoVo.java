package com.spt.bas.purchase.wx.server.vo;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * <p>
 *  用户服务开通信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-27 16:09
 */
@Data
@Builder
//@ApiModel(value = "用户服务开通信息")
public class ServiceOpeningInfoVo {
    /**
     * 企业信息提交状态
     */
    //@ApiModelProperty(value = "企业信息提交状态", required = true)
    private String companyApplyStatus;

    /**
     * 委托授权状态
     */
    //@ApiModelProperty(value = "委托授权状态", required = true)
    private String entrustApplyStatus;

    /**
     * 入金状态
     */
    //@ApiModelProperty(value = "入金状态", required = true)
    private String depositStatus;

    /**
     * 申请白条
     */
    //@ApiModelProperty(value = "申请白条", required = true)
    private String applyIouStatus;

    /**
     * 白条服务费
     */
    //@ApiModelProperty(value = "白条服务费", required = true)
    private String serviceFeeForIouStatus;

    /**
     * cfca平台审核状态
     */
    //@ApiModelProperty(value = "cfca平台审核状态", required = true)
    private String cfcaApprovedStatus;

    /**
     * cfca费用支付状态
     */
    //@ApiModelProperty(value = "cfca费用支付状态", required = true)
    private String cfcaPayFeeStatus;

    /**
     * 合伙人的申请状态
     */
    //@ApiModelProperty(value = "合伙人的申请状态", required = true)
    private String partnerApplyStatus;

    /**
     * 额度测试状态
     */
    //@ApiModelProperty(value = "额度测试状态", required = true)
    private String quotaTestStatus;

}
