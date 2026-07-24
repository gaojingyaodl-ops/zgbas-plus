package com.spt.bas.purchase.wx.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-20 17:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CfcaVo {
    /**
     * 申请Ukey数量
     */
    private Integer ukeyNumber;

    /**
     * 电子签章名称
     */
    private String cfcaName;

    /**
     * 电子签章图片ID
     */
    private String electronicSignFileId;
}
