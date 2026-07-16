package com.spt.bas.server.dao;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.CtrContractLoss;
import com.spt.tools.jpa.dao.BaseDao;

public interface CtrContractLossDao extends BaseDao<CtrContractLoss> {
	@Transactional
	@Modifying
	@Query("update CtrContractLoss c set c.fileId =?2 where c.id=?1 ")
	public void updateFileId(Long id, String fileId);

	@Transactional
	@Modifying
	@Query("update CtrContractLoss c set c.enableFlg =?2 where c.id=?1 ")
	public void updateEnableFlg(Long id, Boolean enableFlg);

	@Transactional
	@Modifying
	@Query("update CtrContractLoss  set lossNum =?1 ,lossAmount=?2 where contractId=?3 ")
	public int updateContractLoss(BigDecimal lossNum, BigDecimal lossAmount, Long contractId);

	public CtrContractLoss findByContractId(Long contractId);
}

