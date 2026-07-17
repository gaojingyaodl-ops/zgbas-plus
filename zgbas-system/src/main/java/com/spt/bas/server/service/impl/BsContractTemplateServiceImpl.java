package com.spt.bas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsContractTemplate;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsContractTemplateDao;
import com.spt.bas.server.service.IBsContractTemplateService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class BsContractTemplateServiceImpl extends BaseService<BsContractTemplate> implements IBsContractTemplateService {
	@Autowired
	private BsContractTemplateDao contractTemplateDao;
	@Override
	public BaseDao<BsContractTemplate> getBaseDao() {
		return contractTemplateDao;
	}
	// 更新附件ID
	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		contractTemplateDao.updateFileId(id, fileId);
	}
	@Override
	public BsContractTemplate findByTemplateTagAndEnterpriseId(BsContractTemplate template) {
		BsContractTemplate temp = contractTemplateDao.findByTemplateTagAndEnterpriseId(template.getTemplateTag(),template.getEnterpriseId());
		return temp;
	}

	@Override
	public List<BsContractTemplate> findByContractTypeAndEnterpriseId(BsContractTemplate template) {
		List<BsContractTemplate> templateList = contractTemplateDao.findByContractTypeAndEnterpriseId(template.getContractType(), template.getEnterpriseId());
		if (!Boolean.TRUE.equals(template.getWithContentFlag())){
			templateList.forEach(t-> t.setContent(null));
		}
		return templateList;
	}

	@Override
	public List<BsContractTemplate> findByContractsell(BsContractTemplate template) {
		List<BsContractTemplate> templateList = contractTemplateDao.findByContractsell(template.getContractType(), template.getEnterpriseId());
		templateList.forEach(t->{
			t.setContent(null);
		});
		return templateList;
	}

	@Override
	public BsContractTemplate findByIdAndEnterpriseId(BsContractTemplate template) {
		return contractTemplateDao.findByIdAndEnterpriseId(template.getId(), template.getEnterpriseId());
	}
}
