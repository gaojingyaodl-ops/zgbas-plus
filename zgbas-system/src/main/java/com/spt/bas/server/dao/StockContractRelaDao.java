package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;

import com.spt.bas.client.entity.StockContractRela;
import com.spt.tools.jpa.dao.BaseDao;

public interface StockContractRelaDao extends BaseDao<StockContractRela> {

	StockContractRela findByContractIdAndRelaTypeAndStockContractId(Long contractId, String relaType,
                                                                    Long stockContractId);
	/**销售只会有一条，入库/出库存在多条*/
	List<StockContractRela> findByRelaTypeAndCtrProductId(String relaType, Long ctrProductId);
	List<StockContractRela> findByRelaTypeAndStockContractId(String relaType, Long stockContractId);

	List<StockContractRela> findByApproveId(Long approveId);

	long countByStockContractIdAndRelaType(Long stockContractId, String relaType);
	@Transactional
	@Modifying
	void deleteByStockContractId(Long stockContractId);
	List<StockContractRela> findByStockContractId(Long stockContractId);
}
