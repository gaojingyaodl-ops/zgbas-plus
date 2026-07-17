package com.spt.bas.server.service.impl;

import com.google.common.base.Splitter;
import com.google.common.cache.LoadingCache;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsProductType;
import com.spt.bas.client.entity.BsProductTypeAccess;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsProductTypeAccessDao;
import com.spt.bas.server.service.IBsProductTypeAccessService;
import com.spt.bas.server.util.ProductTypeUtility;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.InvalidParamException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class BsProductTypeAccessServiceImpl extends BaseService<BsProductTypeAccess>
		implements IBsProductTypeAccessService {
	private static LoadingCache<Long, List<BsProductType>> productTypeCache;
	private static LoadingCache<String, List<BsProductType>> productTypeCacheAll;
	private static LoadingCache<String, BsProductType> cacheOne;
	@Autowired
	private BsProductTypeAccessDao bsProductTypeAccessDao;

	@Override
	public BaseDao<BsProductTypeAccess> getBaseDao() {
		return bsProductTypeAccessDao;
	}

	@Override
	public Class<BsProductTypeAccess> getEntityClazz() {
		return BsProductTypeAccess.class;
	}

	@Override
	public List<BsProductTypeAccess> findByEnterpriseId(Long enterpriseId){
		return bsProductTypeAccessDao.findByEnterpriseId(enterpriseId);
	}

	@Override
	@ServerTransactional
	public void saveAccess(Long enterpriseId, String productCds) throws ApplicationException {
		if (StringUtils.isBlank(productCds)) {
			throw new InvalidParamException("productCds");
		}
		if (enterpriseId == null || enterpriseId == 0) {
			throw new InvalidParamException("enterpriseId");
		}
		bsProductTypeAccessDao.deleteAll(enterpriseId);

		List<String> lstProduct = Splitter.on(BasConstants.SEPARATE).splitToList(productCds);

		for (String productCd : lstProduct) {
			if (StringUtils.isBlank(productCd)) {
				continue;
			}
			Long cnt = bsProductTypeAccessDao.countByProductCdAndEnterpriseId(productCd, enterpriseId);

			if (cnt == 0) {
				BsProductTypeAccess access =new BsProductTypeAccess();
				access.setEnterpriseId(enterpriseId);
				access.setProductCd(productCd);
				bsProductTypeAccessDao.save(access);
			}
		}

	}

	@Override
	@ServerTransactional
	public void countByProductCdAndEnterpriseId(BsProductTypeAccess access){
		Long cnt = bsProductTypeAccessDao.countByProductCdAndEnterpriseId(access.getProductCd(), access.getEnterpriseId());
		if (cnt == 0) {
			bsProductTypeAccessDao.save(access);
		}
	}

	@Override
	public void reFreshCache() {
		ProductTypeUtility.init();
		LocalCacheManager.refreshAll();
	}
}
