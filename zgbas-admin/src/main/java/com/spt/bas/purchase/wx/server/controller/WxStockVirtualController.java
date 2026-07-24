package com.spt.bas.purchase.wx.server.controller;

import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.report.client.remote.IRptStockVirtualReportClient;
import com.spt.bas.report.client.vo.RptWxStockVirtualSearchVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/8/6 09:59
 */
@RestController
@RequestMapping(value = "/wx/stock/virtual")
public class WxStockVirtualController {
    @Resource
    private IRptStockVirtualReportClient stockVirtualReportClient;


    @PostMapping("/getPage")
    public ApiResult getPage(@RequestBody RptWxStockVirtualSearchVo searchVo) {
        return ApiResult.ofSuccess(stockVirtualReportClient.getWxStockVirtualPage(searchVo));
    }
}
