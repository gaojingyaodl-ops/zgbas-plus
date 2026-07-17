package com.spt.pm.service.impl;

import com.google.common.base.Splitter;
import com.spt.auth.sdk.cache.UserCache;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmApproveStepDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcessStep;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.service.IPmApproveHistoryService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmApproveStepService;
import com.spt.pm.service.IPmProcessNodeService;
import com.spt.pm.vo.PmApproveStepFlowVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class PmApproveStepServiceImpl extends BaseService<PmApproveStep> implements IPmApproveStepService {
	@Autowired
	private PmApproveStepDao pmApproveStepDao;
	@Autowired
	private IPmApproveService pmApproveService;
	@Resource
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IPmApproveHistoryService pmApproveHistoryService;
	@Autowired
	private IPmProcessNodeService pmProcessNodeService;

	@Override
	public BaseDao<PmApproveStep> getBaseDao() {
		return pmApproveStepDao;
	}

	@Override
	public Class<PmApproveStep> getEntityClazz() {
		return PmApproveStep.class;
	}

	@Override
	@ServerTransactional
	public List<PmApproveStep> saveSteps(IPmEntity bizEntity, Long approveId, Long userId, List<PmProcessStep> lstStep, Boolean specialAutoSignFlg) throws ApplicationException {
		pmApproveStepDao.deleteByApproveId(approveId);
		if (CollectionUtils.isEmpty(lstStep)) {
			throw new ApplicationException("未获取到符合条件的流程,请联系管理员!");
		}
		Map<Long, Long> repeatMap = getRepeatNodeUser(bizEntity, userId, lstStep);
		List<PmApproveStep> approveSteps = new ArrayList<>();
		Date approveDate = new Date();
		for (PmProcessStep step : lstStep) {
			PmApproveStep entity = new PmApproveStep();
			entity.setApproveId(approveId);
			entity.setBackFlg(step.getBackFlg());
			entity.setConditionId(step.getConditionId());
			entity.setDispOrderNo(step.getDispOrderNo());
			entity.setNodeId(step.getNodeId());
			entity.setProcessId(step.getProcessId());
			entity.setStepId(step.getId());
			entity.setStepName(step.getStepName());
			entity.setStepGroup(step.getStepGroup());
			entity.setEnterpriseId(step.getEnterpriseId());
			entity.setAutoSignLimit(Boolean.FALSE.equals(specialAutoSignFlg) ? null : step.getAutoSignLimit());
			// 重复节点是否跳过
			Boolean repeatSkipFlg = Objects.nonNull(step.getRepeatSkipFlg()) ? step.getRepeatSkipFlg() : true;
			if (repeatMap.containsKey(entity.getStepId()) && Boolean.TRUE.equals(repeatSkipFlg)) {
				entity.setApproveRemark("[审批人重合系统自动跳过]");
				entity.setApproveDate(approveDate);
				entity.setApproveUserId(repeatMap.get(entity.getStepId()));
				SysUserSdk sysUser = authOpenFacade.findUserById(entity.getApproveUserId());
				entity.setApproveUserName(Objects.nonNull(sysUser) ? sysUser.getNickName() : "");
				entity.setApproveOpinion(BasConstants.APPROVE_OPINION_AGREE);
			}
			entity = pmApproveStepDao.save(entity);
			approveSteps.add(entity);
		}
		return approveSteps;
	}

	/**
	 * 获取重复节点审批人
	 * @param bizEntity
	 * @param userId
	 * @param stepList
	 * @return
	 */
	private Map<Long, Long> getRepeatNodeUser(IPmEntity bizEntity, Long userId, List<PmProcessStep> stepList) throws ApplicationException {
		Map<Long, Long> repeatMap = new HashMap<>();
		List<PmProcessStep> processStepList = new ArrayList<>();
		processStepList.addAll(stepList);
		Collections.reverse(processStepList);
		List<String> approveUserIdsAllList = new ArrayList<>();
		for (PmProcessStep processStep : processStepList) {
			try {
				String approveUserIds = pmApproveService.findNodeUserId(bizEntity, processStep.getNodeId(), userId);
				if (approveUserIdsAllList.contains(approveUserIds)) {
					String userIdStr = approveUserIds.replace(BasConstants.SEPARATE, "");
					repeatMap.put(processStep.getId(), Long.valueOf(userIdStr));
				} else {
					approveUserIdsAllList.add(approveUserIds);
				}
			} catch (Exception e) {
				logger.error("getRepeatNodeUser error:{}", e.getMessage());
			}
		}
		return repeatMap;
	}

	@Override
	@ServerTransactional
	public PmApproveStep doStep(PmApproveStepFlowVo flowVo) {
		PmApproveStep step = getEntity(flowVo.getApproveStepId());
		if (step != null) {
			step.setApproveDate(new Date());
			step.setApproveOpinion(flowVo.getApproveOpinion());
			step.setApproveRemark(flowVo.getApproveRemark());
			step.setApproveUserId(flowVo.getApproveUserId());
			step.setApproveUserName(flowVo.getApproveUserName());
			setAutoComplete(flowVo, step);
			step.setApproveEnvironment(String.format("%s - %s - %s", flowVo.getIp(), flowVo.getBrowser(), flowVo.getOs()));
			step = pmApproveStepDao.save(step);
		} else {
			logger.warn("doStep error:{}", JsonUtil.obj2Json(flowVo));
		}
		return step;
	}

	@Override
	public List<PmApproveStep> getNextStep(Long approveId, Long approveStepId) {
		List<PmApproveStep> nextList = new ArrayList<>();
		List<PmApproveStep> lstStep = pmApproveStepDao.findByApproveId(approveId);
		PmApproveStep step = lstStep.stream().filter(s -> Objects.equals(s.getId(), approveStepId)).findFirst().orElse(null);
		if (Objects.nonNull(step) && StringUtils.isNotBlank(step.getStepGroup())) {
			nextList = lstStep.stream().filter(s -> StringUtils.equals(step.getStepGroup(), s.getStepGroup())).
					filter(s -> Objects.equals(step.getDispOrderNo(), s.getDispOrderNo())).
					filter(s -> StringUtils.isBlank(s.getApproveOpinion())).
					filter(s -> !Objects.equals(s.getId(), approveStepId)).
					collect(Collectors.toList());
		}
		if (CollectionUtils.isNotEmpty(nextList)) {
			return nextList;
		}
		lstStep = lstStep.stream().filter(s -> StringUtils.isBlank(s.getApproveOpinion())).collect(Collectors.toList());
		for (Iterator<PmApproveStep> iterator = lstStep.iterator(); iterator.hasNext(); ) {
			PmApproveStep pmApproveStep = iterator.next();
			if (approveStepId.equals(pmApproveStep.getId())) {
				if (iterator.hasNext()) {
					PmApproveStep nextStep = iterator.next();
					if (StringUtils.isNotBlank(nextStep.getStepGroup())) {
						List<PmApproveStep> nexts = lstStep.stream().
								filter(s -> StringUtils.equals(nextStep.getStepGroup(), s.getStepGroup())).
								filter(s -> Objects.equals(nextStep.getDispOrderNo(), s.getDispOrderNo())).
								collect(Collectors.toList());
						nextList.addAll(nexts);
					} else {
						nextList.add(nextStep);
					}
					break;
				}
			}
		}
		return nextList;
	}

	@Override
	public PmApproveStep getFirstStep(Long approveId) {
		List<PmApproveStep> lstStep = pmApproveStepDao.findByApproveId(approveId);
		lstStep = lstStep.stream().filter(s -> !StringUtils.equals("[审批人重合系统自动跳过]", s.getApproveRemark())).collect(Collectors.toList());
		if (lstStep.size() > 0) {
			return lstStep.get(0);
		}
		return null;
	}

	@Override
	public List<PmApproveStep> findByApproveId(Long approveId) {
		return pmApproveStepDao.findByApproveId(approveId);
	}

	@Override
	public List<PmApproveStep> findStepByIds(List<Long> stepIdList) {
		if (CollectionUtils.isEmpty(stepIdList)){
			return null;
		}
		return pmApproveStepDao.findStepByIds(stepIdList);
	}

	/**
	 * 自动完成审批，批量生成审批记录
	 * @param approve
	 */
	@Override
	@ServiceTransactional
	public void completeApproveStep(PmApprove approve, String approveRemark, String approveUserName) {
		List<PmApproveStep> approveStepList = pmApproveStepDao.findByApproveId(approve.getId());
		approveStepList.forEach(approveStep -> {

			approveStep.setApproveDate(new Date());
			approveStep.setApproveOpinion(BasConstants.APPROVE_OPINION_AGREE);
			approveStep.setApproveRemark(approveRemark);
			approveStep.setApproveUserId(0L);
			approveStep.setApproveUserName(approveUserName);
			setApproveUser(approve.getCreateUserId(), approveStep);
			pmApproveStepDao.save(approveStep);

			pmApproveHistoryService.addHistory(approve, approveStep);
		});
	}

	private void setAutoComplete(PmApproveStepFlowVo flowVo, PmApproveStep step) {
		if (Boolean.TRUE.equals(flowVo.isComplete())) {
			setApproveUser(flowVo.getApproveUserId(), step);
		}
	}

	private void setApproveUser(Long approveUserId, PmApproveStep step){
		try {
			String nodeUserId = pmProcessNodeService.getNodeUserId(step.getNodeId(), approveUserId);
			if (StringUtils.isNotBlank(nodeUserId)) {
				List<String> nodeUserIdList = Splitter.on(BasConstants.SEPARATE).omitEmptyStrings().splitToList(nodeUserId);
				if (CollectionUtils.isNotEmpty(nodeUserIdList)) {
					Long currApproveUserId = Long.parseLong(nodeUserIdList.get(0));
					step.setApproveUserId(currApproveUserId);
					step.setApproveUserName(UserCache.getUserName(currApproveUserId));
				}
			}
		} catch (Exception e) {
			logger.error("getNodeUserId error", e);
		}
	}
}
