package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsNotice;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BsNoticeDao extends BaseDao<BsNotice> {

    @Query(nativeQuery = true,value="select * from t_bs_notice m order by m.id desc LIMIT 1 ")
    BsNotice findLast();

    @Query(nativeQuery = true,value="select * from t_bs_notice m  where  receive_dept_id like CONCAT('%',?1,'%') or receive_dept_id is null order by m.id desc LIMIT 5  ")
    List<BsNotice> findLimit5(String deptId);

    @Query(nativeQuery = true,value="select * from t_bs_notice m order by m.id desc LIMIT 5 ")
    List<BsNotice> findLimit();
}
