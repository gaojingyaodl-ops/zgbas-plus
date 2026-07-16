package com.spt.bas.client.vo.basTrade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取合同VO
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2025/6/27 15:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasTradeContractTextRespVo {
    private Long fileId;
    private String content;

    public BasTradeContractTextRespVo(Long fileId) {
        this.fileId = fileId;
    }

    public BasTradeContractTextRespVo(String content) {
        this.content = content;
    }
}
