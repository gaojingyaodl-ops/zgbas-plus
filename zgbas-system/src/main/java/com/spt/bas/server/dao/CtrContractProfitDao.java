package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractProfit;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CtrContractProfitDao extends BaseDao<CtrContractProfit> {

   List<CtrContractProfit> findByApproveIdAndProfitTypeAndLevel(Long approId, String profitType, Long level);

   List<CtrContractProfit> findByAndApproveId(Long approId);

   CtrContractProfit findBySellContractNo(String  contractNo);

   CtrContractProfit findByBuyContractNo(String  contractNo);


   @Query("from CtrContractProfit p where p.buyContractNo in ?1")
   List<CtrContractProfit> findByBuyContractNoIn(List<String> buyContractNoList);

   @Query("from CtrContractProfit p where p.sellContractNo in ?1")
   List<CtrContractProfit> findBySellContractNoIn(List<String> sellContractNoList);
}
