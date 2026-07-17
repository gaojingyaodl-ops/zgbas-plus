package com.spt.pm.dao;

import com.spt.pm.entity.PmProcessPush;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/4/26 11:13
 * @version: 1.0
 * @description:
 */
public interface PmProcessPushDao extends BaseDao<PmProcessPush> {

    @Query("from PmProcessPush p where p.processId =?1 and p.enterpriseId = ?2 and p.enableFlg = true")
    List<PmProcessPush> findPushCondition(Long processId, Long enterpriseId);
}
