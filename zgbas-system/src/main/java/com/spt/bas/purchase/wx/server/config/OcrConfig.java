package com.spt.bas.purchase.wx.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *  ocr配置信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-22 14:30
 */
@Data
@ConfigurationProperties(prefix = "aliyun.ocr")
public class OcrConfig {

    private String appcode;
    private String host;

    /**
     * 营业执照
     */
    private String businessLicenseUrl;

    /**
     * 身份证
     */
    private String idCardUrl;

    /**
     * 行驶证
     */
    private String vehicleUrl;

    /**
     * 驾驶证
     */
    private String driverLicenseUrl;

    /**
     * 户口页
     */
    private String householdRegisterUrl;

    /**
     * 银行卡
     */
    private String bankCardUrl;

    /**
     * 护照
     */
    private String passportUrl;
}
