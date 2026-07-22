// Phase 4 stub — Phase 5 will overlay with complete source version
package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.purchase.wx.server.entity.WxAccessToken;
import com.spt.tools.jpa.service.IBaseService;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 13:04
 */
public interface IWxAccessTokenService extends IBaseService<WxAccessToken> {

    WxAccessToken findByUserid(Long userId);

    void deleteByUserid(String userId);
}
