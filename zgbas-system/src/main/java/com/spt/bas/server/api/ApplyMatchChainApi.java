package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyMatchChain;
import com.spt.bas.server.service.IApplyMatchChainService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 代采赊销中间链条表
 * @Author: gaojy
 * @create 2022/9/14 10:44
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping(value = "apply/matchChain")
public class ApplyMatchChainApi extends BaseApi<ApplyMatchChain> {
    @Autowired
    private IApplyMatchChainService applyMatchChainService;

    @Override
    public IBaseService<ApplyMatchChain> getService() {
        return applyMatchChainService;
    }

    @PostMapping(value = "findMatchChains")
    public List<ApplyMatchChain> findMatchChains(@RequestBody Long applyMatchId){
        return applyMatchChainService.findMatchChains(applyMatchId);
    }
}
