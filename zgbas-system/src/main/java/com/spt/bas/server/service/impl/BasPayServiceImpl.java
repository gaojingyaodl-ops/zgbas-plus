package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.hsoft.push.sdk.remote.PushClientHttp;
import com.hsoft.push.sdk.vo.PushRequest;
import com.hsoft.push.sdk.vo.PushTarget;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasPay;
import com.spt.bas.client.vo.BasPayTicketVo;
import com.spt.bas.client.vo.ContractOpVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BasPayDao;
import com.spt.bas.server.service.IBasContractService;
import com.spt.bas.server.service.IBasPayService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("basPayService")
@Transactional(readOnly = true)
public class BasPayServiceImpl extends BaseService<BasPay> implements IBasPayService, IPmService, IPmApproveListener {
	@Autowired
	private BasPayDao basPayDao;
	@Autowired
	private IBasContractService basContractService;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private PushClientHttp pushRemote;

	@Override
	public BaseDao<BasPay> getBaseDao() {
		return basPayDao;
	}

	@Override
	public Class<BasPay> getEntityClazz() {
		return BasPay.class;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {

		basPayDao.updateFileId(id, fileId);
	}

	@Override
	@ServerTransactional
	public BasPay newEntity(BasContract contract) {
		BasPay pay = basPayDao.findByContractId(contract.getId());
		if (pay == null) {
			pay = new BasPay();
			pay.setCompanyId(contract.getOppCompanyId());
			pay.setCompanyName(contract.getOppCompanyName());
			pay.setContractId(contract.getId());
			pay.setContractNo(contract.getContractNo());
			pay.setDealAmount(contract.getDealAmount());
			pay.setDealAmountNotax(contract.getDealAmountNotax());
			pay.setDealNumber(contract.getDealNumber());
			pay.setDealPrice(contract.getDealPrice());
			pay.setFileId(contract.getFileId());
			pay.setNumberUnit(contract.getNumberUnit());
			pay.setProductCode(contract.getProductCode());
			pay.setProductName(contract.getProductName());
			pay.setStatus(BasConstants.APPROVE_STATUS_N);
			pay.setTaxAmount(contract.getTaxAmount());
			pay = basPayDao.save(pay);
		}
		return pay;
	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		if (pmEntity != null) {
			BasPay entity = (BasPay) pmEntity;
			return save(entity);
		}
		return null;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			BasPay entity = (BasPay) pmEntity;
			String payTypeName =DictUtil.getValue(BasConstants.DICT_TYPE_PAYTYPE, entity.getPayType());
			String payAmount =NumberUtil.formatDealNum(entity.getPayAmount());
			String subject = String.format("%s, %s, %s, %s, %s 元 ", entity.getContractNo(),
					entity.getCompanyName(), entity.getProductName(),payTypeName, payAmount);
			return subject;
		}
		return null;
	}

	@Override
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		BasPay pay = basPayDao.findOne(approve.getBizId());
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			// 审批完成，更新合同表已付金额
			basContractService.updatePayAmount(pay.getContractId(), pay.getPayType(), pay.getPayAmount());
			String payType = pay.getPayType();
			if(BasConstants.PAY_TYPE_REMAIN.equals(payType)||BasConstants.PAY_TYPE_ALL.equals(payType)){
				//更新合同状态：已付款
				ContractOpVo opVo =new ContractOpVo();
				opVo.setFondFlg(true);
				opVo.setId(pay.getContractId());
				opVo.setContractStatus(BasConstants.CONTRACTSTATUS_F1);
				opVo.setCreateUserId(approve.getCreateUserId());
				opVo.setCreateUserName(approve.getCreateUserName());
				basContractService.doContractOp(opVo);
			}
		} else if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_A)) {

		} else if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_B)) {

		}
	}

	/**保存发票信息*/
	@ServiceTransactional
	public BasPay saveTicket(BasPayTicketVo vo) {
		BasPay pay = basPayDao.findOne(vo.getId());
		if (pay != null) {
			pay.setInAmount(vo.getInAmount());
			pay.setInAmountNotax(vo.getInAmountNotax());
			pay.setInTaxAmount(vo.getInTaxAmount());
			pay.setInBillNo(vo.getInBillNo());
			pay.setInInvoiceNo(vo.getInInvoiceNo());
			pay.setInInvoiceDate(vo.getInInvoiceDate());
			pay.setRemark(vo.getRemark());
			pay = basPayDao.save(pay);

			//更新合同状态：已收票
			ContractOpVo opVo =new ContractOpVo();
			opVo.setBillFlg(true);
			opVo.setId(pay.getContractId());
			opVo.setContractStatus(BasConstants.CONTRACTSTATUS_V1);
			opVo.setCreateUserId(vo.getCreateUserId());
			opVo.setCreateUserName(vo.getCreateUserName());
			basContractService.doContractOp(opVo);
		}

		return pay;
	}

	/**
	 * 付款通知
	 * @param pay
	 * @param userId
	 */
	public void payNotice(BasPay pay,Long userId) {
		SysUserSdk sysUser = authOpenFacade.findUserById(userId);
		if(sysUser!=null && StringUtils.isNotBlank(sysUser.getEmail())) {
			PushRequest req = new PushRequest();
			req.setBusinessId(pay.getBusinessNo());
			req.setModule("S");
			req.setPushType("basPayNotice");//付款通知
			req.setSubmitUserId("sys");
			List<PushTarget> lst = new ArrayList<>();
			lst.add(new PushTarget(String.valueOf(userId), sysUser.getPhonenumber(), sysUser.getEmail()));
			req.setTargets(lst);
			Map<String, Object> param = new HashMap<>();
			param.put("contractNo", pay.getContractNo());//合同编号
			param.put("payType", pay.getPayType());//付款类型
			param.put("payAmount", pay.getPayAmount());//付款金额
			req.setParam(param);
			try {
				pushRemote.send(req);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//审批撤回
	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		BasPay entity = this.getBaseDao().findOne(vo.getBizId());
		if(BasConstants.APPROVE_STATUS_D.equals(entity.getStatus())){
			entity.setStatus(BasConstants.APPROVE_STATUS_N);
		}
		this.save(entity);
	}
}
