package com.spt.bas.server.dao;


import com.spt.bas.client.entity.ApplyCreditCycle;
import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ApplyCtrContractFactoDao extends BaseDao<ApplyCtrContractFactor> {

   ApplyCtrContractFactor findByApproveId(Long approveid);

   ApplyCtrContractFactor findByContractNo(String contrcact);


   @Transactional
   @Modifying
   @Query("update ApplyCtrContractFactor c set c.factorStatus=?1, c.loanAmount =?2,c.loanDate=?3  where c.contractNo=?4 ")
   void updateFacto(String status,BigDecimal londamount, Date londDate, String contrcact);


   @Transactional
   @Modifying
   @Query("update ApplyCtrContractFactor c set c.factorStatus=?2 where c.contractNo=?1")
   void updateStatusByContractNo(String contractNo,String factorStatus);

}
