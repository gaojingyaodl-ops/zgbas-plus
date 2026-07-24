package com.spt.bas.purchase.wx.server.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NegativeOrZero;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-14 17:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandRequest {
    private Long brandId;
}
