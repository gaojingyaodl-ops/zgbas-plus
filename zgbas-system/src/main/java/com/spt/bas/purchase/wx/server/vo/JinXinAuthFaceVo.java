package com.spt.bas.purchase.wx.server.vo;

import com.spt.bas.client.entity.SyncData;
import lombok.Data;

/**
 * 金信-静默活体识别Vo
 * @Author: gaojy
 * @create 2022/1/17 9:28
 * @version: 1.0
 * @description:
 */
@Data
public class JinXinAuthFaceVo {

    /**
     * 商户订单号
     */
    private String dsorderid;

    /**
     * 商户号
     */
    private String merchno;

    /**
     * 交易码
     */
    private String transcode;

    /**
     * 商户请求流水号
     */
    private String ordersn;

    /**
     * 平台交易流水号
     */
    private String orderid;

    /**
     * 加密校验值
     */
    private String sign;

    /**
     * 平台返回码
     */
    private String platformCode;

    /**
     * 平台返回信息
     */
    private String platformDesc;

    /**
     * 查询结果数据 需要使用RSA解密
     */
    private String data;

    private SyncData syncData;
}
