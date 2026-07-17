package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.vo.BasContractExistVo;
import com.spt.bas.client.vo.BasContractRelaVo;
import com.spt.bas.client.vo.BasContractVo;
import com.spt.bas.client.vo.ContractOpVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BasContractDao;
import com.spt.bas.server.service.IBasContractOphisService;
import com.spt.bas.server.service.IBasContractService;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.InvalidParamException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component("basContractService")
@Transactional(readOnly = true)
public class BasContractServiceImpl extends BaseService<BasContract> implements IBasContractService, IPmService {
	@Autowired
	private BasContractDao basContractDao;
	@Autowired
	private IBsKeySequenceService keySequenceService;
	@Autowired
	private IBasContractOphisService contractOphisService;

	@Override
	public BaseDao<BasContract> getBaseDao() {
		return basContractDao;
	}


	@Override
	@ServerTransactional
	public BasContract save(BasContract entity) throws ApplicationException {
		if (entity.getId() == null || entity.getId() == 0) {
			entity.setBusinessNo(keySequenceService.getNextKey(BasConstants.KEYSEQUENCE_CATEGORY_BUSINESSNO, entity.getEnterpriseId()));
		}
		// var dealNumber =$('#dealNumber').numberbox('getValue');
		// var dealPrice = $(this).val();
		// var dealAmount = (dealNumber*dealPrice).toFixed(2);
		// $('#dealAmount').val(dealAmount);
		// var dealAmountNotax = (dealAmount/1.17).toFixed(2);
		// $('#dealAmountNotax').val(dealAmountNotax);
		// $('#taxAmount').val((dealAmountNotax*0.17).toFixed(2));

		BigDecimal dealAmountNotax = entity.getDealAmount().divide(BigDecimal.valueOf(1.17), 2,
				BigDecimal.ROUND_HALF_UP);
		BigDecimal taxAmount = dealAmountNotax.multiply(BigDecimal.valueOf(0.17));
		entity.setDealAmountNotax(dealAmountNotax);
		entity.setTaxAmount(taxAmount);
		return super.save(entity);
	}

	@Override
	public Class<BasContract> getEntityClazz() {
		return BasContract.class;
	}

	@Override
	public boolean existGoodsCode(BasContractExistVo vo) {
		long cnt = basContractDao.countByContractNo(vo.getContractNo(), vo.getContractNoOld());
		return cnt > 0;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {

		basContractDao.updateFileId(id, fileId);
	}

	@Override
	@ServerTransactional
	public void updatePayAmount(Long contractId, String payType, BigDecimal payAmount) throws ApplicationException {
		if (contractId == null || contractId == 0) {
			throw new InvalidParamException("contractId");
		}
		if (StringUtils.isBlank(payType)) {
			throw new InvalidParamException("payType");
		}
		if (payAmount == null) {
			throw new InvalidParamException("payAmount");
		}

		BasContract contract = basContractDao.findOne(contractId);
		if (BasConstants.PAY_TYPE_BOND.equals(payType)) {
			if (contract.getPayBond() != null && contract.getPayBond().compareTo(BigDecimal.ZERO) > 0) {
				throw new ApplicationException("定金已经支付，不能重复申请");
			}
			contract.setPayBond(payAmount);
		} else if (BasConstants.PAY_TYPE_APPEND.equals(payType)) {
			//追加保证金
			BigDecimal payBond = contract.getPayBond();
			if (payBond == null) {
				payBond = BigDecimal.ZERO;
			}
			contract.setPayBond(payBond.add(payAmount));

		} else if (BasConstants.PAY_TYPE_REMAIN.equals(payType)) {
			if (contract.getPayRemain() != null && contract.getPayRemain().compareTo(BigDecimal.ZERO) > 0) {
				throw new ApplicationException("尾款已经支付，不能重复申请");
			}
			contract.setPayRemain(payAmount);
		} else if (BasConstants.PAY_TYPE_ALL.equals(payType)) {
			if (contract.getPayRemain() != null && contract.getPayRemain().compareTo(BigDecimal.ZERO) > 0) {
				throw new ApplicationException("全款已经支付，不能重复申请");
			}
			BigDecimal payBond = payAmount.multiply(contract.getBondRate());
			contract.setPayBond(payBond);
			contract.setPayRemain(payAmount.subtract(payBond));
		}
		basContractDao.save(contract);
	}

	@Override
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		if (pmEntity != null) {
			BasContract entity = (BasContract) pmEntity;

			if (entity.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
				//更新合同状态：已签约
				entity.setContractStatus(BasConstants.CONTRACTSTATUS_S);
			}
			return save(entity);
		}
		return null;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			BasContract entity = (BasContract) pmEntity;
			String contractTypeName = DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTTYPE, entity.getContractType());
			String dealPrice = NumberUtil.formatDealNum(entity.getDealPrice());
			String dealNumber = NumberUtil.formatDealNum(entity.getDealNumber());
			String subject = String.format("%s, %s, %s, %s 元 / %s %s", contractTypeName, entity.getOppCompanyName(),
					entity.getProductName(), dealPrice, dealNumber, entity.getNumberUnit());
			return subject;
		}
		return null;
	}

	@Override
	public List<BasContractVo> saveBatchByRelaApprove(BasContractRelaVo relaVo) throws ApplicationException {
		List<BasContractVo> lstInsert = relaVo.getLstInsert();
		List<BasContractVo> lstUpdate = relaVo.getLstUpdate();
		List<BasContractVo> lstDelete = relaVo.getLstDelete();

		List<BasContractVo> bsList = new ArrayList<BasContractVo>();
		BasContract entity;
		for (BasContractVo vo : lstInsert) {
			entity = new BasContract();
			BeanUtils.copyProperties(vo, entity);
			entity.setContractTime(relaVo.getContractTime());
			entity.setProductCode(relaVo.getProductCode());
			entity.setProductName(relaVo.getProductName());
			entity.setOurCompanyName(relaVo.getOurCompanyName());
			entity.setContractRelaId(relaVo.getId());
			entity.setApproveId(relaVo.getApproveId());
			entity.setMatchUserId(relaVo.getMatchUserId());
			entity.setMatchUserName(relaVo.getMatchUserName());
			entity.setRemainNumber(entity.getDealNumber());
			if (entity.getDeliveryDateTo() == null) {
				entity.setDeliveryDateTo(entity.getDeliveryDateFrom());
			}
			entity.setContractRelaId(relaVo.getId());
			entity.setCloseFlg(false);
			entity.setBrandNumber(relaVo.getBrandNumber());
			entity.setEnterpriseId(relaVo.getEnterpriseId());
			BasContract bc = save(entity);
			vo.setId(bc.getId());
			bsList.add(vo);
		}
		for (BasContractVo vo : lstUpdate) {
			entity = new BasContract();
			BeanUtils.copyProperties(vo, entity);
			entity.setContractTime(relaVo.getContractTime());
			entity.setProductCode(relaVo.getProductCode());
			entity.setProductName(relaVo.getProductName());
			entity.setOurCompanyName(relaVo.getOurCompanyName());
			entity.setContractRelaId(relaVo.getId());
			entity.setApproveId(relaVo.getApproveId());
			entity.setRemainNumber(entity.getDealNumber());
			if (entity.getDeliveryDateTo() == null) {
				entity.setDeliveryDateTo(entity.getDeliveryDateFrom());
			}
			entity.setMatchUserId(relaVo.getMatchUserId());
			entity.setMatchUserName(relaVo.getMatchUserName());
			entity.setContractRelaId(relaVo.getId());
			entity.setBrandNumber(relaVo.getBrandNumber());
			entity.setCloseFlg(false);
			save(entity);
			bsList.add(vo);
		}
		for (BasContractVo vo : lstDelete) {
			delete(vo.getId());
		}
		return bsList;
	}

	@Override
	public List<BasContract> findByContractRelaId(Long id) {
		return basContractDao.findByContractRelaId(id);
	}

