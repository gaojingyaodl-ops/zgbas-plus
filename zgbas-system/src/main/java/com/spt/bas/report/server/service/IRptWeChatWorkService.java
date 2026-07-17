package com.spt.bas.report.server.service;

import com.spt.bas.client.vo.WeChatWorkVo;

public interface IRptWeChatWorkService {

    /**
     * 推送业绩排行榜到企业微信
     */
    void pushWeChatWorkLeaderboard(WeChatWorkVo vo);

    /**
     * 推送客户开发部业绩排行榜到企业微信
     */
    void pushWeChantWorkLeaderboardForCustomerDevelop(WeChatWorkVo vo);
}
