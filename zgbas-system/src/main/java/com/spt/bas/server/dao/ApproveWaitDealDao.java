package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApproveWaitDeal;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApproveWaitDealDao extends BaseDao<ApproveWaitDeal>{

    @Query("from ApproveWaitDeal a where a.relaUserId =?1")
    List<ApproveWaitDeal> findPageWaitDealById(Long id);

    @Query("update ApproveWaitDeal set readFlg = 1 where id =?1")
    @Modifying
    void updateStatus(Long id);

    @Query("update ApproveWaitDeal set completeFlg = 1 where id =?1")
    @Modifying
    void updateFlg(Long id);

    @Query("from ApproveWaitDeal where relaDeptId =?1 or relaUserId=?2")
    List<ApproveWaitDeal> findPageWaitDealCount(Long relaDeptId, String relaUserId);

    @Query("select subject from  ApproveWaitDeal  where id =?1")
    String findSubject(Long id);

    @Query("select count(1) from ApproveWaitDeal where relaUserId =?1 and readFlg = '0'")
    Long getUserWaitDealNum(String userId);
}
