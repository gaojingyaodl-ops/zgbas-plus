package com.spt.bas.purchase.wx.server.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.purchase.wx.client.entity.BuyMessage;
import com.spt.bas.purchase.wx.client.vo.MessageSearchVo;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.config.WxConfiguration;
import com.spt.bas.purchase.wx.server.dao.BuyMessageDao;
import com.spt.bas.purchase.wx.server.enums.MessageEnums;
import com.spt.bas.purchase.wx.server.ewechat.EweChatApi;
import com.spt.bas.purchase.wx.server.service.IBuyMessageService;
import com.spt.bas.purchase.wx.server.util.DateUtils;
import com.spt.bas.purchase.wx.server.util.NumUtils;
import com.spt.bas.purchase.wx.server.util.StrUtils;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;

/**
 * 消息业务逻辑类
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/21 14:09
 */
@Service
@Slf4j
public class BuyMessageServiceImpl extends BaseService<BuyMessage> implements IBuyMessageService {

    @Autowired
    private BuyMessageDao buyMessageDao;

    @Value("${wx.miniapp.msg.template_quote}")
    private String templateQuote;

    @Value("${wx.miniapp.msg.template_deal}")
    private String templateDeal;

    @Value("${wx.miniapp.msg.template_system}")
    private String templateSystem;
    @Autowired
    private EweChatApi eweChatApi;
    @Autowired
    private IBsCompanyClient bsCompanyClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Value("${ewechat.config.messageUrl}")
    private String messageUrl;
    @Value("${spt.app.secretKey}")
    private String secretKey;


    /**
     * 获取消息
     *
     * @param searchVo 查询参数
     * @return 结果
     */
    @Override
    public ApiResult getMessage(MessageSearchVo searchVo) {
        Page<BuyMessage> page = buyMessageDao.getMessagePage(searchVo.getMessageType(), searchVo.getOpenId(), PageRequest.of(searchVo.getPage() - 1, searchVo.getRows()));
        return ApiResult.ofSuccess(page);
    }

    @Override
    public BaseDao<BuyMessage> getBaseDao() {
        return buyMessageDao;
    }


    /**
     * 保存报价消息
     *
     * @param quoteMessage 报价消息
     * @param openId       微信小程序openId
     */
    @Override
    public BuyMessage sendQuoteMessage(MessageEnums.QuoteMessage quoteMessage, String openId) {
        BuyMessage buyMessage = MessageEnums.QUOTED_MESSAGE.buildQuotedMessage(quoteMessage, openId);
        List<WxMaSubscribeMessage.Data> subscribeDataList = new ArrayList<>();
        subscribeDataList.add(new WxMaSubscribeMessage.Data("thing12", quoteMessage.getProductName()));
        subscribeDataList.add(new WxMaSubscribeMessage.Data("amount4", quoteMessage.getProductName()));
        subscribeDataList.add(new WxMaSubscribeMessage.Data("time11", quoteMessage.getProductName()));
        subscribeDataList.add(new WxMaSubscribeMessage.Data("time3", quoteMessage.getProductName()));
        subscribeDataList.add(new WxMaSubscribeMessage.Data("thing5", quoteMessage.getProductName()));
        sendMessage(openId, templateQuote, "pages/index/index", subscribeDataList);
        return buyMessageDao.save(buyMessage);
    }

    /**
     * 发送消息
     *
     * @param openId            openId
     * @param templateId        模板 id
     * @param page              页面路径
     * @param subscribeDataList 消息
     */
    private void sendMessage(String openId, String templateId, String page, List<WxMaSubscribeMessage.Data> subscribeDataList) {
        final WxMaService wxService = WxConfiguration.getMaService();
        WxMaSubscribeMessage message = new WxMaSubscribeMessage();
        message.setTemplateId(templateId);
        message.setToUser(openId);
        //message.setPage(page);
        message.setData(subscribeDataList);
        try {
            wxService.getMsgService().sendSubscribeMsg(message);
        } catch (WxErrorException e) {
            logger.error("发送消息失败-{}", e.toString());
            //throw new RuntimeException(e);
        }
    }

