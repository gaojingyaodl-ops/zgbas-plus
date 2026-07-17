package com.spt.bas.web.controller.trade.VO;

import lombok.Data;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2025/6/26 11:34
 */
@Data
public class TradeRespVO<T> {
    private String code;

    private T data;

    private String msg;
}
