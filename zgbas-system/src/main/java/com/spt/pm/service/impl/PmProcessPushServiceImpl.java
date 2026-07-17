package com.spt.pm.service.impl;

import com.spt.pm.dao.PmProcessPushDao;
import com.spt.pm.entity.PmProcessPush;
import com.spt.pm.service.IPmProcessPushService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 流程推送配置表
 * @Author: gaojy
 * @create 2022/4/26 11:16
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class PmProcessPushServiceImpl extends BaseService<PmProcessPush> implements IPmProcessPushService {
    @Autowired
    private PmProcessPushDao processPushDao;

    @Override
    public BaseDao<PmProcessPush> getBaseDao() {
        return processPushDao;
    }
}
