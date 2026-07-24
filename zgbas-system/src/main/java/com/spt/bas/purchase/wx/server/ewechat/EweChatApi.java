package com.spt.bas.purchase.wx.server.ewechat;

import com.spt.bas.purchase.wx.server.cache.RedisCache;
import com.spt.bas.purchase.wx.server.config.EweChatConfig;
import com.spt.bas.purchase.wx.server.service.IBuyMessageService;
import com.spt.bas.purchase.wx.server.vo.EweChatAccessTokenCallBackDto;
import com.spt.bas.purchase.wx.server.vo.TemplateCardMessage;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.http.util.HTTPUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/1/11 09:34
 */
@Component
@Slf4j
public class EweChatApi {

    @Autowired
    private EweChatConfig eweChatConfig;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private IBuyMessageService messageService;


    /**
     * 获取企业access_tokenurl
     */
    private static final String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={0}&corpsecret={1}";

    /**
     * 发送消息url
     */
    private static final String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token={0}";

    /**
     * 缓存key
     */
    private static final String E_WE_ACCESS_TOKEN = "ewe:accessToken";

    /**
     * 询价消息
     */
    public static final String QUOTE_HTML = "<div class=\"normal\">品名：{0}</div><div class=\"normal\">数量：{1}</div><div class=\"normal\">账期：{2}</div><div class=\"normal\">送到日期：{3}</div><div class=\"normal\">失效日期：{4}</div><div class=\"gray\">{5}</div>";

    /**
     * 成交消息
     */
    public static final String DEAL_HTML = "<div class=\"normal\">品名：{0}</div><div class=\"normal\">数量：{1}</div><div class=\"normal\">价格：{2}</div><div class=\"normal\">账期：{3}</div><div class=\"normal\">送到日期：{4}</div><div class=\"normal\">确定日期：{5}</div><div class=\"gray\">{6}</div>";

    /**
     * 获取企业微信access_token
     * 该方法不对外公开
     *
     * @return access_token
     */
    private String getAccessTokenByEweChat() {
        String access_token_url = MessageFormat.format(ACCESS_TOKEN_URL, eweChatConfig.getCorpid(), eweChatConfig.getCorpsecret());
        String resultJson = null;
        try {
            resultJson = HTTPUtility.doGet(access_token_url);
        } catch (Exception e) {
            log.error("获取企业微信access_token失败！{}", e.toString());
        }
        if (StringUtils.isBlank(resultJson)) {
            log.error("获取企业微信access_token失败！返回结果为空");
        }
        log.info("获取企业微信access_token---{}", resultJson);
        EweChatAccessTokenCallBackDto eweChatAccessTokenCallBackDto = JsonUtil.json2Object(EweChatAccessTokenCallBackDto.class, resultJson);
        if (Objects.isNull(eweChatAccessTokenCallBackDto)) {
            log.error("获取企业微信access_token返回结果解析失败！");
        }
        if (eweChatAccessTokenCallBackDto.getErrcode() != 0) {
            log.error("获取企业微信access_token失败！错误码：{}，错误信息：{}", eweChatAccessTokenCallBackDto.getErrcode(), eweChatAccessTokenCallBackDto.getErrmsg());
        }
        // 存入缓存
        setRedisCache(eweChatAccessTokenCallBackDto.getAccess_token(), eweChatAccessTokenCallBackDto.getExpires_in());
        return eweChatAccessTokenCallBackDto.getAccess_token();
    }

    /**
     * 将access_token存入redis
     *
     * @param accessToken access_token
     */
    private void setRedisCache(String accessToken, Integer expires_in) {
        redisCache.setValue(E_WE_ACCESS_TOKEN, accessToken, expires_in);
    }

    /**
     * 获取企业微信access_token
     *
     * @return access_token
     */
    public String getAccessToken() {
        String accessToken = redisCache.getCacheObject(E_WE_ACCESS_TOKEN);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = getAccessTokenByEweChat();
        }
        return accessToken;
    }

    /**
     * 发送企业微信应用消息
     *
     * @param touser      用户
     * @param title       消息卡片标题
     * @param description 消息体
     * @param url         点击详情跳转地址
     */
    public void sendEWxMessage(String touser, String title, String description, String url) {
        TemplateCardMessage messageBody = new TemplateCardMessage();
        messageBody.setAgentid(eweChatConfig.getAgentid());
        messageBody.setTouser(touser);
        TemplateCardMessage.TextCard textCard = new TemplateCardMessage.TextCard();
        textCard.setTitle(title);
        textCard.setDescription(description);
        textCard.setUrl(url);
        messageBody.setTextcard(textCard);
        String postBody = JsonUtil.obj2Json(messageBody);
        String messageUrl = MessageFormat.format(SEND_MESSAGE_URL, getAccessToken());
        try {
            String json = HTTPUtility.doPost(messageUrl, postBody);
            log.info("发送企业微信消息---{}", json);
        } catch (Exception e) {
            log.error("发送企业微信消息失败！{}", e.toString());
            throw new RuntimeException(e);
        }
    }
}
