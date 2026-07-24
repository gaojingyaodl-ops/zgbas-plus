package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.purchase.wx.client.entity.BuyMessage;
import com.spt.bas.purchase.wx.client.vo.MessageSearchVo;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.enums.MessageEnums;

import java.util.List;
import java.util.Set;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/21 14:08
 */

public interface IBuyMessageService {

    /**
     * 获取消息
     *
     * @param searchVo 查询参数
     * @return 结果
     */
    ApiResult getMessage(MessageSearchVo searchVo);

    /**
     * 保存报价消息
     *
     * @param quoteMessage 报价消息
     * @param openId       微信小程序openId
     */
    BuyMessage sendQuoteMessage(MessageEnums.QuoteMessage quoteMessage, String openId);

    /**
     * 发送微信小程序成交消息
     *
     * @param dealMessage 成交消息
     * @param openId      微信小程序openId
     */
    BuyMessage sendWxDealMessage(MessageEnums.DealMessage dealMessage, String openId);

    /**
     * 发送企业微信成交消息
     *
     * @param dealMessage 成交消息
     * @param userId   客户id
     */
    BuyMessage sendEWxDealMessage(MessageEnums.DealMessage dealMessage, Long userId);

    /**
     * 保存系统消息
     *
     * @param systemMessage 系统消息
     * @param openId        微信小程序openId
     */
    BuyMessage sendSystemMessage(MessageEnums.SystemMessage systemMessage, String openId);

    /**
     * 保存系统消息
     *
     * @param enquiryMessage 询价消息
     * @param pushList      客户id
     */
    List<BuyMessage> sendEWxEnquiryMessage(MessageEnums.EnquiryMessage enquiryMessage, Set<String> pushList,String title);


    void allRead(MessageSearchVo searchVo);

    void singleRead(Long id);
}
