package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.vo.CtrLastDateVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ApplyDeliveryOutDao extends BaseDao<ApplyDeliveryOut> {

	@Transactional
	@Modifying
	@Query("update ApplyDeliveryOut c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	/**根据合同编号 找到该入库审批单**/
	List<ApplyDeliveryOut> findByContractId(Long contractId);

	@Modifying
	@Query("update ApplyDeliveryOut c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);

	@Query("from ApplyDeliveryOut o where o.status = 'D' and o.contractNo =?1")
	List<ApplyDeliveryOut> findByContractNo(String contractNo);

	@Query("from ApplyDeliveryOut c  where c.approveId=?1 ")
	ApplyDeliveryOut findEntity(Long approveId);

	@Query("select max(c.warehouseOutDate) from ApplyDeliveryOut c  where c.contractId = ?1 and c.status = 'D'")
	Date findLastDelivery(Long contractId);

	@Query("SELECT NEW com.spt.bas.client.vo.CtrLastDateVo(contractId,warehouseOutDate) FROM ApplyDeliveryOut WHERE contractId in ?1 AND status = 'D'")
	List<CtrLastDateVo> findLastDelivery(List<Long> contractIds);

	@Query("select max(c.createdDate) from ApplyDeliveryOut c  where c.contractId in ?1 and c.status = 'D'")
	Date findLastDeliveryList(List<Long> sellIdList);

	/**
	 * 查询已出库未确认批次信息
	 * @param contractId
	 * @param confrimFlg
	 * @param status
	 * @return
	 */
	List<ApplyDeliveryOut> findByContractIdAndConfirmFlgAndStatus(Long contractId, String confrimFlg, String status);

	List<ApplyDeliveryOut> findByContractIdAndStatus(Long contractId, String status);

	ApplyDeliveryOut findByApplyNo(String applyNo);

	@Query("from ApplyDeliveryOut where contractId = ?1 and status not in ('B','N','C')")
	List<ApplyDeliveryOut> findByContractIdNoStatusB(Long contractId);

	@Query("from ApplyDeliveryOut where contractNo = ?1 ")
	List<ApplyDeliveryOut> findByContractNo2(String contractNo);

	@Modifying
	@Transactional
	@Query("update ApplyDeliveryOut c set c.confirmFlg = '0', c.confirmReceiptApplyId = NULL, c.confirmDcsxFlg = '0', c.confirmReceiptDcsxApplyId = NULL where c.id =?1")
	void invalidConfirmReceive(Long deliveryOutId);

	@Modifying
	@Transactional
	@Query("update ApplyDeliveryOut c set c.confirmDcsxFlg='0', c.confirmReceiptDcsxApplyId=NULL where c.id=?1")
	void invalidConfirmReceiveDcsx(Long deliveryOutId);

	@Query("from ApplyDeliveryOut o where o.confirmReceiptApplyId =?1")
	ApplyDeliveryOut findByConfirmReceiptApplyId(Long confirmReceiptApplyId);

	@Query("from ApplyDeliveryOut o where o.status = 'D' and o.contractId in ?1")
	List<ApplyDeliveryOut> findDeliveryInByContractIds(List<Long> contractIds);
}

