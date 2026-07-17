package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.OwnRegionEnum;
import com.spt.bas.client.entity.ApplyReceiveRefund;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.ApplyReceiveRefundDao;
import com.spt.bas.server.service.IApplyReceiveRefundService;
import com.spt.bas.server.service.ICtrContractApplyService;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


/**
 * 销售 退款
 */
@Component("applyReceiveRefundService")
@Transactional(readOnly = true)
public class ApplyReceiveRefundServiceImpl extends BaseService<ApplyReceiveRefund> implements IApplyReceiveRefundService ,IPmService, IPmApproveListener{
	@Autowired
	private ApplyReceiveRefundDao applyReceiveRefundDao;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private ICtrContractApplyService contractApplyService;
	@Autowired
	private ICtrContractUpdateService ctrContractUpdateService;
	@Autowired
	private ICtrContractOphisService contractOphisService;

	@Autowired
	private IPmApproveService pmApproveService;

	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@Override
	public BaseDao<ApplyReceiveRefund> getBaseDao() {
		return applyReceiveRefundDao;
	}

	@Override
	public Class<ApplyReceiveRefund> getEntityClazz() {
		return ApplyReceiveRefund.class;
	}

	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		ApplyReceiveRefund entity = applyReceiveRefundDao.findOne(approve.getBizId());
		Long contractId = entity.getContractId();
		//2.添加合同操作记录
		contractOphisService.addHis(BasConstants.APPLY_TYPE_U, contractId, approve,entity.getRefundDate());
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyReceiveRefund entity = applyReceiveRefundDao.findOne(approve.getBizId());
			//1.保存合同回款金额
			Long contractId = entity.getContractId();
			BigDecimal refundAmount = entity.getRefundAmount();
			CtrContract contract = ctrContractService.getEntity(contractId);
			if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,contract.getBusinessType())) {
				if ( (contract.getTotalAmount().add(contract.getTpInterest()).subtract(contract.getApproveTpInterest())).compareTo(contract.getDealedAmount().subtract(entity.getRefundAmount())) !=0) {
					throw new ApplicationException("不能重复退款，请驳回");
				}
			} else {
				if (contract.getTotalAmount().compareTo(contract.getDealedAmount().subtract(entity.getRefundAmount())) !=0) {
					throw new ApplicationException("不能重复退款，请驳回");
				}
			}
			ctrContractUpdateService.addrefundAmount(contractId, refundAmount, approve.getApproveNo(),entity.getRefundType());
			// 适配历史审批中的审批单添加操作记录：过段时间可以去除
			Date createdDate = approve.getCreatedDate();
			if (createdDate != null) {
				String format = DateUtil.format(createdDate, "yyyy-MM-dd");
				if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
					//2.添加合同操作记录
					contractOphisService.addHis(BasConstants.APPLY_TYPE_U, contractId, approve,entity.getRefundDate());
				}
			}

		}

	}

	@Override
	@ServerTransactional
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
		ApplyReceiveRefund entity = applyReceiveRefundDao.findOne(approve.getBizId());
		rollbackContractApply(entity);
	}

	@Override
	@ServerTransactional
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

	}

	@Override
	public Map<String, Object> buildConditionDefaultMap(IPmEntity pmEntity) {
		try {
			ApplyReceiveRefund entity = (ApplyReceiveRefund) pmEntity;
			CtrContract ctrContract = ctrContractService.findByContractNo(entity.getContractNo());
			return BasBusinessUtil.buildConditionDefaultMap(ctrContract);
		} catch (Exception e) {
			logger.error("buildConditionDefaultMap error", e);
		}
		return new HashMap<>();
	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		if (pmEntity != null) {
			ApplyReceiveRefund entity = (ApplyReceiveRefund) pmEntity;
			CtrContract ctr = ctrContractService.getEntity(entity.getContractId());
			if (Objects.nonNull(ctr)){
				SysDeptSdk sysDeptSdk = authOpenFacade.findDeptById(ctr.getDeptId());
				entity.setDeptId(ctr.getDeptId());
				entity.setOwnRegion(Objects.nonNull(sysDeptSdk) && Objects.nonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName()))
						? Objects.requireNonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName())).getRegionCode()
						: "");
			}
			PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
			if (entity1 != null) {
				SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
				entity.setDeptId(deptByUserId.getDeptId());
			}
			BigDecimal interestAmount = ctr.getInterestAmount();
			interestAmount = interestAmount == null ? BigDecimal.ZERO : interestAmount;
			BigDecimal unPayedAmount = ctr.getTotalAmount().subtract(ctr.findRealDealedAmount()).add(interestAmount);
			entity.setUnpayedAmount(unPayedAmount);
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
			ApplyReceiveRefund payRefund = (ApplyReceiveRefund) pmEntity;
			String contractNo = payRefund.getContractNo();
			String companyName = payRefund.getCompanyName();
			BigDecimal sumNumber = BigDecimal.ZERO;
			sumNumber = payRefund.getRefundAmount();
			String payAmount = NumberUtil.formatNumber(sumNumber, "#.##");
			String refundType = payRefund.getRefundType();
			String companyName1 = RuleUtil.companyNameSubString(companyName);
			String companyName2 = RuleUtil.companyNameSubString(payRefund.getOurCompanyName());
			String company="";
			if(StringUtils.isNotBlank(companyName1)&&StringUtils.isNotBlank(companyName2)){
				company=companyName1+"-"+companyName2;
			}
			String subject = SubjectUtil.formatSubject(contractNo,company,SubjectUtil.formatMoney(sumNumber , RuleUtil.monetaryUnit),"销售退款");
			return subject;
		}
		return null;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyReceiveRefundDao.updateFileId(id, fileId);
	}

	private String composeContractNo(String contractNo) {
		List<ApplyReceiveRefund> receiveRefund = applyReceiveRefundDao.findByContractNo(contractNo);
		String fmt = String.format("%02d", receiveRefund.size() + 1);
		return contractNo + BasConstants.APPLY_TYPE_U + fmt;
	}

	private void rollbackContractApply(ApplyReceiveRefund entity) throws ApplicationException {
		//更新CtrContractApply中数据
		CtrContractApplyVo vo = new CtrContractApplyVo();
		vo.setContractId(entity.getContractId());
		vo.setDealAmount(entity.getRefundAmount().negate());
		vo.setApplyType(BasConstants.APPLY_TYPE_U);
		contractApplyService.updateCtrContractApply(vo);
	}
}

