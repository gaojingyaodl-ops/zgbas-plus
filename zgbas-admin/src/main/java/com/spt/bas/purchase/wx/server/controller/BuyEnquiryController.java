package com.spt.bas.purchase.wx.server.controller;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.remote.IBasBrandClient;
import com.spt.bas.client.remote.IBsFactoryClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.purchase.wx.client.entity.BuyEnquiry;
import com.spt.bas.purchase.wx.client.vo.BuyEnquirySearchVo;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.service.IBuyEnquiryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/21 14:05
 */
@RestController
@RequestMapping(value = "/ewechat/buyEnquiry")
public class BuyEnquiryController {

    @Autowired
    private IBuyEnquiryService buyEnquiryService;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBasBrandClient brandClient;
    @Autowired
    private IBsFactoryClient factoryClient;

    @ApiOperation(value = "获取商品树")
    @PostMapping("/getProductTree")
    @ApiOperationSupport(order = 1)
    public ApiResult getProductTree() {
        return ApiResult.ofSuccess(productTypeClient.findAllProductTree(BasConstants.ZG_ENTERPRISE_ID));
    }

    @ApiOperation(value = "获取牌号列表")
    @PostMapping("/getBrandList")
    @ApiOperationSupport(order = 2)
    public ApiResult getBrandList() {
        return ApiResult.ofSuccess(brandClient.findAll());
    }

    @ApiOperation(value = "获取厂商列表")
    @PostMapping("/getFactoryList")
    @ApiOperationSupport(order = 3)
    public ApiResult getFactoryList() {
        return ApiResult.ofSuccess(factoryClient.findByEnterpriseId(BasConstants.ZG_ENTERPRISE_ID));
    }

    @ApiOperation(value = "新增询价")
    @PostMapping("/addBuyEnquiry")
    @ApiOperationSupport(order = 4)
    public ApiResult addBuyEnquiry(@RequestBody BuyEnquiry buyEnquiry) {
        buyEnquiry.setEnquiryTime(new Date());
        Map<String, Long> result = new HashMap<>(1);
        Long enquiryId = buyEnquiryService.saveBuyEnquiry(buyEnquiry);
        result.put("id", enquiryId);
        return ApiResult.ofSuccess(result);
    }

    @ApiOperation(value = "获取询价列表")
    @PostMapping("/getBuyEnquiryList")
    @ApiOperationSupport(order = 4)
    public ApiResult getBuyEnquiryList(@RequestBody BuyEnquirySearchVo searchVo) {
        return ApiResult.ofSuccess(buyEnquiryService.findBuyEnquiryList(searchVo));
    }

    @ApiOperation(value = "获取询价详情")
    @PostMapping("/findBuyEnquiryById")
    @ApiOperationSupport(order = 5)
    public ApiResult findBuyEnquiryById(@RequestBody BuyEnquirySearchVo searchVo) {
        return ApiResult.ofSuccess(buyEnquiryService.findBuyEnquiryById(searchVo.getId()));
    }

    @ApiOperation(value = "修改询价")
    @PostMapping("/updateBuyEnquiry")
    @ApiOperationSupport(order = 6)
    public ApiResult updateBuyEnquiry(@RequestBody BuyEnquiry buyEnquiry) {
        buyEnquiry.setEnquiryTime(new Date());
        Map<String, Long> result = new HashMap<>(1);
        Long enquiryId = buyEnquiryService.saveBuyEnquiry(buyEnquiry);
        result.put("id", enquiryId);
        return ApiResult.ofSuccess(result);
    }

    @ApiOperation(value = "修改询价")
    @PostMapping("/deleteBuyEnquiry")
    @ApiOperationSupport(order = 7)
    public ApiResult deleteBuyEnquiry(@RequestBody BuyEnquiry buyEnquiry) {
        buyEnquiryService.deleteBuyEnquiry(buyEnquiry);
        return ApiResult.ofSuccess();
    }


}
