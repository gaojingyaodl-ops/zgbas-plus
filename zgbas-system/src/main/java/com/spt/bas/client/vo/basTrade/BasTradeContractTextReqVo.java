package com.spt.bas.client.vo.basTrade;

import lombok.Data;

/**
 * 获取合同VO
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2025/6/27 15:47
 */
@Data
public class BasTradeContractTextReqVo {
    private Long templateId;
    private Long approveId;
    private Long enterpriseId;
    private String contractType;
}
