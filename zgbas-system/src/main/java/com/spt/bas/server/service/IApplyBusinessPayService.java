package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyBusinessPay;
import com.spt.bas.client.vo.ApplyBusinessPayVo;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.CtrContractDeliveryVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;

public interface IApplyBusinessPayService extends IBaseService<ApplyBusinessPay> {

    void updateFileId(Long id, String fileId);

    ApplyBusinessPay findByApproveId(Long id);

    Page<ApplyBusinessPayVo> findPageContract(ApplyBusinessPayVo queryVo);
}

