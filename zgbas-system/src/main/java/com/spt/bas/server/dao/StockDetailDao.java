package com.spt.bas.server.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.StockDetail;
import com.spt.tools.jpa.dao.BaseDao;

public interface StockDetailDao extends BaseDao<StockDetail> {

	@Query("from StockDetail d where d.id =?1 or d.linkStockDetailId =?1 ")
	List<StockDetail> findByIdOrLinkId(Long id);
	List<StockDetail> findByBuyContractId(String buyContractId);
	List<StockDetail> findByStockContractId(Long stockContractId);

	List<StockDetail> findByBuyContractIdAndProductAttr(String buyContractId, String productAttr);

//	@Query("from StockDetail d where d.sellContractId like %?1% ")
//	List<StockDetail> findSellContractId(String sellContractId);
//	@Query("from StockDetail d where d.stockId =?1 and d.sellContractId like %?2% ")
//	List<StockDetail> findStockIdAndSellContractId(Long stockId, String sellContractId);

	@Query("from StockDetail d where d.warehouseName=?1 ")
	StockDetail findWarehouseName(String warehouseName);

	List<StockDetail> findByStockId(Long stockId);

	@Query("from StockDetail d where d.stockId=?1 and d.presellNumber >0")
	StockDetail findPresellDetail(Long stockId);

	List<StockDetail> findByCtrProductId(Long productId);

	List<StockDetail> findByLinkStockDetailId(Long stockDetailId);

	List<StockDetail> findByBuyContractIdAndProductAttrAndWarehouseName(String buyContractId, String productAttr, String warehouseName);

	@Query("from StockDetail d where d.productCd =?1 and d.warehouseName =?2 and d.availableNumber= ?3 and d.productAttr =?4 and d.enterpriseId = ?5")
	List<StockDetail> findByCondition(String productCd, String warehouseName, BigDecimal totalNumber, String productAttr, Long enterpriseId);

	@Query("select MAX(updatedDate) from StockDetail d where d.buyContractId = ?1 and d.availableNumber = d.deliveryOutNumber")
	Date findMaxDeliveryOutDate(String contractId);
}

