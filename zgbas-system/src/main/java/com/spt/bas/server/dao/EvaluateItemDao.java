package com.spt.bas.server.dao;

import com.spt.bas.client.entity.EvaluateItem;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EvaluateItemDao extends BaseDao<EvaluateItem> {
    @Query(" from EvaluateItem e ")
    List<EvaluateItem> findAllEvaluateItem();
}