    /**
     * 保存成交消息
     *
     * @param dealMessage 成交消息
     * @param openId      微信小程序openId
     */
    @Override
    public BuyMessage sendWxDealMessage(MessageEnums.DealMessage dealMessage, String openId) {
        BuyMessage message = MessageEnums.DEAL_MESSAGE.buildDealMessage(dealMessage, openId);
        List<WxMaSubscribeMessage.Data> subscribeDataList = new ArrayList<>();
        String productName = dealMessage.getProductName();
        if (StringUtils.isNotBlank(productName) && productName.length() > 20) {
            productName = productName.substring(0, 20);
        }
        subscribeDataList.add(new WxMaSubscribeMessage.Data("thing12", StrUtils.toPlaceholder(productName)));
        subscribeDataList.add(new WxMaSubscribeMessage.Data("amount4", StrUtils.toPlaceholder(NumUtils.toStr(dealMessage.getSellPrice()))));
        subscribeDataList.add(new WxMaSubscribeMessage.Data("time11", StrUtils.toPlaceholder(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, dealMessage.getConfirmTime()))));
        subscribeDataList.add(new WxMaSubscribeMessage.Data("time3", StrUtils.toPlaceholder(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, dealMessage.getArriveDate()))));
        subscribeDataList.add(new WxMaSubscribeMessage.Data("thing5", "成交消息"));
        HashMap<String, Object> param = new HashMap<>();
        param.put("id", dealMessage.getEnquiryId());
        String form = JsonUtil.obj2Json(param);
        String page = "/pages/buyQuote/main?form=" + form;
        sendMessage(openId, templateDeal, page, subscribeDataList);
        return buyMessageDao.save(message);
    }

    /**
     * 发送企业微信成交消息
     *
     * @param dealMessage 成交消息
     * @param userId      客户id
     * @return 结果
     */
    public BuyMessage sendEWxDealMessage(MessageEnums.DealMessage dealMessage, Long userId) {

        String description = MessageFormat.format(EweChatApi.DEAL_HTML,
                StrUtils.toPlaceholder(dealMessage.getProductName()),// 品名
                StrUtils.toPlaceholder(NumUtils.toStr(dealMessage.getDealNumber())),// 数量
                StrUtils.toPlaceholder(NumUtils.toStr(dealMessage.getSellPrice())),// 销售价格
                StrUtils.toPlaceholder(dealMessage.getPaymentDays() + ""),// 账期
                StrUtils.toPlaceholder(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, dealMessage.getArriveDate())),// 送达日期
                StrUtils.toPlaceholder(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, dealMessage.getConfirmTime())),// 确认日期
                StrUtils.toPlaceholder(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getNowDate())));// 消息时间
        SysUserSdk userById = authOpenFacade.findUserById(userId);
        String matchUserId = userById.getUserName();
        if (StringUtils.isNotBlank(matchUserId)) {
            eweChatApi.sendEWxMessage(matchUserId, "成交消息", description, messageUrl);
        } else {
            logger.error("发送企业微信询价消息失败，未找到客户匹配业务员,客户id---{}", userId);
        }
        // 构建消息，落库
        BuyMessage message = MessageEnums.DEAL_MESSAGE.buildDealMessage(dealMessage, matchUserId);
        return buyMessageDao.save(message);
    }

    /**
     * 保存系统消息
     *
     * @param systemMessage 系统消息
     * @param openId        微信小程序openId
     */
    @Override
    public BuyMessage sendSystemMessage(MessageEnums.SystemMessage systemMessage, String openId) {
        BuyMessage message = MessageEnums.SYSTEM_MESSAGE.buildSystemMessage(systemMessage, openId);
        List<WxMaSubscribeMessage.Data> subscribeDataList = new ArrayList<>();
        subscribeDataList.add(new WxMaSubscribeMessage.Data("thing12", systemMessage.getSystemMessage()));
        subscribeDataList.add(new WxMaSubscribeMessage.Data("thing5", systemMessage.getRemark()));
        sendMessage(openId, templateSystem, "pages/index/index", subscribeDataList);
        return buyMessageDao.save(message);
    }


    /**
     * 发送企业微信询价消息
     *
     * @param enquiryMessage 询价消息
     * @param pushList       推送列表
     * @return 结果
     */
    @Override
    public List<BuyMessage> sendEWxEnquiryMessage(MessageEnums.EnquiryMessage enquiryMessage, Set<String> pushList,String title) {
        String description = MessageFormat.format(EweChatApi.QUOTE_HTML,
                StrUtils.toPlaceholder(enquiryMessage.getProductName()),// 品名
                StrUtils.toPlaceholder(NumUtils.toStr(enquiryMessage.getDealNumber())),// 数量
                StrUtils.toPlaceholder(enquiryMessage.getPaymentDays() + ""),// 账期
                StrUtils.toPlaceholder(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, enquiryMessage.getArriveDate())),// 送达日期
                StrUtils.toPlaceholder(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, enquiryMessage.getExpireTime())),// 失效日期
                StrUtils.toPlaceholder(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getNowDate())));// 消息时间
        List<BuyMessage> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(pushList)) {
            String touser = String.join("|", pushList);
            // 发送企业微信自建应用消息
            eweChatApi.sendEWxMessage(touser, title, description, messageUrl);
            // 构建消息，落库
            for (String loginName : pushList) {
                BuyMessage message = MessageEnums.ENQUIRY_MESSAGE.buildEnquiryMessage(enquiryMessage, loginName);
                buyMessageDao.save(message);
                result.add(message);
            }
        } else {
            logger.error("发送企业微信询价消息失败，推送消息列表为空");
        }
        return result;
    }

    /**
     * 全部已读
     *
     * @param searchVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void allRead(MessageSearchVo searchVo) {
        if (StringUtils.isNotBlank(searchVo.getMessageType())) {
            buyMessageDao.allRead(searchVo.getOpenId(), searchVo.getMessageType());
        } else {
            buyMessageDao.allReadByOpenId(searchVo.getOpenId());
        }

    }

    /**
     * 单个消息已读
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void singleRead(Long id) {
        buyMessageDao.singleRead(id);
    }

    /**
     * 获取领用业务员
     *
     * @param companyId 客户id
     * @return 领用业务员LoginName
     */
    public String getMatchUserIds(Long companyId) {
        BsCompany company = bsCompanyClient.findCompany(companyId);
        if (Objects.isNull(company)) {
            return null;
        }
        SysUserSdk user = authOpenFacade.findUserById(company.getMatchUserId());
        if (Objects.nonNull(user)) {
            return user.getUserName();
        }
        return null;
    }
}
