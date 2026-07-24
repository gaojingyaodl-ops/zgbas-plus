package com.spt.bas.purchase.wx.server.vo;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-21 18:04
 */
@Data
@Builder
public class OcrLicenseVo {
    private String companyName;
    private String licenseNumber;
}
