package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptStockVirtualSearchVo;
import com.spt.bas.report.client.vo.RptStockVirtualVo;
import com.spt.bas.report.client.vo.RptWxStockVirtualSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/stockReport/virtual",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptStockVirtualReportClient {

    /**
     * 展示合同模板信息分页
     * @param searchVo 查询参数
     */
    @PostMapping("findPageVo")
    PageDown<RptStockVirtualVo> getStockVirtualList(RptStockVirtualSearchVo searchVo);

    @PostMapping("getWxStockVirtualPage")
    PageDown<RptStockVirtualVo> getWxStockVirtualPage(RptWxStockVirtualSearchVo searchVo);
}
