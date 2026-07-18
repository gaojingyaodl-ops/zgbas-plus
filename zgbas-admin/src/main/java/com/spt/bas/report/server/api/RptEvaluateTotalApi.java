package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptEvaluateTotalSearch;
import com.spt.bas.report.client.vo.RptEvaluateTotalVo;
import com.spt.bas.report.server.service.IRptEvaluateTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/evaluate/total")
public class RptEvaluateTotalApi {
    @Autowired
    private IRptEvaluateTotalService evaluateTotalService;
    /**
     * 考评列表
     * @param vo
     * @return
     */
    @PostMapping("findPageEvaluateTotal")
    public Page<RptEvaluateTotalVo> findPageEvaluateTotal(@RequestBody RptEvaluateTotalSearch vo){
        Page<RptEvaluateTotalVo> pageEvaluateTotal = evaluateTotalService.findPageEvaluateTotal(vo);
        return pageEvaluateTotal;
    }
}
