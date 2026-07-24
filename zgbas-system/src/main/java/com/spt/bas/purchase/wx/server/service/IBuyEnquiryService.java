package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.purchase.wx.client.entity.BuyEnquiry;
import com.spt.bas.purchase.wx.client.vo.BuyEnquirySearchVo;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/21 14:08
 */

public interface IBuyEnquiryService {

    Long saveBuyEnquiry(BuyEnquiry buyEnquiry);

    List<BuyEnquiry> findBuyEnquiryList(BuyEnquirySearchVo searchVo);

    BuyEnquiry findBuyEnquiryById(Long id);

    Long updateBuyEnquiry(BuyEnquiry buyEnquiry);

    void deleteBuyEnquiry(BuyEnquiry buyEnquiry);

    BigDecimal getTransactionNum(Long enquiryId);
}
