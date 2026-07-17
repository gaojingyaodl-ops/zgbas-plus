package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ApproveDealRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.IApproveDealService;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.dao.PmProcessNodeDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmProcess;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 待办事项
 * @author wlddh
 *
 */
@Component
@Transactional(readOnly = true)
public class ApproveDealServiceImpl extends BaseService<ApproveDeal> implements IApproveDealService {
	@Autowired
	private ApproveDealDao approveDealDao;
	@Autowired
	private PmProcessDao pmProcessDao;
	@Autowired
	private CtrContractDao contractDao;
	@Autowired
	private ApplyPayDao payDao;
	@Autowired
	private ApplyReceiveDao receiveDao;
	@Autowired
	private ApplyDeliveryInDao deliveryInDao;
	@Autowired
	private ApplyDeliveryOutDao deliveryOutDao;
	@Autowired
	private PmProcessNodeDao processNodeDao;

	@Autowired
	private ApplyInvoiceDao applyInvoiceDao;

	private static Map<String, String> mapDealType =new HashMap<>();
	static {
		mapDealType.put(BasConstants.APPLY_TYPE_N, "CTR_INVOICE");//开票
		mapDealType.put(BasConstants.APPLY_TYPE_V, "APPLY_INRECEIVED");//收票
		mapDealType.put(BasConstants.APPLY_TYPE_P, "APPLY_PAY");//付款
		mapDealType.put(BasConstants.APPLY_TYPE_E, "APPLY_RECEIVE");//收款
		mapDealType.put(BasConstants.APPLY_TYPE_I, "APPLY_DELIVERYIN");//入库
		mapDealType.put(BasConstants.APPLY_TYPE_O, "APPLY_DELIVERYOUT");//出库
	}

	@Override
	public BaseDao<ApproveDeal> getBaseDao() {
		return approveDealDao;
	}

	@Override
	public Class<ApproveDeal> getEntityClazz() {
		return ApproveDeal.class;
	}

	@Override
	@Transactional
	public void addApproveDeal(PmApprove approve) {
		String applyStatus = approve.getStatus();
		Long processId = approve.getProcessId();
		PmProcess process = pmProcessDao.findOne(processId);
		Long approveId = approve.getId();
		String processCode = process.getProcessCode();
		String applyType = DictUtil.getValue(BasConstants.DEAL_TYPE, processCode);
		// 当审批状态为已完成 或者 驳回的时候 删掉该事项
		if (BasConstants.APPROVE_STATUS_N.equals(applyStatus) || BasConstants.APPROVE_STATUS_B.equals(applyStatus)) {
			approveDealDao.deleteByRelationId(approveId);

		} else if (BasConstants.APPROVE_STATUS_D.equals(applyStatus)) { // 当审批已完成时需要生成 收 /付款，出/入库待处理事项的通知
			// 去掉已完成的申请单事项
			approveDealDao.deleteByRelationId(approveId);
			addAfter(approve, applyType);
		}else if (BasConstants.APPROVE_STATUS_C.equals(applyStatus)) {
			//申请单作废
			approveDealDao.deleteByRelationId(approveId);
			rollback(approve, applyType);

		} else {
			ApproveDeal deal = new ApproveDeal();
			ApproveDeal now = approveDealDao.findByRelationId(approveId);
			if (now != null) {
				deal = now;
			}
			deal.setEnterpriseId(approve.getEnterpriseId()); // 企业账套ID
			deal.setRelationId(approveId); // 关联ID，审批单id
			deal.setRelaUserId(approve.getCurrApproverUserId()); // 责任人ID
			deal.setProcessCode(processCode); // 流程code
			deal.setDealType(applyType); // 申请单类型
			// 根据审核类型 得到name
			String dealTypeName = DictUtil.getValue(BasConstants.APPLY_TYPE, applyType);
			String subject = dealTypeName + "申请待处理:" + approve.getSubject();
			deal.setSubject(subject); // 摘要
			deal.setCreatedUserId(approve.getCreateUserId()); // 创建人用户id
			approveDealDao.save(deal);
		}
	}


