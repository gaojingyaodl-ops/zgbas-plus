// Phase 4 stub — Phase 5 will overlay with complete source version
package com.spt.bas.purchase.wx.server.exception;

import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import lombok.EqualsAndHashCode;

/**
 * SecurityException
 * @ClassName SecurityException
 * @Author shengong
 * @Date 2020-07-23 21:56
 */
@EqualsAndHashCode(callSuper = true)
public class SecurityException extends BaseException {

    public SecurityException(Status status) {
        super(status);
    }

    public SecurityException(Status status, Object data) {
        super(status, data);
    }

    public SecurityException(Integer code, String message) {
        super(code, message);
    }

    public SecurityException(Integer code, String message, Object data) {
        super(code, message, data);
    }
}
