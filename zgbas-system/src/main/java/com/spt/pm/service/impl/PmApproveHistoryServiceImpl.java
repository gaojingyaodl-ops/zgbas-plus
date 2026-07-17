package com.spt.pm.service.impl;

import com.spt.auth.sdk.cache.UserCache;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.*;
import com.spt.pm.entity.*;
import com.spt.pm.service.IPmApproveHistoryService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Transactional(readOnly = true)
public class PmApproveHistoryServiceImpl extends BaseService<PmApproveHistory> implements IPmApproveHistoryService {
	@Autowired
	private PmApproveHistoryDao pmApproveHistoryDao;
	@Autowired
	private PmProcessStepDao pmProcessStepDao;
	@Autowired
	private PmProcessNodeDao pmProcessNodeDao;
	@Autowired
	private PmApproveStepDao pmApproveStepDao;
	@Autowired
	private PmProcessConditionDao pmProcessConditionDao;

	@Override
	public BaseDao<PmApproveHistory> getBaseDao() {
		return pmApproveHistoryDao;
	}

	@Override
	public Class<PmApproveHistory> getEntityClazz() {
		return PmApproveHistory.class;
	}

	@Override
	@ServerTransactional
	public void addHistory(PmApprove approve, PmApproveStep step) {
		if (step == null)
			return;
		PmApproveHistory his = new PmApproveHistory();
		his.setApproveId(approve.getId());
		his.setApproveRemark(step.getApproveRemark());
		his.setApproveOpinion(step.getApproveOpinion());
		his.setApproveUserId(step.getApproveUserId());
		his.setApproveUserName(step.getApproveUserName());
		his.setConditionId(step.getConditionId());
		his.setNodeId(step.getNodeId());
		his.setProcessId(approve.getProcessId());
		his.setApproveStepId(step.getId());
		his.setStepName(step.getStepName());
		his.setEnterpriseId(approve.getEnterpriseId());
		pmApproveHistoryDao.save(his);
	}

	@Override
	public List<PmApproveHistory> findByApproveId(Long approveId){
		return pmApproveHistoryDao.findByApproveId(approveId);
	}

	@Override
	public List<PmApproveHistory> findByApproveIdOrProcessId(Long approveId, Long processId, Long enterpriseId) {
		List<PmApproveHistory> history = new ArrayList<>();
		if (approveId != null && approveId != 0L) {
			List<PmApproveStep> approveStepList = pmApproveStepDao.findByApproveId(approveId);
			List<PmApproveHistory> firstHistory = pmApproveHistoryDao.findByApproveIdAndApproveStepIdOrderByIdDesc(approveId, null);
			if (firstHistory != null && firstHistory.size() > 0) {
				history.add(firstHistory.get(0));
			}
			for (PmApproveStep pmApproveStep : approveStepList) {
				PmApproveHistory currHistory = new PmApproveHistory();
				List<PmApproveHistory> hisList = pmApproveHistoryDao.findByApproveIdAndApproveStepIdOrderByIdDesc(approveId, pmApproveStep.getId());
				if (hisList != null && hisList.size() > 0) {
					currHistory = hisList.get(0);
				} else {
					currHistory.setStepName(pmApproveStep.getStepName());
					PmProcessNode node = pmProcessNodeDao.findOne(pmApproveStep.getNodeId());
					String nodeUserId = node.getNodeUserId();
					if (nodeUserId != null) {
						currHistory.setApproveUserName(UserCache.getUserName(nodeUserId));
					}

				}
				if (StringUtils.isNotBlank(pmApproveStep.getStepGroup())){
					String stepGroupName = BsDictUtil.getValue(pmApproveStep.getEnterpriseId(), BasConstants.DictType.DICT_STEP_GROUP_TYPE, pmApproveStep.getStepGroup());
					if (StringUtils.isNotBlank(stepGroupName)){
						currHistory.setStepName("【" + stepGroupName + "】" + pmApproveStep.getStepName());
					}
				}
				history.add(currHistory);

			}

		} else if (processId != null && processId != 0L) {
			PmApproveHistory his = new PmApproveHistory();
			his.setStepName("发起申请");
			history.add(his);
			//流程存在多个条件时，默认选择第一个流程条件下的流程步骤
			List<PmProcessCondition> conditionList = pmProcessConditionDao.findAllEnable(processId);
			if (conditionList != null && conditionList.size() > 0) {
				Long conditionId = conditionList.get(0).getId();
				List<PmProcessStep> processList = pmProcessStepDao.findByConditionId(conditionId);
				for (PmProcessStep pmProcessStep : processList) {
					PmApproveHistory h = new PmApproveHistory();
					h.setStepName(pmProcessStep.getStepName());
					PmProcessNode node = pmProcessNodeDao.findOne(pmProcessStep.getNodeId());
					if (Objects.nonNull(node) && Objects.nonNull(node.getNodeUserId())) {
						h.setApproveUserName(UserCache.getUserName(node.getNodeUserId()));
					}
					history.add(h);
				}
			}

		}
		return history;
	}
}
