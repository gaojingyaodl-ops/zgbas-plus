package com.spt.bas.server.service.impl;


import com.spt.bas.client.entity.SysQuickSearch;
import com.spt.bas.client.vo.SysQuickSearchVo;
import com.spt.bas.server.dao.SysQuickSearchDao;
import com.spt.bas.server.service.ISysQuickSearchService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("sysQuickSearchService")
public class SysQuickSearchServiceImpl extends BaseService<SysQuickSearch> implements ISysQuickSearchService {

    @Autowired
    private SysQuickSearchDao sysQuickSearchDao;

    @Override
    public BaseDao<SysQuickSearch> getBaseDao() {
        return sysQuickSearchDao;
    }


    @Override
    public List<SysQuickSearch> findListByUserIdAndModuleUrl(SysQuickSearchVo searchVo) {
        return sysQuickSearchDao.findListByUserIdAndModuleUrl(searchVo.getModuleUrl(),searchVo.getUserId());
    }
}
