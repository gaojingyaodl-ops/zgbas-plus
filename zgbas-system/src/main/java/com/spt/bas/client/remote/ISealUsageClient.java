package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.client.vo.SealUsageSearchVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/seal/usage", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)

public interface ISealUsageClient extends BaseClient<SealUsage> {

    @RequestMapping(value = "findUsagePage")
    PageDown<SealUsage> findUsagePage(@RequestBody SealUsageSearchVo searchVo);

    @PostMapping(value = "startSealUsage")
    void startSealUsage(@RequestBody PmApprove approve);

    @PostMapping(value = "findById")
    SealUsage findById(@RequestParam("id") Long id);

    @PostMapping(value = "findByContractId")
    List<SealUsage> findByContractId(@RequestParam("contractId") Long contractId);
}

