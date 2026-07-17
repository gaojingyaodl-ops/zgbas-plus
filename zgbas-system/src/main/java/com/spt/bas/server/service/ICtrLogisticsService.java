package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractChain;
import com.spt.bas.client.entity.CtrLogistics;
import com.spt.bas.client.vo.CtrLogisticsVo;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface ICtrLogisticsService extends IBaseService<CtrLogistics> {
    
    void saveLogistics(CtrLogisticsVo ctrLogisticsVo);

    List<CtrLogistics> getByBuyContractNo(String buyContractNo);

    List<CtrLogistics> getBySellContractNo(String sellContractNo);

    void invalidLogistics(String contractNo);
    
    CtrLogistics addLogisticsParams(List<CtrContract> contractList, List<CtrContractChain> contractChainList, List<ApplyCtrDCSX> dcsxList);

    CtrLogistics initLogistics(String contractNo);

    List<CtrLogistics> findByLogisticsNo(String logisticsNo);
}

