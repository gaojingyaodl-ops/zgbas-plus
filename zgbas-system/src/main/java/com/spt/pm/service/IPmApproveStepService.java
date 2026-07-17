package com.spt.pm.service;

import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcessStep;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.vo.PmApproveStepFlowVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IPmApproveStepService extends IBaseService<PmApproveStep> {

	PmApproveStep getFirstStep(Long approveId);

	/** 获取下一个审批步骤 */
	List<PmApproveStep> getNextStep(Long approveId, Long approveStepId);

	/** 执行步骤 */
	PmApproveStep doStep(PmApproveStepFlowVo flowVo);

	List<PmApproveStep> saveSteps(IPmEntity bizEntity, Long approveId, Long userId, List<PmProcessStep> lstStep, Boolean specialAutoSignFlg) throws ApplicationException;

	List<PmApproveStep> findByApproveId(Long approveId);

	List<PmApproveStep> findStepByIds(List<Long> stepIdList);

	/**
	 * 自动完成审批，批量生成审批记录
	 * @param approve
	 */
	void completeApproveStep(PmApprove approve, String approveRemark, String approveUserName);

}
