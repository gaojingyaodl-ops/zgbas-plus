package com.spt.bas.purchase.wx.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/1/11 11:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateCardMessage {
    private String touser;
    private String toparty;
    private String totag;
    private String msgtype = "textcard";
    private Integer agentid;
    private TextCard textcard;
    private Integer enableIdTrans;
    private Integer enableDuplicateCheck;
    private Integer duplicateCheckInterval;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TextCard {
        private String title;
        private String description;
        private String url;
        private String btntxt;
    }
}
