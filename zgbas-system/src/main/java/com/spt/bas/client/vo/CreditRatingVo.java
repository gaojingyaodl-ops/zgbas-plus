package com.spt.bas.client.vo;

import lombok.Data;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/4 17:11
 */
@Data
public class CreditRatingVo {
    /**
     * 白名单、黑名单、灰名单
     */
    private String creditRating;

    /**
     * 企业性质
     */
    private String companyCategory;

    /**
     * 备注
     */
    private String remark;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * zgbase搬过来字段
     */
    private String warehouseFlag;

    /**
     * 企业类型
     */
    private String companyType;

    /**
     * 企业来源
     */
    private String companySource;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;
}
