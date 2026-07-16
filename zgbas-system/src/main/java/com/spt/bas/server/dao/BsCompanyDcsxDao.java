package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BsCompanyDcsxDao extends BaseDao<BsCompanyDcsx> {

	@Query("from BsCompanyDcsx where companyName = ?1 ")
	BsCompanyDcsx findByCompanyName(String companyName);

	List<BsCompanyDcsx> findByDcsxFlgAndEnableFlgOrderByDispOrderNoDesc(Boolean dcsxFlg, Boolean enableFlg);

	BsCompanyDcsx findByCompanyCd(String companyCd);
}
