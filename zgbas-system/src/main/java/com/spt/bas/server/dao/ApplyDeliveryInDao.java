package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyDeliveryIn;
import com.spt.bas.client.vo.CtrLastDateVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ApplyDeliveryInDao extends BaseDao<ApplyDeliveryIn> {
	
	@Transactional
	@Modifying
	@Query("update ApplyDeliveryIn c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	@Query("from ApplyDeliveryIn c where c.contractId=?1 and c.status !='C' ")
	List<ApplyDeliveryIn> findByContractId(Long contractId);
	
	@Query("update ApplyDeliveryIn c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);
	
	List<ApplyDeliveryIn> findByContractNo(String contractNo);

	@Query("from ApplyDeliveryIn c where c.contractId=?1 ")
	List<ApplyDeliveryIn> findDeliveryInContractId(Long contractId);
	
	@Query("from ApplyDeliveryIn c where c.approveId=?1 ")
	ApplyDeliveryIn findByApplyId(Long approveId);
	
	@Query("select max(warehouseInDate) from ApplyDeliveryIn c where c.contractId = ?1 and c.status = 'D'")
	Date findLastDelivery(Long contractId);

	@Query("SELECT NEW com.spt.bas.client.vo.CtrLastDateVo(contractId,warehouseInDate) FROM ApplyDeliveryIn WHERE contractId in ?1 AND status = 'D'")
	List<CtrLastDateVo> findLastDelivery(List<Long> contractIds);
	
	@Query("from ApplyDeliveryIn i where i.status = 'D' and i.contractId in ?1")
	List<ApplyDeliveryIn> findDeliveryInByContractIds(List<Long> contractIds);

	@Query(nativeQuery = true, value = "SELECT i.* FROM t_apply_delivery_in i LEFT JOIN t_ctr_logistics_file f ON i.logistics_file_id = f.id WHERE i.`status` = 'D' AND f.sign_flg = FALSE AND i.created_date >= '2024-04-28'")
	List<ApplyDeliveryIn> findUnSignLogistics();
}

