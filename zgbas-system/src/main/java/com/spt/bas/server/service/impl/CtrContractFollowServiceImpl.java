package com.spt.bas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractFollow;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrContractFollowDao;
import com.spt.bas.server.service.ICtrContractFollowService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class CtrContractFollowServiceImpl extends BaseService<CtrContractFollow> implements ICtrContractFollowService {
	@Autowired
	private CtrContractFollowDao ctrContractFollowDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	
	@Override
	public BaseDao<CtrContractFollow> getBaseDao() {
		return ctrContractFollowDao;
	}
	
	@Override
	public Class<CtrContractFollow> getEntityClazz() {
		return CtrContractFollow.class;
	}
	@Override
	public List<CtrContractFollow> findByCtrContractId(Long ctrContractId) {
		return ctrContractFollowDao.findByCtrContractId(ctrContractId);
	}
	/**
	 * 添加通知
	 */
	@Override
	@ServerTransactional
	public void toNotify(CtrContractFollow follow) {
		CtrContract ctr = ctrContractDao.findOne(follow.getCtrContractId());
		follow.setEnterpriseId(ctr.getEnterpriseId());
		follow.setRespUserId(ctr.getMatchUserId().toString());
		follow.setContractStatus(ctr.getContractStatus());
		follow.setStatus(BasConstants.ORRVER_REPLY_STATUS_N);
		String notify = "合同编号为:["+ctr.getBusinessNo()+"]的合同现已逾期,请尽快处理";
		follow.setNotifyContent(notify);
		ctrContractFollowDao.save(follow);
	}

}