package com.spt.bas.web.controller.trade.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2025/6/24 15:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeMenuVO {
    /**
     * 菜单名字
     */
    private String name;

    /**
     * icon
     */
    private String icon;

    /**
     * 路由地址
     */
    private String path;

    private String url;
}
