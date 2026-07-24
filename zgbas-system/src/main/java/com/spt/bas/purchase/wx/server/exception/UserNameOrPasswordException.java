package com.spt.bas.purchase.wx.server.exception;

import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *  用户名密码错误异常
 * </p>
 *
 * @ClassName: UserNameOrPasswordException
 * @Author: shengong
 * @Date: Created in 2020-09-15 16:10
 */
@EqualsAndHashCode(callSuper = true)
public class UserNameOrPasswordException extends BaseException {
    public UserNameOrPasswordException(Status status) {
        super(status);
    }

    public UserNameOrPasswordException(Status status, Object data) {
        super(status, data);
    }

    public UserNameOrPasswordException(Integer code, String message) {
        super(code, message);
    }

    public UserNameOrPasswordException(Integer code, String message, Object data) {
        super(code, message, data);
    }
}
