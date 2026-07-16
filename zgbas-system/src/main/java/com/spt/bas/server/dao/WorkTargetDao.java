package com.spt.bas.server.dao;

import com.spt.bas.client.entity.WorkTarget;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkTargetDao extends BaseDao<WorkTarget> {

    @Query(value = "from WorkTarget w where w.branchCd=:#{#query.branchCd} and w.targetMonth=:#{#query.targetMonth} and w.targetType=:#{#query.targetType}")
    WorkTarget findByBranchCdAndTargetMonth(@Param("query") WorkTarget query);

    @Query("select count(*) from WorkTarget")
    Integer selectAllCount();

}
