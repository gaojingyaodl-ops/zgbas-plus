package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.server.entity.WxUserTextRead;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

public interface WxUserTextReadDao extends BaseDao<WxUserTextRead> {

    @Query(nativeQuery = true, value = "SELECT * FROM t_wx_user_text_read WHERE user_id = ?1 AND text_type = ?2")
    WxUserTextRead findByUserIdAndTextType(Long userId, String textType);
}
