package com.spt.pm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmProcessAccessDao;
import com.spt.pm.entity.PmProcessAccess;
import com.spt.pm.service.IPmProcessAccessService;
import com.spt.pm.vo.PmProcessAccessVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class PmProcessAccessServiceImpl extends BaseService<PmProcessAccess> implements IPmProcessAccessService {
	@Autowired
	private PmProcessAccessDao pmProcessAccessDao;

	@Override
	public BaseDao<PmProcessAccess> getBaseDao() {
		return pmProcessAccessDao;
	}

	@Override
	public Class<PmProcessAccess> getEntityClazz() {
		return PmProcessAccess.class;
	}

	@Override
	public List<PmProcessAccess> findByProcessId(Long processId) {
		return pmProcessAccessDao.findByProcessId(processId);
	}

	@Override
	@ServerTransactional
	public void saveChanges(List<PmProcessAccess> list)throws ApplicationException  {
		if(list.size()>0){
			Long processId = list.get(0).getProcessId();
			//添加的情况
			for (PmProcessAccess access : list) {
				PmProcessAccess pm = pmProcessAccessDao.findByProcessIdAndUserId(access.getProcessId(), access.getUserId());
				if(pm==null){
					this.save(access);
				}
			}
			//获取传进来list的userId的List
			List<Long> ulist = new ArrayList<Long>();
			for (PmProcessAccess access : list) {
				ulist.add(access.getUserId());
			}
			//删除的情况
			List<PmProcessAccess> plist = pmProcessAccessDao.findByProcessId(processId);
			if(!plist.isEmpty()){
				for (PmProcessAccess paccess : plist) {
					if(!ulist.contains(paccess.getUserId())){
						this.delete(paccess.getId());
					}
				}
			}
		}
	}

	@Override
	public List<PmProcessAccess> findByUserId(Long userId) {
		// TODO Auto-generated method stub
		return pmProcessAccessDao.findByUserId(userId);
	}

	@Override
	@ServerTransactional
	public void saveByUser(PmProcessAccessVo vo) {
		List<PmProcessAccess> beforeOperation = pmProcessAccessDao.findByUserId(vo.getUserId());
		List<PmProcessAccess> afterOperation = new ArrayList<PmProcessAccess>();
		String processIds = vo.getProcessIds();
		if(StringUtils.isNoneBlank(processIds)){
			String[] idArray = processIds.split("\\|");
			for (String pid : idArray) {
				Long processId = Long.valueOf(pid);
				PmProcessAccess access = pmProcessAccessDao.findByProcessIdAndUserId(processId, vo.getUserId());
				if(access == null){
					access = new PmProcessAccess();
					BeanUtils.copyProperties(vo, access);
					access.setProcessId(processId);
					access = pmProcessAccessDao.save(access);
				}
				afterOperation.add(access);
			}
		}

		//操作前的某个元素若不存在操作后的list中，则表示需删除
		for (PmProcessAccess access : beforeOperation) {
			if(!afterOperation.contains(access)){
				pmProcessAccessDao.delete(access);
			}
		}
	}

	@Override
	public Boolean verifyUserProcessPermission(PmProcessAccessVo vo) {
		try {
			List<PmProcessAccess> resultList = pmProcessAccessDao.findByUserIdAndProcessCode(vo.getProcessCode(), vo.getUserId());
			return CollectionUtils.isNotEmpty(resultList);
		} catch (Exception e) {
			logger.info("queryVo:{}", vo);
			logger.error("verifyUserProcessPermission error", e);
			return false;
		}
	}
}

