package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BasContract;
import com.spt.tools.jpa.dao.BaseDao;

public interface BasContractDao extends BaseDao<BasContract> {

	@Query(value = "select count(*) from BasContract g where g.contractNo=?1 and g.contractNo <> ?2")
	public long countByContractNo(String contractNo, String contractNoOld);

	@Transactional
	@Modifying
	@Query("update BasContract c set c.fileId =?2 where c.id=?1 ")
	public void updateFileId(Long id, String fileId);
	
	@Transactional
	@Modifying
	@Query("update BasContract c set c.contractStatus =?2 where c.id=?1 ")
	public void updateContractStatus(Long id, String contractStatus);

	public List<BasContract> findByContractRelaId(Long id);
	
	@Transactional
	@Modifying
	@Query("update BasContract c set c.fondFlg =?2 where c.id=?1 ")
	public void updateFondFlg(Long id, Boolean fondFlg);
	
	@Transactional
	@Modifying
	@Query("update BasContract c set c.billFlg =?2 where c.id=?1 ")
	public void updateBillFlg(Long id, Boolean billFlg);
	@Transactional
	@Modifying
	@Query("update BasContract c set c.payFlg =?2 where c.id=?1 ")
	public void updatePayFlg(Long id, Boolean payFlg);
	
//	@Query("from BasContract c where c.id in (select r.buyContractId from BasContractRela r where r.sellContractId IS NULL)")
//	public List<BasContract> findInventoryN();
	
}
