package com.spt.bas.server.dao;

import java.util.List;

import com.spt.bas.client.entity.BsTemplateConfig;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsTemplateConfigDao extends BaseDao<BsTemplateConfig> {

	//@Query(value ="select c from BsTemplateConfig c where c.templateCat=?1 ")
	List<BsTemplateConfig> findByTemplateCat(String templateCat);
}

