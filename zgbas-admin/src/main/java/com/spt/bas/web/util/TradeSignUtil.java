package com.spt.bas.web.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * bas-trade 签名工具类
 *
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2025/6/19 9:12
 */
public class TradeSignUtil {
    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * 生成签名
     *
     * @param appSecret 应用密钥
     * @param timestamp 时间戳
     * @return 签名
     */
    public static String generateSignature(String appSecret, long timestamp, String appId) {
        try {
            // 1. 参数按key字典序排序
//            params = params == null ? Collections.emptyMap() : params;
//            Map<String, Object> sortedParams = new TreeMap<>();
//            for (Map.Entry<String, Object> entry : params.entrySet()) {
//                sortedParams.put(entry.getKey(), formatValue(entry.getValue()));
//            }
            // 2. 拼接键值对
            StringBuilder sb = new StringBuilder();
//            for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
//                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//            }
            // 添加时间戳
            sb.append("timestamp=").append(timestamp);
            sb.append("&appId=").append(appId);

            // 3. 使用HMAC-SHA256加密
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKey);
            byte[] hashBytes = mac.doFinal(sb.toString().getBytes(StandardCharsets.UTF_8));

            // 4. Base64编码
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }

    /**
     * 验证签名
     *
     * @param appSecret 应用密钥
     * @param timestamp 时间戳
     * @param signature 待验证签名
     * @return 是否验证通过
     */
    public static boolean verifySignature(String appSecret, long timestamp, String signature, String appId) {
        String generatedSign = generateSignature(appSecret, timestamp, appId);
        return generatedSign.equals(signature);
    }

    // 添加展平嵌套对象的方法
    public static Map<String, Object> flattenMap(Map<String, Object> map) {
        Map<String, Object> flattened = new HashMap<>();
        if (map == null){
            return flattened;
        }
        flattenMap("", map, flattened);
        return flattened;
    }

    private static void flattenMap(String prefix, Map<String, Object> source, Map<String, Object> target) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                flattenMap(key, nestedMap, target);
            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) value;
                for (int i = 0; i < list.size(); i++) {
                    String listKey = key + "[" + i + "]";
                    Object item = list.get(i);
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> nestedMap = (Map<String, Object>) item;
                        flattenMap(listKey, nestedMap, target);
                    } else {
                        target.put(listKey, formatValue(item));
                    }
                }
            } else {
                target.put(key, formatValue(value));
            }
        }
    }

    /**
     * 格式化值，特别处理数字类型保留小数位数
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "";
        }

        // 特别处理BigDecimal和Double类型
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        } else if (value instanceof Double) {
            // 检查是否是整数
            double d = (Double) value;
            if (d == (long) d) {
                return String.format("%d.000000", (long) d);
            }
            return String.valueOf(d);
        } else if (value instanceof Float) {
            // 检查是否是整数
            float f = (Float) value;
            if (f == (long) f) {
                return String.format("%d.000000", (long) f);
            }
            return String.valueOf(f);
        } else if (value instanceof Number) {
            return value.toString();
        }
        return value.toString();
    }
}
