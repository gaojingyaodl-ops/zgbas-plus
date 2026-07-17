package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApplyCancel2Vo;
import com.spt.bas.server.service.*;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.sign.client.remote.ICfcaSignClient;
import com.spt.sign.client.remote.ISignInfoClient;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component("applyCancel2Service")
@Transactional(readOnly = true)
@Slf4j
public class ApplyCancel2ServiceImpl
		implements IApplyCancel2Service, IPmApproveListener {
	@Autowired
	private PmApproveContentsDao pmApproveContentsDao;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private IPmApproveService pmApproveService;
	@Autowired
	private IBsCompanyService bsCompanyService;
	/**
	 * 发起审批
	 *
	 * @param approve
	 */
	@Override
	public void doStepIn(PmApprove approve) throws ApplicationException {
		// 校验预算是否已进行业务环节
		PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
		String contents = pmApproveContents.getContents();
		ApplyCancel2Vo applyCancel2Vo = JsonUtil.json2Object(ApplyCancel2Vo.class, contents);
		log.info("applyCancel2Vo:{}",contents);
		CtrContract buyContract = ctrContractService.findByContractNo(applyCancel2Vo.getBuyContractNo());
		CtrContract sellContract = ctrContractService.findByContractNo(applyCancel2Vo.getSellContractNo());
		if (buyContract.getDealedAmount().compareTo(BigDecimal.ZERO) > 0
		|| sellContract.getDealedAmount().compareTo(BigDecimal.ZERO) > 0) {
			throw new ApplicationException("已开始业务，无法作废");
		}
	}

	/**
	 * 执行审批步骤
	 *
	 * @param approve
	 * @param nextStep
	 */
	@Override
	@ServiceTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
			String contents = pmApproveContents.getContents();
			ApplyCancel2Vo applyCancel2Vo = JsonUtil.json2Object(ApplyCancel2Vo.class, contents);
			log.info("applyCancel2Vo:{}",contents);
			CtrContract buyContract = ctrContractService.findByContractNo(applyCancel2Vo.getBuyContractNo());
			CtrContract sellContract = ctrContractService.findByContractNo(applyCancel2Vo.getSellContractNo());
			if (buyContract.getDealedAmount().compareTo(BigDecimal.ZERO) > 0
					|| sellContract.getDealedAmount().compareTo(BigDecimal.ZERO) > 0) {
				throw new ApplicationException("已开始业务，无法作废");
			}
			buyContract.setContractStatus(BasConstants.CONTRACTSTATUS_C);
			sellContract.setContractStatus(BasConstants.CONTRACTSTATUS_C);
			Long approveId = sellContract.getApproveId();
			PmApprove pmApprove = pmApproveService.getEntity(approveId);
			pmApprove.setStatus(BasConstants.CONTRACTSTATUS_C);
			pmApproveService.save(pmApprove);
			// 回调额度
			// 赊销 销售合同作废修正额度
			if (sellContract.getSettlementType() != null){
				BsCompany company = bsCompanyService.getEntity(sellContract.getCompanyId());
				company.setUsedCreditAmount(company.getUsedCreditAmount().subtract(sellContract.getTotalAmount()));
				bsCompanyService.save(company);
			}
		}
	}

	/**
	 * 审批撤回
	 *
	 * @param vo
	 */
	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

	}


}
