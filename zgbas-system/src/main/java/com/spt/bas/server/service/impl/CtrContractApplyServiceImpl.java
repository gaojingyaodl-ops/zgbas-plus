package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.CtrContractApplyDao;
import com.spt.bas.server.service.ICtrContractApplyService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.InvalidParamException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Component
@Transactional(readOnly = true)
public class CtrContractApplyServiceImpl extends BaseService<CtrContractApply> implements ICtrContractApplyService {
	@Autowired
	private CtrContractApplyDao ctrContractApplyDao;
	@Override
	public BaseDao<CtrContractApply> getBaseDao() {
		return ctrContractApplyDao;
	}

	@Override
	public Class<CtrContractApply> getEntityClazz() {
		return CtrContractApply.class;
	}

	@Override
	@ServerTransactional
	public void saveCtrContractApply(Long contractId,Long enterpriseId) {
		CtrContractApply contractApply = new CtrContractApply();
		contractApply.setCtrContractId(contractId);
		contractApply.setEnterpriseId(enterpriseId);
		ctrContractApplyDao.save(contractApply);
	}

	/**
	 * 更新合同申请单
	 * @param vo
	 * @throws ApplicationException
	 */
	@Override
	@ServerTransactional
	public void updateCtrContractApply(CtrContractApplyVo vo) throws ApplicationException {
		if (vo.getContractId()==null || vo.getContractId()==0L) {
			throw new InvalidParamException("contractId");
		}
		Date realDate = vo.getRealDate();
		CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(vo.getContractId());
		if (contractApply != null) {
			BigDecimal applyPayAmount = contractApply.getApplyPayAmount();
			BigDecimal applyBillAmount = contractApply.getApplyBillAmount();
			BigDecimal applyWarehouseNumber = contractApply.getApplyWarehouseNumber();
			BigDecimal applyRefundAmount = contractApply.getApplyRefundAmount();
			BigDecimal applyServiceAmount = contractApply.getApplyServiceAmount();
			BigDecimal applyServiceBillAmount = contractApply.getApplyServiceBillAmount();
			BigDecimal applyTransportAmount = contractApply.getApplyTransportAmount();
			BigDecimal applyWarehouseAmount = contractApply.getApplyWarehouseAmount();
			BigDecimal applyStevedorage = contractApply.getApplyStevedorage();
			String applyType = vo.getApplyType();
			BigDecimal dealAmount = vo.getDealAmount();
			BigDecimal dealNumber = vo.getDealNumber();
			// 当前申请单费用
			BigDecimal transportAmount = vo.getTransportAmount();
			BigDecimal warehouseAmount = vo.getWarehouseAmount();
			BigDecimal stevedorage = vo.getStevedorage();
			// 付\收款单申请 更新付款金额（applyPayAmount）、实际付款日期（realPayDate）
			if(BasConstants.APPLY_TYPE_P.equals(applyType) || BasConstants.APPLY_TYPE_E.equals(applyType)){
				contractApply.setApplyPayAmount(applyPayAmount.add(dealAmount));
				contractApply.setRealPayDate(realDate);
			}else if(BasConstants.APPLY_TYPE_N.equals(applyType) || BasConstants.APPLY_TYPE_V.equals(applyType)){
				// 收\开票申请 更新开票金额（applyBillAmount）、实际开\收票日期（realBillDate）
				contractApply.setApplyBillAmount(applyBillAmount.add(dealAmount));
				contractApply.setRealBillDate(realDate);
			}else if(BasConstants.APPLY_TYPE_O.equals(applyType) || BasConstants.APPLY_TYPE_I.equals(applyType)){
				contractApply.setApplyWarehouseNumber(applyWarehouseNumber.add(dealNumber));
				contractApply.setRealWarehoseDate(realDate);
				contractApply.setApplyTransportAmount(applyTransportAmount.add(transportAmount));
				contractApply.setApplyWarehouseAmount(applyWarehouseAmount.add(warehouseAmount));
				contractApply.setApplyStevedorage(applyStevedorage.add(stevedorage));
			}else if(BasConstants.APPLY_TYPE_K.equals(applyType)){
				contractApply.setApplyPayAmount(BigDecimal.ZERO);
				contractApply.setApplyBillAmount(BigDecimal.ZERO);
				contractApply.setApplyWarehouseNumber(BigDecimal.ZERO);
			}else if(BasConstants.APPLY_TYPE_U.equals(applyType)) {
				contractApply.setApplyRefundAmount(applyRefundAmount.add(dealAmount));
			}else if(BasConstants.APPLY_TYPE_H.equals(applyType)){
				contractApply.setApplyServiceAmount(applyServiceAmount.add(dealAmount));
			}else if(BasConstants.APPLY_TYPE_SB.equals(applyType)){
				contractApply.setApplyServiceBillAmount(applyServiceBillAmount.add(dealAmount));
			}
			ctrContractApplyDao.save(contractApply);
		}
	}

	@Override
	public CtrContractApply findByContractId(Long contractId) {
		return this.ctrContractApplyDao.findByCtrContractId(contractId);
	}
}

