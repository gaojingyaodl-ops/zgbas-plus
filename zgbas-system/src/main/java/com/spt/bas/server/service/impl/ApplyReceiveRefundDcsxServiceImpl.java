package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyReceiveRefundDcsxDao;
import com.spt.bas.server.enums.FundFlowEnum;
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
import com.spt.tools.data.annotation.ServiceTransactional;
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
import java.util.stream.Collectors;


/**
 * 代采赊销 收退款
 */
@Component("applyReceiveRefundDcsxService")
@Transactional(readOnly = true)
public class ApplyReceiveRefundDcsxServiceImpl extends BaseService<ApplyReceiveRefundDcsx> implements IApplyReceiveRefundDcsxService ,IPmService, IPmApproveListener{
	@Autowired
	private ApplyReceiveRefundDcsxDao applyReceiveRefundDcsxDao;
	@Autowired
	private IApplyDcsxService applyDcsxService;
	@Autowired
	private ICtrContractOphisService contractOphisService;

	@Autowired
	private IPmApproveService pmApproveService;
	@Autowired
	private IPmProcessService pmProcessService;
	@Autowired
	private IBsCompanyOurService companyOurService;
	@Autowired
	private IBsCompanyDcsxService companyDcsxService;
	@Resource
	private IFundAmountFlowService fundAmountFlowService;

	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@Override
	public BaseDao<ApplyReceiveRefundDcsx> getBaseDao() {
		return applyReceiveRefundDcsxDao;
	}

