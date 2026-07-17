package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptCtrContractUnDeliverySearchVo;
import com.spt.bas.report.client.vo.RptCtrContractUnDeliveryVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/unDelivery",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptCtrContractUnDeliveryClient {

    @PostMapping("findUnDeliveryPage")
    public PageDown<RptCtrContractUnDeliveryVo> findUnDeliveryPage(@RequestBody RptCtrContractUnDeliverySearchVo searchVo);
    
    
}
