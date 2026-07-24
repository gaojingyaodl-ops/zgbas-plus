package com.spt.bas.purchase.wx.server.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *     请求参数基类
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-17 13:56
 */
@Data
public class BaseRequest {

    /**
     * 用户id
     */

    private String userId;

}
