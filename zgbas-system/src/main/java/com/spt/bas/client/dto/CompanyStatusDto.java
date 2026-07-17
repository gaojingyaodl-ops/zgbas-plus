package com.spt.bas.client.dto;

import lombok.Data;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/12 17:02
 */
@Data
public class CompanyStatusDto {

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 当前登录人id
     */
    private Long userId;

    /**
     * 当前登录人名称
     */
    private String userName;

    /**
     * 状态
     */
    private String status;
}
