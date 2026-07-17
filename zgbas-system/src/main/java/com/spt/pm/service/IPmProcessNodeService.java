package com.spt.pm.service;

import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.vo.PmProcessNodeRefVo;
import com.spt.pm.vo.PmProcessNodeRespVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface IPmProcessNodeService extends IBaseService<PmProcessNode> {

	String getNodeUserId(Long nodeId, Long userId) throws ApplicationException ;

	boolean initProcessNode(Long enterpriseId);

	String getNodeUserId(IPmEntity bizEntity) throws ApplicationException ;

	List<PmProcessNode> findNodeList(Long enterpriseId);

	/**
	 * 批量获取多个节点的引用统计信息
	 * @param nodeIds 节点ID列表
	 * @return 节点ID到引用统计列表的映射
	 */
	Map<Long, List<PmProcessNodeRefVo>> batchGetNodeRefInfo(List<Long> nodeIds);

	Page<PmProcessNodeRespVo> findNodePage(PageSearchVo pageSearchVo);
}
