package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.WorkTarget;
import com.spt.bas.server.dao.WorkTargetDao;
import com.spt.bas.server.service.IWorkTargetService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WorkTargetServiceImpl extends BaseService<WorkTarget> implements IWorkTargetService {

    @Autowired
    private WorkTargetDao workTargetDao;

    @Override
    public BaseDao<WorkTarget> getBaseDao() {
        return workTargetDao;
    }

    @Override
    public String findByBranchCdAndTargetMonth(WorkTarget query) {
        WorkTarget workTarget = workTargetDao.findByBranchCdAndTargetMonth(query);
        if(Objects.isNull(workTarget) || Objects.isNull(workTarget.getId())){
            return null;
        }
        return "该地区在" + query.getTargetMonth() + "月份已存在相同类型的总价！";
    }
}
