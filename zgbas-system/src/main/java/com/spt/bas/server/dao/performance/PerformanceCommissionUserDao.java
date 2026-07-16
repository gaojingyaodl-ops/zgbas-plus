package com.spt.bas.server.dao.performance;

import com.spt.bas.client.entity.PerformanceCommissionUser;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface PerformanceCommissionUserDao extends BaseDao<PerformanceCommissionUser> {

    @Query(nativeQuery = true, value = "SELECT * FROM t_performance_commission_user WHERE performance_date = DATE_FORMAT(?1, '%Y-%m')")
    List<PerformanceCommissionUser> getPerformanceCommissionList(Date commissionDate);

    @Query(nativeQuery = true, value = "SELECT * FROM t_performance_commission_user WHERE performance_date = DATE_FORMAT(?1, '%Y-%m') AND user_id = ?2")
    List<PerformanceCommissionUser> getPerformanceCommissionList(Date commissionDate, Long userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM t_performance_commission_user WHERE performance_date = ?1")
    void deleteAllByPerformanceDate(String yearMonth);

    @Query(nativeQuery = true, value = "SELECT * FROM t_performance_commission_user WHERE performance_date = ?1 AND user_id = ?2")
    PerformanceCommissionUser getPerformanceCommissionUser(String performanceDate, Long userId);
}
