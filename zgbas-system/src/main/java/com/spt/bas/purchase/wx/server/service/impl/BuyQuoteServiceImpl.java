package com.spt.bas.purchase.wx.server.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.purchase.wx.client.entity.BuyEnquiry;
import com.spt.bas.purchase.wx.client.entity.BuyMessage;
import com.spt.bas.purchase.wx.client.entity.BuyQuote;
import com.spt.bas.purchase.wx.client.vo.BuyQuoteSearchVo;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.dao.BuyEnquiryDao;
import com.spt.bas.purchase.wx.server.dao.BuyMessageDao;
import com.spt.bas.purchase.wx.server.dao.BuyQuoteDao;
import com.spt.bas.purchase.wx.server.enums.MessageEnums;
import com.spt.bas.purchase.wx.server.service.IBuyMessageService;
import com.spt.bas.purchase.wx.server.service.IBuyQuoteService;
import com.spt.bas.purchase.wx.server.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/21 14:09
 */
@Service
@Slf4j
public class BuyQuoteServiceImpl implements IBuyQuoteService {

    @Autowired
    private BuyQuoteDao buyQuoteDao;

    @Autowired
    private BuyMessageDao buyMessageDao;

    @Autowired
    private IBsCompanyClient iBsCompanyClient;

    @Autowired
    private IAuthOpenFacade iAuthOpenFacade;

    @Autowired
    private BuyEnquiryDao buyEnquiryDao;

    @Autowired
    private BuyEnquiryServiceImpl buyEnquiryService;
    @Autowired
    private IBuyMessageService messageService;

    /**
     * 客户只能看见有效的最低价
     * 相关送达日期，只显示最低报价
     */
    @Override
    public List<BuyQuote> getEffectiveMin(Long enquiryId) {
        //获取有效的报价信息,已确认成交、未成交且未到达失效日期，没有被删除的
        List<BuyQuote> effectiveMin = buyQuoteDao.getEffectiveMin(enquiryId);
        // 结果集
        List<BuyQuote> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 通过送达日期、吨数分类，获取其中的最低一个报价
        if (ObjectUtil.isNotEmpty(effectiveMin)) {
            // 获取业务人员手机号码，jpa关联字段有问题，询价那边使用对象集合报错，采用遍历赋值
            effectiveMin.forEach(it -> {
                SysUserSdk userById = iAuthOpenFacade.findUserById(Long.valueOf(it.getUserId().toString()));
                it.setPhone(userById.getPhonenumber());
            });
            // 日期格式化,只找同一天，没有时分秒的概念
            effectiveMin.stream()
                    .forEach(it -> {
                        try {
                            String format = sdf.format(it.getArriveDate());
                            it.setArriveDate(sdf.parse(format));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    });
            // 获取已经成交的数据
            List<BuyQuote> confirmed = effectiveMin.stream().filter(it -> it.getStatus().equals("1")).collect(Collectors.toList());
            result.addAll(confirmed);
            // 获取没有成交数据
            List<BuyQuote> unconfirmed = effectiveMin.stream().filter(it -> it.getStatus().equals("0")).collect(Collectors.toList());
            // 按送达日期分类
            Map<Date, List<BuyQuote>> arriveDateMap = unconfirmed.stream().collect(Collectors.groupingBy(BuyQuote::getArriveDate, Collectors.mapping(value -> value, Collectors.toList())));
            // 遍历送达日期map
            for (Date key : arriveDateMap.keySet()) {
                // 获取相同日期的报价
                List<BuyQuote> buyQuotes = arriveDateMap.get(key);
                // 再按照吨数数量分类
                Map<BigDecimal, List<BuyQuote>> dealGroup = buyQuotes.stream().collect(Collectors.groupingBy(BuyQuote::getDealNumber, Collectors.mapping(value -> value, Collectors.toList())));
                for (BigDecimal bigDecimal : dealGroup.keySet()) {
                    List<BuyQuote> bigDecimals = dealGroup.get(bigDecimal);
                    // 获取同一日期、同一数据量的最低价格
                    BuyQuote buyQuote = bigDecimals.stream()
                            .min(Comparator.comparing(BuyQuote::getSellPrice))
                            .orElse(null);
                    // 加入结果集中
                    result.add(buyQuote);
                }
            }
        }
        // 结果，按送达日期、吨数量升序

        // 使用 Comparator 定义排序规则
        Comparator<BuyQuote> comparator = Comparator
                .comparing(BuyQuote::getId);
//                .thenComparing(BuyQuote::getArriveDate)
//                .thenComparing(BuyQuote::getDealNumber);

        // 对列表进行排序
        result.sort(comparator);
        return result;
    }

    /**
     * 客户确认成交
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void confirmDeal(BuyQuote buyQuote) {
        // 获取对应的询价信息
        BuyEnquiry buyEnquiry = buyEnquiryService.findBuyEnquiryById(buyQuote.getEnquiryId());
        // 询价待成交吨数,判断询价待成交数量和确认成交数是否相同，相同询价状态改为已成交
        BigDecimal surplusNum = buyEnquiry.getSurplusNum();
        if (surplusNum.compareTo(buyQuote.getDealNumber()) == 0) {
            buyEnquiryDao.updateStatus(buyEnquiry.getId());
        }
        buyQuote.setConfirmTime(DateUtils.getNowDate());
        // 修改报价状态
        buyQuoteDao.confirmDeal(buyQuote.getId(), buyQuote.getConfirmTime());
        sendEweChatMessage(buyQuote, buyEnquiry);
    }

    @Override
    public ApiResult getQuoteSuccess(BuyQuoteSearchVo buyQuoteSearchVo) {
        Page<Map<String,Object>> page = buyQuoteDao.getQuoteSuccess(buyQuoteSearchVo.getCompanyName(),buyQuoteSearchVo.getOddNumber(), PageRequest.of(buyQuoteSearchVo.getPage() - 1, buyQuoteSearchVo.getRows()));
        return ApiResult.ofSuccess(page.getContent());
    }

    /**
     * 客户确认成交后微信小程序消息/企业微信消息
     *
     * @param buyQuote   报价消息
     * @param buyEnquiry 询价消息
     */
    public void sendEweChatMessage(BuyQuote buyQuote, BuyEnquiry buyEnquiry) {
        MessageEnums.DealMessage dealMessage = new MessageEnums.DealMessage.Builder()
                .enquiryOddNumber(buyEnquiry.getOddNumber())
                .oddNumber(buyQuote.getOddNumber())
                .enquiryId(buyEnquiry.getId())
                .productName(buyEnquiry.getProductName()+"/"+buyEnquiry.getBrandNumber()+"/"+buyEnquiry.getFactoryName())
                .dealNumber(buyQuote.getDealNumber())
                .sellPrice(buyQuote.getSellPrice())
                .paymentDays(buyEnquiry.getPaymentDays())
                .arriveDate(buyQuote.getArriveDate())
                .confirmTime(buyQuote.getConfirmTime())
                .build();
        log.info("成交消息--{}", dealMessage.toString());
        // 发送微信小程序消息
        messageService.sendWxDealMessage(dealMessage, buyEnquiry.getOpenId());
        // 发送企业微信消息
        messageService.sendEWxDealMessage(dealMessage, buyQuote.getUserId());
    }
}
