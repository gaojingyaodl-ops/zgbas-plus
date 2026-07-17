package com.spt.pm.dao;

import com.spt.pm.entity.PmProcessAutoStep;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 额外条件节点配置
 * @Author: gaojy
 * @create 2022/2/8 15:12
 * @version: 1.0
 * @description:
 */
public interface PmProcessAutoStepDao extends BaseDao<PmProcessAutoStep> {
    @Transactional
    @Modifying
    @Query("delete from PmProcessAutoStep where id in ?1")
    void deleteByIds(List<Long> autoStepIds);

    @Transactional
    @Modifying
    @Query("delete from PmProcessAutoStep where processId = ?1")
    void deleteByProcessId(Long processId);

    @Query("from PmProcessAutoStep where processId = ?1 and (referConditionId = ?2 OR referConditionId is null) and enableFlg = true order by conditionType,dispOrderNo")
    List<PmProcessAutoStep> findProcessAutoStepList(Long processId, Long processConditionId);
}
