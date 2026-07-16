package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyWxCfca;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.print.DocFlavor;
import javax.transaction.Transactional;
import java.util.List;

public interface ApplyWxCfcaDao extends BaseDao<ApplyWxCfca> {


    @Transactional
    @Modifying
    @Query("update ApplyWxCfca c set c.status =?2 , c.approveId=?3 , c.applyUserName=?4 where c.id=?1 ")
    void updateStatus(Long id, String status ,Long approveId, String applyUserName);


    @Query(value = "SELECT * from t_wx_apply_cfca  where company_id=?1 order by  created_date desc ",nativeQuery = true)
    List<ApplyWxCfca> findByCompanyId(Long  companyId);
}
