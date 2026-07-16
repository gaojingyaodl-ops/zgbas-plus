package com.spt.bas.client.vo;

import lombok.Data;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/6 09:40
 */
@Data
public class FloatingRateApproveVo {

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 申请上浮的档位
     */
    private String floatingGear;

    /**
     * 备注
     */
    private String remark;

    /**
     * 申请人id
     */
    private Long userId;

    /**
     * 申请人名称
     */
    private String nickName;

    /**
     * 是否拥有 zgbas:new:salesman 权限 业务员
     */
    private Boolean salesman;

}
