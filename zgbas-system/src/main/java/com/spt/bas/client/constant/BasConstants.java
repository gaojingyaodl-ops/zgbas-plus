package com.spt.bas.client.constant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BasConstants {
    String APP_CODE = "bas";
    /**
     * 中光的数据appcode
     */
    String ZGBAS_APP_CODE = "zgbas";
    String SERVER_NAME = "spt-bas-server";
    // 中光亿云id
    Long ZG_ENTERPRISE_ID = 44L;
    /**
     * 指定服务地址，默认为空字符串
     */
    String SERVER_BEAN_NAME = "basServerConfig";
    String SERVER_URL = "#{" + SERVER_BEAN_NAME + ".url}"; //"#{basServerConfig.url}";
    String SERVER_URL_KEY = "spt.bas.server.url";

    String SEPARATE = "|";
    String COMMA = ",";
    String OBL = "/";
    String UNDER = "_";

    /**
     * 新业务操作记录保存逻辑开始日期
     */
    String NEW_ADD_HIS_START_DATE = "2024-08-06";

    /**
     * 部门类型：group-集团，company-公司，center-中心，dept-部门，team-小组
     */
    String DEPTTYPE_GROUP = "group";
    String DEPTTYPE_COMPANY = "company";
    String DEPTTYPE_CENTER = "center";
    String DEPTTYPE_DEPT = "dept";
    String DEPTTYPE_TEAM = "team";

    /**
     * 审批状态
     */
    String DICT_TYPE_APPROVESTATUS = "approveStatus";
    /**
     * 审批意见
     */
    String DICT_TYPE_APPROVEOPINION = "approveOpinion";
    /**
     * 采购合同查询条件
     **/
    String DICT_TYPE_SELLSTATUS = "sellStatus";
    /**
     * 销售合同查询条件
     **/
    String DICT_TYPE_BUYSTATUS = "buyStatus";
    String APPLY_TYPE_PY = "PY";    //已收付款
    String APPLY_TYPE_PN = "PN";    //未收付款
    String APPLY_TYPE_WY = "WY";    //已出入库
    String APPLY_TYPE_WN = "WN";    //未出入库
    String APPLY_TYPE_BY = "BY";    //已收开票
    String APPLY_TYPE_BN = "BN";    //未收开票

    String APPLY_STATUS_I = "I";    //'I':收/开票
    String APPLY_STATUS_R = "R";    //'R':收/付款
    String APPLY_STATUS_W = "W";    //'W':出/入库
    String APPLY_STATUS_B = "B";    //'B':退款
    String STATUS_DI = "DI";        //入库
    String STATUS_DO = "DO";        //出库

    /**
     * 化工产品
     */
    String PRODUCT_TYPE_HG = "HG";

    /**
     * 非化工产品
     */
    String PRODUCT_TYPE_NHG = "NHG";

    /**
     * 化工业务员IDS数据字典
     */
    String DICT_HG_MATCH_USER_IDS = "hgMatchUserIds";
    /**
     * 化工业务员品种下拉框
     */
    String DICT_HG_PRODUCT_SELECT = "hg_product_select";
    
    String DICT_BUSINESS_WORKBENCH_CONFIG = "businessWorkbenchConfig";
    String DICT_BUSINESS_WORKBENCH_CONFIG_begin_date = "beginDate";

    /**
     * 默认通用状态 是/否
     */
    String DICT_TYPE_DEFAULTFLG = "defaultFlg";
    /**
     * 合同查询类型
     */
    String DICT_TYPE_CONTRACTTYPES = "contractTypes";
    String DICT_TYPE_Y = "Y";
    String DICT_TYPE_N = "N";
    String DICT_PRODUCT_TYPE_HG = "HG";
    String DICT_PRODUCT_TYPE_NHG = "NHG";
    String DICT_TYPE_APPLYTYPE = "applyType";
    String DICT_TYPE_DELIVERY = "delivery";
    /**
     * 合同状态
     */
    String DICT_TYPE_CONTRACTSTATUS = "contractStatus";

    /**
     * 保理合同状态
     */
    String DICT_TYPE_FACTORCONTRACTSTATUS = "BlContractStatus";

    /**
     * 我方企业
     */
    String DICT_TYPE_OURCOMPANY = "ourCompany";
    /**
     * 履约状态
     */
    String DICT_TYPE_PERFORMANCE_STATUS = "performanceStatus";
    /**
     * 节点类型
     */
    String DICT_TYPE_NODETYPE = "nodeType";
    /**
     * 合同类型
     */
    String DICT_TYPE_CONTRACTTYPE = "contractType";
    /**
     * 库存类型
     */
    String DICT_TYPE_INVENTORYTYPE = "inventoryType";
    /**
     * 支付类型
     */
    String DICT_TYPE_PAYTYPE = "payType";

    /**
     * 质量标准
     */
    String DICT_TYPE_QUALITYSTANDDARD = "qualityStandard";
    /**
     * 交货补充时间
     */
    String DICT_TYPE_ATTACHDELIVERYTIME = "attachDeliveryTime";
    /**
     * 赊销交货补充时间
     */
    String DICT_TYPE_ATTACHDELIVERYSX = "attachDeliverySX";
    /**
     * 入库申请-入库方式
     */
    String DICT_TYPE_DELIVERYIN_TYPE = "deliveryInType";
    /**
     * 入库申请-库存性质
     */
    String DICT_TYPE_DELIVERYIN_NATURE = "deliveryInNature";
    /**
     * 出库申请-出库方式
     */
    String DICT_TYPE_DELIVERYOUT_TYPE = "deliveryOutType";

    String DICT_TYPE_DELIVERY_TYPE = "deliveryType";

    /**
     * 损耗申请-责任方/担责方
     */
    String DICT_TYPE_LOSS_TYPE = "lossType";
    /**
     * 责任方、承担方可选项：1-供应商、2-我方、3-物流公司、4-客户、5-业务员
     */
    String DICT_TYPE_LOSS_TYPE_1 = "1";
    String DICT_TYPE_LOSS_TYPE_2 = "2";
    String DICT_TYPE_LOSS_TYPE_3 = "3";
    String DICT_TYPE_LOSS_TYPE_4 = "4";
    String DICT_TYPE_LOSS_TYPE_5 = "5";

    /**
     * 出入库台账操作类型
     */
    String DICT_OUT_IN_LEDGER_TYPE = "outInLedgerType";
    // 合同签订
    String DICT_OUT_IN_LEDGER_TYPE_1 = "1";
    // 入库
    String DICT_OUT_IN_LEDGER_TYPE_2 = "2";
    // 出库
    String DICT_OUT_IN_LEDGER_TYPE_3 = "3";
    // 中游确认收货
    String DICT_OUT_IN_LEDGER_TYPE_4 = "4";

    /**
     * 支付方式,付款方式
     */
    String DICT_APPLY_PAYMODE = "payMode";

    String TEMPLATE_CONTENT_WAREHOUSE = "WAREHOUSE";

    String TEMPLATE_CONTENT_DELIVERYMODE = "DELIVERYMODE";
    /**
     * 包装规格
     */
    String DICT_TYPE_PACKINGSPECIFICA = "packingSpecifica";
    /**
     * 白名单仓库
     */
    String DICT_TYPE_SAFEWAREHOUSE = "safeWarehouse";
    /**
     * 包装规格
     */
    String DICT_TYPE_IMPORTBUYPACKING = "importBuyPacking";
    /**
     * 包装规格-合同模板
     */
    String DICT_TYPE_PACKINGSPECIFICATEXT = "packingText";
    /**
     * 交货方式-合同模板
     */
    String DICT_DELIVERYTYPETEXT = "deliveryType";
    /**
     * 交货方式-合同模板
     */
    String DICT_HKDELIVERYMODE = "hkDeliveryMode";

    /**
     * 签约地点-合同模板
     */
    String DICT_SIGNADDRESS = "signAddress";

    /**
     * 默认服务费率
     */
    BigDecimal DEFAULT_RATE = new BigDecimal("0.0003");

    /**
     * 默认超期服务费率
     */
    BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("0.001");

    /**
     * 基础赊销额度
     */
    BigDecimal DEFAULT_QUOTA = new BigDecimal("500000");

    /**
     * 保理条款-合同模板
     */
    String DICT_ADDITONALAGEREEMENT = "additionalAgreement";

    /**
     * 质量标准-合同模板
     */
    String DICT_QUALITYSTANDARDTEXT = "qualityStandardText";
    /**
     * 开票时间
     */
    String DICT_TYPE_INVOICEDATE = "invoiceDate";
    /**
     * 合同状态：新增
     */
    // 采购：N-新增，A-审批中，S-已签约(已盖章)，F1-已付款，G1-已收货，V1-已收票，D-完成，B-已审批，C-作废 w-等待
    // 销售：N-新增，A-审批中，S-已签约(已盖章)，F2-已收款，G2-已发货，V2-已开票，D-完成，B-已审批，C-作废 w-等待
    String CONTRACTSTATUS_NEW = "N";
    String CONTRACTSTATUS_S = "S";
    String CONTRACTSTATUS_W = "W";
    String CONTRACTSTATUS_B = "B";
    String CONTRACTSTATUS_C = "C";
    String CONTRACTSTATUS_F1 = "F1";
    String CONTRACTSTATUS_F2 = "F2";
    String CONTRACTSTATUS_G1 = "G1";
    String CONTRACTSTATUS_G2 = "G2";
    String CONTRACTSTATUS_V1 = "V1";
    String CONTRACTSTATUS_V2 = "V2";
    String CONTRACTSTATUS_A = "A";
    String CONTRACTSTATUS_D = "D";


    /**
     * 履约状态-进行中-N
     */
    String PERFORMANCE_STATUS_N = "N";

    /**
     * 履约状态-宽限期-B
     */
    String PERFORMANCE_STATUS_B = "B";

    /**
     * 履约状态-催告期-D
     */
    String PERFORMANCE_STATUS_D = "D";

    /**
     * 履约状态-逾期-S
     */
    String PERFORMANCE_STATUS_S = "S";

    /**
     * 履约状态-违约-P
     */
    String PERFORMANCE_STATUS_P = "P";

    /**
     * 履约状态-已完成-A
     */
    String PERFORMANCE_STATUS_A = "A";

    /**
     * 采购
     */
    String CONTRACTTYPE_BUY = "B";
    /**
     * 销售
     */
    String CONTRACTTYPE_SELL = "S";
    /**
     * 合同预算编号
     */
    String KEYSEQUENCE_CATEGORY_BUSINESSNO = "businessNo";
    /**
     * 审批编号
     */
    String KEYSEQUENCE_CATEGORY_APPROVENO = "approveNo";
    /**
     * 特殊合同编号
     */
    String KEYSEQUENCE_CATEGORY_CONTRACTSPECNO = "contractSpecNo";
    /**
     * 库存明细流水号
     */
    String KEYSEQUENCE_CATEGORY_STOCKDETAILNO = "stockDetailNo";

    /**
     * 库存盘点调整单号
     */
    String KEYSEQUENCE_CATEGORY_BUSINESSNOPD = "businessNoPD";
    /**
     * 二次结算单号
     */
    String KEYSEQUENCE_CALCULATE_NO = "calculateNo";

    /**
     * 审批意见：同意
     */
    String APPROVE_OPINION_AGREE = "A";
    /**
     * 审批意见：拒绝
     */
    String APPROVE_OPINION_DENY = "D";
    /**
     * 审批意见：追回
     */
    String APPROVE_OPINION_BACK = "R";

    /**
     * 状态 'N-新增，A-审批中，B-驳回，D-完成，C-取消, E-追回'
     */
    String APPROVE_STATUS_N = "N";
    String APPROVE_STATUS_A = "A";
    String APPROVE_STATUS_B = "B";
    String APPROVE_STATUS_D = "D";
    String APPROVE_STATUS_C = "C";
    String APPROVE_STATUS_E = "E";

    /**
     * 打印状态  作废
     **/
    String PRIN_CANCEL = "2";

    /**
     * 节点类型：用户
     */
    String NODE_TYPE_USER = "U";
    /**
     * 节点类型：部门
     */
    String NODE_TYPE_DEPT = "D";
    /**
     * 节点类型：组，组内任何一个人审批同意，流程通过
     */
    String NODE_TYPE_GROUP = "G";

    /**
     * 审批节点：总经理
     */
    String PROCESS_NODE_BS_MANAGER = "bs_manager";

    /**
     * 支付类型：B-定金，P-追加保证金，R-尾款，A-全款,T-贴现费用
     */
    String PAY_TYPE_BOND = "B";
    String PAY_TYPE_APPEND = "P";
    String PAY_TYPE_REMAIN = "R";
    String PAY_TYPE_ALL = "A";
    String PAY_TYPE_T = "T";
    String PAY_TYPE_W = "W";

    String UN_COMPANY_NAME = "未知";
    // 跟进方式
    String DICT_TYPE_FOLLOWTYPE = "followType";

    // 物流章类型
    String DICT_TYPE_LOGISTICSSEALTYPE = "logisticsSealType";
    // 盖章类型
    String DICT_TYPE_STAMPTYPE = "stampType";

    String DICT_COMPANY_STATUS = "companyStatus";
    // 公司状态：公海
    String COMPANY_STATUS_N = "N";
    // 公司状态：私海
    String COMPANY_STATUS_F = "F";
    // 公司状态：已合作
    String COMPANY_STATUS_D = "D";

    String COMPANY_STATUS_Z = "Z";

    String COMPANY_STATUS_S = "S";

    String COMPANY_STATUS_T = "T";

    String COMPANY_STATUS_U = "U";

    String COMPANY_STATUS_O = "O";

    String COMPANY_STATUS_R = "R";

    String COMPANY_STATUS_P = "P";
    /**
     * 企业操作类型
     **/
    String COMPANY_N = "[退回]";
    String COMPANY_F = "[领用]";
    String COMPANY_T = "[新增]";
    String COMPANY_U = "[修改]";
    String COMPANY_S = "[共享]";
    String COMPANY_Z = "[指派]";
    String COMPANY_O = "[退回共享]";
    String COMPANY_R = "[逾期]";
    String COMPANY_P = "[审核PS塑料分类]";
    String BANK_TYPE = "bankType";

    //逾期通知回复 状态
    String ORRVER_REPLY_STATUS_N = "N";//未回复
    String ORRVER_REPLY_STATUS_D = "D";//已回复

    // 收款状态
    String RECEIVE_STATUS = "receiveStatus";
    String RECEIVE_STATUS_N = "N"; // 新增
    String RECEIVE_STATUS_D = "D"; // 完成

    // 收款类型
    String RECEIVE_TYPE = "receiveType";
    String RECEIVE_TYPE_B = "B"; // 定金
    String RECEIVE_TYPE_R = "R"; // 余款
    String RECEIVE_TYPE_Z = "Z"; // 逐笔
    String RECEIVE_TYPE_A = "A"; // 全款

    /**
     * 司机配置
     */
    String DICT_DRIVER_CONFIG = "driverConfig";
    /**
     * 是否验证司机信息
     */
    String DICT_IS_CHECK_DRIVER = "isCheckDriver";


    /**
     * 花旗配置参数
     */
    String HQ_CONFIG_PARAM = "hqConfigParam";
    /**
     * 是否发送花旗
     */
    String IS_SEND_HQ = "isSendHq";

    /**
     * 考核项目  1-工作态度，2-工作能力
     */
    String EVALUATE_GROUP = "evaluateGroup";
    /**
     * 考核指标 1-工作用时,2-纪律性,3-责任感,4-协作力,5-专业知识,6-工作效率,7-工作质量,8-责任事故
     */
    String EVALUATE_METRICS = "evaluateMetrics";
    /**
     * 评级部门 HR-人力行政部，UP-直接上级
     */
    String EVALUATE_DEPT = "evaluateDept";
    String EVALUATE_DEPT_HR = "HR";
    String EVALUATE_DEPT_UP = "UP";
    /**
     * HR评级人员
     */
    String EVALUATE_USER_HR = "evaluateUserHR";
    String EVALUATE_USER_HR_CONFIG = "evaluateUserHRConfig";

    /**
     * 不参加考评人员
     */
    String NO_EVALUATE_USERS = "noEvaluateUsers";

    /**
     * 产品类型
     */
    String DICT_TYPE_BUYPRODUCT = "buyProductType";
    /**
     * 手机号保护
     */
    String DICT_TYPE_PHONEPROTECT = "phoneProtect";
    /**
     * 提货方式
     */
    String DICT_TYPE_BUYDELIVERY = "buyDeliveryType";
    String DICT_TYPE_BUYDELIVERY_Z = "Z";// 自提
    String DICT_TYPE_BUYDELIVERY_P = "P";// 配送
    String DICT_TYPE_DELIVERY_P1 = "P1"; // 上家配送
    String DICT_TYPE_DELIVERY_P2 = "P2"; // 我司配送
    /**
     * 库存类型
     */
    String DICT_TYPE_STOCK_GATEGORY = "stockCategory";
    String DICT_TYPE_STOCKTYPE = "stockType";
    String DICT_TYPE_STOCKTYPE_XH = "XH";//现货库存
    String DICT_TYPE_STOCKTYPE_QH = "QH";//期货库存
    /**
     * 现货库存
     */
    String DICT_TYPE_SPOTTYPE = "spotType";
    String DICT_TYPE_SPOTTYPE_W = "W";//我司货权
    String DICT_TYPE_SPOTTYPE_S = "S";//上家货权
    /**
     * 默认交货地址
     **/
    String DICT_TYPE_DEFAULT_DELIVERYADDR = "defaultDeliveryAddr";
    /**
     * saas推送合同默认业务员
     */
    String DICT_TYPE_SAAS_USER_CONFIG = "saasUserConfig";
    /**
     * 交货方式
     */
    String DICT_TYPE_DELIVERYMODE = "DELIVERYMODE";
    /**
     * 收款方式
     */
    String DICT_MODE_APPLYRECEIVE = "receiveMode";
    /**
     * 贴息费用承担方
     */
    String DICT_DISCOUNT_TARGET = "discountTarget";
    /**
     * 库存状态
     */
    String DICT_TYPE_STOCKSTATUS = "stockStatus";
    /**
     * 操作类型
     */
    String DICT_TYPE_OPERATIONTYPE = "operationType";
    /**
     * 打印状态
     */
    String DICT_TYPE_PRINTSTATUS = "printStatus";
    /**
     * 合同属性
     */
    String DICT_TYPE_CONTRACTATTR = "contractAttr";
    /**
     * 采购结算方式
     */
    String DICT_TYPE_BUY_DELIVERYMODE = "buyDeliveryMode";
    /**
     * 销售结算方式
     */
    String DICT_TYPE_SELL_DELIVERYMODE = "sellDeliveryMode";
    /**
     * 赊销结算方式
     */
    String DICT_TYPE_SX_DELIVERYMODE = "sxDeliveryMode";
    /**
     * 货到付款结算方式
     */
    String DICT_TYPE_HK_DELIVERYMODE = "hkDeliveryMode";

    String DICT_TYPE_CANCELTYPE = "cancelType";

    String DICT_TYPE_CONTRACTATTR_F = "F";//期货
    String DICT_TYPE_CONTRACTATTR_N = "N";//现货
    String DICT_TYPE_CONTRACTATTR_X = "X";//赊销

    String PRINTSTATUS_NONE = "0";//未打印
    String PRINTSTATUS_PRINT = "1";//已打印
    String PRINTSTATUS_CANCEL = "2";//已作废

    /**
     * 审批类型
     */
    String APPLY_TYPE = "applyType";
    String APPLY_TYPE_RB = "RB"; // 进口采购
    String APPLY_TYPE_RS = "RS"; // 进口销售
    String APPLY_TYPE_MB = "MB"; // 撮合采购
    String APPLY_TYPE_MS = "MS"; // 撮合销售
    String APPLY_TYPE_B = "B"; // 采购
    String APPLY_TYPE_S = "S"; // 销售
    String APPLY_TYPE_I = "I"; // 入库
    String APPLY_TYPE_SN = "SN"; // 合同签约
    String APPLY_TYPE_O = "O"; // 出库
    String APPLY_TYPE_G = "G"; // 买家收货确认
    String APPLY_TYPE_Z = "Z"; // 中游收货确认
    String APPLY_TYPE_M = "M"; // 撮合
    String APPLY_TYPE_R = "R"; // 进口代理
    String APPLY_TYPE_E = "E"; // 收款
    String APPLY_TYPE_H = "H";    // 收服务费
    String APPLY_TYPE_P = "P"; // 付款
    String APPLY_TYPE_N = "N"; // 开票
    String APPLY_TYPE_SB = "SB"; // 服务费开票
    String APPLY_TYPE_V = "V"; // 收票
    String APPLY_TYPE_D = "D"; // 提货
    String APPLY_TYPE_L = "L"; // 预售
    String APPLY_TYPE_A = "A"; // 预售采购
    String APPLY_TYPE_Q = "Q"; // 出库调整
    String APPLY_TYPE_K = "K"; // 入库调整
    String APPLY_TYPE_C = "C"; // 合同调整
    String APPLY_TYPE_F = "F"; // 内部交易
    String APPLY_TYPE_CC = "CC"; // 二次结算
    String APPLY_TYPE_U = "U"; //退款
    String APPLY_TYPE_PU = "PU"; //中游付退款
    String APPLY_TYPE_RU = "RU"; //中游收退款
    String APPLY_TYPE_FW = "FW";//服务合同
    String APPLY_SAAS_TYPE_AB = "AB"; //saas-采购
    String APPLY_SAAS_TYPE_AS = "AS"; //saas-销售
    String APPLY_TYPE_MUSE = "MUSE"; // 手动结算
    String APPLY_TYPE_DICUSS = "DICUSS"; // 溢短装
    String APPLY_TYPE_ZY = "ZY"; // 争议
    String APPLY_TYPE_DCSX_V = "DV"; // 代采赊销收票
    String APPLY_TYPE_DCSX_P = "DP"; // 代采赊销付款
    String APPLY_TYPE_DCSX_N = "N"; // 代采赊销开票
    String APPLY_TYPE_DCSX_R = "R"; // 代采赊销收款
    String APPLY_TYPE_LA = "LA"; // 物流调整申请

    String APPLY_TYPE_FC = "FC";    //签署保理
    /**
     * 入库减少
     **/
    String OPERATE_TYPE_OB = "OB"; // 出库撤回
    String OPERATE_TYPE_IB = "IB"; // 入库撤回
    String OPERATE_TYPE_IC = "IC"; // 入库撤回减少
    String OPERATE_TYPE_IS = "IS"; // 入库减少
    String OPERATE_TYPE_BC = "BC";// 采购取消
    String OPERATE_TYPE_SC = "SC";// 销售取消
    String OPERATE_TYPE_MA = "MA";// 移库增加
    String OPERATE_TYPE_MS = "MU";// 移库减少

    String OPERATE_TYPE_AA = "AA";// 盘点增加
    String OPERATE_TYPE_AS = "AS";// 盘点减少

    String OPERATE_TYPE_CA = "CA";// 合同调整增加
    String OPERATE_TYPE_CS = "CS";// 合同调整减少

    String OPERATE_TYPE_FA = "FA";// 内部交易增加
    String OPERATE_TYPE_FS = "FS";// 内部交易减少
    String OPERATE_TYPE_TA = "TA";// 内部交易退回增加
    String OPERATE_TYPE_TS = "TS";// 内部交易退回减少

    /**
     * 操作类型-出入库
     **/
    String[] DELIVERY_ARRAY = {OPERATE_TYPE_AA, OPERATE_TYPE_AS, APPLY_TYPE_I, OPERATE_TYPE_IS, APPLY_TYPE_O, APPLY_TYPE_G, OPERATE_TYPE_MA, OPERATE_TYPE_MS, OPERATE_TYPE_OB, OPERATE_TYPE_IB, OPERATE_TYPE_IC};
    /**
     * 操作类型-销售采购
     **/
    String[] BUYANDSELL_ARRAY = {APPLY_TYPE_B, APPLY_TYPE_S, OPERATE_TYPE_SC, OPERATE_TYPE_BC, APPLY_TYPE_A, APPLY_TYPE_L, APPLY_TYPE_MB, APPLY_TYPE_MS, OPERATE_TYPE_CA, OPERATE_TYPE_CS, APPLY_TYPE_RB, APPLY_TYPE_RS, OPERATE_TYPE_FA, OPERATE_TYPE_FS, OPERATE_TYPE_TA, OPERATE_TYPE_TS};

    /**
     * 合同类型
     */
    String CONTRACT_TYPE_B = "B";
    String CONTRACT_TYPE_S = "S";
    String CONTRACT_TYPE_F = "F";
    String CONTRACT_TYPE_X = "X";

    /**
     * 数量单位默认：吨
     */
    String NUMBER_UNIT_DUN = "吨";
    /**
     * 现货
     **/
    String STOCK_PRODUCT_ATTR_N = "N";
    /**
     * 在途
     **/
    String STOCK_PRODUCT_ATTR_P = "P";
    /**
     * 无状态
     **/
    String STOCK_PRODUCT_ATTR_A = "A";

    String STOCK__PRODUCT_ATTR = "productAttr";
    String STOCK__CONTRACT_ATTR = "contractAttr";

    /**
     * 库存状态
     */
    String STOCK_STATUS_I = "I";// 入库
    String STOCK_STATUS_O = "O";// 出库
    String STOCK_STATUS_P = "P";// 部分出库

    /**
     * 货物状态
     */
    String PRODUCT_STATUS_A = "A";// 预售合同生成默认为无状态的，既非在途也非现货
    String PRODUCT_STATUS_P = "P";// 在途
    String PRODUCT_STATUS_I = "I";// 入库
    String PRODUCT_STATUS_O = "O";// 出库
    String PRODUCT_STATUS_PI = "PI";// 部分入库
    String PRODUCT_STATUS_PO = "PO";// 部分出库

    /**** 代办事项类型 ***/
    String DEAL_TYPE = "dealType";

    String KEY_COMMON_NO_SUFFIX = "commonNoSuffix";

    /*** 提货单生成编号 ***/
    String DEAL_APPLYNO = "applyNo";
    /**
     * 系统配置项
     */
    String BS_DICT_TYPE_SYSCONFIG = "sysConfig";
    /**
     * 应用id
     */
    String ZG_APP_CODE = "appCode";

    String BS_DICT_TYPE_BL_MESSAGE = "BLMessage";
    /**
     * 域名企业关系
     */
    String DICT_TYPE_DOMAIN_ENTERPRISE = "domain_enterprise";
    // M-我的私海 , P-公海数据, MP-我的私海+公海，A-全部
    String COMPANY_SEARCH_MODE_PUBLIC = "P";
    String COMPANY_SEARCH_MODE_MY = "M";
    String COMPANY_SEARCH_MODE_ALL = "A";
    String COMPANY_SEARCH_MODE_F = "F";
    String COMPANY_SEARCH_MODE_MP = "MP";
    String COMPANY_SEARCH_MODE_N = "N";//不是我的

    /**
     * 库存数量增减
     */
    String STOCK_NUMBER_ADD = "ADD";
    String STOCK_NUMBER_SUB = "SUB";
    /**
     * 客户分类
     **/
    String DICT_TYPE_COMPANYTYPE = "bsCompanyType";
    String DICT_TYPE_CPYTYPE = "companyType";
    String DICT_TYPE_COMPANYGRADE = "companyGrade";
    String DICT_TYPE_SUPPLIERGRADE = "supplierGrade";
    String DICT_TYPE_ONLiNEFLG = "onLineFlg";
    String DICT_TYPE_COMPANYTYPE_I = "I"; //工业客户
    String DICT_TYPE_COMPANYTYPE_T = "T";//T-贸易商
    String DICT_TYPE_COMPANYTYPE_P = "P";//P-上游石化
    /**
     * 信用等级
     **/
    String DICT_TYPE_CREDITRATING = "creditRating";
    String DICT_TYPE_CREDITRATING_W = "W";
    String DICT_TYPE_CREDITRATING_W_TEXT = "白名单";
    String DICT_TYPE_CREDITRATING_B = "B";
    String DICT_TYPE_CREDITRATING_B_TEXT = "黑名单";
    String DICT_TYPE_CREDITRATING_G = "G";
    String DICT_TYPE_CREDITRATING_G_TEXT = "灰名单";

    /**
     * 去年纳税销售额
     */
    String DICT_TYPE_LASTYEARTAXABLESALE = "lastYearTaxableSales";

    /**
     * 常备库存
     */
    String DICT_TYPE_UNALLOCATEDSTOCK = "unallocatedStock";

    /**
     * 供应商级别
     */
    String DICT_TYPE_SUPPLIERLEVEL = "supplierLevel";
    /*
    企业类别
     */
    String DICT_TYPE_COMPANYCATEGORY = "companyCategory";
    String DICT_TYPE_COMPANYCATEGORY_A = "A";
    String DICT_TYPE_COMPANYCATEGORY_B = "B";
    String DICT_TYPE_COMPANYCATEGORY_C = "C";
    String DICT_TYPE_COMPANYCATEGORY_D = "D";
    /**
        供应商性质
    */
    String DICT_TYPE_SUPPLIERCATEGORY = "supplierCategory";

    /**
     * 评分企业分类（C-客户，S-供应商）
     */
    String SCORE_COMPANY_TYPE = "scoreCompanyType";
    String SCORE_COMPANY_TYPE_C = "C";
    String SCORE_COMPANY_TYPE_S = "S";

    String DICT_TYPE_LANDTYPE = "landType";//土地类型

    String DICT_TYPE_PLANTTYPE = "plantType";//厂房类型

    String DICT_TYPE_ACCESSREPORTFLG = "accessReportFlg";//访厂报告是否通过
    String DICT_TYPE_ACTUAL_GUARANTEE = "actualGuaranteeType";//实控人担保
    String DICT_TYPE_EQUIPMENTTYPE = "equipmentType";//机械设备类型
    String DICT_TYPE_PLASTIC_TYPE = "plasticType"; // 塑料分类
    String DICT_TYPE_ALLOWED = "allowed";//是否准入
    String DICT_TYPE_ALLOWED_Y = "Y";//准入
    String DICT_TYPE_ALLOWED_Y_TEXT = "准入";//准入
    String DICT_TYPE_ALLOWED_N = "N";//禁止
    String DICT_TYPE_ALLOWED_N_TEXT = "禁止";//禁止
    String DICT_TYPE_ALLOWED_NEW = "NEW";//初始
    String DICT_TYPE_ALLOWED_NEW_TEXT = "初始";//初始


    String DICT_TYPE_SUPPLIER_ALLOWED = "供应商准入申请";



    String OPE_BUYSELL_B = "buy";
    String OPE_BUYSELL_S = "sell";

    String BIZ_ADMIN = "bizadmin";//业务助理

    /**
     * 业务助理配置
     */
    String DICT_BUSINESS_ASSISTANT_DICT = "business_assistant_dict";

    String PROCESS_CODE_IN = "APPLY_DELIVERYIN";//入库流程代码
    String PROCESS_CODE_OUT = "APPLY_DELIVERYOUT";//出库流程代码

    //库存盘点调整状态
    String ADJUST_STATUS = "adjustStatus";
    String ADJUST_STATUS_N = "N";
    String ADJUST_STATUS_D = "D";

    String PROCESS_CODE_PAY = "APPLY_PAY";//付款申请
    String PROCESS_CODE_INTEREST_PAY = "APPLY_INTEREST_PAY";//中游付息申请
    String PROCESS_CODE_DCSX_PAY = "APPLY_DCSX_PAY";//DCSX付款申请
    String PROCESS_CODE_DCTP_PAY = "APPLY_DCTP_PAY";//DCTP付款申请
    String PROCESS_CODE_RECEIVE = "APPLY_RECEIVE";//收款申请
    String PROCESS_CODE_SERVICE_RECEIVE = "APPLY_SERVICE_RECEIVE";//收服务费申请
    String PROCESS_APPLY_INVALID_RECEIVE = "APPLY_INVALID";//作废申请
    String PROCESS_CTR_INVOICE = "CTR_INVOICE";//开票申请
    String PROCESS_CTR_DCSXINVOICE = "CTR_DCSXINVOICE";//代采赊销开票申请
    String PROCESS_CTR_DCSXINVOICE_TP = "CTR_DCSXINVOICE_TP";//托盘中游开票申请
    String PROCESS_APPLY_INRECEIVED = "APPLY_INRECEIVED";//收票申请
    String PROCESS_APPLY_DCSXINRECEIVED = "APPLY_DCSXINRECEIVED";//代采赊销收票申请
    String PROCESS_APPLY_DCSXINRECEIVED_TP = "APPLY_DCSXINRECEIVED_TP";//托盘中游收票申请
    String PROCESS_APPLY_BUY = "APPLY_BUY";//采购申请
    String PROCESS_APPLY_SELL = "APPLY_SELL";//销售申请
    String PROCESS_SAAS_PRESELL = "SAAS_PRESELL";//saas预售
    String PROCESS_APPLY_SXSELL = "APPLY_SXSELL";//赊销申请
    String PROCESS_APPLY_HKSELL = "APPLY_HKSELL";//货到付款申请
    String PROCESS_APPLY_SYBUY = "APPLY_SYBUY";//质押采购申请
    String PROCESS_APPLY_SYSELL = "APPLY_SYSELL";//质押销售申请
    String PROCESS_APPLY_DCMATCH = "APPLY_DCMATCH";//国企代采申请
    String PROCESS_APPLY_MATCH = "APPLY_MATCH";//背靠背申请
    String PROCESS_APPLY_MATCH_PALLET = "APPLY_MATCH_PALLET";//托盘业务
    String PROCESS_APPLY_IMPORTBUY = "APPLY_IMPORTBUY";//自营进口申请
    String PROCESS_APPLY_IMPORT = "APPLY_IMPORT";//代理开证申请
    String PROCESS_APPLY_PRESELL = "APPLY_PRESELL";//预售申请
    String PROCESS_APPLY_PRESELL_BUY = "APPLY_PRESELL_BUY";//预售采购申请
    String PROCESS_APPLY_CONTRACTADJUST = "APPLY_CONTRACTADJUST";//合同调整申请
    String PROCESS_APPLY_CANCEL = "APPLY_CANCEL";//作废申请
    String PROCESS_APPLY_PRESXSELL = "APPLY_PRE_SX_SELL";//预售赊销
    String PROCESS_APPLY_PREHKSELL = "APPLY_PRE_HK_SELL";//预售货到付款
    String PROCESS_APPLY_PAY_REFUND = "APPLY_PAY_REFUND";//采购退款
    String PROCESS_APPLY_RECEIVE_REFUND = "APPLY_RECEIVE_REFUND";//销售退款
    String PROCESS_APPLY_MATCH_IOUS = "APPLY_MATCH_IOUS";//背靠背白条
    String PROCESS_APPLY_CHARGE_SALES = "APPLY_CHARGE_SALES"; //代采赊销
    String PROCESS_APPLY_STOCK_VIRTUAL_BUY2 = "APPLY_STOCK_VIRTUAL_BUY2"; //自营库存采购
    String PROCESS_APPLY_STOCK_VIRTUAL_BUY = "APPLY_STOCK_VIRTUAL_BUY"; //协议采购申请
    String PROCESS_APPLY_SELL_SERVICE = "APPLY_SELL_SERVICE";//服务合同申请
    String PROCESS_APPLY_SEAL_USAGE = "APPLY_SEAL_USAGE";//印章使用申请
    String PROCESS_APPLY_SEAL_USAGE_BUSINESS = "APPLY_SEAL_USAGE_BUSINESS"; //业务盖章申请(业务自动发起的盖章申请)
    String APPLY_SEAL_USAGE_DCSX = "APPLY_SEAL_USAGE_DCSX";//代采赊销印章使用申请
    String APPLY_SEAL_USAGE_DCTP = "APPLY_SEAL_USAGE_DCTP";//代采托盘印章使用申请
    String PROCESS_APPLY_SEAL_BORROW = "APPLY_SEAL_BORROW";//印章外借申请
    String PROCESS_APPLY_BUSINESS_PAY = "APPLY_BUSINESS_PAY";//付费申请
    String PROCESS_APPLY_CPN_ALLOWED = "APPLY_CPN_ALLOWED";//公司准入申请
    String PROCESS_APPLY_CPN_ALLOWED2 = "APPLY_CPN_ALLOWED2";//PS塑料分类申请
    String PROCESS_APPLY_INTERAL_TRANSFER_MONEY = "APPLY_INTERAL_TRANSFER_MONEY"; //内部资金拆借申请
    String PROCESS_APPLY_PROTOCOL_DOC = "APPLY_PROTOCOL_DOC"; // 协议文件申请

    String PROCESS_APPLY_DISCUSS = "APPLY_DISCUSS";//议短申请
    String PROCESS_APPLY_LOSS = "APPLY_LOSS";//损耗申请
    String PROCESS_APPLY_LOGISTICS_ADJUST = "APPLY_LOGISTICS_ADJUST";//物流调整申请

    String PROCESS_APPLY_COMPANY_INFO = "APPLY_COMPANY_INFO";// 公司信息审批申请

    String PROCESS_APPLY_DEPOSIT = "APPLY_DEPOSIT";// 入金验证申请
    String PROCESS_APPLY_ENTRUST = "APPLY_ENTRUST";// 委托授权申请
    String PROCESS_APPLY_FEEDBACK = "APPLY_FEEDBACK";// 意见审批
    String PROCESS_APPLY_PARTNER = "APPLY_PARTNER";// 合伙人申请审批
    String PROCESS_APPLY_CFCA = "APPLY_CFCA";// cfca申请审批
    String PROCESS_APPLY_QUOTA = "APPLY_CPN_QUOTA";// 公司额度浮动审批
    String PROCESS_APPLY_QUOTA_V1 = "APPLY_QUOTA";// 公司额度审批
    String PROCESS_APPLY_QUOTA_TEMPORARY = "APPLY_QUOTA_TEMPORARY";// 公司额度提额审批
    String PROCESS_APPLY_COMPANY_VISIT = "APPLY_COMPANY_VISIT";// 访厂记录审批
    String PROCESS_APPLY_RATE = "APPLY_RATE";// 服务费率审批
    String PROCESS_APPLY_CREDIT_CYCLE = "APPLY_CREDIT_CYCLE";// 回款周期审批
    String PROCESS_APPLY_INSURANCE = "APPLY_INSURANCE";// 保险资料审批
    String PROCESS_APPLY_VIP="APPLY_VIP";//vip发起审批vip
    String PROCESS_APPLY_DISPUTE="APPLY_DISPUTE";//争议审批
    String PROCESS_APPLY_VIP_PROMOTE="APPLY_VIP_PROMOTE";//vip发起提额审批
    String PROCESS_APPLY_VIP_RECEIVE="PROCESS_APPLY_VIP_RECEIVE";//vip发起提额收款审批
    String PROCESS_APPLY_ONLINE = "PROCESS_APPLY_ONLINE";// 线上化审批

    String ONLINE_ACCOUNT_OPENING = "ONLINE_ACCOUNT_OPENING";// cfca上传资料审批合并后

    /**
     * vip提额开票申请
     */
    String PROCESS_APPLY_VIP_INVOICE= "APPLY_VIP_INVOICE";

    /**
     * vip开票申请
     */
    String PROCESS_APPLY_VIP_MAIN_INVOICE= "APPLY_VIP_MAIN_INVOICE";
    String PROCESS_APPLY_VIP_MAIN_RECEIVE="PROCESS_APPLY_VIP_MAIN_RECEIVE";//vip收款审批
    /**
     * 合同调整申请
     */
    String PROCESS_APPLY_CONTRACT_ADJUST = "APPLY_CONTRACT_ADJUST";

    /**
     * 确认收货申请
     */
    String PROCESS_APPLY_CONFIRM_RECEIPT= "CONFIRM_RECEIPT";

    /**
     * 中游确认收货申请
     */
    String PROCESS_APPLY_CONFIRM_RECEIPT_DCSX= "CONFIRM_RECEIPT_DCSX";

    /**
     * 出库申请
     */
    String PROCESS_APPLY_DELIVERYOUT= "APPLY_DELIVERYOUT";

    /**
     * 收货款申请
     */
    String PROCESS_APPLY_RECEIVE= "APPLY_RECEIVE";
    /**
     * 代采赊销收货款申请
     */
    String PROCESS_APPLY_RECEIVE_DCSX= "APPLY_RECEIVE_DCSX";
    /**
     * 托盘中游收款申请
     */
    String PROCESS_APPLY_RECEIVE_DCTP= "APPLY_RECEIVE_DCTP";
    /**
     * 代采赊销收退款
     */
    String PROCESS_APPLY_RECEIVE_REFUND_DCSX = "APPLY_RECEIVE_REFUND_DCSX";

    /**
     * 代采赊销付退款
     */
    String PROCESS_APPLY_PAY_REFUND_DCSX = "APPLY_PAY_REFUND_DCSX";
    /**
     * 开票申请
     */
    String PROCESS_APPLY_INVOICE= "CTR_INVOICE";

    /**
     * 发票寄送
     */
    String PROCESS_INVOICE_DELIVERY = "INVOICE_DELIVERY";

    /**
     * 收服务费申请
     */
    String PROCESS_SERVICE_RECE = "APPLY_SERVICE_RECE";
    /**
     * 报送供应商准入审批
     */
    String PROCESS_APPLY_SUPPLIER_ALLOWED = "APPLY_SUPPLIER_ALLOWED";
    /**
     * 报送供应商额度审批
     */
    String PROCESS_APPLY_SUPPLIER_QUOTA = "APPLY_SUPPLIER_QUOTA";
    /**
     * 报送供应商配送审批
     */
    String PROCESS_APPLY_SUPPLIER_DELIVERY = "APPLY_SUPPLIER_DELIVERY";
    /**
     * 报送供应商远期审批
     */
    String PROCESS_APPLY_SUPPLIER_FUTURE = "APPLY_SUPPLIER_FUTURE";

    /**
     * 经营费用申请
     */
    String PROCESS_APPLY_OPERATING_BUSINESS_PAY = "APPLY_OPERATING_BUSINESS_PAY";

    /**
     * 业务退款申请
     */
    String PROCESS_APPLY_REFUND_BUSINESS_PAY = "APPLY_REFUND_BUSINESS_PAY";

    /**
     * 管理费用申请
     */
    String PROCESS_APPLY_MANAGE_BUSINESS_PAY = "APPLY_MANAGE_BUSINESS_PAY";

    /**
     * 内部调拨费用申请
     */
    String PROCESS_APPLY_INTERNAL_BUSINESS_PAY = "APPLY_INTERNAL_BUSINESS_PAY";

    /**
     * 外部往来费用申请
     */
    String PROCESS_APPLY_EXTERNAL_BUSINESS_PAY = "APPLY_EXTERNAL_BUSINESS_PAY";

    /**
     * 签署保理申请
     */
    String PROCESS_APPLY_FACTOR_SIGN = "APPLY_FACTOR_SIGN";

    /**
     * 资金方充值申请
     */
    String PROCESS_APPLY_FUND_RECHARGE = "APPLY_FUND_RECHARGE";

    List<String> BS_CONFIG_FILTER_PROCESS_LIST = new ArrayList<String>(2) {
        private static final long serialVersionUID = 8613875060564912772L;
        {
            add(PROCESS_APPLY_MATCH_IOUS);
            add(PROCESS_APPLY_CHARGE_SALES);
        }
    };

    List<String> VIRTUAL_PROCESS_LIST = new ArrayList<String>(2) {
        private static final long serialVersionUID = 2561664022948882481L;

        {
            add(PROCESS_APPLY_STOCK_VIRTUAL_BUY);
            add(PROCESS_APPLY_STOCK_VIRTUAL_BUY2);
        }
    };
    //代采、白条预算
    String[] PROCESS_GROUP_DCBTYS = new String[]{PROCESS_APPLY_MATCH_IOUS, PROCESS_APPLY_MATCH,PROCESS_APPLY_MATCH_PALLET,PROCESS_APPLY_CHARGE_SALES};
    //自营贸易
    String[] PROCESS_GROUP_ZYMY = new String[]{PROCESS_APPLY_BUY, PROCESS_APPLY_SELL, PROCESS_APPLY_SXSELL, PROCESS_APPLY_MATCH, PROCESS_APPLY_IMPORTBUY, PROCESS_APPLY_MATCH_IOUS, PROCESS_APPLY_PRESELL, PROCESS_APPLY_PRESELL_BUY, PROCESS_APPLY_SELL_SERVICE};
    //工厂赊销
    String[] PROCESS_GROUP_GCSX = new String[]{PROCESS_APPLY_HKSELL};
    //代理业务
    String[] PROCESS_GROUP_DLYW = new String[]{PROCESS_APPLY_IMPORT, PROCESS_APPLY_DCMATCH};
    //质押业务
    String[] PROCESS_GROUP_ZYYW = new String[]{PROCESS_APPLY_SYBUY, PROCESS_APPLY_SYSELL};
    //预售
    String[] PROCESS_GROUP_YS = new String[]{PROCESS_APPLY_PRESELL, PROCESS_APPLY_PRESXSELL, PROCESS_APPLY_PREHKSELL, PROCESS_APPLY_PRESELL_BUY};
    //财务
    String[] PROCESS_GROUP_CW = new String[]{PROCESS_CODE_PAY, PROCESS_CODE_RECEIVE, PROCESS_CTR_INVOICE, PROCESS_APPLY_INRECEIVED, PROCESS_APPLY_PAY_REFUND, PROCESS_APPLY_RECEIVE_REFUND};
    //仓储
    String[] PROCESS_GROUP_CC = new String[]{PROCESS_CODE_IN, PROCESS_CODE_OUT};
    //其他
    String[] PROCESS_GROUP_OTHER = new String[]{PROCESS_APPLY_BUSINESS_PAY, PROCESS_APPLY_CONTRACTADJUST, PROCESS_APPLY_CANCEL};
    //印章使用申请
    String[] PROCESS_SEAL_APPLY = new String[]{PROCESS_APPLY_SEAL_USAGE, PROCESS_APPLY_SEAL_BORROW};
    //默认牌号，默认厂商
    String DEFUALT_BRANDNUMBER = "OT9999";
    Long DEFUALT_FACTORYID = 0L;

    //合同调整类型
    String CONTRACTADJUSTDETAILTYPE_N = "N";//新合同
    String CONTRACTADJUSTDETAILTYPE_O = "O";//原合同

    //销售合同模板标示符
    String TEMPLATE_CONTRACT_SALE = "contract_sale";
    //采购合同模板标示符
    String TEMPLATE_CONTRACT_BUY = "contract_buy";

    /**
     * 回调saas状态参数
     */
    String SAAS_STATUS_1 = "1";//审批成功
    String SAAS_STATUS_0 = "0";//驳回

    String SAAS_GATEWAY_URL = "saaszs.gateWay.url";
    String UCS_PUSH_URL = "ucs.push.url";
    /**
     * saas合同推送路径
     */
    String SAAS_CONTRACT_URL = "/open/out/basContractImport";
    /**
     * saas合同状态推送路径
     */
    String SAAS_CONTRACT_STATUS_URL = "/open/off/contract/approveDealRequest";
    /**
     * saas获取线上化标识
     */
    String SAAS_COMPANY_FLG_URL = "/open/out/queryCompanyToBas";
    /**
     * saas根据合同编号判断是否可以作废请求路径
     */
    String SAAS_CANINVALID_URL = "/open/out/cancelContractFlg";
    /**
     * saas结算表推送路径
     */
    String SAAS_SETTLEMENT_URL = "/open/out/basPushPartnerSettle";
    /**
     * ucs信用评价合同状态推送路径
     */
    String UCS_CONTRACT_STATUS_URL = "/open/ucs/receiveBasInfo/receivePiccCredit";
    /**
     * ucs信用评价赊销合同推送路径
     */
    String UCS_CONTRACT_URL = "/open/ucs/receiveBasInfo/receiveContract";
    /**
     * ucs信用评价授信额度推送路径
     */
    String UCS_CREDIT_URL = "/open/ucs/receiveSaasCreditScoreInfo/receiveRemainingCredit";
    /**
     * ucs新增客户获取其授信额度
     */
    String GET_COMPANY_CREDIT_URL = "/open/ucs/receiveBasInfo/receiveCredit";
    /**
     * ucs信用评价结算单推送路径
     */
    String UCS_SETTLEMENT_URL = "/open/ucs/receiveBasInfo/receiveSettlement";
    /**
     * ucs合同状态更新推送路径
     */
    String UCS_UPDATE_STATUS_URL = "/open/ucs/receiveBasInfo/receiveContractStatus";

    /**
     * picc人保赊销合同推送路径
     */
    String PICC_PUSH_CONTRACT_URL = "/InterfaceProxy/ExternalServices/SaleDeclareXAB";
    /**
     * picc人保按金额回款推送路径
     */
    String PICC_PUSH_RECOVERINFO_URL = "/InterfaceProxy/ExternalServices/SaleRecover";
    /**
     * picc人保按条件回款推送路径
     */
    String PICC_PUSH_CPRPXSALERECOVER_URL = "/InterfaceProxy/ExternalServices/SaleDeclareRecover";
    String UCS_APP_CODE = "ucs";    //信用评价系统标识
    String PICC_APP_CODE = "picc";    //人保系统标识
    String SAAS_APP_CODE = "saas";    //采购机器人系统标识

    /**
     * 销售申请中的付款方式
     */
    String PAYMODE_KDXH = "KDXH";// 款到卸货
    String PAYMODE_QKZF = "QKZF";// 全额支付
    String PAYMODE_HDFK = "HDFK";// 货到付款
    String PAYMODE_XHHK = "XKHH";// 先款后货
    String PAYMODE_DJZF = "DJZF";// 定金支付
    String PAYMODE_SX = "SX";// 赊销

    /**
     * 采购-付款方式
     */
    String BSDICT_BUY_PAYMODE = "buyPayMode";
    /**
     * 销售-付款方式
     */
    String BSDICT_SELL_PAYMODE = "sellPayMode";
    /**
     * 赊销-付款方式
     */
    String BSDICT_SX_PAYMODE = "sxPayMode";
    /**
     * 货到付款-付款方式
     */
    String BSDICT_HK_PAYMODE = "hkPayMode";
    /**
     * 背靠背-销售付款方式
     */
    String BSDICT_MATCH_PAYMODE = "matchPayMode";
    /**
     * 质押-付款方式
     */
    String BSDICT_SY_PAYMODE = "syPayMode";

    /**
     * 销售方式
     */
    String DELIVERY_MODE_SX = "SX";//赊销
    String DELIVERY_MODE_XHHK = "XHHK";//货到付款
    String DELIVERY_MODE_XKHH = "XKHH";//款到发货

    /**
     * 结算方式 赊销（一票制）
     */
    String SETTLEMENT_TYPE_ONE = "0";

    /**
     * 结算方式 赊销（两票制）
     */
    String SETTLEMENT_TYPE_TWO = "1";

    String CREDIT_FLOW_TYPE = "creditFlowType";//授信流水类型

    String PICC_XML_RECOVERINFO = "RECOVERINFO";                //picc按金额回款请求报文
    String PICC_XML_CPrpxSaleRecover = "CPrpxSaleRecover";        //picc按条件回款请求报文
    String PICC_XML_CPRPXSALEDECLARE = "CPRPXSALEDECLARE";        //picc内贸赊销请求报文

    String PICC_CODE = "PICCCODE";        //picc人保信用险保单

    String QUALITY_Y = "Y";            //原厂标准
    String QUALITY_G = "G";            //过渡料
    String QUALITY_F = "F";            //副牌料

    String ATTACH_DELIVERY_TIME_LR = "LR";    //交货补充时间-左右
    String ATTACH_DELIVERY_TIME_B = "B";    //交货补充时间-前（含当日）
    String ATTACH_DELIVERY_TIME_K = "K";    //款到发货

    String PRESELLDEPTID = "preSellDeptId";//限制预售发起采购部门ID
    String DEPTID = "deptID";

    /**
     * 业务类型-大类
     */
    String DICT_TYPE_BUSINESS = "business";
    /**
     * 业务类型-小类
     */
    String DICT_TYPE_BUSINESSTYPE = "businessType";
    /**
     * 预售-业务类型
     */
    String DICT_TYPE_PRESELL_BUSINESS = "preSellBusiness";

    String DICT_TYPE_BUSINESS_ZY = "ZY";            //自营
    String DICT_TYPE_BUSINESS_SX = "SX";            //赊销
    String DICT_TYPE_BUSINESS_DL = "DL";            //代理
    String DICT_TYPE_BUSINESS_SY = "SY";            //质押
    String DICT_TYPE_BUSINESS_DC = "DC";            //代采
    String BUSINESS_TYPE_FW = "FW";                 //服务合同
    String BUSINESS_TYPE_ZY_CG = "ZY-CG";           //自营采购
    String BUSINESS_TYPE_ZY_XS = "ZY-XS";           //自营销售
    String BUSINESS_TYPE_ZY_BB = "ZY-BB";           //背靠背
    String BUSINESS_TYPE_ZY_TP = "ZY-TP";           //托盘
    String BUSINESS_TYPE_KC_CG = "KC-CG";           //库存采购
    String BUSINESS_TYPE_ZY_BB_C = "ZY-BB-C";       //中间链条
    String BUSINESS_TYPE_ZY_JK = "ZY-JK";           //自营进口
    String BUSINESS_TYPE_SX_SX = "SX-SX";           //赊销
    String BUSINESS_TYPE_SX_HK = "SX-HK";           //货到付款
    String BUSINESS_TYPE_DL_KZ = "DL-KZ";           //代理开证
    String BUSINESS_TYPE_DL_DC = "DL-DC";           //国企代采
    String BUSINESS_TYPE_SY_CG = "SY-CG";           //质押采购
    String BUSINESS_TYPE_SY_XS = "SY-XS";           //质押销售
    String BUSINESS_TYPE_DX_SX = "DC-XS";           //代采赊销
    /**
     * 合同费用类型
     */
    String DICT_TYPE_CONTRACTFEETYPE = "contractFeeType";

    /**
     * 费用登记费率
     */
    String DICT_TYPE_CONTRACTFEERATE = "contractFeeRate";
    /**
     * 股东背景
     */
    String DICT_TYPE_SHAREHOLDER = "shareholder";

    String PROCESS_GROUP_BIZ = "biz";
    String PROCESS_GROUP_MNG = "mng";


    interface DictType {
        /**
         * 是否有效，boolean类型
         */
        String COMM_ENABLE_BOOLEAN = "COMM_ENABLE_BOOLEAN";
        /**
         * 流程分组：biz是业务流程，mng是企管流程
         * */
        String DICT_PROCESS_GROUP = "processGroup";

        String DICT_STEP_GROUP_TYPE = "stepGroupType";
    }

    /**
     * 赊销模式：contractModel
     */
    String CONFIG_TYPE_CONTRACT_MODEL = "contractModel";
    String CONFIG_TYPE_CONTRACT_MODEL_0 = "0";          //0-不限
    String CONFIG_TYPE_CONTRACT_MODEL_BL = "BL";        //BL-保理
    String CONFIG_TYPE_CONTRACT_MODEL_PT = "PT";        //PT-全额

    /**
     * 资金来源：fundSource
     */
    String CONFIG_TYPE_FUND_SOURCE = "fundSource";
    String CONFIG_TYPE_FUND_SOURCE_OUR = "OUR";         //OUR-自有全额

    /**
     * 代采赊销单位：sxCompany
     */
    String CONFIG_TYPE_SX_COMPANY_0 = "FTK";               //0-泛太克
    String CONFIG_TYPE_SX_COMPANY_1 = "NFJM";              //1-南方经贸
    String CONFIG_TYPE_SX_COMPANY_2 = "SHSL";              //2-上海笙竺
    String CONFIG_TYPE_SX_COMPANY_3 = "SHCO";              //3-上海宸瓯
    String CONFIG_TYPE_SX_COMPANY_4 = "SHKX";              //4-上海锴旭
    String CONFIG_TYPE_SX_COMPANY_5 = "SHGZ";              //5-上海歌泽瑞
    String CONFIG_TYPE_SX_COMPANY_6 = "SHGZ";              //6-上海歌泽瑞
    String CONFIG_TYPE_SX_COMPANY_7 = "SZKKW";             //7-深圳可口屋
    String CONFIG_TYPE_SX_COMPANY_8 = "SRY";               //8-塑融云
    String CONFIG_TYPE_SX_COMPANY_9 = "SHCYSM";            //9-上海从易
    List<String> SX_COMPANY_LIST = new ArrayList<String>(10) {
        private static final long serialVersionUID = -8491714001026207207L;
        {
            add(CONFIG_TYPE_SX_COMPANY_0);
            add(CONFIG_TYPE_SX_COMPANY_1);
            add(CONFIG_TYPE_SX_COMPANY_2);
            add(CONFIG_TYPE_SX_COMPANY_3);
            add(CONFIG_TYPE_SX_COMPANY_4);
            add(CONFIG_TYPE_SX_COMPANY_5);
            add(CONFIG_TYPE_SX_COMPANY_6);
            add(CONFIG_TYPE_SX_COMPANY_7);
            add(CONFIG_TYPE_SX_COMPANY_8);
            add(CONFIG_TYPE_SX_COMPANY_9);
        }
    };
    /**
     * 群发待办已读状态:readFlg
     */
    String READ_FLG ="readFlg";
    String READ_FLG_NOT="0"; //未读
    String READ_FLG_HAVE="1"; //已读

    /**
     * 群发待办类型:waitDealType
     */
    String WAIT_DEAL_TYPE ="waitDealType";
    String WAIT_DEAL_TYPE_NOTIFY="通知"; //通知

    /**
     * 完成状态
     */
    String COMPLETE_FLG ="completeFlg";

    String CONFIG_FLG_SWITCH = "bsConfigSwitch";
    String CONFIG_FLG_SWITCH_SUGX = "sugx";

    String DICT_TYPE_FEETYPE_WF = "WF";        //费用类型-仓储费
    String DICT_TYPE_FEETYPE_TF = "TF";        //费用类型-运输费
    String DICT_TYPE_FEETYPE_LF = "LF";        //费用类型-装车费
    String DICT_TYPE_FEETYPE_IPF = "IPF";        //费用类型-罚息费
    String DICT_TYPE_FEETYPE_SWF = "SWF";        //费用类型-系统仓储费
    String DICT_TYPE_CANCEL_CN = "CN";            //合同作废


    String DICT_TYPE_SHOULD_PAY_TYPE = "shouldPayType";//应付款类型

    String DICT_TYPE_CONTRACT_BOND_RATE = "contractBondRate";//合同付定金比例
    String DEPOSIT_PROPORTION="depositProportion";//货到付款模式定金比例
    String HDFK_PAY_RATE="hdfkPayRate";//默认赊销预算货到付款模式定金比例
    String DCSX_HDFK_PAY_RATE="dcsxHdfkPayRate";//默认代采赊销预算货到付款模式定金比例
    String DICT_TYPE_SERVICE_CONTRACT_RATE = "serviceContractRate";//服务合同费率

    String DICT_TYPE_SEAL_TYPE = "sealType";//印章-印章类型
    String DICT_TYPE_SEAL_TYPE_TS = "TS";    //印章类型-合同章
    String DICT_TYPE_FILE_TYPE_BS = "BS";    //文件类型-购销合同
    String DICT_TYPE_FILE_TYPE_DCSX = "DCSX";//文件类型-代采赊销合同
    String DICT_TYPE_CUSTOMER_NAME = "customerName";//印章-公司名称
    String DICT_TYPE_BUSINESS_NAME = "manageCostType";//经营费用申请-费用类别
    String DICT_TYPE_ITEM_TYPE = "itemType";//印章外借-物品类型
    String DICT_TYPE_SEAL_STATUS = "sealStatus";//印章外借-印章状态

    String CONFIG_KEY_FS_DF = "FS_DF";            //商品配置项-默认
    String CONFIG_KEY_FS_SL = "FS_SL";            //商品配置项-塑料
    String CONFIG_KEY_FS_HG = "FS_HG";            //商品配置项-化工
    String CONFIG_KEY_FS_WH = "FS_WH";            //商品配置项-万华
    String CONFIG_KEY_PAY_FULL_RULE = "PAY_FULL_RULE";//付全款日期规则配置
    String HG_CL_YE = "HG_CL_YE";                 //商品代码-乙二醇
    String AUTO_INVOICE_CONFIG = "AUTO_INVOICE_CONFIG"; //自动开收票配置项
    String CONFIG_KEY_DCSX_INSURANCE_RATE_PARAM = "DCSX_INSURANCE_RATE_PARAM";//代采赊销中游合同保费费率
    String PARAM_BY_COMPANY_GRADE = "PARAM_BY_COMPANY_GRADE";// 根据客户等级配置服务费率及罚息费率
    String CALCULATE_CONFIG_KEY = "CALCULATE_PAEAM";//提成计算默认参数
    String CALCUALTE_INSURANCE_RATE_KEY = "INSURANCE_RATE_PARAM";//动态保费费率取值表达式
    String BATCH_PAY_APPLY_CONFIG = "BATCH_PAY_APPLY_CONFIG";//批量发起付款申请配置项

    String PRODUCT_INDUSTRY_SL = "SL";            //塑料
    String PRODUCT_INDUSTRY_HG = "HG";            //化工
    String PRODUCT_INDUSTRY_AL = "AL";            //农产品

    String SETTLEMENT_STATUS_I = "I";            //结算单-进行中
    String SETTLEMENT_STATUS_D = "D";            //结算单-已完成
    String SETTLEMENT_STATUS_B = "B";            //结算单-已违约

    String KEY_SETTLEMENT_NO = "settlementNo";    //结算单编号
    String KEY_BUDGET_SETTLEMENT_NO = "budgetSettlementNo";    //预算结算单编号
    String KEY_PAIR_CODE = "pairCode";            //撮合排序号
    String KEY_SERVICE_NO = "serviceContentNo";//服务合同编号

    String DICT_TYPE_SEAL_STATUS_D = "D";        //已审批
    String DICT_TYPE_SEAL_STATUS_R = "R";        //已归还
    String DICT_TYPE_SEAL_STATUS_L = "L";        //已延期

    String SEAL_APPLY_NAME_BORROW = "SealBorrow";//印章外借申请
    String SEAL_APPLY_NAME_USAGE = "SealUsage";//印章使用申请
    String COMPANY_APPLY_ALLOWED = "BsCompanyAllowed";//公司准入申请
    String COMPANY_APPLY_QUOTA = "BsCompanyQuota";//公司额度浮动申请
    String COMPANY_QUOTA = "BsCompanyQuotaV1";//公司额度申请
    String APPLY_COMPANY_INFO = "ApplyCompanyInfo";// 企业信息申请
    String APPLY_DEPOSIT = "ApplyDeposit";// 入金验证审批
    String APPLY_ENTRUST = "ApplyEntrust";// 委托授权审批
    String APPLY_PARTNER = "ApplyPartner";// 申请成为合伙人
    String APPLY_FEEDBACK = "ApplyFeedback";// 意见反馈
    String APPLY_CFCA = "ApplyCfca";// cfca
    String APPLY_RATE = "ApplyRate";// 服务费率
    String APPLY_CREDIT_CYCLE = "ApplyCreditCycle";// 回款周期
    String APPLY_CANCEL = "ApplyCancel";// 作废申请

    String DICT_TYPE_SEAL_FS = "FS";            //财务章
    String DICT_TYPE_SEAL_FILE_TYPE = "sealFileType";//印章文件类别
    String VEHICLE_APPLY_NAME_USE = "VehicleUse";//车辆使用预约
    String DICT_APPLY_VEHICLE_USE = "plateNumber";// 车牌号
    String DICT_APPLY_VEHICLE_LOCATION = "vehicleLocation";//所在地点

    String APPLY_BRAND = "applyBrand";//牌号申请

    String FACTOR_AMOUNT = "factorAmount";

    /**
     * B1-月末3个工作日内进项发票未到
     */
    String WARNING_TODO_B1 = "月末前3日未收进项发票";
    /**
     * W1-存货时间超过7天
     */
    String WARNING_TODO_W1 = "存货时间超过7天";
    /**
     * W2-负存货时间超过7天
     */
    String WARNING_TODO_W2 = "负存货时间超过7天";

    BigDecimal RISK_MATH_AMOUNT5 = new BigDecimal(5000000);
    BigDecimal RISK_MATH_AMOUNT3 = new BigDecimal(3000000);

    String SCHEDULE_TYPE_W = "W";                //入库超期待办事项
    String SCHEDULE_TYPE_B = "B";                //预售回补待办事项
    String SCHEDULE_TYPE_P = "P";                //进项发票待办事项

    String DICT_SCHEDULE_STATUS = "scheduleStatus";//待办事项
    String SCHEDULE_STATUS_N = "N";            //待办事项-N-待处理
    String SCHEDULE_STATUS_D = "D";            //待办事项-D-已处理

    String DICT_BUSINESS_DEPT_TYPE = "businessDeptType";//所属部门
    String DICT_BACKGROUND_COST_TYPE = "backgroundCostType";//后台费用类别
    String DICT_PERSONNEL_COST_TYPE = "personnelCostType";//人事费用类别
    String DICT_RECEPTION_COST_TYPE = "receptionCostType";//前台费用类别

    String DICT_OPERATING_COST_TYPE = "operatingCostType";  //经营费用申请-费用类别
    String DICT_REFUND_COST_TYPE = "refundCostType";  //业务退款申请-费用类别
    String DICT_MANAGE_COST_TYPE = "manageCostType";        //管理费用申请-费用类别

    String CURRENCY_EXCHANGE_RATE = "currencyExchangeRate";// 货币汇率
    String DOLLAR_TO_RMB = "dollarToRmb";// 美元转人民币汇率
    /**
     * 采购类型
     */
    String DICT_TYPE_VIRTUAL_BUY_TYPE = "virtualBuyType";
    /**
     * 采购来源
     */
    String DICT_TYPE_BUY_SOURCE = "buySource";

    /**
     * 销售来源
     */
    String DICT_TYPE_SELL_SOURCE = "sellSource";

    /**
     * 服务费收取方式
     */
    String DICT_TYP_SERVICE_TYPE = "serviceType";

    /**
     * 服务费收取方式 无
     */
    String SERVICE_TYPE_N = "N";

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
     * 保险申报状态:未申报
     */
    String DECLARE_STATUS_NO_START = "0";

    /**
     * 保险申报状态:申报中
     */
    String DECLARE_STATUS_DECLARING = "1";

    /**
     * 保险申报状态:申报驳回
     */
    String DECLARE_STATUS_REJECT = "2";

    /**
     * 保险申报状态:已受理
     */
    String DECLARE_STATUS_DEAL = "3";
    /**
     * 保险申报状态:申报完成
     */
    String DECLARE_STATUS_COMPLETE = "4";

    // 采购管家用户合同状态
    /**
     * 待收货
     */
    String CONTRACT_STATUS_W = "W";
    /**
     * 待付服务费
     */
    String CONTRACT_STATUS_S = "S";
    /**
     * 待付款
     */
    String CONTRACT_STATUS_P = "P";

    /**
     * 待盖章
     */
    String CONTRACT_STATUS_N = "N";
    /**
     * 待收票（包括待收服务费发票和待收货款发票
     */
    String CONTRACT_STATUS_B = "B";
    /**
     * 逾期
     */
    String CONTRACT_STATUS_L = "L";
    /**
     * 违约
     */
    String CONTRACT_STATUS_T = "T";
    /**
     * 已完成
     */
    String CONTRACT_STATUS_O = "O";

    /**
     * 争议
     */
    String CONTRACT_STATUS_Z = "Z";

    /**
     * 确认收货状态 0：未确认
     */
    String CONFIRM_FLG_NOT = "0";
    /**
     * 确认收货状态 1：已确认
     */
    String CONFIRM_FLG_YES = "1";
    /**
     * 确认收货状态 2：确认中
     */
    String CONFIRM_FLG_ING = "2";

    /**
     * 违约限制日期 默认 7天
     */
    Integer BREACH_LIMIT_DAY = 7;

    Integer ONE_MONTH_DAYS_30 = 30;

    String Y = "Y";
    String X = "X";

    String COMPANY_COLLECT = "companyCollect";

    String EVERYDAY_LIMIT = "everyDayLimit";

    String COMPANY_INTERVAL_DAYS = "CompanyIntervalDays";

    String  INTERVAL_DATES = "IntervalDates";
    /**
     * 供应商
     */
    String  SUPPLIER = "supplier";
    /**
     * 终端工厂
     */
    String TERMINAL_FACTORY = "terminalFactory";
    /**
     * 终端工厂自提审批
     */
    String APPLY_TERMINAL = "APPLY_TERMINAL";

    /**
     * 终端工厂自提审批状态
     */
    String CREDIT_DELIVERY_STATUS = "true";

    /**
     * 终端工厂自提状态-- 审批中
     */
    String TERMINAL_PICK_ING = "1";

    /**
     * 终端工厂自提状态-- 已完成
     */
    String TERMINAL_PICK_DONE = "4";
    /**
     * 终端工厂自提审批-- 驳回
     */
    String TERMINAL_PICK_REFUSE = "3";

    String BUSINESS_TYPE_DCSX = "DCSX";

    String TEMPLATETAG_DCSX = "dcsx_contract";
    String TEMPLATETAG_DCSX_CONTRACT = "dcsx_contract_template";
    String TEMPLATETAG_DCSX_CONTRACT_SDNH = "dcsx_contract_template_sdnh";
    String TEMPLATETAG_DCSX_CONTRACT_SUGX = "dcsx_contract_template_sugx";
    String TEMPLATETAG_DCSX_CONTRACT_YD = "dcsx_contract_template_yd";
    /**
     * 赊销合同模板
     */
    String TEMPLATETAG_SELL_DCSX_CONTRACT = "sell_dcsx_contract_template";
    String TEMPLATETAG_SELL_DCSX_CONTRACT_ALL = "sell_dcsx_contract_template_all";
    String TEMPLATETAG_SELL_DCSX_CONTRACT_HDFK = "sell_dcsx_contract_template_hdfk";
    String TEMPLATETAG_SELL_DCSX_CONTRACT_ALL_HDFK = "sell_contract_template_all_hdfk";
    String TEMPLATETAG_SELL_DC_TP_CONTRACT_SUGX = "sell_tp_contract_template_sugx";
    String TEMPLATETAG_SELL_DCSX_CONTRACT_GXHL = "sell_dcsx_contract_template_gxhl";
    /**
     * 赊销合同模板-苏高新
     */
    String TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX = "sell_dcsx_contract_template_sugx";
    String TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX_ALL = "sell_template_sugx_all";
    String TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX_HDFK = "sell_template_sugx_hdfk";
    String TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX_ALL_HDFK = "sell_template_sugx_all_hdfk";

    // 代采合同模板
    String TEMPLATETAG_SELL_DC_CONTRACT_TEMPLATE = "sell_dc_contract_template";
    String TEMPLATETAG_SELL_DC_CONTRACT_TEMPLATE_ALL = "sell_dc_contract_template_all";
    String TEMPLATETAG_SELL_DC_CONTRACT_TEMPLATE_GXHL = "sell_dc_contract_template_gxhl";
    String TEMPLATETAG_BUY_DC_CONTRACT_TEMPLATE_GXHL = "buy_dc_contract_template_gxhl";

    String TEMPLATETAG_SELL_DC_CONTRACT = "sell_dc_contract_template";
    String TEMPLATETAG_BUY_FLK_DC_CONTRACT = "flk_dc_contract_template";
    String TEMPLATETAG_BUY_ZJKR_AHZY_CONTRACT = "zjkr_ahzy_contract_template";

    // 安心签常量
    String CONTRACT_SEAL_TYPE_UK_SIGN = "2";

    // 签名类别 云签 1 或 uk签 2
    String CONTRACT_SEAL_TYPE_SIGN = "1";

    String CONTRACT_SEAL_NOT_SEND_PWD = "0";
    String CONTRACT_SIGN_LOCATION = "buyerSignature;sellerSignature";
    String CONTRACT_BUYER_SIGN_LOCATION = "buyerSignature";
    String CONTRACT_SELLER_SIGN_LOCATION = "sellerSignature";

    /**
     * 安心签配置信息
     */
    String WX_PURCHASE_INFO = "wx_purchase_info";
    String SN = "SN";


    String Customer_Rating_Level_A = "A";

    String Customer_Rating_Level_N = "N";

    String Customer_Change_Type_Fee ="手续费返还";

    String Customer_Change_Type = "合同抵扣";

    /**
     * 开户提醒
     */
    String CREATE_ACCOUNT = "createAccountUser";
    String CREATE_ACCOUNT_USER = "user";
    String Common_Invoice_Mark = "N";

    String Special_Invoice_Mark = "S";

    String  CREDITOR_NO="debtCertificate";

    String PAY_MODE="payMode";

    String PAY_MODE_H = "H";

    String BUSINESS_TYPE_BL = "BL";

    String BUSINESS_TYPE_HDFK = "HDFK";
    String BUSINESS_TYPE_DCSX_HDFK = "DCSXHDFK";
    
    String BUSINESS_TYPE_DCSXCK = "DCSXCK";

    String BUSINESS_TYPE_DCSXBL = "DCSXBL";

    List<String> BL_BUSINESS_CODE = new ArrayList<String>(2) {
        private static final long serialVersionUID = -1420661688679952679L;
        {
            add(BUSINESS_TYPE_BL);
            add(BUSINESS_TYPE_DCSXBL);
        }
    };

    List<String> PT_BUSINESS_CODE = new ArrayList<String>(3) {
        private static final long serialVersionUID = -1;
        {
            add(BUSINESS_TYPE_DCSX);
            add(BUSINESS_TYPE_HDFK);
            add(BUSINESS_TYPE_DCSX_HDFK);
        }
    };

    String APPLY_DCSXBL_PAY = "APPLY_DCSXBL_PAY";//代采赊销保理还款申请

    String DEPOSIT_PAYMENT_DCSXBL ="DEPOSIT_PAYMENT_DCSXBL";//保理预算保证金付款

    //保理还款发起人
    String Factoring_Repayment_Originator="FactoringRepaymentOriginator";
    String Factoring_Repayment_Originator_User="ZJWS";
    // 	保理公司
    String  FACTOR_COMPANY="factor_company";

//    N-待处理、Z-资料已收集、B-保证金已支付、F-银联已放款、D-已还款
    String FACTOR_STATUS_N = "N"; //待处理

    String FACTOR_STATUS_Z = "Z"; //资料已收集

    String FACTOR_STATUS_B = "B"; //保证金已支付

    String FACTOR_STATUS_F = "F"; //银联已放款

    String FACTOR_STATUS_D = "D"; //已还款

    String CONDITION_TYPE_A = "A";  //增加步骤

    //供应商准入申请状态
    String SUPPLIER_RATING_STATUS_ONE = "0"; //未开始

    String CONDITION_TYPE_S = "S";  //减少步骤

    String APPLY_MODEL_DCSX_BL="DCSXBL";

    String APPLY_MODEL_BL="BL";
    /**
     * 物流單狀態
     */
    String APPROVE_DELIVERY_STAUS = "D";

    String LOADING_OUR_COMPANY_TEMPLATE = "loadingOurCompanyTemplate";//提货单模板配置

    String LOADING_DELIVERY_OUR_COMPANY_TEMPLATE = "deliveryOurCompanyTemplate";//委托配送单模板配送

    String GOOD_RECEIVE_TEMPLATE = "goodReceiveTemplate";//货物签收单模板

    String LOADING_BILL_TYPE_T = "T";   //提货单
    String LOADING_BILL_TYPE_P = "P";   //配送单
    /**
     * 合同履约状态
     * N-进行中，B-宽限期，D-催告期，S-逾期，P-违约
     */
    String DICT_TYPE_CONTRACTPE_RFOEMACE_STATUS = "performanceStatus";
    Map<String, String> LOADING_BILL_TEMPLATE_MAP = new HashMap<String, String>(4) {
        private static final long serialVersionUID = -8071653943396490581L;
        {
            put("范太克供应链管理（宁波）有限公司", "FTK_CONTRACT_LOADING_TEMPLATE");
            put("青岛中光亿云供应链管理有限公司", "QDZG_CONTRACT_LOADING_TEMPLATE");
            put("浙江网塑科技股份有限公司", "ZJWS_CONTRACT_LOADING_TEMPLATE");
            put("上海中光亿云供应链管理有限公司", "SHZG_CONTRACT_LOADING_TEMPLATE");
        }
    };
    /**
     * 签署印章类型
     */
    String SIGN_TYPE =  "signType";

    /**
     * 被通知人
     * DH:代恒，ZJ:张建
     */
    String DEAL_NOTIFIED_PARTY = "dealNotifiedParty";
    /**
     * 财务待办事项通知
     */
    String FINANCE_WAIT_DEAL = "financeWaitDeal";


    String COMPANY_GRADE_C = "C";
    String COMPANY_GRADE_D = "D";
    List<String> LIABILITY_COMPANY_GRADE = new ArrayList<String>(2) {
        private static final long serialVersionUID = -9098914210347116725L;
        {
            add(COMPANY_GRADE_C);
            add(COMPANY_GRADE_D);
        }
    };
    String TEMPLATETAG_LIABILITY = "liability";

    String COMPANY_NAME_FTK = "范太克供应链管理（宁波）有限公司";
    String COMPANY_NAME_FLK = "范伦克供应链管理（上海）有限公司";
    String COMPANY_NAME_NFJM = "南方经贸集团（上海）实业有限公司";
    String COMPANY_NAME_SHSL = "上海笙竺贸易有限公司";
    String COMPANY_NAME_SHKX = "上海锴旭网络科技有限公司";
    String COMPANY_NAME_SHCO = "上海宸瓯实业有限公司";
    String COMPANY_NAME_SRY = "塑融云（上海）科技有限公司";
    String COMPANY_NAME_ASY = "青岛奥顺宇供应链管理有限公司";
    String COMPANY_NAME_QDZG = "青岛中光亿云供应链管理有限公司";
    String COMPANY_NAME_WSNB = "网塑（宁波）供应链管理有限公司";
    String COMPANY_NAME_SDNH = "山东能化云链供应链发展有限公司";
    String COMPANY_NAME_SUGX = "苏州高新供应链管理有限公司";
    String COMPANY_NAME_AHZY = "安徽致远供应链管理有限公司";
    String COMPANY_NAME_ZJKR = "浙江康瑞供应链服务有限公司";
    String COMPANY_NAME_SHZG = "上海中光亿云供应链管理有限公司";
    String COMPANY_NAME_QDYS = "青岛云塑合创新材料有限公司";
    String COMPANY_NAME_QDGT = "青岛高投能源发展有限公司";
    String COMPANY_NAME_YYHB = "原阳县鸿博返乡创业服务有限公司";
    String COMPANY_NAME_ZJWS = "浙江网塑科技股份有限公司";

    String FUNDER_TYPE = "funderType";

    /**
     * 发货预警通知
     * LJ-卢君
     * ZJ-张健
     * ZF-赵飞
     */
    String DEAL_UN_DELIVRY_PARTY = "dealUnDelivryParty";

    /**
     * 推送类型-系统推送
     */
    String PUSH_TYPE_1 = "1";

    /**
     * 供应商等级对应的付款方式
     */
    String PAYMENT_METHOD_AND_GRADE = "paymentMethodAndGrade";
    /**
     * 供应商等级对应提货方式
     */
    String GRADE_ANDDELIVERY_TYPE = "gradeAndDeliveryType";

    /**
     * 虚拟库存状态key
     */
    String STOCK_VIRTUAL_STATUS = "stockVirtualStatus";
    /**
     * 虚拟库存类型 key
     */
    String CONTRACT_TYPE = "contractType";
    /**
     * 虚拟库存编号
     */
    String STOCKVIRTUAL_NO = "stockVirtualNo";

    String STOCK_VIRTUAL_KC = "KC";          //虚拟采购类型-库存-合同编号前缀
    String STOCK_VIRTUAL_XY = "XY";          //虚拟采购类型-协议-合同编号前缀

    String STOCK_VIRTUAL_STATUS_N = "N";     //虚拟库存状态-N新录入
    String STOCK_VIRTUAL_STATUS_I = "I";     //虚拟库存状态-I申请中
    String STOCK_VIRTUAL_STATUS_A = "A";     //虚拟库存状态-A已审批
    String STOCK_VIRTUAL_STATUS_S = "S";     //虚拟库存状态-S已签约
    String STOCK_VIRTUAL_STATUS_C = "C";     //虚拟库存状态-C已失效
    String STOCK_VIRTUAL_STATUS_F = "F";     //虚拟库存状态-F待释放
    String STOCK_VIRTUAL_STATUS_Y = "Y";     //虚拟库存状态-Y已释放

    String BUY_SOURCE_G = "G";               //采购来源-G供方
    String BUY_SOURCE_B = "B";               //采购来源-B采购报价库存
    String SELL_SOURCE_K = "K";              //销售来源-K客户
    String SELL_SOURCE_S = "S";              //销售来源-S销售报价库存

    String EVALUATE_USER_STATUS  = "evaluateUserStatus";

    /**
     * 考核申诉发送邮件 key
     */
    String EVALUATE_EMAIL_KEY = "evaluateEmailKey";

    /**
     * 投诉类型
     */
    String COMPLAINTS_TYPE = "complaintsType";

    /**
     * 投诉邮箱配置
     */
    String DICT_COMPLAINTS_CMEAIL = "complaintsEmail";

    /**
     * 公告部门
     */
    String ANNOUNCEMENT_DEPT_TYPE = "announcementDeptType";

    /**
     *
     */
    String DICT_STATISTICAL_TYPE = "statisticalType";

    /**
     * 发文企业
     */
    String ISSUIG_COMPANY = "issuingCompany";

    /**
     * 查看审批流程tab也数据字典
     */
    String APPROVEPERMISSION = "approvePermission";

    /**
     * 不计算企业得分列表
     */
    String NO_CALC_SCORE_COMPANY = "noCalcScoreCompany";

    /**
     * 不退回灰名单企业所属业务员id
     */
    String DICT_GRAY = "GRAY";
    /**
     * 不退回灰名单企业所属部门id
     */
    String DICT_GRAY_DEPT_ID = "grayDeptId";

    String DICT_FREED_TO_DEPT_LEADER = "freedToDeptLeader";


    String PROFIT_RATE_CREDIT = "profitRateCredit";
    String PROFIT_RATE = "profitRate";
    BigDecimal DEFAULT_CREDIT_PROFIT_RATE = BigDecimal.valueOf(0.0007);
    BigDecimal DEFAULT_PROFIT_RATE = BigDecimal.valueOf(0.001);

    String DICT_TYPE_CALCULATE_TYPE = "calculateType";      // 中间链中游单价计算类型
    String CALCULATE_TYPE_1 = "1";                          // 中间链中游单价计算类型 【固定收益X周期】
    String CALCULATE_TYPE_2 = "2";                          // 中间链中游单价计算类型 【固定收益X周期（税后）】
    String CALCULATE_TYPE_3 = "3";                          // 中间链中游单价计算类型 【固定年化X账期】
    String CALCULATE_TYPE_4 = "4";                          // 中间链中游单价计算类型 【固定加单价】
    String CALCULATE_TYPE_5 = "5";                          // 中间链中游单价计算类型 【销售单价-中游企业单价收益】

    /**
     * 奥顺宇账号
     */
    String ACCOUNT_ASY="accountAsy";

    String DICT_TYPE_CHAIN_PAY_TYPE = "chainPayType";       // 中间链中游合同付款方式
    String CHAIN_PAY_TYPE_1 = "1";                          // 合同盖章后发起
    String CHAIN_PAY_TYPE_2 = "2";                          // 下游客户回款后发起
    String CHAIN_PAY_TYPE_3 = "3";                          // 约定付款日期到达前一天发起

    /**
     * 税率
     */
    String DICT_TYPE_TAX_RATES = "taxRates";
    String DICT_TYPE_TAX_RATES_SL = "SL";


    String DICT_TYPE_BANK_DATA = "bankData";
    String DICT_COMPANY_NAME = "companyName";
    String DICT_TAX_NO  = "taxNo";
    String DICT_BANK_NAME = "bankName";
    String DICT_BANK_NUM = "bankNum";

    String DICT_BUSINESS_TYPE = "dictBusinessType";

    String DICT_MATTERS_TYPE = "mattersType";

    String DICT_OWN_REGION = "ownRegion";

    /**
     * 赊销
     */
    String PROFIT_TYPE_1 = "1";
    /**
     * 代采
     */
    String PROFIT_TYPE_2 = "2";
    /**
     * 代采赊销
     */
    String PROFIT_TYPE_5 = "5";

    /**
     * 区域数据字典
     */
    String BRANCH_CD = "branchCd";
    String OWN_REGION_ASSISTANT = "ownRegionAssistant";
    /**
     * 数据中台毛利指标值
     */
    String TARGET_TYPE = "targetType";

    String ZJWS_COMPANY_NAME = "浙江网塑科技股份有限公司";

    /**
     * 不参与报表统计事业部
     */
    String REPORT_NOT_DEPT = "reportNotDept";

    /**
     * 浙塑网站发起审批人
     */
    String DICT_CMS_APPLY_USER = "cmsApplyUser";

    /**
     * 浙塑网站审批回调URL
     */
    String CMS_ENTRUST_CALLBACK_URL = "/admin/biz_entrust_info/entrustCallback";

    /**
     * 离职人员部门ID
     */
    Long LZ_PERSON_DEPT_ID = 253L;
    String DICT_COMPANY_SEARCH_NOT_PUBLIC = "companySearchNotPublic";
    //公海退回时间
    String HIGH_SEAS_RETURN_TIME ="highSeasReturnTime";
    String OWN="own";
    String NOT_OWN="notOwn";

    String HD = "HD";
    String HN = "HN";
    String HB = "HB";
    String HDO = "HDO";

    String HZ = "HZ"; // 华中
    String NB = "NB"; 
    String JX = "JX";

    String YJ_BIG_SCREEN_PATH = "/dataShowIn/indexPlus";
    String MLR_BIG_SCREEN_PATH = "/dataShowIn/indexMlrPlus";
    String MLR_RANGE_BIG_SCREEN_PATH = "/dataShowIn/indexMlrPlusRange";
    String MLR_RANGE_BIG_SCREEN_PHONE_PATH = "/dataShowIn/indexMlrPlusRangePhone";
    String YJ_BIG_SCREEN_PHONE_PATH = "/dataShowIn/indexPlusPhone";
    String MLR_BIG_SCREEN_PHONE_PATH = "/dataShowIn/indexMlrPlusPhone";
    String OUT_BIG_SCREEN_PATH = "/dataShowOut/outLargeScreen/indexPlus";
    String OUT_BIG_SCREEN_PHONE_PATH = "/dataShowOut/outLargeScreen/indexPlusPhone";


    String AUTO_TYPE_XT="[系统]";

    // 人保申请状态
    String PICC_APPLY_STATUS_0 = "0"; // 未申请
    String PICC_APPLY_STATUS_1 = "1"; // 申请中
    String PICC_APPLY_STATUS_2 = "2"; // 已批复
    String PICC_APPLY_STATUS_3 = "3"; // 已拒绝
    
    // 中银申请状态
    String ZHONG_YIN_APPLY_STATUS_0 = "0"; // 未申请
    String ZHONG_YIN_APPLY_STATUS_1 = "1"; // 申请中
    String ZHONG_YIN_APPLY_STATUS_2 = "2"; // 已批复
    String ZHONG_YIN_APPLY_STATUS_3 = "3"; // 已拒绝

    // 是否需要人保限额申请
    String PICC_APPLY_CREDIT_AMOUNT_FLG_0 = "0"; // 否
    String PICC_APPLY_CREDIT_AMOUNT_FLG_1 = "1"; // 是

    interface SETTLEMENT_ENUM{
        String SETTLEMENT_STATUS_0 = "0";   //未确认
        String SETTLEMENT_STATUS_1 = "1";   //已确认
        String SETTLEMENT_STATUS_2 = "2";   //已审核
        String SETTLEMENT_STATUS_3 = "3";   //已结算

        String SETTLEMENT_STATUS_D = "D";   //完成
    }

    interface SETTLEMENT_AMOUNT_ENUM{
        String SETTLEMENT_STATUS_0 = "0";   //未结算
        String SETTLEMENT_STATUS_1 = "1";   //已结算

        String SETTLEMENT_TYPE_0 = "0";     //收货款
        String SETTLEMENT_TYPE_1 = "1";     //收逾期罚息
        String SETTLEMENT_TYPE_2 = "2";     //返还逾期罚息
    }

    /**
     * 合作模式
     **/
    String DICT_TYPE_COOPERATION_MODE = "cooperationMode";



    interface CFCA_SEAL_TYPE{
        String SEAL_TYPE_CTR = "CTR";       //合同章
        String SEAL_TYPE_OFC = "OFC";       //公章
        String SEAL_TYPE_LGS = "LGS";       //物流章
        String SEAL_TYPE_CPS = "CPS";       //法人章
    }

    /**
     * 作废类型
     */
    String DICT_INVALID_TYPE = "invalidType";

    List<String> CC_PROCESS_List = new ArrayList<String>(5) {{
        add(BasConstants.PROCESS_APPLY_BUY);
        add(BasConstants.PROCESS_APPLY_SELL);
        add(BasConstants.PROCESS_APPLY_MATCH);
        add(BasConstants.PROCESS_APPLY_MATCH_IOUS);
        add(BasConstants.PROCESS_APPLY_CHARGE_SALES);
    }};

    /**
     * 首页统计配置
     */
    String DICT_TYPE_INDEX_CONFIG = "indexConfig";
    /**
     * 毛利统计业务区域
     */
    String DICT_TYPE_INDEX_CONFIG_DEPTIDS = "deptIds";

    /**
     * 摘要金额计算配置项
     */
    String DICT_COMPUTE_SUBJECT_CONFIG = "computeSubjectConfig";

    /**
     * 摘要配置
     */
    String DICT_COMPUTE_SUBJECT_CONFIG_CONTAINS_STRING = "containsString";

    /**
     * 赊销模式 -- 普通模式
     */
    String CONTRACT_MODEL_PT = "PT";

    /**
     * 赊销模式 -- 保理模式
     */
    String CONTRACT_MODEL_BL = "BL";

    /**
     * 赊销模式 -- 货到付款
     */
    String CONTRACT_MODEL_HDFK = "HDFK";

    /**
     * 代采赊销模式 -- 普通模式
     */
    String CONTRACT_MODEL_DCSX = "DCSX";

    /**
     * 代采赊销模式 -- 保理模式
     */
    String CONTRACT_MODEL_DCSXBL = "DCSXBL";

    /**
     * 代采赊销模式 -- 货到付款
     */
    String CONTRACT_MODEL_DCSXHDFK = "DCSXHDFK";
    /**
     * 代采赊销模式 -- 代采出口
     */
    String CONTRACT_MODEL_DCSXCK = "DCSXCK";

    interface BusinessKind {

        /**
         * 业务类型 -- 赊销代采
         */
        String DICT_SXDC = "SXDC";

        /**
         * 业务类型 -- 赊销代采货到付款
         */
        String DICT_SXDCHDFK = "SXDCHDFK";

        /**
         * 业务类型 -- 代采赊销
         */
        String DICT_DCSX = "DCSX";

        /**
         * 业务类型 -- 代采赊销货到付款
         */
        String DICT_DCSXHDFK = "DCSXHDFK";

        /**
         * 业务类型 -- 代采赊销货到付款
         */
        String DICT_DCSXCK = "DCSXCK";

        /**
         * 业务类型 -- 代采赊销保理
         */
        String DICT_DCSXBL = "DCSXBL";

        /**
         * 业务类型 -- 代采
         */
        String DICT_DC = "DC";

        /**
         * 业务类型 -- 赊销
         */
        String DICT_SX = "SX";

        /**
         * 业务类型 -- 赊销货到付款
         */
        String DICT_SXHDFK = "SXHDFK";

        /**
         * 业务类型 -- 赊销保理
         */
        String DICT_SXBL = "SXBL";

        /**
         * 业务类型 -- 自营采购
         */
        String DICT_ZYCG = "ZYCG";

        /**
         * 业务类型 -- 自营销售
         */
        String DICT_ZYXS = "ZYXS";

        /**
         * 业务类型 -- 代采托盘
         */
        String DICT_DCTP = "DCTP";
    }

    /**
     * 业务类型数据字典
     */
    String DICT_BUSINESS_KIND = "businessKind";

    /**
     * 业务类型数据字典
     */
    String DICT_REGION_CONTRAST = "regionContrast";

    /**
     * 业务类型数据字典
     */
    String DICT_REGION_CONTRAST_WECHAT = "regionContrastWechat";

    /**
     * 企业微信消息推送指定部门id数据字典
     */
    String EWECHAT_MESSAGE_DEPT = "ewechatMessageDept";

    /**
     * 企业微信消息推送指定部门id数据字典key
     */
    String MESSAGE_PUCH_DEPT = "dept";

    /**
     * 企业微信询价消息消息推送黑名单
     */
    String MESSAGE_BLACK_LIST = "ewechatMessageBlackList";

    /**
     * 企业微信询价消息消息推送白名单
     */
    String MESSAGE_WHITE_LIST = "ewechatMessageWhiteList";

    /**
     * 公司证照用途
     */
    String COMPANY_LICENSE_USER_TYPE = "companyLicenseUserType";

    /**
     * 文件类型
     */
    String COMPANY_LICENSE_FILE_TYPE = "companyLicenseFileType";

    /**
     * 收款账户
     */
    String FILE_TYPE_4 = "4";

    /**
     * 文件协议类型 RL: 催款函 LN: 逾期滞纳金告知函 CP: 取消协议 RA: 还款协议 SP: 合同补充协议 RP: 付款提示函 DZ: 对账单
     */
    String DICT_DOC_TYPE = "docType";
    String DICT_DOC_TYPE_RL = "RL";
    String DICT_DOC_TYPE_LN = "LN";
    String DICT_DOC_TYPE_CP = "CP";
    String DICT_DOC_TYPE_RA = "RA";
    String DICT_DOC_TYPE_SP = "SP";
    String DICT_DOC_TYPE_RP = "RP";
    String DICT_DOC_TYPE_DZ = "DZ";

    /**
     * 资金方流水类型
     */
    String FUND_FLOW_TYPE = "fundFlowType";

    /**
     * 结算状态
     */
    String DICT_SETTLEMENT_STATUS = "settlementStatus";

    /**
     * 结算数据不包含部门
     */
    String DICT_SETTLEMENT_NOT_DEPT = "settlementNotDept";
    String AUTO_APPLY_CENTER_SWITCH = "autoApplyCenterSwitch";
    String SWITCH = "switch";

    String DICT_PERSON_COST_CHART_BRANCH_CD = "personCostChartBranchCd";

    /**
     * 资金方保费流水类型
     */
    String INSURANCE_AMOUNT_FLOW_TYPE = "insuranceAmountFlowType";
    // 保费流水类型 充值
    String DICT_TYPE_INSURANCE_AMFL_T ="T";
    // 保费流水类型 手动扣款
    String DICT_TYPE_INSURANCE_AMFL_D ="D";
    // 保费流水类型 销售合同
    String DICT_TYPE_INSURANCE_AMFL_S ="S";
    // 保费流水类型 合同作废
    String DICT_TYPE_INSURANCE_AMFL_C ="C";

    /**
     * 授信类别
     */
    String DICT_CREDIT_TYPE = "creditType";
    /**
     * 授信类别 0-人保
     */
    String CREDIT_TYPE_0 = "0";
    /**
     * 授信类别 1-大地
     */
    String CREDIT_TYPE_1 = "1";
    /**
     * 授信类别 2-中银
     */
    String CREDIT_TYPE_2 = "2";
    /**
     * 授信类别 9-自主
     */
    String CREDIT_TYPE_9 = "9";

    /**
     * 资金方余额我方抬头
     */
    String DICT_FUNDER_OUR_COMPANY_NAME = "funderOurCompanyName";
    /**
     * 商品和服务税收编码
     */
    String GOODS_SERVICES_TAX_CODE = "goodsServicesTaxCode";
    /**
     * 财务开票管理税务导出税率
     */
    String INVOICE_DETAIL_TAX_RATE = "invoiceDetailTaxRate";

    /**
     * 开票管理导出税务，两个excel 前面表头
     */
    String INVOICE_EXCEL_DETAIL = "填表说明：\n" +
            "1、全部数据使用文本输入；\n" +
            "2、系统将根据发票流水号将发票明细与发票基本信息进行关联；\n" +
            "3、表格中数量单价与金额填写规则为：金额为必填项，填写“数量”、“单价”的任意一项时，导入后系统将自动依据填列的两项计算未填写项，计算规则为“金额”=“单价”×“数量”；\n" +
            "4、即征即退类型：“有效增值税即征即退备案信息纳税人”在发票开具时若选择的商编在商编表中“即征即退”列非空且该条明细属于增值税即征即退收入时，为必填项；";
    String INVOICE_EXCEL_INFO = "填表说明：\n" +
            "1、仅支持数电票的导入开具，不支持纸质发票批量导入开具；\n" +
            "2、发票流水号：纳税人自定义，长度不超过20位；发票流水号为导入开具区分发票的唯一标识，用于关联发票的明细信息、特定业务信息、附加要素信息；\n" +
            "3、发票类型：增值税专用发票、普通发票；\n" +
            "4、不支持差额开具和减按开具模式；\n" +
            "5、当开具的发票类型为专票时，购买方纳税人识别号必填。当开具的发票类型为普票时，若购买方为企业的，购买方纳税人识别号必填。\n" +
            "6、所有内容输入均为文本格式输入；\n" +
            "7、若开具发票需要使用邮箱推送时：可填写购买方电子邮箱；\n" +
            "8、“放弃享受减按1%征收率原因”填写说明：您在2023年1月1日以后取得的适用3%征收率的应税销售收入，可减按1%征收率征收增值税。若您有特殊情况，需要开具其他发票，请在【放弃享受减按1%征收率原因】字段中选择相应原因（小规模纳税人开具增值税普通发票可不填写此项）。\n" +
            "9、含税标志: 在填写sheet页“2-发票明细信息”中,根据实际业务需要,当单价、金额为含税时,选择“是”,当单价、金额为不含税时,选择“否”。\n" +
            "10、开具除特定业务外的普通发票：1、如受票方（发票抬头）为自然人，请根据实际需要提供姓名或姓。如您的姓名为张某某，可在名称栏次填写：张某某、张先生或张女士；2、如受票方（发票抬头）为自然人，并要求能将发票归集在个人票夹中展示，需要提供姓名及身份证号码（自然人纳税人识别号）；3、如受票方（发票抬头）为个体工商户，需提供统一社会信用代码或纳税人识别号，并在受票方自然人标识栏次选择“否”。\n" +
            "11、当“特定业务类型”为“农产品收购”、“光伏收购”、“报废产品收购”时,本模板中填写的“购买方名称”指实际的销售方名称，“购买方纳税人识别号”指实际的销售方纳税人识别号。\n" +
            "12、当“特定业务类型”为“农产品收购”、“报废产品收购”时，“购买方名称”、“证件类型”、“购买方纳税人识别号”为必填项。\n" +
            "13、当“特定业务类型”为“报废产品收购”时，“购买方地址”、“购买方电话”为必填项。\n" +
            "14、当“特定业务类型”为“报废产品收购”且商品和服务编码包含“报废机动车”类（即1110700000000000000、1110701000000000000、1110702000000000000、1110799000000000000之一）时，本模版中“报废产品销售类型”为必填，请根据真实业务情况选择（如为向出售者收购其自用的报废机动车，选择“销售自己使用过的报废产品”，其他情形请选择“销售收购的报废产品”）；开具非“报废机动车”类商品和服务编码无需填写。";
    // 四大区域
    String REGION_MONTH_SALES_AREA = "regionMonthSalesArea";
    // 省份统计客户销售额对照
    String PROVINCE_CUSTOMER_SALES_AREA = "provinceCustomerSalesArea";

    /**
     * 授信类别 0-人保
     */
    String CREDIT_TYPE_NAME_0 = "人保";
    /**
     * 授信类别 1-大地
     */
    String CREDIT_TYPE_NAME_1 = "大地";
    /**
     * 授信类别 2-中银
     */
    String CREDIT_TYPE_NAME_2 = "中银";
    /**
     * 授信类别 9-自主
     */
    String CREDIT_TYPE_NAME_9 = "自主";
    // 额度审批
    String APPLY_QUOTA_TYPE_Q="Q";
    // 额度提额
    String APPLY_QUOTA_TYPE_T="T";
    // 客户来源
    String DICT_TYPE_COMPANY_SOURCE="companySource";
    // 所属行业
    String DICT_TYPE_COMPANY_INDUSTRY="industry";
    // 协议采购权限
    String DICT_TYPE_PROTOCOL_PURCHASE="protocolPurchase";
    //中上游付款申请自动化开关
    String AUTO_APPLY_PAY_SWITCH="autoApplyPaySwitch";
    //中上游付款申请自动化开关
    String AUTO_APPLY_PAY_OUR_COMPANY="autoApplyPayOurCompany";

    /**
     * 人保配置
     */
    String DICT_PICC_CONFIG_PARAM = "piccConfigParam";
    
    /**
     * 能报送人保时间节点 can
     */
    String DICT_PICC_CONFIG_PARAM_STATE_DATE = "start_date";

    /**
     * 采购中心副总经理
     */
    Long WU_FAN_USER_ID = 383L;
}
