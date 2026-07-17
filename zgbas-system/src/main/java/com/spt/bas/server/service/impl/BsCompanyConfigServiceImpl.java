package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsCompanyConfig;
import com.spt.bas.server.dao.BsCompanyConfigDao;
import com.spt.bas.server.service.IBsCompanyConfigService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 客户计算配置表
 * @Author: gaojy
 * @create 2022/4/2 11:03
 * @version: 1.0
 * @description:
 */
@Slf4j
@Component
public class BsCompanyConfigServiceImpl extends BaseService<BsCompanyConfig> implements IBsCompanyConfigService {
    @Autowired
    private BsCompanyConfigDao bsCompanyConfigDao;

    @Override
    public BaseDao<BsCompanyConfig> getBaseDao() {
        return bsCompanyConfigDao;
    }

    @Override
    public BsCompanyConfig findByBsCompanyIdAndMatchUserId(Long bsCompanyId, Long matchUserId) {
        return bsCompanyConfigDao.findByBsCompanyIdAndMatchUserId(bsCompanyId, matchUserId);
    }

    @Override
    public BsCompanyConfig findConfigByCompanyId(Long bsCompanyId) {
        return bsCompanyConfigDao.findByBsCompanyId(bsCompanyId);
    }
}
