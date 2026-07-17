package com.spt.bas.server.service.impl;

import java.util.List;

import com.spt.tools.core.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.vo.ApplyCancelVo;
import com.spt.bas.server.dao.ApplyCancelDetailDao;
import com.spt.bas.server.service.IApplyCancelDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class ApplyCancelDetailServiceImpl extends BaseService<ApplyCancelDetail> implements IApplyCancelDetailService {
	@Autowired
	private ApplyCancelDetailDao applyCancelDetailDao;
	
	@Override
	public BaseDao<ApplyCancelDetail> getBaseDao() {
		return applyCancelDetailDao;
	}
	
	@Override
	public Class<ApplyCancelDetail> getEntityClazz() {
		return ApplyCancelDetail.class;
	}

	@Override
	public List<ApplyCancelDetail> saveDetailBatch(ApplyCancelVo vo) throws ApplicationException {
		for(ApplyCancelDetail entity:vo.getLstInsert()){
			if (entity.getEnterpriseId() == null) {
				entity.setEnterpriseId(vo.getEnterpriseId());
			}
			entity.setApplyCancelId(vo.getId());
			entity.setId(0L);
			applyCancelDetailDao.save(entity);
		}
		for (ApplyCancelDetail entity : vo.getLstDelete()) {
			if (entity.getId() == null || entity.getId() == 0L) {
				continue;
			} else {
				delete(entity.getId());
			}
		}
		return null;
	}

	@Override
	public List<ApplyCancelDetail> findByApplyCancelId(Long id) {
		
		return applyCancelDetailDao.findByApplyCancelId(id);
	}
	
}

