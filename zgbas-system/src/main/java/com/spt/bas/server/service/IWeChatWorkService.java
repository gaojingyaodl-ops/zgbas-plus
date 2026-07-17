package com.spt.bas.server.service;


public interface IWeChatWorkService {

    /**
     * 推送业绩排行榜到企业微信
     */
    void pushWeChatWorkLeaderboard();

    /**
     * 推送客户开发业绩排行榜到企业微信
     */
    void pushWeChantWorkLeaderboardForCustomerDevelop();
}
