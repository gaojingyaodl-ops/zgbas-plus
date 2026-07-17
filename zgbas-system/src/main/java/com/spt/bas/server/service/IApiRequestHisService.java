package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApiRequestHis;
import com.spt.tools.jpa.service.IBaseService;

/**
 * @Author: gaojy
 * @create 2022/2/18 14:53
 * @version: 1.0
 * @description:
 */
public interface IApiRequestHisService extends IBaseService<ApiRequestHis> {

    void addApiRequestHis(ApiRequestHis requestHis);
}
