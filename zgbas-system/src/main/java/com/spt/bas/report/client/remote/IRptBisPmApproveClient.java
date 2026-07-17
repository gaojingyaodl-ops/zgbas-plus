package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptBisSearchVo;
import com.spt.bas.report.client.vo.RptCtrContractSettlementDateSearchVo;
import com.spt.bas.report.client.vo.RptCtrContractSettlementDateVo;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:52
 */
@FeignClient(name = ReportConstant.SERVER_NAME, path = ReportConstant.SERVER_NAME + "/bisPmApprove", url = ReportConstant.SERVER_URL, configuration = FeignConfig.class)
public interface IRptBisPmApproveClient {

    /**
     * 中光业务系统查询相关审批单数据接口
     *
     * @param searchVo 查询参数
     * @return 结果
     */
    @PostMapping("/getPmApproveByUserId")
    RespVo<?> getPmApproveByUserId(@RequestBody RptBisSearchVo searchVo);

    @PostMapping("/getSettlementBusinessDate")
    List<RptCtrContractSettlementDateVo> getSettlementBusinessDate(@RequestBody RptCtrContractSettlementDateSearchVo searchVo);
}
