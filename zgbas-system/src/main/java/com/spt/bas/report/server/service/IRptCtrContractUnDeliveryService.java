package com.spt.bas.report.server.service;


import com.spt.bas.report.client.vo.RptCtrContractUnDeliverySearchVo;
import com.spt.bas.report.client.vo.RptCtrContractUnDeliveryVo;
import org.springframework.data.domain.Page;

public interface IRptCtrContractUnDeliveryService {

    public Page<RptCtrContractUnDeliveryVo> findUnDeliveryPage(RptCtrContractUnDeliverySearchVo vo);
}
