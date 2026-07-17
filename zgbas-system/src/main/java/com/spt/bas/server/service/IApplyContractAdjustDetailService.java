package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.ApplyContractAdjustDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApplyContractAdjustVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyContractAdjustDetailService extends IBaseService<ApplyContractAdjustDetail> {


	public List<ApplyContractAdjustDetail> findByContractAdjustId(Long contractAdjustId);
}

