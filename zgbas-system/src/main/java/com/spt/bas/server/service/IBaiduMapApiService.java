package com.spt.bas.server.service;

import com.spt.bas.client.vo.DistanceResultVo;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/7/6 15:18
 */

public interface IBaiduMapApiService {

    /**
     * 获取两个地点之间的距离
     *
     * @param start 开始地址
     * @param end 结束地址
     * @return 结果
     */
    DistanceResultVo getTwoDistance(String start, String end);
}