	private void addAfter(PmApprove approve,String applyType) {

		if (BasConstants.APPLY_TYPE_M.equals(applyType) || BasConstants.APPLY_TYPE_R.equals(applyType)
				|| BasConstants.APPLY_TYPE_S.equals(applyType) || BasConstants.APPLY_TYPE_B.equals(applyType)) {

			List<CtrContract> contractList = contractDao.findByApproveId(approve.getId());
			for (CtrContract contract : contractList) {
				// 已作废的合同不在生成待办事项
				if (contract.getStatus().equals(BasConstants.APPROVE_STATUS_C)) {
					continue;
				}
				ApproveDeal deal = new ApproveDeal();
				ApproveDeal deal2 = new ApproveDeal();
				deal.setEnterpriseId(approve.getEnterpriseId()); // 企业账套ID
				deal.setRelaUserId(String.valueOf(approve.getCreateUserId()) + "|"); // 责任人ID
				deal.setRelationId(new Long(0)); // 关联ID默认为零
				BeanUtils.copyProperties(deal, deal2);
				String dealType = null;
				String dealType2 = null;
				if (BasConstants.APPLY_TYPE_MB.equals(contract.getSource())) {
					dealType = BasConstants.APPLY_TYPE_P; // 待付款申请
				} else if (BasConstants.APPLY_TYPE_MS.equals(contract.getSource())) {
					dealType = BasConstants.APPLY_TYPE_E; // 待收款申请
					dealType2=BasConstants.APPLY_TYPE_O; //出库申请

				}else if (BasConstants.APPLY_TYPE_S.equals(contract.getSource())) {
					dealType = BasConstants.APPLY_TYPE_E; // 待收款申请
//					code = "APPLY_RECEIVE";
					// 判断对应的采购合同是否已入库，
					dealType2 = BasConstants.APPLY_TYPE_O; // 出库申请
//					code2 = "APPLY_DELIVERYOUT";
				} else if (BasConstants.APPLY_TYPE_B.equals(contract.getSource()) || BasConstants.APPLY_TYPE_A.equals(contract.getSource())) {
					dealType = BasConstants.APPLY_TYPE_P; // 待付款申请
//					code = "APPLY_PAY";
					dealType2 = BasConstants.APPLY_TYPE_I; // 入库申请
//					code2 = "APPLY_DELIVERYIN";
				}
				deal.setDealType(dealType);
				deal.setSubject(getSubjectContent(dealType, contract.getContractNo(), contract.getTotalAmount(),
						contract.getTotalNumber()));
				deal.setProcessCode(mapDealType.get(dealType));
				// 将合同编号存入备注中
				deal.setRemark(String.valueOf(contract.getId()));

				approveDealDao.save(deal);
				if (dealType2 != null) {
					deal2.setEnterpriseId(approve.getEnterpriseId());
					deal2.setDealType(dealType2);
					deal2.setSubject(getSubjectContent(dealType2, contract.getContractNo(),
							contract.getTotalAmount(), contract.getTotalNumber()));
					deal2.setProcessCode(mapDealType.get(dealType2));
					deal2.setRemark(String.valueOf(contract.getId()));
					approveDealDao.save(deal2);

				}

//				add(approve, applyType);
			}
		}else if (BasConstants.APPLY_TYPE_P.equals(applyType) || BasConstants.APPLY_TYPE_E.equals(applyType)) {
			// 收款、付款
			ApproveDeal deal = new ApproveDeal();
			deal.setEnterpriseId(approve.getEnterpriseId()); // 企业账套ID
			deal.setRelaUserId(String.valueOf(approve.getCreateUserId()) + "|"); // 责任人ID
			deal.setRelationId(new Long(0)); // 关联ID默认为零
			String dealType = null;
			Long contractId =0L;

			BigDecimal price = BigDecimal.ZERO;
			if (BasConstants.APPLY_TYPE_P.equals(applyType)) {
				dealType = BasConstants.APPLY_TYPE_V; // 收票
	//			code = "APPLY_INRECEIVED";
				//根据付款信息收票
				ApplyPay pay = payDao.findOne(approve.getBizId());
				// 获得合同编号
				contractId = pay.getContractId();
				price = pay.getPayAmount();
			} else {
				dealType = BasConstants.APPLY_TYPE_N; // 开票
	//			code = "CTR_INVOICE";
				// 根据收款信息开票
				ApplyReceive receive = receiveDao.findOne(approve.getBizId());
				// 获得合同编号
				contractId = receive.getContractId();
				price = receive.getReceiveAmount();
			}
			String processCode = mapDealType.get(dealType);
			CtrContract ctr = contractDao.findOne(contractId);
			String subject = getSubjectContent(dealType, ctr.getContractNo(), price, null);
			deal.setSubject(subject);
			deal.setProcessCode(processCode);
			deal.setDealType(dealType);
			deal.setRelationId(new Long(0));
			deal.setRelaUserId(approve.getCreateUserId() + "|");
			deal.setRemark(String.valueOf(ctr.getId()));
			approveDealDao.save(deal);
		}
	}
	private void rollback(PmApprove approve,String applyType) {

		if (BasConstants.APPLY_TYPE_P.equals(applyType) || BasConstants.APPLY_TYPE_E.equals(applyType)
			   || BasConstants.APPLY_TYPE_I.equals(applyType) || BasConstants.APPLY_TYPE_O.equals(applyType)) {
			// 收款、付款、入库、出库
			String dealType = applyType;
			Long contractId = 0L;
			BigDecimal amount = BigDecimal.ZERO;
			BigDecimal totalNumber = BigDecimal.ZERO;
			if (BasConstants.APPLY_TYPE_P.equals(applyType)) {
				//根据付款信息收票
				ApplyPay pay = payDao.findOne(approve.getBizId());
				// 获得合同编号
				contractId = pay.getContractId();
				amount = pay.getPayAmount();
			} else if (BasConstants.APPLY_TYPE_P.equals(applyType)) {
				// 根据收款信息开票
				ApplyReceive receive = receiveDao.findOne(approve.getBizId());
				// 获得合同编号
				contractId = receive.getContractId();
				amount = receive.getReceiveAmount();
			}else if (BasConstants.APPLY_TYPE_I.equals(applyType)) {
				ApplyDeliveryIn deliveryIn =  deliveryInDao.findOne(approve.getBizId());
				contractId = deliveryIn.getContractId();
			}else if (BasConstants.APPLY_TYPE_O.equals(applyType)) {
				ApplyDeliveryOut deliveryOut =  deliveryOutDao.findOne(approve.getBizId());
				contractId = deliveryOut.getContractId();
			}
			String processCode = mapDealType.get(dealType);
			CtrContract ctr = contractDao.findOne(contractId);
			totalNumber = ctr.getTotalNumber();
			String subject = getSubjectContent(dealType, ctr.getContractNo(), amount, totalNumber);

			ApproveDeal deal = new ApproveDeal();
			deal.setEnterpriseId(approve.getEnterpriseId()); // 企业账套ID
			deal.setRelaUserId(String.valueOf(approve.getCreateUserId()) + "|"); // 责任人ID
			deal.setRelationId(0L); // 关联ID默认为零
			deal.setSubject(subject);
			deal.setProcessCode(processCode);
			deal.setDealType(dealType);
			deal.setRelationId(0L);
			deal.setRelaUserId(approve.getCreateUserId() + "|");
			deal.setRemark(String.valueOf(ctr.getId()));
			approveDealDao.save(deal);
		}
	}
	/***
	 * 删除通知事项 目前删除该用户的发起申请通知
	 */
	@Override
	public void removeApproveDeal(Long processId, String contractId) {
		PmProcess process = pmProcessDao.findOne(processId);
		String processCode = process.getProcessCode();
		approveDealDao.deleteByDealType(processCode, contractId);

	}
	@Override
	public void removeApproveDeal(Long contractId) {
		approveDealDao.deleteByRemark(String.valueOf(contractId));
	}

