/**
 * 
 */
package com.spt.pm.inter;

import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveRetrieveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;

/**
 * 审批监听器
 * 
 * @author wlddh
 *
 */
public interface IPmApproveListener {

	/** 发起审批 */
	default void doStepIn(PmApprove approve) throws ApplicationException {
	}

	/** 执行审批步骤 */
	void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException;

	/** 审批驳回 */
	default void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
	}

	/** 审批撤回 */
	void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException;

	/** 审批追回 */
	default void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {};
}
