package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyDeposit;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-06 22:11
 */
public class ApplyDepositVo extends ApplyDeposit {
    private static final long serialVersionUID = 8030017118938945062L;
    private String wxUserPhone;

    public String getWxUserPhone() {
        return wxUserPhone;
    }

    public void setWxUserPhone(String wxUserPhone) {
        this.wxUserPhone = wxUserPhone;
    }
}
