package com.spt.bas.purchase.wx.server.util;

import cn.hutool.core.util.StrUtil;
import com.hsoft.push.sdk.remote.PushClientHttp;
import com.hsoft.push.sdk.vo.PushRequest;
import com.hsoft.push.sdk.vo.PushTarget;
import com.spt.bas.purchase.wx.server.common.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>
 *  校验手机号是否合规的工具类
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-16 09:01
 */
// Phase 8 (D-P8-01 minimal fix, behavior-equivalent): explicit bean name disambiguates this from
// com.spt.bas.server.util.SMSUtils (both @Component, simple name SMSUtils → default bean name
// "SMSUtils" collides → ConflictingBeanDefinitionException). Injected by type (WX UserService
// imports this package) — name change is safe. Source isolated via separate apps; monolith's
// broad com.spt scan requires explicit name. Mirrors FileController precedent. No semantic change.
@Component("wxSMSUtils")
public class SMSUtils {
    //校验手机是否合规 2020年最全的国内手机号格式
    private static final String REGEX_MOBILE = "((\\+86|0086)?\\s*)((134[0-8]\\d{7})|(((13([0-3]|[5-9]))|(14[5-9])|15([0-3]|[5-9])|(16(2|[5-7]))|17([0-3]|[5-8])|18[0-9]|19(1|[8-9]))\\d{8})|(14(0|1|4)0\\d{7})|(1740([0-5]|[6-9]|[10-12])\\d{7}))";

    @Autowired
    private PushClientHttp pushRemote;

    private static SMSUtils smsUtils;

    @PostConstruct
    public void init() {
        smsUtils = this;
        smsUtils.pushRemote = this.pushRemote;
    }

    /**
     * 校验手机号
     *
     * @param phone 手机号
     * @return boolean true:是  false:否
     */
    public static boolean isMobile(String phone) {
        if (StrUtil.isEmpty(phone)) {
            return false;
        }
        return Pattern.matches(REGEX_MOBILE, phone);
    }


    /**
     * 发送短信
     * @param sendType 发送类型
     * @param phone 手机号
     * @param checkCodeOrInitPassword 验证码或者是初始密码
     */
    public static void send(String sendType,String phone,String checkCodeOrInitPassword) {
        //  发送初始密码给用户
        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setPushType(sendType);//收款通知
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        lst.add(new PushTarget(null, phone, null));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        if (StrUtil.equals(sendType, Constant.SEND_SMS_TYPE_CHECK_CODE)) {
            param.put("checkCode", checkCodeOrInitPassword);
        } else if (StrUtil.equals(sendType, Constant.SEND_SMS_TYPE_INIT_PASSWORD)) {
            param.put("password", checkCodeOrInitPassword);
        }
        req.setParam(param);
        smsUtils.pushRemote.send(req);
    }
}
