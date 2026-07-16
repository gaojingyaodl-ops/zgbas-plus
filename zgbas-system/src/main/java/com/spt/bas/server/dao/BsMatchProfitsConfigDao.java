package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsMatchProfitsConfig;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/2/8 14:08
 * @version: 1.0
 * @description:
 */
public interface BsMatchProfitsConfigDao extends BaseDao<BsMatchProfitsConfig> {
    @Query("from BsMatchProfitsConfig where matchUserId =?1 ")
    List<BsMatchProfitsConfig> findByMathUserId(Long mathUserId);
}