	/***
	 * 修改摘要
	 */
	@Override
	public void updateSubject(ApproveDealRequest request) {
		PmProcess process = pmProcessDao.findOne(request.getProcessId());
		String processCode = process.getProcessCode();
		CtrContract ctr = contractDao.findOne(request.getContractId());
		String subject = getSubjectContent(request.getDealType(), ctr.getContractNo(), request.getTotalAmount(),
				request.getTotalNumber());
		approveDealDao.updateSubject(String.valueOf(ctr.getId()), processCode, subject);
	}

	/***
	 * 获得摘要内容
	 *
	 * @param dealType
	 * @param contract
	 * @return
	 */
	private String getSubjectContent(String dealType, String contractNo, BigDecimal totalAmount,
			BigDecimal totalNumber) {
		// 根据审核类型 得到name
		String dealTypeName = DictUtil.getValue(BasConstants.APPLY_TYPE, dealType);

		// String contractId=contract.getContractNo();
		// BigDecimal price=contract.getBondAmount();
		// BigDecimal totalNumber=contract.getTotalNumber();
		// BigDecimal totalAmount=contract.getTotalAmount();
		StringBuffer subject = new StringBuffer("");
		if (dealType.equals(BasConstants.APPLY_TYPE_P)) { // 付款
			// 获得摘要
			subject.append("待" + dealTypeName + ":");
			subject.append("合同编号:" + contractNo + ",");
			// subject.append("品名:"+productName);
			subject.append("付款金额:" + totalAmount + " 元");
		} else if (dealType.equals(BasConstants.APPLY_TYPE_E)) { // 收款
			// 获得摘要
			subject.append("待" + dealTypeName + ":");
			subject.append("合同编号:" + contractNo + ",");
			// subject.append("品名:"+productName);
			subject.append("收款金额:" + totalAmount + " 元");
		} else if (dealType.equals(BasConstants.APPLY_TYPE_I)) { // 待入库
			// 获得摘要
			subject.append("待" + dealTypeName + ":");
			subject.append("合同编号:" + contractNo + ",");
			// subject.append("品名:"+productName);
			subject.append("入库数量:" + totalNumber + " 吨");
		} else if (dealType.equals(BasConstants.APPLY_TYPE_O)) { // 出库
			// 获得摘要
			subject.append("待" + dealTypeName + ":");
			subject.append("合同编号:" + contractNo + ",");
			subject.append("出库数量:" + totalNumber + " 吨");
		} else if (BasConstants.APPLY_TYPE_V.equals(dealType)) { // 收票
			// 获得摘要
			subject.append("待" + dealTypeName + ":");
			subject.append("合同编号:" + contractNo + ",");
			subject.append("收票金额:" + totalAmount + " 元");
		} else if (BasConstants.APPLY_TYPE_N.equals(dealType)) { // 开票
			// 获得摘要
			subject.append("待" + dealTypeName + ":");
			List<ApplyInvoice> applyInvoiceList = applyInvoiceDao.findByContractNo(contractNo);
			String billMark = applyInvoiceList.get(applyInvoiceList.size() - 1).getBillMark();
			if(billMark.equals(BasConstants.Special_Invoice_Mark)){
				subject.append("["+"特殊"+"]");
			}
			subject.append("合同编号:" + contractNo + ",");
			subject.append("开票金额:" + totalAmount + " 元");
		}

		return subject.toString();
	}

