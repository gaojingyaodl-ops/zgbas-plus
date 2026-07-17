package com.spt.bas.server.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spt.bas.client.constant.BasConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.client.vo.BasBrandSearchVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BasBrandDao;
import com.spt.bas.server.service.IBasBrandService;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BasBrandServiceImpl extends BaseService<BasBrand> implements IBasBrandService {
	@Autowired
	private BasBrandDao basBrandDao;

	@Override
	public BaseDao<BasBrand> getBaseDao() {
		return basBrandDao;
	}

	@Override
	public Class<BasBrand> getEntityClazz() {
		return BasBrand.class;
	}
	
	@Override
	@ServerTransactional
	public void saveBrand(String productCode, String brandNumber,Long enterpriseId) {
		if (StringUtils.isNotBlank(brandNumber)) {
			// 品名+牌号唯一
			Long count = countBrandNumber(productCode, brandNumber,enterpriseId);
			if (count == 0) {
				BasBrand brand = new BasBrand();
				brand.setBrandNumber(brandNumber);
				brand.setProductCd(productCode);
				brand.setEnterpriseId(enterpriseId);
				basBrandDao.save(brand);
			}
		}
	}

	@Override
	public List<BasBrand> findBrand() {
		return basBrandDao.findBrand(BasConstants.ZG_ENTERPRISE_ID);
	}

	@Override
	public List<String> findBrandNumberList(BasBrandSearchVo vo) {
		List<BasBrand> list = findsBrand(vo);
		List<String> lstStr = CollectionUtil.getPropList(list, "brandNumber");
		return lstStr;
	}

	private Long countBrandNumber(String productCode, String brandNumber,Long enterpriseId) {

		Map<String, Object> map = new HashMap<>();
		// 品名
		map.put("EQS_productCd", productCode);
		// 牌号
		map.put("EQS_brandNumber", brandNumber);
		map.put("EQL_enterpriseId", enterpriseId);
		Specification<BasBrand> spec = WebUtil.buildSpecification(map);

		return basBrandDao.count(spec);
	}

	@Override
	public List<BasBrand> findsBrand(BasBrandSearchVo vo) {
		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isNotBlank(vo.getProductCd())) {
			// 品名
			map.put("EQS_productCd", vo.getProductCd());
		}
		if (StringUtils.isNotBlank(vo.getBrandNumber())) {
			// 牌号
			map.put("EQS_brandNumber", vo.getBrandNumber());
		}
		map.put("EQL_enterpriseId", vo.getEnterpriseId());
		Specification<BasBrand> spec = WebUtil.buildSpecification(map);
		List<BasBrand> list = basBrandDao.findAll(spec);
		return list;
	}

	@Override
	public List<BasBrand> findSafeBrand() {
		return basBrandDao.findSafeBrand(BasConstants.ZG_ENTERPRISE_ID);
	}
}
