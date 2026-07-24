package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.purchase.wx.client.entity.BuyQuote;
import com.spt.bas.purchase.wx.client.vo.BuyQuoteSearchVo;
import com.spt.bas.purchase.wx.server.common.ApiResult;

import java.util.List;
import java.util.Map;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/21 14:08
 */

public interface IBuyQuoteService {
    List<BuyQuote> getEffectiveMin(Long  enquiryId);

    void confirmDeal(BuyQuote buyQuote);

    ApiResult getQuoteSuccess(BuyQuoteSearchVo buyQuoteSearchVo);
}
