package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface BsCompanyCreditDao extends BaseDao<BsCompanyCredit> {
    @Query("from BsCompanyCredit bc where bc.companyId=?1 and bc.creditType=?2")
    BsCompanyCredit findByCompanyIdAndType(Long companyId, String creditType);

    @Query("from BsCompanyCredit bc where bc.companyId=?1")
    List<BsCompanyCredit> findByCompanyId(Long companyId);

    BsCompanyCredit findByCompanyIdAndCreditTypeAndEnableFlg(Long companyId, String creditType, Boolean enableFlg);

    List<BsCompanyCredit> findByTemporaryExpiryDateBefore(Date temporaryExpiryDate);

    @Modifying
    @Transactional
    @Query("update BsCompanyCredit c set c.temporaryAmount =?2,c.temporaryExpiryDate=?3 where c.id=?1 ")
    void recoverTemporaryAmount(Long id, BigDecimal temporaryAmount, Date temporaryExpiryDate);

    @Query(nativeQuery = true, value = "SELECT c.* FROM t_bs_company_credit c WHERE c.id =?1 FOR UPDATE")
    BsCompanyCredit findEntityForUpdate(Long id);

    List<BsCompanyCredit> findByIdIn(List<Long> ids);
}
