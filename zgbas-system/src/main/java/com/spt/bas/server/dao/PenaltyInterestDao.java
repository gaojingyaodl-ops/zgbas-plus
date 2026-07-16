package com.spt.bas.server.dao;

import com.spt.bas.client.entity.PenaltyInterest;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: wm
 * @Date: Created in 2022-06-08 10:22
 */

public interface PenaltyInterestDao extends BaseDao<PenaltyInterest> {
    @Transactional
    @Modifying
    @Query("update PenaltyInterest c set c.interestStatus =?1 where c.bizId = ?2 ")
    void updateInterStatus(String interestStatus, Long id);


    @Query("SELECT c.interestContractNo FROM PenaltyInterest c where c.interestCompanyId =?1 and c.interestStatus not in('B')")
     List<String> findContractNoByCompanyId(String companyId);

}
