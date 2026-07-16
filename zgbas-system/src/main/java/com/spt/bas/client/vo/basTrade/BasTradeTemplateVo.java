package com.spt.bas.client.vo.basTrade;

import lombok.Data;

/**
 * 合同模板查询VO
 *
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2025/6/18 11:33
 */
@Data
public class BasTradeTemplateVo {

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 是否包含合同内容
     */
    private Boolean withContentFlag = false;
}
