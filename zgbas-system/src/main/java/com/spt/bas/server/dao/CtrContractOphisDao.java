package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractOphis;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface CtrContractOphisDao extends BaseDao<CtrContractOphis> {

	CtrContractOphis findByCtrContractId(Long contractId);
	CtrContractOphis findFirstByApproveIdAndCtrContractIdOrderByCreatedDateDesc(Long approveId, Long contractId);
	@Query("from CtrContractOphis where ctrContractId = ?1")
	List<CtrContractOphis> findByContractId(Long contractId);

	@Transactional
	@Modifying
	@Query("delete from CtrContractOphis where approveId =?1 and ctrContractId =?2")
	void deleteContractOphis(Long approveId, Long ctrContractId);

	@Query("select count(*) from CtrContractOphis")
    Integer selectAllCount();
}
