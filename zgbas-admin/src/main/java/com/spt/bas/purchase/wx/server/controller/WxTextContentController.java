package com.spt.bas.purchase.wx.server.controller;

import com.spt.bas.client.entity.BsTextContent;
import com.spt.bas.client.remote.IBsTextContentClient;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.entity.WxUserTextRead;
import com.spt.bas.purchase.wx.server.service.IWxUserTextReadService;
import com.spt.bas.purchase.wx.server.util.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping(value = "/wx/content")
public class WxTextContentController {

    @Autowired
    private IBsTextContentClient bsTextContentClient;

    @Autowired
    private IWxUserTextReadService wxUserTextReadService;

    @PostMapping("/getTextContentDetail")
    public ApiResult getTextContentDetail(@RequestBody BsTextContent bsTextContent) {
        Long userId = UserHelper.getCurUserId();
        WxUserTextRead wxUserTextRead = wxUserTextReadService.findByUserIdAndTextType(userId, bsTextContent.getTextType());
        if (Objects.nonNull(wxUserTextRead)) {
            wxUserTextRead.setReadTime(new Date());
            wxUserTextReadService.saveWxUserTextRead(wxUserTextRead);
        } else {
            WxUserTextRead wxUserTextRead1 = new WxUserTextRead();
            wxUserTextRead1.setUserId(userId);
            wxUserTextRead1.setTextType(bsTextContent.getTextType());
            wxUserTextRead1.setReadTime(new Date());
            wxUserTextReadService.saveWxUserTextRead(wxUserTextRead1);
        }
        return ApiResult.ofSuccess(bsTextContentClient.findNewTextContent(bsTextContent.getTextType()));
    }
}
