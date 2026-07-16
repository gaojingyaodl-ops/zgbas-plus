package com.spt.bas.client.vo;

import lombok.Data;

/**
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/3/3 17:05
 */
@Data
public class ApplyPlasticTypeVo {

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 塑料分类
     */
    private String plasticType;

    /**
     * 塑料分类备注
     */
    private String plasticTypeRemark;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;
}
