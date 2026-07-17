package com.spt.bas.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * *    java配置的优先级低于yml配置；如果yml配置不存在，会采用java配置
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-06 14:16
 */
@Data
@ConfigurationProperties(prefix = "picc.config")
public class BasPiccConfig {
    /**
     * 限额url
     */
    private String quotaInUrl;

    /**
     * 上传附件url
     */
    private String uploadFileUrl;

    private String chnlcode;

    /**
     * 赊销申请url
     */
    private String sxUrl;
    private String sxAuthcode;

    /**
     * 按金额回款申请url
     */
    private String recoveryUrl;
    private String recoveryAuthcode;

    /**
     * 申报状态查询
     */
    private String queryUrl;
    private String queryAuthcode;

    /**
     * 按条件回款申请url
     */
    private String declarerRecoveryUrl;
    private String declarerRecoveryAuthcode;

    /**
     * 赊销剩余额度查询接口
     */
    private String queryAvailableUrl;

    /**
     * 变更发票url
     */
    private String changeInvoiceUrl;


}
