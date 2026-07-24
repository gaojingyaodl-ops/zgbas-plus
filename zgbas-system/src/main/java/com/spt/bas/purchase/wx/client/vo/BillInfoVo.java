package com.spt.bas.purchase.wx.client.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *     公司发票信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-20 11:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillInfoVo {
    /**
     * 开户行
     */
    private String bankName;

    /**
     * 银行账户
     */
    private String bankAccount;

    /**
     * 税号
     */
    private String taxNo;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 发票寄送地址
     */
    private String contactAddress;

    /**
     *
     */
    private Long accountId;

}
