package com.spt.bas.server.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractFee;
import com.spt.bas.client.vo.ContractFeeSearchVo;
import com.spt.tools.jpa.service.IBaseService;

public interface ICtrContractFeeService extends IBaseService<CtrContractFee> {
	/**
	 * 更新附件ID
	 * @param id 合同模板ID
	 * @param fileId 附件ID
	 */
	public void updateFileId(Long id, String fileId);

	/**
	 * 条件查询
	 * @param vo
	 * @return
	 */
	Page<CtrContractFee> findPageContractFee(ContractFeeSearchVo queryVo);

	public 	List<CtrContractFee> findByContractIdAndFeeType(Long contractId, String feeType);

	public void saveWarehouseFee(CtrContract contract, BigDecimal feeAmount, Boolean backFlg);
}

