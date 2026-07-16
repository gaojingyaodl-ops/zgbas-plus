package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.StockDetailPresell;
import com.spt.tools.jpa.dao.BaseDao;

public interface StockDetailPresellDao extends BaseDao<StockDetailPresell> {

	public StockDetailPresell findByCtrProductId(Long sellProductId);

	@Query("from StockDetailPresell h where h.presellNumber > h.buyedNumber+h.approveBuyNumber and h.enterpriseId = ?1")
	public List<StockDetailPresell> findApplyPage(Long enterpriseId, Pageable request);

	public List<StockDetailPresell> findByContractId(Long contractId);

	@Query("select s from StockDetailPresell s,CtrContract c where s.contractId = c.id and s.presellNumber > (s.buyedNumber + s.approveBuyNumber) and DateDiff(NOW(),s.updatedDate) > ?1 and c.contractStatus != 'C'")
	public List<StockDetailPresell> findPresellSchedulte(Integer days);
}

