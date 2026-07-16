package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrProduct;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CtrProductDao extends BaseDao<CtrProduct> {

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_product WHERE ctr_contract_id =?1 LIMIT 1")
	CtrProduct findOneByCtrContractId(Long contractId);

	List<CtrProduct> findByCtrContractId(Long contractId);
	@Query(" from CtrProduct c where c.productCd =?1 and c.brandNumber=?2 and c.factoryId=?3 and c.warehouseName=?4 and c.ctrContractId =?5 order by c.createdDate asc ")
	List<CtrProduct> findProductList(String productCd, String brandNumber, Long factoryId, String warehouseName,
                                     Long contractId);
	@Query("select count(c.id) from CtrProduct c where c.ctrContractId =?1 and c.stockContractId =?2 ")
	Long countStockContractId(Long ctrContractId, Long stockContractId);

	@Query(" from CtrProduct c where c.ctrContractId =?1 ")
	List<CtrProduct> findByOutCtrContractId(Long ctrContractId);

	@Query("select MIN(c.deliveryDateTo) from CtrProduct p,CtrContract c where p.ctrContractId = c.id and p.id in ?1")
    Object findMinDeliveryDateByProductId(List<Long> productList);

	@Query("select p from CtrProduct p,CtrContract c where p.ctrContractId=c.id and p.productCd = ?1 and p.enterpriseId = ?2 and c.contractType = ?3 and c.contractStatus != 'C' order by p.id desc")
    List<CtrProduct> findByProductCd(String productCd, Long enterpriseId, String contractType);

	@Query("select count(*) from CtrProduct")
    Integer selectAllCount();
}

