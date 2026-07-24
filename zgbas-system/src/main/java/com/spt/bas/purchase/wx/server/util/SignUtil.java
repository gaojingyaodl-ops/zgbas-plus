package com.spt.bas.purchase.wx.server.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 签名工具类
 *
 * @author xuguiyi
 * @className SignUtil
 * @Description
 * @contact
 * @date 2016-6-7 下午11:11:00
 */
public class SignUtil {
    public static String getSign(Map<String, Object> reqParams, String merchKey) throws Exception {
        StringBuilder signSrc = new StringBuilder();
        List<String> keys = new ArrayList<>(reqParams.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Object value = reqParams.get(key);
            if (key != null && !"".equals(key) && value != null && !"sign".equals(key)) {
                signSrc.append(key).append("=").append(value);
            }
        }
        signSrc.append(merchKey); // 拼接商户key
        String sign = SM3.byteArrayToHexString(SM3.hash(signSrc.toString().getBytes()));
        return sign;
    }
}
