package com.spt.bas.purchase.wx.server.payload;

import com.spt.bas.purchase.wx.server.vo.ServiceOpeningInfoVo;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-27 16:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//@ApiModel(value = "更新用户服务开通信息请求参数")
public class ServiceOpeningInfoRequest {

    /**
     * 信息类型：<br />
     * 0：企业信息 <br />
     * 1：委托授权<br />
     * 2：入金<br />
     * 3：申请白条<br />
     * 4：白条服务费<br />
     * 5：cfca平台审核<br />
     * 6：cfca费用支付
     * 7:合伙人的申请状态
     * 8:额度测试申请状态
     */
    //@ApiModelProperty(value = "信息类型", required = true)
    @NotBlank(message = "信息类型不能为空")
    private String type;


    /**
     * 更新状态：<br />
     * 0:未开始<br />
     * 1:审批中<br />
     * 2:未确认<br />
     * 3:审批驳回<br />
     * 4:完成
     */
    //@ApiModelProperty(value = "更新状态", required = true)
    @NotBlank(message = "更新状态不能为空")
    private String status;

}
