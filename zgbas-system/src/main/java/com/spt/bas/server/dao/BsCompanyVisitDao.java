package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompanyVisit;
import com.spt.bas.client.vo.ApplyCompanyVisitVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * @Author 田起立
 * @Date 2024/6/3 9:55
 * @Description:
 */
public interface BsCompanyVisitDao extends BaseDao<BsCompanyVisit> {
    @Transactional
    @Modifying
    @Query("update BsCompanyVisit c set c.status =?2 where c.id = ?1 ")
    void updateStatus(Long id, String a);
}
