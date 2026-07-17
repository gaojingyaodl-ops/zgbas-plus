package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.ApiExternalHis;
import com.spt.bas.server.dao.ApiExternalHisDao;
import com.spt.bas.server.service.IApiExternalHisService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: wm
 * @create 2022/3/25 17:39
 * @version: 1.0
 * @description:
 */

@Component
@Slf4j
public class ApiExternalHisServiceImpl extends BaseService<ApiExternalHis> implements IApiExternalHisService {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    @Autowired
    private ApiExternalHisDao apiExternalHisDao;

   @Override
    public BaseDao<ApiExternalHis> getBaseDao() {
        return apiExternalHisDao;
    }


    @Override
    public void addApiExternalHis(ApiExternalHis externalHis) {
        try {
            if (Objects.isNull(externalHis)) {
                return;
            }
            threadPool.execute(() -> apiExternalHisDao.save(externalHis));
        } catch (Exception e) {
            log.error("addApiRequestHis error, appCode:{},appName:{},apiUrl:{},apiParam:{}",
                    externalHis.getAppCode(), externalHis.getAppName(), externalHis.getApiUrl(), externalHis.getApiParam());
        }
    }
}
