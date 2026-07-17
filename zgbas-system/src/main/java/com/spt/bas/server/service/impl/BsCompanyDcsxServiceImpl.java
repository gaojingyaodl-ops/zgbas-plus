package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.server.dao.BsCompanyDcsxDao;
import com.spt.bas.server.service.IBsCompanyDcsxService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class BsCompanyDcsxServiceImpl extends BaseService<BsCompanyDcsx> implements IBsCompanyDcsxService {

	@Autowired
    private  BsCompanyDcsxDao bsCompanyDcsxDao;

	@Override
	public BaseDao<BsCompanyDcsx> getBaseDao() {
		return bsCompanyDcsxDao;
	}

	@Override
	public BsCompanyDcsx findByCompanyName(String companyName) {
		return bsCompanyDcsxDao.findByCompanyName(companyName);
	}

	@Override
	public Map<String, BsCompanyDcsx> getCompanyConfigMap() {
		List<BsCompanyDcsx> companyDcsxes = this.findAll();
		return companyDcsxes.stream().collect(Collectors.toMap(BsCompanyDcsx::getCompanyName, Function.identity(), (t1, t2) -> t1));
	}

	@Override
	public List<BsCompanyDcsx> findDcsxCompanyList() {
		return bsCompanyDcsxDao.findByDcsxFlgAndEnableFlgOrderByDispOrderNoDesc(true,true);
	}

	@Override
	public BsCompanyDcsx findByCompanyCd(String companyCd) {
		return bsCompanyDcsxDao.findByCompanyCd(companyCd);
	}

	/**
	 * 判断中游企业是否开通电子签章
	 *
	 * @param companyName
	 * @return
	 */
	@Override
	public Boolean verifySignCompany(String companyName) {
		BsCompanyDcsx company = this.findByCompanyName(companyName);
		return Objects.nonNull(company) && Boolean.TRUE.equals(company.getEnableFlg()) && Boolean.TRUE.equals(company.getSignFlg());
	}

}
