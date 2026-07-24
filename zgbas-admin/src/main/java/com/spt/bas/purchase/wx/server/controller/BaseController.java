package com.spt.bas.purchase.wx.server.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-13 14:22
 */
public class BaseController {
    /**
     * 返回map封装
     * @param returnKey
     * @param returnObj
     * @return
     */
    Map<String, Object> returnMap(String returnKey, Object returnObj) {
        Map<String, Object> result = new HashMap<>(1);
        result.put(returnKey, returnObj);
        return result;
    }

    /**
     * 组装返回map封装
     *
     * @param result
     * @param returnKey
     * @param returnObj
     * @return
     */
    Map<String, Object> assembleReturnMap(Map<String, Object> result, String returnKey, Object returnObj) {
        result.put(returnKey, returnObj);
        return result;
    }
}
