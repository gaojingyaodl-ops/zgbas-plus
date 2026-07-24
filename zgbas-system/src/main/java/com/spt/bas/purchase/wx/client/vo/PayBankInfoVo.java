package com.spt.bas.purchase.wx.client.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  付款银行信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-20 10:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayBankInfoVo {
    private String accountName;
    private String accountNumber;
    private String bank;
    private Long accountId;
}
