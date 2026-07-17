package com.spt.bas.report.client.vo;

import lombok.Data;

/**
 * 资金方管理查询结果VO
 */
@Data
public class RptFunderVo {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 企业套账ID
     */
    private Long enterpriseId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 企业名称
     */
    private String companyNames;

    /**
     * 备注
     */
    private String remark;
    
    
    
}