//	@Override
//	@ServerTransactional
//	public void updateContractStatus(Long contractId, String contractStatus) throws ApplicationException {
//		if (contractId == null || contractId == 0) {
//			throw new InvalidParamException("contractId");
//		}
//		if (StringUtils.isBlank(contractStatus)) {
//			throw new InvalidParamException("contractStatus");
//		}
//		basContractDao.updateContractStatus(contractId, contractStatus);
//	}
//
//	@Override
//	@ServerTransactional
//	public void updateContractStatusByFond(BasContract contract) {
//		basContractDao.updateContractStatus(contract.getId(), contract.getContractStatus());
//		basContractDao.updateFondFlg(contract.getId(), contract.getFondFlg());
//		
//		contractOphisService.addOphis(contract);
//	}
//
//	@Override
//	@ServerTransactional
//	public void updateContractStatusByBill(BasContract contract) {
//		basContractDao.updateContractStatus(contract.getId(), contract.getContractStatus());
//		basContractDao.updateBillFlg(contract.getId(), contract.getBillFlg());
//		
//		contractOphisService.addOphis(contract);
//	}

	/**
	 * 合同状态操作
	 */
	@ServerTransactional
	public void doContractOp(ContractOpVo opVo) {

		if (opVo.getBillFlg()) {
			basContractDao.updateBillFlg(opVo.getId(), opVo.getBillFlg());
		}
		if (opVo.getFondFlg()) {
			basContractDao.updateFondFlg(opVo.getId(), opVo.getFondFlg());
		}
		if (opVo.getPayFlg()) {
			basContractDao.updatePayFlg(opVo.getId(), opVo.getPayFlg());
		}

		basContractDao.updateContractStatus(opVo.getId(), opVo.getContractStatus());
		contractOphisService.addOphis(opVo);
	}

	/**
	 * 更新敞口业务合同的剩余数量
	 */
	@Override
	@ServerTransactional
	public void updateExposureContract(Long exposureContractId, BigDecimal useDealNumber) {
		// TODO Auto-generated method stub
		//获取敞口业务合同
		BasContract bc = basContractDao.findOne(exposureContractId);
		if (bc != null) {
			BigDecimal remainNumber = bc.getRemainNumber();
			if (remainNumber != null) {
				bc.setRemainNumber(remainNumber.subtract(useDealNumber));
				basContractDao.save(bc);
			}
		}
	}

}
