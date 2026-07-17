package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsProductType;
import com.spt.bas.server.dao.BsProductTypeDao;
import com.spt.bas.server.service.IBsProductTypeService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(readOnly = true)
public class BsProductTypeServiceImpl extends BaseService<BsProductType> implements IBsProductTypeService {
	@Autowired
	private BsProductTypeDao bsProductTypeDao;
	
	@Override
	public BaseDao<BsProductType> getBaseDao() {
		return bsProductTypeDao;
	}
	
	@Override
	public Class<BsProductType> getEntityClazz() {
		return BsProductType.class;
	}

	@Override
	public List<BsProductType> findByList(String typeCode) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		if(StringUtils.isNotBlank(typeCode)){
			map.put("LIKES_typeCode", typeCode+"_%");
		}
		Specification<BsProductType> spec = WebUtil.buildSpecification(map);
		List<BsProductType> typeList = bsProductTypeDao.findAll(spec);
		
		return typeList;
	}


	@Override
	public BsProductType findProductTypeCode(String typeCode) {
		return bsProductTypeDao.findProductTypeCode(typeCode);
	}

	@Override
	public List<BsProductType> findAllByEnterpriseId(Long enterpriseId) {
		return bsProductTypeDao.findAllByEnterpriseId(enterpriseId);
	}

	@Override
	public BsProductType findHGByTypeName(String typeName) {
		return bsProductTypeDao.findHGByTypeName(typeName);
	}

	@Override
	public List<BsProductType> findAllProductAlAndHg() {
		return bsProductTypeDao.findAllProductAlAndHg();
	}
}

