package com.spt.bas.server.dao;

import com.spt.bas.client.entity.DataScore;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DataScoreDao extends BaseDao<DataScore> {
    List<DataScore> findByCompanyIdOrderByIdDesc(Long companyId);
}
