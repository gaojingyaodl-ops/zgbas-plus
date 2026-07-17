package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyPayRefundDcsx;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.bas.client.vo.protocol.SupplementaryAgreement;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyPayRefundDcsxDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component("applyPayRefundDcsxService")
@Transactional(readOnly = true)
public class ApplyPayRefundDcsxServiceImpl extends BaseService<ApplyPayRefundDcsx> implements IApplyPayRefundDcsxService ,IPmService, IPmApproveListener{
	@Autowired
	private ApplyPayRefundDcsxDao applyPayRefundDcsxDao;
	@Autowired
	private IApplyReceiveRefundDcsxService applyReceiveRefundDcsxService;
	@Autowired
	private IApplyDcsxService applyDcsxService;
	@Autowired
	private ICtrContractOphisService contractOphisService;
	@Autowired
	private IPmApproveService pmApproveService;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Resource
	private IBsCompanyOurService companyOurService;
	@Resource
	private IBsCompanyDcsxService companyDcsxService;
	@Resource
	private IPmProcessService pmProcessService;

	@Override
	public BaseDao<ApplyPayRefundDcsx> getBaseDao() {
		return applyPayRefundDcsxDao;
	}

	@Override
	public Class<ApplyPayRefundDcsx> getEntityClazz() {
		return ApplyPayRefundDcsx.class;
	}
	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		ApplyPayRefundDcsx entity = applyPayRefundDcsxDao.findOne(approve.getBizId());
		Long contractId = entity.getContractId();
		ApplyCtrDCSX ctrDcsx = applyDcsxService.getEntity(contractId);
		//2.添加合同操作记录
		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDcsx.getBusinessType())) {
			contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_PU,ctrDcsx.getContractStatus(), contractId, approve, entity.getRefundDate());
		} else {
			contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_PU,ctrDcsx.getContractStatus(), contractId, approve, entity.getRefundDate());
		}
	}
	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyPayRefundDcsx entity = applyPayRefundDcsxDao.findOne(approve.getBizId());
			//1.保存合同回款金额
			Long contractId = entity.getContractId();
			BigDecimal refundAmount = entity.getRefundAmount();
			ApplyCtrDCSX ctrDcsx = applyDcsxService.getEntity(contractId);
			if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDcsx.getBusinessType())) {
				if (ctrDcsx.getSettlementTotalAmount().compareTo(ctrDcsx.getReceiveAmount().subtract(entity.getRefundAmount())) !=0) {
					throw new ApplicationException("不能重复退款，请驳回");
				}
			} else {
				if (ctrDcsx.getTotalAmount().compareTo(ctrDcsx.getReceiveAmount().subtract(entity.getRefundAmount())) !=0) {
					throw new ApplicationException("不能重复退款，请驳回");
				}
			}
			applyDcsxService.addPayRefundAmount(contractId, refundAmount);

			// 适配历史审批中的审批单添加操作记录：过段时间可以去除
			Date createdDate = approve.getCreatedDate();
			if (createdDate != null) {
				String format = DateUtil.format(createdDate, "yyyy-MM-dd");
				if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
					// 添加合同操作记录
					if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDcsx.getBusinessType())) {
						contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_PU,ctrDcsx.getContractStatus(), contractId, approve, entity.getRefundDate());
					} else {
						contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_PU,ctrDcsx.getContractStatus(), contractId, approve, entity.getRefundDate());
					}
				}
			}

			// 自动发起收退款申请
			if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, ctrDcsx.getBusinessType())) {
				try {
					applyReceiveRefundDcsxService.autoApplyReceiveRefundDcsx(approve,ctrDcsx, entity);
				} catch (Exception e) {
					logger.error("doApplyDcsxReceiveTask error", e);
				}
			}
		}
	}

	@Override
	@ServerTransactional
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		ApplyPayRefundDcsx entity = applyPayRefundDcsxDao.findOne(vo.getBizId());
		rollbackContractApply(entity);
	}

	@Override
	@ServerTransactional
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
		ApplyPayRefundDcsx entity = applyPayRefundDcsxDao.findOne(approve.getBizId());
		rollbackContractApply(entity);
	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		if (pmEntity != null) {
			ApplyPayRefundDcsx entity = (ApplyPayRefundDcsx) pmEntity;
			PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
			if(entity1 != null){
				SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
				entity.setDeptId(deptByUserId.getDeptId());
			}
			//新增时
			if(entity.getId()==0){
				//生产退款编号
				String applyNo= composeContractNo(entity.getContractNo());
				entity.setApplyNo(applyNo);
			}
			return save(entity);
		}
		return null;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			ApplyPayRefundDcsx payRefund = (ApplyPayRefundDcsx) pmEntity;
			String contractNo = payRefund.getContractNo();
			String companyName = payRefund.getCompanyName();
			BigDecimal sumNumber = payRefund.getRefundAmount();
			String payAmount = NumberUtil.formatNumber(sumNumber, "#.##");
			String companyName1 = RuleUtil.companyNameSubString(companyName);
			String companyName2 = RuleUtil.companyNameSubString(payRefund.getOurCompanyName());
			String company="";
			if(StringUtils.isNotBlank(companyName1)&&StringUtils.isNotBlank(companyName2)){
				company=companyName1+"-"+companyName2;
			}
			String subject = SubjectUtil.formatSubject(contractNo,company,SubjectUtil.formatMoney(sumNumber , RuleUtil.monetaryUnit),"代采赊销付退款");
			return subject;

		}
		return null;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyPayRefundDcsxDao.updateFileId(id, fileId);
	}

	@Override
	@ServerTransactional
	public void autoStartRefundWithProtocolDocument(SupplementaryAgreement agreement, PmApprove approve) {
		String contractNo = agreement.getContractNo();
		ApplyCtrDCSX ctrDCSX = applyDcsxService.findByContractNo(contractNo);
		if (Objects.isNull(ctrDCSX)){
			return;
		}
		BigDecimal totalAmount = agreement.getTotalAmount();
		BigDecimal alterTotalAmount = agreement.getAlterTotalAmount();
		if (Objects.isNull(alterTotalAmount) || alterTotalAmount.compareTo(totalAmount) >= 0){
			return;
		}
		BigDecimal receiveAmount = ctrDCSX.getReceiveAmount();
		if (Objects.isNull(receiveAmount) || receiveAmount.compareTo(alterTotalAmount) <= 0){
			return;
		}
		BigDecimal refundAmount = receiveAmount.subtract(alterTotalAmount);
		this.autoApplyPayRefundDcsx(ctrDCSX, alterTotalAmount, refundAmount, approve);
	}

	private void autoApplyPayRefundDcsx(ApplyCtrDCSX ctrDCSX, BigDecimal alterTotalAmount, BigDecimal refundAmount, PmApprove approve) {
		try {
			ApplyPayRefundDcsx entity = new ApplyPayRefundDcsx();
			entity.setId(0L);
			entity.setContractId(ctrDCSX.getId());
			entity.setContractNo(ctrDCSX.getContractNo());
			entity.setRefundAmount(refundAmount);
			entity.setRefundDate(new Date());
			entity.setTotalAmount(alterTotalAmount);
			entity.setPayedAmount(ctrDCSX.getReceiveAmount());
			entity.setCompanyName(ctrDCSX.getCompanyName());
			entity.setOurCompanyName(ctrDCSX.getOurCompanyName());
			entity.setStatus(BasConstants.APPROVE_STATUS_A);
			entity.setEnterpriseId(ctrDCSX.getEnterpriseId());
			entity.setDeptId(ctrDCSX.getDeptId());

			String bankName = "";
			String bankAccount = "";
			BsCompanyDcsx bsCompanyDcsx = companyDcsxService.findByCompanyName(ctrDCSX.getOurCompanyName());
			if (Objects.nonNull(bsCompanyDcsx)) {
				bankName = bsCompanyDcsx.getCompanyBankName();
				bankAccount = bsCompanyDcsx.getCompanyCardId();
			} else {
				BsCompanyOur companyOur = companyOurService.findByCompanyName(ctrDCSX.getOurCompanyName());
				if (Objects.nonNull(companyOur)) {
					bankName = companyOur.getCompanyBankName();
					bankAccount = companyOur.getCompanyCardId();
				}
			}
			entity.setBankName(bankName);
			entity.setBankAccount(bankAccount);

			String bizEntityJson = JsonUtil.obj2Json(entity);
			String processCode = BasConstants.PROCESS_APPLY_PAY_REFUND_DCSX;
			PmApproveSaveVo startVo = new PmApproveSaveVo();

			startVo.setMode(BasConstants.APPROVE_STATUS_A);
			startVo.setStatus(BasConstants.APPROVE_STATUS_A);
			startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
			startVo.setAutoStartMessage("补充协议自动发起代采赊销付退款申请，原审批编号：" + approve.getApproveNo());
			PmProcessSearchVo searchVo = new PmProcessSearchVo();
			searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
			searchVo.setProcessCode(processCode);
			PmProcess process = pmProcessService.findByProcessCode(searchVo);
			if (process == null) {
				logger.error("找不到流程记录");
			}
			startVo.setUserId(approve.getCreateUserId());
			startVo.setUserName(approve.getCreateUserName());
			startVo.setProcessId(process.getId());
			startVo.setApproveId(0L);
			startVo.setBizEntityJson(bizEntityJson);
			startVo.setAutoStartFlgReal(true);
			pmApproveService.startFlow(startVo);
		} catch (Exception e) {
			logger.error("autoApplyPayRefundDcsx error", e);
		}
	}

	private String composeContractNo(String contractNo) {
		List<ApplyPayRefundDcsx> payRefund = applyPayRefundDcsxDao.findByContractNo(contractNo);
		String fmt = String.format("%02d", payRefund.size() + 1);
		return contractNo + BasConstants.APPLY_TYPE_U + fmt;
	}

	private void rollbackContractApply(ApplyPayRefundDcsx entity) throws ApplicationException {
		//更新CtrContractApply中数据
		if(StringUtils.equals(BasConstants.APPROVE_STATUS_D,entity.getStatus())) {
			Long contractId = entity.getContractId();
			BigDecimal refundAmount = entity.getRefundAmount().negate();
			applyDcsxService.addPayRefundAmount(contractId, refundAmount);
		}
	}

}

