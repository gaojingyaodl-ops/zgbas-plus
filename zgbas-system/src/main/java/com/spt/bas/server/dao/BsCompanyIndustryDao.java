package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

import com.spt.bas.client.entity.BsCompanyIndustry;

public interface BsCompanyIndustryDao extends BaseDao<BsCompanyIndustry> {
	List<BsCompanyIndustry> findAll();
}

