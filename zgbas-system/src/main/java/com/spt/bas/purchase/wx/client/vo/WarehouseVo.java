package com.spt.bas.purchase.wx.client.vo;

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
 * @Date: Created in 2020-09-28 18:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseVo {

    private Long id;

    /**
     * 仓库公司名称
     */
    private String wareCompanyName;

    /**
     *  仓库简称
     *
     */
    private String warehouseName;

    /**
     *  省
     */
    private String provinceName;

    /**
     *  仓库状态
     */
    private String warehouseFlag;
    /**
     * 市
     */
    private String cityName;

    /**
     * 联系人
     */
    private String contactPerson;
    /**
     * 联系人手机号码
     */
    private String contactPhone;

    /**
     * 区/县
     */
    private String curAreaName;

    /**
     * 详细地址
     */
    private String areaName;

}
