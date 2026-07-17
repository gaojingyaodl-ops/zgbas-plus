package com.spt.bas.server.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.hsoft.push.sdk.remote.PushClientHttp;
import com.hsoft.push.sdk.vo.PushRequest;
import com.hsoft.push.sdk.vo.PushResponse;
import com.hsoft.push.sdk.vo.PushTarget;
import com.spt.bas.client.entity.AssComplaints;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.EvaluateUser;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.date.DateOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>
 *  校验手机号是否合规的工具类
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-16 09:01
 */
@Slf4j
@Component
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


//    /**
//     * 发送短信
//     * @param sendType 发送类型
//     * @param phone 手机号
//     * @param checkCodeOrInitPassword 验证码或者是初始密码
//     */
//    public static void send(String sendType,String phone,String checkCodeOrInitPassword) {
//        //  发送初始密码给用户
//        PushRequest req = new PushRequest();
//        req.setModule("S");
//        req.setPushType(sendType);//收款通知
//        req.setSubmitUserId("sys");
//        List<PushTarget> lst = new ArrayList<>();
//        lst.add(new PushTarget(null, phone, null));
//        req.setTargets(lst);
//        Map<String, Object> param = new HashMap<>();
//        if (StrUtil.equals(sendType, Constant.SEND_SMS_TYPE_CHECK_CODE)) {
//            param.put("checkCode", checkCodeOrInitPassword);
//        } else if (StrUtil.equals(sendType, Constant.SEND_SMS_TYPE_INIT_PASSWORD)) {
//            param.put("password", checkCodeOrInitPassword);
//        }
//        req.setParam(param);
//        smsUtils.pushRemote.send(req);
//    }
    /**
     * 合同盖章签署通知
     */
    public static void sendContractNo (String phone,String contractNo){
        try {
            PushRequest req = new PushRequest();
            req.setModule("S");
            req.setPushType("contractSignNotice");//合同盖章
            req.setSubmitUserId("sys");
            List<PushTarget> lst = new ArrayList<>();
            lst.add(new PushTarget(null, phone, null));
            req.setTargets(lst);
            Map<String, Object> param = new HashMap<>();
            param.put("contractNo",contractNo);
            req.setParam(param);
            smsUtils.pushRemote.send(req);
        }catch (Exception e){
            log.error("sendContractNo error :{}" , e.getMessage());
        }
    }

    /**
     * 邮件通知
     */
    public static void sendConfirmReceiptEmailNotification(String contractNo, String createUserName,String companyName,String email){
        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setPushType("emailConfirmNotification");//合同盖章
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        lst.add(new PushTarget(null, null, email));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("contractNo",contractNo);
        param.put("createUserName",createUserName);
        param.put("companyName",companyName);
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH-mm-ss");
        String format = sdf.format(new Date());
        param.put("Date",format);
        req.setParam(param);
        smsUtils.pushRemote.send(req);
    }


    /**
     * 收货确认邮件通知
     */
    public static void sendEmailNotification(String contractNo, String createUserName,String companyName,String email){
        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setPushType("emailNotification");//合同盖章
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        lst.add(new PushTarget(null, null, email));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("contractNo",contractNo);
        param.put("createUserName",createUserName);
        param.put("companyName",companyName);
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());
        param.put("Date",format);
        req.setParam(param);
        smsUtils.pushRemote.send(req);
    }

    /**
     * 货物签收签署通知
     */
    public static void sendReceiveGoodsContractNo (String phone,String contractNo){

        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setPushType("receiveGoodsNotice");//合同盖章
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        lst.add(new PushTarget(null, phone, null));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("contractNo",contractNo);
        req.setParam(param);
        smsUtils.pushRemote.send(req);
    }

    /**
     * 开户短信通知
     */
    public static void sendEmailCreateAccount(String  companyname,String phone){
        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setPushType("axqNotification");
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        lst.add(new PushTarget(null, null, phone));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("companyname",companyname);
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH-mm-ss");
        String format = sdf.format(new Date());
        param.put("Date",format);
        req.setParam(param);
        smsUtils.pushRemote.send(req);
    }

    /**
     * 通用短信验证码
     * @param phone
     * @param title
     * @param message
     */
    public static PushResponse sendVerificationCode(String phone, String title, String message){
        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setBusinessId(phone);
        req.setPushType("sendVerificationCode");
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        lst.add(new PushTarget(null, phone, null));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("title",title);
        param.put("message",message);
        req.setParam(param);
        return smsUtils.pushRemote.send(req);
    }

    /**
     * 发货预警通知
     */
    public static void sendUnDeliveryEmail(CtrContract contract, String deliveryModeStr, String deliveryTypeStr, String deliveryDateStr, String statusStr, List<String> emailList) {
        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setPushType("sendUnDeliveryEmail");
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        emailList.forEach(e -> lst.add(new PushTarget(null, null, e)));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("contractNo", contract.getContractNo());
        param.put("companyName", contract.getCompanyName());
        param.put("productNames", contract.getProductsName());
        param.put("deliveryMode", deliveryModeStr);
        param.put("deliveryType", deliveryTypeStr);
        param.put("totalNumber", contract.getTotalNumber());
        param.put("totalAmount", contract.getTotalAmount());
        param.put("deliveryDateStr", deliveryDateStr);
        param.put("warehouseNumber", contract.getWarehouseNumber());
        param.put("statusStr", statusStr);
        param.put("matchUserName", contract.getMatchUserName());
        req.setParam(param);
        smsUtils.pushRemote.send(req);
    }


    public static void setApprovePushEmail(PmApprove approve, List<String> emailList) {
        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setPushType("setApprovePushEmail");
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        emailList.forEach(e -> lst.add(new PushTarget(null, null, e)));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("approveNo", approve.getApproveNo());
        param.put("processName", approve.getProcessName());
        param.put("title", approve.getSubject());
        param.put("matchUserName", approve.getCreateUserName());
        param.put("applyDate", DateOperator.formatDate(approve.getCreatedDate()));
        req.setParam(param);
        smsUtils.pushRemote.send(req);
    }

    /**
     * 考核申诉邮件发送
     * @param evaluateUserApproveWaitDealVo
     */
    public static void setEvaluateAppealPushEmail(EvaluateUserApproveWaitDealVo evaluateUserApproveWaitDealVo) {
        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setPushType("sendEvaluateEmail");
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        List<String> emailList = evaluateUserApproveWaitDealVo.getEnterpriseAppealEmail();
        EvaluateUser evaluateUser = evaluateUserApproveWaitDealVo.getEvaluateUser();
        emailList.forEach(e -> lst.add(new PushTarget(null, null, e)));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("evaluateUserName", evaluateUser.getUserName());// 申诉人名字
        param.put("evaluateMonth", evaluateUser.getEvaluateMonth());// 考核年月
        param.put("deptName", evaluateUser.getDeptName());// 申诉人部门名字
        param.put("score", evaluateUser.getScore().toString());// 评分总和
        param.put("evaluateDate", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));// 申诉日期
        param.put("evaluateRemark", evaluateUser.getEvaluateRemark());// 上级评语
        param.put("appealRemark", evaluateUserApproveWaitDealVo.getAppealRemark());// 申诉详情
        req.setParam(param);
        smsUtils.pushRemote.send(req);
    }

    /**
     * 投诉通知
     */
    public static void sendComplaintsEmail(AssComplaints assComplaints, List<String> emailList) {
        PushRequest req = new PushRequest();
        req.setModule("S");
        if (StringUtils.isNotBlank(assComplaints.getToUserName())) {
            req.setPushType("sendComplaintsEmailUser");
        } else if (StringUtils.isNotBlank(assComplaints.getToDeptName())) {
            req.setPushType("sendComplaintsEmailDept");
        } else {
            req.setPushType("sendComplaintsEmail");
        }
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        emailList.forEach(e -> lst.add(new PushTarget(null, null, e)));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("fromUserName", assComplaints.getFromUserName());
        param.put("createdDate", DateOperator.formatDate(assComplaints.getCreatedDate()));
        param.put("toUserName", assComplaints.getToUserName());
        param.put("toDeptName", assComplaints.getToDeptName());
        param.put("subject", assComplaints.getSubject());
        param.put("content", assComplaints.getContent());
        req.setParam(param);
        smsUtils.pushRemote.send(req);
    }
}
