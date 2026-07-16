package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyDelivery;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyDeliveryDao extends BaseDao<ApplyDelivery> {


	@Transactional
	@Modifying
	@Query("update ApplyDelivery c set c.printCount =?2,c.operation=?3 where c.id=?1 ")
	void startPrint(Long id, Integer printCount, String operation);

	@Query("from ApplyDelivery c where c.id=?1 ")
	ApplyDelivery getApplyDeliveryEntity(Long id);

	@Query("from ApplyDelivery c where c.id=?1 ")
	ApplyDelivery getApplyDeliveryInvoiceEntity(Long id);

	List<ApplyDelivery> findByDeliveryOutApplyId(Long deliveryOutApplyId);

	@Query("from ApplyDelivery c where c.contractId=?1 ")
	List<ApplyDelivery> findByContractId(Long id);

	@Transactional
	@Modifying
	@Query("update ApplyDelivery c set c.operation=?1 where c.deliveryOutApplyId=?2 ")
	void updateDelivery(String operation, Long deliveryOutId);
}

