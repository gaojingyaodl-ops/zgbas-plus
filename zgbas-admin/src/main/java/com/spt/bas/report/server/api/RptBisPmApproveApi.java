package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptBisSearchVo;
import com.spt.bas.report.client.vo.RptCtrContractSettlementDateSearchVo;
import com.spt.bas.report.client.vo.RptCtrContractSettlementDateVo;
import com.spt.bas.report.server.service.IRptBisPmApproveService;
import com.spt.tools.core.bean.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/11/8 10:09
 */
@RestController
@RequestMapping(value = "/bisPmApprove")
public class RptBisPmApproveApi {

    @Autowired
    private IRptBisPmApproveService bisPmApproveService;

    /**
     * 中光业务系统查询相关审批单数据接口
     *
     * @param searchVo 查询参数
     * @return 结果
     */
    @PostMapping("/getPmApproveByUserId")
    public RespVo<?> getPmApproveByUserId(@RequestBody RptBisSearchVo searchVo) {
        return bisPmApproveService.getPmApproveByUserId(searchVo);
    }

    @PostMapping("/getSettlementBusinessDate")
    List<RptCtrContractSettlementDateVo> getSettlementBusinessDate(@RequestBody RptCtrContractSettlementDateSearchVo searchVo){
        return bisPmApproveService.getSettlementBusinessDate(searchVo);
    }
}
