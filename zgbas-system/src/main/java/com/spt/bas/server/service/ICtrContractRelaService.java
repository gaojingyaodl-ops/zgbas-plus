package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractRela;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;
import java.util.List;

public interface ICtrContractRelaService extends IBaseService<CtrContractRela> {

	void saveCtrContractReal2(CtrProduct buyProduct, Long sellProductId);

	void saveCtrContractRealMS(Long buyContractId, Long sellProductId);

	void saveCtrContractReal(Long stockContractId, CtrProduct sellProduct, BigDecimal dealNumber);

	void saveCtrContractReal3(PmApprove pmApprove, List<ApplyProductDetail> productDetailList, CtrContract sellCtrContract);

	Long countRela(Long contractId, String contractType);

	void invalidContract(CtrContract contract);

	/**
	 * 通过sellContractId获取合同关联关系
	 * @param contractId
	 * @return
	 */
	CtrContractRela getRelaBySellContractId(Long contractId);

}

