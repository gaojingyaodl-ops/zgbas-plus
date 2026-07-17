package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.tools.data.annotation.ServiceTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.CtrContractLoss;
import com.spt.bas.server.dao.CtrContractLossDao;
import com.spt.bas.server.service.ICtrContractLossService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

import java.math.BigDecimal;

@Component
@Transactional(readOnly = false)
public class CtrContractLossServiceImpl extends BaseService<CtrContractLoss> implements ICtrContractLossService {
	@Autowired
	private CtrContractLossDao ctrContractLossDao;
	@Autowired
	private ICtrContractService ctrContractService;
	
	@Override
	public BaseDao<CtrContractLoss> getBaseDao() {
		return ctrContractLossDao;
	}
	
	@Override
	public Class<CtrContractLoss> getEntityClazz() {
		return CtrContractLoss.class;
	}

	@Override
	public void updateFileId(Long id, String fileId) {
		ctrContractLossDao.updateFileId(id, fileId);
		
	}

	@Override
	@ServiceTransactional
	public void updateEnableFlg(Long id, Boolean enableFlg,Long contractId) {
		try {
			CtrContractLoss contractLoss = ctrContractLossDao.findOne(id);
			CtrContract contract = ctrContractService.getEntity(contractId);
			BigDecimal lossAmount = contractLoss.getLossAmount();
			BigDecimal lossNumber = contractLoss.getLossNum();
			BigDecimal contractLossAmount = contract.getLossAmount();
			BigDecimal contractLossNumber = contract.getLossNumber();
			if(contractLossAmount != null) {
				contractLossAmount = contractLossAmount.subtract(lossAmount);
			}
			if(contractLossAmount.compareTo(new BigDecimal("0")) < 0){
				contractLossAmount = new BigDecimal("0");
			}
			if(contractLossNumber != null) {
				contractLossNumber = contractLossNumber.subtract(lossNumber);
			} 
			if(contractLossNumber.compareTo(new BigDecimal("0")) < 0){
				contractLossNumber = new BigDecimal("0");
			}
			contract.setLossAmount(contractLossAmount);
			contract.setLossNumber(contractLossNumber);
			ctrContractService.save(contract);
			
			ctrContractLossDao.updateEnableFlg(id, enableFlg);

		} catch (Exception e) {
			logger.error("系统异常：",e);
		}
	}

	@Override
	public int updateContractLoss(CtrContractLoss vo) {
		return ctrContractLossDao.updateContractLoss(vo.getLossNum(),vo.getLossAmount(),vo.getContractId());
	}

	@Override
	public CtrContractLoss findByContractId(Long contractId) {
		CtrContractLoss  entity = ctrContractLossDao.findByContractId(contractId);
		return entity;
	}
	
}

