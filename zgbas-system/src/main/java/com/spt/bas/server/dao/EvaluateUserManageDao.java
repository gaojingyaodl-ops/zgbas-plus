package com.spt.bas.server.dao;

import com.spt.bas.client.entity.EvaluateUserManage;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

public interface EvaluateUserManageDao extends BaseDao<EvaluateUserManage> {
    
    List<EvaluateUserManage> findAllByUserId(Long userId);
}
