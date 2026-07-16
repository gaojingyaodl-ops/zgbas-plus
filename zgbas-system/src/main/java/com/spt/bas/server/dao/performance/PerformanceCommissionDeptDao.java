package com.spt.bas.server.dao.performance;

import com.spt.bas.client.entity.PerformanceCommissionDept;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface PerformanceCommissionDeptDao extends BaseDao<PerformanceCommissionDept> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM t_performance_commission_dept WHERE performance_date = ?1")
    void deleteAllByPerformanceDate(String yearMonth);

    @Query(nativeQuery = true, value = "SELECT * FROM t_performance_commission_dept WHERE performance_date = ?1 AND leader_user_id = ?2")
    PerformanceCommissionDept getPerformanceCommissionDept(String performanceDate, Long userId);
}
