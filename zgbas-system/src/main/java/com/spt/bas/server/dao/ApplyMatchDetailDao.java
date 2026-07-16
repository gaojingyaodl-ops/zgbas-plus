package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ApplyMatchDetailDao extends BaseDao<ApplyMatchDetail> {
	@Query("from ApplyMatchDetail d where d.applyMatchId = ?1 order by d.contractType")
	public List<ApplyMatchDetail>findByApplyMatchId(Long applyMatchId);
	@Query("from ApplyMatchDetail d where d.applyMatchId = ?1 and d.contractType = ?2")
	public List<ApplyMatchDetail>findByQueryVo(Long applyMatchId, String contractType);
	@Transactional
	@Modifying
	public void deleteByApplyMatchId(Long id);

	public ApplyMatchDetail findByContractId(Long contractId);

	@Modifying
	@Query("update ApplyMatchDetail c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);

	ApplyMatchDetail findByContractNo(String contractNo);

	@Query("from ApplyMatchDetail c where c.applyMatchId =?1 and c.id <>?2")
	ApplyMatchDetail findOtherMatchDetail(Long applyMatchId, Long applyMatchDetailId);

	@Query("select count(*) from ApplyMatchDetail")
    Integer selectAllCount();
}

