package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsTextContent;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BsTextContentDao extends BaseDao<BsTextContent> {

    @Query(value = "SELECT * from t_bs_text_content where text_type = ?1 and enable_flg = true order by version desc" ,nativeQuery = true)
    List<BsTextContent> findNewTextContentByType(String textType);
}
