package com.spt.bas.server.dao.sign;

import com.spt.bas.client.entity.SignFileUser;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SignFileUserDao extends BaseDao<SignFileUser> {

    @Query("from SignFileUser s where s.signFileId=?1")
    List<SignFileUser> findBySignId(Long signId);

    @Transactional
    @Modifying
    @Query("update SignFileUser set status =?2 where signFileId =?1")
    void updateSignFileUserStatus(Long signId, String status);
}
