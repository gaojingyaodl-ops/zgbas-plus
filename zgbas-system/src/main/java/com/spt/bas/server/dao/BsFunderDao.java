package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsFunder;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BsFunderDao extends BaseDao<BsFunder> {
    List<BsFunder> findAllByUserId(Long userId);
}