	@Override
	public Class<ApplyReceiveRefundDcsx> getEntityClazz() {
		return ApplyReceiveRefundDcsx.class;
	}

	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		ApplyReceiveRefundDcsx entity = applyReceiveRefundDcsxDao.findOne(approve.getBizId());
		Long contractId = entity.getContractId();
		ApplyCtrDCSX ctrDcsx = applyDcsxService.getEntity(contractId);
		//2.添加合同操作记录
		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDcsx.getBusinessType())) {
			contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_RU,ctrDcsx.getContractStatus(), contractId, approve, entity.getRefundDate());
		} else {
			contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_RU,ctrDcsx.getContractStatus(), contractId, approve, entity.getRefundDate());
		}
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyReceiveRefundDcsx entity = applyReceiveRefundDcsxDao.findOne(approve.getBizId());
			//1.保存合同回款金额
			Long contractId = entity.getContractId();
			BigDecimal refundAmount = entity.getRefundAmount();
			ApplyCtrDCSX ctrDcsx = applyDcsxService.getEntity(contractId);
			if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDcsx.getBusinessType())) {
				if (ctrDcsx.getSettlementTotalAmount().compareTo(ctrDcsx.getDealedAmount().subtract(entity.getRefundAmount())) !=0) {
					throw new ApplicationException("不能重复退款，请驳回");
				}
			} else {
				if (ctrDcsx.getTotalAmount().compareTo(ctrDcsx.getDealedAmount().subtract(entity.getRefundAmount())) !=0) {
					throw new ApplicationException("不能重复退款，请驳回");
				}
			}
			applyDcsxService.addReceiveRefundAmount(contractId, refundAmount);

			// 适配历史审批中的审批单添加操作记录：过段时间可以去除
			Date createdDate = approve.getCreatedDate();
			if (createdDate != null) {
				String format = DateUtil.format(createdDate, "yyyy-MM-dd");
				if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
					// 添加合同操作记录
					if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDcsx.getBusinessType())) {
						contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_RU,ctrDcsx.getContractStatus(), contractId, approve, entity.getRefundDate() );
					} else {
						contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_RU,ctrDcsx.getContractStatus(), contractId, approve, entity.getRefundDate());
					}
				}
			}

			// 【苏高新】代采赊销收退款，审批完成后，自动充值余额
			String ourCompanyName = entity.getOurCompanyName();
			String companyName = entity.getCompanyName();
			BsCompanyDcsx companyDcsx = companyDcsxService.findByCompanyName(ourCompanyName);
			if (Objects.nonNull(companyDcsx)) {
				Boolean fundFlg = companyDcsx.getFundFlg();
				if (fundFlg &&
						(StringUtils.equals(BasConstants.COMPANY_NAME_QDZG,companyName) || StringUtils.equals(BasConstants.COMPANY_NAME_WSNB,companyName))) {
					fundAmountFlowService.addFundFlow(ourCompanyName, companyName, entity.getRefundAmount(), FundFlowEnum.ReceiveRefund, approve);

				}
			}
		}

	}

	@Override
	@ServerTransactional
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
		ApplyReceiveRefundDcsx entity = applyReceiveRefundDcsxDao.findOne(approve.getBizId());
		rollbackContractApply(entity);
	}

	@Override
	@ServerTransactional
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		ApplyReceiveRefundDcsx entity = applyReceiveRefundDcsxDao.findOne(vo.getBizId());
		rollbackContractApply(entity);


		if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, entity.getStatus())) {
			// 【苏高新】代采赊销收退款，审批完成后，自动充值余额
			String ourCompanyName = entity.getOurCompanyName();
			String companyName = entity.getCompanyName();
			PmApprove approve = pmApproveService.getEntity(entity.getApproveId());
			BsCompanyDcsx companyDcsx = companyDcsxService.findByCompanyName(ourCompanyName);
			if (Objects.nonNull(companyDcsx)) {
				Boolean fundFlg = companyDcsx.getFundFlg();
				if (fundFlg &&
						(StringUtils.equals(BasConstants.COMPANY_NAME_QDZG,companyName) || StringUtils.equals(BasConstants.COMPANY_NAME_WSNB,companyName))) {
					fundAmountFlowService.addFundFlow(ourCompanyName, companyName, entity.getRefundAmount().negate(), FundFlowEnum.ReceiveRefundCancel, approve);

				}
			}
		}
	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		if (pmEntity != null) {
			ApplyReceiveRefundDcsx entity = (ApplyReceiveRefundDcsx) pmEntity;
//			CtrContract ctr = ctrContractService.getEntity(entity.getContractId());
//			ApplyCtrDCSX ctrDCSX = applyDcsxService.getEntity(entity.getContractId());
//			if (Objects.nonNull(ctrDCSX)){
//				SysDeptSdk sysDeptSdk = authOpenFacade.findDeptById(ctrDCSX.getDeptId());
//				entity.setDeptId(ctrDCSX.getDeptId());
//			}
			PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
			if (entity1 != null) {
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
			ApplyReceiveRefundDcsx payRefund = (ApplyReceiveRefundDcsx) pmEntity;
			String contractNo = payRefund.getContractNo();
			String companyName = payRefund.getCompanyName();
			BigDecimal sumNumber = BigDecimal.ZERO;
			sumNumber = payRefund.getRefundAmount();
			String payAmount = NumberUtil.formatNumber(sumNumber, "#.##");
			String companyName1 = RuleUtil.companyNameSubString(companyName);
			String companyName2 = RuleUtil.companyNameSubString(payRefund.getOurCompanyName());
			String company="";
			if(StringUtils.isNotBlank(companyName1)&&StringUtils.isNotBlank(companyName2)){
				company=companyName1+"-"+companyName2;
			}
			String subject = SubjectUtil.formatSubject(contractNo,company,SubjectUtil.formatMoney(sumNumber , RuleUtil.monetaryUnit),"代采赊销收退款");
			return subject;
		}
		return null;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyReceiveRefundDcsxDao.updateFileId(id, fileId);

	}

	@Override
	@ServiceTransactional
	public void autoApplyReceiveRefundDcsx(PmApprove approve,ApplyCtrDCSX ctrDcsx, ApplyPayRefundDcsx payRefund) throws ApplicationException {
		List<ApplyReceiveRefundDcsx> refundReceiveList = applyReceiveRefundDcsxDao.findByContractNo(ctrDcsx.getContractNo());

		List<ApplyReceiveRefundDcsx> refundReceives = refundReceiveList
				.stream().filter(r -> StringUtils.equals(BasConstants.APPROVE_STATUS_A, r.getStatus())
						|| StringUtils.equals(BasConstants.APPROVE_STATUS_D, r.getStatus()))
				.collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(refundReceives)){
			return;
		}
		ApplyReceiveRefundDcsx applyReceiveRefundDcsx = new ApplyReceiveRefundDcsx();
		applyReceiveRefundDcsx.setId(0L);
		applyReceiveRefundDcsx.setContractId(ctrDcsx.getId());
		applyReceiveRefundDcsx.setContractNo(ctrDcsx.getContractNo());
		applyReceiveRefundDcsx.setTotalAmount(ctrDcsx.getTotalAmount());
		applyReceiveRefundDcsx.setPayedAmount(ctrDcsx.getReceiveAmount());
		applyReceiveRefundDcsx.setRefundDate(payRefund.getRefundDate());
		applyReceiveRefundDcsx.setRefundAmount(payRefund.getRefundAmount());
		applyReceiveRefundDcsx.setCompanyName(payRefund.getCompanyName());
		applyReceiveRefundDcsx.setStatus(BasConstants.APPROVE_STATUS_A);
		applyReceiveRefundDcsx.setRemark(payRefund.getRemark());
		applyReceiveRefundDcsx.setEnterpriseId(payRefund.getEnterpriseId());
		applyReceiveRefundDcsx.setOurCompanyName(payRefund.getOurCompanyName());
		String bankName = "";
		String bankAccount = "";
		BsCompanyOur companyOur = companyOurService.findByCompanyName(payRefund.getCompanyName());
		if(Objects.nonNull(companyOur)){
			bankName = companyOur.getCompanyBankName();
			bankAccount = companyOur.getCompanyCardId();
		} else {
			BsCompanyDcsx bsCompanyDcsx = companyDcsxService.findByCompanyName(payRefund.getCompanyName());
			if(Objects.nonNull(bsCompanyDcsx)) {
				bankName = bsCompanyDcsx.getCompanyBankName();
				bankAccount = bsCompanyDcsx.getCompanyCardId();
			}
		}
		applyReceiveRefundDcsx.setBankName(bankName);
		applyReceiveRefundDcsx.setBankAccount(bankAccount);

		String bizEntityJson = JsonUtil.obj2Json(applyReceiveRefundDcsx);
		String processCode = BasConstants.PROCESS_APPLY_RECEIVE_REFUND_DCSX;
		PmApproveSaveVo startVo = new PmApproveSaveVo();

		startVo.setMode(BasConstants.APPROVE_STATUS_A);
		startVo.setStatus(BasConstants.APPROVE_STATUS_D);
		startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		startVo.setAutoStartMessage("自动发起代采赊销付退款申请，原审批编号：" + approve.getApproveNo());
		PmProcessSearchVo searchVo = new PmProcessSearchVo();
		searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		searchVo.setProcessCode(processCode);
		PmProcess process = pmProcessService.findByProcessCode(searchVo);
		if (process == null) {
			throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
		}
		startVo.setUserId(approve.getCreateUserId());
		startVo.setUserName(approve.getCreateUserName());
		startVo.setProcessId(process.getId());
		startVo.setApproveId(0L);
		startVo.setBizEntityJson(bizEntityJson);
		startVo.setAutoStartFlgReal(true);
		pmApproveService.startFlow(startVo);
	}

	private String composeContractNo(String contractNo) {
		List<ApplyReceiveRefundDcsx> receiveRefund = applyReceiveRefundDcsxDao.findByContractNo(contractNo);
		String fmt = String.format("%02d", receiveRefund.size() + 1);
		return contractNo + BasConstants.APPLY_TYPE_U + fmt;
	}

	private void rollbackContractApply(ApplyReceiveRefundDcsx entity) throws ApplicationException {
		//更新CtrContractApply中数据
		if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, entity.getStatus())) {
			Long contractId = entity.getContractId();
			BigDecimal refundAmount = entity.getRefundAmount().negate();
			applyDcsxService.addReceiveRefundAmount(contractId, refundAmount);
		}
	}
}

