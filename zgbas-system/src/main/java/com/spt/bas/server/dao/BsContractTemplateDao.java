package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsContractTemplate;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface BsContractTemplateDao extends BaseDao<BsContractTemplate> {

	@Transactional
	@Modifying
	@Query("update BsContractTemplate c set c.fileId =?2 where c.id=?1")
	void updateFileId(Long id, String fileId);

	@Query("from BsContractTemplate c where c.templateTag =?1 and c.enterpriseId =?2 ")
	BsContractTemplate findByTemplateTagAndEnterpriseId(String templateTag, Long enterpriseId);

	@Query("from BsContractTemplate c where c.contractType =?1 and c.enterpriseId =?2  order by c.createdDate desc")
	List<BsContractTemplate> findByContractTypeAndEnterpriseId(String contractType, Long enterpriseId);

	@Query("from BsContractTemplate c where c.contractType =?1 and c.enterpriseId =?2 and c.enableFlg='1' order by c.createdDate desc")
	List<BsContractTemplate> findByContractsell(String contractType, Long enterpriseId);


	@Query("from BsContractTemplate c where c.id =?1 and c.enterpriseId =?2 ")
	BsContractTemplate findByIdAndEnterpriseId(Long id, Long enterpriseId);
}
