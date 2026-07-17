package com.spt.pm.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.cache.PmNodeCache;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmProcessNodeDao;
import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.service.IPmProcessNodeService;
import com.spt.pm.vo.PmProcessNodeRefVo;
import com.spt.pm.vo.PmProcessNodeRespVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class PmProcessNodeServiceImpl extends BaseService<PmProcessNode> implements IPmProcessNodeService {
	@Autowired
	private PmProcessNodeDao pmProcessNodeDao;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@Override
	public BaseDao<PmProcessNode> getBaseDao() {
		return pmProcessNodeDao;
	}

	@Override
	public Class<PmProcessNode> getEntityClazz() {
		return PmProcessNode.class;
	}

	@Override
	public String getNodeUserId(Long nodeId, Long userId) throws ApplicationException {
		PmProcessNode node = PmNodeCache.getEntity(nodeId);
		if (Objects.isNull(node)){
			return null;
		}
		String nodeType = node.getNodeType();
		if (StringUtils.equals(PmConstants.NODE_TYPE_USER, nodeType)) {
			// 人员类型节点
			String nodeUserId  = node.getNodeUserId();
			if (StringUtils.isNotBlank(nodeUserId)) {
				if (!nodeUserId.startsWith(PmConstants.SEPARATE )) {
					nodeUserId = PmConstants.SEPARATE + nodeUserId;
				}
				if (!nodeUserId.endsWith(PmConstants.SEPARATE )) {
					nodeUserId = nodeUserId + PmConstants.SEPARATE;
				}
			}
			return nodeUserId;
		} else {
			DeptSearchVo searchVo = new DeptSearchVo();
			searchVo.setUserId(userId);
			searchVo.setDeptType(nodeType);
			Long nodeUserId = authOpenFacade.findDeptLeader(searchVo);
			// 特殊情况 发起人挂载在中心下(使用findDeptLeader方法查询会有问题)
			if (isNullNodeUserId(nodeUserId) && StringUtils.equals(PmConstants.NODE_TYPE_DEPT, nodeType)){
				SysDeptSdk sysDept = authOpenFacade.findDeptByUserId(userId);
				nodeUserId = sysDept.getLeaderId();
			}

			// 查询部门负责人时，发起人是部门负责人时，向上取机构负责人
			if (Objects.equals(userId, nodeUserId) && StringUtils.equals(PmConstants.NODE_TYPE_DEPT, nodeType)) {
				// 递归 向上取不是自己的部门负责人
				SysDeptSdk sysDept = authOpenFacade.findDeptByUserId(userId);
				nodeUserId = getNodeDeptLeader(sysDept, userId);
			}
			// 部门负责人如果找不到，就取中心负责人
			if (isNullNodeUserId(nodeUserId) && StringUtils.equals(PmConstants.NODE_TYPE_DEPT, nodeType)) {
				searchVo.setDeptType(PmConstants.NODE_TYPE_CENTER);
				nodeUserId = authOpenFacade.findDeptLeader(searchVo);
			}
			if (isNullNodeUserId(nodeUserId)) {
				logger.warn("未找到指定的审批人,nodeId:{},userId:{}", nodeId, userId);
				throw new ApplicationException("未找到指定的审批人！");
			}
			return PmConstants.SEPARATE + nodeUserId + PmConstants.SEPARATE;
		}
	}

	@Override
	@ServerTransactional
	public boolean initProcessNode(Long enterpriseId) {
		String sql = "{call up_init_process_node(?)}";
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(0, String.valueOf(enterpriseId));
		boolean flag = commonDao.executeStoreProcedure(sql, map);
		return flag;
	}

	@Override
	public String getNodeUserId(IPmEntity bizEntity) throws ApplicationException {
		//PmProcessNode node = pmProcessNodeDao.findOne(nodeId);
		if (bizEntity instanceof ApplyMatch) {
			ApplyMatch applyMatch = (ApplyMatch) bizEntity;
			String internalContractNo = applyMatch.getSellContractNo();
			if (StringUtils.isNotBlank(internalContractNo)) {
				return PmConstants.SEPARATE+applyMatch.getBuyUserId().toString()+PmConstants.SEPARATE;
			}
		}
		return null;
	}

	@Override
	public List<PmProcessNode> findNodeList(Long enterpriseId) {
		return pmProcessNodeDao.findByEnterpriseIdAndEnableTrue(enterpriseId);
	}

	@Override
	public Map<Long, List<PmProcessNodeRefVo>> batchGetNodeRefInfo(List<Long> nodeIds) {
		if (nodeIds == null || nodeIds.isEmpty()) {
			return Collections.emptyMap();
		}

		// 初始化结果Map
		Map<Long, Map<String, Long>> nodeRefMap = new HashMap<>();
		for (Long nodeId : nodeIds) {
			nodeRefMap.put(nodeId, new HashMap<>());
		}

		// 批量查询审批流程引用信息
		List<Object[]> processRefs = pmProcessNodeDao.findProcessRefByNodeIds(nodeIds);
		for (Object[] ref : processRefs) {
			Long nodeId = ((Number) ref[0]).longValue();
			String processName = (String) ref[1];
			Long count = ((Number) ref[2]).longValue();
			nodeRefMap.get(nodeId).merge(processName, count, Long::sum);
		}

		// 批量查询额外审批流程引用信息
		List<Object[]> autoProcessRefs = pmProcessNodeDao.findAutoProcessRefByNodeIds(nodeIds);
		for (Object[] ref : autoProcessRefs) {
			Long nodeId = ((Number) ref[0]).longValue();
			String processName = (String) ref[1];
			Long count = ((Number) ref[2]).longValue();
			nodeRefMap.get(nodeId).merge(processName, count, Long::sum);
		}

		// 转换为最终结果
		Map<Long, List<PmProcessNodeRefVo>> result = new HashMap<>();
		for (Map.Entry<Long, Map<String, Long>> entry : nodeRefMap.entrySet()) {
			List<PmProcessNodeRefVo> refList = entry.getValue().entrySet().stream()
					.map(e -> new PmProcessNodeRefVo(e.getKey(), e.getValue()))
					.sorted(Comparator.comparing(PmProcessNodeRefVo::getProcessName))
					.collect(Collectors.toList());
			result.put(entry.getKey(), refList);
		}

		return result;
	}

	@Override
	public Page<PmProcessNodeRespVo> findNodePage(PageSearchVo searchVo) {
		Page<PmProcessNode> resultPage = this.findPage(searchVo);
		List<PmProcessNode> content = resultPage.getContent();
		if (CollectionUtils.isEmpty(content)) {
			PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
			return new PageImpl<>(new ArrayList<>(), pageRequest, resultPage.getTotalElements());
		}
		List<Long> nodeIdList = content.stream().map(PmProcessNode::getId).collect(Collectors.toList());
		// 初始化结果Map
		Map<Long, Map<String, Long>> nodeRefMap = new HashMap<>();
		for (Long nodeId : nodeIdList) {
			nodeRefMap.put(nodeId, new HashMap<>());
		}

		// 批量查询审批流程引用信息
		List<Object[]> processRefs = pmProcessNodeDao.findProcessRefByNodeIds(nodeIdList);
		for (Object[] ref : processRefs) {
			Long nodeId = ((Number) ref[0]).longValue();
			String processName = (String) ref[1];
			Long count = ((Number) ref[2]).longValue();
			nodeRefMap.get(nodeId).merge(processName, count, Long::sum);
		}

		// 批量查询额外审批流程引用信息
		List<Object[]> autoProcessRefs = pmProcessNodeDao.findAutoProcessRefByNodeIds(nodeIdList);
		for (Object[] ref : autoProcessRefs) {
			Long nodeId = ((Number) ref[0]).longValue();
			String processName = (String) ref[1];
			Long count = ((Number) ref[2]).longValue();
			nodeRefMap.get(nodeId).merge(processName, count, Long::sum);
		}

		// 转换为最终结果
		Map<Long, List<PmProcessNodeRefVo>> resultMap = new HashMap<>();
		for (Map.Entry<Long, Map<String, Long>> entry : nodeRefMap.entrySet()) {
			List<PmProcessNodeRefVo> refList = entry.getValue().entrySet().stream()
					.map(e -> new PmProcessNodeRefVo(e.getKey(), e.getValue()))
					.sorted(Comparator.comparing(PmProcessNodeRefVo::getProcessName))
					.collect(Collectors.toList());
			resultMap.put(entry.getKey(), refList);
		}
		List<PmProcessNodeRespVo> resultList = new ArrayList<>();
		content.forEach(e -> {
			PmProcessNodeRespVo vo = new PmProcessNodeRespVo();
			BeanUtils.copyProperties(e, vo);
			List<PmProcessNodeRefVo> refList = resultMap.get(e.getId());
			if (refList != null && !refList.isEmpty()) {
				// 格式化显示：流程名(次数),流程名(次数)...
				String refDisplay = refList.stream()
						.map(ref -> ref.getProcessName() + "(" + ref.getRefCount() + ")")
						.collect(Collectors.joining(", "));
				vo.setRefInfo(refDisplay);
				vo.setRefInfoJson(JsonUtil.obj2Json(refList));
			} else {
				vo.setRefInfo("");
				vo.setRefInfoJson("[]");
			}
			resultList.add(vo);
		});
		PageRequest pageRequest_new = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<PmProcessNodeRespVo> pageVo = new PageImpl<>(resultList, pageRequest_new, resultPage.getTotalElements());
		return pageVo;
	}

	private static boolean isNullNodeUserId(Long userId){
		return Objects.isNull(userId) || Objects.equals(0L, userId);
	}

	/**
	 * 向上取不是自己的部门负责人
	 * @param dept
	 * @param userId
	 * @return
	 */
	private Long getNodeDeptLeader(SysDeptSdk dept, Long userId) {
		if (Objects.isNull(dept) || Objects.isNull(userId)) {
			return null;
		}
		Long leaderUserId = dept.getLeaderId();
		if (Objects.equals(userId, leaderUserId)) {
			return getNodeDeptLeader(dept.getParent(), userId);
		} else {
			return leaderUserId;
		}
	}
}
