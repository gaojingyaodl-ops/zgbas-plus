package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ApplyProductDetailDao extends BaseDao<ApplyProductDetail> {

	@Query("from ApplyProductDetail p where p.applyId = ?1 and p.applyType =?2")
	List<ApplyProductDetail> findApplyDetail(Long applyId, String applyType);

	@Transactional
	@Modifying
	@Query("delete from ApplyProductDetail p where p.applyId=?1 and p.applyType =?2")
	void deleteDetail(Long applyId, String applyType);

	@Query("from ApplyProductDetail p where p.applyId = ?1  and p.applyType=?2 ")
	List<ApplyProductDetail> findApplyId(Long applyId, String applyType);

	@Query("select sum(dealNumber),sum(totalPrice),sum(curNumber) from ApplyProductDetail p where p.applyId = ?1 and p.applyType =?2")
	List<Object[]> sumApplyDetail(Long id, String applyTypeI);

	/**
	 * 查询商品详细
	 * @param applyId 出库申请id
	 * @param applyType 类型
	 * @return
	 */
	List<ApplyProductDetail> findByApplyIdInAndApplyType(List<Long> applyId, String applyType);

	/**
	 * 查询商品详细
	 * @param applyId 出库申请id
	 * @param applyType 类型
	 * @return
	 */
	ApplyProductDetail findByApplyIdAndApplyType(Long applyId, String applyType);

	@Query("from ApplyProductDetail p where p.productName = ?1 ")
	List<ApplyProductDetail> findByProductName(String  productName);


}

