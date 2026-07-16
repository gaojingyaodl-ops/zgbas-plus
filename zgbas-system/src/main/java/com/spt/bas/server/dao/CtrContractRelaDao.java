package com.spt.bas.server.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.CtrContractRela;
import com.spt.tools.jpa.dao.BaseDao;

public interface CtrContractRelaDao extends BaseDao<CtrContractRela> {

	@Query("select count(*) from CtrContractRela c where c.buyContractId =?1 and c.buyDate<=c.sellDate ")
	Long countByBuyContractId(Long buyContractId);

	@Query("select count(*) from CtrContractRela c where c.sellContractId =?1 and c.buyDate>=c.sellDate ")
	Long countBySellContractId(Long sellContractId);

	@Query("select count(*) from CtrContractRela c where (c.sellProductId =?1 and c.buyProductId=?2) or (c.sellProductId =?2 and c.buyProductId=?1) ")
	Long countByUnion(Long productId1, Long productId2);

	@Transactional
	@Modifying
	@Query("delete from CtrContractRela c where c.sellContractId =?1")
	void deleteBySellContractId(Long sellContractId);

	@Transactional
	@Modifying
	@Query("delete from CtrContractRela c where c.buyContractId =?1")
	void deleteByBuyContractId(Long buyContractId);

	CtrContractRela findBySellProductIdAndStockContractId(Long sellProductId, Long stockContractId);

	//CtrContractRela findByBuyProductIdAndStockContractId(Long buyProductId,Long stockContractId);

	List<CtrContractRela> findBySellProductId(Long sellProductId);

	List<CtrContractRela> findByBuyProductId(Long buyProductId);

	List<CtrContractRela> findBySellContractId(Long string);

	List<CtrContractRela> findByBuyContractId(Long buyContractId);

	List<CtrContractRela> findBySellProductIdAndSellContractId(Long sellProductId, Long sellContractId);

	@Transactional
	@Modifying
	@Query("update CtrContractRela r set r.buyCompanyId = ?1, r.buyCompanyName = ?2, r.buyPrice = ?3 where r.buyContractId = ?4 and r.buyProductId = ?5")
	void updateRelaByBuyContract(Long companyId, String companyName, BigDecimal dealPrice, Long contractId, Long ctrProductId);

	@Transactional
	@Modifying
	@Query("update CtrContractRela r set r.sellCompanyId = ?1, r.sellCompanyName = ?2, r.sellPrice = ?3, r.dealNumber = ?4 where r.sellContractId = ?5 and r.sellProductId = ?6")
	void updateRelaBySellContract(Long companyId, String companyName, BigDecimal dealPrice, BigDecimal dealNumber, Long contractId, Long ctrProductId);
}
