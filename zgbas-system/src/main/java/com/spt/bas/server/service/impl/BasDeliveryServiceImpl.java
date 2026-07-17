package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasDelivery;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BasDeliveryDao;
import com.spt.bas.server.service.IBasDeliveryService;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("basDeliveryService")
@Transactional(readOnly = true)
public class BasDeliveryServiceImpl extends BaseService<BasDelivery> implements IBasDeliveryService , IPmService {
	@Autowired
	private BasDeliveryDao basDeliveryDao;
	
	@Override
	public BaseDao<BasDelivery> getBaseDao() {
		return basDeliveryDao;
	}
	
	@Override
	public Class<BasDelivery> getEntityClazz() {
		return BasDelivery.class;
	}
	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {

		basDeliveryDao.updateFileId(id, fileId);
	}

	
	@Override
	@ServerTransactional
	public BasDelivery newEntity(BasContract contract) {
		BasDelivery  invoice =basDeliveryDao.findByContractId(contract.getId());
		if (invoice==null) {
			invoice=new BasDelivery();
			invoice.setCompanyId(contract.getOppCompanyId());
			invoice.setCompanyName(contract.getOppCompanyName());
			invoice.setContractId(contract.getId());
			invoice.setDealAmount(contract.getDealAmount());
			invoice.setDealNumber(contract.getDealNumber());
			invoice.setDealPrice(contract.getDealPrice());
			invoice.setFileId(contract.getFileId());
			invoice.setNumberUnit(contract.getNumberUnit());
			invoice.setProductCode(contract.getProductCode());
			invoice.setProductName(contract.getProductName());
			invoice.setStatus(BasConstants.APPROVE_STATUS_N);
			invoice.setDeliveryMode(contract.getDeliveryMode());
			invoice = basDeliveryDao.save(invoice);
		}
		return invoice;
	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		if (pmEntity != null) {
			BasDelivery entity = (BasDelivery) pmEntity;
			return save(entity);
		}
		return null;
	}
	
	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			BasDelivery entity = (BasDelivery) pmEntity;
			String subject = String.format("%s, %s, %s, %s ", entity.getContractNo(),
					entity.getCompanyName(), entity.getProductName(), entity.getDeliveryNo());
			return subject;
		}
		return null;
	}
}

