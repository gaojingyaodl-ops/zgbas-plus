package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.OwnRegionEnum;
import com.spt.bas.client.entity.ApplyPayRefund;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.ApplyPayRefundDao;
import com.spt.bas.server.enums.FundFlowEnum;
import com.spt.bas.server.service.*;
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

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Component("applyPayRefundService")
@Transactional(readOnly = true)
public class ApplyPayRefundServiceImpl extends BaseService<ApplyPayRefund> implements IApplyPayRefundService ,IPmService, IPmApproveListener{
	@Autowired
	private ApplyPayRefundDao applyPayRefundDao;
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
	private IBsCompanyDcsxService companyDcsxService;
	@Resource
	private IFundAmountFlowService fundAmountFlowService;

	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@Override
	public BaseDao<ApplyPayRefund> getBaseDao() {
		return applyPayRefundDao;
	}

	@Override
	public Class<ApplyPayRefund> getEntityClazz() {
		return ApplyPayRefund.class;
	}


	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		ApplyPayRefund entity = applyPayRefundDao.findOne(approve.getBizId());
		Long contractId = entity.getContractId();
		//2.添加合同操作记录
		contractOphisService.addHis(BasConstants.APPLY_TYPE_U, contractId, approve,entity.getRefundDate());
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyPayRefund entity = applyPayRefundDao.findOne(approve.getBizId());
			//1.保存合同回款金额
			Long contractId = entity.getContractId();
			BigDecimal refundAmount = entity.getRefundAmount();
			CtrContract contract = ctrContractService.getEntity(contractId);
			if (contract.getTotalAmount().compareTo(contract.getDealedAmount().subtract(entity.getRefundAmount())) !=0) {
				throw new ApplicationException("不能重复退款，请驳回");
			}
			ctrContractUpdateService.addrefundAmount(contractId, refundAmount, approve.getApproveNo(),null);

			// 适配历史审批中的审批单添加操作记录：过段时间可以去除
			Date createdDate = approve.getCreatedDate();
			if (createdDate != null) {
				String format = DateUtil.format(createdDate, "yyyy-MM-dd");
				if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
					// 添加合同操作记录
					contractOphisService.addHis(BasConstants.APPLY_TYPE_U, contractId, approve,entity.getRefundDate());
				}
			}

			// 【苏高新】代采赊销收退款，审批完成后，自动充值余额
			String virtualType = contract.getVirtualType();
			String ourCompanyName = entity.getOurCompanyName();
			String companyName = entity.getCompanyName();
			BsCompanyDcsx companyDcsx = companyDcsxService.findByCompanyName(ourCompanyName);
			if (Objects.nonNull(companyDcsx)) {
				Boolean fundFlg = companyDcsx.getFundFlg();
				// 开通了余额且是库存采购且是青岛中光或网塑宁波
				if (fundFlg && StringUtils.equals("KC", virtualType) &&
						(StringUtils.equals(BasConstants.COMPANY_NAME_QDZG,companyName) || StringUtils.equals(BasConstants.COMPANY_NAME_WSNB,companyName))) {
					fundAmountFlowService.addFundFlow(ourCompanyName, companyName, entity.getRefundAmount(), FundFlowEnum.VirtualRefund, approve);
				}
			}
		}
	}

	@Override
	@ServerTransactional
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

		ApplyPayRefund entity = applyPayRefundDao.findOne(vo.getBizId());
		CtrContract contract = ctrContractService.getEntity(entity.getContractId());
		String virtualType = contract.getVirtualType();
		if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, entity.getStatus())) {
			// 【苏高新】代采赊销收退款，审批完成后，自动充值余额
			String ourCompanyName = entity.getOurCompanyName();
			String companyName = entity.getCompanyName();
			PmApprove approve = pmApproveService.getEntity(entity.getApproveId());
			BsCompanyDcsx companyDcsx = companyDcsxService.findByCompanyName(ourCompanyName);
			if (Objects.nonNull(companyDcsx)) {
				Boolean fundFlg = companyDcsx.getFundFlg();
				if (fundFlg && StringUtils.equals("KC", virtualType) &&
						(StringUtils.equals(BasConstants.COMPANY_NAME_QDZG,companyName) || StringUtils.equals(BasConstants.COMPANY_NAME_WSNB,companyName))) {
					fundAmountFlowService.addFundFlow(ourCompanyName, companyName, entity.getRefundAmount().negate(), FundFlowEnum.VirtualRefundCancel, approve);
				}
			}
		}
	}

	@Override
	@ServerTransactional
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
		ApplyPayRefund entity = applyPayRefundDao.findOne(approve.getBizId());
		rollbackContractApply(entity);
	}

	@Override
	public Map<String, Object> buildConditionDefaultMap(IPmEntity pmEntity) {
		try {
			ApplyPayRefund entity = (ApplyPayRefund) pmEntity;
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
			ApplyPayRefund entity = (ApplyPayRefund) pmEntity;
			CtrContract contract = ctrContractService.getEntity(entity.getContractId());
			if (Objects.nonNull(contract)){
				SysDeptSdk sysDeptSdk = authOpenFacade.findDeptById(contract.getDeptId());
				entity.setDeptId(contract.getDeptId());
				entity.setOwnRegion(Objects.nonNull(sysDeptSdk) && Objects.nonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName()))
						? Objects.requireNonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName())).getRegionCode()
						: "");
			}
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
			ApplyPayRefund payRefund = (ApplyPayRefund) pmEntity;
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
			String subject = SubjectUtil.formatSubject(contractNo,company,SubjectUtil.formatMoney(sumNumber , RuleUtil.monetaryUnit),"采购退款");
			return subject;

		}
		return null;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyPayRefundDao.updateFileId(id, fileId);
	}

	private String composeContractNo(String contractNo) {
		List<ApplyPayRefund> payRefund = applyPayRefundDao.findByContractNo(contractNo);
		String fmt = String.format("%02d", payRefund.size() + 1);
		return contractNo + BasConstants.APPLY_TYPE_U + fmt;
	}

	private void rollbackContractApply(ApplyPayRefund entity) throws ApplicationException {
		//更新CtrContractApply中数据
		CtrContractApplyVo vo = new CtrContractApplyVo();
		vo.setContractId(entity.getContractId());
		vo.setDealAmount(entity.getRefundAmount().negate());
		vo.setApplyType(BasConstants.APPLY_TYPE_U);
		contractApplyService.updateCtrContractApply(vo);
	}

}

