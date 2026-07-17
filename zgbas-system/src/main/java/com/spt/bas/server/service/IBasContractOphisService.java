package com.spt.bas.server.service;

import com.spt.bas.client.entity.BasContractOphis;
import com.spt.bas.client.vo.ContractOpVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IBasContractOphisService extends IBaseService<BasContractOphis> {

	/** 添加合同状态历史 */
	void addOphis(ContractOpVo opVo);

}
