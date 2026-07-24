package com.spt.bas.purchase.wx.server.common;

/**
 * <p>
 *  常量
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 16:22
 */
public interface Constant {
    /**
     * 登录方式：手机
     */
    String LOGIN_TYPE_PHONE = "0";

    /**
     * 登录方式：验证码
     */
    String LOGIN_TYPE_CODE = "1";

    /**
     * 登录方式：微信授权登录
     */
    String LOGIN_TYPE_WX = "2";

    /**
     * 邀请码登录
     */
    String LOGIN_TYPE_INVITE_CODE = "3";

    /**
     * 数据库字段：有效
     */
    Boolean ENABLE_FLG = true;

    /**
     * 数据库字段：无效
     */
    Boolean DISABLE_FLG = false;

    /**
     * 短信类型 验证码
     */
    String SEND_SMS_TYPE_CHECK_CODE = "checkCode";

    /**
     * 短信类型 初始密码
     */
    String SEND_SMS_TYPE_INIT_PASSWORD = "initPassword";

    /**
     * 临时保存信息类型：基本信息类型
     */
    String TEMP_SAVE_TYPE_BASE_INFO= "0";

    /**
     * 临时保存信息类型：委托授权
     */
    String TEMP_SAVE_TYPE_ENTRUST= "1";

    /**
     * 临时保存信息类型：入金状态
     */
    String TEMP_SAVE_TYPE_DEPOSIT= "2";

    /**
     * 临时保存信息类型：申请白条
     */
    String TEMP_SAVE_TYPE_APPLY_IOU= "3";

    /**
     * 临时保存信息类型：白条服务费
     */
    String TEMP_SAVE_TYPE_SERVICE_FEE_FOR_IOU= "4";

    /**
     * 临时保存信息类型：cfca平台审核
     */
    String TEMP_SAVE_TYPE_CFCA_APPROVED= "5";

    /**
     * 临时保存信息类型：cfca费用支付
     */
    String TEMP_SAVE_TYPE_CFCA_PAY_FEE= "6";

    /**
     * 0：未开始
     */
    String APPLY_STATUS_NO_START = "0";

    /**
     * 1：审批中
     */
    String APPLY_STATUS_APPLYING = "1";

    /**
     * 2：未确认
     */
    String APPLY_STATUS_NO_CONFIRM = "2";

    /**
     * 3：审批驳回
     */
    String APPLY_STATUS_REJECT = "3";

    /**
     * 4：完成
     */
    String APPLY_STATUS_COMPLETE = "4";

    /**
     * 服务开通信息类型
     * 0：企业信息
     */
    String company_Apply_Status = "0";

    /**
     * 服务开通信息类型
     * 1：委托授权
     */
    String ENTRUST_APPLY_STATUS = "1";

    /**
     * 服务开通信息类型
     * 2:入金
     */
    String DEPOSIT_STATUS = "2";

    /**
     * 服务开通信息类型
     * 3:申请白条
     */
    String APPLY_IOU_STATUS = "3";

    /**
     * 服务开通信息类型
     * 4:白条服务费
     */
    String SERVICE_FEE_FOR_IOU_STATUS = "4";

    /**
     * 服务开通信息类型
     * 5:cfca平台审核
     */
    String CFCA_APPROVED_STATUS = "5";

    /**
     * 服务开通信息类型
     * 6:cfca费用支付
     */
    String CFCA_PAY_FEE_STATUS = "6";

    /**
     * 服务开通信息类型
     * 7:合伙人的申请状态
     */
    String PARTNER_APPLY_STATUS = "7";


    /**
     * ==================收款账户信息start======================
     */
    /**
     * 字典代码
     */
    String DICT_CODE_ACCOUNT_TYPE = "collectionAccount";
    String DICT_CODE_ACCOUNT_NAME = "accountName";
    String DICT_CODE_ACCOUNT_NUMBER = "accountNumber";
    String DICT_CODE_BANK = "bank";
    String DICT_CODE_REMARK = "remark";

    String DICT_CODE_SERVICE_ACCOUNT_TYPE = "serviceAccount";

    /**
     * ==================收款账户信息end======================
     */

    /**
     * 服务费金额
     */
    String DICT_SERVICE_FEE_PRICE_TYPE = "serviceFeePriceType";
    String DICT_SERVICE_FEE_PRICE_CODE = "serviceFeePriceCode";

    /**
     * cfca服务费
     */
    String DICT_CFCA_FEE_PRICE_TYPE = "cfcaFeePriceType";
    String DICT_CFCA_FEE_PRICE_CODE = "cfcaFeePriceCode";


    /**
     * ocr识别 身份证正面
     */
    String ID_CARD_SIDE_FACE = "0";
    /**
     * ocr识别 身份证反面
     */
    String ID_CARD_SIDE_BACK = "1";

    /**
     * 自定义额度 30万以内
     */
    String CUSTOM_QUOTA_0 = "0";

    /**
     * 自定义额度 30到100万
     */
    String CUSTOM_QUOTA_1 = "1";
    /**
     * 自定义额度 大于100万
     */
    String CUSTOM_QUOTA_2 = "2";

    /**
     * 意见反馈字典代码
     */
    String FEEDBACK_TYPE_DICT = "feedbackType";

}
