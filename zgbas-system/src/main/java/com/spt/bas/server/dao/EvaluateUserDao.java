package com.spt.bas.server.dao;

import com.spt.bas.client.entity.EvaluateUser;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

public interface EvaluateUserDao extends BaseDao<EvaluateUser> {
    List<EvaluateUser> findAllByEvaluateMonthAndDeptId(String evaluateMonh,Long deptId);
    List<EvaluateUser> findAllByEvaluateMonthAndUserId(String evaluateMonh,Long userId);
}
