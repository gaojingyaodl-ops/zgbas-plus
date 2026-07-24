package com.spt.bas.purchase.wx.server.payload;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 *  入金测试提交
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-23 17:03
 */
@Builder
@Data
public class DepositRequest {
    private Long userId;
    private BigDecimal price;
}
