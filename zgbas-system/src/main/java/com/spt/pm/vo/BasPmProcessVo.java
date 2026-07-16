package com.spt.pm.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * zgBas PmProcess 实体
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/9/13 10:20
 */

public class BasPmProcessVo {
    private static final long serialVersionUID = -985262208661968792L;
    private String processCode;// 流程代码
    private String processName;// 流程名称
    private String remark;// 备注
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg;// 是否有效

    private String entityName;// 实体名称，完整路径
    private String entityService;// 服务名称
    private String listenerService;
    private String contentUrl;// 审批内容地址:/bas/contract/content/
    private Long dispOrderNo;// 序号

    private String applyType;

    private Long enterpriseId;//企业帐套ID

    private String processGroup;//流程分组

    /**
     * 发起人提示
     */
    private String sponsorTips;
}
