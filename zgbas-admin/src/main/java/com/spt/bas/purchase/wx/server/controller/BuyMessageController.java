package com.spt.bas.purchase.wx.server.controller;

import com.spt.bas.purchase.wx.client.vo.MessageSearchVo;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.service.IBuyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/21 14:05
 */
@RestController
@RequestMapping(value = "/ewechat/message")
public class BuyMessageController {

    @Autowired
    private IBuyMessageService buyMessageService;

    /**
     * 根据openId获取消息
     *
     * @param searchVo 查询参数
     * @return 结果
     */
    @PostMapping("/getMessage")
    private ApiResult getMessage(@RequestBody MessageSearchVo searchVo) {
        return buyMessageService.getMessage(searchVo);
    }

    /**
     * 全部已读
     *
     * @param searchVo 查询参数
     * @return 结果
     */
    @PostMapping("/allRead")
    private ApiResult allRead(@RequestBody MessageSearchVo searchVo) {
        buyMessageService.allRead(searchVo);
        return ApiResult.ofSuccess();
    }

    /**
     * 单个消息已读
     *
     * @param searchVo 查询参数
     * @return 结果
     */
    @PostMapping("/singleRead")
    private ApiResult singleRead(@RequestBody MessageSearchVo searchVo) {
        buyMessageService.singleRead(searchVo.getId());
        return ApiResult.ofSuccess();
    }

}
