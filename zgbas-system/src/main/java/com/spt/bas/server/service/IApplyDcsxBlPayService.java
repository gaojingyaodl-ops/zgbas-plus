package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IApplyDcsxBlPayService extends IBaseService<ApplyPay> {


    ApplyCtrContractFactor findByApproveId(Long approveid);
}

