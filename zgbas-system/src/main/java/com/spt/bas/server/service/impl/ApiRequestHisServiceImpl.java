package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.ApiRequestHis;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApiRequestHisDao;
import com.spt.bas.server.service.IApiRequestHisService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: gaojy
 * @create 2022/2/18 14:54
 * @version: 1.0
 * @description:
 */
@Component
@Slf4j
public class ApiRequestHisServiceImpl extends BaseService<ApiRequestHis> implements IApiRequestHisService {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    @Autowired
    private ApiRequestHisDao apiRequestHisDao;

    @Override
    public BaseDao<ApiRequestHis> getBaseDao() {
        return apiRequestHisDao;
    }

    @Override
    @ServerTransactional
    public void addApiRequestHis(ApiRequestHis requestHis) {
        try {
            if (Objects.isNull(requestHis)) {
                return;
            }
            threadPool.execute(() -> apiRequestHisDao.save(requestHis));
        } catch (Exception e) {
            log.error("addApiRequestHis error, appCode:{},appName:{},apiCode:{},apiName:{}",
                    requestHis.getAppCode(), requestHis.getAppName(), requestHis.getApiCode(), requestHis.getApiName());
        }
    }
}
