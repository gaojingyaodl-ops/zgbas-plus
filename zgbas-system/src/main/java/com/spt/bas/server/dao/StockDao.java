package com.spt.bas.server.dao;//package com.spt.bas.server.dao;
//
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.List;
//
//import org.springframework.data.jpa.repository.Query;
//
//import com.spt.bas.client.entity.Stock;
//import com.spt.tools.jpa.dao.BaseDao;
//
//public interface StockDao extends BaseDao<Stock> {
//
//	@Query("from Stock b where b.brandNumber=?1 and productAttr=?2 and b.enterpriseId=?3")
//	public Stock findBrandNumber(String brandNumber,String productAttr,Long enterpriseId);
//
////	@Query("from Stock c where c.productCd =?1 and c.brandNumber=?2 and c.enterpriseId=?3 ")
////	public List<Stock> findWarehouse(String productCd, String brandNumber,Long enterpriseId);
//
//	@Query("select c.frozenNumber,c.realNumber from Stock c where c.productCd =?1 and c.brandNumber=?2 and c.factoryId=?3 and c.warehouseName=?4 and c.enterpriseId = ?5")
//	public List<Object> findDealNumber(String productCd, String brandNumber,Long factoryId,String warehouseName,Long enterpriseId);
//
//	@Query("from Stock c where c.productCd =?1 and c.brandNumber=?2 and c.factoryId=?3 and c.warehouseName=?4 and c.enterpriseId=?5  order by c.productAttr")
//	public List<Stock> findStockList(String productCd, String brandNumber,Long factoryId,String warehouseName,Long enterpriseId);
////	@Query("from Stock c where c.productCd =?1 and c.brandNumber=?2 and c.factoryId=?3 and c.warehouseName=?4 and c.enterpriseId=?5  and c.productAttr=?6 ")
////	public List<Stock> findStockList(String productCd, String brandNumber,Long factoryId,String warehouseName,Long enterpriseId,String productAttr);
//
//	@Query("select c from Stock c,StockDetail d where  c.id = d.stockId and c.productCd =?1 and c.brandNumber=?2 and c.factoryId=?3 and c.enterpriseId=?4 and c.realNumber>=?5 and d.buyContractId =?6 and (?7 is null or d.id=?7) ")
//	public List<Stock> findStock4CancelBuy(String productCd, String brandNumber, Long factoryId, Long enterpriseId, BigDecimal dealNumber,String buyContractId,Long stockDetailId);
//	@Query("from Stock c where c.productCd =?1 and c.brandNumber=?2 and c.factoryId=?3 and c.warehouseName=?4 and c.enterpriseId=?5 and c.frozenNumber>=?6 and c.productAttr=?7 ")
//	public List<Stock> findStockForzenNumber(String productCd, String brandNumber, Long factoryId, String warehouseName,
//			Long enterpriseId, BigDecimal dealNumber, String productAttr);
//
//	@Query("select c from Stock c,StockDetail d where c.id = d.stockId and c.productCd =?1 and c.brandNumber=?2 and c.factoryId=?3 and c.enterpriseId=?4 and c.frozenNumber>0 and d.sellContractId like %?5% ")
//	public List<Stock> findStock4CancelSell(String productCd, String brandNumber, Long factoryId, Long enterpriseId, String sellContractId);
//
//	@Query("from Stock d where d.updatedDate >= ?1 and d.updatedDate< ?2")
//	List<Stock> findYesterdayData(Date startDate, Date endDate);
//}
//
