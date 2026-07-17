package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsCompanyShare;
import com.spt.bas.server.dao.BsCompanyShareDao;
import com.spt.bas.server.service.IBsCompanyShareService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class BsCompanyShareServiceImpl extends BaseService<BsCompanyShare> implements IBsCompanyShareService {
	@Autowired
	private BsCompanyShareDao bsCompanyShareDao;

	@Override
	public BaseDao<BsCompanyShare> getBaseDao() {
		return bsCompanyShareDao;
	}

	@Override
	public Class<BsCompanyShare> getEntityClazz() {
		return BsCompanyShare.class;
	}

	@Override
	@Transactional(readOnly = false)
	public BsCompanyShare save(BsCompanyShare entity) throws ApplicationException {
		BsCompanyShare share = bsCompanyShareDao.findByCompanyIdAndSharedUserId(entity.getCompanyId(),
				entity.getSharedUserId());
		if (share == null) {
			return bsCompanyShareDao.save(entity);
		} else {
			return share;
		}
	}

	@Override
	public BsCompanyShare findByCompanyIdAndSharedUserId(Long companyId, Long userId) {
		return bsCompanyShareDao.findByCompanyIdAndSharedUserId(companyId, userId);
	}

	@Override
	public List<BsCompanyShare> findByCompanyIdAndCreateUserId(Long companyId, Long createUserId) {
		return bsCompanyShareDao.findByCompanyIdAndCreateUserId(companyId, createUserId);
	}

	@Override
	public List<BsCompanyShare> findBySharedUserId(Long shareUserId) {
		return bsCompanyShareDao.findBySharedUserId(shareUserId);
	}
	
	@Override
	public List<BsCompanyShare> findByCompanyId(Long shareUserId) {
		return bsCompanyShareDao.findByCompanyId(shareUserId);
	}

	@Override
	public void deleteCompanyShare(Long companyId) {
		bsCompanyShareDao.deleteByCompanyId(companyId);
	}
}
