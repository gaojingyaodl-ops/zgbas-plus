package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyMatch;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ApplyMatchDao extends BaseDao<ApplyMatch> {

	@Modifying
	@Query("update ApplyMatch c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	@Modifying
	@Query("update ApplyMatch c set c.liabilityFileId =?2 where c.id=?1 ")
	void updateLiabilityFileId(Long id, String fileId);

	ApplyMatch findByApproveId(Long approveId);

	@Query("select m from ApplyMatch m left join ApplyMatchDetail d on m.id = d.applyMatchId where d.contractNo =?1 and m.stockVirtualId > 0")
	ApplyMatch findVirtualMatch(String contractNo);

	@Query("from ApplyMatch m where m.approveId in ?1")
	List<ApplyMatch> findByApproveIdList(List<Long> approveIds);

	@Query("select count(*) from ApplyMatch")
    Integer selectAllCount();

	@Modifying
	@Query("update ApplyMatch c set c.businessRestrictRelieveFlg =?2 where c.id=?1 ")
	void updateBusinessRestrictRelieveFlg(Long id, Boolean businessRestrictRelieveFlg);

	@Query("select sum(m.sellAmount) from ApplyMatch m left join PmApprove p on m.approveId = p.id where p.status = 'A' and m.companyCreditId =?1")
	BigDecimal getApproveCreditAmount(Long companyCreditId);
}

