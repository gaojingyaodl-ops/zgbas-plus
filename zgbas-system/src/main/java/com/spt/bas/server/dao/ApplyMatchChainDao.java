package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyMatchChain;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 代采赊销中间链条表
 * @Author: gaojy
 * @create 2022/9/14 10:40
 * @version: 1.0
 * @description:
 */
public interface ApplyMatchChainDao extends BaseDao<ApplyMatchChain> {

    List<ApplyMatchChain> findByApplyMatchIdOrderBySerialNumberAsc(Long applyMatchId);

    @Transactional
    @Modifying
    @Query("update ApplyMatchChain a set a.approveId=?2 where a.applyMatchId=?1")
    void updateChainApproveId(Long applyMatchId, Long approveId);
}
