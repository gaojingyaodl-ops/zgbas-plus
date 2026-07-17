package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsMatchProfitsConfig;
import com.spt.bas.server.dao.BsMatchProfitsConfigDao;
import com.spt.bas.server.service.IBsMatchProfitsConfigService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 业务员利润计算配置表
 * @Author: gaojy
 * @create 2022/2/8 14:10
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class BsMatchProfitsConfigImpl extends BaseService<BsMatchProfitsConfig> implements IBsMatchProfitsConfigService {
    @Autowired
    private BsMatchProfitsConfigDao matchProfitsConfigDao;

    @Override
    public BaseDao<BsMatchProfitsConfig> getBaseDao() {
        return matchProfitsConfigDao;
    }

    @Override
    public Class<BsMatchProfitsConfig> getEntityClazz() {
        return BsMatchProfitsConfig.class;
    }
    /* 根据业务员查询 */
    @Override
    public List<BsMatchProfitsConfig> findByMathUserId(@RequestBody Long mathUserId) {
        System.out.println("mathcUserId:"+mathUserId);
        return matchProfitsConfigDao.findByMathUserId(mathUserId);
    }
}
