package com.spt.bas.server.service;

import java.math.BigDecimal;
import java.util.List;

import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.vo.BasContractExistVo;
import com.spt.bas.client.vo.BasContractRelaVo;
import com.spt.bas.client.vo.BasContractVo;
import com.spt.bas.client.vo.ContractOpVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IBasContractService extends IBaseService<BasContract> {

	boolean existGoodsCode(BasContractExistVo vo);

	void updateFileId(Long id, String fileId);

	/** 根据合同支付金额 */
	void updatePayAmount(Long contractId, String payType, BigDecimal payAmount) throws ApplicationException;

	List<BasContractVo> saveBatchByRelaApprove(BasContractRelaVo relaVo)throws ApplicationException ;

	List<BasContract> findByContractRelaId(Long id);
	
//	/**更新合同状态
//	 * @param fondflog */
//	void updateContractStatus(Long contractId, String contractStatus) throws ApplicationException;
	
//	/**更新合同状态 已收款
//	 * @param fondflog */
//	void updateContractStatusByFond(BasContract contract);
//	/**
//	 * 更新合同状态  已收票
//	 * @param contract
//	 */
//	void updateContractStatusByBill(BasContract contract);
	
	/**合同状态操作*/
	public void doContractOp(ContractOpVo opVo);
	/**
	 * 更新敞口业务合同的剩余数量
	 * @param exposureContractId 敞口业务ID
	 * @param useDealNumber
	 */
	void updateExposureContract(Long exposureContractId, BigDecimal useDealNumber);



}
