package com.spt.bas.report.server.api;


import com.spt.bas.report.client.entity.RptApplyBusinessPayVo;
import com.spt.bas.report.server.service.IRptApplyBusinessPayService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping(value = "/rpt/businessPay")
public class RptApplyBusinessPayApi {

    @Autowired
    private IRptApplyBusinessPayService applyBusinessPayService;

    @PostMapping("findPageContract")
    public Page<RptApplyBusinessPayVo> findPageContract(@RequestBody RptApplyBusinessPayVo searchVo){
        return applyBusinessPayService.findPageContract(searchVo);
    }

    @PostMapping("selectUserEvectionCost")
    public List<RptApplyBusinessPayVo> selectUserEvectionCost(@RequestBody String baseDate){
        return applyBusinessPayService.selectUserEvectionCost(baseDate);
    }

}
