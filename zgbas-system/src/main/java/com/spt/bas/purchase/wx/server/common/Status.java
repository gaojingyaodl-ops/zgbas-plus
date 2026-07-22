package com.spt.bas.purchase.wx.server.common;

// Phase 4 stub — Phase 5 will overlay with complete source version

/**
 * 通用状态码
 */
public enum Status {
    /**
     * 操作成功！
     */
    SUCCESS(200, "操作成功！"),

    /**
     * 操作异常！
     */
    ERROR(500, "操作异常！"),

    /**
     * 退出成功！
     */
    LOGOUT(200, "退出成功！"),

    /**
     * 请先登录！
     */
    UNAUTHORIZED(401, "请先登录！"),

    /**
     * 暂无权限访问！
     */
    ACCESS_DENIED(403, "权限不足！"),

    /**
     * 请求不存在！
     */
    REQUEST_NOT_FOUND(404, "请求不存在！"),

    /**
     * 请求方式不支持！
     */
    HTTP_BAD_METHOD(405, "请求方式不支持！"),

    /**
     * 请求异常！
     */
    BAD_REQUEST(400, "请求异常！"),

    /**
     * 参数不匹配！
     */
    PARAM_NOT_MATCH(400, "参数不匹配！"),

    /**
     * 参数不能为空！
     */
    PARAM_NOT_NULL(400, "参数不能为空！"),

    APPLICATION_EXCEPTION(400, "APPLICATION异常！"),

    /**
     * 获取短链接失败！
     */
    GET_SHORT_URL_ERROR(400, "获取短链接失败，请确认当前登录账号是否为企业经办人!"),
    /**
     * 当前用户已被锁定，请联系管理员解锁！
     */
    USER_DISABLED(403, "当前用户已被锁定，请联系管理员解锁！"),

    /**
     * 用户名或密码错误！
     */
    USERNAME_PASSWORD_ERROR(5001, "用户名或密码错误！"),
    /**
     * 验证码错误
     */
    CHECKCODE_ERROR(5002, "验证码错误！"),

    /**
     * token 已过期，请重新登录！
     */
    TOKEN_EXPIRED(5003, "token 已过期，请重新登录！"),

    /**
     * token 解析失败，请尝试重新登录！
     */
    TOKEN_PARSE_ERROR(5004, "token 解析失败，请尝试重新登录！"),

    /**
     * 当前用户已在别处登录，请尝试更改密码或重新登录！
     */
    TOKEN_OUT_OF_CTRL(5005, "当前用户已在别处登录，请尝试更改密码或重新登录！"),

    /**
     * 无法手动踢出自己，请尝试退出登录操作！
     */
    KICKOUT_SELF(5006, "无法手动踢出自己，请尝试退出登录操作！"),

    /**
     * 短信发送太频繁，请稍后尝试
     */
    SMS_SEND_BUSY(5006, "短信发送太频繁，请稍后尝试！"),

    /**
     * 手机格式不正确
     */
    PHONE_WRONGFUL(5007, "手机格式不正确！"),

    /**
     * 注册时账号已存在
     */
    REGISTER_EXIST(5008, "账号已存在！"),

    /**
     * 账号不存在
     */
    ACCOUNT_NOT_EXIST(5009, "账号不存在！"),

    /**
     * 无法取到token
     */
    NO_ACCESS_TOKEN(5010, "无法取到token！"),

    /**
     * 手机号不能为空
     */
    LOGIN_NO_PHONE(5011, "手机号不能为空！"),

    /**
     * 微信登录时没有appid
     */
    WX_LOGIN_NO_APPID(5012, "微信登录时没有appid"),

    /**
     * 微信登录时没有code
     */
    WX_LOGIN_NO_CODE(5013, "微信登录时没有code"),

    /**
     * 图片识别错误
     */
    OCR_ERROR(5014, "图片识别错误"),

    /**
     * 获取临时保存信息时 异常
     */
    NO_SAVE_INFO(5015, "没有保存相关信息"),

    /**
     * 合并模板异常
     */
    MERGE_TEMPLATE_FAIL(5016, "合并模板异常"),

    /**
     * 图片转化失败
     */
    IMAGE_CONVERT_FAIL(5017, "图片转化失败 "),

    /**
     * 营业执照无法识别
     */
    LICENSE_OCR_FAIL(5018,"营业执照无法识别"),

    /**
     * 无法查询到企业信息
     */
    SEARCH_COMPANY_INFO_FAIL(5019,"无法查询到企业信息"),

    /**
     * 该公司已绑定过用户，无法继续绑定
     */
    CHECK_BIND_FAIL(5020,"该公司已绑定过用户，无法继续绑定"),

    /**
     * 用户还没有保存
     */
    USER_NOT_SAVE(5021,"用户还没有保存"),

    /**
     * 还未绑定企业，无法申请CFCA
     */
    CFCA_FAIL(5022,"还未绑定企业，无法申请CFCA"),

    /**
     * 服务费金额设置错误
     */
    CFCA_FEE_FAIL(5023,"服务费金额设置错误"),

    /**
     * 你没有权限删除此仓库
     */
    DELETE_WAREHOUSE_FAIL(5024,"你没有权限删除此仓库"),

    /**
     * 仓库id不能为空
     */
    WAREHOUSE_NOT_ID(5025, "仓库id不能为空"),

    /**
     * 你没有权限修改此仓库
     */
    UPDATE_WAREHOUSE_FAIL(5026, "你没有权限修改此仓库"),

    /**
     * 公司不存在
     */
    NO_COMPANY(5027, "公司不存在"),

    /**
     * 该账号还未绑定过企业
     */
    USER_NOT_BIND(5028, "该账号还未绑定过企业"),

    /**
     *
     */
    USER_HAS_BIND(5029, "该企业已绑定过，无法绑定其他账号"),

    /**
     * 请检查与上一步上传的营业执照是否是同一个营业执照
     */
    CHECK_LICENSE_FAIL(5030, "请检查与上一步上传的营业执照是否是同一个营业执照"),

    /**
     * 没有绑定业务员
     */
    BIND_PARTNER_FAIL(5031, "您还没有绑定业务员"),

    /**
     * 该业务员没有手机号
     */
    PARTNER_NO_PHONE(5032, "该业务员没有手机号"),

    /**
     * 暂时只支持身份证
     */
    OCR_ONLY_ID_CARD(5033, "暂时只支持身份证"),

    /**
     * 审批发起错误
     */
    APPLY_FAIL(5034, "审批发起错误"),

    /**
     * 审批发起校验不通过
     */
    APPLY_CHECK_FAIL(5035, "审批发起校验不通过"),

    /**
     * 企业还未绑定
     */
    COMPANY_NOT_BIND(5036, "企业还未绑定"),

    /**
     * 登录时没有loginType参数
     */
    LOGIN_NO_LOGINTYPE(5012, "登录时没有传LoginType参数"),
    LOGIN_NO_CODE(5037, "登录时没有传微信code参数"),
    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("Status:{code=%s, message=%s}", getCode(), getMessage());
    }
}
