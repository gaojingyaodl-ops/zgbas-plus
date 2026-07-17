package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasInvoice;
import com.spt.bas.client.entity.BasReceive;
import com.spt.bas.client.vo.ContractOpVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BasInvoiceDao;
import com.spt.bas.server.dao.BasReceiveDao;
import com.spt.bas.server.service.IBasContractService;
import com.spt.bas.server.service.IBasInvoiceService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("basInvoiceService")
@Transactional(readOnly = true)
public class BasInvoiceServiceImpl extends BaseService<BasInvoice> implements IBasInvoiceService, IPmService, IPmApproveListener  {
	@Autowired
	private BasInvoiceDao basInvoiceDao;
	@Autowired
	private IBasContractService basContractService;
	@Autowired
	private BasReceiveDao basReceiveDao;
	
	@Override
	public BaseDao<BasInvoice> getBaseDao() {
		return basInvoiceDao;
	}
	
	@Override
	public Class<BasInvoice> getEntityClazz() {
		return BasInvoice.class;
	}
	
	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		
		basInvoiceDao.updateFileId(id, fileId);
	}
	
	@Override
	@ServerTransactional
	public BasInvoice newEntity(BasContract contract) {
		BasInvoice  invoice =basInvoiceDao.findByContractId(contract.getId());
		if (invoice==null) {
			invoice=new BasInvoice();
			invoice.setCompanyId(contract.getOppCompanyId());
			invoice.setCompanyName(contract.getOppCompanyName());
			invoice.setContractId(contract.getId());
			invoice.setContractNo(contract.getContractNo());
			invoice.setDealAmount(contract.getDealAmount());
			invoice.setDealAmountNotax(contract.getDealAmountNotax());
			invoice.setDealNumber(contract.getDealNumber());
			invoice.setDealPrice(contract.getDealPrice());
			invoice.setFileId(contract.getFileId());
			invoice.setNumberUnit(contract.getNumberUnit());
			invoice.setProductCode(contract.getProductCode());
			invoice.setProductName(contract.getProductName());
			invoice.setStatus(BasConstants.APPROVE_STATUS_N);
			invoice.setTaxAmount(contract.getTaxAmount());
			invoice = basInvoiceDao.save(invoice);
		}
		return invoice;
	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity)throws ApplicationException  {
		if (pmEntity != null) {
			BasInvoice entity = (BasInvoice) pmEntity;
			return save(entity);
		}
		return null;
	}
	
	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			BasInvoice entity = (BasInvoice) pmEntity;
			String subject = String.format("%s, %s, %s, 开票金额: %s 元 ", entity.getContractNo(),
					entity.getCompanyName(), entity.getProductName(), entity.getDealAmount());
			return subject;
		}
		return null;
	}

	@Override
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		BasInvoice pay = basInvoiceDao.findOne(approve.getBizId());
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			//更新合同状态：已开票
			ContractOpVo opVo =new ContractOpVo();
			opVo.setBillFlg(true);
			opVo.setId(pay.getContractId());
			opVo.setContractStatus(BasConstants.CONTRACTSTATUS_V2);
			opVo.setCreateUserId(approve.getCreateUserId());
			opVo.setCreateUserName(approve.getCreateUserName());
			basContractService.doContractOp(opVo);
			//保存付款信息
			List<BasReceive> receiveList = basReceiveDao.findByContractId(pay.getContractId());
			for (BasReceive basReceive : receiveList) {
				basReceive.setInInvoiceNo(pay.getInvoiceNo());
				basReceive.setInInvoiceDate(pay.getInvoiceDate());
				basReceive.setInBillNo(pay.getReceiveBillNo());
				basReceiveDao.save(basReceive);
			}
		} else if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_A)) {

		} else if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_B)) {

		}
		
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		// TODO Auto-generated method stub
		BasInvoice entity = this.getBaseDao().findOne(vo.getBizId());
		if(BasConstants.APPROVE_STATUS_D.equals(entity.getStatus())){
			entity.setStatus(BasConstants.APPROVE_STATUS_N);
		}
		this.save(entity);
	}
}

