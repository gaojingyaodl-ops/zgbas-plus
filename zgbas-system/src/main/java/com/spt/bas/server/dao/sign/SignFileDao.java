package com.spt.bas.server.dao.sign;

import com.spt.bas.client.entity.SignFile;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SignFileDao extends BaseDao<SignFile> {

    @Query("from SignFile s where s.cfcaContractNo=?1")
    SignFile findByCfcaContractNo(String cfcaContractNo);

    @Modifying
    @ServerTransactional
    @Query("update SignFile c set c.fileId =?2 where c.id=?1 ")
    void updateFileId(Long id, String fileId);

    @Query(nativeQuery=true, value = "SELECT *  FROM t_sign_file ORDER BY created_date DESC LIMIT 1")
    SignFile findByAllLimit();
}