	/**
	 * 提前两天到期收付款提醒（待办事项）
	 * */
	@Override
	@ServerTransactional
	public void dueToRemind(List<CtrContract> repaymentList){
		if(!repaymentList.isEmpty()){
			Long enterpriseId = repaymentList.get(0).getEnterpriseId();
			//查询业务助理Id
			String bizUserId = processNodeDao.findByNodeCode(BasConstants.BIZ_ADMIN,enterpriseId);

			for (CtrContract contract : repaymentList) {
				String applyType = contract.getContractType().equals(BasConstants.CONTRACT_TYPE_B) ? BasConstants.APPLY_TYPE_P :BasConstants.APPLY_TYPE_E;
				BigDecimal totalAmount = contract.getTotalAmount().subtract(contract.findRealDealedAmount());
				String subject = getSubjectContent(applyType,contract.getContractNo(),totalAmount,BigDecimal.ZERO);

				ApproveDeal deal = new ApproveDeal();
				deal.setEnterpriseId(contract.getEnterpriseId()); // 企业账套ID
				deal.setRelaUserId(String.valueOf(contract.getMatchUserId())+"|"+bizUserId); // 责任人ID
				deal.setRelationId(new Long(0)); // 关联ID默认为零
				deal.setDealType(applyType);
				deal.setProcessCode(mapDealType.get(applyType));
				deal.setSubject(subject);
				deal.setRemark(String.valueOf(contract.getId()));

				approveDealDao.save(deal);
			}
		}
	}
}
