package com.spt.bas.server.service;

import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.client.vo.SealUsageSearchVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ISealUsageService extends IBaseService<SealUsage> {

    Page<SealUsage> findUsagePage(SealUsageSearchVo searchVo);

    void startSealUsage(PmApprove approve);

    SealUsage findById( Long id);

    void signLogistics(SealUsage entity, PmApprove approve);

    List<SealUsage> findByContractId(Long contractId);
}

