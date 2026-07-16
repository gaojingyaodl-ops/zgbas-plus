package com.spt.bas.client.constant;

/**
 * <p>
 * 人保对接常量
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-03-01 11:20
 */
public interface BasPiccConstant {


    /**
     * 默认超时时间 30s
     */
    int DEFAULT_TIMEOUT = 30 * 1000;

    /**
     * 企业内部限额唯一标识[即流水号]
     */
    String CORP_SERIAL_NO_CODE = "corpSerialNo";

    /**
     * 人保接口返回状态 默认
     */
    String PICC_SYNC_STATUS_DEFAULT = "0";

    /**
     * 人保接口返回状态 成功
     */
    String PICC_SYNC_STATUS_SUCCESS = "1";

    /**
     * 成功字段（picc返回）
     */
    String SUCCESS_FROM_PICC = "Success";


    /**
     * 发送不成功
     */
    String PICC_APPLYSTATUS_0 = "0";

    /**
     * 资信调查 已发送 流程中
     */
    String PICC_APPLYSTATUS_1 = "1";

    /**
     * 审批通过
     */
    String PICC_APPLYSTATUS_2 = "2";

    /**
     * 审批未通过 未受理
     */
    String PICC_APPLYSTATUS_3 = "3";

    /**
     * 暂存
     */
    String PICC_APPLYSTATUS_5 = "5";

    /**
     * picc return xml
     */
    String XML_ATTR_BuyerCodeApprove = "<BuyerCodeApprove>";
    String XML_ATTR_MMessage = "<MMessage>";

    /**
     * 非限额  1：自行掌握限额
     */
    String PICC_XML_LIMITFLAG_1 = "1";

    /**
     * 限额
     */
    String PICC_XML_LIMITFLAG_0 = "0";

    /**
     * 非限额 自控
     */
    String PICC_XML_request_type_LS = "LS04";

    /**
     * 限额   申请通过 非自控
     */
    String PICC_XML_request_type_SA = "SA04";

    /**
     * 使用方系统编号
     */
    String PICC_XML_SENDER = "NBZG";

    /**
     * 商品类别
     */
    String PICC_EXPORT_TRADE = "16";

    /**
     * 商品类别说明
     */
    String PICC_EXPORT_TRADE_INPUT = "塑料";

    /**
     * 发送失败
     */
    String PICC_PUSH_DATA_0 = "0";

    /**
     * 发送成功
     */
    String PICC_PUSH_DATA_1 = "1";

    //发送状态 1 人保发送成功 ，2人保 发送失败 ，3 回款正常， 4 回款失败
    /**
     * 赊销申请 发送人保 成功
     */
    String PICC_SEND_CODE_1 = "1";
    /**
     * 赊销申请 发送人保 失败
     */
    String PICC_SEND_CODE_2 = "2";

    /**
     * 回款正常
     */
    String PICC_SEND_CODE_3 = "3";

    /**
     * 回款失败
     */
    String PICC_SEND_CODE_4 = "4";

    /**
     * 批复状态 1-已批复
     */
    String PICC_APPROVE_FLAG_1 = "1" ;

    /**
     * 批复状态 2-审核中
     */
    String PICC_APPROVE_FLAG_2 = "2" ;

    /**
     * 批复状态 3-退回客户端
     */
    String PICC_APPROVE_FLAG_3 = "3" ;

    /**
     * 批复状态 4-退回客户端不予受理
     */
    String PICC_APPROVE_FLAG_4 = "4" ;



    String RECEIVER = "PICC";

    /**
     * picc人保字典category
     */
    String PICC_DICT_CATEGORY = "piccConfigParam";

    String PICC_DICT_ISSENDPICC = "isSendPicc";
    String PICC_DICT_BUSSINESSNO = "bussinessNo";
    String PICC_DICT_INSUREDPICCCODE = "insuredPiccCode";
    String PICC_DICT_INSUREDNAME = "insuredName";
    String PICC_DICT_SENDER = "sender";
    String PICC_DICT_TEST_FLG = "testFlg";

    /**
     * 人保可用额度操作 加
     */
    String CREDIT_OPTION_ADD = "add";
    /**
     * 人保可用额度操作 减
     */
    String CREDIT_OPTION_SUBTRACT = "subtract";


    /**
     * 赊销申请
     */
    String PICC_LOG_REQUEST_TYPE_0 = "0";

    /**
     * 回款申请
     */
    String PICC_LOG_REQUEST_TYPE_1 = "1";

    /**
     * 查询申请
     */
    String PICC_LOG_REQUEST_TYPE_2 = "2";

    /**
     * 失败
     */
    String PICC_LOG_STATUS_0 = "0";

    /**
     * 成功
     */
    String PICC_LOG_STATUS_1 = "1";

    /**
     * 人保保险费用计算状态 未计算
     */
    String PICC_INSURANCE_COMPUTE_STATUS_0 = "0";

    /**
     * 人保保险费用计算状态 已计算
     */
    String PICC_INSURANCE_COMPUTE_STATUS_1 = "1";

    /**
     * 未申报
     */
    String PICC_DECLARE_STATUS_0 = "0";
    /**
     * 已申报
     */
    String PICC_DECLARE_STATUS_1 = "1";
    /**
     * 申报成功
     */
    String PICC_DECLARE_STATUS_2 = "2";
    /**
     * 申报驳回
     */
    String PICC_DECLARE_STATUS_3 = "3";
    /**
     * 已回款
     */
    String PICC_DECLARE_STATUS_4 = "4";

    Integer DECLARE_RECOVER_STATUS_0 = 0;

}
