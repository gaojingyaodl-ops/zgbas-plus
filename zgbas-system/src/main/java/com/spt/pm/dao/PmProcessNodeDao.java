package com.spt.pm.dao;

import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.vo.PmProcessNodeRefVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PmProcessNodeDao extends BaseDao<PmProcessNode> {

	@Query("select n.nodeUserId from PmProcessNode n where n.nodeCode = ?1 and n.enterpriseId =?2")
	String findByNodeCode(String nodeCode, Long enterpriseId);

	PmProcessNode findByNodeCodeAndEnterpriseId(String nodeCode, Long enterpriseId);

	@Query("from PmProcessNode where enterpriseId = ?1 and enableFlg = true")
	List<PmProcessNode> findByEnterpriseIdAndEnableTrue(Long enterpriseId);

	/**
	 * 批量查询多个节点被审批流程引用的统计信息
	 * @param nodeIds 节点ID列表
	 * @return nodeId, processName, refCount 结果列表
	 */
	@Query(value = "select s.node_id as nodeId, p.process_name as processName, count(1) as refCount " +
			"from t_pm_process_step s " +
			"left join t_pm_process p on s.process_id = p.id " +
			"where s.node_id in (?1) and s.enable_flg = true and p.enable_flg = true " +
			"group by s.node_id, s.process_id", nativeQuery = true)
	List<Object[]> findProcessRefByNodeIds(List<Long> nodeIds);

	/**
	 * 批量查询多个节点被额外审批流程引用的统计信息
	 * @param nodeIds 节点ID列表
	 * @return nodeId, processName, refCount 结果列表
	 */
	@Query(value = "select s.auto_node_id as nodeId, p.process_name as processName, count(1) as refCount " +
			"from t_pm_process_auto_step s " +
			"left join t_pm_process p on s.process_id = p.id " +
			"where s.auto_node_id in (?1) and s.enable_flg = true and p.enable_flg = true " +
			"group by s.auto_node_id, s.process_id", nativeQuery = true)
	List<Object[]> findAutoProcessRefByNodeIds(List<Long> nodeIds);
}