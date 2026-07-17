package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsContractTemplate;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IBsContractTemplateService extends IBaseService<BsContractTemplate> {
	/**
	 * 更新附件ID
	 * @param id 合同模板ID
	 * @param fileId 附件ID
	 */
	public void updateFileId(Long id, String fileId);

	BsContractTemplate findByTemplateTagAndEnterpriseId(BsContractTemplate template);

	List<BsContractTemplate> findByContractTypeAndEnterpriseId(BsContractTemplate template);

	List<BsContractTemplate> findByContractsell(BsContractTemplate template);

	BsContractTemplate findByIdAndEnterpriseId(BsContractTemplate template);


}
