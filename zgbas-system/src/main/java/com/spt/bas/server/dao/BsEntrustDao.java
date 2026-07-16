package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsEntrust;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BsEntrustDao extends BaseDao<BsEntrust> {

    BsEntrust findByWxUserIdAndEnableFlgTrue(Long wxUserId);

    @Query("from BsEntrust where wxUserId = ?1 and enableFlg = true order by updatedDate desc")
    List<BsEntrust> findByWxUserId(Long wxUserId);


    @Query("from BsEntrust c where c.companyId = ?1 and c.enableFlg = true order by c.updatedDate desc")
    List<BsEntrust> findByCompanyId(Long companyId);




}
