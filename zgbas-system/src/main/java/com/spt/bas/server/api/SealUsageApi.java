package com.spt.bas.server.api;

import com.spt.bas.client.entity.SealMixture;
import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.client.vo.SealUsageSearchVo;
import com.spt.bas.server.service.ISealUsageService;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "seal/usage")
public class SealUsageApi extends BaseApi<SealUsage> {
    @Autowired
    private ISealUsageService sealUsageService;

    @Override
    public IBaseService<SealUsage> getService() {
        return sealUsageService;
    }

    @RequestMapping(value = "findUsagePage")
    public Page<SealUsage> findUsagePage(@RequestBody SealUsageSearchVo searchVo) {
        return sealUsageService.findUsagePage(searchVo);
    }

    @PostMapping(value = "startSealUsage")
    public void startSealUsage(@RequestBody PmApprove approve) {
        sealUsageService.startSealUsage(approve);
    }

    @PostMapping(value = "findById")
    public  SealUsage findById(@RequestParam("id") Long id){
       return sealUsageService.findById(id);
    }

    @PostMapping(value = "findByContractId")
    List<SealUsage> findByContractId(@RequestParam("contractId") Long contractId){
        return sealUsageService.findByContractId(contractId);
    }

}

