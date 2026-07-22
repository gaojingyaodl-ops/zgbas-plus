package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.client.entity.BuyMessage;
import com.spt.bas.purchase.wx.client.vo.MessageSearchVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface BuyMessageDao extends BaseDao<BuyMessage> {

    @Query(value = "select * FROM t_buy_message WHERE if(?1 !='',message_type =?1 , 1=1) and open_id = ?2 order by read_flag,id desc",
            countQuery = "SELECT count(*) FROM t_buy_message WHERE if(?1 !='',message_type =?1 , 1=1) and open_id = ?2",
            nativeQuery = true)
    Page<BuyMessage> getMessagePage(String messageType, String openId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update BuyMessage b set b.readFlag =1 where b.openId=?1 and b.messageType =?2")
    void allRead(String openId, String messageType);

    @Transactional
    @Modifying
    @Query("update BuyMessage b set b.readFlag =1 where b.openId=?1")
    void allReadByOpenId(String openId);

    @Transactional
    @Modifying
    @Query("update BuyMessage b set b.readFlag =1 where b.id=?1")
    void singleRead(Long id);
}
