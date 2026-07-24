package com.spt.bas.purchase.wx.server.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *     cfca信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-23 21:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CfcaRequest extends CompanyBaseInfoRequest{
    private String userId;

    /**
     * 申请Ukey数量
     */
    private Integer ukeyNumber;

    /**
     * 电子签章名称
     */
    private String cfcaName;

    /**
     * 电子签名
     */
    private String electronicSign;

    /**
     * 电子签章图片ID
     */
    private String electronicSignFileId;
}
