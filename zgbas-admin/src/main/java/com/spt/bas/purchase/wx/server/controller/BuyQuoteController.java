package com.spt.bas.purchase.wx.server.controller;

import com.spt.bas.purchase.wx.client.entity.BuyEnquiry;
import com.spt.bas.purchase.wx.client.entity.BuyQuote;
import com.spt.bas.purchase.wx.client.vo.BuyQuoteSearchVo;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.service.IBuyQuoteService;
import com.spt.bas.purchase.wx.server.util.UserHelper;
import com.spt.bas.report.client.entity.RptCtrContractSearch;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/21 14:05
 */
@RestController
@RequestMapping(value = "/ewechat/buyQuote")
public class BuyQuoteController {

    @Autowired
    private IBuyQuoteService buyQuoteService;

    /**
     * 客户只能看见有效的最低价
     * 相关送达日期，只显示最低报价
     *
     * @param buyEnquiry
     * @return
     */
    @ApiOperation(value = "客户获取有效的最低报价")
    @PostMapping("/getEffectiveMin")
    public ApiResult getEffectiveMin(@RequestBody BuyEnquiry buyEnquiry) {
        return ApiResult.ofSuccess(buyQuoteService.getEffectiveMin(buyEnquiry.getId()));
    }

    /**
     * 根据报价ID修改状态为已成交
     * 自动给“领用业务员和报价业务员”发送确认信息
     *
     * @param buyQuote
     * @return
     */
    @ApiOperation(value = "确认成交")
    @PostMapping("/confirmDeal")
    @ApiOperationSupport(order = 2)
    public ApiResult confirmDeal(@RequestBody BuyQuote buyQuote) {
        // 修改状态
        buyQuoteService.confirmDeal(buyQuote);
        return ApiResult.ofSuccess();
    }

    /**
     * 获取客户询价成交信息
     * @param buyQuoteSearchVo
     * @return
     */
    @ApiOperation(value = "获取客户询价成交信息")
    @PostMapping("/getQuoteSuccess")
    public ApiResult getQuoteSuccess(@RequestBody BuyQuoteSearchVo buyQuoteSearchVo) {
        return buyQuoteService.getQuoteSuccess(buyQuoteSearchVo);
    }
}
