package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.ApplyServiceContract;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.util.ContractCfsUtil;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.ApplyServiceContractDao;
import com.spt.bas.server.service.IApplyServiceContractService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component("applyServiceContractService")
@Transactional(readOnly = true)
public class ApplyServiceContractServiceImpl extends BaseService<ApplyServiceContract>
		implements IApplyServiceContractService, IPmService, IPmApproveListener {
	@Autowired
	private ApplyServiceContractDao applyServiceContractDao;
	@Autowired
	private ICtrContractSaveService ctrContractSaveService;

	@Override
	public BaseDao<ApplyServiceContract> getBaseDao() {
		return applyServiceContractDao;
	}

	@Override
	public Class<ApplyServiceContract> getEntityClazz() {
		return ApplyServiceContract.class;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyServiceContractDao.updateFileId(id, fileId);
	}

	@Override
	public ApplyServiceContract findByContractId(Long contractId) {
		return applyServiceContractDao.findByContractId(contractId);
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyServiceContract service = applyServiceContractDao.findOne(approve.getBizId());
			// 保存合同主表
			CtrContract entity = new CtrContract();
			entity.setContractNo(service.getContractNo());
			entity.setLinkContractId(ContractCfsUtil.addContractId(service.getLinkContractId()));
			entity.setContractTime(service.getContractTime());
			entity.setCompanyId(service.getCompanyId());
			entity.setCompanyName(service.getCompanyName());
			entity.setOurCompanyName(service.getOurCompanyName());
			entity.setTotalAmount(service.getServiceAmount());
			entity.setRemark(service.getRemark());
			entity.setFileId(service.getFileId());
			entity.setBusinessType(BasConstants.BUSINESS_TYPE_FW);
			entity.setSource(BasConstants.APPLY_TYPE_FW);
			entity.setContractType(BasConstants.CONTRACTTYPE_SELL);
			entity.setApproveId(service.getApproveId());
			entity.setEnterpriseId(service.getEnterpriseId());
			entity = ctrContractSaveService.saveContract(entity, new ArrayList<ApplyProductDetail>(), approve);

			service.setContractId(entity.getId());
			this.saveEntity(service);
		}

	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		// TODO Auto-generated method stub

	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		if (pmEntity instanceof ApplyServiceContract) {
			ApplyServiceContract serviceContract = (ApplyServiceContract) pmEntity;
			if(serviceContract.getId()==0){
				//生成服务合同号
				String contractNo = composeContractNo(serviceContract.getLinkContractNo());
				serviceContract.setContractNo(contractNo);
			}
			BigDecimal serviceRate = serviceContract.getServiceRate();
			BigDecimal totalAmount = serviceContract.getTotalAmount();
			BigDecimal serviceAmount = serviceRate.multiply(totalAmount);
			serviceContract.setServiceAmount(serviceAmount);
			return save(serviceContract);
		}
		return null;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			ApplyServiceContract serviceContract = (ApplyServiceContract) pmEntity;
			String companyName = serviceContract.getCompanyName();
			BigDecimal serviceAmount = serviceContract.getServiceAmount();
			String serviceAmountStr = NumberUtil.formatNumber(serviceAmount, "#.##");
			String rateStr = DictUtil.getValue(BasConstants.DICT_TYPE_SERVICE_CONTRACT_RATE,
					String.valueOf(serviceContract.getServiceRate()));
			String subject = String.format("%s %s %s", companyName, "[" + serviceAmountStr + "]", rateStr);
			return subject;
		}
		return null;
	}

	private String composeContractNo(String linkContractNo) {
		List<ApplyPay> deliveryIn = applyServiceContractDao.findByLinkContractNo(linkContractNo);
		String fmt = String.format("%02d", deliveryIn.size() + 1);
		return linkContractNo + BasConstants.APPLY_TYPE_FW + fmt;
	}

}
