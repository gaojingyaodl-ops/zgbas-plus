package com.spt.bas.purchase.wx.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 *  企业类别
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-18 21:56
 */
@Data
@NoArgsConstructor
public class CustomVo extends BaseVo {
    /**
     * 企业类别 0：贸易商 1：终端工厂
     */
    private String customCompanyType;

    private String customCompanyName;

    @Builder(toBuilder = true)
    public CustomVo(Integer infoStep, String customCompanyType, String customCompanyName) {
        super(infoStep);
        this.customCompanyType = customCompanyType;
        this.customCompanyName = customCompanyName;
    }
}
