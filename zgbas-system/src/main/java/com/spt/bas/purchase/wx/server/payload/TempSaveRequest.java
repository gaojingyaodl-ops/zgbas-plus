package com.spt.bas.purchase.wx.server.payload;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-21 10:23
 */
@Data
public class TempSaveRequest extends BaseRequest {
    private String saveType;
}
